package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MonthlyMaterialLineDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MonthlyReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ProgressReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ProgressRowDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReportFormat;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Downloads;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.time.Instant;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Los informes de mantenimiento: el avance del preventivo (por paquete, via, tipo y fechas) y el
 * resumen de un mes. Cada consulta pide el JSON y lo pinta, con los nombres de paquetes y vias; las
 * cifras son las del servicio. Tras consultar aparecen los enlaces Excel y PDF de esa misma consulta,
 * que se sirven a traves de esta aplicacion con el token de la persona.
 */
@Route(value = MaintenanceRoutes.REPORTS, layout = MainLayout.class)
@PageTitle("Informes")
@Menu(title = "Informes", order = 65, icon = "vaadin:file-table")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class ReportsView extends VerticalLayout {

    private static final int MONTHS = 24;
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"));

    /** Lo que se consulto, fijado al pulsar: los enlaces descargan eso aunque luego cambien los filtros. */
    record ProgressQuery(Long executionPackageId, Long trackId, CatenaryAssetType assetType, Instant from, Instant to) {
    }

    record MonthlyQuery(YearMonth month, Long executionPackageId) {
    }

    private final MaintenanceClients clients;
    private final MaintenanceNames names;

    private final ComboBox<RefItem> progressPackage;
    private final ComboBox<RefItem> progressTrack;
    private final ComboBox<CatenaryAssetType> progressType = new ComboBox<>("Tipo de activo");
    private final DatePicker progressFrom = new DatePicker("Desde");
    private final DatePicker progressTo = new DatePicker("Hasta");
    private final Div progressSummary = new Div();
    private final Grid<ProgressRowDto> progressGrid = new Grid<>();
    private final HorizontalLayout progressDownloads = new HorizontalLayout();

    private final ComboBox<YearMonth> month = new ComboBox<>("Mes");
    private final ComboBox<RefItem> monthlyPackage;
    private final Div monthlySummary = new Div();
    private final Grid<MonthlyMaterialLineDto> monthlyMaterials = new Grid<>();
    private final HorizontalLayout monthlyDownloads = new HorizontalLayout();

    public ReportsView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.progressPackage = Pickers.reference("Paquete", names.packages());
        this.progressTrack = Pickers.reference("Via", names.tracks());
        this.monthlyPackage = Pickers.reference("Paquete", names.packages());
        setSizeFull();
        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.add("Avance", progress());
        tabs.add("Mensual", monthly());
        add(new H2("Informes"), tabs);
        expand(tabs);
    }

    private Component progress() {
        progressPackage.setId("progress-package");
        progressTrack.setId("progress-track");
        progressType.setId("progress-type");
        progressType.setItems(CatenaryAssetType.selectable());
        progressType.setItemLabelGenerator(CatenaryAssetType::label);
        progressType.setClearButtonVisible(true);
        progressFrom.setId("progress-from");
        progressTo.setId("progress-to");
        Button query = new Button("Consultar", VaadinIcon.SEARCH.create(), click -> queryProgress());
        query.setId("progress-query");
        query.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        FlexLayout filters = new FlexLayout(progressPackage, progressTrack, progressType, progressFrom, progressTo, query);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");

        progressSummary.setId("progress-summary");
        progressGrid.setId("progress-grid");
        progressGrid.addColumn(row -> names.packageName(row.executionPackageId())).setHeader("Paquete").setAutoWidth(true);
        progressGrid.addColumn(row -> names.trackName(row.trackId())).setHeader("Via").setAutoWidth(true);
        progressGrid.addColumn(row -> row.assetType() == null ? "" : row.assetType().label()).setHeader("Tipo").setAutoWidth(true);
        progressGrid.addColumn(row -> row.checkedAssets() + " de " + row.totalAssets()).setHeader("Revisados").setAutoWidth(true);
        progressGrid.addColumn(row -> MaintenanceFormats.percent(row.completionRatio())).setHeader("Avance").setAutoWidth(true);
        progressGrid.addColumn(row -> Formats.quantity(row.coveredKm()) + " de " + Formats.quantity(row.totalKm()) + " km").setHeader("Km")
                .setFlexGrow(1);
        progressGrid.setAllRowsVisible(true);
        progressDownloads.setId("progress-downloads");
        VerticalLayout layout = new VerticalLayout(filters, progressSummary, progressDownloads, progressGrid);
        layout.setPadding(false);
        return layout;
    }

    private Component monthly() {
        month.setId("monthly-month");
        YearMonth current = YearMonth.now();
        month.setItems(IntStream.range(0, MONTHS).mapToObj(current::minusMonths).toList());
        month.setItemLabelGenerator(MONTH::format);
        month.setValue(current);
        monthlyPackage.setId("monthly-package");
        Button query = new Button("Consultar", VaadinIcon.SEARCH.create(), click -> queryMonthly());
        query.setId("monthly-query");
        query.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout filters = new HorizontalLayout(month, monthlyPackage, query);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);

        monthlySummary.setId("monthly-summary");
        monthlyMaterials.setId("monthly-materials-grid");
        monthlyMaterials.addColumn(MonthlyMaterialLineDto::materialCode).setHeader("Material").setAutoWidth(true);
        monthlyMaterials.addColumn(line -> Formats.quantity(line.consumedQuantity())).setHeader("Consumido").setAutoWidth(true);
        monthlyMaterials.addColumn(MonthlyMaterialLineDto::unit).setHeader("Unidad").setFlexGrow(1);
        monthlyMaterials.setAllRowsVisible(true);
        monthlyDownloads.setId("monthly-downloads");
        VerticalLayout layout = new VerticalLayout(filters, monthlySummary, monthlyDownloads, monthlyMaterials);
        layout.setPadding(false);
        return layout;
    }

    private void queryProgress() {
        ProgressQuery query = new ProgressQuery(idOf(progressPackage.getValue()), idOf(progressTrack.getValue()), progressType.getValue(),
                Formats.startOfDay(progressFrom.getValue()), Formats.endOfDay(progressTo.getValue()));
        try {
            ProgressReportDto report = clients.reports().progress(query.executionPackageId(), query.trackId(), query.assetType(), query.from(),
                    query.to());
            progressSummary.setText(report.checkedAssets() + " de " + report.totalAssets() + " activos revisados ("
                    + MaintenanceFormats.percent(report.completionRatio()) + ") · " + Formats.quantity(report.coveredKm()) + " de "
                    + Formats.quantity(report.totalKm()) + " km");
            progressGrid.setItems(report.rows());
            progressDownloads.removeAll();
            for (ReportFormat format : ReportFormat.values()) {
                progressDownloads.add(Downloads.link("progress-" + format.parameter(), format.label(), "avance." + format.parameter(),
                        () -> clients.reports().progressFile(query.executionPackageId(), query.trackId(), query.assetType(), query.from(),
                                query.to(), format.parameter())));
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void queryMonthly() {
        if (month.getValue() == null) {
            month.setErrorMessage("Elige un mes");
            month.setInvalid(true);
            return;
        }
        month.setInvalid(false);
        MonthlyQuery query = new MonthlyQuery(month.getValue(), idOf(monthlyPackage.getValue()));
        try {
            MonthlyReportDto report = clients.reports().monthly(query.month(), query.executionPackageId());
            monthlySummary.removeAll();
            monthlySummary.add(new Div("Turnos: " + report.shiftsPlanned() + " planificados, " + report.shiftsClosed() + " cerrados, "
                    + report.shiftsCancelled() + " cancelados · " + report.netWorkMinutes() + " min netos ("
                    + Formats.quantity(report.averageNetMinutesPerShift()) + " por turno)"));
            monthlySummary.add(new Div("Ordenes completadas: " + report.ordersCompleted() + " · Tareas completadas: " + report.tasksCompleted()
                    + " · Perfiles revisados: " + report.profilesChecked() + " · " + Formats.quantity(report.coveredKm()) + " km"));
            monthlySummary.add(new Div("Defectos: " + report.defectsDetected() + " detectados, " + report.defectsResolved() + " resueltos · "
                    + "Ordenes correctivas: " + report.correctiveOrdersCreated()));
            monthlyMaterials.setItems(report.materials());
            monthlyDownloads.removeAll();
            for (ReportFormat format : ReportFormat.values()) {
                monthlyDownloads.add(Downloads.link("monthly-" + format.parameter(), format.label(),
                        "mensual-" + query.month() + "." + format.parameter(),
                        () -> clients.reports().monthlyFile(query.month(), query.executionPackageId(), format.parameter())));
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private static Long idOf(RefItem item) {
        return item == null ? null : item.id();
    }
}

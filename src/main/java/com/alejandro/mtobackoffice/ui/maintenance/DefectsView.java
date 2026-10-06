package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectStatus;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.RowActions;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/** Los defectos de catenaria, paginados y filtrados en el servidor, el mas reciente primero; una fila abre su ficha. */
@Route(value = MaintenanceRoutes.DEFECTS, layout = MainLayout.class)
@PageTitle("Defectos")
@Menu(title = "Defectos", order = 64, icon = "vaadin:warning")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class DefectsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("detectedAt,desc");

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final ComboBox<DefectSeverity> severity = new ComboBox<>("Gravedad");
    private final ComboBox<DefectStatus> status = new ComboBox<>("Estado");
    private final ComboBox<RefItem> track;
    private final ComboBox<RefItem> executionPackage;
    private final DatePicker from = new DatePicker("Detectado desde");
    private final DatePicker to = new DatePicker("Detectado hasta");
    private final Span count = new Span();
    private final Grid<DefectDto> grid = new Grid<>();
    private LazyPages<DefectDto> pages;

    public DefectsView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        setSizeFull();
        severity.setId("defects-severity");
        severity.setItems(DefectSeverity.selectable());
        severity.setItemLabelGenerator(DefectSeverity::label);
        status.setId("defects-status");
        status.setItems(DefectStatus.selectable());
        status.setItemLabelGenerator(DefectStatus::label);
        track = Pickers.reference("Via", names.tracks());
        track.setId("defects-track");
        executionPackage = Pickers.reference("Paquete", names.packages());
        executionPackage.setId("defects-package");
        from.setId("defects-from");
        to.setId("defects-to");
        for (ComboBox<?> combo : List.of(severity, status, track, executionPackage)) {
            combo.setClearButtonVisible(true);
        }
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(severity, status, track, executionPackage, from, to)) {
            filter.addValueChangeListener(change -> refresh());
        }
        Button create = new Button("Nuevo defecto", VaadinIcon.PLUS.create(), click -> new DefectEditorDialog(null, null, clients,
                created -> UI.getCurrent().navigate(DefectDetailView.class, DefectDetailView.parametersOf(created.id()))).open());
        create.setId("defect-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE));

        FlexLayout filters = new FlexLayout(severity, status, track, executionPackage, from, to);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("defects-grid");
        grid.addColumn(DefectDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(defect -> Formats.dateTime(defect.detectedAt())).setHeader("Detectado").setKey("detectedAt")
                .setSortProperty("detectedAt").setSortable(true).setAutoWidth(true);
        grid.addColumn(defect -> defect.asset() == null ? "" : defect.asset().label()).setHeader("Activo").setKey("asset").setAutoWidth(true);
        grid.addColumn(defect -> names.trackName(defect.trackId())).setHeader("Via").setKey("track").setAutoWidth(true);
        grid.addColumn(defect -> MaintenanceFormats.kpRange(defect.startKp(), defect.endKp())).setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(defect -> defect.severity() == null ? "" : defect.severity().label()).setHeader("Gravedad").setKey("severity")
                .setSortProperty("severity").setSortable(true).setAutoWidth(true);
        grid.addColumn(defect -> defect.status() == null ? "" : defect.status().label()).setHeader("Estado").setKey("status")
                .setSortProperty("status").setSortable(true).setAutoWidth(true);
        grid.addColumn(defect -> MaintenanceFormats.date(defect.repairPlannedDate())).setHeader("Reparacion prevista").setKey("repair")
                .setSortProperty("repairPlannedDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(DefectDto::description).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total + " defectos"));
        RowActions.openWithDoubleClickOrButton(grid, DefectDto::id, defect -> "Abrir " + defect.code(),
                defect -> UI.getCurrent().navigate(DefectDetailView.class, DefectDetailView.parametersOf(defect.id())));

        add(new H2("Defectos"), filters, toolbar, grid);
        expand(grid);
    }

    void refresh() {
        pages.refresh();
    }

    private DefectFilter filter() {
        return new DefectFilter(severity.getValue(), status.getValue(), null, null, track.getValue() == null ? null : track.getValue().id(),
                executionPackage.getValue() == null ? null : executionPackage.getValue().id(), Formats.startOfDay(from.getValue()),
                Formats.endOfDay(to.getValue()));
    }

    /** Una pagina con su total: una peticion ({@link LazyPages}). */
    private LazyPages.Page<DefectDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.defects().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

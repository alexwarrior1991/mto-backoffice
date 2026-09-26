package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Stream;

/** Las inspecciones, paginadas y filtradas en el servidor, la mas reciente primero; una fila abre su ficha. */
@Route(value = MaintenanceRoutes.INSPECTIONS, layout = MainLayout.class)
@PageTitle("Inspecciones")
@Menu(title = "Inspecciones", order = 63, icon = "vaadin:clipboard-check")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class InspectionsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("inspectionDate,desc");

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final ComboBox<InspectionResult> result = new ComboBox<>("Resultado");
    private final ComboBox<CatenaryAssetType> assetType = new ComboBox<>("Tipo de activo");
    private final ComboBox<RefItem> track;
    private final ComboBox<RefItem> executionPackage;
    private final DatePicker from = new DatePicker("Desde");
    private final DatePicker to = new DatePicker("Hasta");
    private final TextField inspector = new TextField("Inspector");
    private final Span count = new Span();
    private final Grid<InspectionDto> grid = new Grid<>();

    public InspectionsView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        setSizeFull();
        result.setId("inspections-result");
        result.setItems(InspectionResult.selectable());
        result.setItemLabelGenerator(InspectionResult::label);
        assetType.setId("inspections-asset-type");
        assetType.setItems(CatenaryAssetType.selectable());
        assetType.setItemLabelGenerator(CatenaryAssetType::label);
        track = Pickers.reference("Via", names.tracks());
        track.setId("inspections-track");
        executionPackage = Pickers.reference("Paquete", names.packages());
        executionPackage.setId("inspections-package");
        from.setId("inspections-from");
        to.setId("inspections-to");
        inspector.setId("inspections-inspector");
        inspector.setValueChangeMode(ValueChangeMode.LAZY);
        inspector.setClearButtonVisible(true);
        for (ComboBox<?> combo : List.of(result, assetType, track, executionPackage)) {
            combo.setClearButtonVisible(true);
        }
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(result, assetType, track, executionPackage, from, to, inspector)) {
            filter.addValueChangeListener(change -> refresh());
        }
        Button create = new Button("Nueva inspeccion", VaadinIcon.PLUS.create(), click -> new InspectionEditorDialog(null, null, clients,
                created -> UI.getCurrent().navigate(InspectionDetailView.class, InspectionDetailView.parametersOf(created.id()))).open());
        create.setId("inspection-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE));

        FlexLayout filters = new FlexLayout(result, assetType, track, executionPackage, from, to, inspector);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("inspections-grid");
        grid.addColumn(InspectionDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(inspection -> MaintenanceFormats.date(inspection.inspectionDate())).setHeader("Fecha").setKey("inspectionDate")
                .setSortProperty("inspectionDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(inspection -> inspection.asset() == null ? "" : inspection.asset().label()).setHeader("Activo").setKey("asset")
                .setAutoWidth(true);
        grid.addColumn(inspection -> names.trackName(inspection.trackId())).setHeader("Via").setKey("track").setAutoWidth(true);
        grid.addColumn(inspection -> MaintenanceFormats.kp(inspection.kp())).setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(inspection -> inspection.inspectionKind() == null ? "" : inspection.inspectionKind().label()).setHeader("Tipo")
                .setKey("kind").setAutoWidth(true);
        grid.addColumn(inspection -> inspection.result() == null ? "" : inspection.result().label()).setHeader("Resultado").setKey("result")
                .setSortProperty("result").setSortable(true).setAutoWidth(true);
        grid.addColumn(InspectionDto::inspector).setHeader("Inspector").setKey("inspector").setSortProperty("inspector").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(inspection -> inspection.generatedDefectId() == null ? "" : "Si").setHeader("Defecto").setKey("defect").setAutoWidth(true);
        grid.addColumn(inspection -> inspection.generatedOrderId() == null ? "" : "Si").setHeader("Orden").setKey("order").setAutoWidth(true);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        grid.addItemClickListener(click -> UI.getCurrent().navigate(InspectionDetailView.class,
                InspectionDetailView.parametersOf(click.getItem().id())));

        add(new H2("Inspecciones"), filters, toolbar, grid);
        expand(grid);
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private InspectionFilter filter() {
        return new InspectionFilter(result.getValue(), assetType.getValue(), track.getValue() == null ? null : track.getValue().id(),
                executionPackage.getValue() == null ? null : executionPackage.getValue().id(), from.getValue(), to.getValue(),
                inspector.getValue(), null);
    }

    private Stream<InspectionDto> fetch(Query<InspectionDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<InspectionDto> page = clients.inspections().search(filter(), query.getOffset() / size, size,
                    sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " inspecciones");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<InspectionDto, Void> query) {
        try {
            long total = clients.inspections().search(filter(), 0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " inspecciones");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

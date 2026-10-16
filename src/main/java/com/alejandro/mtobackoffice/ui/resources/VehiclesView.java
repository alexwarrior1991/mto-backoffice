package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOperationalStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * Los vehiculos de la flota de mto-resources, paginados, buscados y filtrados en el servidor, con donde
 * esta cada uno, como en mto-frontend. Una fila se abre (la ficha, con su historial de ubicaciones) con
 * doble clic o con su boton. Escribir pide {@code resources-fleet-write}; retirar,
 * {@code resources-delete} (409 {@code VEH-001} mientras sea el vehiculo por defecto de un equipo);
 * registrar donde esta, {@code resources-location-write}.
 */
@Route(value = ResourcesRoutes.FLEET, layout = MainLayout.class)
@PageTitle("Vehiculos")
@Menu(title = "Vehiculos", order = 72, icon = "vaadin:train")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class VehiclesView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("code,asc");

    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean canWrite;
    private final boolean canDelete;
    private final boolean canLocate;

    private final TextField search = new TextField("Buscar");
    private final Select<VehicleMobility> mobility = new Select<>();
    private final Select<VehicleOperationalStatus> status = new Select<>();
    private final Select<EnabledFilter> available = EnabledFilter.select("Disponible", "Todos", "Disponibles", "No disponibles");
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Retirados");
    private final Span count = new Span();
    private final Grid<VehicleDto> grid = new Grid<>();
    private LazyPages<VehicleDto> pages;

    public VehiclesView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = ResourcesNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_FLEET_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        this.canLocate = authentication.hasRole(ResourcesRoles.RESOURCES_LOCATION_WRITE);
        setSizeFull();

        search.setId("vehicles-search");
        search.setPlaceholder("Codigo, nombre, matricula, numero ferroviario");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        mobility.setLabel("Movilidad");
        mobility.setId("vehicles-mobility");
        mobility.setItems(VehicleMobility.selectable());
        mobility.setItemLabelGenerator(value -> value == null ? "Todas" : value.label());
        mobility.setEmptySelectionAllowed(true);
        mobility.setEmptySelectionCaption("Todas");
        status.setLabel("Estado operativo");
        status.setId("vehicles-status");
        status.setItems(VehicleOperationalStatus.selectable());
        status.setItemLabelGenerator(value -> value == null ? "Todos" : value.label());
        status.setEmptySelectionAllowed(true);
        status.setEmptySelectionCaption("Todos");
        available.setId("vehicles-available");
        state.setId("vehicles-state");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(search, mobility, status, available, state)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo vehiculo", VaadinIcon.PLUS.create(), click -> new VehicleEditorDialog(null, clients, saved -> refresh()).open());
        create.setId("vehicle-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        FlexLayout filters = new FlexLayout(search, mobility, status, available, state);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Vehiculos"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<VehicleDto> buildGrid() {
        grid.setId("vehicles-grid");
        grid.addColumn(VehicleDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(VehicleDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setAutoWidth(true);
        grid.addColumn(vehicle -> vehicle.type() == null ? "" : vehicle.type().code()).setHeader("Modelo").setKey("type").setAutoWidth(true);
        grid.addColumn(vehicle -> vehicle.mobility() == null ? "" : vehicle.mobility().label()).setHeader("Movilidad").setKey("mobility")
                .setAutoWidth(true);
        grid.addColumn(VehicleDto::registrationPlate).setHeader("Matricula").setKey("plate").setSortProperty("registrationPlate").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(VehicleDto::railVehicleNumber).setHeader("N. ferroviario").setKey("railNumber").setAutoWidth(true);
        grid.addColumn(vehicle -> ResourcesFormats.operational(vehicle.operationalStatus(), vehicle.statusReason())).setHeader("Estado operativo")
                .setKey("status").setSortProperty("operationalStatus").setSortable(true).setAutoWidth(true);
        grid.addColumn(vehicle -> ResourcesFormats.place(vehicle.position(), names)).setHeader("Donde esta").setKey("where").setAutoWidth(true);
        grid.addColumn(vehicle -> ResourcesFormats.vehicleState(vehicle.active())).setHeader("Estado").setKey("active").setSortProperty("active")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.addItemDoubleClickListener(event -> open(event.getItem()));
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total == 1 ? "1 vehiculo" : total + " vehiculos"));
        return grid;
    }

    private static void open(VehicleDto vehicle) {
        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(vehicle.id()));
    }

    private Component rowActions(VehicleDto vehicle) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(Screens.rowButton("vehicle-open-" + vehicle.id(), VaadinIcon.ARROW_RIGHT, "Abrir " + vehicle.code(), click -> open(vehicle)));
        if (canLocate && vehicle.isActive()) {
            actions.add(Screens.rowButton("vehicle-locate-" + vehicle.id(), VaadinIcon.MAP_MARKER, "Registrar donde esta " + vehicle.code(),
                    click -> new VehicleLocationDialog(vehicle, clients, names, recorded -> refresh()).open()));
        }
        if (canWrite) {
            actions.add(Screens.rowButton("vehicle-edit-" + vehicle.id(), VaadinIcon.EDIT, "Modificar " + vehicle.code(),
                    click -> new VehicleEditorDialog(vehicle, clients, saved -> refresh()).open()));
        }
        if (vehicle.isActive() && canDelete) {
            Button retire = Screens.rowButton("vehicle-retire-" + vehicle.id(), VaadinIcon.BAN, "Retirar " + vehicle.code(),
                    click -> Screens.confirm("Retirar " + vehicle.label(), VehicleDetailView.RETIRE_TEXT, "Retirar", () -> retire(vehicle)));
            retire.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(retire);
        } else if (!vehicle.isActive() && canWrite) {
            actions.add(Screens.rowButton("vehicle-activate-" + vehicle.id(), VaadinIcon.CHECK_CIRCLE, "Reactivar " + vehicle.code(),
                    click -> activate(vehicle)));
        }
        return actions;
    }

    private void retire(VehicleDto vehicle) {
        try {
            clients.vehicles().deactivate(vehicle.id());
            Screens.success("Retirado " + vehicle.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void activate(VehicleDto vehicle) {
        try {
            clients.vehicles().update(vehicle.id(), MergePatch.of(VehicleUpdateRequest.active(true), vehicle.version()));
            Screens.success("Reactivado " + vehicle.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        pages.refresh();
    }

    private VehicleFilter filter() {
        return new VehicleFilter(search.getValue(), null, mobility.getValue(), null, status.getValue(),
                available.getValue() == null ? null : available.getValue().value(), state.getValue() == null ? null : state.getValue().value());
    }

    private LazyPages.Page<VehicleDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.vehicles().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

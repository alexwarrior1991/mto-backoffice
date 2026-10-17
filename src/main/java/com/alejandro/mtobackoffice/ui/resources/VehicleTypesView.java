package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCapability;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import java.util.stream.Collectors;

/**
 * Los modelos de vehiculo de mto-resources, paginados, buscados y filtrados en el servidor, como en
 * mto-frontend. Darlos de alta y modificarlos pide {@code resources-fleet-write}; retirarlos (un
 * {@code DELETE}: ningun vehiculo nuevo es de el, los que ya lo son se quedan),
 * {@code resources-delete}; volver a ofrecerlo es un {@code PATCH active=true}. No tienen historial: son
 * un catalogo.
 */
@Route(value = ResourcesRoutes.VEHICLE_TYPES, layout = MainLayout.class)
@PageTitle("Modelos de vehiculo")
@Menu(title = "Modelos de vehiculo", order = 74, icon = "vaadin:truck")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class VehicleTypesView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("code,asc");

    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;

    private final TextField search = new TextField("Buscar");
    private final Select<VehicleMobility> mobility = new Select<>();
    private final ComboBox<VehicleCategory> category = new ComboBox<>("Categoria");
    private final ComboBox<String> capability = new ComboBox<>("Capacidad");
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Retirados");
    private final Span count = new Span();
    private final Grid<VehicleTypeDto> grid = new Grid<>();
    private LazyPages<VehicleTypeDto> pages;

    public VehicleTypesView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_FLEET_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();

        search.setId("vehicle-types-search");
        search.setPlaceholder("Codigo, nombre, fabricante, modelo");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        mobility.setLabel("Movilidad");
        mobility.setId("vehicle-types-mobility");
        mobility.setItems(VehicleMobility.selectable());
        mobility.setItemLabelGenerator(value -> value == null ? "Todas" : value.label());
        mobility.setEmptySelectionAllowed(true);
        mobility.setEmptySelectionCaption("Todas");
        category.setId("vehicle-types-category");
        category.setItems(VehicleCategory.selectable());
        category.setItemLabelGenerator(VehicleCategory::label);
        category.setClearButtonVisible(true);
        capability.setId("vehicle-types-capability");
        capability.setItems(VehicleCapability.codes());
        capability.setItemLabelGenerator(VehicleCapability::labelOf);
        capability.setClearButtonVisible(true);
        state.setId("vehicle-types-state");
        // La categoria se ofrece de la movilidad elegida; una que ya no es de ella se quita.
        mobility.addValueChangeListener(change -> {
            List<VehicleCategory> offered = change.getValue() == null ? VehicleCategory.selectable() : VehicleCategory.of(change.getValue());
            VehicleCategory chosen = category.getValue();
            category.setItems(offered);
            category.setValue(offered.contains(chosen) ? chosen : null);
        });
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(search, mobility, category, capability, state)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo modelo", VaadinIcon.PLUS.create(), click -> new VehicleTypeEditorDialog(null, clients, this::refresh).open());
        create.setId("vehicle-type-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        FlexLayout filters = new FlexLayout(search, mobility, category, capability, state);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Modelos de vehiculo"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<VehicleTypeDto> buildGrid() {
        grid.setId("vehicle-types-grid");
        grid.addColumn(VehicleTypeDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(VehicleTypeDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setAutoWidth(true);
        grid.addColumn(type -> ResourcesHistory.join(type.manufacturer(), type.model())).setHeader("Fabricante").setKey("make").setAutoWidth(true);
        grid.addColumn(type -> type.mobility() == null ? "" : type.mobility().label()).setHeader("Movilidad").setKey("mobility")
                .setSortProperty("mobility").setSortable(true).setAutoWidth(true);
        grid.addColumn(type -> type.category() == null ? "" : type.category().label()).setHeader("Categoria").setKey("category")
                .setSortProperty("category").setSortable(true).setAutoWidth(true);
        grid.addColumn(type -> type.seats() == null ? "" : String.valueOf(type.seats())).setHeader("Plazas").setKey("seats").setAutoWidth(true);
        grid.addColumn(type -> type.capabilities().stream().map(VehicleCapability::labelOf).collect(Collectors.joining(", ")))
                .setHeader("Capacidades").setKey("capabilities").setAutoWidth(true);
        grid.addColumn(type -> type.requiredDriverQualification() == null ? "" : type.requiredDriverQualification().code())
                .setHeader("Habilitacion del conductor").setKey("driverQualification").setAutoWidth(true);
        grid.addColumn(type -> type.isActive() ? "Activo" : "Retirado").setHeader("Estado").setKey("active").setSortProperty("active")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> new VehicleTypeEditorDialog(event.getItem(), clients, this::refresh).open());
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total == 1 ? "1 modelo" : total + " modelos"));
        return grid;
    }

    /** La columna existe siempre, como en las demas listas, aunque quien solo lee no tenga nada que hacer en ella. */
    private Component rowActions(VehicleTypeDto type) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite) {
            actions.add(Screens.rowButton("vehicle-type-edit-" + type.id(), VaadinIcon.EDIT, "Modificar " + type.code(),
                    click -> new VehicleTypeEditorDialog(type, clients, this::refresh).open()));
        }
        if (type.isActive() && canDelete) {
            Button retire = Screens.rowButton("vehicle-type-retire-" + type.id(), VaadinIcon.BAN, "Retirar " + type.code(),
                    click -> Screens.confirm("Retirar " + type.label(),
                            "No se borra: ningun vehiculo nuevo sera de este modelo; los que ya lo son se quedan. Se puede volver a ofrecer.",
                            "Retirar", () -> retire(type)));
            retire.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(retire);
        } else if (!type.isActive() && canWrite) {
            actions.add(Screens.rowButton("vehicle-type-activate-" + type.id(), VaadinIcon.CHECK_CIRCLE, "Volver a ofrecer " + type.code(),
                    click -> activate(type)));
        }
        return actions;
    }

    private void retire(VehicleTypeDto type) {
        try {
            clients.vehicleTypes().deactivate(type.id());
            Screens.success("Retirado " + type.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void activate(VehicleTypeDto type) {
        try {
            clients.vehicleTypes().update(type.id(), MergePatch.of(VehicleTypeUpdateRequest.active(true), type.version()));
            Screens.success("Se vuelve a ofrecer " + type.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        pages.refresh();
    }

    private VehicleTypeFilter filter() {
        return new VehicleTypeFilter(search.getValue(), mobility.getValue(), category.getValue(), capability.getValue(),
                state.getValue() == null ? null : state.getValue().value());
    }

    private LazyPages.Page<VehicleTypeDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.vehicleTypes().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

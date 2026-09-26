package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Stream;

/**
 * Los activos de catenaria, paginados y filtrados en el servidor. Un tramo de via se da de alta
 * aqui y se modifica entero; perfiles, seccionadores y aisladores llegan de mto-configuration y
 * aqui solo cambian descripcion e intervalo preventivo. Desactivar es un {@code DELETE}
 * ({@code maintenance-delete}) y reactivar, una modificacion ({@code maintenance-write}); las dos,
 * solo en tramos propios: el {@code enabled} de un activo sincronizado lo vuelve a escribir el
 * siguiente evento de mto-configuration.
 */
@Route(value = MaintenanceRoutes.ASSETS, layout = MainLayout.class)
@PageTitle("Activos")
@Menu(title = "Activos", order = 61, icon = "vaadin:flash")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class AssetsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("trackId,asc", "startKp,asc");

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final boolean canWrite;
    private final boolean canDelete;

    private final ComboBox<CatenaryAssetType> type = new ComboBox<>("Tipo");
    private final ComboBox<RefItem> track;
    private final ComboBox<RefItem> station;
    private final ComboBox<RefItem> executionPackage;
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Desactivados");
    private final TextField name = new TextField("Nombre");
    private final DatePicker dueBy = new DatePicker("Preventivo vence hasta");
    private final Span count = new Span();
    private final Grid<AssetDto> grid = new Grid<>();

    public AssetsView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE);
        this.canDelete = authentication.hasRole(MaintenanceRoles.MAINTENANCE_DELETE);
        setSizeFull();

        type.setId("assets-type");
        type.setItems(CatenaryAssetType.selectable());
        type.setItemLabelGenerator(CatenaryAssetType::label);
        type.setClearButtonVisible(true);
        track = Pickers.reference("Via", names.tracks());
        track.setId("assets-track");
        station = Pickers.reference("Estacion", names.stations());
        station.setId("assets-station");
        executionPackage = Pickers.reference("Paquete", names.packages());
        executionPackage.setId("assets-package");
        state.setId("assets-state");
        name.setId("assets-name");
        name.setPlaceholder("12-2.27, HSA-NS5...");
        name.setClearButtonVisible(true);
        name.setValueChangeMode(ValueChangeMode.LAZY);
        dueBy.setId("assets-due");
        dueBy.setClearButtonVisible(true);
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(type, track, station, executionPackage, state, name, dueBy)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo tramo", VaadinIcon.PLUS.create(),
                click -> new AssetEditorDialog(null, clients, names, this::refresh).open());
        create.setId("asset-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // Sin config-read no hay vias entre las que elegir, y la via es obligatoria.
        create.setVisible(canWrite && names.readsConfiguration());

        FlexLayout filters = new FlexLayout(type, track, station, executionPackage, state, name, dueBy);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Activos"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<AssetDto> buildGrid() {
        grid.setId("assets-grid");
        grid.addColumn(AssetDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(AssetDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setAutoWidth(true);
        grid.addColumn(asset -> asset.type() == null ? "" : asset.type().label()).setHeader("Tipo").setKey("type")
                .setSortProperty("type").setSortable(true).setAutoWidth(true);
        grid.addColumn(asset -> names.trackName(asset.trackId())).setHeader("Via").setKey("track").setAutoWidth(true);
        grid.addColumn(asset -> MaintenanceFormats.kpRange(asset.startKp(), asset.endKp())).setHeader("KP").setKey("kp")
                .setSortProperty("startKp").setSortable(true).setAutoWidth(true);
        grid.addColumn(asset -> names.packageName(asset.executionPackageId())).setHeader("Paquete").setKey("package").setAutoWidth(true);
        grid.addColumn(AssetDto::sectioning).setHeader("Seccionamiento").setKey("sectioning").setAutoWidth(true);
        grid.addColumn(asset -> asset.preventiveIntervalDays() == null ? "" : asset.preventiveIntervalDays() + " d")
                .setHeader("Intervalo").setKey("interval").setSortProperty("preventiveIntervalDays").setSortable(true).setAutoWidth(true);
        grid.addColumn(asset -> Formats.dateTime(asset.nextPreventiveDueAt())).setHeader("Proximo preventivo").setKey("nextPreventive")
                .setAutoWidth(true);
        grid.addColumn(asset -> asset.isEnabled() ? "Activo" : "Desactivado").setHeader("Estado").setKey("enabled")
                .setSortProperty("enabled").setSortable(true).setAutoWidth(true);
        grid.addColumn(asset -> asset.isSynchronized() ? "mto-configuration" : "Mantenimiento").setHeader("Origen").setKey("source")
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        return grid;
    }

    /** La columna existe siempre: ver las ordenes de un activo es lectura. */
    private Component rowActions(AssetDto asset) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(MaintenanceUi.rowButton("asset-orders-" + asset.id(), VaadinIcon.LIST, "Ordenes",
                click -> new AssetOrdersDialog(asset, clients).open()));
        if (canWrite) {
            actions.add(MaintenanceUi.rowButton("asset-edit-" + asset.id(), VaadinIcon.EDIT, "Modificar",
                    click -> new AssetEditorDialog(asset, clients, names, this::refresh).open()));
        }
        if (!asset.isSynchronized()) {
            if (asset.isEnabled() && canDelete) {
                Button disable = MaintenanceUi.rowButton("asset-disable-" + asset.id(), VaadinIcon.BAN, "Desactivar",
                        click -> MaintenanceUi.confirm("Desactivar " + asset.label(),
                                "Deja de admitir trabajo nuevo; las ordenes que tiene no cambian. Se puede reactivar.", "Desactivar",
                                () -> disable(asset)));
                disable.addThemeVariants(ButtonVariant.LUMO_ERROR);
                actions.add(disable);
            } else if (!asset.isEnabled() && canWrite) {
                actions.add(MaintenanceUi.rowButton("asset-enable-" + asset.id(), VaadinIcon.CHECK_CIRCLE, "Reactivar",
                        click -> enable(asset)));
            }
        }
        return actions;
    }

    private void disable(AssetDto asset) {
        try {
            clients.assets().disable(asset.id());
            MaintenanceUi.success("Desactivado " + asset.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void enable(AssetDto asset) {
        try {
            clients.assets().update(asset.id(), AssetUpdateRequest.enabled(true));
            MaintenanceUi.success("Reactivado " + asset.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private AssetFilter filter() {
        return new AssetFilter(type.getValue(), idOf(track.getValue()), idOf(station.getValue()), idOf(executionPackage.getValue()),
                state.getValue() == null ? null : state.getValue().value(), name.getValue(), Formats.endOfDay(dueBy.getValue()));
    }

    private static Long idOf(RefItem item) {
        return item == null ? null : item.id();
    }

    private Stream<AssetDto> fetch(Query<AssetDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<AssetDto> page = clients.assets().search(filter(), query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " activos");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<AssetDto, Void> query) {
        try {
            long total = clients.assets().search(filter(), 0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " activos");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

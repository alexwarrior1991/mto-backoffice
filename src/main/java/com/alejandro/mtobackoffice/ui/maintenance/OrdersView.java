package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
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

/**
 * Las ordenes de mantenimiento, paginadas, filtradas y ordenadas en el servidor; es la entrada
 * «Mantenimiento» del menu y a la vez el nodo del grupo. Via y paquete llegan como ids de
 * mto-configuration y se nombran con {@link MaintenanceNames}; el activo y el equipo vienen
 * resumidos en la propia orden. Solo se ordena por atributos de la orden: el avance y la
 * estimacion los calcula el servicio. Una fila abre la ficha de la orden.
 */
@Route(value = MaintenanceRoutes.ORDERS, layout = MainLayout.class)
@PageTitle("Ordenes")
@Menu(title = "Mantenimiento", order = 60, icon = "vaadin:wrench")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class OrdersView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("createdAt,desc");

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final MaintenanceCatalogs catalogs;

    private final ComboBox<MaintenanceOrderStatus> status = new ComboBox<>("Estado");
    private final ComboBox<MaintenanceOrderType> type = new ComboBox<>("Tipo");
    private final ComboBox<MaintenancePriority> priority = new ComboBox<>("Prioridad");
    private final ComboBox<RefItem> track;
    private final ComboBox<RefItem> executionPackage;
    private final ComboBox<TeamDto> team = new ComboBox<>("Equipo");
    private final TextField code = new TextField("Codigo");
    private final TextField assignedUser = new TextField("Asignada a");
    private final DatePicker plannedFrom = new DatePicker("Prevista desde");
    private final DatePicker plannedTo = new DatePicker("Prevista hasta");
    private final Span count = new Span();
    private final Grid<OrderDto> grid = new Grid<>();

    public OrdersView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.catalogs = new MaintenanceCatalogs(clients.catalog());
        setSizeFull();

        status.setId("orders-status");
        status.setItems(MaintenanceOrderStatus.selectable());
        status.setItemLabelGenerator(MaintenanceOrderStatus::label);
        type.setId("orders-type");
        type.setItems(MaintenanceOrderType.selectable());
        type.setItemLabelGenerator(MaintenanceOrderType::label);
        priority.setId("orders-priority");
        priority.setItems(MaintenancePriority.selectable());
        priority.setItemLabelGenerator(MaintenancePriority::label);
        track = Pickers.reference("Via", names.tracks());
        track.setId("orders-track");
        executionPackage = Pickers.reference("Paquete", names.packages());
        executionPackage.setId("orders-package");
        team.setId("orders-team");
        team.setItems(catalogs.teams());
        team.setItemLabelGenerator(TeamDto::label);
        code.setId("orders-code");
        code.setPlaceholder("MO-000001");
        assignedUser.setId("orders-assigned-user");
        plannedFrom.setId("orders-planned-from");
        plannedTo.setId("orders-planned-to");
        for (ComboBox<?> combo : List.of(status, type, priority, track, executionPackage, team)) {
            combo.setClearButtonVisible(true);
        }
        for (TextField text : List.of(code, assignedUser)) {
            text.setClearButtonVisible(true);
            text.setValueChangeMode(ValueChangeMode.LAZY);
        }
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(status, type, priority, track, executionPackage, team, code, assignedUser,
                plannedFrom, plannedTo)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nueva orden", VaadinIcon.PLUS.create(),
                click -> new OrderEditorDialog(null, clients, catalogs,
                        created -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(created.id()))).open());
        create.setId("order-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE));

        count.setId("orders-count");
        FlexLayout filters = new FlexLayout(status, type, priority, track, executionPackage, team, code, assignedUser, plannedFrom, plannedTo);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Ordenes de mantenimiento"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<OrderDto> buildGrid() {
        grid.setId("orders-grid");
        grid.addColumn(OrderDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(OrderDto::title).setHeader("Titulo").setKey("title").setSortProperty("title").setSortable(true).setFlexGrow(1);
        grid.addColumn(order -> order.type() == null ? "" : order.type().label()).setHeader("Tipo").setKey("type")
                .setSortProperty("type").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> order.status() == null ? "" : order.status().label()).setHeader("Estado").setKey("status")
                .setSortProperty("status").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> order.priority() == null ? "" : order.priority().label()).setHeader("Prioridad").setKey("priority")
                .setSortProperty("priority").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> order.asset() == null ? "" : order.asset().label()).setHeader("Activo").setKey("asset").setAutoWidth(true);
        grid.addColumn(order -> names.trackName(order.trackId())).setHeader("Via").setKey("track").setAutoWidth(true);
        grid.addColumn(order -> MaintenanceFormats.kpRange(order.startKp(), order.endKp())).setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(order -> names.packageName(order.executionPackageId())).setHeader("Paquete").setKey("package").setAutoWidth(true);
        grid.addColumn(order -> MaintenanceFormats.date(order.plannedDate())).setHeader("Prevista").setKey("plannedDate")
                .setSortProperty("plannedDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> order.team() == null ? "" : order.team().label()).setHeader("Equipo").setKey("team").setAutoWidth(true);
        grid.addColumn(order -> MaintenanceFormats.progress(order.completedTaskCount(), order.taskCount())).setHeader("Tareas")
                .setKey("tasks").setAutoWidth(true);
        grid.addColumn(OrderDto::assignedUser).setHeader("Asignada a").setKey("assignedUser")
                .setSortProperty("assignedUser").setSortable(true).setAutoWidth(true);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        grid.addItemClickListener(click -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(click.getItem().id())));
        return grid;
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private OrderFilter filter() {
        return new OrderFilter(status.getValue(), type.getValue(), priority.getValue(), null, null, idOf(track.getValue()), null,
                idOf(executionPackage.getValue()), plannedFrom.getValue(), plannedTo.getValue(), assignedUser.getValue(),
                team.getValue() == null ? null : team.getValue().id(), code.getValue());
    }

    private static Long idOf(RefItem item) {
        return item == null ? null : item.id();
    }

    private Stream<OrderDto> fetch(Query<OrderDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<OrderDto> page = clients.orders().search(filter(), query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " ordenes");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<OrderDto, Void> query) {
        try {
            long total = clients.orders().search(filter(), 0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " ordenes");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

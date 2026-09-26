package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Stream;

/**
 * Las ordenes de mantenimiento, paginadas y ordenadas en el servidor; es la entrada «Mantenimiento»
 * del menu y a la vez el nodo del grupo. Via y paquete llegan como ids de mto-configuration y se
 * nombran con {@link MaintenanceNames}; el activo y el equipo vienen resumidos en la propia orden.
 * Solo se ordena por atributos de la orden: el avance y la estimacion los calcula el servicio.
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
    private final Span count = new Span();
    private final Grid<OrderDto> grid = new Grid<>();

    public OrdersView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        setSizeFull();
        count.setId("orders-count");
        HorizontalLayout toolbar = new HorizontalLayout(count);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Ordenes de mantenimiento"), toolbar, buildGrid());
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
        return grid;
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private PageResponse<OrderDto> search(int page, int size, List<String> sort) {
        return clients.orders().search(OrderFilter.NONE, page, size, sort);
    }

    private Stream<OrderDto> fetch(Query<OrderDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<OrderDto> page = search(query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " ordenes");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<OrderDto, Void> query) {
        try {
            long total = search(0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " ordenes");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

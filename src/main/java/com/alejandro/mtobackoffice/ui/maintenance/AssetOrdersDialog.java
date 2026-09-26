package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.Query;

import java.util.List;
import java.util.stream.Stream;

/** Las ordenes de un activo, paginadas en el servidor y la mas reciente primero; una fila abre su ficha. */
public class AssetOrdersDialog extends Dialog {

    static final String GRID_ID = "asset-orders-grid";
    private static final List<String> DEFAULT_SORT = List.of("createdAt,desc");

    public AssetOrdersDialog(AssetDto asset, MaintenanceClients clients) {
        setHeaderTitle("Ordenes de " + asset.label());
        setWidth("min(95vw, 960px)");
        setHeight("70vh");
        Grid<OrderDto> grid = new Grid<>();
        grid.setId(GRID_ID);
        grid.addColumn(OrderDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(OrderDto::title).setHeader("Titulo").setKey("title").setFlexGrow(1);
        grid.addColumn(order -> order.type() == null ? "" : order.type().label()).setHeader("Tipo").setKey("type").setAutoWidth(true);
        grid.addColumn(order -> order.status() == null ? "" : order.status().label()).setHeader("Estado").setKey("status")
                .setSortProperty("status").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> MaintenanceFormats.date(order.plannedDate())).setHeader("Prevista").setKey("plannedDate")
                .setSortProperty("plannedDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> MaintenanceFormats.progress(order.completedTaskCount(), order.taskCount())).setHeader("Tareas")
                .setKey("tasks").setAutoWidth(true);
        grid.setSizeFull();
        grid.setItems(query -> fetch(clients, asset, query), query -> count(clients, asset));
        grid.addItemClickListener(click -> {
            close();
            UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(click.getItem().id()));
        });
        add(grid);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    private static Stream<OrderDto> fetch(MaintenanceClients clients, AssetDto asset, Query<OrderDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<OrderDto> page = clients.assets().orders(asset.id(), query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private static int count(MaintenanceClients clients, AssetDto asset) {
        try {
            return (int) Math.min(Integer.MAX_VALUE, clients.assets().orders(asset.id(), 0, 1, DEFAULT_SORT).page().totalElements());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

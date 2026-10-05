package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.QuerySortOrder;

import java.util.List;

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
        LazyPages.of(grid, (offset, limit, sort) -> load(clients, asset, offset, limit, sort));
        grid.addItemClickListener(click -> {
            close();
            UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(click.getItem().id()));
        });
        add(grid);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    /** Una pagina de las ordenes del activo, con su total: una peticion ({@link LazyPages}). */
    private static LazyPages.Page<OrderDto> load(MaintenanceClients clients, AssetDto asset, int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        return LazyPages.Page.of(clients.assets().orders(asset.id(), offset / size, size,
                MasterFilters.sort(sort, DEFAULT_SORT, MasterFilters.BY_ID)));
    }
}

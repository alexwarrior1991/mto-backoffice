package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.WarehouseDto;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = StockRoutes.WAREHOUSES, layout = MainLayout.class)
@PageTitle("Almacenes")
@Menu(title = "Almacenes", order = 51, icon = "vaadin:archive")
@RolesAllowed(StockRoles.STOCK_READ)
public class WarehousesView extends StockCatalogueView<WarehouseDto> {

    private final WarehouseClient client;

    public WarehousesView(WarehouseClient client, AuthenticationContext authentication) {
        super(client, authentication, "Almacenes", "almacenes");
        this.client = client;
    }

    @Override
    protected void configureColumns(Grid<WarehouseDto> grid) {
        grid.addColumn(WarehouseDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(WarehouseDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> yesNo(dto.active())).setHeader("Activo").setKey("active").setSortProperty("active").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected UUID idOf(WarehouseDto row) {
        return row.id();
    }

    @Override
    protected void openEditor(WarehouseDto existing) {
        CatalogueEditorDialog.Snapshot snapshot = existing == null ? null
                : new CatalogueEditorDialog.Snapshot(existing.id(), existing.code(), existing.name(), existing.isEnabled());
        new CatalogueEditorDialog("Almacen", snapshot, client, this::refresh).open();
    }
}

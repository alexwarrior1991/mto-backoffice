package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.SupplierDto;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = StockRoutes.SUPPLIERS, layout = MainLayout.class)
@PageTitle("Proveedores")
@Menu(title = "Proveedores", order = 52, icon = "vaadin:truck")
@RolesAllowed(StockRoles.STOCK_READ)
public class SuppliersView extends StockCatalogueView<SupplierDto> {

    private final SupplierClient client;

    public SuppliersView(SupplierClient client, AuthenticationContext authentication) {
        super(client, authentication, "Proveedores", "proveedores");
        this.client = client;
    }

    @Override
    protected void configureColumns(Grid<SupplierDto> grid) {
        grid.addColumn(SupplierDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(SupplierDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> yesNo(dto.active())).setHeader("Activo").setKey("active").setSortProperty("active").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected UUID idOf(SupplierDto row) {
        return row.id();
    }

    @Override
    protected void openEditor(SupplierDto existing) {
        CatalogueEditorDialog.Snapshot snapshot = existing == null ? null
                : new CatalogueEditorDialog.Snapshot(existing.id(), existing.code(), existing.name(), existing.isEnabled());
        new CatalogueEditorDialog("Proveedor", snapshot, client, this::refresh).open();
    }
}

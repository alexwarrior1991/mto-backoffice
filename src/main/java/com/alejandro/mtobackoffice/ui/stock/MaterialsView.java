package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

@Route(value = StockRoutes.MATERIALS, layout = MainLayout.class)
@PageTitle("Materiales")
@Menu(title = "Materiales", order = 50, icon = "vaadin:cubes")
@RolesAllowed(StockRoles.STOCK_READ)
public class MaterialsView extends StockCatalogueView<MaterialDto> {

    private final MaterialClient client;

    public MaterialsView(MaterialClient client, AuthenticationContext authentication) {
        super(client, authentication, "Materiales", "materiales");
        this.client = client;
    }

    @Override
    protected void configureColumns(Grid<MaterialDto> grid) {
        grid.addColumn(MaterialDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(MaterialDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(MaterialDto::unitOfMeasure).setHeader("Unidad").setKey("unitOfMeasure").setAutoWidth(true);
        grid.addColumn(dto -> MaterialEditorDialog.quantity(dto.minimumStockLevel())).setHeader("Stock minimo").setKey("minimumStockLevel")
                .setSortProperty("minimumStockLevel").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.active())).setHeader("Activo").setKey("active").setSortProperty("active").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected UUID idOf(MaterialDto row) {
        return row.id();
    }

    @Override
    protected void openEditor(MaterialDto existing) {
        new MaterialEditorDialog(existing, client, this::refresh).open();
    }
}

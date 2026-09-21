package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

/**
 * Los conjuntos: productos virtuales definidos por su lista de materiales, sin stock propio. La
 * lista es la de cualquier catalogo de almacen; cada fila ofrece ademas su disponibilidad por
 * almacen (a quien solo lee tambien: es una consulta), que calcula el servicio.
 */
@Route(value = StockRoutes.ASSEMBLIES, layout = MainLayout.class)
@PageTitle("Conjuntos")
@Menu(title = "Conjuntos", order = 57, icon = "vaadin:cluster")
@RolesAllowed(StockRoles.STOCK_READ)
public class AssembliesView extends StockCatalogueView<AssemblyDto> {

    private final AssemblyClient client;
    private final MaterialClient materials;
    private final WarehouseClient warehouses;

    public AssembliesView(AssemblyClient client, MaterialClient materials, WarehouseClient warehouses, AuthenticationContext authentication) {
        super(client, authentication, "Conjuntos", "conjuntos");
        this.client = client;
        this.materials = materials;
        this.warehouses = warehouses;
    }

    @Override
    protected void configureColumns(Grid<AssemblyDto> grid) {
        grid.addColumn(AssemblyDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(AssemblyDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> dto.components().size()).setHeader("Materiales").setKey("components").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.active())).setHeader("Activo").setKey("active").setSortProperty("active").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected UUID idOf(AssemblyDto row) {
        return row.id();
    }

    @Override
    protected void openEditor(AssemblyDto existing) {
        new AssemblyEditorDialog(existing, client, materials, this::refresh).open();
    }

    @Override
    protected boolean readersHaveActions() {
        return true;
    }

    @Override
    protected void addRowActions(AssemblyDto row, HorizontalLayout actions) {
        actions.add(rowButton("availability-" + row.id(), VaadinIcon.CALC, "Disponibilidad por almacen",
                click -> new AssemblyAvailabilityDialog(row, client, warehouses).open()));
    }
}

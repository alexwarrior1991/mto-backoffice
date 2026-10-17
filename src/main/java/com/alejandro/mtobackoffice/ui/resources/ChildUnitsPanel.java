package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.function.Supplier;

/**
 * Las unidades que cuelgan directamente de una, activas o no ({@code GET /units?parentId}). El
 * organigrama es pequeno: una peticion de hasta {@link #ALL}, sin paginar. Cada fila se abre con doble
 * clic o con su boton.
 */
class ChildUnitsPanel extends LazyPanel {

    static final int ALL = 200;

    private final Supplier<OrganizationUnitDto> unit;
    private final ResourcesClients clients;
    private final Grid<OrganizationUnitDto> grid = new Grid<>();

    ChildUnitsPanel(Supplier<OrganizationUnitDto> unit, ResourcesClients clients, ResourcesNames names) {
        this.unit = unit;
        this.clients = clients;
        grid.setId("unit-children-grid");
        grid.addColumn(OrganizationUnitDto::label).setHeader("Unidad").setKey("unit").setAutoWidth(true);
        grid.addColumn(child -> ResourcesFormats.kind(child.kind())).setHeader("Nivel").setKey("kind").setAutoWidth(true);
        grid.addColumn(child -> ResourcesFormats.head(child.head())).setHeader("Jefe").setKey("head").setAutoWidth(true);
        grid.addColumn(child -> ResourcesFormats.linemen(child.kind(), child.linemen())).setHeader("Linemen").setKey("linemen").setAutoWidth(true);
        grid.addColumn(child -> names.packageNames(child.executionPackageIds())).setHeader("EP").setKey("packages").setAutoWidth(true);
        grid.addColumn(child -> ResourcesFormats.state(child.active())).setHeader("Estado").setKey("active").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(child -> Screens.rowButton("unit-child-open-" + child.id(), VaadinIcon.ARROW_RIGHT,
                "Abrir " + child.code(), click -> open(child)))).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        grid.addItemDoubleClickListener(event -> open(event.getItem()));
        grid.setAllRowsVisible(true);
        add(grid);
    }

    private static void open(OrganizationUnitDto child) {
        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(child.id()));
    }

    @Override
    protected void load() {
        try {
            grid.setItems(clients.organization().children(unit.get().id(), 0, ALL).content());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            grid.setItems(List.of());
        }
    }
}

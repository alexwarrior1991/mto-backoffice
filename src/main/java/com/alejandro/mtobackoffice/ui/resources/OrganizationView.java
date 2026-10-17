package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationTreeNodeDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.UnitSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * El organigrama de mto-resources, entero y desplegado ({@code GET /units/tree}: unas decenas de
 * unidades, no miles), como en mto-frontend: proyecto, departamento, jefatura de supervisores,
 * supervision y equipo, con su jefe o «Vacante» y, en un equipo, sus linemen frente a la regla de 2 a
 * 6. Una fila se abre con doble clic o con su boton; «Nueva unidad debajo» da de alta con ese padre
 * ({@code resources-write}). Las inactivas solo se ven si se piden.
 */
@Route(value = ResourcesRoutes.ORGANIZATION, layout = MainLayout.class)
@PageTitle("Organigrama")
@Menu(title = "Organigrama", order = 71, icon = "vaadin:sitemap")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class OrganizationView extends VerticalLayout {

    public static final String ACTIONS_COLUMN = "actions";

    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean canWrite;

    private final Checkbox includeInactive = new Checkbox("Ver tambien las inactivas");
    private final Span count = new Span();
    private final TreeGrid<OrganizationTreeNodeDto> grid = new TreeGrid<>();
    private OrganizationTree tree = new OrganizationTree(List.of());

    public OrganizationView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = ResourcesNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_WRITE);
        setSizeFull();

        includeInactive.setId("organization-include-inactive");
        includeInactive.addValueChangeListener(change -> refresh());
        Button create = new Button("Nueva unidad", VaadinIcon.PLUS.create(), click -> openEditor(null));
        create.setId("unit-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);
        HorizontalLayout toolbar = new HorizontalLayout(includeInactive, count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Organigrama"), toolbar, buildGrid());
        expand(grid);
        refresh();
    }

    private TreeGrid<OrganizationTreeNodeDto> buildGrid() {
        grid.setId("organization-tree");
        grid.addHierarchyColumn(OrganizationTreeNodeDto::label).setHeader("Unidad").setKey("unit").setAutoWidth(true).setFlexGrow(1);
        grid.addColumn(node -> ResourcesFormats.kind(node.kind())).setHeader("Nivel").setKey("kind").setAutoWidth(true);
        grid.addColumn(node -> ResourcesFormats.discipline(node.discipline())).setHeader("Disciplina").setKey("discipline").setAutoWidth(true);
        grid.addColumn(node -> ResourcesFormats.head(node.head())).setHeader("Jefe").setKey("head").setAutoWidth(true);
        grid.addColumn(node -> ResourcesFormats.linemen(node.kind(), node.linemen())).setHeader("Linemen").setKey("linemen").setAutoWidth(true);
        grid.addColumn(node -> names.packageNames(node.executionPackageIds())).setHeader("EP").setKey("packages").setAutoWidth(true);
        grid.addColumn(node -> ResourcesFormats.state(node.active())).setHeader("Estado").setKey("active").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.addItemDoubleClickListener(event -> open(event.getItem()));
        grid.setSizeFull();
        return grid;
    }

    private Component rowActions(OrganizationTreeNodeDto node) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(Screens.rowButton("unit-open-" + node.id(), VaadinIcon.ARROW_RIGHT, "Abrir " + node.code(), click -> open(node)));
        if (canWrite && node.isActive() && node.kind() != OrganizationUnitKind.TEAM && node.kind() != OrganizationUnitKind.UNKNOWN) {
            actions.add(Screens.rowButton("unit-create-under-" + node.id(), VaadinIcon.PLUS, "Nueva unidad debajo de " + node.code(),
                    click -> openEditor(node.summary())));
        }
        return actions;
    }

    private void open(OrganizationTreeNodeDto node) {
        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(node.id()));
    }

    private void openEditor(UnitSummaryDto under) {
        new UnitEditorDialog(null, under, tree, clients, names, saved -> refresh()).open();
    }

    void refresh() {
        try {
            tree = new OrganizationTree(clients.organization().tree(includeInactive.getValue()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            tree = new OrganizationTree(List.of());
        }
        TreeData<OrganizationTreeNodeDto> data = new TreeData<>();
        data.addItems(tree.roots(), OrganizationTreeNodeDto::children);
        grid.setTreeData(data);
        grid.expandRecursively(tree.roots(), Integer.MAX_VALUE);
        int units = tree.all().size();
        count.setText(units == 1 ? "1 unidad" : units + " unidades");
    }
}

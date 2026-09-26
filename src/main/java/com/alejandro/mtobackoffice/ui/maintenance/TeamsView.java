package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Los equipos de mantenimiento, sin paginar (son pocos). Se dan de alta y se modifican; se retiran con «Activo». */
@Route(value = MaintenanceRoutes.TEAMS, layout = MainLayout.class)
@PageTitle("Equipos")
@Menu(title = "Equipos", order = 66, icon = "vaadin:specialist")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class TeamsView extends VerticalLayout {

    public static final String ACTIONS_COLUMN = "actions";

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final boolean canWrite;
    private final Span count = new Span();
    private final Grid<TeamDto> grid = new Grid<>();

    public TeamsView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE);
        setSizeFull();

        Button create = new Button("Nuevo equipo", VaadinIcon.PLUS.create(),
                click -> new TeamEditorDialog(null, clients, names, this::refresh).open());
        create.setId("team-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("teams-grid");
        grid.addColumn(TeamDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        grid.addColumn(TeamDto::name).setHeader("Nombre").setKey("name").setAutoWidth(true);
        grid.addColumn(TeamDto::baseName).setHeader("Base").setKey("baseName").setAutoWidth(true);
        grid.addColumn(TeamDto::vehicle).setHeader("Vehiculo").setKey("vehicle").setAutoWidth(true);
        grid.addColumn(team -> team.executionPackageIds().stream().sorted().map(names::packageName).collect(Collectors.joining(", ")))
                .setHeader("Paquetes").setKey("packages").setFlexGrow(1);
        grid.addColumn(team -> team.isActive() ? "Activo" : "Retirado").setHeader("Estado").setKey("active").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        add(new H2("Equipos"), toolbar, grid);
        expand(grid);
        refresh();
    }

    private Component rowActions(TeamDto team) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite) {
            actions.add(MaintenanceUi.rowButton("team-edit-" + team.id(), VaadinIcon.EDIT, "Modificar",
                    click -> new TeamEditorDialog(team, clients, names, this::refresh).open()));
        }
        return actions;
    }

    void refresh() {
        try {
            List<TeamDto> teams = clients.catalog().teams().stream().sorted(Comparator.comparing(TeamDto::code)).toList();
            grid.setItems(teams);
            count.setText(teams.size() + " equipos");
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

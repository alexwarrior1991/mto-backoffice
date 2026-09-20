package com.alejandro.mtobackoffice.ui.views;

import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * Primera pantalla que lee del gateway: el catalogo de estados de perfil, que una base de datos
 * recien migrada ya trae (DRAFT, PROVISIONAL, DEFINITIVE). Solo lectura: la fase 1 la sustituye
 * por la vista generica de catalogos.
 */
@Route(value = "estados-de-perfil", layout = MainLayout.class)
@PageTitle("Estados de perfil")
@Menu(title = "Estados de perfil", order = 10, icon = "vaadin:list")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class ProfileStatusesView extends VerticalLayout {

    private final LovClient lovClient;
    private final Grid<LovDto> grid = new Grid<>();

    public ProfileStatusesView(LovClient lovClient) {
        this.lovClient = lovClient;
        setSizeFull();

        grid.addColumn(LovDto::code).setHeader("Codigo").setAutoWidth(true).setSortable(true);
        grid.addColumn(LovDto::description).setHeader("Descripcion").setFlexGrow(1).setSortable(true);
        grid.addColumn(dto -> Boolean.TRUE.equals(dto.enabled()) ? "Si" : "No").setHeader("Activo").setAutoWidth(true);
        grid.setSizeFull();

        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> load());
        add(new H2(LovResource.PROFILE_STATUSES.title()), reload, grid);
        expand(grid);
        load();
    }

    void load() {
        try {
            grid.setItems(lovClient.findAll(LovResource.PROFILE_STATUSES));
        } catch (BackofficeApiException exception) {
            grid.setItems(List.of());
            UiErrors.show(exception);
        }
    }

    Grid<LovDto> grid() {
        return grid;
    }
}

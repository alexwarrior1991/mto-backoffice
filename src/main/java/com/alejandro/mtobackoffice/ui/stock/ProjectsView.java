package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.ProjectDto;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;

/**
 * Los proyectos del almacen. Un proyecto sincronizado desde mto-configuration (un paquete de
 * ejecucion, {@code sourceService} informado) es de su origen: el servicio rechaza el {@code PUT}
 * con 422 {@code PRJ-001} y aqui no se ofrece modificarlo; se edita alli.
 */
@Route(value = StockRoutes.PROJECTS, layout = MainLayout.class)
@PageTitle("Proyectos")
@Menu(title = "Proyectos", order = 54, icon = "vaadin:clipboard-text")
@RolesAllowed(StockRoles.STOCK_READ)
public class ProjectsView extends StockCatalogueView<ProjectDto> {

    static final String MANUAL_ORIGIN = "manual";

    private final ProjectClient client;

    public ProjectsView(ProjectClient client, AuthenticationContext authentication) {
        super(client, authentication, "Proyectos", "proyectos");
        this.client = client;
    }

    @Override
    protected void configureColumns(Grid<ProjectDto> grid) {
        grid.addColumn(ProjectDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(ProjectDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(ProjectsView::origin).setHeader("Origen").setKey("origin").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.active())).setHeader("Activo").setKey("active").setSortProperty("active").setSortable(true).setAutoWidth(true);
    }

    static String origin(ProjectDto dto) {
        return dto.isSynchronized() ? "sincronizado de " + (dto.sourceService() == null ? "datos maestros" : dto.sourceService()) : MANUAL_ORIGIN;
    }

    @Override
    protected boolean isEditable(ProjectDto row) {
        return !row.isSynchronized();
    }

    @Override
    protected UUID idOf(ProjectDto row) {
        return row.id();
    }

    @Override
    protected void openEditor(ProjectDto existing) {
        CatalogueEditorDialog.Snapshot snapshot = existing == null ? null
                : new CatalogueEditorDialog.Snapshot(existing.id(), existing.code(), existing.name(), existing.isEnabled());
        new CatalogueEditorDialog("Proyecto", snapshot, client, this::refresh).open();
    }
}

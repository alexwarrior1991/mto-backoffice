package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import tools.jackson.databind.ObjectMapper;

/** Las estaciones: nombre y paquete de ejecucion. Sus vias, seccionadores y aisladores se editan en su pantalla. */
@Route(value = MasterView.ROUTE_PREFIX + "/estaciones", layout = MainLayout.class)
@PageTitle("Estaciones")
@Menu(title = "Estaciones", order = 11, icon = "vaadin:building")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class StationsView extends MasterView<StationDto> {

    private final ReferenceCatalog catalog;

    public StationsView(StationClient client, ExecutionPackageClient packages, TrackClient tracks,
                        BusinessEntityClient companies, AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.STATIONS, StationDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(packages, client, tracks, companies);
        init();
    }

    @Override
    protected void configureColumns(Grid<StationDto> grid) {
        grid.addColumn(StationDto::getName).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> catalog.packageName(dto.getExecutionPackageId())).setHeader("Paquete de ejecucion").setKey("executionPackage")
                .setSortProperty("executionPackage.name").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected StationDto newDto() {
        return new StationDto();
    }

    @Override
    protected MasterEditorDialog<StationDto> editor(StationDto dto) {
        return new StationEditor(dto, catalog, this::save, this::onSaved);
    }

    @Override
    protected void onSaved(StationDto saved) {
        catalog.invalidate();
        super.onSaved(saved);
    }

    @Override
    protected String labelOf(StationDto dto) {
        return dto.getName() == null ? super.labelOf(dto) : dto.getName();
    }
}

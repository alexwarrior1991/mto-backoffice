package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/** Los paquetes de ejecucion. La empresa se elige entre las entidades de negocio (solo lectura). */
@Route(value = MasterView.ROUTE_PREFIX + "/paquetes", layout = MainLayout.class)
@PageTitle("Paquetes de ejecucion")
@Menu(title = "Paquetes de ejecucion", order = 10, icon = "vaadin:folder-open")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class ExecutionPackagesView extends MasterView<ExecutionPackageDto> {

    private final ReferenceCatalog catalog;
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Inactivos");

    public ExecutionPackagesView(ExecutionPackageClient client, StationClient stations, TrackClient tracks,
                                 BusinessEntityClient companies, AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.EXECUTION_PACKAGES, ExecutionPackageDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(client, stations, tracks, companies);
        state.addValueChangeListener(change -> refresh());
        init();
    }

    @Override
    protected List<Component> extraFilters() {
        return List.of(state);
    }

    @Override
    protected void addFilters(Map<String, Object> filter) {
        if (state.getValue() != null && state.getValue().value() != null) {
            filter.put("enabled", state.getValue().value());
        }
    }

    @Override
    protected void configureColumns(Grid<ExecutionPackageDto> grid) {
        grid.addColumn(ExecutionPackageDto::getName).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> catalog.companyName(dto.getCompanyId())).setHeader("Empresa").setKey("company").setAutoWidth(true);
        grid.addColumn(dto -> dto.getStartDate() == null ? "" : DATE.format(dto.getStartDate())).setHeader("Inicio").setKey("startDate").setSortProperty("startDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> dto.getEndDate() == null ? "" : DATE.format(dto.getEndDate())).setHeader("Fin").setKey("endDate").setSortProperty("endDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(ExecutionPackageDto::getLength).setHeader("Longitud").setKey("length").setSortProperty("length").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.getInitialPackage())).setHeader("Inicial").setKey("initialPackage").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.getEnabled())).setHeader("Activo").setKey("enabled").setSortProperty("enabled").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected ExecutionPackageDto newDto() {
        return new ExecutionPackageDto();
    }

    @Override
    protected MasterEditorDialog<ExecutionPackageDto> editor(ExecutionPackageDto dto) {
        return new ExecutionPackageEditor(dto, catalog, this::save, this::onSaved);
    }

    @Override
    protected void onSaved(ExecutionPackageDto saved) {
        catalog.invalidate();
        super.onSaved(saved);
    }

    @Override
    protected String labelOf(ExecutionPackageDto dto) {
        return dto.getName() == null ? super.labelOf(dto) : dto.getName();
    }
}

package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
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
import java.util.stream.Collectors;

/**
 * Las vias: nombre, paquete y las estaciones que atraviesa. Sus perfiles (cientos por via) no
 * viajan en la lista ni se tocan al guardar.
 */
@Route(value = MasterView.ROUTE_PREFIX + "/vias", layout = MainLayout.class)
@PageTitle("Vias")
@Menu(title = "Vias", order = 12, icon = "vaadin:road")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class TracksView extends MasterView<TrackDto> {

    private final ReferenceCatalog catalog;
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todas", "Activas", "Inactivas");

    public TracksView(TrackClient client, ExecutionPackageClient packages, StationClient stations,
                      BusinessEntityClient companies, AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.TRACKS, TrackDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(packages, stations, client, companies);
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
    protected void configureColumns(Grid<TrackDto> grid) {
        grid.addColumn(TrackDto::getName).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> catalog.packageName(dto.getExecutionPackageId())).setHeader("Paquete de ejecucion").setKey("executionPackage")
                .setSortProperty("executionPackage.name").setSortable(true).setAutoWidth(true);
        grid.addColumn(this::stationNames).setHeader("Estaciones").setKey("stations").setFlexGrow(1);
        grid.addColumn(dto -> yesNo(dto.getEnabled())).setHeader("Activa").setKey("enabled").setSortProperty("enabled").setSortable(true).setAutoWidth(true);
    }

    private String stationNames(TrackDto dto) {
        if (dto.getStationIds() == null) {
            return "";
        }
        return dto.getStationIds().stream().map(id -> catalog.stationRef(id).map(RefItem::label).orElse("#" + id))
                .collect(Collectors.joining(", "));
    }

    @Override
    protected TrackDto newDto() {
        return new TrackDto();
    }

    @Override
    protected MasterEditorDialog<TrackDto> editor(TrackDto dto) {
        return new TrackEditor(dto, catalog, this::save, this::onSaved);
    }

    @Override
    protected void onSaved(TrackDto saved) {
        catalog.invalidate();
        super.onSaved(saved);
    }

    @Override
    protected String labelOf(TrackDto dto) {
        return dto.getName() == null ? super.labelOf(dto) : dto.getName();
    }
}

package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackAccessPointClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointKind;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointSide;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Los puntos de acceso a la via (V28 de mto-configuration): por donde un vehiculo bivial sale de la
 * carretera y se encarrila, en que via y en que KP, de que clase son y lo que admiten. mto-resources
 * los recibe por evento y los ofrece al registrar donde esta un vehiculo. Se filtran en el servicio
 * por el texto (nombre, via o estacion), la via, la clase y el estado.
 */
@Route(value = TrackAccessPointsView.ROUTE, layout = MainLayout.class)
@PageTitle("Accesos a via")
@Menu(title = "Accesos a via", order = 16, icon = "vaadin:road-branch")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class TrackAccessPointsView extends MasterView<TrackAccessPointDto> {

    public static final String ROUTE = MasterView.ROUTE_PREFIX + "/accesos";

    private final ReferenceCatalog catalog;
    private final ComboBox<RefItem> track;
    private final ComboBox<TrackAccessPointKind> kind = new ComboBox<>("Tipo");
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Inactivos");

    public TrackAccessPointsView(TrackAccessPointClient client, ExecutionPackageClient packages, StationClient stations, TrackClient tracks,
                                 BusinessEntityClient companies, AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.TRACK_ACCESS_POINTS, TrackAccessPointDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(packages, stations, tracks, companies);
        track = Pickers.reference("Via", catalog.tracks());
        track.setId("access-points-track");
        track.addValueChangeListener(change -> refresh());
        kind.setId("access-points-kind");
        kind.setItems(TrackAccessPointKind.values());
        kind.setItemLabelGenerator(TrackAccessPointKind::label);
        kind.setClearButtonVisible(true);
        kind.addValueChangeListener(change -> refresh());
        state.setId("access-points-state");
        state.addValueChangeListener(change -> refresh());
        init();
    }

    @Override
    protected List<Component> extraFilters() {
        return List.of(track, kind, state);
    }

    @Override
    protected void addFilters(Map<String, Object> filter) {
        if (track.getValue() != null) {
            filter.put("trackId", track.getValue().id());
        }
        if (kind.getValue() != null) {
            filter.put("kind", kind.getValue().name());
        }
        if (state.getValue() != null && state.getValue().value() != null) {
            filter.put("enabled", state.getValue().value());
        }
    }

    @Override
    protected void configureColumns(Grid<TrackAccessPointDto> grid) {
        grid.addColumn(TrackAccessPointDto::getName).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> catalog.trackName(dto.getTrackId())).setHeader("Via").setKey("track").setSortProperty("track.name").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(dto -> catalog.stationName(dto.getStationId())).setHeader("Estacion").setKey("station").setSortProperty("station.name")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> plain(dto.getKp())).setHeader("KP (m)").setKey("kp").setSortProperty("kp").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> TrackAccessPointKind.labelOf(dto.getKind())).setHeader("Tipo").setKey("kind").setSortProperty("kind")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> TrackAccessPointSide.labelOf(dto.getSide())).setHeader("Lado").setKey("side").setSortProperty("side")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.getRoadRailAccess())).setHeader("Biviales").setKey("roadRailAccess").setSortProperty("roadRailAccess")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> plain(dto.getMaxAxleLoadT())).setHeader("Carga por eje (t)").setKey("maxAxleLoadT").setAutoWidth(true);
        grid.addColumn(dto -> plain(dto.getMaxGrossWeightT())).setHeader("Peso bruto (t)").setKey("maxGrossWeightT").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.getEnabled())).setHeader("Activo").setKey("enabled").setSortProperty("enabled").setSortable(true)
                .setAutoWidth(true);
    }

    @Override
    protected TrackAccessPointDto newDto() {
        return new TrackAccessPointDto();
    }

    @Override
    protected MasterEditorDialog<TrackAccessPointDto> editor(TrackAccessPointDto dto) {
        return new TrackAccessPointEditor(dto, catalog, this::save, this::onSaved);
    }

    @Override
    protected String labelOf(TrackAccessPointDto dto) {
        return dto.getName() == null ? super.labelOf(dto) : dto.getName();
    }

    private static String plain(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }
}

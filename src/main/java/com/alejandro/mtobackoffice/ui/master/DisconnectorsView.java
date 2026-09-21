package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
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

/** Los seccionadores: en que estacion y en que perfil estan, y su funcion de catalogo. */
@Route(value = MasterView.ROUTE_PREFIX + "/seccionadores", layout = MainLayout.class)
@PageTitle("Seccionadores")
@Menu(title = "Seccionadores", order = 14, icon = "vaadin:power-off")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class DisconnectorsView extends MasterView<DisconnectorDto> {

    private final ReferenceCatalog catalog;
    private final LovCatalog lovs;
    private final ProfileClient profiles;
    private final Select<EnabledFilter> onLoad = EnabledFilter.select("Carga", "Todos", "En carga", "Sin carga");

    public DisconnectorsView(DisconnectorClient client, ProfileClient profiles, ExecutionPackageClient packages, StationClient stations,
                             TrackClient tracks, BusinessEntityClient companies, LovClient lovClient,
                             AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.DISCONNECTORS, DisconnectorDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(packages, stations, tracks, companies);
        this.lovs = new LovCatalog(lovClient);
        this.profiles = profiles;
        onLoad.addValueChangeListener(change -> refresh());
        init();
    }

    @Override
    protected List<Component> extraFilters() {
        return List.of(onLoad);
    }

    @Override
    protected void addFilters(Map<String, Object> filter) {
        if (onLoad.getValue() != null && onLoad.getValue().value() != null) {
            filter.put("onLoad", onLoad.getValue().value());
        }
    }

    @Override
    protected void configureColumns(Grid<DisconnectorDto> grid) {
        grid.addColumn(DisconnectorDto::getName).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> catalog.stationName(dto.getStationId())).setHeader("Estacion").setKey("station").setSortProperty("station.name").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> dto.getDisconnectorFunction() == null ? "" : dto.getDisconnectorFunction().label()).setHeader("Funcion").setKey("function")
                .setSortProperty("disconnectorFunction.code").setSortable(true).setAutoWidth(true);
        grid.addColumn(DisconnectorDto::profileLabel).setHeader("Perfil").setKey("profile").setSortProperty("profile.profileId").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.getOnLoad())).setHeader("En carga").setKey("onLoad").setSortProperty("onLoad").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected DisconnectorDto newDto() {
        return new DisconnectorDto();
    }

    @Override
    protected MasterEditorDialog<DisconnectorDto> editor(DisconnectorDto dto) {
        return new DisconnectorEditor(dto, catalog, lovs, profiles, this::save, this::onSaved);
    }

    @Override
    protected String labelOf(DisconnectorDto dto) {
        return dto.getName() == null ? super.labelOf(dto) : dto.getName();
    }
}

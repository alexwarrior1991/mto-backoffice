package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.SectionInsulatorClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorDto;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorInstallationType;
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

import java.util.List;
import java.util.Map;

/** Los aisladores de seccion: donde estan, como estan instalados y que vias conectan. Sus agujas no se tocan aqui. */
@Route(value = MasterView.ROUTE_PREFIX + "/aisladores", layout = MainLayout.class)
@PageTitle("Aisladores de seccion")
@Menu(title = "Aisladores de seccion", order = 15, icon = "vaadin:split")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class SectionInsulatorsView extends MasterView<SectionInsulatorDto> {

    private final ReferenceCatalog catalog;
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Inactivos");
    private final ComboBox<SectionInsulatorInstallationType> installation = new ComboBox<>("Instalacion");

    public SectionInsulatorsView(SectionInsulatorClient client, ExecutionPackageClient packages, StationClient stations, TrackClient tracks,
                                 BusinessEntityClient companies, AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.SECTION_INSULATORS, SectionInsulatorDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(packages, stations, tracks, companies);
        state.addValueChangeListener(change -> refresh());
        installation.setItems(SectionInsulatorInstallationType.values());
        installation.setItemLabelGenerator(SectionInsulatorInstallationType::label);
        installation.setClearButtonVisible(true);
        installation.addValueChangeListener(change -> refresh());
        init();
    }

    @Override
    protected List<Component> extraFilters() {
        return List.of(state, installation);
    }

    @Override
    protected void addFilters(Map<String, Object> filter) {
        if (state.getValue() != null && state.getValue().value() != null) {
            filter.put("enabled", state.getValue().value());
        }
        if (installation.getValue() != null) {
            filter.put("installationType", installation.getValue().name());
        }
    }

    @Override
    protected void configureColumns(Grid<SectionInsulatorDto> grid) {
        grid.addColumn(SectionInsulatorDto::getName).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> catalog.stationName(dto.getStationId())).setHeader("Estacion").setKey("station").setSortProperty("station.name").setSortable(true).setAutoWidth(true);
        grid.addColumn(SectionInsulatorDto::getKp).setHeader("KP").setKey("kp").setSortProperty("kp").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> dto.getInstallationType() == null ? "" : dto.getInstallationType().label()).setHeader("Instalacion").setKey("installationType")
                .setSortProperty("installationType").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> catalog.trackName(dto.getTrackId())).setHeader("Via").setKey("track").setSortProperty("track.name").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> catalog.trackName(dto.getConnectedTrackId())).setHeader("Via conectada").setKey("connectedTrack").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.getEnabled())).setHeader("Activo").setKey("enabled").setSortProperty("enabled").setSortable(true).setAutoWidth(true);
    }

    @Override
    protected SectionInsulatorDto newDto() {
        return new SectionInsulatorDto();
    }

    @Override
    protected MasterEditorDialog<SectionInsulatorDto> editor(SectionInsulatorDto dto) {
        return new SectionInsulatorEditor(dto, catalog, this::save, this::onSaved);
    }

    @Override
    protected String labelOf(SectionInsulatorDto dto) {
        return dto.getName() == null ? super.labelOf(dto) : dto.getName();
    }
}

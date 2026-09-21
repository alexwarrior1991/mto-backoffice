package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Los perfiles (postes): el maestro grande, miles de filas, asi que la lista se pagina en el
 * servidor y se filtra por via y por estado ademas del texto.
 */
@Route(value = MasterView.ROUTE_PREFIX + "/perfiles", layout = MainLayout.class)
@PageTitle("Perfiles")
@Menu(title = "Perfiles", order = 13, icon = "vaadin:map-marker")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class ProfilesView extends MasterView<ProfileDto> {

    private final ReferenceCatalog catalog;
    private final LovCatalog lovs;
    private final DisconnectorClient disconnectors;
    private final ComboBox<RefItem> track = new ComboBox<>("Via");
    private final ComboBox<LovRef> status = new ComboBox<>("Estado");

    public ProfilesView(ProfileClient client, TrackClient tracks, ExecutionPackageClient packages, StationClient stations,
                        BusinessEntityClient companies, LovClient lovClient, DisconnectorClient disconnectors,
                        AuthenticationContext authentication, ObjectMapper objectMapper) {
        super(MasterResource.PROFILES, ProfileDto.class, client, authentication, objectMapper);
        this.catalog = new ReferenceCatalog(packages, stations, tracks, companies);
        this.lovs = new LovCatalog(lovClient);
        this.disconnectors = disconnectors;
        track.setItems(catalog.tracks());
        track.setItemLabelGenerator(RefItem::label);
        track.setClearButtonVisible(true);
        track.addValueChangeListener(change -> refresh());
        status.setItems(lovs.of(LovResource.PROFILE_STATUSES));
        status.setItemLabelGenerator(LovRef::label);
        status.setClearButtonVisible(true);
        status.addValueChangeListener(change -> refresh());
        init();
    }

    @Override
    protected List<Component> extraFilters() {
        return List.of(track, status);
    }

    @Override
    protected void addFilters(Map<String, Object> filter) {
        if (track.getValue() != null) {
            filter.put("trackId", track.getValue().id());
        }
        if (status.getValue() != null) {
            filter.put("profileStatusCode", status.getValue().code());
        }
    }

    @Override
    protected void configureColumns(Grid<ProfileDto> grid) {
        grid.addColumn(ProfileDto::getProfileId).setHeader("Identificador").setKey("profileId").setSortProperty("profileId").setSortable(true).setAutoWidth(true);
        grid.addColumn(ProfileDto::getKp).setHeader("KP").setKey("kp").setSortProperty("kp").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> catalog.trackName(dto.getTrackId())).setHeader("Via").setKey("track").setSortProperty("track.name").setSortable(true).setFlexGrow(1);
        grid.addColumn(dto -> code(dto.getProfileStatus())).setHeader("Estado").setKey("profileStatus").setSortProperty("profileStatus.code").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> code(dto.getPoleType())).setHeader("Tipo de poste").setKey("poleType").setSortProperty("poleType.code").setSortable(true).setAutoWidth(true);
        grid.addColumn(ProfileDto::getSpan).setHeader("Vano (m)").setKey("span").setAutoWidth(true);
    }

    private static String code(LovRef ref) {
        return ref == null || ref.code() == null ? "" : ref.code();
    }

    @Override
    protected ProfileDto newDto() {
        return new ProfileDto();
    }

    @Override
    protected MasterEditorDialog<ProfileDto> editor(ProfileDto dto) {
        return new ProfileEditor(dto, catalog, lovs, disconnectors, this::save, this::onSaved);
    }

    @Override
    protected String labelOf(ProfileDto dto) {
        return dto.getProfileId() == null ? super.labelOf(dto) : dto.getProfileId() + " (kp " + dto.getKp() + ")";
    }
}

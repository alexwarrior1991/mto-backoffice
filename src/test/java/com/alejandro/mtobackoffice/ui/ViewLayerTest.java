package com.alejandro.mtobackoffice.ui;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.SectionInsulatorClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageMetadata;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.ApiProblem;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.BackofficeUser;
import com.alejandro.mtobackoffice.configuration.security.JwtClaimNames;
import com.alejandro.mtobackoffice.ui.lov.LovBulkCreateDialog;
import com.alejandro.mtobackoffice.ui.lov.LovCrudView;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.master.TracksView;
import com.alejandro.mtobackoffice.ui.views.HomeView;
import com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributesting.v10.LocatorJ;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.NotificationsKt;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringSecurity;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Las vistas en la JVM, sin navegador (Karibu-Testing sobre el contexto de Spring real, con el
 * cliente del gateway sustituido). La persona se finge en el {@code SecurityContextHolder}, que es
 * de donde Vaadin lee principal y roles con {@link MockSpringSecurity}.
 */
@SpringBootTest
@ActiveProfiles("test")
class ViewLayerTest {

    private static final Routes ROUTES = new Routes().autoDiscoverViews("com.alejandro.mtobackoffice.ui");
    private static final String PROFILE_STATUSES = "profile-statuses";
    private static final String CATALOGUE_ROUTE = "catalogos/" + PROFILE_STATUSES;

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private LovClient lovClient;
    @MockitoBean
    private ExecutionPackageClient executionPackageClient;
    @MockitoBean
    private StationClient stationClient;
    @MockitoBean
    private TrackClient trackClient;
    @MockitoBean
    private ProfileClient profileClient;
    @MockitoBean
    private DisconnectorClient disconnectorClient;
    @MockitoBean
    private SectionInsulatorClient sectionInsulatorClient;
    @MockitoBean
    private BusinessEntityClient businessEntityClient;

    @BeforeEach
    void setUp() {
        stubEmptyMasters();
        MockSpringSecurity.mock();
        Function0<UI> uiFactory = UI::new;
        MockVaadin.setup(uiFactory, new MockSpringServlet(ROUTES, context, uiFactory));
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
        SecurityContextHolder.clearContext();
    }

    private static void loginAs(String name, String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(name, "n/a", authorities));
    }

    private static List<String> menuLabels() {
        return LocatorJ._find(SideNavItem.class).stream().map(SideNavItem::getLabel).toList();
    }

    private static List<LovDto> threeStatuses() {
        return List.of(
                new LovDto(1L, "DRAFT", "Borrador", true, LocalDateTime.of(2026, 8, 1, 10, 15), "config.responsable"),
                new LovDto(2L, "PROVISIONAL", "Provisional", true, null, null),
                new LovDto(3L, "DEFINITIVE", "Definitivo", false, null, null));
    }

    @SuppressWarnings("unchecked")
    private static Grid<LovDto> grid() {
        return LocatorJ._get(Grid.class);
    }

    private static Button button(String text) {
        return LocatorJ._get(Button.class, spec -> spec.withText(text));
    }

    // --- Menu y permisos -------------------------------------------------------------------------

    @Test
    void theMenuHidesWhatThePersonCannotOpen() {
        loginAs("almacen.lector", "ROLE_STOCK_READ", "ROLE_REALM_MTO_WAREHOUSE_VIEWER");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.contains("Inicio"), labels.toString());
        assertFalse(labels.contains("Catalogos"), labels.toString());
        assertFalse(labels.contains("Estados de perfil"), labels.toString());
        assertFalse(labels.contains("Infraestructura"), labels.toString());
        assertFalse(labels.contains("Vias"), labels.toString());
    }

    @Test
    void theMenuGroupsTheSixMastersUnderInfrastructure() {
        loginAs("config.lector", "ROLE_CONFIG_READ");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.contains("Infraestructura"), labels.toString());
        for (MasterResource resource : MasterResource.values()) {
            assertTrue(labels.contains(resource.title()), "falta " + resource.title() + " en " + labels);
        }
    }

    @Test
    void theMenuOffersTheSeventeenCataloguesToWhoCanRead() {
        loginAs("config.lector", "ROLE_CONFIG_READ", "ROLE_REALM_MTO_VIEWER");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.contains("Catalogos"), labels.toString());
        for (LovResource resource : LovResource.values()) {
            assertTrue(labels.contains(resource.title()), "falta " + resource.title() + " en " + labels);
        }
    }

    /** Un rol de realm con el mismo nombre que el permiso no abre la pantalla: solo el rol de cliente. */
    @Test
    void aProtectedViewIsNotReachableWithARealmRoleOnly() {
        loginAs("config.impostor", "ROLE_REALM_CONFIG_READ");

        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(CATALOGUE_ROUTE));

        assertTrue(LocatorJ._find(LovCrudView.class).isEmpty());
    }

    @Test
    void aReadOnlyPersonSeesTheCatalogueWithoutAnyWriteControl() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());

        UI.getCurrent().navigate(CATALOGUE_ROUTE);

        assertEquals(3, GridKt._size(grid()));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withText("Nuevo")).isEmpty());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withText("Alta multiple")).isEmpty());
        assertNull(grid().getColumnByKey("actions"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("delete-1")).isEmpty());
    }

    // --- La vista de catalogos --------------------------------------------------------------------

    @Test
    void theCatalogueOfTheRouteIsListedAndTheFilterIsLocal() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());

        UI.getCurrent().navigate(CATALOGUE_ROUTE);

        LocatorJ._get(H2.class, spec -> spec.withText("Estados de perfil"));
        Grid<LovDto> grid = grid();
        assertEquals(3, GridKt._size(grid));
        assertEquals("DRAFT", GridKt._get(grid, 0).code());
        LocatorJ._get(Span.class, spec -> spec.withText("3 entradas"));

        TextField filter = LocatorJ._get(TextField.class, spec -> spec.withPlaceholder("Filtrar por codigo o descripcion"));
        LocatorJ._setValue(filter, "prov");
        assertEquals(1, GridKt._size(grid));
        assertEquals("PROVISIONAL", GridKt._get(grid, 0).code());
        LocatorJ._get(Span.class, spec -> spec.withText("1 de 3 entradas"));

        verify(lovClient, times(1)).findAll(PROFILE_STATUSES);
    }

    @Test
    void anUnknownCatalogueIsNotFoundInsteadOfCrashing() {
        loginAs("config.lector", "ROLE_CONFIG_READ");

        try {
            UI.getCurrent().navigate("catalogos/no-existe");
        } catch (Throwable notFoundByKaribu) {
            // Karibu convierte la pagina de error de Vaadin en una excepcion; tambien vale.
        }

        assertTrue(LocatorJ._find(LovCrudView.class).isEmpty());
    }

    @Test
    void anApiErrorBecomesANotificationWithItsReference() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        ApiProblem problem = new ApiProblem("about:blank", "Service Unavailable", 503,
                "El servicio mto-configuration no esta disponible en este momento.", null,
                null, null, "corr-5", null, null, null, "mto-configuration");
        when(lovClient.findAll(PROFILE_STATUSES)).thenThrow(BackofficeApiException.of(
                HttpStatus.SERVICE_UNAVAILABLE, problem, "corr-5", Duration.ofSeconds(30), "GET /api/configuration/profile-statuses"));

        UI.getCurrent().navigate(CATALOGUE_ROUTE);

        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class,
                spec -> spec.withText("El servicio no esta disponible ahora mismo. Intentalo en 30 s."));
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("Referencia: corr-5"));
        assertEquals(0, GridKt._size(grid()));
    }

    @Test
    void creatingAnEntryPostsItAndReloadsTheCatalogue() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());
        LovDto expected = LovDto.forCreate("ARCHIVED", "Archivado", true);
        when(lovClient.create(PROFILE_STATUSES, expected)).thenReturn(new LovDto(4L, "ARCHIVED", "Archivado", true, null, null));

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        LocatorJ._click(button("Nuevo"));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Codigo")), " ARCHIVED ");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Descripcion")), "Archivado");
        LocatorJ._click(button("Guardar"));

        verify(lovClient).create(PROFILE_STATUSES, expected);
        verify(lovClient, times(2)).findAll(PROFILE_STATUSES);
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");
    }

    @Test
    void serverValidationErrorsLandOnTheirFieldsAndTheDialogStaysOpen() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());
        ApiProblem problem = new ApiProblem("https://api.mto-configuration/errors/val-000", "Peticion invalida", 400,
                "Validation failed", null, "VAL-000", "t-1", null, null, false,
                List.of(new ApiFieldError("code", "VAL-002", "El codigo ya existe"),
                        new ApiFieldError("somethingElse", "VAL-001", "Otro problema")), null);
        when(lovClient.create(eq(PROFILE_STATUSES), any())).thenThrow(
                BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-2", null, "POST /api/configuration/profile-statuses"));

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        LocatorJ._click(button("Nuevo"));
        TextField code = LocatorJ._get(TextField.class, spec -> spec.withLabel("Codigo"));
        LocatorJ._setValue(code, "DRAFT");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Descripcion")), "Duplicado");
        LocatorJ._click(button("Guardar"));

        assertTrue(code.isInvalid());
        assertEquals("El codigo ya existe", code.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");
        assertEquals(1, NotificationsKt.getNotifications().size(), "lo no atribuible a un campo se notifica");
    }

    @Test
    void anEmptyFormNeverReachesTheService() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        LocatorJ._click(button("Nuevo"));
        LocatorJ._click(button("Guardar"));

        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Codigo")).isInvalid());
        verify(lovClient, times(0)).create(any(), any());
    }

    @Test
    void deletingAsksForConfirmationThenCallsTheService() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_DELETE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        Component actions = GridKt._getCellComponent(grid(), 0, "actions");
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withId("delete-1")));

        verify(lovClient, times(0)).delete(any(), any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));

        verify(lovClient).delete(PROFILE_STATUSES, 1L);
        verify(lovClient, times(2)).findAll(PROFILE_STATUSES);
    }

    @Test
    void bulkDisablingUsesTheBulkEndpointForTheSelection() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT", "ROLE_LOV_MANAGE");
        List<LovDto> statuses = threeStatuses();
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(statuses);
        when(lovClient.bulkUpdate(eq(PROFILE_STATUSES), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        Button disable = button("Desactivar seleccionados");
        assertFalse(disable.isEnabled(), "sin seleccion no hay lote");
        grid().select(statuses.get(0));
        grid().select(statuses.get(1));
        assertTrue(disable.isEnabled());
        LocatorJ._click(disable);

        verify(lovClient).bulkUpdate(eq(PROFILE_STATUSES), argThat(changes ->
                changes.size() == 2 && changes.stream().noneMatch(LovDto::isEnabled)));
        verify(lovClient, times(2)).findAll(PROFILE_STATUSES);
    }

    @Test
    void bulkCreateParsesOneEntryPerLineAndRejectsWhatItCannotRead() {
        List<LovDto> entries = LovBulkCreateDialog.parse("PT1;Poste tipo 1\n\nPT2\tPoste tipo 2\nPT3 - Poste tipo 3\n");

        assertEquals(3, entries.size());
        assertEquals(LovDto.forCreate("PT1", "Poste tipo 1", true), entries.get(0));
        assertEquals(LovDto.forCreate("PT2", "Poste tipo 2", true), entries.get(1));
        assertEquals(LovDto.forCreate("PT3", "Poste tipo 3", true), entries.get(2));

        IllegalArgumentException invalid = assertThrows(IllegalArgumentException.class,
                () -> LovBulkCreateDialog.parse("PT1;Poste tipo 1\nSIN-DESCRIPCION\n"));
        assertTrue(invalid.getMessage().contains("linea 2"));
        assertThrows(IllegalArgumentException.class, () -> LovBulkCreateDialog.parse("  \n"));
    }

    // --- Inicio ----------------------------------------------------------------------------------

    @Test
    void homeShowsThePrincipalAndTheAudiencesOfTheAccessToken() {
        Instant now = Instant.now();
        OidcIdToken idToken = OidcIdToken.withTokenValue("id").issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim("sub", "u-1").claim(JwtClaimNames.PREFERRED_USERNAME, "config.responsable").build();
        BackofficeUser user = new BackofficeUser(AuthorityUtils.createAuthorityList("ROLE_CONFIG_READ", "ROLE_REALM_MTO_ADMIN"),
                idToken, null, JwtClaimNames.PREFERRED_USERNAME,
                List.of("mto-configuration-api", "mto-stock-api", "mto-maintenance-api", "mto-users-api"));
        SecurityContextHolder.getContext().setAuthentication(
                new OAuth2AuthenticationToken(user, user.getAuthorities(), "keycloak"));

        UI.getCurrent().navigate(HomeView.class);

        List<ListItem> audiences = LocatorJ._find(ListItem.class).stream()
                .filter(item -> item.getElement().hasAttribute("data-audience")).toList();
        assertEquals(5, audiences.size());
        long present = audiences.stream().filter(item -> "true".equals(item.getElement().getAttribute("data-present"))).count();
        assertEquals(4, present);
        assertTrue(audiences.stream().anyMatch(item -> "mto-gateway-api".equals(item.getElement().getAttribute("data-audience"))
                && "false".equals(item.getElement().getAttribute("data-present"))));
        LocatorJ._get(ListItem.class, spec -> spec.withText("ROLE_REALM_MTO_ADMIN"));
    }

    // --- Maestros de infraestructura -------------------------------------------------------------

    private static final String TRACKS_ROUTE = "infraestructura/vias";
    private static final String PROFILES_ROUTE = "infraestructura/perfiles";

    private static <D extends MasterDto> PageResponse<D> page(List<D> all, int page, int size) {
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return new PageResponse<>(all.subList(from, to), new PageMetadata(page, size, all.size(), (all.size() + size - 1) / size));
    }

    private void stubEmptyMasters() {
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<ExecutionPackageDto>of(), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<StationDto>of(), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<TrackDto>of(), 0, 50));
        when(profileClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<ProfileDto>of(), 0, 50));
        when(businessEntityClient.findAll()).thenReturn(List.of());
    }

    private static ExecutionPackageDto executionPackage(Long id, String name) {
        ExecutionPackageDto dto = new ExecutionPackageDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    private static StationDto station(Long id, String name, Long packageId) {
        StationDto dto = new StationDto();
        dto.setId(id);
        dto.setName(name);
        dto.setExecutionPackageId(packageId);
        return dto;
    }

    private static TrackDto track(Long id, String name, boolean enabled, Long packageId, List<Long> stationIds) {
        TrackDto dto = new TrackDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEnabled(enabled);
        dto.setExecutionPackageId(packageId);
        dto.setStationIds(stationIds);
        dto.setProfiles(null);
        dto.setVersionNumber(7);
        dto.putExtra("fieldOfTomorrow", 1);
        return dto;
    }

    /** El servicio simulado: pagina, filtra por texto y por estado, como hace el de verdad. */
    private void stubTracks(List<TrackDto> all) {
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenAnswer(call -> {
            Map<String, Object> body = call.getArgument(3);
            String text = String.valueOf(body.getOrDefault("searchText", "")).toLowerCase(Locale.ROOT);
            Object enabled = body.get("enabled");
            List<TrackDto> matching = all.stream()
                    .filter(dto -> text.isEmpty() || dto.getName().toLowerCase(Locale.ROOT).contains(text))
                    .filter(dto -> enabled == null || enabled.equals(dto.getEnabled()))
                    .toList();
            return page(matching, call.getArgument(0), call.getArgument(1));
        });
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(executionPackage(100L, "EP4")), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(station(12L, "ATOCHA", 100L), station(13L, "CHAMARTIN", 100L)), 0, 50));
    }

    private static List<TrackDto> threeTracks() {
        return List.of(
                track(3L, "VIA 1", true, 100L, List.of(12L, 13L)),
                track(4L, "VIA 2", true, 100L, List.of()),
                track(5L, "VIA MUERTA", false, 100L, List.of(12L)));
    }

    @SuppressWarnings("unchecked")
    private static Grid<TrackDto> trackGrid() {
        return LocatorJ._get(Grid.class);
    }

    @Test
    void aMasterListIsPagedSortedAndFilteredInTheServer() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());

        UI.getCurrent().navigate(TRACKS_ROUTE);

        Grid<TrackDto> grid = trackGrid();
        assertEquals(3, GridKt._size(grid));
        assertEquals("VIA 1", GridKt._get(grid, 0).getName());
        List<String> firstRow = GridKt._getFormattedRow(grid, 0);
        assertTrue(firstRow.contains("EP4"), "el paquete se ensena por su nombre: " + firstRow);
        assertTrue(firstRow.contains("ATOCHA (EP4), CHAMARTIN (EP4)"), "las estaciones por su nombre: " + firstRow);
        LocatorJ._get(Span.class, spec -> spec.withText("3 vias"));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withPlaceholder("Buscar")), "muerta");
        assertEquals(1, GridKt._size(grid));
        assertEquals("VIA MUERTA", GridKt._get(grid, 0).getName());
        verify(trackClient, atLeastOnce()).filter(anyInt(), anyInt(), anyList(), eq(Map.of("searchText", "muerta")));
        LocatorJ._get(Span.class, spec -> spec.withText("1 via"));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withPlaceholder("Buscar")), "");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withLabel("Estado")), EnabledFilter.ENABLED);
        assertEquals(2, GridKt._size(grid));
        verify(trackClient, atLeastOnce()).filter(anyInt(), anyInt(), anyList(), eq(Map.of("enabled", true)));

        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("name"), SortDirection.DESCENDING)));
        GridKt._get(grid, 0);
        verify(trackClient, atLeastOnce()).filter(anyInt(), anyInt(), eq(List.of("name,desc")), anyMap());
    }

    @Test
    void aReadOnlyPersonSeesTheMastersWithoutAnyWriteControl() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());

        UI.getCurrent().navigate(TRACKS_ROUTE);

        assertTrue(LocatorJ._find(Button.class, spec -> spec.withText("Nuevo")).isEmpty());
        assertNull(trackGrid().getColumnByKey("actions"));
    }

    /** README_API §4 desde la pantalla: la fila vuelve entera, con lo que la UI no conoce, y los hijos a null. */
    @Test
    void editingARowSendsItBackWithItsUnknownFieldsAndItsChildrenLeftAlone() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        stubTracks(threeTracks());
        when(trackClient.update(eq(3L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        Component actions = GridKt._getCellComponent(trackGrid(), 0, "actions");
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withId("edit-3")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), "VIA PRINCIPAL");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(trackClient).update(eq(3L), argThat(dto -> "VIA PRINCIPAL".equals(dto.getName())
                && dto.getProfiles() == null
                && Integer.valueOf(1).equals(dto.extras().get("fieldOfTomorrow"))
                && List.of(12L, 13L).equals(dto.getStationIds())
                && Long.valueOf(100L).equals(dto.getExecutionPackageId())
                && Integer.valueOf(7).equals(dto.getVersionNumber())));
        verify(trackClient, never()).create(any());
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");
        assertEquals("VIA 1", GridKt._get(trackGrid(), 0).getName(), "se edito una copia: la fila del Grid no cambia hasta recargar");
    }

    @Test
    void serverValidationErrorsLandOnTheMasterFields() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        stubTracks(threeTracks());
        ApiProblem problem = new ApiProblem("https://api.mto-configuration/errors/val-000", "Peticion invalida", 400,
                "La peticion tiene 1 errores de validacion", null, "VAL-000", "t-2", null, null, false,
                List.of(new ApiFieldError("executionPackageId", "VAL-001", "El paquete no existe")), null);
        when(trackClient.update(eq(3L), any())).thenThrow(
                BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-3", null, "PUT /api/configuration/tracks/3"));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(trackGrid(), 0, "actions"), Button.class, spec -> spec.withId("edit-3")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        @SuppressWarnings("unchecked")
        ComboBox<RefItem> executionPackage = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Paquete de ejecucion"));
        assertTrue(executionPackage.isInvalid());
        assertEquals("El paquete no existe", executionPackage.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");
    }

    @Test
    void deletingAMasterAsksForConfirmationThenCallsTheService() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_DELETE");
        stubTracks(threeTracks());

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(trackGrid(), 0, "actions"), Button.class, spec -> spec.withId("delete-3")));

        verify(trackClient, never()).delete(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));

        verify(trackClient).delete(3L);
    }

    @Test
    void creatingAProfileSendsItsCatalogueReferencesAndItsTrack() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null)));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(executionPackage(100L, "EP4")), 0, 50));
        when(profileClient.create(any())).thenAnswer(call -> {
            ProfileDto created = call.getArgument(0);
            created.setId(99L);
            return created;
        });

        UI.getCurrent().navigate(PROFILES_ROUTE);
        LocatorJ._click(button("Nuevo"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Identificador")), "P-9");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("KP")), "10.500");
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via"));
        LocatorJ._setValue(track, new RefItem(3L, "TRACK 1 (EP4)"));
        @SuppressWarnings("unchecked")
        ComboBox<LovRef> status = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Estado"));
        LocatorJ._setValue(status, new LovRef(5L, "PT1", "Poste tipo 1"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(profileClient).create(argThat(dto -> "P-9".equals(dto.getProfileId())
                && "10.500".equals(dto.getKp())
                && Long.valueOf(3L).equals(dto.getTrackId())
                && "PT1".equals(dto.getProfileStatus().code())
                && dto.getCantilevers() == null
                && dto.getSectionings().isEmpty()));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");
    }

    @Test
    void aKpWithLettersNeverReachesTheService() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of());

        UI.getCurrent().navigate(PROFILES_ROUTE);
        LocatorJ._click(button("Nuevo"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Identificador")), "P-9");
        TextField kp = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("KP"));
        LocatorJ._setValue(kp, "10,5 km");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        assertTrue(kp.isInvalid());
        verify(profileClient, never()).create(any());
    }
}

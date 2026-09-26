package com.alejandro.mtobackoffice.ui;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.JobsClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.SectionInsulatorClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageMetadata;
import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobItemError;
import com.alejandro.mtobackoffice.client.dto.jobs.JobStatus;
import com.alejandro.mtobackoffice.client.dto.jobs.JobType;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleAssignmentDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleDto;
import com.alejandro.mtobackoffice.client.dto.users.CreateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.ExecuteActionsEmailRequest;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileSummaryDto;
import com.alejandro.mtobackoffice.client.dto.users.ResetPasswordRequest;
import com.alejandro.mtobackoffice.client.dto.users.RoleNamesRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserCredentialDto;
import com.alejandro.mtobackoffice.client.dto.users.UserRolesDto;
import com.alejandro.mtobackoffice.client.dto.users.UserSessionDto;
import com.alejandro.mtobackoffice.ui.users.TakeOut;
import org.mockito.InOrder;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileDto;
import com.alejandro.mtobackoffice.ui.users.ClientRolesView;
import com.alejandro.mtobackoffice.ui.users.UserDetailView;
import com.alejandro.mtobackoffice.ui.users.UserProfilesView;
import com.vaadin.flow.component.tabs.TabSheet;
import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.UpdateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.dto.users.UsersPage;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.users.UserAttributes;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.alejandro.mtobackoffice.ui.users.UsersView;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectDto;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierDto;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseDto;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.MovementClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.ReservationClient;
import com.alejandro.mtobackoffice.client.stock.StockCatalogueClient;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentDirection;
import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.EntryRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialStockDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementType;
import com.alejandro.mtobackoffice.client.dto.stock.OutputRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.TransferRequest;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyAvailabilityComponentDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyAvailabilityDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyComponentDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyComponentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyRequest;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.AuditDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationStatus;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.RevisionMetadataDto;
import com.alejandro.mtobackoffice.client.dto.RevisionOperation;
import com.alejandro.mtobackoffice.ui.stock.ReservationsView;
import com.alejandro.mtobackoffice.ui.stock.StockCatalogueView;
import com.alejandro.mtobackoffice.ui.stock.StockFormats;
import com.alejandro.mtobackoffice.ui.stock.StockView;
import com.alejandro.mtobackoffice.ui.stock.MaterialsView;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Div;
import java.time.LocalDate;
import com.alejandro.mtobackoffice.ui.stock.ProjectsView;
import com.alejandro.mtobackoffice.ui.stock.StockRoutes;
import com.alejandro.mtobackoffice.ui.stock.SuppliersView;
import com.alejandro.mtobackoffice.ui.stock.WarehousesView;
import java.util.function.Function;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import java.util.Set;
import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.ApiProblem;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.BackofficeUser;
import com.alejandro.mtobackoffice.configuration.security.JwtClaimNames;
import com.alejandro.mtobackoffice.ui.jobs.JobsView;
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
import com.github.mvysny.kaributesting.v10.UploadKt;
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
import com.vaadin.flow.component.html.Anchor;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.maintenance.AssetClient;
import com.alejandro.mtobackoffice.client.maintenance.MaintenanceCatalogClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.FunctionalGroup;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateItemDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUnit;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TrackKind;
import com.alejandro.mtobackoffice.ui.maintenance.AssetEditorDialog;
import com.alejandro.mtobackoffice.ui.maintenance.AssetsView;
import com.vaadin.flow.component.html.H3;
import com.alejandro.mtobackoffice.ui.maintenance.TeamEditorDialog;
import com.alejandro.mtobackoffice.ui.maintenance.TeamsView;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.maintenance.MaintenanceRoutes;
import com.alejandro.mtobackoffice.ui.maintenance.OrdersView;
import com.alejandro.mtobackoffice.ui.support.Downloads;
import com.vaadin.flow.server.streams.DownloadResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import java.util.UUID;

import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorDto;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorInstallationType;
import com.alejandro.mtobackoffice.client.dto.master.SectionInsulatorSwitchDto;
import com.alejandro.mtobackoffice.client.dto.master.SteadyArmDto;
import com.alejandro.mtobackoffice.ui.master.CantileverDialog;
import com.alejandro.mtobackoffice.ui.master.SwitchDialog;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.VaadinService;
import java.math.BigDecimal;
import java.util.ArrayList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto;
import com.alejandro.mtobackoffice.ui.master.SchematicDrawing;
import com.alejandro.mtobackoffice.ui.master.TrackSchematicDialog;
import com.vaadin.flow.component.Svg;

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
    @MockitoBean
    private JobsClient jobsClient;
    @MockitoBean
    private UsersClient usersClient;
    @MockitoBean
    private MaterialClient materialClient;
    @MockitoBean
    private WarehouseClient warehouseClient;
    @MockitoBean
    private SupplierClient supplierClient;
    @MockitoBean
    private ProjectClient projectClient;
    @MockitoBean
    private AssemblyClient assemblyClient;
    @MockitoBean
    private MovementClient movementClient;
    @MockitoBean
    private ReservationClient reservationClient;
    @MockitoBean
    private OrderClient orderClient;
    @MockitoBean
    private AssetClient assetClient;
    @MockitoBean
    private MaintenanceCatalogClient maintenanceCatalogClient;

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
                new LovDto(1L, "DRAFT", "Borrador", true, 3, LocalDateTime.of(2026, 8, 1, 10, 15), "config.responsable"),
                new LovDto(2L, "PROVISIONAL", "Provisional", true, 1, null, null),
                new LovDto(3L, "DEFINITIVE", "Definitivo", false, 1, null, null));
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
        assertFalse(labels.contains("Trabajos"), labels.toString());
        assertFalse(labels.contains("Usuarios"), labels.toString());
        assertFalse(labels.contains("Mantenimiento"), labels.toString());
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
        assertTrue(labels.contains("Trabajos"), labels.toString());
        assertFalse(labels.contains("Usuarios"), "sin users-read no hay modulo de usuarios: " + labels);
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
        when(lovClient.create(PROFILE_STATUSES, expected)).thenReturn(new LovDto(4L, "ARCHIVED", "Archivado", true, 1, null, null));

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

    private static void editTheFirstRowDescription(String description) {
        Component actions = GridKt._getCellComponent(grid(), 0, "actions");
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withId("edit-1")));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Descripcion")), description);
        LocatorJ._click(button("Guardar"));
    }

    private static BackofficeApiException configurationConflict(String code, String detail) {
        ApiProblem problem = new ApiProblem("https://api.mto-configuration/errors/" + code.toLowerCase(Locale.ROOT),
                "Conflicto", 409, detail, null, code, "t-409", null, null, "CON-001".equals(code), null, null);
        return BackofficeApiException.of(HttpStatus.CONFLICT, problem, "corr-409", null, "PUT /api/configuration/profile-statuses/1");
    }

    /**
     * El versionNumber es el bloqueo optimista del servicio: cada modificacion lleva el de la fila que
     * se edito, y la segunda el de la fila recargada tras la primera, no el que se leyo al entrar.
     */
    @Test
    void editingAnEntrySendsTheVersionItReadAndTheNextEditTheReloadedOne() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        List<LovDto> reloaded = List.of(
                new LovDto(1L, "DRAFT", "Borrador revisado", true, 4, LocalDateTime.of(2026, 9, 23, 9, 0), "config.responsable"),
                threeStatuses().get(1), threeStatuses().get(2));
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses(), reloaded);
        when(lovClient.update(eq(PROFILE_STATUSES), eq(1L), any())).thenAnswer(call -> {
            LovDto sent = call.getArgument(2);
            return new LovDto(1L, sent.code(), sent.description(), sent.enabled(), sent.versionNumber() + 1, null, null);
        });

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        editTheFirstRowDescription("Borrador revisado");
        editTheFirstRowDescription("Borrador definitivo");

        InOrder order = inOrder(lovClient);
        order.verify(lovClient).update(eq(PROFILE_STATUSES), eq(1L), argThat(dto ->
                Integer.valueOf(3).equals(dto.versionNumber()) && "Borrador revisado".equals(dto.description())));
        order.verify(lovClient).update(eq(PROFILE_STATUSES), eq(1L), argThat(dto ->
                Integer.valueOf(4).equals(dto.versionNumber()) && "Borrador definitivo".equals(dto.description())));
        verify(lovClient, times(3)).findAll(PROFILE_STATUSES);
    }

    /** Otra persona guardo la entrada despues de leerla: el servicio no la pisa y la pantalla dice que se recargue. */
    @Test
    void aStaleVersionEndsInTheReloadMessageAndTheDialogStaysOpen() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());
        when(lovClient.update(eq(PROFILE_STATUSES), eq(1L), any()))
                .thenThrow(configurationConflict("CON-001", "Conflicto de concurrencia detectado. Inténtelo de nuevo."));

        UI.getCurrent().navigate(CATALOGUE_ROUTE);
        editTheFirstRowDescription("Borrador revisado");

        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class,
                spec -> spec.withText("Conflicto con otro cambio: recarga y vuelve a intentarlo."));
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("Referencia: t-409"));
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");
        verify(lovClient, times(1)).findAll(PROFILE_STATUSES);
    }

    /** Recargar arregla una version vieja, pero no un codigo repetido: los dos 409 no se dicen igual. */
    @Test
    void theTwoConfigurationConflictsSayWhetherReloadingHelps() {
        assertEquals("Conflicto con otro cambio: recarga y vuelve a intentarlo.",
                UiErrors.message(configurationConflict("CON-001", "Conflicto de concurrencia detectado. Inténtelo de nuevo.")));
        assertEquals("Ya existe otro registro con ese valor (un codigo que no se puede repetir), o la entrada esta en uso.",
                UiErrors.message(configurationConflict("BUS-002",
                        "La operación entra en conflicto con un registro existente: valor único repetido o referencia en uso")));
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

        // Cada entrada del lote lleva la version de su fila: una sola desactualizada y el servicio
        // rechaza el lote entero con 409 CON-001.
        verify(lovClient).bulkUpdate(eq(PROFILE_STATUSES), argThat(changes ->
                changes.size() == 2 && changes.stream().noneMatch(LovDto::isEnabled)
                        && changes.stream().map(dto -> dto.id() + "@" + dto.versionNumber()).sorted().toList()
                                .equals(List.of("1@3", "2@1"))));
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

    private static <T> PageResponse<T> page(List<T> all, int page, int size) {
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
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<DisconnectorDto>of(), 0, 50));
        when(sectionInsulatorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<SectionInsulatorDto>of(), 0, 50));
        when(jobsClient.list(anyInt(), anyInt(), any(), any())).thenReturn(page(List.<JobDto>of(), 0, 20));
        stubUsers(List.of());
        stubCatalogue(warehouseClient, List.<WarehouseDto>of(), WarehouseDto::code, WarehouseDto::name, WarehouseDto::active);
        stubCatalogue(supplierClient, List.<SupplierDto>of(), SupplierDto::code, SupplierDto::name, SupplierDto::active);
        stubCatalogue(projectClient, List.<ProjectDto>of(), ProjectDto::code, ProjectDto::name, ProjectDto::active);
        stubCatalogue(materialClient, List.<MaterialDto>of(), MaterialDto::code, MaterialDto::name, MaterialDto::active);
        stubCatalogue(assemblyClient, List.<com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto>of(),
                AssemblyDto::code, AssemblyDto::name, AssemblyDto::active);
        when(materialClient.lowStock(any(), anyInt(), anyInt(), anyList())).thenReturn(page(List.<MaterialDto>of(), 0, 50));
        when(materialClient.movements(any(), any(), any(), any(), any(), anyInt(), anyInt(), anyList())).thenReturn(page(List.<MovementDto>of(), 0, 50));
        when(movementClient.search(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), anyList())).thenReturn(page(List.<MovementDto>of(), 0, 50));
        when(orderClient.search(any(OrderFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<OrderDto>of(), 0, 50));
        when(assetClient.search(any(AssetFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<AssetDto>of(), 0, 50));
        when(maintenanceCatalogClient.teams()).thenReturn(List.of());
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(List.of());
        when(maintenanceCatalogClient.inspectionTemplates()).thenReturn(List.of());
    }

    /** Un catalogo de almacen simulado: busca en codigo o nombre, filtra por estado y pagina por page/size como el servicio. */
    private static <D> void stubCatalogue(StockCatalogueClient<D, ?, ?> client, List<D> all, Function<D, String> code,
                                          Function<D, String> name, Function<D, Boolean> active) {
        // doAnswer y no when(...).thenAnswer: volver a simular un metodo ya simulado con thenAnswer
        // ejecuta la respuesta anterior con los comodines (page 0, size 0) y divide por cero.
        doAnswer(call -> {
            String search = call.getArgument(0);
            Boolean state = call.getArgument(1);
            String text = search == null ? "" : search.toLowerCase(Locale.ROOT);
            List<D> matching = all.stream()
                    .filter(dto -> text.isEmpty() || code.apply(dto).toLowerCase(Locale.ROOT).contains(text)
                            || name.apply(dto).toLowerCase(Locale.ROOT).contains(text))
                    .filter(dto -> state == null || state.equals(active.apply(dto)))
                    .toList();
            return page(matching, call.getArgument(2), call.getArgument(3));
        }).when(client).search(any(), any(), anyInt(), anyInt(), anyList());
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
        assertEquals(List.of("schematic-3"), actionIds(anyGrid(), 0),
                "la columna de acciones existe para leer: solo el esquema, ni modificar ni borrar");
    }

    @SuppressWarnings("unchecked")
    private static Grid<Object> anyGrid() {
        return LocatorJ._get(Grid.class);
    }

    private static TrackSchematicDto schematicOfVia1() {
        var arm = new TrackSchematicDto.CantileverArm(21L, "PT1", "-200", "5300", "1400", "SA1", 1200L);
        var disconnector = new TrackSchematicDto.DisconnectorMark(40L, "SEC-40", true, "FEED", "ATOCHA");
        var p1 = new TrackSchematicDto.ProfileNode(1L, "P-001", "10.000", 1, "55.000", "HEB", null, "OK", "-2.500",
                List.of("S1"), List.of(arm), null);
        var p2 = new TrackSchematicDto.ProfileNode(2L, "P-002", "20.000", 2, null, null, null, null, "2.500",
                List.of(), List.of(), disconnector);
        var turnout = new TrackSchematicDto.SwitchMark(60L, "W31", "15.500", 9, "VIA 1");
        var insulator = new TrackSchematicDto.InsulatorMark(50L, "AIS-50", "15.000", "TRACK_CONNECTION", true,
                "ATOCHA", "VIA 1", "VIA 2", List.of(turnout));
        return new TrackSchematicDto(3L, "VIA 1", true, "EP4", List.of("ATOCHA", "CHAMARTIN"), List.of(p1, p2), List.of(insulator));
    }

    private static Button schematicButton(int row, long trackId) {
        return LocatorJ._get(GridKt._getCellComponent(trackGrid(), row, "actions"), Button.class, spec -> spec.withId("schematic-" + trackId));
    }

    private static String drawing(Dialog dialog) {
        return LocatorJ._get(dialog, Svg.class).getElement().getProperty("innerHTML");
    }

    /** Fase 7: el esquema es una llamada al servicio y se dibuja tal cual llega, en su orden. */
    @Test
    void theSchematicOfATrackOpensFromItsRowInOneCallAndDrawsWhatTheServiceSent() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        when(trackClient.schematic(3L)).thenReturn(schematicOfVia1());

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(schematicButton(0, 3L));

        verify(trackClient).schematic(3L);
        Dialog dialog = LocatorJ._get(Dialog.class);
        assertEquals("Esquema · VIA 1 (EP4)", dialog.getHeaderTitle());
        assertEquals("2 perfiles · 1 mensula · 1 seccionador · 1 aislador · Estaciones: ATOCHA, CHAMARTIN",
                LocatorJ._get(dialog, Span.class, spec -> spec.withId(TrackSchematicDialog.SUMMARY_ID)).getText());
        String svg = drawing(dialog);
        assertTrue(svg.startsWith("<svg "), svg);
        assertTrue(svg.indexOf("id=\"pole-1\"") < svg.indexOf("id=\"pole-2\""), "los postes van en el orden recibido, sin reordenar");
        assertTrue(svg.contains(">P-001<") && svg.contains(">KP 10.000<") && svg.contains(">HEB · OK<"), "codigo, KP y tipo/estado del poste");
        assertTrue(svg.contains(">P-002<") && svg.contains(">KP 20.000<"));
        assertTrue(svg.contains("id=\"arm-21\"") && svg.contains(">PT1<"), "la mensula con su tipo");
        assertTrue(svg.contains("Mensula PT1 · descentramiento -200 · altura hilo 5300 · altura catenaria 1400 · brazo SA1 1200 mm"));
        assertTrue(svg.contains(">S1<"), "los seccionamientos del poste");
        assertTrue(svg.contains("id=\"disconnector-40\"") && svg.contains(">SEC-40<") && svg.contains(">ATOCHA<"), "el seccionador sobre su poste, con su estacion");
        assertTrue(svg.contains("id=\"insulator-50\"") && svg.contains(">AIS-50<"), "el aislador sobre la via");
        assertTrue(svg.contains(">conexion de vias · ↔ VIA 2 · ATOCHA<") && svg.contains(">W31 1:9<"), "la otra via, la estacion y las agujas del aislador");
        assertTrue(LocatorJ._find(dialog, Span.class, spec -> spec.withId(TrackSchematicDialog.EMPTY_ID)).isEmpty());
    }

    @Test
    void whatTheServiceSendsIsEscapedBeforeItBecomesSvg() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        var evil = new TrackSchematicDto.ProfileNode(9L, "<script>P</script>", "1.000", 1, null, null, null, null, null,
                List.of("<b>"), List.of(), new TrackSchematicDto.DisconnectorMark(1L, "\"SEC\" & co", null, null, null));
        when(trackClient.schematic(3L)).thenReturn(new TrackSchematicDto(3L, "VIA \"1\" & <b>", true, null, List.of(), List.of(evil), List.of()));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(schematicButton(0, 3L));

        Dialog dialog = LocatorJ._get(Dialog.class);
        assertEquals("Esquema · VIA \"1\" & <b>", dialog.getHeaderTitle(), "la cabecera es texto: Vaadin la escapa sola");
        String svg = drawing(dialog);
        assertFalse(svg.contains("<script>") || svg.contains("<b>"), svg);
        assertTrue(svg.contains("&lt;script&gt;P&lt;/script&gt;"));
        assertTrue(svg.contains("VIA &quot;1&quot; &amp; &lt;b&gt;"));
        assertTrue(svg.contains("&quot;SEC&quot; &amp; co"));
    }

    @Test
    void aFailureLoadingTheSchematicIsNotifiedAndOpensNoWindow() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        ApiProblem problem = new ApiProblem("https://api.mto-configuration/errors/not-001", "Recurso no encontrado", 404,
                "Track not found with id 4", null, "NOT-001", "t-9", null, null, false, List.of(), null);
        when(trackClient.schematic(4L)).thenThrow(
                BackofficeApiException.of(HttpStatus.NOT_FOUND, problem, "corr-9", null, "GET /api/configuration/tracks/4/schematic"));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(schematicButton(1, 4L));

        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "sin esquema no hay ventana");
        assertFalse(NotificationsKt.getNotifications().isEmpty(), "el fallo se notifica");
    }

    @Test
    void aTrackWithoutProfilesSaysSoInsteadOfDrawing() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        when(trackClient.schematic(4L)).thenReturn(new TrackSchematicDto(4L, "VIA 2", false, "EP4", null, null, null));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(schematicButton(1, 4L));

        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._get(dialog, Span.class, spec -> spec.withId(TrackSchematicDialog.EMPTY_ID));
        assertTrue(LocatorJ._find(dialog, Svg.class).isEmpty(), "sin perfiles no se dibuja nada");
        assertEquals("0 perfiles · 0 mensulas · 0 seccionadores · 0 aisladores · sin estaciones · via inactiva",
                LocatorJ._get(dialog, Span.class, spec -> spec.withId(TrackSchematicDialog.SUMMARY_ID)).getText());
    }

    /** El reparto del dibujo, sin Vaadin: espaciado uniforme, el aislador entre sus vecinos por KP, el brazo al lado del poste. */
    @Test
    void theDrawingSpacesPolesEvenlyAndPlacesInsulatorsBetweenTheirNeighboursByKp() {
        List<TrackSchematicDto.ProfileNode> profiles = schematicOfVia1().profiles(); // KP 10.000 y 20.000

        assertEquals(SchematicDrawing.MARGIN, SchematicDrawing.xOf(0), 0.0);
        assertEquals(SchematicDrawing.MARGIN + SchematicDrawing.STEP, SchematicDrawing.xOf(1), 0.0);
        assertEquals(2 * SchematicDrawing.MARGIN + SchematicDrawing.STEP * 599, SchematicDrawing.width(600), "600 postes, 599 pasos");
        assertEquals(SchematicDrawing.MARGIN + SchematicDrawing.STEP / 2.0, SchematicDrawing.insulatorX(profiles, "15.000"), 0.001);
        assertEquals(SchematicDrawing.MARGIN + SchematicDrawing.STEP * 0.25, SchematicDrawing.insulatorX(profiles, "12.500"), 0.001);
        assertEquals(SchematicDrawing.xOf(1) + SchematicDrawing.STEP / 2.0, SchematicDrawing.insulatorX(profiles, "99.000"), 0.001, "despues del ultimo: medio paso fuera");
        assertEquals(Math.max(SchematicDrawing.MARGIN / 3.0, SchematicDrawing.xOf(0) - SchematicDrawing.STEP / 2.0),
                SchematicDrawing.insulatorX(profiles, "1.000"), 0.001, "antes del primero: medio paso fuera, sin salirse de la linea");
        assertEquals(SchematicDrawing.xOf(1) + SchematicDrawing.STEP / 2.0, SchematicDrawing.insulatorX(profiles, null), 0.001, "sin KP, al final");
        assertEquals(-1, SchematicDrawing.armDirection(profiles.get(0)), "railPoleDistance negativo: el poste a la izquierda");
        assertEquals(1, SchematicDrawing.armDirection(profiles.get(1)));

        // Dos tramos con la kilometracion reiniciada (README_API §4): el aislador cae en el tramo que lo contiene primero.
        var reinicio = new TrackSchematicDto.ProfileNode(3L, "P-003", "5.000", 3, null, null, null, null, null, List.of(), List.of(), null);
        var dosTramos = List.of(profiles.get(0), profiles.get(1), reinicio);
        assertEquals(SchematicDrawing.xOf(0) + SchematicDrawing.STEP * 0.5, SchematicDrawing.insulatorX(dosTramos, "15.000"), 0.001);
        assertEquals(SchematicDrawing.xOf(1) + SchematicDrawing.STEP * (20.0 - 8.0) / (20.0 - 5.0), SchematicDrawing.insulatorX(dosTramos, "8.000"), 0.001,
                "8.000 no cabe entre 10 y 20: cae en el tramo que baja de 20 a 5");
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
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null, null)));
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

    private static final String DISCONNECTORS_ROUTE = "infraestructura/seccionadores";
    private static final String SECTION_INSULATORS_ROUTE = "infraestructura/aisladores";

    private static ProfileDto profileWithOneCantilever() {
        ProfileDto dto = new ProfileDto();
        dto.setId(7L);
        dto.setProfileId("P-007");
        dto.setKp("12.345");
        dto.setTrackId(3L);
        dto.setVersionNumber(2);
        dto.setProfileStatus(new LovRef(5L, "PT1", "Poste tipo 1"));
        CantileverDto cantilever = new CantileverDto();
        cantilever.setId(21L);
        cantilever.setCantileverType(new LovRef(5L, "PT1", "Poste tipo 1"));
        cantilever.setCwHeight(new BigDecimal("5300"));
        SteadyArmDto arm = new SteadyArmDto();
        arm.setId(31L);
        arm.setLength(1200L);
        arm.setSteadyArmType(new LovRef(5L, "PT1", "Poste tipo 1"));
        cantilever.setSteadyArm(arm);
        dto.setCantilevers(new ArrayList<>(List.of(cantilever)));
        return dto;
    }

    /** README_API.md §4: sin tocar las mensulas van a null; tocadas, va la lista entera, con el brazo 1:1 de cada una. */
    @Test
    void theCantileversOfAProfileGoAsNullUntouchedAndWholeWhenEdited() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null, null)));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        when(profileClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(profileWithOneCantilever()), 0, 50));
        when(profileClient.update(eq(7L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(PROFILES_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<ProfileDto> profiles = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("edit-7")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        Grid<CantileverDto> cantilevers = LocatorJ._get(dialog, Grid.class, spec -> spec.withId("cantilevers-grid"));
        assertEquals(1, GridKt._size(cantilevers));
        assertTrue(GridKt._getFormattedRow(cantilevers, 0).stream().anyMatch(cell -> cell.endsWith("1200 mm")),
                "el brazo se ensena con su tipo y su longitud: " + GridKt._getFormattedRow(cantilevers, 0));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        verify(profileClient).update(eq(7L), argThat(dto -> dto.getCantilevers() == null && dto.getDisconnector() == null));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("edit-7")));
        dialog = LocatorJ._get(Dialog.class);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("cantilevers-add")));
        CantileverDialog cantilever = LocatorJ._get(CantileverDialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<LovRef> type = LocatorJ._get(cantilever, ComboBox.class, spec -> spec.withLabel("Tipo de mensula"));
        LocatorJ._setValue(type, new LovRef(5L, "PT1", "Poste tipo 1"));
        LocatorJ._setValue(LocatorJ._get(cantilever, BigDecimalField.class, spec -> spec.withLabel("Descentramiento (mm)")), new BigDecimal("-200"));
        LocatorJ._setValue(LocatorJ._get(cantilever, Checkbox.class, spec -> spec.withId("cantilever-with-arm")), true);
        @SuppressWarnings("unchecked")
        ComboBox<LovRef> armType = LocatorJ._get(cantilever, ComboBox.class, spec -> spec.withLabel("Tipo de brazo"));
        LocatorJ._setValue(armType, new LovRef(5L, "PT1", "Poste tipo 1"));
        LocatorJ._setValue(LocatorJ._get(cantilever, IntegerField.class, spec -> spec.withLabel("Longitud del brazo (mm)")), 900);
        LocatorJ._click(LocatorJ._get(cantilever, Button.class, spec -> spec.withId("cantilever-accept")));
        @SuppressWarnings("unchecked")
        Grid<CantileverDto> edited = LocatorJ._get(dialog, Grid.class, spec -> spec.withId("cantilevers-grid"));
        assertEquals(2, GridKt._size(edited));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(profileClient).update(eq(7L), argThat(dto -> dto.getCantilevers() != null && dto.getCantilevers().size() == 2
                && Long.valueOf(21L).equals(dto.getCantilevers().get(0).getId())
                && Long.valueOf(1200L).equals(dto.getCantilevers().get(0).getSteadyArm().getLength())
                && dto.getCantilevers().get(1).getId() == null
                && new BigDecimal("-200").equals(dto.getCantilevers().get(1).getStagger())
                && "PT1".equals(dto.getCantilevers().get(1).getCantileverType().code())
                && Long.valueOf(900L).equals(dto.getCantilevers().get(1).getSteadyArm().getLength())));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");
    }

    /** README_API.md §4 quater: las agujas son una coleccion de hijos; el codigo tiene forma fija. */
    @Test
    void theSwitchesOfASectionInsulatorAreEditedInTheirOwnDialogAndSentWhole() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of()), track(4L, "TRACK 2", true, 100L, List.of())), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        SectionInsulatorDto insulator = new SectionInsulatorDto();
        insulator.setId(9L);
        insulator.setName("B7");
        insulator.setStationId(12L);
        insulator.setInstallationType(SectionInsulatorInstallationType.TRACK_CONNECTION);
        insulator.setTrackId(3L);
        insulator.setConnectedTrackId(4L);
        insulator.setVersionNumber(1);
        SectionInsulatorSwitchDto w31 = new SectionInsulatorSwitchDto();
        w31.setId(41L);
        w31.setCode("W31");
        w31.setKp(new BigDecimal("110176.000"));
        w31.setTurnoutDenominator(9);
        w31.setTrackId(3L);
        w31.setEnabled(true);
        insulator.setSwitches(new ArrayList<>(List.of(w31)));
        when(sectionInsulatorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(insulator), 0, 50));
        when(sectionInsulatorClient.update(eq(9L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(SECTION_INSULATORS_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<SectionInsulatorDto> insulators = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(insulators, 0, "actions"), Button.class, spec -> spec.withId("edit-9")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        Grid<SectionInsulatorSwitchDto> switches = LocatorJ._get(dialog, Grid.class, spec -> spec.withId("switches-grid"));
        assertEquals(1, GridKt._size(switches));
        assertTrue(GridKt._getFormattedRow(switches, 0).contains("1:9"), "la tangente se ensena como en el plano");

        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("switches-add")));
        SwitchDialog aguja = LocatorJ._get(SwitchDialog.class);
        TextField code = LocatorJ._get(aguja, TextField.class, spec -> spec.withLabel("Codigo"));
        LocatorJ._setValue(code, "X1");
        LocatorJ._click(LocatorJ._get(aguja, Button.class, spec -> spec.withId("switch-accept")));
        assertTrue(code.isInvalid(), "W y hasta cuatro cifras");
        LocatorJ._setValue(code, "W41");
        LocatorJ._setValue(LocatorJ._get(aguja, BigDecimalField.class, spec -> spec.withLabel("KP (m)")), new BigDecimal("110249"));
        LocatorJ._setValue(LocatorJ._get(aguja, IntegerField.class, spec -> spec.withLabel("Denominador de la tangente (1:n)")), 12);
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(aguja, ComboBox.class, spec -> spec.withLabel("Via"));
        LocatorJ._setValue(track, new RefItem(4L, "TRACK 2 (EP4)"));
        LocatorJ._click(LocatorJ._get(aguja, Button.class, spec -> spec.withId("switch-accept")));
        assertEquals(2, GridKt._size(switches));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(sectionInsulatorClient).update(eq(9L), argThat(dto -> dto.getSwitches() != null && dto.getSwitches().size() == 2
                && "W31".equals(dto.getSwitches().get(0).getCode())
                && Long.valueOf(41L).equals(dto.getSwitches().get(0).getId())
                && "W41".equals(dto.getSwitches().get(1).getCode())
                && Integer.valueOf(12).equals(dto.getSwitches().get(1).getTurnoutDenominator())
                && Long.valueOf(4L).equals(dto.getSwitches().get(1).getTrackId())
                && Boolean.TRUE.equals(dto.getSwitches().get(1).getEnabled())));
    }

    /** El servicio manda profileCode y profileKp con cada seccionador: la lista no va perfil por perfil. */
    @Test
    void theDisconnectorListShowsTheProfileByItsCodeAndKp() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(lovClient.findAll(anyString())).thenReturn(List.of());
        DisconnectorDto known = new DisconnectorDto();
        known.setId(5L);
        known.setName("SEC-1");
        known.setStationId(12L);
        known.setProfileId(7L);
        known.setProfileCode("P-007");
        known.setProfileKp("12.345");
        known.setDisconnectorFunction(new LovRef(9L, "Disc", "Seccionador"));
        DisconnectorDto bare = new DisconnectorDto();
        bare.setId(6L);
        bare.setName("SEC-2");
        bare.setProfileId(8L);
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenAnswer(call -> page(List.of(known, bare), call.getArgument(0), call.getArgument(1)));

        UI.getCurrent().navigate(DISCONNECTORS_ROUTE);

        @SuppressWarnings("unchecked")
        Grid<DisconnectorDto> grid = LocatorJ._get(Grid.class);
        assertEquals(2, GridKt._size(grid));
        assertTrue(GridKt._getFormattedRow(grid, 0).contains("P-007 (kp 12.345)"), GridKt._getFormattedRow(grid, 0).toString());
        assertTrue(GridKt._getFormattedRow(grid, 1).contains("#8"), "sin identificador, el id sigue siendo mejor que nada");
    }

    /** Tras un reinicio no queda un dialogo muerto: la pantalla recarga y la cadena de seguridad reentra por el SSO. */
    @Test
    void theExpiredSessionReloadsInsteadOfLeavingADeadDialog() {
        SystemMessages messages = VaadinService.getCurrent().getSystemMessages(Locale.getDefault(), null);

        assertEquals("Error interno", messages.getInternalErrorCaption(), "los mensajes son los de esta aplicacion");
        assertFalse(messages.isSessionExpiredNotificationEnabled(), "sin aviso: Vaadin recarga en cuanto la sesion no esta");
        assertNull(messages.getSessionExpiredURL(), "sin URL: recarga la misma pantalla");
        assertNull(messages.getSessionExpiredCaption(), "Vaadin retiene el texto mientras el aviso esta apagado");
    }

    // --- Trabajos en segundo plano ---------------------------------------------------------------

    private static final UUID JOB_ID = UUID.fromString("6f1c0000-0000-4000-8000-000000000001");

    private static JobDto job(JobType type, JobStatus status, Integer total, int processed, int ok, int failed, List<JobItemError> errors) {
        return new JobDto(JOB_ID, type, status, Instant.parse("2026-08-27T09:12:03Z"), null, null, 3L, null,
                total, processed, ok, failed, null, null, errors);
    }

    private static JobDto job(UUID id, JobType type, JobStatus status, Instant createdAt) {
        return new JobDto(id, type, status, createdAt, null, null, null, null, null, 0, 0, 0, null, null, null);
    }

    @SuppressWarnings("unchecked")
    private static Grid<JobDto> jobsGrid() {
        return LocatorJ._get(Grid.class);
    }

    /** El servicio simulado: GET /jobs devuelve lo que haya en la lista, paginado y filtrado por tipo y estado. */
    private void stubJobHistory(List<JobDto> history) {
        when(jobsClient.list(anyInt(), anyInt(), any(), any())).thenAnswer(call -> {
            JobType type = call.getArgument(2);
            JobStatus status = call.getArgument(3);
            List<JobDto> matching = history.stream()
                    .filter(job -> type == null || job.type() == type)
                    .filter(job -> status == null || job.status() == status)
                    .toList();
            return page(matching, call.getArgument(0), call.getArgument(1));
        });
    }

    /** Subir, lanzar, y ver el progreso llegar por @Push sin que el navegador pregunte. */
    @Test
    void launchingAnImportTracksTheJobAndPushesItsProgressUntilItEnds() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT", "ROLE_LOV_MANAGE");
        List<JobDto> history = new ArrayList<>();
        stubJobHistory(history);
        when(jobsClient.importProfiles(any(), eq(false))).thenAnswer(call -> {
            JobDto accepted = job(JobType.PROFILE_IMPORT, JobStatus.PENDING, null, 0, 0, 0, null);
            history.addFirst(accepted);
            return accepted;
        });

        UI.getCurrent().navigate(JobsView.ROUTE);
        Grid<JobDto> grid = jobsGrid();
        assertEquals(0, GridKt._size(grid));
        Button start = LocatorJ._get(Button.class, spec -> spec.withId("import-profiles"));
        assertFalse(start.isEnabled(), "sin fichero no hay nada que importar");
        UploadKt._upload(LocatorJ._get(Upload.class, spec -> spec.withId("import-profiles-upload")),
                "profile-master.xlsx", JobsView.XLSX, "PK-xlsx".getBytes());
        MockVaadin.clientRoundtrip();
        assertTrue(start.isEnabled());
        LocatorJ._click(start);

        verify(jobsClient).importProfiles(argThat(resource -> "profile-master.xlsx".equals(resource.getFilename())), eq(false));
        assertEquals(1, GridKt._size(grid));
        assertEquals(JobStatus.PENDING, GridKt._get(grid, 0).status());
        assertEquals("Importacion del maestro de perfiles (profile-master.xlsx)", GridKt._getFormattedRow(grid, 0).getFirst(),
                "la etiqueta es la de esta sesion");
        assertFalse(start.isEnabled(), "el fichero ya se ha enviado");

        JobsView view = LocatorJ._get(JobsView.class);
        history.set(0, job(JobType.PROFILE_IMPORT, JobStatus.RUNNING, 100, 50, 50, 0, null));
        view.pollOnce();
        MockVaadin.clientRoundtrip();
        assertEquals(JobStatus.RUNNING, GridKt._get(grid, 0).status());
        LocatorJ._get(GridKt._getCellComponent(grid, 0, "progress"), Span.class, spec -> spec.withText("50 / 100"));
        assertTrue(LocatorJ._find(Anchor.class, spec -> spec.withId("download-" + JOB_ID)).isEmpty(), "sin fichero hasta terminar");

        history.set(0, job(JobType.PROFILE_IMPORT, JobStatus.COMPLETED_WITH_ERRORS, 100, 100, 98, 2, null));
        view.pollOnce();
        MockVaadin.clientRoundtrip();
        Component actions = GridKt._getCellComponent(grid, 0, "actions");
        LocatorJ._get(actions, Anchor.class, spec -> spec.withId("download-" + JOB_ID));
        LocatorJ._get(Span.class, spec -> spec.withText("1 en el servicio, 0 en curso"));

        // La fila no trae los errores por elemento: el boton los pide al detalle de la familia.
        when(jobsClient.profileJob(JOB_ID)).thenReturn(job(JobType.PROFILE_IMPORT, JobStatus.COMPLETED_WITH_ERRORS, 100, 100, 98, 2,
                List.of(new JobItemError(118, "create", "ValidationException", "kp obligatorio [kp]"))));
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withText("Errores")));
        Dialog errors = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        Grid<JobItemError> errorRows = LocatorJ._get(errors, Grid.class);
        assertEquals(1, GridKt._size(errorRows));
        assertEquals("kp obligatorio [kp]", GridKt._get(errorRows, 0).message());
        errors.close();

        // Terminado todo, la pantalla deja de preguntar.
        clearInvocations(jobsClient);
        view.pollOnce();
        verify(jobsClient, never()).list(anyInt(), anyInt(), any(), any());
        verify(jobsClient, never()).profileJob(any());
    }

    /** README_ASYNC_JOBS §4: el 429 trae el trabajo rechazado; el servicio lo persiste y se dice cuando reintentar. */
    @Test
    void aRejectedLaunchIsListedAsRejectedAndSaysWhenToRetry() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        List<JobDto> history = new ArrayList<>();
        stubJobHistory(history);
        String body = """
                {"id":"6f1c0000-0000-4000-8000-000000000001","type":"PROFILE_EXPORT","status":"REJECTED",
                 "createdAt":"2026-08-27T09:12:03Z","trackId":3,"mapperType":"basic","processedItems":0,"successfulItems":0,"failedItems":0}
                """;
        when(jobsClient.exportProfiles(3L, "basic")).thenAnswer(call -> {
            history.addFirst(job(JobType.PROFILE_EXPORT, JobStatus.REJECTED, null, 0, 0, 0, null));
            throw BackofficeApiException.of(HttpStatus.TOO_MANY_REQUESTS, ApiProblem.empty(),
                    "corr-9", Duration.ofSeconds(30), "POST /api/configuration/profiles/jobs/export", body);
        });

        UI.getCurrent().navigate(JobsView.ROUTE);
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(ComboBox.class, spec -> spec.withId("export-track"));
        LocatorJ._setValue(track, new RefItem(3L, "TRACK 1 (EP4)"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("export-profiles")));

        Grid<JobDto> grid = jobsGrid();
        assertEquals(1, GridKt._size(grid));
        assertEquals(JobStatus.REJECTED, GridKt._get(grid, 0).status());
        assertEquals("Exportacion de TRACK 1 (EP4)", GridKt._getFormattedRow(grid, 0).getFirst());
        NotificationsKt.expectNotifications("Sin hueco para Exportacion de TRACK 1 (EP4): el servicio lo ha rechazado. Intentalo en 30 s.");
        clearInvocations(jobsClient);
        LocatorJ._get(JobsView.class).pollOnce();
        verify(jobsClient, never()).list(anyInt(), anyInt(), any(), any());
        verify(jobsClient, never()).profileJob(any());
    }

    /** Un trabajo lanzado desde aqui que no esta en la pagina se sigue por su familia, y se avisa al terminar. */
    @Test
    void aJobLaunchedHereButOffThePageIsStillFollowedByItsFamily() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT");
        stubJobHistory(List.of());
        when(jobsClient.importProfiles(any(), eq(false))).thenReturn(job(JobType.PROFILE_IMPORT, JobStatus.PENDING, null, 0, 0, 0, null));

        UI.getCurrent().navigate(JobsView.ROUTE);
        UploadKt._upload(LocatorJ._get(Upload.class, spec -> spec.withId("import-profiles-upload")),
                "profile-master.xlsx", JobsView.XLSX, "PK-xlsx".getBytes());
        MockVaadin.clientRoundtrip();
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("import-profiles")));

        JobsView view = LocatorJ._get(JobsView.class);
        when(jobsClient.profileJob(JOB_ID)).thenReturn(job(JobType.PROFILE_IMPORT, JobStatus.COMPLETED, 10, 10, 10, 0, null));
        view.pollOnce();
        MockVaadin.clientRoundtrip();

        verify(jobsClient).profileJob(JOB_ID);
        NotificationsKt.expectNotifications("Trabajo encolado: Importacion del maestro de perfiles (profile-master.xlsx)",
                "Importacion del maestro de perfiles (profile-master.xlsx): Terminado");
        clearInvocations(jobsClient);
        view.pollOnce();
        verify(jobsClient, never()).profileJob(any());
    }

    /** La lista es la del servicio: se ve lo lanzado desde cualquier sesion, paginado y filtrado alli. */
    @Test
    void theJobHistoryComesFromTheServicePagedAndFilteredByTypeAndStatus() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        List<JobDto> history = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            history.add(job(UUID.randomUUID(), i % 2 == 0 ? JobType.LOV_IMPORT : JobType.MASTER_DATA_REPUBLISH, JobStatus.COMPLETED,
                    Instant.parse("2026-08-27T09:12:03Z").minusSeconds(i)));
        }
        stubJobHistory(history);

        UI.getCurrent().navigate(JobsView.ROUTE);
        Grid<JobDto> grid = jobsGrid();
        assertEquals(20, GridKt._size(grid));
        assertEquals("Importacion del catalogo de LOV", GridKt._getFormattedRow(grid, 0).getFirst(), "lanzado desde otra parte: se describe por su tipo");
        LocatorJ._get(Span.class, spec -> spec.withText("25 en el servicio, 0 en curso"));
        LocatorJ._get(Span.class, spec -> spec.withText("Pagina 1 de 2"));

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("jobs-next")));
        assertEquals(5, GridKt._size(grid));
        LocatorJ._get(Span.class, spec -> spec.withText("Pagina 2 de 2"));

        @SuppressWarnings("unchecked")
        ComboBox<JobType> type = LocatorJ._get(ComboBox.class, spec -> spec.withId("jobs-type"));
        LocatorJ._setValue(type, JobType.MASTER_DATA_REPUBLISH);
        assertEquals(12, GridKt._size(grid));
        LocatorJ._get(Span.class, spec -> spec.withText("Pagina 1 de 1"));
        verify(jobsClient, atLeastOnce()).list(0, 20, JobType.MASTER_DATA_REPUBLISH, null);

        clearInvocations(jobsClient);
        LocatorJ._get(JobsView.class).pollOnce();
        verify(jobsClient, never()).list(anyInt(), anyInt(), any(), any());
    }

    @Test
    void aReaderCanOnlyExport() {
        loginAs("config.lector", "ROLE_CONFIG_READ");

        UI.getCurrent().navigate(JobsView.ROUTE);

        LocatorJ._get(Button.class, spec -> spec.withId("export-profiles"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("import-profiles")).isEmpty());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("import-lovs")).isEmpty());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("republish")).isEmpty());
    }

    @Test
    void theLovCatalogueImportAlsoNeedsLovManage() {
        loginAs("config.editor", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT");

        UI.getCurrent().navigate(JobsView.ROUTE);

        LocatorJ._get(Button.class, spec -> spec.withId("import-profiles"));
        LocatorJ._get(Button.class, spec -> spec.withId("republish"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("import-lovs")).isEmpty());
    }

    // --- Usuarios (mto-users) ---------------------------------------------------------------------

    private static final String USERS_ROUTE = UsersView.ROUTE;
    private static final String ANA_ID = "0d5f1d1a-1111-4e43-9a5b-000000000001";
    private static final String BRUNO_ID = "0d5f1d1a-1111-4e43-9a5b-000000000002";
    private static final String CARLA_ID = "0d5f1d1a-1111-4e43-9a5b-000000000003";

    private static UserDto user(String id, String username, String firstName, String lastName, String email, boolean enabled,
                                Map<String, List<String>> attributes) {
        return new UserDto(id, username, firstName, lastName, email, email != null, enabled,
                Instant.parse("2026-09-01T08:30:00Z"), attributes, List.of());
    }

    private static List<UserDto> threeUsers() {
        return List.of(
                user(ANA_ID, "ana", "Ana", "Alvarez", "ana@mto.local", true, Map.of("dept", List.of("taller"))),
                user(BRUNO_ID, "bruno", "Bruno", "Blanco", "bruno@mto.local", true, Map.of()),
                user(CARLA_ID, "carla", "Carla", null, null, false, Map.of("dept", List.of("oficina"))));
    }

    /**
     * El servicio simulado: filtra por texto, atributo y estado, y pagina con {@code first}/{@code max}
     * como hace el de verdad (y como el de verdad, rechaza {@code search} junto a {@code attribute}).
     */
    private void stubUsers(List<UserDto> all) {
        when(usersClient.search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt())).thenAnswer(call -> {
            String search = call.getArgument(0);
            Boolean enabled = call.getArgument(3);
            List<String> attributes = call.getArgument(5);
            int first = call.getArgument(6);
            int max = call.getArgument(7);
            if (search != null && attributes != null) {
                throw new IllegalStateException("search y attribute no viajan juntos");
            }
            if (max > 200) {
                throw new IllegalStateException("max supera el tope del servicio: " + max);
            }
            String text = search == null ? "" : search.toLowerCase(Locale.ROOT);
            List<UserDto> matching = all.stream()
                    .filter(dto -> text.isEmpty() || dto.username().contains(text) || dto.fullName().toLowerCase(Locale.ROOT).contains(text))
                    .filter(dto -> enabled == null || enabled.equals(dto.enabled()))
                    .filter(dto -> attributes == null || attributes.stream().allMatch(pair -> hasAttribute(dto, pair)))
                    .toList();
            int from = Math.min(first, matching.size());
            int to = Math.min(from + max, matching.size());
            return new UsersPage<>(matching.subList(from, to), first, max, matching.size());
        });
    }

    private static boolean hasAttribute(UserDto dto, String pair) {
        int separator = pair.indexOf(':');
        List<String> values = dto.attributes() == null ? null : dto.attributes().get(pair.substring(0, separator));
        return values != null && values.contains(pair.substring(separator + 1));
    }

    @SuppressWarnings("unchecked")
    private static Grid<UserDto> userGrid() {
        return LocatorJ._get(Grid.class);
    }

    private static Button userAction(String id) {
        return LocatorJ._get(GridKt._getCellComponent(userGrid(), 0, "actions"), Button.class, spec -> spec.withId(id));
    }

    @Test
    void theMenuGroupsTheUsersScreensUnderUsers() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.contains("Usuarios"), labels.toString());
        assertFalse(labels.contains("Infraestructura"), "sin config-read no hay infraestructura: " + labels);
        assertFalse(labels.contains("Catalogos"), labels.toString());
        SideNavItem users = LocatorJ._get(SideNavItem.class, spec -> spec.withLabel("Usuarios"));
        assertEquals(USERS_ROUTE, users.getPath().replaceFirst("^/", ""), "la lista es a la vez el nodo del grupo");
        assertEquals(List.of("Perfiles de usuario", "Roles de cliente"), users.getItems().stream().map(SideNavItem::getLabel).toList(),
                "los catalogos del modulo cuelgan del nodo");
    }

    @Test
    void theUsersViewIsNotReachableWithARealmRoleOnly() {
        loginAs("usuarios.impostor", "ROLE_REALM_USERS_READ", "ROLE_REALM_MTO_USERS_ADMIN");

        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(USERS_ROUTE));

        assertTrue(LocatorJ._find(UsersView.class).isEmpty());
        verify(usersClient, never()).search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt());
    }

    @Test
    void theUsersListIsPagedWithFirstAndMaxAndFilteredInTheServer() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        List<UserDto> many = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            many.add(user(UUID.randomUUID().toString(), String.format("user%03d", i), "Nombre", "Apellido " + i,
                    "user" + i + "@mto.local", i % 3 != 0, Map.of("dept", List.of(i % 2 == 0 ? "taller" : "oficina"))));
        }
        stubUsers(many);

        UI.getCurrent().navigate(USERS_ROUTE);
        Grid<UserDto> grid = userGrid();

        assertEquals(120, GridKt._size(grid));
        LocatorJ._get(Span.class, spec -> spec.withText("120 usuarios"));
        assertEquals("user000", GridKt._get(grid, 0).username());
        assertEquals("user077", GridKt._get(grid, 77).username(), "la segunda pagina se pide con su first");
        verify(usersClient, atLeastOnce()).search(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), anyInt());
        verify(usersClient, atLeastOnce()).search(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), intThat(first -> first > 0), anyInt());
        assertTrue(grid.getColumns().stream().noneMatch(Grid.Column::isSortable), "la API no ordena y las columnas tampoco");

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("users-search")), "user01");
        assertEquals(10, GridKt._size(grid));
        verify(usersClient, atLeastOnce()).search(eq("user01"), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), anyInt());

        @SuppressWarnings("unchecked")
        Select<EnabledFilter> state = LocatorJ._get(Select.class, spec -> spec.withLabel("Estado"));
        LocatorJ._setValue(state, EnabledFilter.DISABLED);
        assertEquals(3, GridKt._size(grid), "user012, user015 y user018 estan desactivados");
        verify(usersClient, atLeastOnce()).search(eq("user01"), isNull(), isNull(), eq(false), isNull(), isNull(), eq(0), anyInt());
        LocatorJ._get(Span.class, spec -> spec.withText("3 usuarios"));
    }

    @Test
    void theSearchTextAndTheAttributeFilterExcludeEachOther() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUsers(threeUsers());

        UI.getCurrent().navigate(USERS_ROUTE);
        TextField search = LocatorJ._get(TextField.class, spec -> spec.withId("users-search"));
        TextField attribute = LocatorJ._get(TextField.class, spec -> spec.withId("users-attribute"));

        LocatorJ._setValue(attribute, "dept:taller");
        assertFalse(search.isEnabled(), "con un atributo la busqueda se deshabilita");
        assertEquals(1, GridKt._size(userGrid()));
        assertEquals("ana", GridKt._get(userGrid(), 0).username());
        verify(usersClient, atLeastOnce()).search(isNull(), isNull(), isNull(), isNull(), isNull(), eq(List.of("dept:taller")), eq(0), anyInt());

        clearInvocations(usersClient);
        LocatorJ._setValue(attribute, "sin separador");
        assertTrue(attribute.isInvalid(), "clave:valor o nada");
        verify(usersClient, never()).search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt());

        LocatorJ._setValue(attribute, "");
        assertTrue(search.isEnabled());
        LocatorJ._setValue(search, "bru");
        assertFalse(attribute.isEnabled(), "con texto de busqueda el atributo se deshabilita");
        assertEquals(1, GridKt._size(userGrid()));
        assertEquals("bruno", GridKt._get(userGrid(), 0).username());
        verify(usersClient, never()).search(any(), any(), any(), any(), any(), argThat(attributes -> attributes != null), anyInt(), anyInt());
    }

    @Test
    void aReadOnlyPersonSeesTheUsersWithoutAnyWriteControl() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUsers(threeUsers());

        UI.getCurrent().navigate(USERS_ROUTE);

        assertEquals(3, GridKt._size(userGrid()));
        List<String> firstRow = GridKt._getFormattedRow(userGrid(), 0);
        assertTrue(firstRow.contains("ana") && firstRow.contains("Ana Alvarez") && firstRow.contains("ana@mto.local"), firstRow.toString());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("user-create")).isEmpty());
        Component actions = GridKt._getCellComponent(userGrid(), 0, "actions");
        LocatorJ._get(actions, Button.class, spec -> spec.withId("open-" + ANA_ID));
        assertTrue(LocatorJ._find(actions, Button.class, spec -> spec.withId("edit-" + ANA_ID)).isEmpty());
        assertTrue(LocatorJ._find(actions, Button.class, spec -> spec.withId("toggle-" + ANA_ID)).isEmpty());
        assertTrue(LocatorJ._find(actions, Button.class, spec -> spec.withId("delete-" + ANA_ID)).isEmpty());
    }

    @Test
    void aManagerWithoutDeleteSeesEverythingButTheTrash() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUsers(threeUsers());

        UI.getCurrent().navigate(USERS_ROUTE);

        LocatorJ._get(Button.class, spec -> spec.withId("user-create"));
        Component actions = GridKt._getCellComponent(userGrid(), 0, "actions");
        LocatorJ._get(actions, Button.class, spec -> spec.withId("edit-" + ANA_ID));
        LocatorJ._get(actions, Button.class, spec -> spec.withId("toggle-" + ANA_ID));
        assertTrue(LocatorJ._find(actions, Button.class, spec -> spec.withId("delete-" + ANA_ID)).isEmpty());
    }

    @Test
    void creatingAUserPostsTheFormAndTheTemporaryPassword() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUsers(threeUsers());
        when(usersClient.create(any())).thenAnswer(call -> {
            CreateUserRequest request = call.getArgument(0);
            return user(UUID.randomUUID().toString(), request.username(), request.firstName(), request.lastName(), request.email(), true, request.attributes());
        });

        UI.getCurrent().navigate(USERS_ROUTE);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("user-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));
        TextField username = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Usuario"));
        assertTrue(username.isInvalid(), "el usuario es obligatorio");
        verify(usersClient, never()).create(any());

        LocatorJ._setValue(username, "dario.diaz");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), "Dario");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Email")), "dario@mto.local");
        PasswordField password = LocatorJ._get(dialog, PasswordField.class, spec -> spec.withLabel("Contrasena temporal"));
        LocatorJ._setValue(password, "corta");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));
        assertTrue(password.isInvalid(), "menos de ocho caracteres no viaja");
        verify(usersClient, never()).create(any());

        LocatorJ._setValue(password, "Temporal-2026");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RequiredAction> actions = LocatorJ._get(dialog, MultiSelectComboBox.class, spec -> spec.withLabel("Acciones requeridas al entrar"));
        LocatorJ._setValue(actions, Set.of(RequiredAction.UPDATE_PASSWORD));
        LocatorJ._setValue(LocatorJ._get(dialog, TextArea.class, spec -> spec.withLabel("Atributos (clave=valor por linea)")), "dept=taller\ndept=noche");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));

        verify(usersClient).create(argThat(request -> "dario.diaz".equals(request.username())
                && "Dario".equals(request.firstName())
                && request.lastName() == null
                && "dario@mto.local".equals(request.email())
                && Boolean.TRUE.equals(request.enabled())
                && "Temporal-2026".equals(request.temporaryPassword())
                && List.of(RequiredAction.UPDATE_PASSWORD).equals(request.requiredActions())
                && Map.of("dept", List.of("taller", "noche")).equals(request.attributes())));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");
        NotificationsKt.expectNotifications("Guardado dario.diaz");
    }

    @Test
    void serverValidationErrorsLandOnTheUserFieldsAndTheDialogStaysOpen() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUsers(threeUsers());
        ApiProblem problem = new ApiProblem("about:blank", "Bad Request", 400, "La peticion no es valida", null,
                "REQ-VALIDATION", null, null, null, false,
                List.of(new ApiFieldError("email", null, "must be a well-formed email address"),
                        new ApiFieldError("temporaryPassword", null, "la politica pide un digito")), null);
        when(usersClient.create(any())).thenThrow(
                BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-u2", null, "POST /api/users"));

        UI.getCurrent().navigate(USERS_ROUTE);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("user-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Usuario")), "elena");
        TextField email = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Email"));
        LocatorJ._setValue(email, "elena@mto.local");
        PasswordField password = LocatorJ._get(dialog, PasswordField.class, spec -> spec.withLabel("Contrasena temporal"));
        LocatorJ._setValue(password, "sinDigitos");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));

        assertTrue(email.isInvalid());
        assertEquals("must be a well-formed email address", email.getErrorMessage());
        assertTrue(password.isInvalid());
        assertEquals("la politica pide un digito", password.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "todo cayo en un campo: nada que notificar");
    }

    @Test
    void editingAUserSendsOnlyWhatChangedAndKeepsTheUsername() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUsers(threeUsers());
        when(usersClient.update(eq(ANA_ID), any())).thenAnswer(call -> threeUsers().getFirst());

        UI.getCurrent().navigate(USERS_ROUTE);
        LocatorJ._click(userAction("edit-" + ANA_ID));
        Dialog dialog = LocatorJ._get(Dialog.class);
        TextField username = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Usuario"));
        assertEquals("ana", username.getValue());
        assertTrue(username.isReadOnly(), "el nombre de usuario no se cambia");
        assertTrue(LocatorJ._find(dialog, PasswordField.class).isEmpty(), "la contrasena tiene su propio dialogo en la ficha");
        TextArea attributes = LocatorJ._get(dialog, TextArea.class, spec -> spec.withLabel("Atributos (clave=valor por linea)"));
        assertEquals("dept=taller", attributes.getValue());
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Apellidos")), "Alvarez Arias");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Email")), "");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));

        verify(usersClient).update(eq(ANA_ID), argThat(request -> request.firstName() == null
                && "Alvarez Arias".equals(request.lastName())
                && "".equals(request.email())
                && request.emailVerified() == null
                && request.attributes() == null));
        verify(usersClient, never()).create(any());
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");
    }

    @Test
    void enablingAndDisablingPatchTheFlagWithoutConfirmation() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUsers(threeUsers());
        when(usersClient.setEnabled(eq(ANA_ID), any())).thenAnswer(call -> {
            UserEnabledRequest request = call.getArgument(1);
            return user(ANA_ID, "ana", "Ana", "Alvarez", "ana@mto.local", request.enabled(), Map.of());
        });

        UI.getCurrent().navigate(USERS_ROUTE);
        LocatorJ._click(userAction("toggle-" + ANA_ID));

        assertTrue(LocatorJ._find(ConfirmDialog.class).isEmpty(), "activar y desactivar son reversibles: sin confirmacion");
        verify(usersClient).setEnabled(ANA_ID, new UserEnabledRequest(false));
        NotificationsKt.expectNotifications("Desactivado ana");
    }

    @Test
    void deletingAUserAsksForConfirmationThenCallsTheService() {
        loginAs("usuarios.responsable", "ROLE_USERS_READ", "ROLE_USERS_WRITE", "ROLE_USERS_DELETE");
        stubUsers(threeUsers());

        UI.getCurrent().navigate(USERS_ROUTE);
        LocatorJ._click(userAction("delete-" + ANA_ID));

        verify(usersClient, never()).delete(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));

        verify(usersClient).delete(ANA_ID);
        NotificationsKt.expectNotifications("Borrado ana");
    }

    @Test
    void attributesAreParsedOneKeyValuePerLineAndRejectWhatCannotBeRead() {
        assertEquals(Map.of("dept", List.of("taller", "noche"), "turno", List.of("")),
                UserAttributes.parse(" dept = taller \n\ndept=noche\nturno=\n"));
        assertEquals(Map.of("url", List.of("http://x/a=b")), UserAttributes.parse("url=http://x/a=b"), "solo parte el primer =");
        assertTrue(UserAttributes.parse(null).isEmpty());
        assertTrue(UserAttributes.parse("  \n ").isEmpty());

        IllegalArgumentException noSeparator = assertThrows(IllegalArgumentException.class, () -> UserAttributes.parse("dept=taller\nsin separador"));
        assertTrue(noSeparator.getMessage().startsWith("Linea 2"), noSeparator.getMessage());
        IllegalArgumentException noKey = assertThrows(IllegalArgumentException.class, () -> UserAttributes.parse("=valor"));
        assertTrue(noKey.getMessage().startsWith("Linea 1"), noKey.getMessage());

        assertEquals("dept=noche\ndept=taller\nturno=", UserAttributes.format(Map.of("turno", List.of(""), "dept", List.of("noche", "taller"))));
        assertEquals("", UserAttributes.format(null));
        assertEquals("dept=noche\ndept=taller\nturno=", UserAttributes.format(UserAttributes.parse(UserAttributes.format(
                Map.of("turno", List.of(""), "dept", List.of("noche", "taller"))))), "ida y vuelta estable");
    }

    // --- La ficha del usuario -----------------------------------------------------------------------

    private static final String ANA_ROUTE = UsersView.ROUTE_PREFIX + "/" + ANA_ID;
    private static final RealmProfileSummaryDto VIEWER = new RealmProfileSummaryDto("mto-users-viewer", "Solo lectura de usuarios");
    private static final RealmProfileSummaryDto MANAGER = new RealmProfileSummaryDto("mto-users-manager", "Gestion de usuarios");
    private static final RealmProfileSummaryDto ADMIN = new RealmProfileSummaryDto("mto-users-admin", null);
    private static final ClientDto USERS_API = new ClientDto("mto-users-api", "MTO Users API", null);
    private static final ClientDto CONFIGURATION_API = new ClientDto("mto-configuration-api", null, null);
    private static final List<String> ANA_REALM_ROLES = List.of("mto-users-viewer", "default-roles-mto");

    private static UserDto ana() {
        return threeUsers().getFirst();
    }

    private static UserRolesDto anaRoles(String... usersApiRoles) {
        return new UserRolesDto(ANA_REALM_ROLES, List.of(new ClientRoleAssignmentDto("mto-users-api", List.of(usersApiRoles))));
    }

    /** El servicio simulado detras de la ficha: el usuario, sus perfiles y roles, y los catalogos. */
    private void stubUserDetail(UserDto dto) {
        when(usersClient.get(dto.id())).thenReturn(dto);
        when(usersClient.userProfiles(dto.id())).thenReturn(List.of(VIEWER));
        when(usersClient.userRoles(dto.id())).thenReturn(anaRoles("users-read"));
        when(usersClient.profiles()).thenReturn(List.of(VIEWER, MANAGER, ADMIN));
        when(usersClient.clients()).thenReturn(List.of(USERS_API, CONFIGURATION_API));
        when(usersClient.clientRoles("mto-users-api")).thenReturn(List.of(
                new ClientRoleDto("users-read", null, false), new ClientRoleDto("users-write", null, false), new ClientRoleDto("users-delete", null, false)));
        when(usersClient.sessions(dto.id())).thenReturn(List.of(
                new UserSessionDto("s1", dto.username(), "10.0.0.7", Instant.parse("2026-09-21T07:00:00Z"), Instant.parse("2026-09-21T07:45:00Z"), List.of("mto-backoffice")),
                new UserSessionDto("s2", dto.username(), "10.0.0.8", Instant.parse("2026-09-21T08:00:00Z"), null, List.of("mto-frontend", "mto-gateway"))));
        when(usersClient.offlineSessions(dto.id())).thenReturn(List.of(
                new UserSessionDto("o1", dto.username(), null, Instant.parse("2026-09-01T09:00:00Z"), Instant.parse("2026-09-20T09:00:00Z"), List.of("mto-frontend"))));
        when(usersClient.credentials(dto.id())).thenReturn(List.of(
                new UserCredentialDto("c1", "password", null, Instant.parse("2026-09-01T08:30:00Z")),
                new UserCredentialDto("c2", "otp", "Movil", Instant.parse("2026-09-02T08:30:00Z"))));
    }

    @SuppressWarnings("unchecked")
    private static Grid<Object> gridWithId(String id) {
        return LocatorJ._get(Grid.class, spec -> spec.withId(id));
    }

    private static Button detailButton(String id) {
        return LocatorJ._get(Button.class, spec -> spec.withId(id));
    }

    private static void selectTab(int index) {
        LocatorJ._get(TabSheet.class).setSelectedIndex(index);
    }

    @Test
    void theListOpensTheDetailWithItsButton() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUsers(threeUsers());
        stubUserDetail(ana());

        UI.getCurrent().navigate(USERS_ROUTE);
        LocatorJ._click(userAction("open-" + ANA_ID));

        LocatorJ._get(UserDetailView.class);
        LocatorJ._get(H2.class, spec -> spec.withText("ana"));
        assertTrue(LocatorJ._find(UsersView.class).isEmpty());
    }

    @Test
    void theUserDetailShowsTheHeaderAndLoadsEachTabWhenSelected() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        UserDto ana = new UserDto(ANA_ID, "ana", "Ana", "Alvarez", "ana@mto.local", true, true,
                Instant.parse("2026-09-01T08:30:00Z"), Map.of("dept", List.of("taller", "noche")), List.of("UPDATE_PASSWORD", "CUSTOM_ACTION"));
        stubUserDetail(ana);

        UI.getCurrent().navigate(ANA_ROUTE);

        LocatorJ._get(H2.class, spec -> spec.withText("ana"));
        LocatorJ._get(Span.class, spec -> spec.withText("Activo"));
        LocatorJ._get(Span.class, spec -> spec.withText("Email verificado"));
        assertTrue(LocatorJ._find(Span.class).stream().anyMatch(span -> span.getText().startsWith("Ana Alvarez · ana@mto.local · creado el ")),
                "nombre, email y fecha en la cabecera");
        LocatorJ._get(Span.class, spec -> spec.withText("Acciones pendientes al entrar: Cambiar la contrasena, CUSTOM_ACTION"));
        LocatorJ._get(Span.class, spec -> spec.withText("Atributos: dept=taller|noche"));

        verify(usersClient).userProfiles(ANA_ID);
        verify(usersClient, never()).userRoles(any());
        Grid<Object> profiles = gridWithId("profiles-grid");
        assertEquals(1, GridKt._size(profiles));
        assertTrue(GridKt._getFormattedRow(profiles, 0).contains("mto-users-viewer"));

        selectTab(1);
        verify(usersClient).userRoles(ANA_ID);
        Grid<Object> roles = gridWithId("roles-grid");
        assertEquals(1, GridKt._size(roles));
        List<String> row = GridKt._getFormattedRow(roles, 0);
        assertTrue(row.contains("mto-users-api") && row.contains("users-read"), row.toString());
        LocatorJ._get(Span.class, spec -> spec.withText("Roles de realm (los perfiles estan entre ellos): mto-users-viewer, default-roles-mto"));

        selectTab(0);
        selectTab(1);
        verify(usersClient, times(1)).userRoles(ANA_ID);
        verify(usersClient, times(1)).userProfiles(ANA_ID);
    }

    @Test
    void anUnknownUserGoesBackToTheListWithANotification() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        ApiProblem problem = new ApiProblem("about:blank", "Not Found", 404, "User nope not found", null,
                "USR-404", null, null, null, false, null, null);
        when(usersClient.get("nope")).thenThrow(BackofficeApiException.of(HttpStatus.NOT_FOUND, problem, "corr-u4", null, "GET /api/users/nope"));

        UI.getCurrent().navigate(UsersView.ROUTE_PREFIX + "/nope");

        LocatorJ._get(UsersView.class);
        assertTrue(LocatorJ._find(UserDetailView.class).isEmpty());
        NotificationsKt.expectNotifications("No existe el usuario nope");
    }

    @Test
    void aReadOnlyPersonSeesTheDetailWithoutAnyAction() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);

        for (String id : List.of("user-edit", "user-toggle", "user-reset-password", "user-actions-email", "user-delete")) {
            assertTrue(LocatorJ._find(Button.class, spec -> spec.withId(id)).isEmpty(), id + " no se ofrece sin su permiso");
        }
        LocatorJ._get(Button.class, spec -> spec.withText("Volver a la lista"));
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("profile-assign")).isEmpty());
        assertNull(gridWithId("profiles-grid").getColumnByKey("actions"));
        selectTab(1);
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("role-client")).isEmpty());
        assertNull(gridWithId("roles-grid").getColumnByKey("actions"));
        verify(usersClient, never()).profiles();
        verify(usersClient, never()).clients();
    }

    @Test
    void eachDetailActionNeedsItsOwnPermission() {
        loginAs("usuarios.mixto", "ROLE_USERS_READ", "ROLE_USERS_PASSWORD_RESET", "ROLE_USERS_PROFILES_WRITE", "ROLE_USERS_DELETE");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);

        detailButton("user-reset-password");
        detailButton("user-delete");
        for (String id : List.of("user-edit", "user-toggle", "user-actions-email")) {
            assertTrue(LocatorJ._find(Button.class, spec -> spec.withId(id)).isEmpty(), id + " pide users-write");
        }
        LocatorJ._get(ComboBox.class, spec -> spec.withId("profile-assign"));
        selectTab(1);
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("role-client")).isEmpty(), "los roles piden users-roles-write");
    }

    @Test
    void assigningAndRemovingAProfileCallTheServiceAndPaintWhatItReturns() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_PROFILES_WRITE");
        stubUserDetail(ana());
        when(usersClient.assignProfile(ANA_ID, "mto-users-manager")).thenReturn(List.of(VIEWER, MANAGER));
        when(usersClient.removeProfile(ANA_ID, "mto-users-viewer")).thenReturn(List.of(MANAGER));

        UI.getCurrent().navigate(ANA_ROUTE);
        @SuppressWarnings("unchecked")
        ComboBox<RealmProfileSummaryDto> picker = LocatorJ._get(ComboBox.class, spec -> spec.withId("profile-assign"));
        Button assign = detailButton("profile-assign-button");
        assertEquals(List.of(MANAGER, ADMIN), picker.getListDataView().getItems().toList(), "solo lo que falta por asignar");
        assertFalse(assign.isEnabled());

        LocatorJ._setValue(picker, MANAGER);
        LocatorJ._click(assign);

        verify(usersClient).assignProfile(ANA_ID, "mto-users-manager");
        Grid<Object> profiles = gridWithId("profiles-grid");
        assertEquals(2, GridKt._size(profiles), "se pinta lo que devuelve el servicio");
        assertEquals(List.of(ADMIN), picker.getListDataView().getItems().toList());
        NotificationsKt.expectNotifications("Perfil mto-users-manager asignado");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("remove-profile-mto-users-viewer")));

        verify(usersClient).removeProfile(ANA_ID, "mto-users-viewer");
        assertEquals(1, GridKt._size(profiles));
        assertTrue(GridKt._getFormattedRow(profiles, 0).contains("mto-users-manager"));
        verify(usersClient, times(1)).userProfiles(ANA_ID);
    }

    @Test
    void clientRolesAreAddedWithAPutAndRemovedWithADeleteBody() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_ROLES_WRITE");
        stubUserDetail(ana());
        when(usersClient.addClientRoles(eq(ANA_ID), eq("mto-users-api"), any())).thenReturn(anaRoles("users-read", "users-write"));
        when(usersClient.removeClientRoles(eq(ANA_ID), eq("mto-users-api"), any())).thenReturn(anaRoles("users-write"));

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(1);
        @SuppressWarnings("unchecked")
        ComboBox<ClientDto> clientPicker = LocatorJ._get(ComboBox.class, spec -> spec.withId("role-client"));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<String> rolePicker = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("role-names"));
        Button assign = detailButton("role-assign");
        assertEquals(List.of(USERS_API, CONFIGURATION_API), clientPicker.getListDataView().getItems().toList());
        assertFalse(rolePicker.isEnabled(), "primero el cliente");

        LocatorJ._setValue(clientPicker, USERS_API);
        verify(usersClient).clientRoles("mto-users-api");
        assertEquals(List.of("users-write", "users-delete"), rolePicker.getListDataView().getItems().toList(), "sin los que ya tiene");
        assertFalse(assign.isEnabled());
        LocatorJ._setValue(rolePicker, Set.of("users-write"));
        LocatorJ._click(assign);

        verify(usersClient).addClientRoles(ANA_ID, "mto-users-api", new RoleNamesRequest(List.of("users-write")));
        Grid<Object> roles = gridWithId("roles-grid");
        assertEquals(2, GridKt._size(roles));
        NotificationsKt.expectNotifications("Rol users-write asignado");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(roles, 0, "actions"), Button.class, spec -> spec.withId("remove-role-mto-users-api-users-read")));

        verify(usersClient).removeClientRoles(ANA_ID, "mto-users-api", new RoleNamesRequest(List.of("users-read")));
        assertEquals(1, GridKt._size(roles));
        assertTrue(GridKt._getFormattedRow(roles, 0).contains("users-write"));
        verify(usersClient, times(1)).userRoles(ANA_ID);
    }

    @Test
    void theTemporaryPasswordDialogDefaultsToTemporaryAndNeverSendsAShortOne() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_PASSWORD_RESET");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-reset-password"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        Checkbox temporary = LocatorJ._get(dialog, Checkbox.class);
        PasswordField password = LocatorJ._get(dialog, PasswordField.class);
        Button save = LocatorJ._get(dialog, Button.class, spec -> spec.withId("reset-password-save"));
        assertTrue(temporary.getValue(), "temporal por defecto: la fija otra persona");

        LocatorJ._click(save);
        assertTrue(password.isInvalid(), "obligatoria");
        LocatorJ._setValue(password, "corta");
        LocatorJ._click(save);
        assertTrue(password.isInvalid(), "menos de ocho no viaja");
        verify(usersClient, never()).resetPassword(any(), any());

        LocatorJ._setValue(password, "Temporal-2026");
        LocatorJ._click(save);

        verify(usersClient).resetPassword(ANA_ID, new ResetPasswordRequest("Temporal-2026", true));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra");
        NotificationsKt.expectNotifications("Contrasena fijada para ana (temporal)");
    }

    @Test
    void thePasswordPolicyOfTheRealmLandsOnThePasswordField() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_PASSWORD_RESET");
        stubUserDetail(ana());
        ApiProblem problem = new ApiProblem("about:blank", "Bad Request", 400, "Keycloak rejected the request", null,
                "KC-400", null, null, null, false, List.of(new ApiFieldError("password", null, "invalidPasswordMinDigitsMessage")), null);
        doThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-u5", null, "POST /api/users/" + ANA_ID + "/reset-password"))
                .when(usersClient).resetPassword(eq(ANA_ID), any());

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-reset-password"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        PasswordField password = LocatorJ._get(dialog, PasswordField.class);
        LocatorJ._setValue(LocatorJ._get(dialog, Checkbox.class), false);
        LocatorJ._setValue(password, "sinDigitosAqui");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("reset-password-save")));

        verify(usersClient).resetPassword(ANA_ID, new ResetPasswordRequest("sinDigitosAqui", false));
        assertTrue(password.isInvalid());
        assertEquals("invalidPasswordMinDigitsMessage", password.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");
    }

    @Test
    void theActionsEmailDialogSendsTheSelectedActionsAndReportsA502WithItsDetail() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUserDetail(ana());
        ApiProblem problem = new ApiProblem("about:blank", "Bad Gateway", 502, "Keycloak no ha podido enviar el correo: SMTP no configurado en el realm", null,
                "KC-502", null, null, null, false, null, null);
        doThrow(BackofficeApiException.of(HttpStatus.BAD_GATEWAY, problem, "corr-u6", null, "POST /api/users/" + ANA_ID + "/execute-actions-email"))
                .when(usersClient).executeActionsEmail(eq(ANA_ID), any());

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-actions-email"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RequiredAction> actions = LocatorJ._get(dialog, MultiSelectComboBox.class);
        IntegerField lifespan = LocatorJ._get(dialog, IntegerField.class);
        Button send = LocatorJ._get(dialog, Button.class, spec -> spec.withId("actions-email-send"));

        LocatorJ._click(send);
        assertTrue(actions.isInvalid(), "al menos una accion");
        LocatorJ._setValue(actions, Set.of(RequiredAction.UPDATE_PASSWORD));
        LocatorJ._setValue(lifespan, 30);
        LocatorJ._click(send);
        assertTrue(lifespan.isInvalid(), "un enlace de 30 segundos no sirve a nadie");
        verify(usersClient, never()).executeActionsEmail(any(), any());

        LocatorJ._setValue(lifespan, 3600);
        LocatorJ._click(send);

        ExecuteActionsEmailRequest expected = new ExecuteActionsEmailRequest(List.of(RequiredAction.UPDATE_PASSWORD), 3600, null, null);
        verify(usersClient).executeActionsEmail(ANA_ID, expected);
        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText(
                "El servicio no ha podido completar la operacion. Keycloak no ha podido enviar el correo: SMTP no configurado en el realm"));
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "sin SMTP el dialogo sigue abierto: no hay nada que corregir aqui, pero tampoco se pierde");
        NotificationsKt.clearNotifications();

        doNothing().when(usersClient).executeActionsEmail(eq(ANA_ID), any());
        LocatorJ._click(send);

        verify(usersClient, times(2)).executeActionsEmail(ANA_ID, expected);
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Correo enviado a ana@mto.local");
    }

    @Test
    void theActionsEmailCannotBeSentToSomebodyWithoutEmail() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        UserDto carla = threeUsers().get(2);
        stubUserDetail(carla);

        UI.getCurrent().navigate(UsersView.ROUTE_PREFIX + "/" + CARLA_ID);
        LocatorJ._click(detailButton("user-actions-email"));

        Dialog dialog = LocatorJ._get(Dialog.class);
        assertFalse(LocatorJ._get(dialog, Button.class, spec -> spec.withId("actions-email-send")).isEnabled());
    }

    @Test
    void editingFromTheDetailReloadsTheHeader() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        UserDto renamed = user(ANA_ID, "ana", "Ana", "Alvarez Arias", "ana@mto.local", true, Map.of("dept", List.of("taller")));
        stubUserDetail(ana());
        when(usersClient.get(ANA_ID)).thenReturn(ana(), renamed);
        when(usersClient.update(eq(ANA_ID), any())).thenReturn(renamed);

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-edit"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Apellidos")), "Alvarez Arias");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));

        verify(usersClient).update(eq(ANA_ID), argThat(request -> "Alvarez Arias".equals(request.lastName()) && request.firstName() == null));
        verify(usersClient, times(2)).get(ANA_ID);
        assertTrue(LocatorJ._find(Span.class).stream().anyMatch(span -> span.getText().startsWith("Ana Alvarez Arias · ana@mto.local")),
                "la cabecera se repinta con lo releido");
    }

    @Test
    void disablingFromTheDetailRepaintsTheBadgeWithoutConfirmation() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        stubUserDetail(ana());
        when(usersClient.setEnabled(ANA_ID, new UserEnabledRequest(false)))
                .thenReturn(user(ANA_ID, "ana", "Ana", "Alvarez", "ana@mto.local", false, Map.of()));

        UI.getCurrent().navigate(ANA_ROUTE);
        Button toggle = detailButton("user-toggle");
        assertEquals("Desactivar", toggle.getText());
        LocatorJ._click(toggle);

        assertTrue(LocatorJ._find(ConfirmDialog.class).isEmpty());
        verify(usersClient).setEnabled(ANA_ID, new UserEnabledRequest(false));
        LocatorJ._get(Span.class, spec -> spec.withText("Desactivado"));
        assertEquals("Activar", toggle.getText());
        NotificationsKt.expectNotifications("Desactivado ana");
    }

    @Test
    void deletingFromTheDetailConfirmsAndGoesBackToTheList() {
        loginAs("usuarios.responsable", "ROLE_USERS_READ", "ROLE_USERS_DELETE");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-delete"));

        verify(usersClient, never()).delete(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));

        verify(usersClient).delete(ANA_ID);
        LocatorJ._get(UsersView.class);
        assertTrue(LocatorJ._find(UserDetailView.class).isEmpty());
        NotificationsKt.expectNotifications("Borrado ana");
    }

    // --- Sesiones, credenciales y «sacar a la persona» ----------------------------------------------

    @Test
    void theSessionsTabListsBothKindsAndClosingThemNeedsThePermission() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        verify(usersClient, never()).sessions(any());
        selectTab(2);

        verify(usersClient).sessions(ANA_ID);
        verify(usersClient).offlineSessions(ANA_ID);
        Grid<Object> sessions = gridWithId("sessions-grid");
        Grid<Object> offline = gridWithId("offline-sessions-grid");
        assertEquals(2, GridKt._size(sessions));
        assertEquals(1, GridKt._size(offline));
        List<String> first = GridKt._getFormattedRow(sessions, 0);
        assertTrue(first.contains("10.0.0.7") && first.contains("mto-backoffice"), first.toString());
        assertTrue(GridKt._getFormattedRow(sessions, 1).contains("mto-frontend, mto-gateway"));
        LocatorJ._get(Span.class, spec -> spec.withText("2 sesiones"));
        LocatorJ._get(Span.class, spec -> spec.withText("1 sesion offline"));
        assertNull(sessions.getColumnByKey("actions"));
        assertNull(offline.getColumnByKey("actions"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("sessions-revoke-all")).isEmpty());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("offline-sessions-revoke-all")).isEmpty());
    }

    @Test
    void closingOneSessionIsDirectAndClosingAllConfirmsForBothKinds() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_SESSIONS_WRITE");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(2);
        Grid<Object> sessions = gridWithId("sessions-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(sessions, 0, "actions"), Button.class, spec -> spec.withId("revoke-s1")));

        assertTrue(LocatorJ._find(ConfirmDialog.class).isEmpty(), "una sesion se cierra sin preguntar");
        verify(usersClient).revokeSession(ANA_ID, "s1");
        verify(usersClient, times(2)).sessions(ANA_ID);
        verify(usersClient, times(2)).offlineSessions(ANA_ID);
        NotificationsKt.expectNotifications("Sesion cerrada");

        LocatorJ._click(detailButton("sessions-revoke-all"));
        verify(usersClient, never()).revokeAllSessions(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(usersClient).revokeAllSessions(ANA_ID);
        verify(usersClient, never()).revokeAllOfflineSessions(any());
        NotificationsKt.expectNotifications("Sesiones cerradas");

        Grid<Object> offline = gridWithId("offline-sessions-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(offline, 0, "actions"), Button.class, spec -> spec.withId("revoke-offline-o1")));
        verify(usersClient).revokeOfflineSession(ANA_ID, "o1");
        NotificationsKt.expectNotifications("Sesion offline revocada");

        LocatorJ._click(detailButton("offline-sessions-revoke-all"));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(usersClient).revokeAllOfflineSessions(ANA_ID);
        verify(usersClient, never()).setEnabled(any(), any());
    }

    @Test
    void aSessionThatIsNotOfThisUserIsReportedAndTheListsReloaded() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_SESSIONS_WRITE");
        stubUserDetail(ana());
        ApiProblem problem = new ApiProblem("about:blank", "Not Found", 404, "Session s1 does not belong to user", null,
                "SES-404", null, null, null, false, null, null);
        doThrow(BackofficeApiException.of(HttpStatus.NOT_FOUND, problem, "corr-u7", null, "DELETE /api/users/" + ANA_ID + "/sessions/s1"))
                .when(usersClient).revokeSession(ANA_ID, "s1");

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(2);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("sessions-grid"), 0, "actions"), Button.class, spec -> spec.withId("revoke-s1")));

        NotificationsKt.expectNotifications("Esa sesion ya no existe o no es de este usuario.");
        verify(usersClient, times(2)).sessions(ANA_ID);
    }

    @Test
    void removingACredentialWarnsAndCallsTheService() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_CREDENTIALS_WRITE");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(3);
        Grid<Object> credentials = gridWithId("credentials-grid");
        assertEquals(2, GridKt._size(credentials));
        assertTrue(GridKt._getFormattedRow(credentials, 0).contains("Contrasena"));
        List<String> otp = GridKt._getFormattedRow(credentials, 1);
        assertTrue(otp.contains("Segundo factor (OTP)") && otp.contains("Movil"), otp.toString());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(credentials, 0, "actions"), Button.class, spec -> spec.withId("remove-credential-c1")));
        ConfirmDialog confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message").contains("no podra entrar"), "quitar la contrasena avisa de lo que supone");
        verify(usersClient, never()).deleteCredential(any(), any());
        ConfirmDialogKt._fireConfirm(confirm);

        verify(usersClient).deleteCredential(ANA_ID, "c1");
        verify(usersClient, times(2)).credentials(ANA_ID);
        NotificationsKt.expectNotifications("Credencial quitada: Contrasena");
    }

    @Test
    void aReaderSeesTheCredentialsWithoutTheTrash() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(3);

        Grid<Object> credentials = gridWithId("credentials-grid");
        assertEquals(2, GridKt._size(credentials));
        assertNull(credentials.getColumnByKey("actions"));
    }

    @Test
    void theTakeOutButtonNeedsWriteAndSessionsTogether() {
        loginAs("usuarios.mixto", "ROLE_USERS_READ", "ROLE_USERS_WRITE", "ROLE_USERS_DELETE", "ROLE_USERS_PASSWORD_RESET");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);

        detailButton("user-edit");
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("user-take-out")).isEmpty(), "sin users-sessions-write no hay tercer paso");
    }

    @Test
    void takingSomebodyOutMakesTheThreeCallsInOrder() {
        loginAs("usuarios.responsable", "ROLE_USERS_READ", "ROLE_USERS_WRITE", "ROLE_USERS_SESSIONS_WRITE");
        UserDto disabled = user(ANA_ID, "ana", "Ana", "Alvarez", "ana@mto.local", false, Map.of());
        stubUserDetail(ana());
        when(usersClient.get(ANA_ID)).thenReturn(ana(), disabled);
        when(usersClient.setEnabled(ANA_ID, new UserEnabledRequest(false))).thenReturn(disabled);

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(2);
        LocatorJ._click(detailButton("user-take-out"));
        verify(usersClient, never()).setEnabled(any(), any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));

        InOrder order = inOrder(usersClient);
        order.verify(usersClient).setEnabled(ANA_ID, new UserEnabledRequest(false));
        order.verify(usersClient).revokeAllSessions(ANA_ID);
        order.verify(usersClient).revokeAllOfflineSessions(ANA_ID);
        LocatorJ._get(Span.class, spec -> spec.withText("Desactivado"));
        verify(usersClient, times(2)).sessions(ANA_ID);
        NotificationsKt.expectNotifications("ana fuera: desactivado, sesiones cerradas y sesiones offline revocadas");
    }

    @Test
    void takingSomebodyOutStopsAtTheFirstFailureAndSaysWhichStepFailed() {
        loginAs("usuarios.responsable", "ROLE_USERS_READ", "ROLE_USERS_WRITE", "ROLE_USERS_SESSIONS_WRITE");
        UserDto disabled = user(ANA_ID, "ana", "Ana", "Alvarez", "ana@mto.local", false, Map.of());
        stubUserDetail(ana());
        when(usersClient.setEnabled(ANA_ID, new UserEnabledRequest(false))).thenReturn(disabled);
        ApiProblem problem = new ApiProblem("about:blank", "Service Unavailable", 503, "Keycloak no responde", null,
                "KC-503", null, null, null, true, null, null);
        doThrow(BackofficeApiException.of(HttpStatus.SERVICE_UNAVAILABLE, problem, "corr-u8", null, "DELETE /api/users/" + ANA_ID + "/sessions"))
                .when(usersClient).revokeAllSessions(ANA_ID);

        TakeOut.Result result = TakeOut.run(usersClient, ANA_ID);
        assertFalse(result.isComplete());
        assertEquals(List.of(TakeOut.Step.DISABLE), result.done());
        assertEquals(TakeOut.Step.SESSIONS, result.failed());
        verify(usersClient, never()).revokeAllOfflineSessions(any());
        clearInvocations(usersClient);

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-take-out"));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));

        verify(usersClient).setEnabled(ANA_ID, new UserEnabledRequest(false));
        verify(usersClient).revokeAllSessions(ANA_ID);
        verify(usersClient, never()).revokeAllOfflineSessions(any());
        NotificationsKt.expectNotifications("No se ha podido sacar a ana: fallo al cerrar las sesiones (hecho: desactivar). "
                + "El servicio no esta disponible ahora mismo. Intentalo mas tarde.");
    }

    // --- Catalogos de perfiles y roles de cliente ---------------------------------------------------

    @Test
    void aStaticUsersRouteWinsOverTheUserIdParameter() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        when(usersClient.profiles()).thenReturn(List.of(VIEWER));
        when(usersClient.clients()).thenReturn(List.of(USERS_API));

        UI.getCurrent().navigate(UserProfilesView.ROUTE);
        LocatorJ._get(UserProfilesView.class);
        UI.getCurrent().navigate(ClientRolesView.ROUTE);
        LocatorJ._get(ClientRolesView.class);

        assertTrue(LocatorJ._find(UserDetailView.class).isEmpty());
        verify(usersClient, never()).get(any());
    }

    @Test
    void theProfilesCatalogueShowsWhatAProfileGrantsAndPagesItsMembersWithoutATotal() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        List<UserDto> admins = new ArrayList<>();
        for (int i = 0; i < 53; i++) {
            admins.add(user(UUID.randomUUID().toString(), String.format("admin%02d", i), "Admin", String.valueOf(i), null, true, Map.of()));
        }
        when(usersClient.profiles()).thenReturn(List.of(VIEWER, MANAGER, ADMIN));
        when(usersClient.profile("mto-users-admin")).thenReturn(new RealmProfileDto("mto-users-admin", "Todo sobre usuarios",
                List.of(new ClientRoleAssignmentDto("mto-users-api", List.of("users-read", "users-write", "users-delete"))), List.of("default-roles-mto")));
        when(usersClient.profileMembers(eq("mto-users-admin"), anyInt(), anyInt())).thenAnswer(call -> {
            int first = call.getArgument(1);
            int max = call.getArgument(2);
            return admins.subList(Math.min(first, admins.size()), Math.min(first + max, admins.size()));
        });

        UI.getCurrent().navigate(UserProfilesView.ROUTE);
        Grid<Object> catalogue = gridWithId("profiles-catalogue");
        assertEquals(3, GridKt._size(catalogue));
        LocatorJ._get(Span.class, spec -> spec.withText("3 perfiles"));
        verify(usersClient, never()).profile(any());

        catalogue.select(GridKt._get(catalogue, 2));

        verify(usersClient).profile("mto-users-admin");
        Grid<Object> grants = gridWithId("grants-grid");
        assertEquals(3, GridKt._size(grants));
        List<String> grant = GridKt._getFormattedRow(grants, 0);
        assertTrue(grant.contains("mto-users-api") && grant.contains("users-read"), grant.toString());
        LocatorJ._get(Span.class, spec -> spec.withText("Roles de realm: default-roles-mto"));
        Grid<Object> members = gridWithId("members-grid");
        assertEquals(50, GridKt._size(members));
        verify(usersClient).profileMembers("mto-users-admin", 0, 50);
        Button next = detailButton("members-next");
        Button previous = detailButton("members-previous");
        assertTrue(next.isEnabled(), "una pagina llena es la unica senal de que hay mas");
        assertFalse(previous.isEnabled());
        LocatorJ._get(Span.class, spec -> spec.withText("Pagina 1"));

        LocatorJ._click(next);
        verify(usersClient).profileMembers("mto-users-admin", 50, 50);
        assertEquals(3, GridKt._size(members));
        assertEquals("admin50", ((UserDto) GridKt._get(members, 0)).username());
        assertFalse(next.isEnabled(), "una pagina corta es la ultima");
        assertTrue(previous.isEnabled());
        LocatorJ._get(Span.class, spec -> spec.withText("Pagina 2"));

        LocatorJ._click(previous);
        verify(usersClient, times(2)).profileMembers("mto-users-admin", 0, 50);
        assertEquals(50, GridKt._size(members));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("profile-filter")), "gestion");
        assertEquals(1, GridKt._size(catalogue), "el filtro es local: el servicio devuelve el catalogo entero");
        assertEquals("mto-users-manager", ((RealmProfileSummaryDto) GridKt._get(catalogue, 0)).name());
        LocatorJ._get(Span.class, spec -> spec.withText("1 de 3 perfiles"));
        assertFalse(members.getParent().orElseThrow().isVisible(), "al filtrar se deselecciona y el detalle se esconde");
        verify(usersClient, times(1)).profiles();
    }

    @Test
    void theClientRolesCatalogueListsRolesPerClientAndWhoHoldsThem() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        when(usersClient.clients()).thenReturn(List.of(USERS_API, CONFIGURATION_API));
        when(usersClient.clientRoles("mto-users-api")).thenReturn(List.of(
                new ClientRoleDto("users-read", "Leer usuarios", false), new ClientRoleDto("users-write", "Escribir usuarios", false),
                new ClientRoleDto("users-delete", null, false)));
        when(usersClient.clientRoleMembers("mto-users-api", "users-read", 0, 50)).thenReturn(List.of(ana(), threeUsers().get(1)));

        UI.getCurrent().navigate(ClientRolesView.ROUTE);
        @SuppressWarnings("unchecked")
        ComboBox<ClientDto> picker = LocatorJ._get(ComboBox.class, spec -> spec.withId("roles-client"));
        Grid<Object> catalogue = gridWithId("roles-catalogue");
        assertEquals(List.of(USERS_API, CONFIGURATION_API), picker.getListDataView().getItems().toList());
        assertEquals(0, GridKt._size(catalogue));

        LocatorJ._setValue(picker, USERS_API);
        verify(usersClient).clientRoles("mto-users-api");
        assertEquals(3, GridKt._size(catalogue));
        LocatorJ._get(Span.class, spec -> spec.withText("3 roles"));
        List<String> row = GridKt._getFormattedRow(catalogue, 0);
        assertTrue(row.contains("users-read") && row.contains("Leer usuarios") && row.contains("No"), row.toString());

        TextField filter = LocatorJ._get(TextField.class, spec -> spec.withId("roles-filter"));
        LocatorJ._setValue(filter, "write");
        assertEquals(1, GridKt._size(catalogue));
        LocatorJ._get(Span.class, spec -> spec.withText("1 de 3 roles"));
        LocatorJ._setValue(filter, "");
        assertEquals(3, GridKt._size(catalogue));

        catalogue.select(GridKt._get(catalogue, 0));
        verify(usersClient).clientRoleMembers("mto-users-api", "users-read", 0, 50);
        Grid<Object> members = gridWithId("role-members-grid");
        assertEquals(2, GridKt._size(members));
        assertTrue(GridKt._getFormattedRow(members, 0).contains("ana"));
        LocatorJ._get(H4.class, spec -> spec.withText("Miembros de mto-users-api / users-read"));
        assertFalse(detailButton("role-members-next").isEnabled());
        assertFalse(detailButton("role-members-previous").isEnabled());
        verify(usersClient, times(1)).clients();
    }

    @Test
    void aMemberRowOpensTheUserDetail() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        when(usersClient.clients()).thenReturn(List.of(USERS_API));
        when(usersClient.clientRoles("mto-users-api")).thenReturn(List.of(new ClientRoleDto("users-read", null, false)));
        when(usersClient.clientRoleMembers("mto-users-api", "users-read", 0, 50)).thenReturn(List.of(ana()));
        stubUserDetail(ana());

        UI.getCurrent().navigate(ClientRolesView.ROUTE);
        @SuppressWarnings("unchecked")
        ComboBox<ClientDto> picker = LocatorJ._get(ComboBox.class, spec -> spec.withId("roles-client"));
        LocatorJ._setValue(picker, USERS_API);
        Grid<Object> catalogue = gridWithId("roles-catalogue");
        catalogue.select(GridKt._get(catalogue, 0));
        GridKt._clickItem(gridWithId("role-members-grid"), 0, 1, false, false, false, false);

        LocatorJ._get(UserDetailView.class);
        LocatorJ._get(H2.class, spec -> spec.withText("ana"));
        assertTrue(LocatorJ._find(ClientRolesView.class).isEmpty());
    }

    // --- Almacen (mto-stock): los mensajes de sus errores -------------------------------------------

    private static BackofficeApiException stockError(int status, String code, String message) {
        ApiProblem problem = new ApiProblem(null, HttpStatus.valueOf(status).name(), status, message, null, code, null, null, null, false, null, null);
        return BackofficeApiException.of(HttpStatusCode.valueOf(status), problem, "corr-s9", null, "POST /api/stock/movements/outputs");
    }

    /** Un 422 sin campos es una regla de negocio y un 409 STK-001 es falta de stock: no «peticion no valida» ni «recarga». */
    @Test
    void stockErrorsReadAsStockErrorsInTheNotifications() {
        assertEquals("La operacion no es posible. Only active reservations can be changed",
                UiErrors.message(stockError(422, "RES-001", "Only active reservations can be changed")));
        assertEquals("No hay stock disponible suficiente. Insufficient stock for material m in warehouse w: requested 5, available 2",
                UiErrors.message(stockError(409, "STK-001", "Insufficient stock for material m in warehouse w: requested 5, available 2")));
        assertEquals("Conflicto con otro cambio: recarga y vuelve a intentarlo. Material code 'MAT-001' already exists",
                UiErrors.message(stockError(409, "MAT-409", "Material code 'MAT-001' already exists")));
        assertEquals("La peticion no es valida. Material 'MAT-001' is inactive",
                UiErrors.message(stockError(400, "VAL-001", "Material 'MAT-001' is inactive")));
    }

    // --- Almacen (mto-stock): los catalogos -------------------------------------------------------

    private static final UUID WH1 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000001");
    private static final UUID PRJ_MANUAL = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000002");
    private static final UUID PRJ_SYNCED = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000003");
    private static final UUID MAT1 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000004");

    private static WarehouseDto warehouse(UUID id, String code, String name, boolean active) {
        return new WarehouseDto(id, code, name, active, null);
    }

    private static List<WarehouseDto> manyWarehouses() {
        List<WarehouseDto> all = new ArrayList<>();
        all.add(warehouse(WH1, "WH-000", "Central", true));
        for (int i = 1; i < 120; i++) {
            all.add(warehouse(UUID.randomUUID(), String.format("WH-%03d", i), "Nave " + i, i % 4 != 0));
        }
        return all;
    }

    @SuppressWarnings("unchecked")
    private static Grid<Object> stockGrid() {
        return LocatorJ._get(Grid.class);
    }

    @Test
    void theMenuGroupsTheStockCataloguesUnderAlmacen() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.contains("Almacen"), labels.toString());
        assertFalse(labels.contains("Infraestructura"), labels.toString());
        assertFalse(labels.contains("Usuarios"), labels.toString());
        SideNavItem stock = LocatorJ._get(SideNavItem.class, spec -> spec.withLabel("Almacen"));
        assertEquals(StockRoutes.PREFIX, stock.getPath().replaceFirst("^/", ""), "las existencias son a la vez el nodo del grupo");
        assertEquals(List.of("Materiales", "Almacenes", "Proveedores", "Proyectos", "Movimientos", "Reservas", "Conjuntos"),
                stock.getItems().stream().map(SideNavItem::getLabel).toList(), "las pantallas cuelgan del grupo, en su orden");
    }

    @Test
    void theStockViewsAreNotReachableWithARealmRoleOnly() {
        loginAs("almacen.impostor", "ROLE_REALM_STOCK_READ", "ROLE_REALM_MTO_WAREHOUSE_ADMIN");

        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(StockRoutes.WAREHOUSES));

        assertTrue(LocatorJ._find(WarehousesView.class).isEmpty());
        verify(warehouseClient, never()).search(any(), any(), anyInt(), anyInt(), anyList());
    }

    @Test
    void aStockCatalogueIsPagedSearchedSortedAndFilteredInTheServer() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubCatalogue(warehouseClient, manyWarehouses(), WarehouseDto::code, WarehouseDto::name, WarehouseDto::active);

        UI.getCurrent().navigate(StockRoutes.WAREHOUSES);
        Grid<Object> grid = stockGrid();

        assertEquals(120, GridKt._size(grid));
        LocatorJ._get(Span.class, spec -> spec.withText("120 almacenes"));
        assertEquals("WH-000", ((WarehouseDto) GridKt._get(grid, 0)).code());
        assertEquals("WH-077", ((WarehouseDto) GridKt._get(grid, 77)).code(), "la segunda pagina se pide con su page");
        verify(warehouseClient, atLeastOnce()).search(isNull(), isNull(), intThat(page -> page > 0), anyInt(), eq(List.of("code,asc")));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("stock-search")), "nave 1");
        assertEquals(31, GridKt._size(grid), "Nave 1, Nave 10..19 y Nave 100..119");
        verify(warehouseClient, atLeastOnce()).search(eq("nave 1"), isNull(), eq(0), anyInt(), anyList());

        @SuppressWarnings("unchecked")
        Select<EnabledFilter> state = LocatorJ._get(Select.class, spec -> spec.withLabel("Estado"));
        LocatorJ._setValue(state, EnabledFilter.DISABLED);
        assertEquals(7, GridKt._size(grid), "de esas 31, las retiradas (i multiplo de 4): 12, 16, 100, 104, 108, 112 y 116");
        verify(warehouseClient, atLeastOnce()).search(eq("nave 1"), eq(false), eq(0), anyInt(), anyList());
        LocatorJ._get(Span.class, spec -> spec.withText("7 almacenes"));

        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("name"), SortDirection.DESCENDING)));
        GridKt._get(grid, 0);
        verify(warehouseClient, atLeastOnce()).search(any(), any(), anyInt(), anyInt(), eq(List.of("name,desc")));
    }

    @Test
    void aReadOnlyPersonSeesTheStockCatalogueWithoutAnyWriteControl() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubCatalogue(materialClient, List.of(new MaterialDto(MAT1, "MAT-001", "Hilo de contacto", "m", new BigDecimal("100.000000"), true, null)),
                MaterialDto::code, MaterialDto::name, MaterialDto::active);

        UI.getCurrent().navigate(StockRoutes.MATERIALS);

        Grid<Object> grid = stockGrid();
        assertEquals(1, GridKt._size(grid));
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertTrue(row.contains("MAT-001") && row.contains("m") && row.contains("100"), row.toString());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("stock-create")).isEmpty());
        assertEquals(List.of("history-" + MAT1), actionIds(grid, 0), "solo el historial, que es lectura");
    }

    @Test
    void creatingAWarehousePostsCodeAndNameAndEditingSendsTheActiveFlag() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        stubCatalogue(warehouseClient, List.of(warehouse(WH1, "WH-000", "Central", true)), WarehouseDto::code, WarehouseDto::name, WarehouseDto::active);
        when(warehouseClient.create(any())).thenAnswer(call -> warehouse(UUID.randomUUID(), ((CatalogueRequest) call.getArgument(0)).code(), "Nave 9", true));
        when(warehouseClient.update(eq(WH1), any())).thenReturn(warehouse(WH1, "WH-000", "Central", false));

        UI.getCurrent().navigate(StockRoutes.WAREHOUSES);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("stock-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        assertTrue(LocatorJ._find(dialog, Checkbox.class).isEmpty(), "el alta nace activa: el estado solo se toca al modificar");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("catalogue-save")));
        TextField code = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Codigo"));
        assertTrue(code.isInvalid());
        verify(warehouseClient, never()).create(any());
        LocatorJ._setValue(code, "WH-009");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), " Nave 9 ");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("catalogue-save")));

        verify(warehouseClient).create(new CatalogueRequest("WH-009", "Nave 9"));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Guardado WH-009");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(stockGrid(), 0, "actions"), Button.class, spec -> spec.withId("edit-" + WH1)));
        Dialog editor = LocatorJ._get(Dialog.class);
        Checkbox active = LocatorJ._get(editor, Checkbox.class);
        assertTrue(active.getValue());
        LocatorJ._setValue(active, false);
        LocatorJ._click(LocatorJ._get(editor, Button.class, spec -> spec.withId("catalogue-save")));

        verify(warehouseClient).update(WH1, new CatalogueUpdateRequest("WH-000", "Central", false));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
    }

    @Test
    void serverValidationErrorsLandOnTheStockFieldsAndTheRestIsNotified() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        ApiProblem problem = new ApiProblem(null, "BAD_REQUEST", 400, "Request validation failed.", null, "REQ-VALIDATION", null, "corr-s2",
                null, false, List.of(new ApiFieldError("code", null, "size must be between 1 and 64"),
                        new ApiFieldError("supplierRequest", null, "something about the whole body")), null);
        when(supplierClient.create(any())).thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-s2", null, "POST /api/stock/suppliers"));

        UI.getCurrent().navigate(StockRoutes.SUPPLIERS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("stock-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        TextField code = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Codigo"));
        LocatorJ._setValue(code, "SUP-1");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), "Rail");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("catalogue-save")));

        assertTrue(code.isInvalid());
        assertEquals("size must be between 1 and 64", code.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");
        assertEquals(1, NotificationsKt.getNotifications().size(), "el error de todo el cuerpo no tiene campo: se notifica");
    }

    @Test
    void aSynchronizedProjectShowsItsOriginAndHasNoEditButton() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        stubCatalogue(projectClient, List.of(
                        new ProjectDto(PRJ_MANUAL, "PRJ-001", "Renovacion", true, null, false, null),
                        new ProjectDto(PRJ_SYNCED, "EP-42", "Tramo Sants-Sagrera", true, "mto-configuration", true, null)),
                ProjectDto::code, ProjectDto::name, ProjectDto::active);

        UI.getCurrent().navigate(StockRoutes.PROJECTS);
        Grid<Object> grid = stockGrid();

        assertTrue(GridKt._getFormattedRow(grid, 0).contains("manual"), GridKt._getFormattedRow(grid, 0).toString());
        assertTrue(GridKt._getFormattedRow(grid, 1).contains("sincronizado de mto-configuration"), GridKt._getFormattedRow(grid, 1).toString());
        LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-" + PRJ_MANUAL));
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 1, "actions"), Button.class, spec -> spec.withId("edit-" + PRJ_SYNCED)).isEmpty(),
                "lo sincronizado se edita en su origen: el servicio lo rechazaria con PRJ-001");
    }

    @Test
    void theMaterialEditorSendsUnitAndMinimumStockAndRejectsANegativeMinimum() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        when(materialClient.create(any())).thenAnswer(call -> new MaterialDto(UUID.randomUUID(), ((MaterialRequest) call.getArgument(0)).code(), "Hilo", "m",
                new BigDecimal("100"), true, null));

        UI.getCurrent().navigate(StockRoutes.MATERIALS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("stock-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Codigo")), "MAT-009");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), "Hilo");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Unidad de medida")), "m");
        BigDecimalField minimum = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withLabel("Stock minimo"));
        LocatorJ._setValue(minimum, new BigDecimal("-1"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("material-save")));
        assertTrue(minimum.isInvalid());
        verify(materialClient, never()).create(any());

        LocatorJ._setValue(minimum, new BigDecimal("100"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("material-save")));

        verify(materialClient).create(new MaterialRequest("MAT-009", "Hilo", "m", new BigDecimal("100")));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Guardado MAT-009");
    }

    // --- Almacen (mto-stock): existencias y movimientos ---------------------------------------------

    private static final UUID WH2 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000005");
    private static final UUID SUP1 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000006");
    private static final MaterialSummaryDto HILO = new MaterialSummaryDto(MAT1, "MAT-001", "Hilo de contacto", "m", true);
    private static final WarehouseSummaryDto CENTRAL = new WarehouseSummaryDto(WH1, "WH-000", "Central", true);
    private static final WarehouseSummaryDto NAVE2 = new WarehouseSummaryDto(WH2, "WH-002", "Nave 2", true);

    private static MovementDto movement(MovementType type, String quantity) {
        BigDecimal amount = new BigDecimal(quantity);
        return new MovementDto(UUID.randomUUID(), HILO, CENTRAL, type, amount.abs(), amount, Instant.parse("2026-09-10T10:00:00Z"),
                null, new ProjectSummaryDto(PRJ_SYNCED, "EP-42", "Tramo", true), null, null, "OT-7", null, null);
    }

    private static MaterialStockDto stockOf(BigDecimal available, boolean low) {
        return new MaterialStockDto(HILO, CENTRAL, new BigDecimal("12.500000"), new BigDecimal("2.000000"), available,
                new BigDecimal("100.000000"), low, Instant.parse("2026-09-21T10:00:00Z"));
    }

    @SuppressWarnings("unchecked")
    private static <T> ComboBox<T> combo(String id) {
        return LocatorJ._get(ComboBox.class, spec -> spec.withId(id));
    }

    @Test
    void theStockViewShowsTheFiguresOfAMaterialInAWarehouseAndItsLedger() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        when(materialClient.stock(MAT1, WH1)).thenReturn(stockOf(new BigDecimal("10.500000"), true));
        List<MovementDto> ledgerRows = List.of(movement(MovementType.OUTPUT, "-3"), movement(MovementType.ENTRY, "10"));
        when(materialClient.movements(eq(MAT1), eq(WH1), isNull(), isNull(), isNull(), anyInt(), anyInt(), anyList()))
                .thenAnswer(call -> page(ledgerRows, call.getArgument(5), call.getArgument(6)));

        UI.getCurrent().navigate(StockRoutes.PREFIX);
        assertTrue(LocatorJ._find(Div.class, spec -> spec.withId("stock-card")).isEmpty(), "sin material no hay cifras");
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("operation-entry")).isEmpty(), "sin stock-write no se opera");

        LocatorJ._setValue(ViewLayerTest.<WarehouseSummaryDto>combo("stock-warehouse"), CENTRAL);
        LocatorJ._setValue(ViewLayerTest.<MaterialSummaryDto>combo("stock-material"), HILO);

        LocatorJ._get(Div.class, spec -> spec.withId("stock-card"));
        LocatorJ._get(Span.class, spec -> spec.withText("12.5"));
        LocatorJ._get(Span.class, spec -> spec.withText("10.5"));
        LocatorJ._get(Span.class, spec -> spec.withText("Bajo minimo"));
        LocatorJ._get(H4.class, spec -> spec.withText("Movimientos de MAT-001 - Hilo de contacto en WH-000"));
        Grid<Object> ledger = gridWithId("stock-ledger");
        assertEquals(2, GridKt._size(ledger));
        List<String> row = GridKt._getFormattedRow(ledger, 0);
        assertTrue(row.contains("Salida") && row.contains("-3") && row.contains("EP-42"), row.toString());
        verify(materialClient).stock(MAT1, WH1);
    }

    @Test
    void theLowStockListFollowsTheChosenWarehouseAndARowSelectsTheMaterial() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        MaterialDto hilo = new MaterialDto(MAT1, "MAT-001", "Hilo de contacto", "m", new BigDecimal("100"), true, null);
        MaterialDto grapa = new MaterialDto(UUID.randomUUID(), "MAT-002", "Grapa", "ud", new BigDecimal("50"), true, null);
        doAnswer(call -> page(List.of(hilo, grapa), call.getArgument(1), call.getArgument(2))).when(materialClient).lowStock(isNull(), anyInt(), anyInt(), anyList());
        doAnswer(call -> page(List.of(hilo), call.getArgument(1), call.getArgument(2))).when(materialClient).lowStock(eq(WH1), anyInt(), anyInt(), anyList());
        when(materialClient.stock(eq(MAT1), any())).thenReturn(stockOf(new BigDecimal("1"), true));

        UI.getCurrent().navigate(StockRoutes.PREFIX);
        Grid<Object> lowStock = gridWithId("low-stock-grid");
        assertEquals(2, GridKt._size(lowStock));
        LocatorJ._get(Span.class, spec -> spec.withText("2 materiales"));

        LocatorJ._setValue(ViewLayerTest.<WarehouseSummaryDto>combo("stock-warehouse"), CENTRAL);
        assertEquals(1, GridKt._size(lowStock));
        verify(materialClient, atLeastOnce()).lowStock(eq(WH1), eq(0), anyInt(), anyList());

        GridKt._clickItem(lowStock, 0, 1, false, false, false, false);
        assertEquals(HILO, ViewLayerTest.<MaterialSummaryDto>combo("stock-material").getValue());
        LocatorJ._get(Div.class, spec -> spec.withId("stock-card"));
        verify(materialClient).stock(MAT1, WH1);
    }

    @Test
    void theMovementsLedgerIsFilteredInTheServer() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        when(movementClient.search(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), anyList()))
                .thenAnswer(call -> page(List.of(movement(MovementType.ENTRY, "10")), call.getArgument(7), call.getArgument(8)));

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);
        Grid<Object> grid = gridWithId("movements-grid");
        assertEquals(1, GridKt._size(grid));
        LocatorJ._get(Span.class, spec -> spec.withText("1 movimientos"));
        assertTrue(GridKt._getFormattedRow(grid, 0).contains("MAT-001 - Hilo de contacto"));
        verify(movementClient, atLeastOnce()).search(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), anyInt(), eq(List.of("occurredAt,desc")));

        LocatorJ._setValue(ViewLayerTest.<MovementType>combo("movements-type"), MovementType.ENTRY);
        LocatorJ._setValue(ViewLayerTest.<WarehouseSummaryDto>combo("movements-warehouse"), CENTRAL);
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("movements-from")), LocalDate.of(2026, 9, 1));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("movements-to")), LocalDate.of(2026, 9, 30));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("movements-user")), "ana");
        GridKt._get(grid, 0);

        verify(movementClient, atLeastOnce()).search(eq(MovementType.ENTRY), eq(WH1), isNull(), isNull(),
                eq(StockFormats.startOfDay(LocalDate.of(2026, 9, 1))), eq(StockFormats.endOfDay(LocalDate.of(2026, 9, 30))), eq("ana"),
                eq(0), anyInt(), anyList());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("operation-output")).isEmpty());
    }

    @Test
    void anEntryPostsMaterialWarehouseSupplierAndQuantity() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        when(materialClient.stock(any(), any())).thenReturn(stockOf(new BigDecimal("10"), false));
        when(movementClient.entry(any())).thenReturn(movement(MovementType.ENTRY, "10"));

        UI.getCurrent().navigate(StockRoutes.PREFIX);
        LocatorJ._setValue(ViewLayerTest.<MaterialSummaryDto>combo("stock-material"), HILO);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-entry")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        assertEquals(HILO, LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-material")).getValue(), "el material elegido viene puesto");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));
        assertTrue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse")).isInvalid(), "el almacen es obligatorio");
        verify(movementClient, never()).entry(any());

        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse")), CENTRAL);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-supplier")), new SupplierSummaryDto(SUP1, "SUP-001", "Rail", true));
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("movement-quantity")), new BigDecimal("10"));
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Referencia externa")), "ALB-1");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));

        verify(movementClient).entry(new EntryRequest(MAT1, WH1, SUP1, new BigDecimal("10"), null, "ALB-1", null));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Entrada registrada: 10 m de MAT-001");
        verify(materialClient, times(2)).stock(MAT1, null);
    }

    @Test
    void anOutputWithoutStockShowsTheStockMessageAndKeepsTheDialogOpen() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        when(movementClient.output(any())).thenThrow(stockError(409, "STK-001",
                "Insufficient stock for material " + MAT1 + " in warehouse " + WH1 + ": requested 5, available 2"));

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-output")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-material")), HILO);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse")), CENTRAL);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-project")), new ProjectSummaryDto(PRJ_SYNCED, "EP-42", "Tramo", true));
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("movement-quantity")), new BigDecimal("5"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));

        verify(movementClient).output(new OutputRequest(MAT1, WH1, PRJ_SYNCED, null, new BigDecimal("5"), null, null, null));
        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText(
                "No hay stock disponible suficiente. Insufficient stock for material " + MAT1 + " in warehouse " + WH1 + ": requested 5, available 2"));
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "se puede corregir la cantidad");
    }

    @Test
    void aTransferNeedsAnotherWarehouseAndPostsTheTwoApuntes() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        when(movementClient.transfer(any())).thenReturn(List.of(movement(MovementType.OUTGOING_TRANSFER, "-3"), movement(MovementType.INCOMING_TRANSFER, "3")));

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-transfer")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-material")), HILO);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse")), CENTRAL);
        ComboBox<WarehouseSummaryDto> target = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-target"));
        LocatorJ._setValue(target, CENTRAL);
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("movement-quantity")), new BigDecimal("3"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));
        assertTrue(target.isInvalid(), "el destino tiene que ser otro almacen");
        verify(movementClient, never()).transfer(any());

        LocatorJ._setValue(target, NAVE2);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));

        verify(movementClient).transfer(new TransferRequest(MAT1, WH1, WH2, new BigDecimal("3"), null, null, null));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Transferencia registrada: 3 m de MAT-001");
    }

    @Test
    void anAdjustmentNeedsTheAdjustPermissionOnTopOfWrite() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);

        LocatorJ._get(Button.class, spec -> spec.withId("operation-entry"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("operation-adjustment")).isEmpty(), "sin stock-adjust no hay ajuste");
    }

    @Test
    void anAdjustmentPostsItsDirectionAndReason() {
        loginAs("almacen.responsable", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE", "ROLE_STOCK_DELETE", "ROLE_STOCK_ADJUST");
        when(movementClient.adjustment(any())).thenReturn(movement(MovementType.NEGATIVE_ADJUSTMENT, "-1"));

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-adjustment")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-material")), HILO);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse")), CENTRAL);
        LocatorJ._setValue(LocatorJ._get(dialog, Select.class, spec -> spec.withId("movement-direction")), AdjustmentDirection.NEGATIVE);
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("movement-quantity")), new BigDecimal("1"));
        LocatorJ._setValue(LocatorJ._get(dialog, TextArea.class, spec -> spec.withLabel("Notas")), "Rotura en obra");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));

        verify(movementClient).adjustment(new AdjustmentRequest(MAT1, WH1, AdjustmentDirection.NEGATIVE, new BigDecimal("1"), null, null, "Rotura en obra"));
        NotificationsKt.expectNotifications("Ajuste registrado: 1 m de MAT-001");
    }

    // --- Almacen (mto-stock): reservas -------------------------------------------------------------

    private static final UUID RES1 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000007");
    private static final ProjectSummaryDto TRAMO = new ProjectSummaryDto(PRJ_MANUAL, "PRJ-001", "Renovacion", true);

    private static ReservationDto reservation(UUID id, ReservationStatus status, String quantity) {
        boolean active = status == ReservationStatus.ACTIVE;
        return new ReservationDto(id, HILO, CENTRAL, TRAMO, new BigDecimal(quantity), status, Instant.parse("2026-09-12T08:00:00Z"),
                active ? null : Instant.parse("2026-09-13T08:00:00Z"), active, new AuditDto(null, null, "almacen.operario", null));
    }

    private void stubReservations(ReservationDto... rows) {
        List<ReservationDto> all = List.of(rows);
        doAnswer(call -> page(all, call.getArgument(4), call.getArgument(5)))
                .when(reservationClient).search(any(), any(), any(), any(), anyInt(), anyInt(), anyList());
    }

    private static Button rowAction(Grid<Object> grid, int row, String id) {
        return LocatorJ._get(GridKt._getCellComponent(grid, row, ReservationsView.ACTIONS_COLUMN), Button.class, spec -> spec.withId(id));
    }

    @Test
    void theReservationsAreFilteredInTheServerAndOnlyTheActiveOnesHaveActions() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        UUID consumed = UUID.randomUUID();
        stubReservations(reservation(RES1, ReservationStatus.ACTIVE, "5"), reservation(consumed, ReservationStatus.CONSUMED, "2"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        Grid<Object> grid = gridWithId("reservations-grid");
        assertEquals(2, GridKt._size(grid));
        LocatorJ._get(Span.class, spec -> spec.withText("2 reservas"));
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertTrue(row.contains("MAT-001 - Hilo de contacto") && row.contains("PRJ-001") && row.contains("Activa"), row.toString());
        verify(reservationClient, atLeastOnce()).search(isNull(), eq(ReservationStatus.ACTIVE), isNull(), isNull(), eq(0), anyInt(), eq(List.of("reservedAt,desc")));

        rowAction(grid, 0, "edit-" + RES1);
        rowAction(grid, 0, "output-" + RES1);
        rowAction(grid, 0, "consume-" + RES1);
        rowAction(grid, 0, "release-" + RES1);
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, ReservationsView.ACTIONS_COLUMN), Button.class, spec -> spec.withId("cancel-" + RES1)).isEmpty(),
                "cancelar pide stock-delete");
        assertEquals(List.of("history-" + consumed), actionIds(grid, 1),
                "una reserva consumida es historia: el servicio rechazaria cualquier cambio con RES-001");

        ViewLayerTest.<ReservationStatus>combo("reservations-status").clear();
        LocatorJ._setValue(ViewLayerTest.<WarehouseSummaryDto>combo("reservations-warehouse"), CENTRAL);
        LocatorJ._setValue(ViewLayerTest.<MaterialSummaryDto>combo("reservations-material"), HILO);
        LocatorJ._setValue(ViewLayerTest.<ProjectSummaryDto>combo("reservations-project"), TRAMO);
        GridKt._get(grid, 0);

        verify(reservationClient, atLeastOnce()).search(eq(WH1), isNull(), eq(PRJ_MANUAL), eq(MAT1), eq(0), anyInt(), anyList());
        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("quantity"), SortDirection.DESCENDING)));
        GridKt._get(grid, 0);
        verify(reservationClient, atLeastOnce()).search(any(), any(), any(), any(), anyInt(), anyInt(), eq(List.of("quantity,desc")));
    }

    @Test
    void aReadOnlyPersonSeesTheReservationsWithoutAnyAction() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubReservations(reservation(RES1, ReservationStatus.ACTIVE, "5"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        Grid<Object> grid = gridWithId("reservations-grid");

        assertEquals(1, GridKt._size(grid));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("reservation-create")).isEmpty());
        assertEquals(List.of("history-" + RES1), actionIds(grid, 0), "sin stock-write ni stock-delete solo queda el historial");
    }

    @Test
    void aReservationIsCreatedWithMaterialWarehouseProjectAndQuantity() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        stubReservations();
        when(reservationClient.create(any())).thenReturn(reservation(RES1, ReservationStatus.ACTIVE, "5"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("reservation-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("reservation-material")), HILO);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("reservation-warehouse")), CENTRAL);
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("reservation-quantity")), new BigDecimal("5"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("reservation-save")));
        assertTrue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("reservation-project")).isInvalid(), "una reserva es siempre para un proyecto");
        verify(reservationClient, never()).create(any());

        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("reservation-project")), TRAMO);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("reservation-save")));

        verify(reservationClient).create(new ReservationRequest(MAT1, WH1, PRJ_MANUAL, new BigDecimal("5"), null));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Reserva registrada: 5 m de MAT-001 para PRJ-001");
        verify(reservationClient, atLeast(2)).search(any(), any(), any(), any(), eq(0), anyInt(), anyList());
    }

    @Test
    void editingAReservationKeepsItsMaterialAndSendsWarehouseProjectAndQuantity() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        stubReservations(reservation(RES1, ReservationStatus.ACTIVE, "5"));
        when(reservationClient.update(eq(RES1), any())).thenReturn(reservation(RES1, ReservationStatus.ACTIVE, "7"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        LocatorJ._click(rowAction(gridWithId("reservations-grid"), 0, "edit-" + RES1));
        Dialog dialog = LocatorJ._get(Dialog.class);
        ComboBox<MaterialSummaryDto> material = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("reservation-material"));
        assertEquals(HILO, material.getValue());
        assertTrue(material.isReadOnly(), "el material de una reserva no cambia: la modificacion no lo lleva");
        assertTrue(LocatorJ._find(dialog, DateTimePicker.class).isEmpty(), "la fecha solo se fija al crear");
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("reservation-warehouse")), NAVE2);
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("reservation-quantity")), new BigDecimal("7"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("reservation-save")));

        verify(reservationClient).update(RES1, new ReservationUpdateRequest(WH2, PRJ_MANUAL, new BigDecimal("7")));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Reserva modificada: 7 m de MAT-001 para PRJ-001");
    }

    @Test
    void releasingCancellingAndConsumingAReservationAreConfirmedAndCancellingNeedsStockDelete() {
        loginAs("almacen.responsable", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE", "ROLE_STOCK_DELETE", "ROLE_STOCK_ADJUST");
        stubReservations(reservation(RES1, ReservationStatus.ACTIVE, "5"));
        when(reservationClient.release(RES1)).thenReturn(reservation(RES1, ReservationStatus.RELEASED, "5"));
        when(reservationClient.cancel(RES1)).thenReturn(reservation(RES1, ReservationStatus.CANCELLED, "5"));
        when(reservationClient.consume(RES1)).thenThrow(stockError(422, "RES-001", "Only active reservations can be changed"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        Grid<Object> grid = gridWithId("reservations-grid");

        LocatorJ._click(rowAction(grid, 0, "release-" + RES1));
        verify(reservationClient, never()).release(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(reservationClient).release(RES1);
        NotificationsKt.expectNotifications("Reserva liberada: 5 de MAT-001");

        LocatorJ._click(rowAction(grid, 0, "cancel-" + RES1));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(reservationClient).cancel(RES1);
        NotificationsKt.expectNotifications("Reserva cancelada: 5 de MAT-001");

        LocatorJ._click(rowAction(grid, 0, "consume-" + RES1));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(reservationClient).consume(RES1);
        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("La operacion no es posible. Only active reservations can be changed"));
        verify(reservationClient, atLeast(4)).search(any(), any(), any(), any(), eq(0), anyInt(), anyList());
    }

    @Test
    void anOutputFromAReservationFixesMaterialWarehouseAndQuantityAndSendsTheReservationId() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        stubReservations(reservation(RES1, ReservationStatus.ACTIVE, "5"));
        when(movementClient.output(any())).thenReturn(movement(MovementType.OUTPUT, "-5"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        LocatorJ._click(rowAction(gridWithId("reservations-grid"), 0, "output-" + RES1));
        Dialog dialog = LocatorJ._get(Dialog.class);
        ComboBox<MaterialSummaryDto> material = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-material"));
        ComboBox<WarehouseSummaryDto> warehouse = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse"));
        BigDecimalField quantity = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("movement-quantity"));
        assertEquals(HILO, material.getValue());
        assertEquals(CENTRAL, warehouse.getValue());
        assertEquals(new BigDecimal("5"), quantity.getValue());
        assertTrue(material.isReadOnly() && warehouse.isReadOnly() && quantity.isReadOnly(), "el servicio exige que coincidan con lo reservado");
        assertEquals(TRAMO, LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-project")).getValue());
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Referencia externa")), "OT-9");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));

        verify(movementClient).output(new OutputRequest(MAT1, WH1, PRJ_MANUAL, RES1, new BigDecimal("5"), null, "OT-9", null));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Salida registrada: 5 m de MAT-001");
        verify(reservationClient, atLeast(2)).search(any(), any(), any(), any(), eq(0), anyInt(), anyList());
    }

    // --- Almacen (mto-stock): conjuntos -----------------------------------------------------------

    private static final UUID ASM1 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000008");
    private static final UUID MAT2 = UUID.fromString("2b2b2b2b-0000-4000-8000-000000000009");
    private static final MaterialSummaryDto GRAPA = new MaterialSummaryDto(MAT2, "MAT-002", "Grapa", "ud", true);

    private static AssemblyDto mensula() {
        return new AssemblyDto(ASM1, "ASM-001", "Mensula", true, List.of(
                new AssemblyComponentDto(UUID.randomUUID(), HILO, new BigDecimal("2")),
                new AssemblyComponentDto(UUID.randomUUID(), GRAPA, new BigDecimal("4"))), null);
    }

    private static AssemblyAvailabilityComponentDto availabilityOf(MaterialSummaryDto material, String required, String available,
                                                                   String producible, boolean limiting) {
        return new AssemblyAvailabilityComponentDto(material, new BigDecimal(required), new BigDecimal(available), BigDecimal.ZERO,
                new BigDecimal(available), new BigDecimal(producible), limiting);
    }

    private static Button assemblyAction(String id) {
        return LocatorJ._get(GridKt._getCellComponent(stockGrid(), 0, StockCatalogueView.ACTIONS_COLUMN), Button.class, spec -> spec.withId(id));
    }

    @Test
    void theAssembliesListShowsTheirLinesAndAReaderCanOnlyAskForAvailability() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubCatalogue(assemblyClient, List.of(mensula()), AssemblyDto::code, AssemblyDto::name, AssemblyDto::active);

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        Grid<Object> grid = stockGrid();

        assertEquals(1, GridKt._size(grid));
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertTrue(row.contains("ASM-001") && row.contains("2"), row.toString());
        LocatorJ._get(Span.class, spec -> spec.withText("1 conjuntos"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("stock-create")).isEmpty());
        assemblyAction("availability-" + ASM1);
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, StockCatalogueView.ACTIONS_COLUMN), Button.class, spec -> spec.withId("edit-" + ASM1)).isEmpty(),
                "sin stock-write no se modifica, pero la disponibilidad es una consulta");
    }

    @Test
    void theAvailabilityOfAnAssemblyIsAskedPerWarehouseAndMarksTheLimitingComponent() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubCatalogue(assemblyClient, List.of(mensula()), AssemblyDto::code, AssemblyDto::name, AssemblyDto::active);
        when(assemblyClient.availability(ASM1, WH1)).thenReturn(new AssemblyAvailabilityDto(mensula().summary(), CENTRAL, new BigDecimal("3.000000"),
                List.of(availabilityOf(HILO, "2", "12.5", "6", false), availabilityOf(GRAPA, "4", "13", "3", true)), Instant.parse("2026-09-21T10:00:00Z")));

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        LocatorJ._click(assemblyAction("availability-" + ASM1));
        Dialog dialog = LocatorJ._get(Dialog.class);
        assertTrue(LocatorJ._find(dialog, Span.class, spec -> spec.withId("availability-quantity")).isEmpty(), "sin almacen no hay calculo: el stock es por almacen");
        verify(assemblyClient, never()).availability(any(), any());

        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("availability-warehouse")), CENTRAL);

        assertEquals("3 conjuntos montables en WH-000", LocatorJ._get(dialog, Span.class, spec -> spec.withId("availability-quantity")).getText());
        Grid<Object> components = gridWithId("availability-grid");
        assertEquals(2, GridKt._size(components));
        List<String> grapa = GridKt._getFormattedRow(components, 1);
        assertTrue(grapa.contains("MAT-002 - Grapa") && grapa.contains("13") && grapa.contains("Limita"), grapa.toString());
        assertFalse(GridKt._getFormattedRow(components, 0).contains("Limita"), "el hilo da para 6");
    }

    @Test
    void anAssemblyIsCreatedWithItsLinesAndAnEmptyListIsRefusedBeforeCalling() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        when(assemblyClient.create(any())).thenReturn(mensula());

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("stock-create")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Codigo")), "ASM-002");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), "Mensula doble");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("assembly-save")));
        LocatorJ._get(dialog, Span.class, spec -> spec.withId("bom-error"));
        verify(assemblyClient, never()).create(any());

        ComboBox<MaterialSummaryDto> material = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("bom-material"));
        BigDecimalField quantity = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("bom-quantity"));
        Button add = LocatorJ._get(dialog, Button.class, spec -> spec.withId("bom-add"));
        LocatorJ._click(add);
        assertTrue(material.isInvalid() && quantity.isInvalid(), "una linea es un material y una cantidad positiva");
        LocatorJ._setValue(material, HILO);
        LocatorJ._setValue(quantity, new BigDecimal("2"));
        LocatorJ._click(add);
        LocatorJ._setValue(material, GRAPA);
        LocatorJ._setValue(quantity, new BigDecimal("4"));
        LocatorJ._click(add);
        LocatorJ._setValue(material, HILO);
        LocatorJ._setValue(quantity, new BigDecimal("3"));
        LocatorJ._click(add);
        Grid<Object> bom = gridWithId("bom-grid");
        assertEquals(2, GridKt._size(bom), "repetir un material sustituye su cantidad");
        assertTrue(GridKt._getFormattedRow(bom, 0).contains("3 m"), GridKt._getFormattedRow(bom, 0).toString());
        assertNull(material.getValue(), "la linea de alta se vacia tras anadir");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("assembly-save")));

        verify(assemblyClient).create(new AssemblyRequest("ASM-002", "Mensula doble", List.of(
                new AssemblyComponentRequest(MAT1, new BigDecimal("3")), new AssemblyComponentRequest(MAT2, new BigDecimal("4")))));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Guardado ASM-002");
    }

    @Test
    void editingAnAssemblySendsTheWholeListWithTheActiveFlag() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        stubCatalogue(assemblyClient, List.of(mensula()), AssemblyDto::code, AssemblyDto::name, AssemblyDto::active);
        when(assemblyClient.update(eq(ASM1), any())).thenReturn(mensula());

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        LocatorJ._click(assemblyAction("edit-" + ASM1));
        Dialog dialog = LocatorJ._get(Dialog.class);
        Grid<Object> bom = gridWithId("bom-grid");
        assertEquals(2, GridKt._size(bom), "las lineas leidas vienen puestas");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(bom, 1, "actions"), Button.class, spec -> spec.withId("bom-remove-" + MAT2)));
        assertEquals(1, GridKt._size(bom));
        LocatorJ._setValue(LocatorJ._get(dialog, Checkbox.class, spec -> spec.withLabel("Activo")), false);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("assembly-save")));

        verify(assemblyClient).update(ASM1, new AssemblyUpdateRequest("ASM-001", "Mensula", false,
                List.of(new AssemblyComponentRequest(MAT1, new BigDecimal("2")))));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        NotificationsKt.expectNotifications("Guardado ASM-001");
    }

    // --- Almacen (mto-stock): historial ------------------------------------------------------------

    private static <T> RevisionDto<T> revision(long number, RevisionOperation operation, String author, String source, T entity) {
        return new RevisionDto<>(new RevisionMetadataDto(number, Instant.parse("2026-09-01T10:00:00Z").plusSeconds(number * 86_400L), operation,
                author, source, "corr-" + number), entity);
    }

    private static List<String> actionIds(Grid<Object> grid, int row) {
        return LocatorJ._find(GridKt._getCellComponent(grid, row, StockCatalogueView.ACTIONS_COLUMN), Button.class).stream()
                .map(button -> button.getId().orElse("")).toList();
    }

    @Test
    void theHistoryOfACatalogueRowIsPagedNewestFirstAndDescribesHowItWas() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubCatalogue(assemblyClient, List.of(mensula()), AssemblyDto::code, AssemblyDto::name, AssemblyDto::active);
        AssemblyDto before = new AssemblyDto(ASM1, "ASM-001", "Mensula", true,
                List.of(new AssemblyComponentDto(UUID.randomUUID(), HILO, new BigDecimal("2"))), null);
        List<RevisionDto<AssemblyDto>> history = List.of(
                revision(2, RevisionOperation.UPDATED, "almacen.operario", "HTTP", mensula()),
                revision(1, RevisionOperation.CREATED, null, "BASELINE", before));
        when(assemblyClient.revisions(eq(ASM1), anyInt(), anyInt())).thenAnswer(call -> page(history, call.getArgument(1), call.getArgument(2)));

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        LocatorJ._click(assemblyAction("history-" + ASM1));
        Dialog dialog = LocatorJ._get(Dialog.class);
        Grid<Object> grid = gridWithId("revisions-grid");

        assertEquals(2, GridKt._size(grid));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("2 revisiones, la mas reciente primero"));
        List<String> newest = GridKt._getFormattedRow(grid, 0);
        assertTrue(newest.containsAll(List.of("2", "Modificacion", "almacen.operario", "HTTP", "corr-2",
                "ASM-001 - Mensula · 2 lineas (MAT-001 x2, MAT-002 x4) · activo")), newest.toString());
        List<String> first = GridKt._getFormattedRow(grid, 1);
        assertTrue(first.containsAll(List.of("1", "Alta", "BASELINE", "ASM-001 - Mensula · 1 linea (MAT-001 x2) · activo")), first.toString());
        verify(assemblyClient, atLeastOnce()).revisions(eq(ASM1), eq(0), anyInt());
    }

    @Test
    void aRowWithoutHistoryYetSaysSoInsteadOfFailing() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        stubCatalogue(warehouseClient, List.of(warehouse(WH1, "WH-000", "Central", true)), WarehouseDto::code, WarehouseDto::name, WarehouseDto::active);
        when(warehouseClient.revisions(eq(WH1), anyInt(), anyInt())).thenThrow(stockError(404, "WH-404", "No revisions found for warehouse " + WH1));

        UI.getCurrent().navigate(StockRoutes.WAREHOUSES);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(stockGrid(), 0, StockCatalogueView.ACTIONS_COLUMN), Button.class, spec -> spec.withId("history-" + WH1)));
        Dialog dialog = LocatorJ._get(Dialog.class);

        LocatorJ._get(dialog, Span.class, spec -> spec.withId("revisions-empty"));
        assertTrue(LocatorJ._find(dialog, Grid.class).isEmpty(), "sin revisiones no hay tabla: el grid pide su pagina al abrirse y se esconde");
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "un 404 aqui es «sin historial», no un error");
    }

    @Test
    void theHistoryOfAReservationIsReachableFromAnyRowAndDescribesTheReservation() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        UUID consumed = UUID.randomUUID();
        stubReservations(reservation(RES1, ReservationStatus.ACTIVE, "5"), reservation(consumed, ReservationStatus.CONSUMED, "2"));
        List<RevisionDto<ReservationDto>> history = List.of(
                revision(2, RevisionOperation.UPDATED, "almacen.operario", "HTTP", reservation(consumed, ReservationStatus.CONSUMED, "2")),
                revision(1, RevisionOperation.CREATED, "almacen.operario", "HTTP", reservation(consumed, ReservationStatus.ACTIVE, "2")));
        when(reservationClient.revisions(eq(consumed), anyInt(), anyInt())).thenAnswer(call -> page(history, call.getArgument(1), call.getArgument(2)));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        LocatorJ._click(rowAction(gridWithId("reservations-grid"), 1, "history-" + consumed));
        Grid<Object> grid = gridWithId("revisions-grid");

        assertEquals(2, GridKt._size(grid));
        assertTrue(GridKt._getFormattedRow(grid, 0).contains("2 m de MAT-001 en WH-000 para PRJ-001 · Consumida"), GridKt._getFormattedRow(grid, 0).toString());
        assertTrue(GridKt._getFormattedRow(grid, 1).contains("2 m de MAT-001 en WH-000 para PRJ-001 · Activa"), GridKt._getFormattedRow(grid, 1).toString());
        assertTrue(LocatorJ._find(Dialog.class).size() == 1);
    }

    // --- Mantenimiento (mto-maintenance): cimientos --------------------------------------------------

    private static final UUID ORDER1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000001");
    private static final UUID ASSET1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000002");
    private static final UUID TEAM1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000003");

    /** Lo que lee una persona de mantenimiento: sus roles de cliente y los dos de lectura que su perfil del realm le da. */
    private static final String[] MAINTENANCE_READER = {"ROLE_MAINTENANCE_READ", "ROLE_CONFIG_READ", "ROLE_STOCK_READ",
            "ROLE_REALM_MTO_MAINTENANCE_VIEWER"};

    private static OrderDto order(UUID id, String code, MaintenanceOrderStatus status, Long trackId, Long packageId) {
        AssetSummaryDto asset = new AssetSummaryDto(ASSET1, "TS-0001", "Tramo 12", CatenaryAssetType.TRACK_SECTION, trackId,
                new BigDecimal("12.100"), new BigDecimal("13.450"), null, true);
        TeamSummaryDto team = new TeamSummaryDto(TEAM1, "EQ-01", "Brigada norte", "Base Norte");
        return new OrderDto(id, code, "Revision tramo 12", null, MaintenanceOrderType.PREVENTIVE, status, MaintenancePriority.HIGH,
                asset, packageId, trackId, null, new BigDecimal("12.100"), new BigDecimal("13.450"), LocalDate.of(2026, 9, 14), null, null,
                team, "mantenimiento.tecnico", null, null, null, null, null, 10, 3, new BigDecimal("450"), 2, null);
    }

    @Test
    void theMaintenanceGroupOpensOnTheOrdersAndShowsWhatTheProfileReads() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        SideNavItem maintenance = LocatorJ._get(SideNavItem.class, spec -> spec.withLabel("Mantenimiento"));
        assertEquals(MaintenanceRoutes.PREFIX, maintenance.getPath().replaceFirst("^/", ""), "las ordenes son a la vez el nodo del grupo");
        assertTrue(labels.contains("Infraestructura") && labels.contains("Almacen"),
                "el perfil de mantenimiento lee configuracion y almacen, y sus vistas de lectura se abren: " + labels);
        assertFalse(labels.contains("Usuarios"), labels.toString());
    }

    @Test
    void theMaintenanceViewsAreNotReachableWithARealmRoleOnly() {
        loginAs("mantenimiento.impostor", "ROLE_REALM_MAINTENANCE_READ", "ROLE_REALM_MTO_MAINTENANCE_MANAGER");

        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(MaintenanceRoutes.ORDERS));

        assertTrue(LocatorJ._find(OrdersView.class).isEmpty());
        verify(orderClient, never()).search(any(OrderFilter.class), anyInt(), anyInt(), anyList());
    }

    private static BackofficeApiException maintenanceError(int status, String code, String message) {
        ApiProblem problem = new ApiProblem(null, HttpStatus.valueOf(status).name(), status, message, null, code, null, null, null, false, null, null);
        return BackofficeApiException.of(HttpStatusCode.valueOf(status), problem, "corr-m9", null, "POST /api/maintenance/orders");
    }

    /** Los 409 de mantenimiento son de estado, no de concurrencia: ninguno pide recargar. */
    @Test
    void maintenanceErrorsSayWhatBlocksTheOperation() {
        assertEquals("El estado actual no permite esta operacion. Order MO-000001 cannot go from COMPLETED to PLANNED",
                UiErrors.message(maintenanceError(409, "TRN-001", "Order MO-000001 cannot go from COMPLETED to PLANNED")));
        assertEquals("El turno no admite ese trabajo. Shift SH-000001 has partial possession; the task includes work that needs full track possession",
                UiErrors.message(maintenanceError(409, "SHF-001",
                        "Shift SH-000001 has partial possession; the task includes work that needs full track possession")));
        assertEquals("La linea de material no admite esta operacion. Material MAT-001 was already consumed in stock; the line cannot be removed",
                UiErrors.message(maintenanceError(409, "MAT-001", "Material MAT-001 was already consumed in stock; the line cannot be removed")));
        assertEquals("El activo esta desactivado, o ese dato lo manda mto-configuration. Catenary asset P-0001 comes from master data",
                UiErrors.message(maintenanceError(409, "AST-001", "Catenary asset P-0001 comes from master data")));
        assertEquals("Ya existe otro con ese codigo.", UiErrors.message(maintenanceError(409, "AST-409", "Catenary asset code 'TS-1' is already in use")));
        assertEquals("Ya existe otro con ese codigo.", UiErrors.message(maintenanceError(409, "TEA-409", "Maintenance team code 'EQ-01' is already in use")));
        assertEquals("La inspeccion o su checklist no admiten esta operacion. Inspection INS-000001 is OK: there is no defect to record",
                UiErrors.message(maintenanceError(422, "INS-001", "Inspection INS-000001 is OK: there is no defect to record")));
        assertEquals("El almacen no responde: la linea de material se queda como estaba. Intentalo mas tarde. Stock service unavailable",
                UiErrors.message(maintenanceError(503, "STK-503", "Stock service unavailable")));
        assertEquals("El servicio no esta disponible ahora mismo. Intentalo mas tarde.",
                UiErrors.message(maintenanceError(503, null, null)), "el 503 del gateway sigue siendo el de siempre");
    }

    @Test
    void theOrdersListNamesTracksAndPackagesFromConfigurationAndSortsInTheServer() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(executionPackage(3L, "PAQ NORTE")), 0, 1000));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(12L, "VIA 1", true, 3L, List.of())), 0, 1000));
        when(orderClient.search(any(OrderFilter.class), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(order(ORDER1, "MO-000001", MaintenanceOrderStatus.IN_PROGRESS, 12L, 3L)), 0, 50));

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);

        Grid<Object> grid = gridWithId("orders-grid");
        assertEquals(1, GridKt._size(grid));
        assertEquals(List.of("MO-000001", "Revision tramo 12", "Preventiva", "En curso", "Alta", "TS-0001 - Tramo 12", "VIA 1 (PAQ NORTE)",
                "12.1 - 13.45", "PAQ NORTE", "14/09/2026", "EQ-01 - Brigada norte", "3/10", "mantenimiento.tecnico"), GridKt._getFormattedRow(grid, 0));
        LocatorJ._get(Span.class, spec -> spec.withText("1 ordenes"));
        verify(orderClient, atLeastOnce()).search(eq(OrderFilter.NONE), eq(0), anyInt(), eq(List.of("createdAt,desc")));

        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("plannedDate"), SortDirection.ASCENDING)));
        GridKt._get(grid, 0);
        verify(orderClient, atLeastOnce()).search(eq(OrderFilter.NONE), eq(0), anyInt(), eq(List.of("plannedDate,asc")));
        assertFalse(grid.getColumnByKey("tasks").isSortable(), "el avance lo calcula el servicio y no se puede ordenar");
        assertFalse(grid.getColumnByKey("track").isSortable(), "ordenar por el id de la via no seria ordenar por su nombre");
    }

    /** Sin config-read no se llama a mto-configuration (seria un 403 por fila): se ensenan los ids. */
    @Test
    void withoutConfigReadTheOrdersShowTheIdsAndConfigurationIsNotCalled() {
        loginAs("mantenimiento.solo", "ROLE_MAINTENANCE_READ");
        when(orderClient.search(any(OrderFilter.class), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(order(ORDER1, "MO-000001", MaintenanceOrderStatus.PLANNED, 12L, 3L)), 0, 50));

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);

        List<String> row = GridKt._getFormattedRow(gridWithId("orders-grid"), 0);
        assertTrue(row.contains("#12") && row.contains("#3"), row.toString());
        verify(trackClient, never()).filter(anyInt(), anyInt(), anyList(), anyMap());
        verify(executionPackageClient, never()).filter(anyInt(), anyInt(), anyList(), anyMap());
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "ninguna notificacion de 403");
    }

    /** Lo que sirve un enlace de descarga: el cuerpo con el nombre y el tipo del servicio, o su estado si falla. */
    @Test
    void aDownloadServesTheFileWithTheNameAndTypeOfTheServiceOrItsStatus() throws Exception {
        byte[] xlsx = {80, 75, 3, 4};
        DownloadResponse served = Downloads.response(() -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("parte-SH-000001.xlsx").build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsx), "parte.xlsx");
        DownloadResponse unnamed = Downloads.response(() -> ResponseEntity.ok().body(new byte[]{1, 2}), "informe.pdf");
        DownloadResponse failed = Downloads.response(() -> {
            throw maintenanceError(503, null, null);
        }, "informe.pdf");

        assertEquals("parte-SH-000001.xlsx", served.getFileName());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", served.getContentType());
        assertEquals(4, served.getContentLength());
        assertArrayEquals(xlsx, served.getInputStream().readAllBytes());
        assertEquals("informe.pdf", unnamed.getFileName(), "sin Content-Disposition, el nombre de reserva");
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, unnamed.getContentType());
        assertTrue(failed.hasError());
        assertEquals(503, failed.getError());

        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        Anchor link = Downloads.link("report-xlsx", "Excel", "avance.xlsx", () -> ResponseEntity.ok().body(xlsx));
        assertEquals("report-xlsx", link.getId().orElseThrow());
        assertEquals("Excel", link.getText());
    }

    // --- Mantenimiento: activos y catalogos -------------------------------------------------------

    private static final UUID ASSET_SYNCED = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000010");
    private static final UUID ASSET_OWN = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000011");
    private static final UUID ASSET_OWN_OFF = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000012");
    private static final String[] MAINTENANCE_TECHNICIAN = {"ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ", "ROLE_STOCK_READ"};
    private static final String[] MAINTENANCE_MANAGER = {"ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_MAINTENANCE_DELETE",
            "ROLE_MAINTENANCE_SUPERVISE", "ROLE_CONFIG_READ", "ROLE_STOCK_READ"};

    private static AssetDto syncedProfile() {
        return new AssetDto(ASSET_SYNCED, "PRF-0001", "12-2.27", CatenaryAssetType.PROFILE, null, 3L, 12L, 4L, new BigDecimal("12.270"),
                new BigDecimal("12.270"), "501", "S-3", null, null, null, List.of(), "mto-configuration", "501", true, 180, null,
                Instant.parse("2026-10-01T00:00:00Z"), null);
    }

    private static AssetDto ownSection(UUID id, String code, boolean enabled) {
        return new AssetDto(id, code, "Tramo " + code, CatenaryAssetType.TRACK_SECTION, "Tramo propio", 3L, 12L, null,
                new BigDecimal("12.100"), new BigDecimal("13.450"), null, null, TrackKind.MAIN, null, null, List.of(), null, null, enabled,
                null, null, null, null);
    }

    /** Los activos simulados, paginados como el servicio: una fila de mas en una pagina rompe el Grid. */
    private void stubAssets(List<AssetDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(assetClient).search(any(AssetFilter.class), anyInt(), anyInt(), anyList());
    }

    private void stubReferencesForMaintenance() {
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(executionPackage(3L, "PAQ NORTE"), executionPackage(5L, "PAQ SUR")), 0, 1000));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(track(12L, "VIA 1", true, 3L, List.of())), 0, 1000));
    }

    @SuppressWarnings("unchecked")
    private static <T> ComboBox<T> comboWithId(String id) {
        return LocatorJ._get(ComboBox.class, spec -> spec.withId(id));
    }

    @Test
    void theAssetsAreFilteredInTheServerAndAReaderSeesNoWriteControl() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        stubAssets(List.of(syncedProfile()));

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);

        Grid<Object> grid = gridWithId("assets-grid");
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertEquals(List.of("PRF-0001", "12-2.27", "Perfil", "VIA 1 (PAQ NORTE)", "12.27", "PAQ NORTE", "S-3", "180 d",
                Formats.dateTime(Instant.parse("2026-10-01T00:00:00Z")), "Activo", "mto-configuration"), row.subList(0, 11));
        verify(assetClient, atLeastOnce()).search(eq(AssetFilter.NONE), eq(0), anyInt(), eq(List.of("trackId,asc", "startKp,asc")));

        LocatorJ._setValue(comboWithId("assets-type"), CatenaryAssetType.PROFILE);
        LocatorJ._setValue(comboWithId("assets-track"), new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("assets-state")), EnabledFilter.ENABLED);
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("assets-name")), "12-2");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("assets-due")), LocalDate.of(2026, 10, 31));
        GridKt._size(grid);
        verify(assetClient, atLeastOnce()).search(eq(new AssetFilter(CatenaryAssetType.PROFILE, 12L, null, null, true, "12-2",
                Formats.endOfDay(LocalDate.of(2026, 10, 31)))), eq(0), anyInt(), anyList());

        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("asset-create")).isEmpty(), "sin write no hay alta");
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, AssetsView.ACTIONS_COLUMN), Button.class).isEmpty(),
                "quien solo lee no ve ni modificar ni desactivar");
    }

    @Test
    void aTrackSectionIsCreatedWithItsReferencesAndABadRangeOrARepeatedCodeStaysInTheDialog() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(assetClient.create(any())).thenThrow(maintenanceError(409, "AST-409", "Catenary asset code 'TS-0002' is already in use"))
                .thenReturn(ownSection(ASSET_OWN, "TS-0002", true));

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("asset-create")));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("asset-code")), "TS-0002");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("asset-name")), "Tramo 13");
        LocatorJ._setValue(comboWithId("asset-track"), new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("asset-start-kp")), new BigDecimal("13.45"));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("asset-end-kp")), new BigDecimal("13.00"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(AssetEditorDialog.SAVE_ID)));

        verify(assetClient, never()).create(any());
        assertTrue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("asset-end-kp")).isInvalid(), "el KP final va despues del inicial");

        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("asset-end-kp")), new BigDecimal("14.2"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(AssetEditorDialog.SAVE_ID)));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText("Ya existe otro con ese codigo."));
        assertFalse(LocatorJ._find(AssetEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(AssetEditorDialog.SAVE_ID)));
        verify(assetClient, times(2)).create(new AssetRequest("TS-0002", "Tramo 13", null, null, 12L, null, new BigDecimal("13.45"),
                new BigDecimal("14.2"), TrackKind.MAIN, null));
        assertTrue(LocatorJ._find(AssetEditorDialog.class).isEmpty());
    }

    /** Un activo de mto-configuration solo cambia descripcion e intervalo, manda solo lo cambiado y no ofrece desactivarse. */
    @Test
    void aSynchronizedAssetOnlyChangesDescriptionAndIntervalAndCannotBeDisabledHere() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        stubReferencesForMaintenance();
        stubAssets(List.of(syncedProfile()));
        when(assetClient.update(any(), any())).thenReturn(syncedProfile());

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        Component actions = GridKt._getCellComponent(gridWithId("assets-grid"), 0, AssetsView.ACTIONS_COLUMN);
        assertTrue(LocatorJ._find(actions, Button.class, spec -> spec.withId("asset-disable-" + ASSET_SYNCED)).isEmpty(),
                "el enabled de un activo sincronizado lo reescribe el siguiente evento");
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withId("asset-edit-" + ASSET_SYNCED)));

        assertTrue(LocatorJ._find(TextField.class, spec -> spec.withId("asset-name")).isEmpty(), "el nombre es de mto-configuration");
        IntegerField interval = LocatorJ._get(IntegerField.class, spec -> spec.withId("asset-interval"));
        LocatorJ._setValue(interval, null);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(AssetEditorDialog.SAVE_ID)));
        assertTrue(interval.isInvalid(), "un PUT parcial no puede vaciar un numero");

        LocatorJ._setValue(interval, 90);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(AssetEditorDialog.SAVE_ID)));
        verify(assetClient).update(ASSET_SYNCED, new AssetUpdateRequest(null, null, null, 90, null, null, null, null, null, null));
    }

    @Test
    void anOwnTrackSectionIsDisabledWithConfirmationAndReactivatedByUpdatingIt() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        stubReferencesForMaintenance();
        stubAssets(List.of(ownSection(ASSET_OWN, "TS-0001", true), ownSection(ASSET_OWN_OFF, "TS-0009", false)));
        when(assetClient.update(any(), any())).thenReturn(ownSection(ASSET_OWN_OFF, "TS-0009", true));

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        Grid<Object> grid = gridWithId("assets-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-disable-" + ASSET_OWN)));
        verify(assetClient, never()).disable(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(assetClient).disable(ASSET_OWN);
        NotificationsKt.expectNotifications("Desactivado TS-0001 - Tramo TS-0001");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-enable-" + ASSET_OWN_OFF)));
        verify(assetClient).update(ASSET_OWN_OFF, AssetUpdateRequest.enabled(true));
        NotificationsKt.expectNotifications("Reactivado TS-0009 - Tramo TS-0009");
    }

    @Test
    void teamsAreWrittenWholeAndTaskTypesAndTemplatesAreOnlyRead() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        TeamDto team = new TeamDto(TEAM1, "EQ-01", "Brigada norte", "Base Norte", "DR-2", true, Set.of(3L, 5L), null);
        when(maintenanceCatalogClient.teams()).thenReturn(List.of(team));
        when(maintenanceCatalogClient.updateTeam(any(), any())).thenReturn(team);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(List.of(new TaskTypeDto(UUID.randomUUID(), "RG-04",
                "Revision del hilo de contacto", FunctionalGroup.OVERHEAD_CONDUCTORS, new BigDecimal("12.50"), TaskUnit.SPAN, null, true, false,
                true, 4)));
        when(maintenanceCatalogClient.inspectionTemplates()).thenReturn(List.of(
                new InspectionTemplateDto(UUID.randomUUID(), CatenaryAssetType.PROFILE, 1, "Perfil", false, List.of()),
                new InspectionTemplateDto(UUID.randomUUID(), CatenaryAssetType.PROFILE, 2, "Perfil", true, List.of(
                        new InspectionTemplateItemDto(UUID.randomUUID(), "P-01", "Altura del hilo", "mm", new BigDecimal("5300"),
                                new BigDecimal("5700"), true, 1)))));

        UI.getCurrent().navigate(MaintenanceRoutes.TEAMS);
        assertEquals(List.of("EQ-01", "Brigada norte", "Base Norte", "DR-2", "PAQ NORTE, PAQ SUR", "Activo"),
                GridKt._getFormattedRow(gridWithId("teams-grid"), 0).subList(0, 6));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("teams-grid"), 0, TeamsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("team-edit-" + TEAM1)));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("team-base")), "");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamEditorDialog.SAVE_ID)));
        verify(maintenanceCatalogClient).updateTeam(TEAM1, new TeamRequest("EQ-01", "Brigada norte", null, "DR-2", true,
                new java.util.TreeSet<>(Set.of(3L, 5L))));

        UI.getCurrent().navigate(MaintenanceRoutes.TASK_TYPES);
        assertEquals(List.of("RG-04", "Revision del hilo de contacto", "Conductores aereos", "Vano", "12.5", "", "Si", "No", "Si"),
                GridKt._getFormattedRow(gridWithId("task-types-grid"), 0));
        LocatorJ._setValue(comboWithId("task-types-group"), FunctionalGroup.OVERHEAD_CONDUCTORS);
        verify(maintenanceCatalogClient).taskTypes(FunctionalGroup.OVERHEAD_CONDUCTORS, null, null);

        UI.getCurrent().navigate(MaintenanceRoutes.TEMPLATES);
        assertEquals(2, GridKt._size(gridWithId("templates-grid")));
        LocatorJ._get(H3.class, spec -> spec.withText("Puntos de Perfil (version 2)"));
        assertEquals(List.of("P-01", "Altura del hilo", "Si", "mm", "5300", "5700"), GridKt._getFormattedRow(gridWithId("template-items-grid"), 0));
    }
}

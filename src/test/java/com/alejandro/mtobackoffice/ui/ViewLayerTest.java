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
import com.alejandro.mtobackoffice.ui.support.RowActions;
import com.alejandro.mtobackoffice.ui.maintenance.CheckItemsDialog;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.users.TakeOut;
import com.github.mvysny.kaributesting.v10.RouterLinkKt;
import com.vaadin.flow.router.RouterLink;
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
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.vaadin.FrontendProperties;
import com.alejandro.mtobackoffice.client.dto.notification.AccessEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.AccessFilter;
import com.alejandro.mtobackoffice.client.dto.notification.AccessOutcome;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityCategory;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityFilter;
import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.dto.notification.ActorDto;
import com.alejandro.mtobackoffice.client.dto.notification.ActorKind;
import com.alejandro.mtobackoffice.client.dto.notification.InboxFilter;
import com.alejandro.mtobackoffice.client.dto.notification.InboxItemDto;
import com.alejandro.mtobackoffice.client.dto.notification.ReadAllDto;
import com.alejandro.mtobackoffice.client.dto.notification.SubjectDto;
import com.alejandro.mtobackoffice.client.dto.notification.UnreadCountDto;
import com.alejandro.mtobackoffice.ui.notification.AccessView;
import com.alejandro.mtobackoffice.ui.notification.ActivityView;
import com.alejandro.mtobackoffice.ui.notification.EventDetailDialog;
import com.alejandro.mtobackoffice.ui.notification.InboxBell;
import com.alejandro.mtobackoffice.ui.notification.NotificationLinks;
import com.alejandro.mtobackoffice.ui.notification.NotificationRoutes;
import com.alejandro.mtobackoffice.ui.notification.NotificationsView;
import com.alejandro.mtobackoffice.ui.users.UserAttributes;
import com.alejandro.mtobackoffice.ui.support.PageVisibility;
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
import com.vaadin.flow.component.ComponentUtil;
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
import com.vaadin.flow.router.QueryParameters;
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
import com.alejandro.mtobackoffice.client.maintenance.ShiftClient;
import com.alejandro.mtobackoffice.client.maintenance.DefectClient;
import com.alejandro.mtobackoffice.client.maintenance.InspectionClient;
import com.alejandro.mtobackoffice.client.maintenance.ReportClient;
import com.alejandro.mtobackoffice.client.dto.maintenance.MonthlyMaterialLineDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MonthlyReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ProgressReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ProgressRowDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportRowDto;
import com.github.mvysny.kaributesting.v10.DownloadKt;
import java.time.YearMonth;
import java.nio.charset.StandardCharsets;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateCorrectiveOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateDefectFromInspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionKind;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.ResolveDefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.StockSyncStatus;
import com.alejandro.mtobackoffice.ui.maintenance.MaterialUsageDialog;
import com.alejandro.mtobackoffice.ui.maintenance.DefectDetailView;
import com.alejandro.mtobackoffice.ui.maintenance.DefectEditorDialog;
import com.alejandro.mtobackoffice.ui.maintenance.InspectionDetailView;
import com.alejandro.mtobackoffice.ui.maintenance.InspectionEditorDialog;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CloseShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.InlineDefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.PossessionType;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskMaterialRequest;
import com.alejandro.mtobackoffice.ui.maintenance.AssignTasksDialog;
import com.alejandro.mtobackoffice.ui.maintenance.CompleteTaskDialog;
import com.alejandro.mtobackoffice.ui.maintenance.ShiftDetailView;
import com.alejandro.mtobackoffice.ui.maintenance.ShiftEditorDialog;
import com.alejandro.mtobackoffice.ui.maintenance.ShiftTransitionDialog;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceTaskStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StockRequestType;
import com.alejandro.mtobackoffice.client.dto.maintenance.PlanOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StatusHistoryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUpdateRequest;
import com.alejandro.mtobackoffice.ui.maintenance.OrderDetailView;
import com.alejandro.mtobackoffice.ui.maintenance.OrderEditorDialog;
import com.alejandro.mtobackoffice.ui.maintenance.OrderTransitionDialog;
import com.alejandro.mtobackoffice.ui.maintenance.TaskEditorDialog;
import com.alejandro.mtobackoffice.ui.maintenance.GenerateTasksDialog;
import com.github.mvysny.kaributesting.v10.ComboBoxKt;
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
import com.vaadin.flow.component.html.Paragraph;
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
import com.vaadin.flow.component.upload.FileRemovedEvent;
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
import java.util.Optional;
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
import com.vaadin.flow.internal.nodefeature.PropertyChangeDeniedException;
import com.vaadin.flow.internal.nodefeature.ElementPropertyMap;
import com.vaadin.flow.server.SystemMessages;
import com.vaadin.flow.server.VaadinService;
import java.math.BigDecimal;
import java.util.ArrayList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
    @MockitoBean
    private ShiftClient shiftClient;
    @MockitoBean
    private InspectionClient inspectionClient;
    @MockitoBean
    private DefectClient defectClient;
    @MockitoBean
    private ReportClient reportClient;
    @MockitoBean
    private NotificationClient notificationClient;

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

    // --- La SPA: las mismas pantallas, a un clic --------------------------------------------------

    @Test
    void theHeaderOpensTheSameScreenInTheSpaWithItsQueryInAnotherTab() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());

        UI.getCurrent().navigate(HomeView.class);

        Anchor link = LocatorJ._get(Anchor.class, spec -> spec.withText("Abrir en mto-frontend"));
        assertEquals("http://frontend.test/", link.getHref());
        assertEquals(Optional.of("_blank"), link.getTarget());
        assertEquals("noopener noreferrer", link.getElement().getAttribute("rel"));

        // Sigue a la pantalla en la que se esta, con sus parametros: las rutas son las mismas.
        UI.getCurrent().navigate(CATALOGUE_ROUTE, QueryParameters.simple(Map.of("q", "borrador")));

        assertEquals("http://frontend.test/" + CATALOGUE_ROUTE + "?q=borrador",
                LocatorJ._get(Anchor.class, spec -> spec.withText("Abrir en mto-frontend")).getHref());
    }

    @Test
    void theSpaLinkNeedsAnHttpAddressAndKeepsTheRoute() {
        assertEquals(Optional.of("http://spa.example/usuarios/u-1?x=1"),
                new FrontendProperties(" http://spa.example/ ").linkTo("usuarios/u-1?x=1"));
        assertEquals(Optional.of("https://spa.example/"), new FrontendProperties("https://spa.example").linkTo(""));
        assertEquals(Optional.empty(), new FrontendProperties(null).linkTo(""));
        assertEquals(Optional.empty(), new FrontendProperties(" ").linkTo(""));
        assertEquals(Optional.empty(), new FrontendProperties("javascript:alert(1)").linkTo(""));
        assertEquals(Optional.empty(), new FrontendProperties("ftp://spa.example").linkTo(""));
        assertEquals(Optional.empty(), new FrontendProperties("localhost:4200").linkTo(""));
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
        // Por codigo, en orden natural: no en el orden en que los manda el servicio.
        assertEquals(List.of("DEFINITIVE", "DRAFT", "PROVISIONAL"), GridKt._findAll(grid).stream().map(LovDto::code).toList());
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

    /** La fila de una entrada del catalogo, que llega por codigo y no en el orden del servicio. */
    private static int rowOfEntry(long id) {
        return GridKt._findAll(grid()).stream().map(LovDto::id).toList().indexOf(id);
    }

    /** Modifica la descripcion de la entrada 1 (DRAFT), este en la fila que este. */
    private static void editTheFirstRowDescription(String description) {
        Component actions = GridKt._getCellComponent(grid(), rowOfEntry(1L), "actions");
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
        Component actions = GridKt._getCellComponent(grid(), rowOfEntry(1L), "actions");
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

    @Test
    void anOverLongLineIsRejectedWithItsNumberBeforeCalling() {
        IllegalArgumentException code = assertThrows(IllegalArgumentException.class,
                () -> LovBulkCreateDialog.parse("PT1;Poste tipo 1\n" + "C".repeat(41) + ";Demasiado largo"));
        assertTrue(code.getMessage().contains("linea 2"), code.getMessage());
        assertTrue(code.getMessage().contains("codigo"), code.getMessage());
        IllegalArgumentException description = assertThrows(IllegalArgumentException.class,
                () -> LovBulkCreateDialog.parse("PT1;" + "d".repeat(201)));
        assertTrue(description.getMessage().contains("linea 1"), description.getMessage());
        assertEquals(1, LovBulkCreateDialog.parse("C".repeat(40) + ";" + "d".repeat(200)).size());
    }

    @Test
    void theFilterIgnoresAccentsAndTheCatalogueComesInNaturalOrder() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(List.of(
                new LovDto(1L, "PT10", "Poste diez", true, 1, null, null),
                new LovDto(2L, "PT2", "Explotación", true, 1, null, null),
                new LovDto(3L, "pt1", "Poste uno", true, 1, null, null)));

        UI.getCurrent().navigate(CATALOGUE_ROUTE);

        assertEquals(List.of("pt1", "PT2", "PT10"), GridKt._findAll(grid()).stream().map(LovDto::code).toList());
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withPlaceholder("Filtrar por codigo o descripcion")), "EXPLOTACION");
        assertEquals(List.of("PT2"), GridKt._findAll(grid()).stream().map(LovDto::code).toList());
    }

    private static final String FOUNDATIONS_ROUTE = "catalogos/foundations";

    private List<LovDto> foundationTypes() {
        List<LovDto> types = List.of(new LovDto(7L, "FT-B", "Tipo B", true, 1, null, null),
                new LovDto(6L, "FT-A", "Tipo A", true, 1, null, null));
        when(lovClient.findAll("foundation-types")).thenReturn(types);
        return types;
    }

    private static LovDto foundationRead() {
        return new LovDto(5L, "C-1", "Cimentacion 1", true, 2, null, null, Map.of(
                "drawingNumber", "D-12",
                "foundationType", Map.of("id", 3, "code", "FT-OLD", "description", "Tipo retirado")));
    }

    @Test
    void aCatalogueWithATypeShowsItAndEditsKeepingWhatTheScreenDoesNotShow() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll("foundations")).thenReturn(List.of(foundationRead()));
        List<LovDto> types = foundationTypes();
        when(lovClient.update(eq("foundations"), eq(5L), any())).thenAnswer(call -> call.getArgument(2));

        UI.getCurrent().navigate(FOUNDATIONS_ROUTE);

        assertTrue(grid().getColumnByKey("parent").isVisible());
        assertEquals("FT-OLD", GridKt._getFormatted(grid(), 0, "parent"));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid(), 0, "actions"), Button.class, spec -> spec.withId("edit-5")));
        @SuppressWarnings("unchecked")
        ComboBox<LovDto> type = LocatorJ._get(ComboBox.class, spec -> spec.withLabel("Tipo de cimentacion"));
        // El tipo que ya tiene sale aunque no este en la lista, y la lista va por codigo.
        assertEquals(3L, type.getValue().id());
        assertEquals(List.of("FT-OLD", "FT-A", "FT-B"),
                type.getListDataView().getItems().map(LovDto::code).toList());
        type.setValue(types.getFirst());
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Descripcion")), "Cimentacion uno");
        LocatorJ._click(button("Guardar"));

        verify(lovClient).update(eq("foundations"), eq(5L), argThat(sent -> "Cimentacion uno".equals(sent.description())
                && sent.versionNumber() == 2
                && "D-12".equals(sent.extras().get("drawingNumber"))
                && Map.of("id", 7L).equals(sent.extras().get("foundationType"))));
    }

    @Test
    void creatingInACatalogueWithATypeRequiresItAndSendsItById() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_LOV_MANAGE");
        when(lovClient.findAll("foundations")).thenReturn(List.of());
        List<LovDto> types = foundationTypes();
        when(lovClient.create(eq("foundations"), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(FOUNDATIONS_ROUTE);
        LocatorJ._click(button("Nuevo"));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Codigo")), "C-2");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withLabel("Descripcion")), "Cimentacion 2");
        LocatorJ._click(button("Guardar"));

        verify(lovClient, never()).create(any(), any());
        @SuppressWarnings("unchecked")
        ComboBox<LovDto> type = LocatorJ._get(ComboBox.class, spec -> spec.withLabel("Tipo de cimentacion"));
        assertTrue(type.isInvalid(), "sin tipo no se llama: el servicio responderia 400");
        type.setValue(types.get(1));
        LocatorJ._click(button("Guardar"));

        verify(lovClient).create(eq("foundations"), argThat(sent -> "C-2".equals(sent.code()) && sent.id() == null
                && Map.of("id", 6L).equals(sent.extras().get("foundationType"))));
    }

    @Test
    void aBulkCreateInACatalogueWithATypeAsksForItOnceForEveryLine() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT", "ROLE_LOV_MANAGE");
        when(lovClient.findAll("foundations")).thenReturn(List.of());
        List<LovDto> types = foundationTypes();
        when(lovClient.bulkCreate(eq("foundations"), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(FOUNDATIONS_ROUTE);
        LocatorJ._click(button("Alta multiple"));
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withLabel("Entradas")), "C-3;Tres\nC-4;Cuatro");
        LocatorJ._click(button("Crear"));
        verify(lovClient, never()).bulkCreate(any(), any());

        @SuppressWarnings("unchecked")
        ComboBox<LovDto> type = LocatorJ._get(ComboBox.class, spec -> spec.withLabel("Tipo de cimentacion"));
        type.setValue(types.getFirst());
        LocatorJ._click(button("Crear"));

        verify(lovClient).bulkCreate(eq("foundations"), argThat(entries -> entries.size() == 2
                && entries.stream().allMatch(entry -> Map.of("id", 7L).equals(entry.extras().get("foundationType")))));
    }

    @Test
    void aBulkDisableSendsBackWhatEachEntryDoesNotModel() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT", "ROLE_LOV_MANAGE");
        LovDto read = foundationRead();
        when(lovClient.findAll("foundations")).thenReturn(List.of(read));
        when(lovClient.bulkUpdate(eq("foundations"), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(FOUNDATIONS_ROUTE);
        grid().select(read);
        LocatorJ._click(button("Desactivar seleccionados"));

        verify(lovClient).bulkUpdate(eq("foundations"), argThat(entries -> entries.size() == 1
                && !entries.getFirst().isEnabled() && read.extras().equals(entries.getFirst().extras())));
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
        assertEquals(6, audiences.size());
        long present = audiences.stream().filter(item -> "true".equals(item.getElement().getAttribute("data-present"))).count();
        assertEquals(4, present);
        assertTrue(audiences.stream().anyMatch(item -> "mto-notification-api".equals(item.getElement().getAttribute("data-audience"))
                && "false".equals(item.getElement().getAttribute("data-present"))));
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
        when(notificationClient.unreadCount()).thenReturn(new UnreadCountDto(0, false));
        when(notificationClient.inbox(any(InboxFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<InboxItemDto>of(), 0, 20));
        when(notificationClient.activity(any(ActivityFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<ActivityEventDto>of(), 0, 50));
        when(notificationClient.access(any(AccessFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<AccessEventDto>of(), 0, 50));
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
        when(shiftClient.search(any(ShiftFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<ShiftDto>of(), 0, 50));
        when(inspectionClient.search(any(InspectionFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<InspectionDto>of(), 0, 50));
        when(defectClient.search(any(DefectFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<DefectDto>of(), 0, 50));
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

    /**
     * Una pagina es una peticion, con su total, como en mto-frontend: el recuento de Vaadin ya no se
     * pide aparte (ni con el orden elegido), y un fallo se notifica una vez aunque Vaadin vuelva a
     * contar en la misma ida y vuelta.
     */
    @Test
    void aMasterListAsksOncePerPageAndNotifiesAFailureOnce() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        UI.getCurrent().navigate(TRACKS_ROUTE);
        Grid<TrackDto> grid = trackGrid();
        GridKt._size(grid);
        MockVaadin.clientRoundtrip();

        clearInvocations(trackClient);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Recargar")));
        assertEquals(3, GridKt._size(grid));
        assertEquals("VIA 1", GridKt._get(grid, 0).getName());
        assertEquals("VIA MUERTA", GridKt._get(grid, 2).getName());
        MockVaadin.clientRoundtrip();
        verify(trackClient, times(1)).filter(anyInt(), anyInt(), anyList(), anyMap());
        verify(trackClient).filter(eq(0), eq(50), eq(List.of()), anyMap());

        clearInvocations(trackClient);
        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("name"), SortDirection.DESCENDING)));
        assertEquals(3, GridKt._size(grid));
        GridKt._get(grid, 0);
        MockVaadin.clientRoundtrip();
        verify(trackClient, times(1)).filter(anyInt(), anyInt(), anyList(), anyMap());
        verify(trackClient).filter(eq(0), eq(50), eq(List.of("name,desc")), anyMap());

        doThrow(BackofficeApiException.of(HttpStatus.SERVICE_UNAVAILABLE, ApiProblem.empty(), "corr-lp", null,
                "POST /api/configuration/tracks/filter")).when(trackClient).filter(anyInt(), anyInt(), anyList(), anyMap());
        clearInvocations(trackClient);
        NotificationsKt.clearNotifications();
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Recargar")));
        assertEquals(0, GridKt._size(grid));
        MockVaadin.clientRoundtrip();
        assertEquals(0, GridKt._size(grid), "la siguiente vez se vuelve a pedir");
        assertEquals(2, NotificationsKt.getNotifications().size(), "un aviso por vez que se pidio, no por recuento");
        verify(trackClient, times(2)).filter(anyInt(), anyInt(), anyList(), anyMap());
        LocatorJ._get(Span.class, spec -> spec.withText("0 vias"));
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

    /**
     * Las medidas de infraestructura, con la forma que mto-frontend les exige antes de llamar: el vano,
     * las alturas, el viento y los KP de agujas y aisladores no son negativos; lo que va en mm en el
     * perfil y el descentramiento son enteros (solo la distancia carril-poste y el descentramiento
     * llevan signo), y la longitud de un paquete son solo cifras. El KP del perfil viaja recortado.
     */
    @Test
    void theInfrastructureMeasuresAreCheckedLikeInTheSpaBeforeCalling() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null, null)));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        when(profileClient.create(any())).thenAnswer(call -> {
            ProfileDto created = call.getArgument(0);
            created.setId(99L);
            return created;
        });

        UI.getCurrent().navigate(PROFILES_ROUTE);
        LocatorJ._click(button("Nuevo"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Identificador")), "P-9");
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("KP")), " 10.500 ");
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via"));
        LocatorJ._setValue(track, new RefItem(3L, "TRACK 1 (EP4)"));
        @SuppressWarnings("unchecked")
        ComboBox<LovRef> status = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Estado"));
        LocatorJ._setValue(status, new LovRef(5L, "PT1", "Poste tipo 1"));
        BigDecimalField span = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withLabel("Vano hasta el siguiente (m)"));
        BigDecimalField support = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withLabel("Altura del soporte de mensula (mm)"));
        BigDecimalField railPole = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withLabel("Distancia carril-poste (mm, con signo)"));
        LocatorJ._setValue(span, new BigDecimal("-47.970"));
        LocatorJ._setValue(support, new BigDecimal("5300.5"));
        LocatorJ._setValue(railPole, new BigDecimal("-2400"));
        Button save = LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar"));
        LocatorJ._click(save);
        assertEquals("No puede ser negativo", span.getErrorMessage());
        assertEquals("Un numero entero, sin decimales", support.getErrorMessage());
        assertTrue(span.isInvalid() && support.isInvalid());
        assertFalse(railPole.isInvalid(), "la distancia carril-poste lleva signo");
        verify(profileClient, never()).create(any());

        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("cantilevers-add")));
        CantileverDialog cantilever = LocatorJ._get(CantileverDialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<LovRef> type = LocatorJ._get(cantilever, ComboBox.class, spec -> spec.withLabel("Tipo de mensula"));
        LocatorJ._setValue(type, new LovRef(5L, "PT1", "Poste tipo 1"));
        BigDecimalField cwHeight = LocatorJ._get(cantilever, BigDecimalField.class, spec -> spec.withLabel("Altura del hilo de contacto (mm)"));
        BigDecimalField stagger = LocatorJ._get(cantilever, BigDecimalField.class, spec -> spec.withLabel("Descentramiento (mm)"));
        BigDecimalField elevation = LocatorJ._get(cantilever, BigDecimalField.class, spec -> spec.withLabel("Elevacion del hilo (mm)"));
        LocatorJ._setValue(cwHeight, new BigDecimal("-5300"));
        LocatorJ._setValue(stagger, new BigDecimal("200.5"));
        LocatorJ._setValue(elevation, new BigDecimal("-0.050"));
        Button accept = LocatorJ._get(cantilever, Button.class, spec -> spec.withId("cantilever-accept"));
        LocatorJ._click(accept);
        assertTrue(cwHeight.isInvalid() && stagger.isInvalid(), "la altura no es negativa y el descentramiento es entero");
        assertFalse(elevation.isInvalid(), "la elevacion lleva signo y decimales");
        assertFalse(LocatorJ._find(CantileverDialog.class).isEmpty(), "la mensula sigue abierta con lo escrito");
        LocatorJ._setValue(cwHeight, new BigDecimal("5300"));
        LocatorJ._setValue(stagger, new BigDecimal("-200"));
        LocatorJ._click(accept);
        assertTrue(LocatorJ._find(CantileverDialog.class).isEmpty());

        LocatorJ._setValue(span, new BigDecimal("47.970"));
        LocatorJ._setValue(support, new BigDecimal("5300"));
        LocatorJ._click(save);
        verify(profileClient).create(argThat(dto -> "10.500".equals(dto.getKp())
                && new BigDecimal("47.970").equals(dto.getSpan())
                && new BigDecimal("5300").equals(dto.getHeightCantileverSupport())
                && new BigDecimal("-2400").equals(dto.getRailPoleDistance())
                && dto.getCantilevers().size() == 1
                && new BigDecimal("-200").equals(dto.getCantilevers().getFirst().getStagger())));

        UI.getCurrent().navigate(SECTION_INSULATORS_ROUTE);
        LocatorJ._click(button("Nuevo"));
        Dialog insulator = LocatorJ._get(Dialog.class);
        BigDecimalField insulatorKp = LocatorJ._get(insulator, BigDecimalField.class, spec -> spec.withLabel("KP (m)"));
        LocatorJ._setValue(insulatorKp, new BigDecimal("-110176"));
        LocatorJ._click(LocatorJ._get(insulator, Button.class, spec -> spec.withId("switches-add")));
        SwitchDialog aguja = LocatorJ._get(SwitchDialog.class);
        LocatorJ._setValue(LocatorJ._get(aguja, TextField.class, spec -> spec.withLabel("Codigo")), "W41");
        BigDecimalField switchKp = LocatorJ._get(aguja, BigDecimalField.class, spec -> spec.withLabel("KP (m)"));
        IntegerField turnout = LocatorJ._get(aguja, IntegerField.class, spec -> spec.withLabel("Denominador de la tangente (1:n)"));
        LocatorJ._setValue(switchKp, new BigDecimal("-110249"));
        LocatorJ._setValue(turnout, 0);
        LocatorJ._click(LocatorJ._get(aguja, Button.class, spec -> spec.withId("switch-accept")));
        assertTrue(switchKp.isInvalid(), "el KP de una aguja no es negativo");
        assertEquals("Tiene que ser 1 o mas", turnout.getErrorMessage());
        assertFalse(LocatorJ._find(SwitchDialog.class).isEmpty());
        aguja.close();
        LocatorJ._click(LocatorJ._get(insulator, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(insulatorKp.isInvalid(), "el KP de un aislador no es negativo");
        verify(sectionInsulatorClient, never()).create(any());
        insulator.close();

        UI.getCurrent().navigate("infraestructura/paquetes");
        LocatorJ._click(button("Nuevo"));
        Dialog executionPackage = LocatorJ._get(Dialog.class);
        TextField length = LocatorJ._get(executionPackage, TextField.class, spec -> spec.withLabel("Longitud"));
        for (String typed : List.of("1.000", "-5", "+5", "12 m")) {
            LocatorJ._setValue(length, typed);
            LocatorJ._click(LocatorJ._get(executionPackage, Button.class, spec -> spec.withText("Guardar")));
            assertTrue(length.isInvalid(), "\"" + typed + "\" no es una longitud: solo cifras");
        }
        verify(executionPackageClient, never()).create(any());
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

    /**
     * README_API.md §4: en una referencia a catalogo, {@code null} es «no la toques». Vaciar la que tenia
     * valor viaja como {@code {}}, y la que nadie toco vuelve como se leyo, igual que en mto-frontend.
     */
    @Test
    void clearingAnOptionalReferenceOfAProfileSendsAnEmptyReferenceAndAnUntouchedOneTravelsAsRead() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null, null)));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        ProfileDto read = profileWithOneCantilever();
        read.setPoleType(new LovRef(5L, "PT1", "Poste tipo 1"));
        read.setSupportType(new LovRef(5L, "PT1", "Poste tipo 1"));
        when(profileClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(read), 0, 50));
        when(profileClient.update(eq(7L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(PROFILES_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<ProfileDto> profiles = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("edit-7")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<LovRef> poleType = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Tipo de poste"));
        LocatorJ._setValue(poleType, null);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(profileClient).update(eq(7L), argThat(dto -> dto.getPoleType() != null && dto.getPoleType().id() == null
                && dto.getPoleType().code() == null
                && Long.valueOf(5L).equals(dto.getSupportType().id())
                && dto.getFoundation() == null));
    }

    @Test
    void aTrackConnectionInsulatorWithoutItsConnectedTrackNeverReachesTheService() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));

        UI.getCurrent().navigate(SECTION_INSULATORS_ROUTE);
        LocatorJ._click(button("Nuevo"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Nombre")), "AS-1");
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> stationBox = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Estacion"));
        LocatorJ._setValue(stationBox, stationBox.getListDataView().getItems().findFirst().orElseThrow());
        @SuppressWarnings("unchecked")
        ComboBox<SectionInsulatorInstallationType> installation = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Instalacion"));
        LocatorJ._setValue(installation, SectionInsulatorInstallationType.TRACK_CONNECTION);
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> trackBox = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via"));
        LocatorJ._setValue(trackBox, new RefItem(3L, "TRACK 1 (EP4)"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        @SuppressWarnings("unchecked")
        ComboBox<RefItem> connected = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via conectada"));
        assertTrue(connected.isInvalid(), "el servicio rechazaria la conexion sin la otra via");
        verify(sectionInsulatorClient, never()).create(any());
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

    /**
     * El seccionador de un perfil se ensena de solo lectura y vuelve como se leyo: el vinculo es del
     * seccionador, y el servicio ignora el que llega dentro del perfil (mto-configuration#31).
     */
    @Test
    void theDisconnectorOfAProfileIsReadOnlyAndTravelsAsRead() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null, null)));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        ProfileDto profile = profileWithOneCantilever();
        DisconnectorDto hanging = new DisconnectorDto();
        hanging.setId(5L);
        hanging.setName("SEC-1");
        hanging.setProfileId(7L);
        hanging.setDisconnectorFunction(new LovRef(9L, "Disc", "Seccionador"));
        profile.setDisconnector(hanging);
        when(profileClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(profile), 0, 50));
        when(profileClient.update(eq(7L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(PROFILES_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<ProfileDto> profiles = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("edit-7")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        TextField disconnector = LocatorJ._get(dialog, TextField.class, spec -> spec.withId("profile-disconnector"));
        assertTrue(disconnector.isReadOnly(), "el vinculo se cambia desde Seccionadores");
        assertEquals("SEC-1 (Disc)", disconnector.getValue());
        assertEquals("El que cuelga de este perfil. Se vincula desde Seccionadores, en el editor del seccionador.",
                disconnector.getHelperText());
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(profileClient).update(eq(7L), argThat(dto -> dto.getDisconnector() != null
                && Long.valueOf(5L).equals(dto.getDisconnector().getId())
                && Long.valueOf(7L).equals(dto.getDisconnector().getProfileId())));
        verifyNoInteractions(disconnectorClient);
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
        doAnswer(call -> {
            JobType type = call.getArgument(2);
            JobStatus status = call.getArgument(3);
            List<JobDto> matching = history.stream()
                    .filter(job -> type == null || job.type() == type)
                    .filter(job -> status == null || job.status() == status)
                    .toList();
            return page(matching, call.getArgument(0), call.getArgument(1));
        }).when(jobsClient).list(anyInt(), anyInt(), any(), any());
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
        LocatorJ._get(errors, Paragraph.class, spec -> spec.withText(
                "2 elementos fallidos; el servicio solo detalla los primeros 1. El informe descargable los trae todos."));
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

    /**
     * La lista ensena los trabajos de todas las familias, asi que un tipo o un estado que
     * mto-configuration estrene llega antes que esta aplicacion: se pinta «Desconocido» y la lista
     * sigue. Un tipo desconocido no tiene familia: no ofrece descarga y sus errores se ensenan como
     * llegaron en la fila. Un estado desconocido cuenta como terminado y no se vuelve a consultar.
     * Ninguno de los dos se ofrece como filtro.
     */
    @Test
    void aJobOfAnUnknownTypeOrStatusIsListedWithoutWhatItCannotDo() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        UUID newType = UUID.fromString("6f1c0000-0000-4000-8000-000000000002");
        UUID newStatus = UUID.fromString("6f1c0000-0000-4000-8000-000000000003");
        stubJobHistory(List.of(
                new JobDto(newType, JobType.UNKNOWN, JobStatus.COMPLETED, Instant.parse("2026-08-27T09:12:03Z"), null, null, null, null,
                        10, 10, 8, 2, null, null, null),
                new JobDto(newStatus, JobType.PROFILE_EXPORT, JobStatus.UNKNOWN, Instant.parse("2026-08-27T09:10:00Z"), null, null, 3L, "basic",
                        10, 4, 4, 0, null, null, null)));

        UI.getCurrent().navigate(JobsView.ROUTE);
        Grid<JobDto> grid = jobsGrid();
        assertEquals(2, GridKt._size(grid));
        assertEquals("Desconocido", GridKt._getFormattedRow(grid, 0).get(1), "el tipo");
        assertEquals("Desconocido", GridKt._getFormattedRow(grid, 1).get(2), "el estado");
        LocatorJ._get(Span.class, spec -> spec.withText("2 en el servicio, 0 en curso"));

        Component unknownType = GridKt._getCellComponent(grid, 0, "actions");
        assertTrue(LocatorJ._find(unknownType, Anchor.class).isEmpty(), "sin familia no hay a quien pedir el fichero");
        LocatorJ._click(LocatorJ._get(unknownType, Button.class, spec -> spec.withId("errors-" + newType)));
        LocatorJ._get(Dialog.class).close();
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 1, "actions"), Anchor.class).isEmpty(), "una exportacion solo se descarga completa");
        verify(jobsClient, never()).profileJob(any());
        verify(jobsClient, never()).lovJob(any());
        verify(jobsClient, never()).republishJob(any());

        assertFalse(ViewLayerTest.<JobType>combo("jobs-type").getListDataView().getItems().toList().contains(JobType.UNKNOWN));
        assertFalse(ViewLayerTest.<JobStatus>combo("jobs-status").getListDataView().getItems().toList().contains(JobStatus.UNKNOWN));
        clearInvocations(jobsClient);
        LocatorJ._get(JobsView.class).pollOnce();
        verify(jobsClient, never()).list(anyInt(), anyInt(), any(), any());
    }

    /** Solo el fichero de una importacion es el informe de sus errores: un republicado no lo tiene. */
    @Test
    void onlyAnImportSaysItsReportBringsEveryError() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubJobHistory(List.of(new JobDto(JOB_ID, JobType.MASTER_DATA_REPUBLISH, JobStatus.COMPLETED_WITH_ERRORS,
                Instant.parse("2026-08-27T09:12:03Z"), null, null, null, null, 10, 10, 7, 3, null, null, null)));
        when(jobsClient.republishJob(JOB_ID)).thenReturn(new JobDto(JOB_ID, JobType.MASTER_DATA_REPUBLISH, JobStatus.COMPLETED_WITH_ERRORS,
                Instant.parse("2026-08-27T09:12:03Z"), null, null, null, null, 10, 10, 7, 3, null, null,
                List.of(new JobItemError(4, "publish", "AmqpException", "sin confirmacion"))));

        UI.getCurrent().navigate(JobsView.ROUTE);
        Component actions = GridKt._getCellComponent(jobsGrid(), 0, "actions");
        assertTrue(LocatorJ._find(actions, Anchor.class).isEmpty(), "un republicado no produce fichero");
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withText("Errores")));

        Dialog errors = LocatorJ._get(Dialog.class);
        LocatorJ._get(errors, Paragraph.class, spec -> spec.withText("3 elementos fallidos; el servicio solo detalla los primeros 1."));
    }

    /**
     * Un fallo al leer la lista no se notifica en cada pasada, que seria un aviso cada dos segundos:
     * se ensena fijo encima de ella, con su referencia, mientras el ultimo intento falle. La pasada
     * que falla deja las filas como estaban; la que sale bien quita el aviso.
     */
    @Test
    void aFailedReadOfTheListIsShownAboveItWhileTheLastAttemptFails() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        List<JobDto> history = new ArrayList<>(List.of(job(JobType.PROFILE_EXPORT, JobStatus.RUNNING, 10, 4, 4, 0, null)));
        stubJobHistory(history);
        BackofficeApiException unavailable = BackofficeApiException.of(HttpStatus.SERVICE_UNAVAILABLE, ApiProblem.empty(), "corr-9",
                Duration.ofSeconds(30), "GET /api/configuration/jobs");

        UI.getCurrent().navigate(JobsView.ROUTE);
        assertTrue(jobsListNotice().isEmpty());

        JobsView view = LocatorJ._get(JobsView.class);
        doThrow(unavailable).when(jobsClient).list(anyInt(), anyInt(), any(), any());
        view.pollOnce();
        MockVaadin.clientRoundtrip();
        Div notice = jobsListNotice().orElseThrow();
        LocatorJ._get(notice, Span.class, spec -> spec.withText(
                "No se ha podido leer la lista de trabajos: El servicio no esta disponible ahora mismo. Intentalo en 30 s."));
        LocatorJ._get(notice, Span.class, spec -> spec.withText("Referencia: corr-9"));
        assertEquals(1, GridKt._size(jobsGrid()), "la pasada que falla deja las filas como estaban");
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "ni un aviso por pasada");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Recargar")));
        assertTrue(jobsListNotice().isPresent(), "recargar a mano tambien lo dice ahi");
        assertEquals(0, GridKt._size(jobsGrid()));
        assertTrue(NotificationsKt.getNotifications().isEmpty());

        stubJobHistory(history);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Recargar")));
        assertTrue(jobsListNotice().isEmpty());
        assertEquals(1, GridKt._size(jobsGrid()));
        doThrow(unavailable).when(jobsClient).list(anyInt(), anyInt(), any(), any());
        view.pollOnce();
        MockVaadin.clientRoundtrip();
        assertTrue(jobsListNotice().isPresent());
        stubJobHistory(history);
        view.pollOnce();
        MockVaadin.clientRoundtrip();
        assertTrue(jobsListNotice().isEmpty(), "la pasada que sale bien lo quita");
    }

    /** El aviso fijo de la lista de trabajos, si se ve. */
    private static Optional<Div> jobsListNotice() {
        return LocatorJ._find(Div.class, spec -> spec.withId("jobs-list-error")).stream().findFirst();
    }

    /** Con la pestana oculta no se pregunta nada desde el hilo compartido: ni los trabajos ni la campana. */
    @Test
    void nothingIsAskedFromTheSharedThreadWhileTheTabIsHidden() {
        loginAs("config.lector", "ROLE_CONFIG_READ", "ROLE_NOTIFICATION_INBOX");
        stubJobHistory(List.of(job(JobType.PROFILE_EXPORT, JobStatus.RUNNING, 10, 4, 4, 0, null)));

        UI.getCurrent().navigate(JobsView.ROUTE);
        JobsView view = LocatorJ._get(JobsView.class);
        InboxBell bell = LocatorJ._get(InboxBell.class);
        PageVisibility visibility = PageVisibility.of(UI.getCurrent());
        assertTrue(visibility.isShown(), "hasta que el navegador diga otra cosa, se ve");

        visibility.changed(false);
        clearInvocations(jobsClient, notificationClient);
        view.pollOnce();
        bell.pollOnce();
        MockVaadin.clientRoundtrip();
        verify(jobsClient, never()).list(anyInt(), anyInt(), any(), any());
        verify(notificationClient, never()).unreadCount();

        visibility.changed(true);
        view.pollOnce();
        bell.pollOnce();
        MockVaadin.clientRoundtrip();
        verify(jobsClient).list(0, 20, null, null);
        verify(notificationClient).unreadCount();
    }

    /** Quitar el fichero subido es no importar nada: el boton no sigue enviando el de antes. */
    @Test
    void removingTheUploadedFileLeavesNothingToImport() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT");
        stubJobHistory(List.of());

        UI.getCurrent().navigate(JobsView.ROUTE);
        Upload upload = LocatorJ._get(Upload.class, spec -> spec.withId("import-profiles-upload"));
        Button start = LocatorJ._get(Button.class, spec -> spec.withId("import-profiles"));
        UploadKt._upload(upload, "profile-master.xlsx", JobsView.XLSX, "PK-xlsx".getBytes());
        MockVaadin.clientRoundtrip();
        assertTrue(start.isEnabled());

        ComponentUtil.fireEvent(upload, new FileRemovedEvent(upload, "profile-master.xlsx"));
        assertFalse(start.isEnabled());
        verify(jobsClient, never()).importProfiles(any(), anyBoolean());
    }

    /**
     * El navegador solo sabria que la descarga fallo: el enlace lo notifica en la pantalla con el
     * motivo. Un 410 es el fichero de un trabajo que ya no esta en el servicio, y hay que relanzarlo.
     */
    @Test
    void aFailedDownloadIsNotifiedAndAGoneFileAsksToRelaunchTheJob() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubJobHistory(List.of());
        UI.getCurrent().navigate(JobsView.ROUTE);
        Anchor link = Downloads.link("download-" + JOB_ID, "Descargar", "perfiles-via-3.csv", () -> {
            throw BackofficeApiException.of(HttpStatus.GONE, ApiProblem.empty(), "corr-11", null,
                    "GET /api/configuration/profiles/jobs/" + JOB_ID + "/file");
        });
        UI.getCurrent().add(link);

        DownloadKt._download(link);
        MockVaadin.clientRoundtrip();

        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("El fichero ya no esta en el servicio: vuelve a lanzar el trabajo."));
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("Referencia: corr-11"));
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

        // Un tramo es una peticion, con su total: el recuento no se pide aparte (LazyPages).
        clearInvocations(usersClient);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Recargar")));
        assertEquals(3, GridKt._size(grid));
        assertEquals("user018", GridKt._get(grid, 2).username());
        MockVaadin.clientRoundtrip();
        verify(usersClient, times(1)).search(any(), any(), any(), any(), any(), any(), anyInt(), anyInt());
        verify(usersClient).search(eq("user01"), isNull(), isNull(), eq(false), isNull(), isNull(), eq(0), eq(UsersView.PAGE_SIZE));
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
        @SuppressWarnings("unchecked")
        Select<EnabledFilter> state = LocatorJ._get(Select.class, spec -> spec.withLabel("Estado"));
        LocatorJ._setValue(state, state.getListDataView().getItems().filter(filter -> Boolean.TRUE.equals(filter.value())).findFirst().orElseThrow());
        // Con un atributo mal formado no se pide nada nuevo: ni el estado cambiado ni «Recargar» sueltan
        // el ultimo filtro bien formado (el Grid puede volver a pedir, pero solo con el).
        assertEquals(1, GridKt._size(userGrid()), "la lista sigue con lo ultimo que pidio");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Recargar")));
        assertEquals(1, GridKt._size(userGrid()));
        verify(usersClient, never()).search(any(), any(), any(), eq(true), any(), any(), anyInt(), anyInt());
        verify(usersClient, never()).search(any(), any(), any(), any(), any(), argThat(attributes -> attributes == null || !attributes.equals(List.of("dept:taller"))),
                anyInt(), anyInt());
        LocatorJ._setValue(attribute, "dept:noche");
        GridKt._size(userGrid());
        verify(usersClient, atLeastOnce()).search(isNull(), isNull(), isNull(), eq(true), isNull(), eq(List.of("dept:noche")), eq(0), anyInt());
        LocatorJ._setValue(state, state.getListDataView().getItems().filter(filter -> filter.value() == null).findFirst().orElseThrow());

        LocatorJ._setValue(attribute, "");
        assertTrue(search.isEnabled());
        clearInvocations(usersClient);
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

        LocatorJ._setValue(password, " Temporal-2026 ");
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
                && " Temporal-2026 ".equals(request.temporaryPassword())
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
        selectTab(1);
        verify(usersClient, times(1)).userRoles(ANA_ID);
        selectTab(0);
        @SuppressWarnings("unchecked")
        ComboBox<RealmProfileSummaryDto> picker = LocatorJ._get(ComboBox.class, spec -> spec.withId("profile-assign"));
        Button assign = detailButton("profile-assign-button");
        assertEquals(List.of(MANAGER, ADMIN), picker.getListDataView().getItems().toList(), "solo lo que falta por asignar");
        assertFalse(assign.isEnabled());

        LocatorJ._setValue(picker, MANAGER);
        LocatorJ._click(assign);

        verify(usersClient).assignProfile(ANA_ID, "mto-users-manager");
        verify(usersClient, times(2)).userRoles(ANA_ID);
        Grid<Object> profiles = gridWithId("profiles-grid");
        assertEquals(2, GridKt._size(profiles), "se pinta lo que devuelve el servicio; los roles que concede, en su pestana, se releen");
        assertEquals(List.of(ADMIN), picker.getListDataView().getItems().toList());
        NotificationsKt.expectNotifications("Perfil mto-users-manager asignado");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("remove-profile-mto-users-viewer")));

        verify(usersClient).removeProfile(ANA_ID, "mto-users-viewer");
        assertEquals(1, GridKt._size(profiles));
        assertTrue(GridKt._getFormattedRow(profiles, 0).contains("mto-users-manager"));
        verify(usersClient, times(1)).userProfiles(ANA_ID);
        verify(usersClient, times(3)).userRoles(ANA_ID);
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
        assertEquals(List.of("users-read", "users-delete"), rolePicker.getListDataView().getItems().toList(),
                "las opciones se rehacen aqui con lo que devolvio el servicio");
        verify(usersClient, times(1)).clientRoles("mto-users-api");
    }

    /** Cambiar un perfil relee los roles de la persona, no el catalogo, como en mto-frontend. */
    @Test
    void aProfileChangeRereadsThePersonsRolesButNotTheCatalogue() {
        loginAs("usuarios.responsable", "ROLE_USERS_READ", "ROLE_USERS_PROFILES_WRITE", "ROLE_USERS_ROLES_WRITE");
        stubUserDetail(ana());
        when(usersClient.assignProfile(ANA_ID, "mto-users-manager")).thenReturn(List.of(VIEWER, MANAGER));

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(1);
        @SuppressWarnings("unchecked")
        ComboBox<ClientDto> clientPicker = LocatorJ._get(ComboBox.class, spec -> spec.withId("role-client"));
        LocatorJ._setValue(clientPicker, USERS_API);
        selectTab(0);
        @SuppressWarnings("unchecked")
        ComboBox<RealmProfileSummaryDto> picker = LocatorJ._get(ComboBox.class, spec -> spec.withId("profile-assign"));
        LocatorJ._setValue(picker, MANAGER);
        LocatorJ._click(detailButton("profile-assign-button"));

        verify(usersClient, times(2)).userRoles(ANA_ID);
        verify(usersClient, times(1)).clients();
        verify(usersClient, times(1)).clientRoles("mto-users-api");
        assertEquals(USERS_API, clientPicker.getValue(), "el cliente elegido sigue elegido");
    }

    @Test
    void theTemporaryPasswordDialogDefaultsToTemporaryAndNeverSendsAShortOne() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_PASSWORD_RESET");
        stubUserDetail(ana());

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(3);
        verify(usersClient, times(1)).credentials(ANA_ID);
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
        verify(usersClient, times(2)).get(ANA_ID);
        verify(usersClient, times(2)).credentials(ANA_ID);
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

        // Lo que manda mto-users de verdad: el texto de Keycloak en el detalle y ningun error por campo.
        // Aqui solo se escribe la contrasena, asi que el rechazo es suyo.
        ApiProblem policy = new ApiProblem("about:blank", "Bad Request", 400, "Password policy not met", null,
                "KC-400", null, null, null, false, null, null);
        doThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, policy, "corr-u7", null, "POST /api/users/" + ANA_ID + "/reset-password"))
                .when(usersClient).resetPassword(eq(ANA_ID), any());
        NotificationsKt.clearNotifications();
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("reset-password-save")));
        assertTrue(password.isInvalid());
        assertEquals("La peticion no es valida. Password policy not met", password.getErrorMessage());
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "en el campo, no en una notificacion");
        assertFalse(LocatorJ._find(Dialog.class).isEmpty());
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
    void editingFromTheDetailPaintsWhatTheServiceAnswers() {
        loginAs("usuarios.gestor", "ROLE_USERS_READ", "ROLE_USERS_WRITE");
        UserDto renamed = user(ANA_ID, "ana", "Ana", "Alvarez Arias", "ana@mto.local", true, Map.of("dept", List.of("taller")));
        stubUserDetail(ana());
        when(usersClient.update(eq(ANA_ID), any())).thenReturn(renamed);

        UI.getCurrent().navigate(ANA_ROUTE);
        LocatorJ._click(detailButton("user-edit"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("Apellidos")), "Alvarez Arias");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("user-save")));

        verify(usersClient).update(eq(ANA_ID), argThat(request -> "Alvarez Arias".equals(request.lastName()) && request.firstName() == null));
        verify(usersClient, times(1)).get(ANA_ID);
        assertTrue(LocatorJ._find(Span.class).stream().anyMatch(span -> span.getText().startsWith("Ana Alvarez Arias · ana@mto.local")),
                "la cabecera pinta lo que devuelve el servicio, que lo relee de Keycloak antes de contestar: sin otro GET");

        // Sin cambios no se llama: el dialogo se cierra y ya.
        clearInvocations(usersClient);
        NotificationsKt.clearNotifications();
        LocatorJ._click(detailButton("user-edit"));
        LocatorJ._click(LocatorJ._get(LocatorJ._get(Dialog.class), Button.class, spec -> spec.withId("user-save")));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty());
        verify(usersClient, never()).update(any(), any());
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "nada que guardar, nada que avisar");
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
    void theNormalAndOfflineSessionsAreLoadedEachOnItsOwn() {
        loginAs("usuarios.lector", "ROLE_USERS_READ");
        stubUserDetail(ana());
        when(usersClient.sessions(ANA_ID)).thenThrow(BackofficeApiException.of(HttpStatus.SERVICE_UNAVAILABLE, ApiProblem.empty(), "corr-u9",
                null, "GET /api/users/" + ANA_ID + "/sessions"));

        UI.getCurrent().navigate(ANA_ROUTE);
        selectTab(2);

        assertEquals(1, NotificationsKt.getNotifications().size(), "el fallo de una se notifica");
        assertEquals(1, GridKt._size(gridWithId("offline-sessions-grid")), "y la otra se ve igual");
        LocatorJ._get(Span.class, spec -> spec.withText("1 sesion offline"));
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
        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("No se ha podido sacar a ana: fallo al cerrar las sesiones "
                + "(hecho: desactivar). El servicio no esta disponible ahora mismo. Intentalo mas tarde."));
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("Referencia: corr-u8"));
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
        // El filtro es local y, como en mto-frontend, sin mirar mayusculas ni tildes.
        TextField filter = LocatorJ._get(TextField.class, spec -> spec.withId("profile-filter"));
        LocatorJ._setValue(filter, "GESTIÓN");
        assertEquals(1, GridKt._size(catalogue));
        LocatorJ._get(Span.class, spec -> spec.withText("1 de 3 perfiles"));
        LocatorJ._setValue(filter, "");

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
        LocatorJ._setValue(filter, "LEÉR");
        assertEquals(1, GridKt._size(catalogue), "sin mirar mayusculas ni tildes, como en mto-frontend");
        LocatorJ._setValue(filter, "");
        assertEquals(3, GridKt._size(catalogue));

        catalogue.select(GridKt._get(catalogue, 0));
        verify(usersClient).clientRoleMembers("mto-users-api", "users-read", 0, 50);
        Grid<Object> members = gridWithId("role-members-grid");
        assertEquals(2, GridKt._size(members));
        assertEquals("ana", ((RouterLink) GridKt._getCellComponent(members, 0, "username")).getText());
        LocatorJ._get(H4.class, spec -> spec.withText("Miembros de mto-users-api / users-read"));
        assertFalse(detailButton("role-members-next").isEnabled());
        assertFalse(detailButton("role-members-previous").isEnabled());
        verify(usersClient, times(1)).clients();

        // Volver a un cliente ya elegido no vuelve a pedir sus roles mientras dura la pantalla.
        LocatorJ._setValue(picker, CONFIGURATION_API);
        LocatorJ._setValue(picker, USERS_API);
        assertEquals(3, GridKt._size(catalogue));
        verify(usersClient, times(1)).clientRoles("mto-users-api");
    }

    @Test
    void aMembersUsernameLinksToTheUserDetail() {
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
        Grid<Object> members = gridWithId("role-members-grid");
        GridKt._clickItem(members, 0, 1, false, false, false, false);
        assertTrue(LocatorJ._find(UserDetailView.class).isEmpty(), "un clic en la fila no abre nada, como en mto-frontend");
        RouterLinkKt._click((RouterLink) GridKt._getCellComponent(members, 0, "username"));

        LocatorJ._get(UserDetailView.class);
        LocatorJ._get(H2.class, spec -> spec.withText("ana"));
        assertTrue(LocatorJ._find(ClientRolesView.class).isEmpty());
    }

    // --- Almacen (mto-stock): los mensajes de sus errores -------------------------------------------

    private static BackofficeApiException stockError(int status, String code, String message) {
        ApiProblem problem = new ApiProblem(null, HttpStatus.valueOf(status).name(), status, message, null, code, null, null, null, false, null, null);
        return BackofficeApiException.of(HttpStatusCode.valueOf(status), problem, "corr-s9", null, "POST /api/stock/movements/outputs");
    }

    /** mto-users: un nombre de usuario o un email repetido no se arregla recargando, asi que no se pide. */
    @Test
    void aRepeatedUsernameOrEmailDoesNotAskToReload() {
        ApiProblem problem = new ApiProblem("about:blank", "Conflict", 409, "User exists with same username", null,
                "USR-409", null, null, null, false, null, null);
        assertEquals("Ya existe un usuario con ese nombre de usuario o ese email. User exists with same username",
                UiErrors.message(BackofficeApiException.of(HttpStatus.CONFLICT, problem, "corr-u1", null, "POST /api/users")));
    }

    /** Un 422 sin campos es una regla de negocio y un 409 STK-001 es falta de stock: no «peticion no valida» ni «recarga». */
    @Test
    void stockErrorsReadAsStockErrorsInTheNotifications() {
        assertEquals("La operacion no es posible. Only active reservations can be changed",
                UiErrors.message(stockError(422, "RES-001", "Only active reservations can be changed")));
        assertEquals("No hay stock disponible suficiente. Insufficient stock for material m in warehouse w: requested 5, available 2",
                UiErrors.message(stockError(409, "STK-001", "Insufficient stock for material m in warehouse w: requested 5, available 2")));
        for (String duplicated : List.of("MAT-409", "WH-409", "SUP-409", "PRJ-409", "ASM-409")) {
            assertEquals("Ya existe otro con ese codigo.", UiErrors.message(stockError(409, duplicated, "Code 'X-1' already exists")),
                    duplicated + ": un codigo repetido no se arregla recargando");
        }
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
        verify(warehouseClient, atLeastOnce()).search(isNull(), isNull(), intThat(page -> page > 0), anyInt(), eq(List.of("code,asc", "id,asc")));

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
        verify(warehouseClient, atLeastOnce()).search(any(), any(), anyInt(), anyInt(), eq(List.of("name,desc", "id,asc")));
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
        LocatorJ._setValue(code, "   ");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("catalogue-save")));
        assertTrue(code.isInvalid(), "solo espacios es un codigo vacio, como en mto-frontend");
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
        verify(movementClient, atLeastOnce()).search(isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), anyInt(),
                eq(List.of("occurredAt,desc", "id,asc")));

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

    /** Un tipo de apunte que mto-stock estrene se pinta «Desconocido» y no se ofrece como filtro. */
    @Test
    void aMovementOfAnUnknownTypeIsListedButNotOfferedAsAFilter() {
        loginAs("almacen.lector", "ROLE_STOCK_READ");
        when(movementClient.search(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt(), anyList()))
                .thenAnswer(call -> page(List.of(movement(MovementType.UNKNOWN, "-3")), call.getArgument(7), call.getArgument(8)));

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);

        List<String> row = GridKt._getFormattedRow(gridWithId("movements-grid"), 0);
        assertTrue(row.contains("Desconocido"), row.toString());
        List<MovementType> offered = ViewLayerTest.<MovementType>combo("movements-type").getListDataView().getItems().toList();
        assertEquals(MovementType.selectable(), offered);
        assertFalse(offered.contains(MovementType.UNKNOWN));
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

    /**
     * Una cantidad de almacen es numeric(19,6): lo que no cabe (siete decimales, catorce cifras
     * enteras) no llega al servicio desde ningun dialogo, como en mto-frontend.
     */
    @Test
    void aStockQuantityThatDoesNotFitTheServiceColumnNeverReachesIt() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        when(materialClient.stock(any(), any())).thenReturn(stockOf(new BigDecimal("10"), false));
        when(movementClient.entry(any())).thenReturn(movement(MovementType.ENTRY, "1.123456"));
        when(materialClient.create(any())).thenAnswer(call -> new MaterialDto(UUID.randomUUID(), ((MaterialRequest) call.getArgument(0)).code(), "Hilo", "m",
                BigDecimal.ZERO, true, null));
        stubReservations();

        UI.getCurrent().navigate(StockRoutes.PREFIX);
        LocatorJ._setValue(ViewLayerTest.<MaterialSummaryDto>combo("stock-material"), HILO);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-entry")));
        Dialog entry = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(entry, ComboBox.class, spec -> spec.withId("movement-warehouse")), CENTRAL);
        BigDecimalField quantity = LocatorJ._get(entry, BigDecimalField.class, spec -> spec.withId("movement-quantity"));
        Button saveEntry = LocatorJ._get(entry, Button.class, spec -> spec.withId("movement-save"));
        LocatorJ._setValue(quantity, new BigDecimal("1.1234567"));
        LocatorJ._click(saveEntry);
        assertEquals("Como mucho 6 decimales", quantity.getErrorMessage());
        LocatorJ._setValue(quantity, new BigDecimal("12345678901234"));
        LocatorJ._click(saveEntry);
        assertEquals("Como mucho 13 cifras enteras", quantity.getErrorMessage());
        verify(movementClient, never()).entry(any());
        LocatorJ._setValue(quantity, new BigDecimal("1.123456"));
        LocatorJ._click(saveEntry);
        verify(movementClient).entry(argThat(request -> new BigDecimal("1.123456").equals(request.quantity())));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("reservation-create")));
        Dialog reservation = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(reservation, ComboBox.class, spec -> spec.withId("reservation-material")), HILO);
        LocatorJ._setValue(LocatorJ._get(reservation, ComboBox.class, spec -> spec.withId("reservation-warehouse")), CENTRAL);
        LocatorJ._setValue(LocatorJ._get(reservation, ComboBox.class, spec -> spec.withId("reservation-project")), TRAMO);
        BigDecimalField reserved = LocatorJ._get(reservation, BigDecimalField.class, spec -> spec.withId("reservation-quantity"));
        LocatorJ._setValue(reserved, new BigDecimal("0.0000001"));
        LocatorJ._click(LocatorJ._get(reservation, Button.class, spec -> spec.withId("reservation-save")));
        assertEquals("Como mucho 6 decimales", reserved.getErrorMessage());
        verify(reservationClient, never()).create(any());
        reservation.close();

        UI.getCurrent().navigate(StockRoutes.MATERIALS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("stock-create")));
        Dialog material = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(material, TextField.class, spec -> spec.withLabel("Codigo")), "MAT-010");
        LocatorJ._setValue(LocatorJ._get(material, TextField.class, spec -> spec.withLabel("Nombre")), "Hilo");
        LocatorJ._setValue(LocatorJ._get(material, TextField.class, spec -> spec.withLabel("Unidad de medida")), "m");
        BigDecimalField minimum = LocatorJ._get(material, BigDecimalField.class, spec -> spec.withLabel("Stock minimo"));
        LocatorJ._setValue(minimum, new BigDecimal("0.1234567"));
        LocatorJ._click(LocatorJ._get(material, Button.class, spec -> spec.withId("material-save")));
        assertEquals("Como mucho 6 decimales", minimum.getErrorMessage());
        verify(materialClient, never()).create(any());
        LocatorJ._setValue(minimum, BigDecimal.ZERO);
        LocatorJ._click(LocatorJ._get(material, Button.class, spec -> spec.withId("material-save")));
        verify(materialClient).create(new MaterialRequest("MAT-010", "Hilo", "m", BigDecimal.ZERO));

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("stock-create")));
        Dialog assembly = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<MaterialSummaryDto> component = LocatorJ._get(assembly, ComboBox.class, spec -> spec.withId("bom-material"));
        BigDecimalField perAssembly = LocatorJ._get(assembly, BigDecimalField.class, spec -> spec.withId("bom-quantity"));
        LocatorJ._setValue(component, HILO);
        LocatorJ._setValue(perAssembly, new BigDecimal("2.1234567"));
        LocatorJ._click(LocatorJ._get(assembly, Button.class, spec -> spec.withId("bom-add")));
        assertEquals("Como mucho 6 decimales", perAssembly.getErrorMessage());
        assertEquals(0, GridKt._size(gridWithId("bom-grid")), "la linea no entra en la lista");
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

    /**
     * Un desplegable del almacen busca en el servidor, con su orden y el id para desempatar. En un
     * filtro ofrece tambien lo retirado, marcado, para encontrar lo de antes; en un dialogo, solo lo
     * activo, porque el servicio rechaza lo retirado.
     */
    @Test
    void theFiltersOfferWhatIsRetiredMarkedAndTheDialogsOnlyWhatIsActive() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        UUID oldWarehouse = UUID.fromString("2b2b2b2b-0000-4000-8000-00000000000b");
        stubCatalogue(warehouseClient, List.of(warehouse(WH1, "WH-000", "Central", true), warehouse(oldWarehouse, "WH-009", "Antigua", false)),
                WarehouseDto::code, WarehouseDto::name, WarehouseDto::active);

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);
        assertEquals(List.of("WH-000 - Central", "WH-009 - Antigua (retirado)"),
                ComboBoxKt.getSuggestions(ViewLayerTest.<WarehouseSummaryDto>combo("movements-warehouse")));
        verify(warehouseClient, atLeastOnce()).search(isNull(), isNull(), anyInt(), anyInt(), eq(List.of("code,asc", "id,asc")));

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-transfer")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<WarehouseSummaryDto> source = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse"));
        assertEquals(List.of("WH-000 - Central"), ComboBoxKt.getSuggestions(source));
        verify(warehouseClient, atLeastOnce()).search(isNull(), eq(true), anyInt(), anyInt(), eq(List.of("code,asc", "id,asc")));
    }

    /** Lo que el servicio dice de una transferencia cae en su campo aunque alli se llame de otra forma. */
    @Test
    void aTransferRejectedByTheServicePutsEachErrorOnItsField() {
        loginAs("almacen.operario", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE");
        ApiProblem problem = new ApiProblem(null, "Bad Request", 400, "Validation failed", null, "REQ-VALIDATION", null, null, null, false,
                List.of(new ApiFieldError("sourceWarehouseId", null, "Warehouse WH-000 is inactive"),
                        new ApiFieldError("differentWarehouses", null, "Source and target warehouses must be different")), null);
        when(movementClient.transfer(any())).thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-s4", null,
                "POST /api/stock/movements/transfers"));

        UI.getCurrent().navigate(StockRoutes.MOVEMENTS);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("operation-transfer")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-material")), HILO);
        @SuppressWarnings("unchecked")
        ComboBox<WarehouseSummaryDto> source = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-warehouse"));
        LocatorJ._setValue(source, CENTRAL);
        @SuppressWarnings("unchecked")
        ComboBox<WarehouseSummaryDto> target = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withId("movement-target"));
        LocatorJ._setValue(target, NAVE2);
        LocatorJ._setValue(LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("movement-quantity")), new BigDecimal("3"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("movement-save")));

        assertTrue(source.isInvalid());
        assertEquals("Warehouse WH-000 is inactive", source.getErrorMessage(), "sourceWarehouseId es el almacen de origen del dialogo");
        assertTrue(target.isInvalid());
        assertEquals("Source and target warehouses must be different", target.getErrorMessage(), "la regla de clase es del destino");
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "todo cayo en un campo: nada que notificar");
        assertFalse(LocatorJ._find(Dialog.class).isEmpty());
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
        verify(reservationClient, atLeastOnce()).search(isNull(), eq(ReservationStatus.ACTIVE), isNull(), isNull(), eq(0), anyInt(),
                eq(List.of("reservedAt,desc", "id,asc")));

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
        verify(reservationClient, atLeastOnce()).search(any(), any(), any(), any(), anyInt(), anyInt(), eq(List.of("quantity,desc", "id,asc")));
    }

    /**
     * Un estado de reserva que mto-stock estrene se pinta «Desconocido»: como no es activa, su fila
     * solo ofrece el historial, tambien a quien puede todo. Tampoco se ofrece como filtro.
     */
    @Test
    void aReservationInAnUnknownStateOnlyOffersItsHistory() {
        loginAs("almacen.responsable", "ROLE_STOCK_READ", "ROLE_STOCK_WRITE", "ROLE_STOCK_DELETE");
        UUID unknown = UUID.randomUUID();
        stubReservations(reservation(unknown, ReservationStatus.UNKNOWN, "2"));

        UI.getCurrent().navigate(StockRoutes.RESERVATIONS);
        Grid<Object> grid = gridWithId("reservations-grid");

        assertTrue(GridKt._getFormattedRow(grid, 0).contains("Desconocido"));
        assertEquals(List.of("history-" + unknown), actionIds(grid, 0), "solo una reserva activa cambia");
        assertFalse(ViewLayerTest.<ReservationStatus>combo("reservations-status").getListDataView().getItems().toList()
                .contains(ReservationStatus.UNKNOWN));
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
        UUID retiredId = UUID.fromString("2b2b2b2b-0000-4000-8000-00000000000a");
        AssemblyDto retired = new AssemblyDto(retiredId, "ASM-009", "Mensula vieja", false,
                List.of(new AssemblyComponentDto(UUID.randomUUID(), HILO, new BigDecimal("1"))), null);
        stubCatalogue(assemblyClient, List.of(mensula(), retired), AssemblyDto::code, AssemblyDto::name, AssemblyDto::active);

        UI.getCurrent().navigate(StockRoutes.ASSEMBLIES);
        Grid<Object> grid = stockGrid();

        assertEquals(2, GridKt._size(grid));
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertTrue(row.contains("ASM-001") && row.contains("2"), row.toString());
        LocatorJ._get(Span.class, spec -> spec.withText("2 conjuntos"));
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 1, StockCatalogueView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("availability-" + retiredId)).isEmpty(), "un conjunto retirado no se monta: sin disponibilidad");
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
        ApiProblem bomRejected = new ApiProblem(null, "Bad Request", 400, "Validation failed", null, "REQ-VALIDATION", null, null, null, false,
                List.of(new ApiFieldError("components[1].quantity", null, "must be greater than 0")), null);
        when(assemblyClient.create(any()))
                .thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, bomRejected, "corr-s3", null, "POST /api/stock/assemblies"))
                .thenReturn(mensula());

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
        assertEquals("must be greater than 0", LocatorJ._get(dialog, Span.class, spec -> spec.withId("bom-error")).getText(),
                "el error del servicio sobre una linea va a la lista de materiales");
        assertTrue(NotificationsKt.getNotifications().isEmpty());
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withId("assembly-save")));

        verify(assemblyClient, times(2)).create(new AssemblyRequest("ASM-002", "Mensula doble", List.of(
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
                team, "mantenimiento.tecnico", null, null, null, null, null, 10, 3, new BigDecimal("450"), 2, null, 3L);
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

    /** Los 409 de estado de mantenimiento no piden recargar; el {@code CON-001} de una version vieja, si. */
    @Test
    void maintenanceErrorsSayWhatBlocksTheOperation() {
        assertEquals("Conflicto con otro cambio: recarga y vuelve a intentarlo.",
                UiErrors.message(maintenanceError(409, "CON-001", "Maintenance order MO-000001 was changed by someone else")));
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
        assertEquals("El almacen ha rechazado la operacion. mto-stock rejected 'reserve' with 422 MAT-001: Material MAT-001 is inactive",
                UiErrors.message(maintenanceError(422, "STK-422", "mto-stock rejected 'reserve' with 422 MAT-001: Material MAT-001 is inactive")));
        assertEquals("No hay stock disponible suficiente. mto-stock rejected 'reserve' with 409 STK-001: Insufficient stock",
                UiErrors.message(maintenanceError(409, "STK-001", "mto-stock rejected 'reserve' with 409 STK-001: Insufficient stock")));
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
                "12.1 - 13.45", "PAQ NORTE", "14/09/2026", "EQ-01 - Brigada norte", "3/10", "mantenimiento.tecnico"),
                GridKt._getFormattedRow(grid, 0).subList(0, 13));
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
    private static final UUID ASSET_OFF_HERE = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000013");
    private static final UUID ASSET_OFF_AT_SOURCE = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000014");
    private static final UUID ASSET_OFF_BOTH = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000015");
    private static final String[] MAINTENANCE_TECHNICIAN = {"ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ", "ROLE_STOCK_READ"};
    private static final String[] MAINTENANCE_MANAGER = {"ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_MAINTENANCE_DELETE",
            "ROLE_MAINTENANCE_SUPERVISE", "ROLE_CONFIG_READ", "ROLE_STOCK_READ"};

    private static AssetDto syncedProfile() {
        return syncedProfile(ASSET_SYNCED, "PRF-0001", true, false);
    }

    /** Un perfil de mto-configuration: activo solo si el origen lo tiene activo y aqui nadie lo desactivo. */
    private static AssetDto syncedProfile(UUID id, String code, boolean enabledAtSource, boolean disabledLocally) {
        return new AssetDto(id, code, "12-2.27", CatenaryAssetType.PROFILE, null, 3L, 12L, 4L, new BigDecimal("12.270"),
                new BigDecimal("12.270"), "501", "S-3", null, null, null, List.of(), "mto-configuration", "501",
                enabledAtSource && !disabledLocally, enabledAtSource, disabledLocally, 180, null, Instant.parse("2026-10-01T00:00:00Z"), null, 7L);
    }

    private static AssetDto ownSection(UUID id, String code, boolean enabled) {
        return new AssetDto(id, code, "Tramo " + code, CatenaryAssetType.TRACK_SECTION, "Tramo propio", 3L, 12L, null,
                new BigDecimal("12.100"), new BigDecimal("13.450"), null, null, TrackKind.MAIN, null, null, List.of(), null, null, enabled,
                null, !enabled, null, null, null, null, 1L);
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
        assertEquals(List.of("asset-orders-" + ASSET_SYNCED, "asset-history-" + ASSET_SYNCED),
                LocatorJ._find(GridKt._getCellComponent(grid, 0, AssetsView.ACTIONS_COLUMN), Button.class).stream()
                        .map(button -> button.getId().orElse("")).toList(), "quien solo lee ve las ordenes y el historial, no modificar ni desactivar");
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

    /**
     * Un activo de mto-configuration solo cambia descripcion e intervalo y manda solo lo cambiado.
     * Desactivarlo aqui avisa de que sobrevive a los datos maestros.
     */
    @Test
    void aSynchronizedAssetOnlyChangesDescriptionAndIntervalAndItsDisablingSurvivesMasterData() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        stubReferencesForMaintenance();
        stubAssets(List.of(syncedProfile()));
        when(assetClient.update(any(), any())).thenReturn(syncedProfile());

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        Grid<Object> grid = gridWithId("assets-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-disable-" + ASSET_SYNCED)));
        ConfirmDialog confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message", "").contains("Sigue desactivado aunque mto-configuration lo mande activo."),
                "lo que se decide aqui no lo deshace el siguiente evento");
        ConfirmDialogKt._fireConfirm(confirm);
        verify(assetClient).disable(ASSET_SYNCED);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-edit-" + ASSET_SYNCED)));

        assertTrue(LocatorJ._find(TextField.class, spec -> spec.withId("asset-name")).isEmpty(), "el nombre es de mto-configuration");
        LocatorJ._setValue(LocatorJ._get(IntegerField.class, spec -> spec.withId("asset-interval")), null);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(AssetEditorDialog.SAVE_ID)));
        verify(assetClient).update(ASSET_SYNCED, new MergePatch<>(new AssetUpdateRequest(null, null, null, null, null, null, null, null, null, null),
                Set.of("preventiveIntervalDays"), 7L));
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
        verify(assetClient).update(ASSET_OWN_OFF, MergePatch.of(AssetUpdateRequest.enabled(true), 1L));
        NotificationsKt.expectNotifications("Reactivado TS-0009 - Tramo TS-0009");
    }

    /**
     * El estado dice quien desactivo un activo sincronizado. Solo se reactiva lo que se desactivo
     * aqui y el origen tiene activo; lo desactivado en mto-configuration se puede desactivar tambien
     * aqui, para que siga asi cuando el origen lo reactive, pero no reactivar (409 {@code AST-001}).
     */
    @Test
    void aSynchronizedAssetSaysWhoDisabledItAndIsOnlyReactivatedIfItWasDisabledHere() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        stubReferencesForMaintenance();
        stubAssets(List.of(syncedProfile(ASSET_OFF_HERE, "PRF-0013", true, true), syncedProfile(ASSET_OFF_AT_SOURCE, "PRF-0014", false, false),
                syncedProfile(ASSET_OFF_BOTH, "PRF-0015", false, true)));
        when(assetClient.update(any(), any())).thenReturn(syncedProfile(ASSET_OFF_HERE, "PRF-0013", true, false));

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        Grid<Object> grid = gridWithId("assets-grid");
        assertTrue(GridKt._getFormattedRow(grid, 0).contains("Desactivado aqui"));
        assertTrue(GridKt._getFormattedRow(grid, 1).contains("Desactivado en configuracion"));
        assertTrue(GridKt._getFormattedRow(grid, 2).contains("Desactivado aqui y en configuracion"));
        assertEquals(List.of("asset-enable"), assetStateActions(grid, 0), "desactivado aqui: se reactiva aqui");
        assertEquals(List.of("asset-disable"), assetStateActions(grid, 1), "desactivado en el origen: se puede decidir que siga asi");
        assertEquals(List.of(), assetStateActions(grid, 2), "reactivarlo seria 409 AST-001 mientras el origen no lo reactive");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-enable-" + ASSET_OFF_HERE)));
        verify(assetClient).update(ASSET_OFF_HERE, MergePatch.of(AssetUpdateRequest.enabled(true), 7L));
        NotificationsKt.expectNotifications("Reactivado PRF-0013 - 12-2.27");
    }

    /** Los botones de desactivar y reactivar de una fila de activos, sin el id. */
    private static List<String> assetStateActions(Grid<Object> grid, int row) {
        return LocatorJ._find(GridKt._getCellComponent(grid, row, AssetsView.ACTIONS_COLUMN), Button.class).stream()
                .map(button -> button.getId().orElse("")).filter(id -> id.startsWith("asset-disable-") || id.startsWith("asset-enable-"))
                .map(id -> id.replaceAll("-[0-9a-f]{8}-.*$", "")).toList();
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

    // --- Mantenimiento: ordenes y tareas ----------------------------------------------------------

    private static final UUID TASK1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000021");
    private static final UUID TASK2 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000022");

    private static OrderDto orderOf(MaintenanceOrderStatus status, MaintenanceOrderType type) {
        OrderDto base = order(ORDER1, "MO-000001", status, 12L, 3L);
        return new OrderDto(base.id(), base.code(), base.title(), base.description(), type, status, base.priority(), base.asset(),
                base.executionPackageId(), base.trackId(), base.stationId(), base.startKp(), base.endKp(), base.plannedDate(),
                base.actualStartDate(), base.actualEndDate(), base.team(), base.assignedUser(), base.closingNotes(), base.cancellationReason(),
                base.originInspectionId(), base.originDefectId(), base.stockProjectId(), base.taskCount(), base.completedTaskCount(),
                base.estimatedMinutes(), base.estimatedShifts(), base.audit(), base.version());
    }

    private static TaskDto task(UUID id, int sequence, MaintenanceTaskStatus status) {
        AssetSummaryDto profile = new AssetSummaryDto(ASSET_SYNCED, "PRF-0001", "12-2.27", CatenaryAssetType.PROFILE, 12L,
                new BigDecimal("12.270"), new BigDecimal("12.270"), "S-3", true);
        return new TaskDto(id, ORDER1, sequence, "Perfil 12-2.27", status, null, profile, null, null, null, null, null, List.of(),
                List.of("RG-01"), List.of(), null, 2L);
    }

    private static List<TaskTypeDto> twoTaskTypes() {
        return List.of(
                new TaskTypeDto(UUID.randomUUID(), "RG-01", "Revision visual", FunctionalGroup.STRUCTURAL_SUPPORTS, BigDecimal.TEN, TaskUnit.PROFILE,
                        null, false, false, true, 1),
                new TaskTypeDto(UUID.randomUUID(), "RG-04", "Revision del hilo de contacto", FunctionalGroup.OVERHEAD_CONDUCTORS,
                        new BigDecimal("12.5"), TaskUnit.SPAN, null, true, false, true, 4));
    }

    /** Las ordenes simuladas, paginadas como el servicio. */
    private void stubOrders(List<OrderDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(orderClient).search(any(OrderFilter.class), anyInt(), anyInt(), anyList());
    }

    private void openOrder(OrderDto order) {
        when(orderClient.findById(order.id())).thenReturn(order);
        UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(order.id()));
    }

    private static boolean hasButton(String id) {
        return !LocatorJ._find(Button.class, spec -> spec.withId(id)).isEmpty();
    }

    private static void click(String id) {
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(id)));
    }

    @Test
    void theOrdersAreFilteredInTheServerAndARowOpensItsDetail() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        when(maintenanceCatalogClient.teams()).thenReturn(List.of(new TeamDto(TEAM1, "EQ-01", "Brigada norte", null, null, true, Set.of(3L), null)));
        OrderDto order = order(ORDER1, "MO-000001", MaintenanceOrderStatus.IN_PROGRESS, 12L, 3L);
        stubOrders(List.of(order));
        when(orderClient.findById(ORDER1)).thenReturn(order);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        LocatorJ._setValue(comboWithId("orders-status"), MaintenanceOrderStatus.IN_PROGRESS);
        LocatorJ._setValue(comboWithId("orders-type"), MaintenanceOrderType.PREVENTIVE);
        LocatorJ._setValue(comboWithId("orders-priority"), MaintenancePriority.HIGH);
        LocatorJ._setValue(comboWithId("orders-track"), new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        LocatorJ._setValue(comboWithId("orders-package"), new RefItem(3L, "PAQ NORTE"));
        ComboBox<TeamDto> team = comboWithId("orders-team");
        ComboBoxKt.selectByLabel(team, "EQ-01 - Brigada norte");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("orders-code")), "MO-0000");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("orders-assigned-user")), "mantenimiento.tecnico");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("orders-planned-from")), LocalDate.of(2026, 9, 1));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("orders-planned-to")), LocalDate.of(2026, 9, 30));
        Grid<Object> grid = gridWithId("orders-grid");
        GridKt._size(grid);

        verify(orderClient, atLeastOnce()).search(eq(new OrderFilter(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE,
                MaintenancePriority.HIGH, null, null, 12L, null, 3L, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), "mantenimiento.tecnico",
                TEAM1, "MO-0000")), eq(0), anyInt(), anyList());
        assertFalse(hasButton("order-create"), "sin write no hay alta");

        GridKt._clickItem(grid, 0, 1, false, false, false, false);
        assertTrue(LocatorJ._find(OrderDetailView.class).isEmpty(), "un clic simple no abre nada, como en mto-frontend");
        GridKt._doubleClickItem(grid, 0, 1, false, false, false, false);
        LocatorJ._get(OrderDetailView.class);
        LocatorJ._get(H2.class, spec -> spec.withText("MO-000001 · Revision tramo 12"));
        assertFalse(hasButton("order-edit"), "quien solo lee no ve ningun boton de escritura");
        assertFalse(hasButton("order-complete"));
    }

    @Test
    void aNewOrderSearchesItsAssetInTheServerAndOpensItsDetail() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        stubAssets(List.of(ownSection(ASSET_OWN, "TS-0001", true)));
        OrderDto created = orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE);
        when(orderClient.create(any())).thenReturn(created);
        when(orderClient.findById(ORDER1)).thenReturn(created);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        click("order-create");
        ComboBox<AssetSummaryDto> asset = comboWithId("order-asset");
        ComboBoxKt.setUserInput(asset, "TS");
        assertEquals(List.of("TS-0001 - Tramo TS-0001"), ComboBoxKt.getSuggestions(asset));
        verify(assetClient, atLeastOnce()).search(eq(new AssetFilter(null, null, null, null, true, "TS", null)), anyInt(), anyInt(), anyList());
        ComboBoxKt.selectByLabel(asset, "TS-0001 - Tramo TS-0001");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("order-title")), "Revision tramo 12");
        click(OrderEditorDialog.SAVE_ID);

        verify(orderClient).create(new OrderRequest("Revision tramo 12", null, MaintenanceOrderType.PREVENTIVE, MaintenancePriority.MEDIUM,
                ASSET_OWN, null, null, null, null));
        LocatorJ._get(OrderDetailView.class);
    }

    /** Cada estado ofrece lo suyo, cancelar pide supervise y una orden que no existe devuelve a la lista. */
    @Test
    void theOrderDetailOffersWhatItsStateAdmitsAndAMissingOrderGoesBack() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));
        assertTrue(hasButton("order-edit") && hasButton("order-plan"));
        assertFalse(hasButton("order-start"), "una preventiva en borrador se planifica antes de iniciar");
        assertFalse(hasButton("order-assign") || hasButton("order-complete"));
        assertFalse(hasButton("order-cancel"), "cancelar pide maintenance-supervise");

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.URGENT));
        assertTrue(hasButton("order-start"), "una urgente arranca sin planificar");

        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        openOrder(orderOf(MaintenanceOrderStatus.ASSIGNED, MaintenanceOrderType.PREVENTIVE));
        assertTrue(hasButton("order-assign") && hasButton("order-start") && hasButton("order-cancel"));
        when(orderClient.cancel(eq(ORDER1), any())).thenReturn(orderOf(MaintenanceOrderStatus.CANCELLED, MaintenanceOrderType.PREVENTIVE));
        click("order-cancel");
        click("reason-confirm");
        verify(orderClient, never()).cancel(any(), any());
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("reason-text")), "Duplicada");
        click("reason-confirm");
        verify(orderClient).cancel(ORDER1, new ReasonRequest("Duplicada"));
        NotificationsKt.expectNotifications("MO-000001 cancelada");
        assertEquals("Cancelada", LocatorJ._get(Span.class, spec -> spec.withId("order-status")).getText());
        assertFalse(hasButton("order-edit") || hasButton("order-cancel"), "una orden terminada no ofrece nada");

        UUID missing = UUID.randomUUID();
        when(orderClient.findById(missing)).thenThrow(maintenanceError(404, "ORD-404", "MaintenanceOrder with id " + missing + " was not found"));
        UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(missing));
        LocatorJ._get(OrdersView.class);
        NotificationsKt.expectNotifications("No existe la orden " + missing);
    }

    @Test
    void planningRepaintsTheOrderAndARefusedTransitionIsNotifiedWithTheDialogOpen() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        LocalDate nextWeek = LocalDate.now().plusDays(7);
        when(orderClient.plan(eq(ORDER1), any())).thenReturn(orderOf(MaintenanceOrderStatus.PLANNED, MaintenanceOrderType.PREVENTIVE));
        when(orderClient.start(eq(ORDER1), any())).thenThrow(maintenanceError(409, "TRN-001", "Order MO-000001 cannot be started from PLANNED"));
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        click("order-plan");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("order-transition-planned-date")), nextWeek);
        click(OrderTransitionDialog.CONFIRM_ID);
        verify(orderClient).plan(ORDER1, new PlanOrderRequest(nextWeek, null));
        assertEquals("Planificada", LocatorJ._get(Span.class, spec -> spec.withId("order-status")).getText());
        assertTrue(hasButton("order-start") && hasButton("order-assign"));
        assertFalse(hasButton("order-plan"));

        click("order-start");
        click(OrderTransitionDialog.CONFIRM_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El estado actual no permite esta operacion. Order MO-000001 cannot be started from PLANNED"));
        assertFalse(LocatorJ._find(OrderTransitionDialog.class).isEmpty(), "el dialogo sigue abierto");
    }

    /**
     * Un editor de mantenimiento manda PATCH con lo cambiado, lo vaciado a {@code null} y la version
     * leida: vaciar la fecha prevista y el equipo de una orden en borrador ya no es «no se puede
     * vaciar». Si otra persona guardo antes, el 409 {@code CON-001} deja el dialogo abierto con lo
     * escrito y pide recargar.
     */
    @Test
    void anOrderEditorClearsWhatWasEmptiedAndAStaleVersionAsksToReload() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(orderClient.update(eq(ORDER1), any())).thenThrow(maintenanceError(409, "CON-001",
                "Maintenance order MO-000001 was changed by someone else"));
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        click("order-edit");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("order-planned-date")), null);
        LocatorJ._setValue(comboWithId("order-team"), null);
        click(OrderEditorDialog.SAVE_ID);

        verify(orderClient).update(ORDER1, new MergePatch<>(new OrderUpdateRequest(null, null, null, null, null, null, null, null, null, null,
                null, null, null), Set.of("plannedDate", "teamId"), 3L));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Conflicto con otro cambio: recarga y vuelve a intentarlo."));
        assertFalse(LocatorJ._find(OrderEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");
    }

    /**
     * Sin {@code stock-read} el editor no ensena el proyecto de almacen de la orden ni puede nombrarlo,
     * pero tampoco lo manda a vaciar: solo viaja lo que la persona cambio.
     */
    @Test
    void anOrderEditedWithoutStockReadKeepsItsStockProject() {
        loginAs("mantenimiento.sin-almacen", "ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ");
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        OrderDto draft = orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE);
        OrderDto withProject = new OrderDto(draft.id(), draft.code(), draft.title(), draft.description(), draft.type(), draft.status(),
                draft.priority(), draft.asset(), draft.executionPackageId(), draft.trackId(), draft.stationId(), draft.startKp(), draft.endKp(),
                draft.plannedDate(), null, null, draft.team(), draft.assignedUser(), null, null, null, null,
                UUID.fromString("3c3c3c3c-0000-4000-8000-000000000057"), 0, 0, BigDecimal.ZERO, 0, null, draft.version());
        when(orderClient.update(eq(ORDER1), any())).thenReturn(withProject);
        openOrder(withProject);

        click("order-edit");
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("order-stock-project")).isEmpty(), "sin stock-read no se ofrece");
        LocatorJ._setValue(comboWithId("order-priority"), MaintenancePriority.CRITICAL);
        click(OrderEditorDialog.SAVE_ID);

        verify(orderClient).update(ORDER1, MergePatch.of(new OrderUpdateRequest(null, null, MaintenancePriority.CRITICAL, null, null, null, null,
                null, null, null, null, null, null), 3L));
    }

    @Test
    void anOrderInProgressOnlyChangesWhatTheServiceAdmitsAndForceNeedsSupervise() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        OrderDto running = orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE);
        when(orderClient.update(eq(ORDER1), any())).thenReturn(running);
        when(orderClient.complete(eq(ORDER1), any())).thenReturn(orderOf(MaintenanceOrderStatus.COMPLETED, MaintenanceOrderType.PREVENTIVE));
        openOrder(running);

        click("order-edit");
        assertTrue(LocatorJ._find(TextField.class, spec -> spec.withId("order-title")).isEmpty(), "en curso ya no se cambia el titulo");
        LocatorJ._setValue(comboWithId("order-priority"), MaintenancePriority.CRITICAL);
        click(OrderEditorDialog.SAVE_ID);
        verify(orderClient).update(ORDER1, MergePatch.of(new OrderUpdateRequest(null, null, MaintenancePriority.CRITICAL, null, null, null, null,
                null, null, null, null, null, null), 3L));

        click("order-complete");
        assertTrue(LocatorJ._find(Checkbox.class, spec -> spec.withId("order-transition-force")).isEmpty(), "force pide supervise");
        click(OrderTransitionDialog.CONFIRM_ID);
        verify(orderClient).complete(ORDER1, new CompleteOrderRequest(null, null, null));

        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        openOrder(running);
        click("order-complete");
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("order-transition-force")), true);
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("order-transition-closing-notes")), "Sin incidencias");
        click(OrderTransitionDialog.CONFIRM_ID);
        verify(orderClient).complete(ORDER1, new CompleteOrderRequest("Sin incidencias", true, null));
    }

    @Test
    void theTasksTabAddsGeneratesEditsAndCancelsOnlyOpenTasks() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(task(TASK1, 1, MaintenanceTaskStatus.PENDING), task(TASK2, 2, MaintenanceTaskStatus.COMPLETED)));
        when(orderClient.generateTasks(eq(ORDER1), any())).thenReturn(new GenerateTasksResultDto(14, 2, 16, new BigDecimal("720.0"), 3));
        when(orderClient.createTask(eq(ORDER1), any())).thenReturn(task(TASK2, 3, MaintenanceTaskStatus.PENDING));
        when(orderClient.updateTask(eq(ORDER1), eq(TASK1), any())).thenReturn(task(TASK1, 1, MaintenanceTaskStatus.PENDING));
        when(orderClient.cancelTask(eq(ORDER1), eq(TASK1), any())).thenReturn(task(TASK1, 1, MaintenanceTaskStatus.CANCELLED));
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        Grid<Object> tasks = gridWithId("order-tasks-grid");
        assertEquals(2, GridKt._size(tasks));
        assertFalse(LocatorJ._find(GridKt._getCellComponent(tasks, 0, "actions"), Button.class).isEmpty(), "una tarea pendiente se toca");
        assertTrue(LocatorJ._find(GridKt._getCellComponent(tasks, 1, "actions"), Button.class).isEmpty(), "una completada no");

        click("task-generate");
        click(GenerateTasksDialog.CONFIRM_ID);
        verify(orderClient).generateTasks(ORDER1, new GenerateTasksRequest(null, null));
        NotificationsKt.expectNotifications("Tareas nuevas: 14; perfiles que ya tenian tarea: 2. Total: 16, unos 720 min en 3 turnos.");

        click("task-add");
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("task-description")), "Revisar la mensula");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<TaskTypeDto> types = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("task-types"));
        LocatorJ._setValue(types, Set.of(twoTaskTypes().get(1)));
        click(TaskEditorDialog.SAVE_ID);
        verify(orderClient).createTask(ORDER1, new TaskRequest("Revisar la mensula", null, null, List.of("RG-04"), null));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("task-edit-" + TASK1)));
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("task-notes")), "Falta la llave");
        click(TaskEditorDialog.SAVE_ID);
        verify(orderClient).updateTask(ORDER1, TASK1, MergePatch.of(new TaskUpdateRequest(null, null, null, "Falta la llave", null, null), 2L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("task-cancel-" + TASK1)));
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("reason-text")), "Perfil desmontado");
        click("reason-confirm");
        verify(orderClient).cancelTask(ORDER1, TASK1, new ReasonRequest("Perfil desmontado"));
        verify(orderClient, atLeast(4)).findById(ORDER1);
    }

    /** Los tipos que el catalogo no trae vuelven tal cual, se comparan como conjunto y quitarlos todos los vacia. */
    @Test
    void aTaskKeepsTheTypesTheCatalogueDoesNotNameAndEmptyingThemClearsThem() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        TaskDto base = task(TASK1, 1, MaintenanceTaskStatus.PENDING);
        TaskDto withRetired = new TaskDto(TASK1, ORDER1, 1, base.description(), MaintenanceTaskStatus.PENDING, null, base.asset(), null, null,
                null, null, null, List.of(), List.of("RP-99", "RG-01"), List.of(), null, 2L);
        TaskDto onlyKnown = new TaskDto(TASK2, ORDER1, 2, base.description(), MaintenanceTaskStatus.PENDING, null, base.asset(), null, null,
                null, null, null, List.of(), List.of("RG-01"), List.of(), null, 2L);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(withRetired, onlyKnown));
        when(orderClient.updateTask(eq(ORDER1), any(), any())).thenAnswer(call -> TASK1.equals(call.getArgument(1)) ? withRetired : onlyKnown);
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));
        Grid<Object> tasks = gridWithId("order-tasks-grid");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("task-edit-" + TASK1)));
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("task-notes")), "Falta la llave");
        click(TaskEditorDialog.SAVE_ID);
        verify(orderClient).updateTask(ORDER1, TASK1, MergePatch.of(new TaskUpdateRequest(null, null, null, "Falta la llave", null, null), 2L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("task-edit-" + TASK1)));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<TaskTypeDto> types = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("task-types"));
        LocatorJ._setValue(types, Set.of());
        click(TaskEditorDialog.SAVE_ID);
        verify(orderClient).updateTask(ORDER1, TASK1, MergePatch.of(new TaskUpdateRequest(null, null, List.of("RP-99"), null, null, null), 2L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 1, "actions"), Button.class, spec -> spec.withId("task-edit-" + TASK2)));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<TaskTypeDto> known = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("task-types"));
        LocatorJ._setValue(known, Set.of());
        click(TaskEditorDialog.SAVE_ID);
        verify(orderClient).updateTask(ORDER1, TASK2, new MergePatch<>(new TaskUpdateRequest(null, null, null, null, null, null),
                Set.of("taskTypeCodes"), 2L));
    }

    @Test
    void theHistoryTabNamesTheStatesAndAnAssetRowShowsItsOrders() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(orderClient.history(ORDER1)).thenReturn(List.of(
                new StatusHistoryDto(UUID.randomUUID(), null, "DRAFT", Instant.parse("2026-09-20T08:00:00Z"), "mantenimiento.tecnico", "Order created"),
                new StatusHistoryDto(UUID.randomUUID(), "DRAFT", "PLANNED", Instant.parse("2026-09-21T08:00:00Z"), "mantenimiento.tecnico", null)));
        OrderDto order = orderOf(MaintenanceOrderStatus.PLANNED, MaintenanceOrderType.PREVENTIVE);
        openOrder(order);
        verify(orderClient, never()).history(any());
        selectTab(4);
        Grid<Object> history = gridWithId("order-history-grid");
        assertEquals(List.of("", "Borrador", "mantenimiento.tecnico", "Order created"),
                List.of(GridKt._getFormattedRow(history, 0).get(1), GridKt._getFormattedRow(history, 0).get(2),
                        GridKt._getFormattedRow(history, 0).get(3), GridKt._getFormattedRow(history, 0).get(4)));
        assertEquals("Planificada", GridKt._getFormattedRow(history, 1).get(2));

        stubAssets(List.of(ownSection(ASSET_OWN, "TS-0001", true)));
        doAnswer(call -> page(List.of(order), call.getArgument(1), call.getArgument(2)))
                .when(assetClient).orders(eq(ASSET_OWN), anyInt(), anyInt(), anyList());
        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("assets-grid"), 0, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-orders-" + ASSET_OWN)));
        Grid<Object> orders = gridWithId("asset-orders-grid");
        assertEquals("MO-000001", GridKt._getFormattedRow(orders, 0).getFirst());
        verify(assetClient, atLeastOnce()).orders(eq(ASSET_OWN), eq(0), anyInt(), eq(List.of("createdAt,desc", "id,asc")));
        verify(assetClient, never()).orders(any(), anyInt(), anyInt(), argThat(sort -> !sort.contains("id,asc")));
        GridKt._doubleClickItem(orders, 0, 1, false, false, false, false);
        LocatorJ._get(OrderDetailView.class);
    }

    // --- Mantenimiento: turnos y ejecucion ----------------------------------------------------------

    private static final UUID SHIFT1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000031");
    private static final UUID DISC1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000032");
    private static final UUID ITEM1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000033");

    private static ShiftDto shiftOf(ShiftStatus status) {
        AssetSummaryDto disconnector = new AssetSummaryDto(DISC1, "DIS-0005", "HSA-NS5", CatenaryAssetType.DISCONNECTOR, 12L,
                new BigDecimal("12.000"), new BigDecimal("12.000"), null, true);
        return new ShiftDto(SHIFT1, "SH-000001", LocalDate.of(2026, 10, 5), new TeamSummaryDto(TEAM1, "EQ-01", "Brigada norte", "Base Norte"),
                "Base Norte", "DR-2", PossessionType.FULL, null, null, null, null, null, null, List.of(disconnector), null, null, 3L, List.of(12L),
                new BigDecimal("12.000"), new BigDecimal("14.000"), null, null, status, null, null, null);
    }

    private static TaskDto taskWithChecklist(MaintenanceTaskStatus status) {
        TaskDto base = task(TASK1, 1, status);
        CheckItemDto item = new CheckItemDto(ITEM1, "P-01", "Altura del hilo", "mm", new BigDecimal("5300"), new BigDecimal("5700"), true,
                null, null, null, null, null, 1, false, 1L);
        return new TaskDto(base.id(), base.orderId(), base.sequence(), base.description(), base.status(), null, base.asset(), SHIFT1, null,
                null, null, null, List.of(), base.taskTypeCodes(), List.of(item), null, null);
    }

    private void stubShifts(List<ShiftDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(shiftClient).search(any(ShiftFilter.class), anyInt(), anyInt(), anyList());
    }

    private void openShift(ShiftDto shift) {
        when(shiftClient.findById(shift.id())).thenReturn(shift);
        UI.getCurrent().navigate(ShiftDetailView.class, ShiftDetailView.parametersOf(shift.id()));
    }

    @Test
    void theShiftsAreFilteredInTheServerAndARowOpensItsDetail() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        when(maintenanceCatalogClient.teams()).thenReturn(List.of(new TeamDto(TEAM1, "EQ-01", "Brigada norte", null, null, true, Set.of(3L), null)));
        stubShifts(List.of(shiftOf(ShiftStatus.IN_PROGRESS)));
        when(shiftClient.findById(SHIFT1)).thenReturn(shiftOf(ShiftStatus.IN_PROGRESS));
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());

        UI.getCurrent().navigate(MaintenanceRoutes.SHIFTS);
        Grid<Object> grid = gridWithId("shifts-grid");
        assertEquals(List.of("SH-000001", "05/10/2026", "EQ-01 - Brigada norte", "Total", "VIA 1 (PAQ NORTE)", "12 - 14", "En curso"),
                GridKt._getFormattedRow(grid, 0).subList(0, 7));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("shifts-from")), LocalDate.of(2026, 10, 1));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("shifts-to")), LocalDate.of(2026, 10, 31));
        ComboBoxKt.selectByLabel(comboWithId("shifts-team"), "EQ-01 - Brigada norte");
        LocatorJ._setValue(comboWithId("shifts-track"), new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        LocatorJ._setValue(comboWithId("shifts-package"), new RefItem(3L, "PAQ NORTE"));
        LocatorJ._setValue(comboWithId("shifts-status"), ShiftStatus.IN_PROGRESS);
        LocatorJ._setValue(comboWithId("shifts-possession"), PossessionType.FULL);
        GridKt._size(grid);
        verify(shiftClient, atLeastOnce()).search(eq(new ShiftFilter(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), TEAM1, 12L, 3L,
                ShiftStatus.IN_PROGRESS, PossessionType.FULL)), eq(0), anyInt(), anyList());
        assertFalse(hasButton("shift-create"));

        Button open = LocatorJ._get(GridKt._getCellComponent(grid, 0, RowActions.OPEN_COLUMN), Button.class,
                spec -> spec.withId("open-" + SHIFT1));
        assertEquals("Abrir SH-000001", open.getTooltip().getText());
        LocatorJ._click(open);
        LocatorJ._get(H2.class, spec -> spec.withText("SH-000001 · 05/10/2026"));
        assertFalse(hasButton("shift-edit") || hasButton("shift-close"), "quien solo lee no ve botones de escritura");
    }

    @Test
    void aNewShiftNeedsATrackAndSendsItsDisconnectors() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.create(any())).thenReturn(shiftOf(ShiftStatus.PLANNED));
        when(shiftClient.findById(SHIFT1)).thenReturn(shiftOf(ShiftStatus.PLANNED));
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());

        UI.getCurrent().navigate(MaintenanceRoutes.SHIFTS);
        click("shift-create");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("shift-date")), LocalDate.of(2026, 10, 5));
        LocatorJ._setValue(comboWithId("shift-possession"), PossessionType.FULL);
        click(ShiftEditorDialog.SAVE_ID);
        verify(shiftClient, never()).create(any());

        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RefItem> tracks = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-tracks"));
        assertTrue(tracks.isInvalid(), "al menos una via");
        LocatorJ._setValue(tracks, Set.of(new RefItem(12L, "VIA 1 (PAQ NORTE)")));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<AssetSummaryDto> disconnectors = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-disconnectors"));
        LocatorJ._setValue(disconnectors, Set.of(shiftOf(ShiftStatus.PLANNED).blockingDisconnectors().getFirst()));
        click(ShiftEditorDialog.SAVE_ID);

        verify(shiftClient).create(new ShiftRequest(LocalDate.of(2026, 10, 5), null, null, null, PossessionType.FULL, null, null, Set.of(DISC1),
                null, null, null, Set.of(12L), null, null, null, null, null));
        LocatorJ._get(ShiftDetailView.class);
    }

    /**
     * Una ficha que no se puede leer por algo que no es un 404 no vuelve a la lista: dice por que y
     * ofrece volver o reintentar, como en mto-frontend. Lo mismo en las cuatro fichas.
     */
    @Test
    void aDetailThatCannotBeReadStaysWithItsReasonBackAndRetry() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(orderClient.findById(ORDER1)).thenThrow(maintenanceError(503, null, null))
                .thenReturn(orderOf(MaintenanceOrderStatus.PLANNED, MaintenanceOrderType.PREVENTIVE));

        UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(ORDER1));
        LocatorJ._get(OrderDetailView.class);
        Component failure = LocatorJ._get(Component.class, spec -> spec.withId("detail-load-failure"));
        LocatorJ._get(failure, Paragraph.class, spec -> spec.withText(
                "No se ha podido leer la orden: El servicio no esta disponible ahora mismo. Intentalo mas tarde."));
        assertEquals(1, NotificationsKt.getNotifications().size(), "y se notifica, con su referencia");
        assertTrue(LocatorJ._find(Span.class, spec -> spec.withId("order-status")).isEmpty(), "la cabecera vacia no se ensena");

        click("detail-load-retry");
        assertTrue(LocatorJ._find(Component.class, spec -> spec.withId("detail-load-failure")).isEmpty());
        assertEquals("Planificada", spanText("order-status"));

        when(inspectionClient.findById(INSPECTION1)).thenThrow(maintenanceError(503, null, null));
        UI.getCurrent().navigate(InspectionDetailView.class, InspectionDetailView.parametersOf(INSPECTION1));
        LocatorJ._get(InspectionDetailView.class);
        LocatorJ._get(Component.class, spec -> spec.withId("detail-load-failure"));
        when(defectClient.findById(DEFECT1)).thenThrow(maintenanceError(503, null, null));
        UI.getCurrent().navigate(DefectDetailView.class, DefectDetailView.parametersOf(DEFECT1));
        LocatorJ._get(DefectDetailView.class);
        LocatorJ._get(Component.class, spec -> spec.withId("detail-load-failure"));

        stubShifts(List.of());
        when(shiftClient.findById(SHIFT1)).thenThrow(maintenanceError(503, null, null));
        UI.getCurrent().navigate(ShiftDetailView.class, ShiftDetailView.parametersOf(SHIFT1));
        LocatorJ._get(Component.class, spec -> spec.withId("detail-load-failure"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Volver a la lista")));
        assertTrue(LocatorJ._find(ShiftDetailView.class).isEmpty(), "de vuelta a la lista de turnos");
    }

    @Test
    void theShiftDetailOffersWhatItsStateAdmitsAndStartsAndClosesTheShift() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(shiftClient.start(eq(SHIFT1), any())).thenReturn(shiftOf(ShiftStatus.IN_PROGRESS));
        when(shiftClient.close(eq(SHIFT1), any())).thenReturn(shiftOf(ShiftStatus.CLOSED));
        openShift(shiftOf(ShiftStatus.PLANNED));

        assertTrue(hasButton("shift-edit") && hasButton("shift-assign-tasks") && hasButton("shift-start") && hasButton("shift-cancel"));
        assertFalse(hasButton("shift-close"));
        click("shift-start");
        click(ShiftTransitionDialog.CONFIRM_ID);
        verify(shiftClient).start(SHIFT1, new StartShiftRequest(null, null));
        assertEquals("En curso", spanText("shift-status"));
        assertTrue(hasButton("shift-close"));
        assertFalse(hasButton("shift-start"));

        click("shift-close");
        LocatorJ._setValue(LocatorJ._get(IntegerField.class, spec -> spec.withId("shift-transition-net-minutes")), 240);
        click(ShiftTransitionDialog.CONFIRM_ID);
        verify(shiftClient).close(SHIFT1, new CloseShiftRequest(null, null, 240, null));
        assertEquals("Cerrado", spanText("shift-status"));
        assertFalse(hasButton("shift-edit") || hasButton("shift-close") || hasButton("shift-cancel"), "un turno cerrado no ofrece nada");
    }

    /** Quitar todos los seccionadores de un turno los vacia: viajan a null en el merge-patch, como en mto-frontend. */
    @Test
    void emptyingTheDisconnectorsOfAShiftClearsThem() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(shiftClient.update(eq(SHIFT1), any())).thenReturn(shiftOf(ShiftStatus.PLANNED));
        openShift(shiftOf(ShiftStatus.PLANNED));

        click("shift-edit");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<AssetSummaryDto> disconnectors = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-disconnectors"));
        assertEquals(1, disconnectors.getValue().size());
        LocatorJ._setValue(disconnectors, Set.of());
        click(ShiftEditorDialog.SAVE_ID);

        verify(shiftClient).update(eq(SHIFT1), argThat(patch -> patch.cleared().equals(Set.of("blockingDisconnectorIds"))
                && patch.values().blockingDisconnectorIds() == null && patch.values().trackIds() == null));
    }

    /** Lo que el servicio dice de un campo cae en el suyo, tambien en los dialogos sin Binder; el dialogo sigue abierto. */
    @Test
    void theFieldErrorsOfTheServiceLandOnTheirFieldsInTheMaintenanceDialogs() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        ApiProblem future = new ApiProblem(null, "Bad Request", 400, "Validation failed", null, "VAL-001", null, null, null, false,
                List.of(new ApiFieldError("actualStart", null, "must not be in the future")), null);
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(shiftClient.start(eq(SHIFT1), any())).thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, future, "corr-m2", null,
                "POST /api/maintenance/shifts/" + SHIFT1 + "/start"));
        openShift(shiftOf(ShiftStatus.PLANNED));
        click("shift-start");
        click(ShiftTransitionDialog.CONFIRM_ID);
        DateTimePicker when = LocatorJ._get(DateTimePicker.class, spec -> spec.withId("shift-transition-when"));
        assertTrue(when.isInvalid(), "actualStart es el inicio real del dialogo");
        assertEquals("must not be in the future", when.getErrorMessage());
        assertTrue(NotificationsKt.getNotifications().isEmpty());
        assertFalse(LocatorJ._find(ShiftTransitionDialog.class).isEmpty());

        ApiProblem material = new ApiProblem(null, "Bad Request", 400, "Validation failed", null, "VAL-001", null, null, null, false,
                List.of(new ApiFieldError("plannedQuantity", null, "must be greater than 0"),
                        new ApiFieldError("hasMaterialReference", null, "materialId or materialReference is required")), null);
        when(orderClient.registerMaterial(eq(ORDER1), any())).thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, material, "corr-m3", null,
                "POST /api/maintenance/orders/" + ORDER1 + "/materials"));
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of());
        click("material-add");
        ComboBox<MaterialSummaryDto> picked = comboWithId("material-material");
        LocatorJ._setValue(picked, new MaterialSummaryDto(MAT1, "MAT-001", "Pendola", "ud", true));
        LocatorJ._setValue(comboWithId("material-warehouse"), new WarehouseSummaryDto(WH1, "WH-000", "Central", true));
        BigDecimalField planned = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-planned"));
        LocatorJ._setValue(planned, new BigDecimal("4"));
        click(MaterialUsageDialog.SAVE_ID);
        assertEquals("must be greater than 0", planned.getErrorMessage());
        assertTrue(planned.isInvalid());
        assertTrue(picked.isInvalid(), "la referencia del material es el material del dialogo");
        assertTrue(NotificationsKt.getNotifications().isEmpty());
    }

    /** El texto de un Span por su id. */
    private static String spanText(String id) {
        return LocatorJ._get(Span.class, spec -> spec.withId(id)).getText();
    }

    @Test
    void assigningTasksReportsTheOnesTheServiceRefused() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        OrderDto open = orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE);
        OrderDto done = order(UUID.randomUUID(), "MO-000002", MaintenanceOrderStatus.COMPLETED, 12L, 3L);
        when(orderClient.search(eq(OrderFilter.onTrack(12L)), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(open, done), 0, 100));
        TaskDto first = task(TASK1, 1, MaintenanceTaskStatus.PENDING);
        TaskDto second = task(TASK2, 2, MaintenanceTaskStatus.PENDING);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(first, second, task(UUID.randomUUID(), 3, MaintenanceTaskStatus.COMPLETED)));
        when(shiftClient.assignTask(SHIFT1, TASK1)).thenReturn(first);
        when(shiftClient.assignTask(SHIFT1, TASK2)).thenThrow(maintenanceError(409, "SHF-001",
                "Shift SH-000001 has partial possession; the task includes work that needs full track possession"));
        openShift(shiftOf(ShiftStatus.IN_PROGRESS));

        click("shift-assign-tasks");
        ComboBox<OrderDto> order = comboWithId("assign-tasks-order");
        assertEquals(List.of("MO-000001 · Revision tramo 12 (en curso)"), ComboBoxKt.getSuggestions(order), "solo las ordenes abiertas de la via");
        ComboBoxKt.selectByLabel(order, "MO-000001 · Revision tramo 12 (en curso)");
        @SuppressWarnings("unchecked")
        Grid<TaskDto> tasks = LocatorJ._get(Grid.class, spec -> spec.withId("assign-tasks-grid"));
        assertEquals(2, GridKt._size(tasks), "solo las pendientes");
        tasks.asMultiSelect().select(first, second);
        click(AssignTasksDialog.ASSIGN_ID);

        verify(shiftClient).assignTask(SHIFT1, TASK1);
        verify(shiftClient).assignTask(SHIFT1, TASK2);
        NotificationsKt.expectNotifications("Asignadas: 1. Rechazadas: tarea 2 (El turno no admite ese trabajo. Shift SH-000001 has partial "
                + "possession; the task includes work that needs full track possession)");
    }

    @Test
    void aTaskIsStartedCheckedAndCompletedInTheShiftWithItsDefectsAndMaterials() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of(taskWithChecklist(MaintenanceTaskStatus.PENDING)));
        when(orderClient.findById(ORDER1)).thenReturn(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));
        when(orderClient.startTask(eq(ORDER1), eq(TASK1), any())).thenReturn(taskWithChecklist(MaintenanceTaskStatus.IN_PROGRESS));
        when(orderClient.updateCheckItem(eq(ORDER1), eq(TASK1), eq(ITEM1), any()))
                .thenThrow(maintenanceError(422, "INS-001", "Item P-01 is out of range (5250 mm) and cannot be OK unless adjusted into range"))
                .thenReturn(taskWithChecklist(MaintenanceTaskStatus.PENDING));
        when(orderClient.completeTask(eq(ORDER1), eq(TASK1), any())).thenReturn(taskWithChecklist(MaintenanceTaskStatus.COMPLETED));
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        openShift(shiftOf(ShiftStatus.IN_PROGRESS));

        Grid<Object> tasks = gridWithId("shift-tasks-grid");
        assertEquals("MO-000001", GridKt._getFormattedRow(tasks, 0).getFirst(), "el codigo de la orden, pedido una vez");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("shift-task-start-" + TASK1)));
        verify(orderClient).startTask(ORDER1, TASK1, new StartTaskRequest(SHIFT1, null));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("shift-task-checklist-" + TASK1)));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("check-measured-" + ITEM1)), new BigDecimal("5250"));
        LocatorJ._setValue(comboWithId("check-result-" + ITEM1), CheckItemResult.OK);
        click("check-save-" + ITEM1);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "La inspeccion o su checklist no admiten esta operacion. Item P-01 is out of range (5250 mm) and cannot be OK unless adjusted into range"));
        LocatorJ._setValue(comboWithId("check-result-" + ITEM1), CheckItemResult.DEFECT);
        click("check-save-" + ITEM1);
        verify(orderClient).updateCheckItem(ORDER1, TASK1, ITEM1,
                MergePatch.of(new CheckItemUpdateRequest(new BigDecimal("5250"), null, null, CheckItemResult.DEFECT, null), 1L));
        LocatorJ._get(com.vaadin.flow.component.dialog.Dialog.class).close();

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("shift-task-complete-" + TASK1)));
        click("complete-task-defect-add");
        LocatorJ._setValue(comboWithId("inline-defect-severity"), DefectSeverity.HIGH);
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("inline-defect-description")), "Pendola rota");
        click("inline-defect-add");
        click("complete-task-material-add");
        MaterialSummaryDto material = new MaterialSummaryDto(MAT1, "MAT-001", "Pendola", "ud", true);
        WarehouseSummaryDto warehouse = new WarehouseSummaryDto(WH1, "WH-000", "Central", true);
        LocatorJ._setValue(comboWithId("material-line-material"), material);
        LocatorJ._setValue(comboWithId("material-line-warehouse"), warehouse);
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-line-quantity")), new BigDecimal("2"));
        click("material-line-add");
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("complete-task-work-complete")), false);
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("complete-task-repair-date")), LocalDate.of(2026, 10, 12));
        click(CompleteTaskDialog.CONFIRM_ID);

        verify(orderClient).completeTask(ORDER1, TASK1, new CompleteTaskRequest(SHIFT1, null, null, null, false, LocalDate.of(2026, 10, 12),
                List.of(new InlineDefectRequest(DefectSeverity.HIGH, "Pendola rota", null, null, null)),
                List.of(new TaskMaterialRequest(MAT1, null, WH1, new BigDecimal("2"), "ud")), null));
        assertTrue(LocatorJ._find(CompleteTaskDialog.class).isEmpty());
    }

    /**
     * Un KP de mantenimiento es numeric(12,3): lo que no cabe no llega al servicio desde ningun
     * dialogo, como en mto-frontend. El KP final de un defecto puede ser el inicial (un defecto en un
     * punto), nunca menor.
     */
    @Test
    void aMaintenanceKpThatDoesNotFitTheServiceColumnNeverReachesIt() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        click("asset-create");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("asset-code")), "TS-0003");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("asset-name")), "Tramo 14");
        LocatorJ._setValue(comboWithId("asset-track"), new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        BigDecimalField assetStart = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("asset-start-kp"));
        BigDecimalField assetEnd = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("asset-end-kp"));
        LocatorJ._setValue(assetStart, new BigDecimal("13.4505"));
        LocatorJ._setValue(assetEnd, new BigDecimal("1234567890"));
        IntegerField interval = LocatorJ._get(IntegerField.class, spec -> spec.withId("asset-interval"));
        LocatorJ._setValue(interval, 0);
        click(AssetEditorDialog.SAVE_ID);
        assertEquals("Tiene que ser mayor que cero", interval.getErrorMessage(), "un intervalo de cero dias no es un plan");
        assertEquals("Como mucho 3 decimales", assetStart.getErrorMessage());
        assertEquals("Como mucho 9 cifras enteras", assetEnd.getErrorMessage());
        verify(assetClient, never()).create(any());
        LocatorJ._get(AssetEditorDialog.class).close();

        UI.getCurrent().navigate(MaintenanceRoutes.SHIFTS);
        click("shift-create");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("shift-date")), LocalDate.of(2026, 10, 5));
        LocatorJ._setValue(comboWithId("shift-possession"), PossessionType.FULL);
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RefItem> tracks = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-tracks"));
        LocatorJ._setValue(tracks, Set.of(new RefItem(12L, "VIA 1 (PAQ NORTE)")));
        BigDecimalField shiftStart = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("shift-start-kp"));
        LocatorJ._setValue(shiftStart, new BigDecimal("12.0001"));
        click(ShiftEditorDialog.SAVE_ID);
        assertEquals("Como mucho 3 decimales", shiftStart.getErrorMessage());
        verify(shiftClient, never()).create(any());
        LocatorJ._get(ShiftEditorDialog.class).close();

        UI.getCurrent().navigate(MaintenanceRoutes.INSPECTIONS);
        click("inspection-create");
        LocatorJ._setValue(comboWithId("inspection-asset"), profileSummary());
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("inspection-date")), LocalDate.of(2026, 9, 20));
        BigDecimalField inspectionKp = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("inspection-kp"));
        LocatorJ._setValue(inspectionKp, new BigDecimal("-12.2705"));
        click(InspectionEditorDialog.SAVE_ID);
        assertEquals("Como mucho 3 decimales", inspectionKp.getErrorMessage());
        verify(inspectionClient, never()).create(any());
        LocatorJ._get(InspectionEditorDialog.class).close();

        when(defectClient.create(any())).thenReturn(defectOf(DefectStatus.OPEN));
        when(defectClient.findById(DEFECT1)).thenReturn(defectOf(DefectStatus.OPEN));
        when(defectClient.history(DEFECT1)).thenReturn(List.of());
        UI.getCurrent().navigate(MaintenanceRoutes.DEFECTS);
        click("defect-create");
        LocatorJ._setValue(comboWithId("defect-asset"), profileSummary());
        LocatorJ._setValue(comboWithId("defect-severity"), DefectSeverity.HIGH);
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("defect-description")), "Pendola rota");
        BigDecimalField defectStart = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("defect-start-kp"));
        BigDecimalField defectEnd = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("defect-end-kp"));
        LocatorJ._setValue(defectStart, new BigDecimal("12.270"));
        LocatorJ._setValue(defectEnd, new BigDecimal("12.100"));
        click(DefectEditorDialog.SAVE_ID);
        assertEquals("El KP final no puede ser menor que el inicial", defectEnd.getErrorMessage());
        verify(defectClient, never()).create(any());
        LocatorJ._setValue(defectEnd, new BigDecimal("12.270"));
        click(DefectEditorDialog.SAVE_ID);
        verify(defectClient).create(argThat(request -> new BigDecimal("12.270").equals(request.startKp())
                && new BigDecimal("12.270").equals(request.endKp())));
        LocatorJ._get(DefectDetailView.class);
    }

    /**
     * Las cantidades de mantenimiento son numeric(19,6) y los minutos netos no son negativos, tambien
     * en los dialogos sin Binder. Una medida que el campo no pudo leer no viaja como vaciada: borraria
     * la que habia.
     */
    @Test
    void maintenanceQuantitiesMinutesAndMeasuresAreCheckedBeforeCalling() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        TaskDto base = taskWithChecklist(MaintenanceTaskStatus.IN_PROGRESS);
        CheckItemDto measuredBefore = new CheckItemDto(ITEM1, "P-01", "Altura del hilo", "mm", new BigDecimal("5300"), new BigDecimal("5700"),
                true, new BigDecimal("5400"), false, null, CheckItemResult.OK, null, 1, false, 1L);
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of(new TaskDto(base.id(), base.orderId(), base.sequence(), base.description(),
                base.status(), null, base.asset(), SHIFT1, null, null, null, null, List.of(), base.taskTypeCodes(), List.of(measuredBefore),
                null, null)));
        when(orderClient.findById(ORDER1)).thenReturn(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        openShift(shiftOf(ShiftStatus.IN_PROGRESS));

        click("shift-close");
        IntegerField netMinutes = LocatorJ._get(IntegerField.class, spec -> spec.withId("shift-transition-net-minutes"));
        LocatorJ._setValue(netMinutes, -30);
        click(ShiftTransitionDialog.CONFIRM_ID);
        assertEquals("No puede ser negativo", netMinutes.getErrorMessage());
        verify(shiftClient, never()).close(any(), any());
        LocatorJ._get(ShiftTransitionDialog.class).close();

        Grid<Object> tasks = gridWithId("shift-tasks-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("shift-task-checklist-" + TASK1)));
        BigDecimalField measured = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("check-measured-" + ITEM1));
        LocatorJ._setValue(measured, new BigDecimal("5250.0001"));
        click("check-save-" + ITEM1);
        assertEquals("Como mucho 3 decimales", measured.getErrorMessage());
        LocatorJ._setValue(measured, null);
        typeFromTheBrowser(measured, "5,250,5");
        click("check-save-" + ITEM1);
        assertEquals(Numbers.UNREADABLE, measured.getErrorMessage());
        verify(orderClient, never()).updateCheckItem(any(), any(), any(), any());
        LocatorJ._get(CheckItemsDialog.class).close();

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("shift-task-complete-" + TASK1)));
        click("complete-task-material-add");
        LocatorJ._setValue(comboWithId("material-line-material"), new MaterialSummaryDto(MAT1, "MAT-001", "Pendola", "ud", true));
        LocatorJ._setValue(comboWithId("material-line-warehouse"), new WarehouseSummaryDto(WH1, "WH-000", "Central", true));
        BigDecimalField used = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-line-quantity"));
        LocatorJ._setValue(used, new BigDecimal("2.1234567"));
        click("material-line-add");
        assertEquals("Como mucho 6 decimales", used.getErrorMessage());
        assertFalse(LocatorJ._find(BigDecimalField.class, spec -> spec.withId("material-line-quantity")).isEmpty(), "la linea sigue abierta");
        LocatorJ._get(CompleteTaskDialog.class).close();
        verify(orderClient, never()).completeTask(any(), any(), any());

        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of(line(LINE_FAILED, StockSyncStatus.FAILED, null)));
        click("material-add");
        LocatorJ._setValue(comboWithId("material-material"), new MaterialSummaryDto(MAT1, "MAT-001", "Pendola", "ud", true));
        LocatorJ._setValue(comboWithId("material-warehouse"), new WarehouseSummaryDto(WH1, "WH-000", "Central", true));
        BigDecimalField planned = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-planned"));
        LocatorJ._setValue(planned, new BigDecimal("4.1234567"));
        click(MaterialUsageDialog.SAVE_ID);
        assertEquals("Como mucho 6 decimales", planned.getErrorMessage());
        verify(orderClient, never()).registerMaterial(any(), any());
        LocatorJ._get(MaterialUsageDialog.class).close();

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("order-materials-grid"), 0, "actions"), Button.class,
                spec -> spec.withId("material-edit-" + LINE_FAILED)));
        BigDecimalField consumed = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-consumed"));
        LocatorJ._setValue(consumed, new BigDecimal("-1"));
        click(MaterialUsageDialog.SAVE_ID);
        assertEquals("No puede ser negativo", consumed.getErrorMessage());
        verify(orderClient, never()).updateMaterial(any(), any(), any());
    }

    /**
     * Lo que la persona escribe y el campo no sabe leer, como llega del navegador: el campo se queda
     * sin valor, pero con el texto. Puesto desde el servidor, el campo lo volveria a leer y lo borraria.
     */
    private static void typeFromTheBrowser(BigDecimalField field, String text) {
        try {
            field.getElement().getNode().getFeature(ElementPropertyMap.class).deferredUpdateFromClient("value", text).run();
        } catch (PropertyChangeDeniedException denied) {
            throw new AssertionError(denied);
        }
    }

    @Test
    void completingFromTheOrderPicksAnInProgressShiftOfItsTrackAndMaterialsNeedStockRead() {
        loginAs("mantenimiento.sin-almacen", "ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ");
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(task(TASK1, 1, MaintenanceTaskStatus.PENDING)));
        when(shiftClient.search(eq(ShiftFilter.inProgressOn(12L)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(shiftOf(ShiftStatus.IN_PROGRESS)), 0, 50));
        when(orderClient.completeTask(eq(ORDER1), eq(TASK1), any())).thenReturn(task(TASK1, 1, MaintenanceTaskStatus.COMPLETED));
        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("order-tasks-grid"), 0, "actions"), Button.class,
                spec -> spec.withId("task-complete-" + TASK1)));
        assertFalse(hasButton("complete-task-material-add"), "sin stock-read no se eligen materiales");
        LocatorJ._get(Paragraph.class, spec -> spec.withText("Elegir materiales pide leer el almacen (stock-read)."));
        click(CompleteTaskDialog.CONFIRM_ID);
        verify(orderClient, never()).completeTask(any(), any(), any());

        ComboBoxKt.selectByLabel(comboWithId("complete-task-shift"), "SH-000001 · 05/10/2026 · EQ-01 - Brigada norte");
        click(CompleteTaskDialog.CONFIRM_ID);
        verify(shiftClient, atLeastOnce()).search(eq(ShiftFilter.inProgressOn(12L)), eq(0), anyInt(), anyList());
        verify(orderClient).completeTask(ORDER1, TASK1, new CompleteTaskRequest(SHIFT1, null, null, null, null, null, null, null, null));
    }

    /** Al completar, los tipos que el catalogo no trae tambien se conservan si se cambian los demas. */
    @Test
    void completingATaskKeepsTheTypesTheCatalogueDoesNotName() {
        loginAs("mantenimiento.sin-almacen", "ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ");
        List<TaskTypeDto> catalogue = twoTaskTypes();
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(catalogue);
        TaskDto base = task(TASK1, 1, MaintenanceTaskStatus.PENDING);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(new TaskDto(TASK1, ORDER1, 1, base.description(), MaintenanceTaskStatus.PENDING, null,
                base.asset(), null, null, null, null, null, List.of(), List.of("RP-99", "RG-01"), List.of(), null, 2L)));
        when(shiftClient.search(eq(ShiftFilter.inProgressOn(12L)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(shiftOf(ShiftStatus.IN_PROGRESS)), 0, 50));
        when(orderClient.completeTask(eq(ORDER1), eq(TASK1), any())).thenReturn(task(TASK1, 1, MaintenanceTaskStatus.COMPLETED));
        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("order-tasks-grid"), 0, "actions"), Button.class,
                spec -> spec.withId("task-complete-" + TASK1)));
        ComboBoxKt.selectByLabel(comboWithId("complete-task-shift"), "SH-000001 · 05/10/2026 · EQ-01 - Brigada norte");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<TaskTypeDto> types = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("complete-task-types"));
        LocatorJ._setValue(types, Set.copyOf(catalogue));
        click(CompleteTaskDialog.CONFIRM_ID);

        verify(orderClient).completeTask(eq(ORDER1), eq(TASK1), argThat(request -> request.taskTypeCodes() != null
                && Set.copyOf(request.taskTypeCodes()).equals(Set.of("RG-01", "RG-04", "RP-99"))));
    }

    // --- Mantenimiento: inspecciones y defectos -----------------------------------------------------

    private static final UUID INSPECTION1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000041");
    private static final UUID DEFECT1 = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000042");
    private static final UUID INSPECTION_ITEM = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000043");

    private static AssetSummaryDto profileSummary() {
        return new AssetSummaryDto(ASSET_SYNCED, "PRF-0001", "12-2.27", CatenaryAssetType.PROFILE, 12L, new BigDecimal("12.270"),
                new BigDecimal("12.270"), "S-3", true);
    }

    private static InspectionDto inspectionOf(InspectionResult result, UUID generatedDefect, UUID generatedOrder) {
        CheckItemDto item = new CheckItemDto(INSPECTION_ITEM, "P-01", "Altura del hilo", "mm", new BigDecimal("5300"), new BigDecimal("5700"),
                true, null, null, null, null, null, 1, false, 1L);
        return new InspectionDto(INSPECTION1, "INS-000001", profileSummary(), 3L, 12L, null, new BigDecimal("12.270"), LocalDate.of(2026, 9, 20),
                "ana", InspectionKind.TECHNICAL, null, result, null, "Pendola rota", null, generatedDefect, generatedOrder, null, null,
                List.of(item), null, null);
    }

    private static DefectDto defectOf(DefectStatus status) {
        return new DefectDto(DEFECT1, "DEF-000001", profileSummary(), INSPECTION1, null, DefectSeverity.HIGH, status, "Pendola rota", null,
                Instant.parse("2026-09-20T00:00:00Z"), null, null, null, 3L, 12L, null, new BigDecimal("12.270"), new BigDecimal("12.270"), null,
                null, null, null, null, List.of(), null, null);
    }

    private void openInspection(InspectionDto inspection) {
        when(inspectionClient.findById(inspection.id())).thenReturn(inspection);
        UI.getCurrent().navigate(InspectionDetailView.class, InspectionDetailView.parametersOf(inspection.id()));
    }

    private void openDefect(DefectDto defect) {
        when(defectClient.findById(defect.id())).thenReturn(defect);
        when(defectClient.history(defect.id())).thenReturn(List.of());
        UI.getCurrent().navigate(DefectDetailView.class, DefectDetailView.parametersOf(defect.id()));
    }

    @Test
    void theInspectionsAreFilteredAndADetailOffersToCreateWhatItFoundOrLinksToIt() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        doAnswer(call -> page(List.of(inspectionOf(InspectionResult.MAJOR_DEFECT, null, null)), call.getArgument(1), call.getArgument(2)))
                .when(inspectionClient).search(any(InspectionFilter.class), anyInt(), anyInt(), anyList());
        when(inspectionClient.findById(INSPECTION1)).thenReturn(inspectionOf(InspectionResult.MAJOR_DEFECT, null, null));

        UI.getCurrent().navigate(MaintenanceRoutes.INSPECTIONS);
        Grid<Object> grid = gridWithId("inspections-grid");
        LocatorJ._setValue(comboWithId("inspections-result"), InspectionResult.MAJOR_DEFECT);
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("inspections-from")), LocalDate.of(2026, 9, 1));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("inspections-inspector")), "ana");
        GridKt._size(grid);
        verify(inspectionClient, atLeastOnce()).search(eq(new InspectionFilter(InspectionResult.MAJOR_DEFECT, null, null, null,
                LocalDate.of(2026, 9, 1), null, "ana", null)), eq(0), anyInt(), anyList());
        GridKt._doubleClickItem(grid, 0, 1, false, false, false, false);

        LocatorJ._get(InspectionDetailView.class);
        assertTrue(hasButton("inspection-create-defect") && hasButton("inspection-create-order"));
        assertFalse(hasButton("inspection-defect-link") || hasButton("inspection-order-link"));
        assertEquals("P-01", GridKt._getFormattedRow(gridWithId("inspection-items-grid"), 0).getFirst());

        UI.getCurrent().navigate(MaintenanceRoutes.INSPECTIONS);
        openInspection(inspectionOf(InspectionResult.MAJOR_DEFECT, DEFECT1, ORDER1));
        assertTrue(hasButton("inspection-defect-link") && hasButton("inspection-order-link"), "lo generado se enlaza, no se vuelve a ofrecer");
        assertFalse(hasButton("inspection-create-defect") || hasButton("inspection-create-order"));
        when(defectClient.findById(DEFECT1)).thenReturn(defectOf(DefectStatus.OPEN));
        click("inspection-defect-link");
        LocatorJ._get(DefectDetailView.class);

        UI.getCurrent().navigate(MaintenanceRoutes.INSPECTIONS);
        openInspection(inspectionOf(InspectionResult.OK, null, null));
        assertFalse(hasButton("inspection-create-defect") || hasButton("inspection-create-order"), "una inspeccion correcta no genera nada");
    }

    @Test
    void anInspectionCreatesItsDefectWithForceWhenMinorAndItsOrderOpensTheOrder() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(inspectionClient.createDefect(eq(INSPECTION1), any())).thenReturn(defectOf(DefectStatus.OPEN));
        when(inspectionClient.createCorrectiveOrder(eq(INSPECTION1), any())).thenReturn(orderOf(MaintenanceOrderStatus.DRAFT,
                MaintenanceOrderType.CORRECTIVE));
        when(orderClient.findById(ORDER1)).thenReturn(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.CORRECTIVE));
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        openInspection(inspectionOf(InspectionResult.MINOR_DEFECT, null, null));

        click("inspection-create-defect");
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("inspection-defect-force")), true);
        click("inspection-defect-confirm");
        verify(inspectionClient).createDefect(INSPECTION1, new CreateDefectFromInspectionRequest(null, null, null, true));
        verify(inspectionClient, atLeast(2)).findById(INSPECTION1);

        click("inspection-create-order");
        LocatorJ._setValue(comboWithId("inspection-order-priority"), MaintenancePriority.HIGH);
        click("inspection-order-confirm");
        verify(inspectionClient).createCorrectiveOrder(INSPECTION1, new CreateCorrectiveOrderRequest(null, null, MaintenancePriority.HIGH, null, null));
        LocatorJ._get(OrderDetailView.class);
    }

    /** Una inspeccion tiene siempre su tipo: vaciarlo no se guarda, como en mto-frontend (visual por defecto). */
    @Test
    void theKindOfAnInspectionCannotBeEmptied() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        openInspection(inspectionOf(InspectionResult.MAJOR_DEFECT, null, ORDER1));

        click("inspection-edit");
        ComboBox<InspectionKind> kind = comboWithId("inspection-kind");
        assertNotNull(kind.getValue());
        LocatorJ._setValue(kind, null);
        click(InspectionEditorDialog.SAVE_ID);

        assertTrue(kind.isInvalid());
        verify(inspectionClient, never()).update(any(), any());
    }

    @Test
    void anInspectionIsCreatedFromAnInspectionOrderAndItsItemsAreAnswered() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        InspectionDto created = inspectionOf(InspectionResult.OK, null, null);
        when(inspectionClient.create(any())).thenReturn(created);
        when(inspectionClient.findById(INSPECTION1)).thenReturn(created);
        when(inspectionClient.updateItem(eq(INSPECTION1), eq(INSPECTION_ITEM), any())).thenReturn(created);
        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.INSPECTION));

        selectTab(3);
        click("order-inspection-create");
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("inspection-asset")).isEmpty(), "el activo es el de la orden");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("inspection-date")), LocalDate.of(2026, 9, 20));
        click(InspectionEditorDialog.SAVE_ID);
        verify(inspectionClient).create(new InspectionRequest(ASSET1, LocalDate.of(2026, 9, 20), null, InspectionKind.VISUAL, InspectionResult.OK,
                null, null, null, null, ORDER1, null));
        verify(inspectionClient, atLeastOnce()).search(eq(InspectionFilter.ofOrder(ORDER1)), eq(0), anyInt(), anyList());

        openInspection(created);
        click("inspection-items");
        LocatorJ._setValue(comboWithId("check-result-" + INSPECTION_ITEM), CheckItemResult.OK);
        click("check-save-" + INSPECTION_ITEM);
        verify(inspectionClient).updateItem(INSPECTION1, INSPECTION_ITEM, MergePatch.of(new CheckItemUpdateRequest(null, null, null, CheckItemResult.OK, null), 1L));
    }

    @Test
    void theDefectsAreFilteredAndTheDetailOffersWhatItsStateAdmits() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        doAnswer(call -> page(List.of(defectOf(DefectStatus.OPEN)), call.getArgument(1), call.getArgument(2)))
                .when(defectClient).search(any(DefectFilter.class), anyInt(), anyInt(), anyList());
        when(defectClient.findById(DEFECT1)).thenReturn(defectOf(DefectStatus.OPEN));
        when(defectClient.history(DEFECT1)).thenReturn(List.of(new StatusHistoryDto(UUID.randomUUID(), null, "OPEN",
                Instant.parse("2026-09-20T08:00:00Z"), "ana", "Created from inspection INS-000001")));

        UI.getCurrent().navigate(MaintenanceRoutes.DEFECTS);
        Grid<Object> grid = gridWithId("defects-grid");
        LocatorJ._setValue(comboWithId("defects-severity"), DefectSeverity.HIGH);
        LocatorJ._setValue(comboWithId("defects-status"), DefectStatus.OPEN);
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("defects-from")), LocalDate.of(2026, 9, 1));
        GridKt._size(grid);
        verify(defectClient, atLeastOnce()).search(eq(new DefectFilter(DefectSeverity.HIGH, DefectStatus.OPEN, null, null, null, null,
                Formats.startOfDay(LocalDate.of(2026, 9, 1)), null)), eq(0), anyInt(), anyList());
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, RowActions.OPEN_COLUMN), Button.class));

        LocatorJ._get(DefectDetailView.class);
        assertEquals("Abierto", GridKt._getFormattedRow(gridWithId("defect-history-grid"), 0).get(2));
        assertTrue(hasButton("defect-edit") && hasButton("defect-link-order") && hasButton("defect-inspection-link"));
        assertFalse(hasButton("defect-resolve") || hasButton("defect-discard"), "resolver y descartar piden supervise");

        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        UI.getCurrent().navigate(MaintenanceRoutes.DEFECTS);
        openDefect(defectOf(DefectStatus.OPEN));
        assertTrue(hasButton("defect-resolve") && hasButton("defect-discard"));
        assertFalse(hasButton("defect-close"));
        UI.getCurrent().navigate(MaintenanceRoutes.DEFECTS);
        openDefect(defectOf(DefectStatus.RESOLVED));
        assertTrue(hasButton("defect-close") && hasButton("defect-edit"));
        assertFalse(hasButton("defect-resolve") || hasButton("defect-discard") || hasButton("defect-link-order"));
    }

    @Test
    void aDefectIsLinkedResolvedAndDiscardedWithItsReasons() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        OrderDto open = orderOf(MaintenanceOrderStatus.PLANNED, MaintenanceOrderType.CORRECTIVE);
        when(orderClient.search(eq(OrderFilter.onTrack(12L)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(open, order(UUID.randomUUID(), "MO-000009", MaintenanceOrderStatus.CANCELLED, 12L, 3L)), 0, 100));
        when(shiftClient.search(eq(new ShiftFilter(null, null, null, 12L, null, null, null)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(shiftOf(ShiftStatus.CLOSED)), 0, 50));
        when(defectClient.linkOrder(DEFECT1, ORDER1)).thenReturn(defectOf(DefectStatus.IN_PROGRESS));
        when(defectClient.resolve(eq(DEFECT1), any()))
                .thenThrow(maintenanceError(409, "TRN-001", "Defect DEF-000001 is linked to order MO-000001 which is PLANNED"))
                .thenReturn(defectOf(DefectStatus.RESOLVED));
        when(defectClient.discard(eq(DEFECT1), any())).thenReturn(defectOf(DefectStatus.DISCARDED));
        openDefect(defectOf(DefectStatus.OPEN));

        click("defect-link-order");
        ComboBox<OrderDto> order = comboWithId("defect-link-order");
        assertEquals(List.of("MO-000001 · Revision tramo 12 (planificada)"), ComboBoxKt.getSuggestions(order), "solo las abiertas de su via");
        ComboBoxKt.selectByLabel(order, "MO-000001 · Revision tramo 12 (planificada)");
        click("defect-link-confirm");
        verify(defectClient).linkOrder(DEFECT1, ORDER1);
        assertEquals("En curso", spanText("defect-status"));

        click("defect-resolve");
        click("defect-resolve-confirm");
        verify(defectClient, never()).resolve(any(), any());
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("defect-resolve-notes")), "Pendola cambiada");
        click("defect-resolve-confirm");
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "El estado actual no permite esta operacion. Defect DEF-000001 is linked to order MO-000001 which is PLANNED"));
        ComboBoxKt.selectByLabel(comboWithId("defect-resolve-shift"), "SH-000001 · 05/10/2026");
        click("defect-resolve-confirm");
        verify(defectClient).resolve(DEFECT1, new ResolveDefectRequest("Pendola cambiada", SHIFT1, null, null));
        assertEquals("Resuelto", spanText("defect-status"));

        UI.getCurrent().navigate(MaintenanceRoutes.DEFECTS);
        openDefect(defectOf(DefectStatus.OPEN));
        click("defect-discard");
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("reason-text")), "Duplicado");
        click("reason-confirm");
        verify(defectClient).discard(DEFECT1, new ReasonRequest("Duplicado"));
        assertEquals("Descartado", spanText("defect-status"));
    }

    /** En las pestanas Defectos e Inspecciones de una orden, una fila se abre con su boton, como en mto-frontend. */
    @Test
    void anOrdersDefectAndInspectionRowsOpenWithTheirButton() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        DefectDto defect = defectOf(DefectStatus.IN_PROGRESS);
        InspectionDto inspection = inspectionOf(InspectionResult.OK, null, null);
        when(defectClient.search(eq(DefectFilter.ofOrder(ORDER1)), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(defect), 0, 100));
        when(inspectionClient.search(eq(InspectionFilter.ofOrder(ORDER1)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(inspection), 0, 100));
        when(defectClient.findById(defect.id())).thenReturn(defect);
        when(defectClient.history(defect.id())).thenReturn(List.of());
        when(inspectionClient.findById(inspection.id())).thenReturn(inspection);
        OrderDto order = orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.CORRECTIVE);
        openOrder(order);

        selectTab(2);
        Grid<Object> defects = gridWithId("order-defects-grid");
        GridKt._clickItem(defects, 0, 1, false, false, false, false);
        GridKt._doubleClickItem(defects, 0, 1, false, false, false, false);
        assertTrue(LocatorJ._find(DefectDetailView.class).isEmpty(), "ni el clic ni el doble clic: su boton, como en mto-frontend");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(defects, 0, RowActions.OPEN_COLUMN), Button.class,
                spec -> spec.withId("open-" + defect.id())));
        LocatorJ._get(DefectDetailView.class);

        openOrder(order);
        selectTab(3);
        Grid<Object> inspections = gridWithId("order-inspections-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(inspections, 0, RowActions.OPEN_COLUMN), Button.class,
                spec -> spec.withId("open-" + inspection.id())));
        LocatorJ._get(InspectionDetailView.class);
    }

    @Test
    void anOrderListsItsDefectsCreatesOneLinkedToItAndLinksToItsOrigin() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(defectClient.search(eq(DefectFilter.ofOrder(ORDER1)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(defectOf(DefectStatus.IN_PROGRESS)), 0, 100));
        when(defectClient.create(any())).thenReturn(defectOf(DefectStatus.IN_PROGRESS));
        OrderDto base = orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.CORRECTIVE);
        OrderDto fromInspection = new OrderDto(base.id(), base.code(), base.title(), base.description(), base.type(), base.status(), base.priority(),
                base.asset(), base.executionPackageId(), base.trackId(), base.stationId(), base.startKp(), base.endKp(), base.plannedDate(),
                base.actualStartDate(), base.actualEndDate(), base.team(), base.assignedUser(), base.closingNotes(), base.cancellationReason(),
                INSPECTION1, null, base.stockProjectId(), base.taskCount(), base.completedTaskCount(), base.estimatedMinutes(),
                base.estimatedShifts(), base.audit(), base.version());
        openOrder(fromInspection);

        verify(defectClient, never()).search(any(DefectFilter.class), anyInt(), anyInt(), anyList());
        selectTab(2);
        assertEquals("DEF-000001", GridKt._getFormattedRow(gridWithId("order-defects-grid"), 0).getFirst());
        click("order-defect-create");
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("defect-asset")).isEmpty(), "el activo es el de la orden");
        LocatorJ._setValue(comboWithId("defect-severity"), DefectSeverity.HIGH);
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("defect-description")), "Pendola rota");
        click(DefectEditorDialog.SAVE_ID);
        verify(defectClient).create(new DefectRequest(ASSET1, DefectSeverity.HIGH, "Pendola rota", null, null, null, ORDER1, null, null, null,
                null, null, null));

        when(inspectionClient.findById(INSPECTION1)).thenReturn(inspectionOf(InspectionResult.MAJOR_DEFECT, null, ORDER1));
        click("order-origin-inspection");
        LocatorJ._get(InspectionDetailView.class);
    }

    // --- Mantenimiento: lineas de material ----------------------------------------------------------

    private static final UUID LINE_RESERVED = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000051");
    private static final UUID LINE_FAILED = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000052");
    private static final UUID LINE_CONSUMED = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000053");
    private static final UUID LINE_REJECTED = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000056");
    private static final String REJECTION = "mto-stock rejected 'reserve' with 422 WH-001: Warehouse WH-000 is inactive";

    private static MaterialUsageDto line(UUID id, StockSyncStatus status, UUID taskId) {
        return new MaterialUsageDto(id, ORDER1, taskId, MAT1, "MAT-001", "Pendola", WH1, new BigDecimal("4.000000"),
                status == StockSyncStatus.CONSUMED ? new BigDecimal("4.000000") : null, "ud", false,
                status == StockSyncStatus.RESERVED || status == StockSyncStatus.CONSUMED ? UUID.randomUUID() : null, status,
                status == StockSyncStatus.FAILED ? "Stock service unavailable" : status == StockSyncStatus.REJECTED ? REJECTION : null, null, null, 2L);
    }

    private static final UUID LINE_OUTPUT = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000058");

    /** Una linea fallida que mando esa peticion al almacen y se quedo sin respuesta. */
    private static MaterialUsageDto inDoubt(UUID id, StockRequestType request) {
        return new MaterialUsageDto(id, ORDER1, null, MAT1, "MAT-001", "Pendola", WH1, new BigDecimal("4.000000"), null, "ud", false, null,
                StockSyncStatus.FAILED, (request == StockRequestType.OUTPUT ? "consume" : "reserve") + ": Read timed out", request, null, 2L);
    }

    private void stubMaterials(MaintenanceOrderStatus status, List<MaterialUsageDto> lines) {
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(task(TASK1, 1, MaintenanceTaskStatus.PENDING)));
        when(orderClient.materials(ORDER1)).thenReturn(lines);
        when(warehouseClient.findById(WH1)).thenReturn(warehouse(WH1, "WH-000", "Central", true));
        openOrder(orderOf(status, MaintenanceOrderType.PREVENTIVE));
        selectTab(1);
    }

    /** Los botones de una fila de materiales, sin el id de la linea. */
    private static List<String> materialActions(Grid<Object> grid, int row) {
        return LocatorJ._find(GridKt._getCellComponent(grid, row, "actions"), Button.class).stream()
                .map(button -> button.getId().orElse("")).map(id -> id.replaceAll("-[0-9a-f]{8}-.*$", "")).toList();
    }

    @Test
    void theMaterialsTabShowsTheLinesWithTheirWarehouseAndOffersWhatEachAdmits() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of(line(LINE_RESERVED, StockSyncStatus.RESERVED, TASK1),
                line(LINE_FAILED, StockSyncStatus.FAILED, null), line(LINE_CONSUMED, StockSyncStatus.CONSUMED, null),
                line(LINE_REJECTED, StockSyncStatus.REJECTED, null)));

        Grid<Object> grid = gridWithId("order-materials-grid");
        assertEquals(List.of("MAT-001 - Pendola", "WH-000 - Central", "4 ud", "", "Tarea 1"), GridKt._getFormattedRow(grid, 0).subList(0, 5));
        assertEquals("Stock service unavailable", ((Span) GridKt._getCellComponent(grid, 1, "status")).getTitle().orElse(""),
                "el error de stock, en el tooltip del estado");
        Span rejected = (Span) GridKt._getCellComponent(grid, 3, "status");
        assertEquals("Rechazada", rejected.getText());
        assertEquals(REJECTION, rejected.getTitle().orElse(""), "el motivo del almacen, en el tooltip del estado");
        verify(warehouseClient, times(1)).findById(WH1);
        assertEquals(List.of("material-edit", "material-sync", "material-remove"), materialActions(grid, 0),
                "una reservada se comprueba: Almacen puede haber liberado su reserva");
        assertEquals("Comprobar la reserva en el almacen", LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class,
                spec -> spec.withId("material-sync-" + LINE_RESERVED)).getTooltip().getText());
        assertEquals(List.of("material-edit", "material-sync", "material-remove"), materialActions(grid, 1));
        assertEquals(List.of(), materialActions(grid, 2), "una linea consumida ya no se toca");
        assertEquals(List.of("material-edit", "material-sync", "material-remove"), materialActions(grid, 3), "una rechazada se reintenta");
        assertTrue(hasButton("material-add"));

        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of(line(LINE_FAILED, StockSyncStatus.FAILED, null)));
        assertEquals(List.of(), materialActions(gridWithId("order-materials-grid"), 0));
        assertFalse(hasButton("material-add"));
    }

    /**
     * Un estado de linea que el servicio estrene no ofrece nada, y una sin pedir solo se reintenta con
     * el estado de la orden conocido, como en mto-frontend.
     */
    @Test
    void aLineInAStateTheScreenDoesNotKnowOffersNothing() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        UUID unknownLine = UUID.fromString("3c3c3c3c-0000-4000-8000-00000000005a");
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of(line(unknownLine, StockSyncStatus.UNKNOWN, null)));
        assertEquals(List.of(), materialActions(gridWithId("order-materials-grid"), 0), "ni modificar, ni sincronizar, ni quitar");

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        stubMaterials(MaintenanceOrderStatus.UNKNOWN, List.of(line(LINE_FAILED, StockSyncStatus.NOT_REQUESTED, null)));
        assertEquals(List.of(), materialActions(gridWithId("order-materials-grid"), 0));
    }

    @Test
    void aLineIsRegisteredFromStockAndAReservedLineOnlyChangesItsConsumption() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.registerMaterial(eq(ORDER1), any())).thenReturn(line(LINE_RESERVED, StockSyncStatus.RESERVED, TASK1));
        when(orderClient.updateMaterial(eq(ORDER1), eq(LINE_RESERVED), any())).thenReturn(line(LINE_RESERVED, StockSyncStatus.RESERVED, TASK1));
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of(line(LINE_RESERVED, StockSyncStatus.RESERVED, TASK1)));

        click("material-add");
        LocatorJ._get(Paragraph.class, spec -> spec.withText("La orden ya esta planificada: la linea se reserva al momento en el almacen."));
        LocatorJ._setValue(comboWithId("material-material"), new MaterialSummaryDto(MAT1, "MAT-001", "Pendola", "ud", true));
        LocatorJ._setValue(comboWithId("material-warehouse"), new WarehouseSummaryDto(WH1, "WH-000", "Central", true));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-planned")), new BigDecimal("4"));
        ComboBoxKt.selectByLabel(comboWithId("material-task"), "1 · Perfil 12-2.27");
        click(MaterialUsageDialog.SAVE_ID);
        verify(orderClient).registerMaterial(ORDER1, new MaterialUsageRequest(MAT1, null, WH1, new BigDecimal("4"), "ud", TASK1, null));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("order-materials-grid"), 0, "actions"), Button.class,
                spec -> spec.withId("material-edit-" + LINE_RESERVED)));
        assertTrue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-planned")).isReadOnly(),
                "lo previsto de una linea reservada no cambia: se quita y se registra otra vez");
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-consumed")), new BigDecimal("3"));
        click(MaterialUsageDialog.SAVE_ID);
        verify(orderClient).updateMaterial(ORDER1, LINE_RESERVED, MergePatch.of(new MaterialUsageUpdateRequest(null, new BigDecimal("3"), null), 2L));

        loginAs("mantenimiento.sin-almacen", "ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ");
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of());
        assertFalse(hasButton("material-add"));
        LocatorJ._get(Paragraph.class, spec -> spec.withText("Anadir materiales pide leer el almacen (stock-read)."));
    }

    @Test
    void removingAReservedLineWarnsAboutItsReservationAndAStockOutageIsNotified() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        doThrow(maintenanceError(503, "STK-503", "Stock service unavailable")).doNothing()
                .when(orderClient).removeMaterial(ORDER1, LINE_RESERVED);
        when(orderClient.syncMaterial(ORDER1, LINE_FAILED)).thenReturn(line(LINE_FAILED, StockSyncStatus.RESERVED, null));
        stubMaterials(MaintenanceOrderStatus.PLANNED, List.of(line(LINE_RESERVED, StockSyncStatus.RESERVED, TASK1),
                line(LINE_FAILED, StockSyncStatus.FAILED, null)));
        Grid<Object> grid = gridWithId("order-materials-grid");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("material-remove-" + LINE_RESERVED)));
        ConfirmDialog confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message", "").startsWith("Se libera antes su reserva en el almacen"),
                "quitar una linea reservada libera su reserva");
        ConfirmDialogKt._fireConfirm(confirm);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "El almacen no responde: la linea de material se queda como estaba. Intentalo mas tarde. Stock service unavailable"));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("material-remove-" + LINE_RESERVED)));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(orderClient, times(2)).removeMaterial(ORDER1, LINE_RESERVED);

        NotificationsKt.clearNotifications();
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, "actions"), Button.class, spec -> spec.withId("material-sync-" + LINE_FAILED)));
        verify(orderClient).syncMaterial(ORDER1, LINE_FAILED);
        NotificationsKt.expectNotifications("MAT-001: reservada");
    }

    /**
     * Sincronizar una linea que el almacen rechaza dice por que (409 {@code STK-001} sin existencias,
     * 422 {@code STK-422} por otro motivo) y relee: la linea queda rechazada con ese motivo. En una
     * orden terminada, una rechazada se sigue ofreciendo, porque el servicio la liquida al reintentar.
     */
    @Test
    void syncingARejectedLineSaysWhyStockSaidNoAndRereadsTheLine() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.syncMaterial(ORDER1, LINE_REJECTED))
                .thenThrow(maintenanceError(422, "STK-422", REJECTION))
                .thenThrow(maintenanceError(409, "STK-001", "mto-stock rejected 'reserve' with 409 STK-001: Insufficient stock"));
        stubMaterials(MaintenanceOrderStatus.IN_PROGRESS, List.of(line(LINE_REJECTED, StockSyncStatus.REJECTED, null)));
        Grid<Object> grid = gridWithId("order-materials-grid");
        clearInvocations(orderClient);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("material-sync-" + LINE_REJECTED)));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText("El almacen ha rechazado la operacion. " + REJECTION));
        verify(orderClient).materials(ORDER1);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("material-sync-" + LINE_REJECTED)));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "No hay stock disponible suficiente. mto-stock rejected 'reserve' with 409 STK-001: Insufficient stock"));

        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        stubMaterials(MaintenanceOrderStatus.COMPLETED, List.of(line(LINE_REJECTED, StockSyncStatus.REJECTED, null),
                line(LINE_RESERVED, StockSyncStatus.RESERVED, null)));
        Grid<Object> completed = gridWithId("order-materials-grid");
        assertEquals(List.of("material-sync"), materialActions(completed, 0), "terminada la orden, solo queda reintentar");
        assertEquals(List.of(), materialActions(completed, 1), "una reservada solo se comprueba con la orden abierta");
    }

    /**
     * Una linea con una peticion al almacen sin respuesta lo dice en su estado, y solo ofrece lo que el
     * servicio acepta mientras tanto: ni lo previsto ni lo consumido, que viajan en ella, ni quitarla
     * si es una salida, porque el material quiza ya salio. Sincronizar y admitir consumir de mas, si, y
     * quitar una reserva, avisando de que el servicio pasa antes por el almacen aunque no tenga id.
     */
    @Test
    void aLineWaitingForTheWarehouseSaysSoAndOnlyOffersWhatTheServiceAccepts() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        when(orderClient.updateMaterial(eq(ORDER1), eq(LINE_FAILED), any())).thenReturn(inDoubt(LINE_FAILED, StockRequestType.RESERVATION));
        stubMaterials(MaintenanceOrderStatus.IN_PROGRESS, List.of(inDoubt(LINE_FAILED, StockRequestType.RESERVATION),
                inDoubt(LINE_OUTPUT, StockRequestType.OUTPUT)));
        Grid<Object> grid = gridWithId("order-materials-grid");

        Span reserving = (Span) GridKt._getCellComponent(grid, 0, "status");
        assertEquals("Fallida · Reserva sin respuesta", reserving.getText());
        assertTrue(reserving.getTitle().orElse("").startsWith("reserve: Read timed out. El almacen no contesto: se reintenta sola cada 5 minutos"),
                reserving.getTitle().orElse(""));
        assertEquals("Fallida · Salida sin respuesta", ((Span) GridKt._getCellComponent(grid, 1, "status")).getText());
        assertEquals(List.of("material-edit", "material-sync", "material-remove"), materialActions(grid, 0),
                "una reserva sin respuesta se quita: el servicio la confirma para liberarla");
        assertEquals(List.of("material-edit", "material-sync"), materialActions(grid, 1), "con una salida sin respuesta, el material quiza ya salio");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("material-edit-" + LINE_FAILED)));
        assertTrue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-planned")).isReadOnly());
        assertTrue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("material-consumed")).isReadOnly());
        assertTrue(LocatorJ._get(Paragraph.class, spec -> spec.withId("material-in-doubt")).getText()
                .startsWith("Reserva sin respuesta. El almacen no contesto"));
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("material-over-consumption")), true);
        click(MaterialUsageDialog.SAVE_ID);
        verify(orderClient).updateMaterial(ORDER1, LINE_FAILED, MergePatch.of(new MaterialUsageUpdateRequest(null, null, true), 2L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("material-remove-" + LINE_FAILED)));
        ConfirmDialog confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message", "").startsWith("Antes se confirma con el almacen la reserva que se quedo sin respuesta"),
                "sin id de reserva, quitarla tambien pasa por el almacen");
        ConfirmDialogKt._fireConfirm(confirm);
        verify(orderClient).removeMaterial(ORDER1, LINE_FAILED);
    }

    /**
     * El proyecto de almacen de una orden no se ofrece mientras alguna de sus lineas espera respuesta
     * del almacen: la reserva sin respuesta se repite contra ese proyecto, y el servicio rechaza
     * cambiarlo. Lo demas se guarda como siempre, y en cuanto el almacen contesta vuelve a ofrecerse.
     */
    @Test
    void theStockProjectOfAnOrderIsNotOfferedWhileOneOfItsLinesWaitsForTheWarehouse() {
        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        OrderDto planned = orderOf(MaintenanceOrderStatus.PLANNED, MaintenanceOrderType.PREVENTIVE);
        when(orderClient.update(eq(ORDER1), any())).thenReturn(planned);
        when(orderClient.materials(ORDER1)).thenReturn(List.of(inDoubt(LINE_FAILED, StockRequestType.RESERVATION)),
                List.of(line(LINE_FAILED, StockSyncStatus.RESERVED, null)));
        openOrder(planned);

        click("order-edit");
        ComboBox<ProjectSummaryDto> project = comboWithId("order-stock-project");
        assertTrue(project.isReadOnly());
        assertEquals("Hay lineas de material esperando respuesta del almacen: el proyecto no cambia hasta que contesten", project.getHelperText());
        LocatorJ._setValue(comboWithId("order-priority"), MaintenancePriority.CRITICAL);
        click(OrderEditorDialog.SAVE_ID);
        verify(orderClient).update(ORDER1, MergePatch.of(new OrderUpdateRequest(null, null, MaintenancePriority.CRITICAL, null, null, null, null,
                null, null, null, null, null, null), 3L));

        click("order-edit");
        assertFalse(this.<ProjectSummaryDto>comboWithId("order-stock-project").isReadOnly(), "el almacen ya contesto");
    }

    @Test
    void completingWithUnsyncedLinesSuggestsWhatToDoAndTheOrderCarriesItsStockProject() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(orderClient.complete(eq(ORDER1), any())).thenThrow(maintenanceError(409, "MAT-001",
                "Order MO-000001 has 1 material line(s) not synchronized with stock; retry /sync or complete with force"));
        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));
        click("order-complete");
        click(OrderTransitionDialog.CONFIRM_ID);
        Notification refused = NotificationsKt.getNotifications().getLast();
        LocatorJ._get(refused, Div.class, spec -> spec.withText(
                "Sincroniza las lineas fallidas o rechazadas en la pestana Materiales, o pide a quien supervisa que la complete igualmente."));

        UUID project = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000054");
        UUID other = UUID.fromString("3c3c3c3c-0000-4000-8000-000000000055");
        when(projectClient.findById(project)).thenReturn(new ProjectDto(project, "EP-3", "Paquete norte", true, "mto-configuration", true, null));
        OrderDto draft = orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE);
        OrderDto withProject = new OrderDto(draft.id(), draft.code(), draft.title(), draft.description(), draft.type(), draft.status(),
                draft.priority(), draft.asset(), draft.executionPackageId(), draft.trackId(), draft.stationId(), draft.startKp(), draft.endKp(),
                draft.plannedDate(), null, null, draft.team(), draft.assignedUser(), null, null, null, null, project, 0, 0, BigDecimal.ZERO, 0, null, draft.version());
        when(orderClient.update(eq(ORDER1), any())).thenReturn(withProject);
        UI.getCurrent().navigate(MaintenanceRoutes.ORDERS);
        openOrder(withProject);
        assertTrue(LocatorJ._get(Div.class, spec -> spec.withId("order-summary")).getElement().getTextRecursively()
                .contains("Proyecto de almacen: EP-3 - Paquete norte"), "el proyecto, por su nombre en mto-stock");
        click("order-edit");
        assertEquals(project, this.<ProjectSummaryDto>comboWithId("order-stock-project").getValue().id(), "el editor parte del proyecto leido");
        LocatorJ._setValue(comboWithId("order-stock-project"), new ProjectSummaryDto(other, "EP-5", "Paquete sur", true));
        click(OrderEditorDialog.SAVE_ID);
        verify(orderClient).update(ORDER1, MergePatch.of(new OrderUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null,
                other), 3L));
    }

    // --- Mantenimiento: informes -------------------------------------------------------------------

    private static final byte[] XLSX = {80, 75, 3, 4};

    private static ResponseEntity<byte[]> file(String name, byte[] body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(name).build().toString())
                .body(body);
    }

    private static Anchor anchorWithId(String id) {
        return LocatorJ._get(Anchor.class, spec -> spec.withId(id));
    }

    private static String textOf(String id) {
        return LocatorJ._get(Div.class, spec -> spec.withId(id)).getElement().getTextRecursively();
    }

    /**
     * El avance: los filtros viajan como el servicio los lee (las fechas como el principio y el
     * ultimo instante del dia), las cifras son las suyas con los nombres de paquete y via, la
     * fraccion se pinta como porcentaje y los enlaces aparecen tras consultar. Un fallo se
     * notifica y no deja enlaces.
     */
    @Test
    void theProgressReportShowsTheServiceFiguresWithNamesAndItsFilesAppearAfterQuerying() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        Instant from = Formats.startOfDay(LocalDate.of(2026, 9, 1));
        Instant to = Formats.endOfDay(LocalDate.of(2026, 9, 30));
        ProgressRowDto row = new ProgressRowDto(3L, 12L, CatenaryAssetType.PROFILE, 40, 18, new BigDecimal("0.4500"), new BigDecimal("5.400"),
                new BigDecimal("12.000"));
        when(reportClient.progress(3L, 12L, CatenaryAssetType.PROFILE, from, to))
                .thenReturn(new ProgressReportDto(from, to, 40, 18, new BigDecimal("0.4500"), new BigDecimal("5.400"), new BigDecimal("12.000"),
                        List.of(row)));
        when(reportClient.progress(null, null, null, null, null)).thenThrow(maintenanceError(503, null, null));

        UI.getCurrent().navigate(MaintenanceRoutes.REPORTS);
        SideNavItem maintenance = LocatorJ._get(SideNavItem.class, spec -> spec.withLabel("Mantenimiento"));
        assertTrue(maintenance.getItems().stream().map(SideNavItem::getLabel).toList().contains("Informes"));
        click("progress-query");
        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("El servicio no esta disponible ahora mismo. Intentalo mas tarde."));
        assertTrue(LocatorJ._find(Anchor.class, spec -> spec.withId("progress-xlsx")).isEmpty(), "un fallo no deja enlaces");

        LocatorJ._setValue(comboWithId("progress-package"), new RefItem(3L, "PAQ NORTE"));
        LocatorJ._setValue(comboWithId("progress-track"), new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        LocatorJ._setValue(comboWithId("progress-type"), CatenaryAssetType.PROFILE);
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("progress-from")), LocalDate.of(2026, 9, 1));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("progress-to")), LocalDate.of(2026, 9, 30));
        click("progress-query");

        assertEquals("18 de 40 activos revisados (45 %) · 5.4 de 12 km", textOf("progress-summary"));
        assertEquals(List.of("PAQ NORTE", "VIA 1 (PAQ NORTE)", "Perfil", "18 de 40", "45 %", "5.4 de 12 km"),
                GridKt._getFormattedRow(gridWithId("progress-grid"), 0));
        assertEquals("Excel", anchorWithId("progress-xlsx").getText());
        assertEquals("PDF", anchorWithId("progress-pdf").getText());

        // Una consulta que falla no deja a la vista la anterior, ni sus descargas, que serian de otra cosa.
        for (String id : List.of("progress-package", "progress-track", "progress-type")) {
            LocatorJ._setValue(comboWithId(id), null);
        }
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("progress-from")), null);
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("progress-to")), null);
        click("progress-query");
        assertTrue(LocatorJ._find(Anchor.class, spec -> spec.withId("progress-xlsx")).isEmpty());
        assertEquals(0, GridKt._size(gridWithId("progress-grid")));
        assertEquals("", textOf("progress-summary"));
    }

    /**
     * El mensual: los ultimos 24 meses con el actual elegido, el mes obligatorio antes de llamar,
     * el resumen y los materiales del servicio, y un enlace que descarga lo consultado aunque
     * despues cambien los filtros.
     */
    @Test
    void theMonthlyReportOffersTheLastMonthsAndItsFilesDownloadWhatWasQueried() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        YearMonth current = YearMonth.now();
        YearMonth last = current.minusMonths(1);
        when(reportClient.monthly(last, 3L)).thenReturn(new MonthlyReportDto(last, 3L, 8, 6, 1, 1440, new BigDecimal("240.00"), 3, 45, 44,
                new BigDecimal("2.900"), 5, 3, 2, List.of(new MonthlyMaterialLineDto(UUID.randomUUID(), "MAT-001", "m", new BigDecimal("12.500000")))));
        byte[] pdf = "%PDF-1.7".getBytes(StandardCharsets.UTF_8);
        when(reportClient.monthlyFile(last, 3L, "pdf")).thenReturn(file("monthly-report-" + last + ".pdf", pdf));

        UI.getCurrent().navigate(MaintenanceRoutes.REPORTS);
        selectTab(1);
        ComboBox<YearMonth> month = comboWithId("monthly-month");
        assertEquals(current, month.getValue(), "el mes en curso por defecto");
        List<String> months = ComboBoxKt.getSuggestions(month);
        assertEquals(24, months.size(), "los ultimos 24 meses");
        assertEquals(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.forLanguageTag("es")).format(last), months.get(1));
        LocatorJ._setValue(month, null);
        click("monthly-query");
        assertTrue(month.isInvalid());
        verify(reportClient, never()).monthly(any(), any());

        LocatorJ._setValue(month, last);
        LocatorJ._setValue(comboWithId("monthly-package"), new RefItem(3L, "PAQ NORTE"));
        click("monthly-query");

        String summary = textOf("monthly-summary");
        assertTrue(summary.contains("Turnos: 8 planificados, 6 cerrados, 1 cancelados · 1440 min netos (240 por turno)"), summary);
        assertTrue(summary.contains("Defectos: 5 detectados, 3 resueltos · Ordenes correctivas: 2"), summary);
        assertEquals(List.of("MAT-001", "12.5", "m"), GridKt._getFormattedRow(gridWithId("monthly-materials-grid"), 0));
        LocatorJ._setValue(comboWithId("monthly-package"), null);
        assertArrayEquals(pdf, DownloadKt._download(anchorWithId("monthly-pdf")));
        verify(reportClient).monthlyFile(last, 3L, "pdf");
    }

    /**
     * El parte del turno: su pestana no pide nada hasta abrirse, enseña los recuentos y las filas
     * del servicio, y su Excel se pide con el token de la persona desde esta aplicacion.
     */
    @Test
    void theShiftReportTabShowsTheDailyReportAndServesItsFiles() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        ShiftDto shift = shiftOf(ShiftStatus.CLOSED);
        ShiftReportRowDto row = new ShiftReportRowDto(1, TASK1, "MO-000001", 3L, 12L, "PRF-0001", "12-2.27", new BigDecimal("12.270"), "S-3",
                List.of(), List.of("RG-01", "RG-04"), "Revision general", "DEF-000001", List.of("MAT-001 2 m"), null, null,
                MaintenanceTaskStatus.COMPLETED, true, null, List.of());
        when(reportClient.shiftReport(SHIFT1)).thenReturn(new ShiftReportDto(shift, 2, 1, 2, 1, 0, List.of(row)));
        when(reportClient.shiftReportFile(SHIFT1, "xlsx")).thenReturn(file("shift-report-SH-000001-2026-10-05.xlsx", XLSX));

        openShift(shift);
        verify(reportClient, never()).shiftReport(any());
        selectTab(2);

        assertEquals("Tareas: 2 completadas, 1 pendientes · Perfiles revisados: 2 · Defectos: 1 encontrados, 0 resueltos",
                textOf("shift-report-summary"));
        assertEquals(List.of("1", "MO-000001", "12-2.27", "12.27", "RG-01, RG-04", "Revision general", "DEF-000001", "MAT-001 2 m", "Completada"),
                GridKt._getFormattedRow(gridWithId("shift-report-grid"), 0));
        assertArrayEquals(XLSX, DownloadKt._download(anchorWithId("shift-report-xlsx")));
        verify(reportClient).shiftReportFile(SHIFT1, "xlsx");
        assertEquals("PDF", anchorWithId("shift-report-pdf").getText());
    }

    // --- Mantenimiento: historial ------------------------------------------------------------------

    /** Abre el historial desde el boton de la ficha y devuelve su tabla; lo cierra quien llama. */
    private static Grid<Object> openHistory(String buttonId) {
        click(buttonId);
        return gridWithId("revisions-grid");
    }

    /**
     * El historial de una orden: paginado en el servicio, la revision mas reciente primero, con quien,
     * por que camino y como quedo la orden. Turnos, inspecciones y defectos lo ofrecen igual desde su
     * ficha, tambien a quien solo lee, cada uno con su linea.
     */
    @Test
    void theHistoryOfAnOrderIsPagedNewestFirstAndEveryDetailOffersItsOwn() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        List<RevisionDto<OrderDto>> orderHistory = List.of(
                revision(2, RevisionOperation.UPDATED, "mantenimiento.tecnico", "HTTP", orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE)),
                revision(1, RevisionOperation.CREATED, "mantenimiento.responsable", "HTTP", orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE)));
        when(orderClient.revisions(eq(ORDER1), anyInt(), anyInt())).thenAnswer(call -> page(orderHistory, call.getArgument(1), call.getArgument(2)));
        when(shiftClient.revisions(eq(SHIFT1), anyInt(), anyInt()))
                .thenReturn(page(List.of(revision(3, RevisionOperation.UPDATED, "mantenimiento.tecnico", "HTTP", shiftOf(ShiftStatus.CLOSED))), 0, 20));
        when(inspectionClient.revisions(eq(INSPECTION1), anyInt(), anyInt())).thenReturn(page(List.of(
                revision(4, RevisionOperation.UPDATED, "ana", "HTTP", inspectionOf(InspectionResult.MINOR_DEFECT, null, null))), 0, 20));
        when(defectClient.revisions(eq(DEFECT1), anyInt(), anyInt()))
                .thenReturn(page(List.of(revision(5, RevisionOperation.CREATED, null, "SYSTEM", defectOf(DefectStatus.OPEN))), 0, 20));

        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));
        Grid<Object> grid = openHistory("order-history");
        assertEquals(2, GridKt._size(grid));
        LocatorJ._get(LocatorJ._get(Dialog.class), Span.class, spec -> spec.withText("2 revisiones, la mas reciente primero"));
        List<String> newest = GridKt._getFormattedRow(grid, 0);
        assertTrue(newest.containsAll(List.of("2", "Modificacion", "mantenimiento.tecnico", "HTTP", "corr-2",
                "Revision tramo 12 · En curso · prioridad Alta · plan 14/09/2026 · EQ-01 · mantenimiento.tecnico")), newest.toString());
        List<String> first = GridKt._getFormattedRow(grid, 1);
        assertTrue(first.containsAll(List.of("1", "Alta", "mantenimiento.responsable",
                "Revision tramo 12 · Borrador · prioridad Alta · plan 14/09/2026 · EQ-01 · mantenimiento.tecnico")), first.toString());
        verify(orderClient, atLeastOnce()).revisions(eq(ORDER1), eq(0), anyInt());
        LocatorJ._get(Dialog.class).close();

        openShift(shiftOf(ShiftStatus.CLOSED));
        assertTrue(GridKt._getFormattedRow(openHistory("shift-history"), 0).contains("Cerrado · 05/10/2026 · EQ-01 · ocupacion Total"));
        LocatorJ._get(Dialog.class).close();

        openInspection(inspectionOf(InspectionResult.MINOR_DEFECT, null, null));
        assertTrue(GridKt._getFormattedRow(openHistory("inspection-history"), 0).contains("Tecnica · 20/09/2026 · ana · Defecto leve"));
        LocatorJ._get(Dialog.class).close();

        openDefect(defectOf(DefectStatus.OPEN));
        List<String> defect = GridKt._getFormattedRow(openHistory("defect-history"), 0);
        assertTrue(defect.containsAll(List.of("Alta", "SYSTEM", "Alta · Abierto")), defect.toString());
        assertTrue(NotificationsKt.getNotifications().isEmpty());
    }

    /**
     * Un activo que solo ha llegado por datos maestros no tiene revisiones (alli es SQL nativo): el
     * 404 es «sin historial todavia», sin notificacion. Un tramo propio si las tiene, con su linea.
     */
    @Test
    void anAssetOnlyKnownFromMasterDataHasNoHistoryYetAndAnOwnSectionDoes() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        AssetDto own = ownSection(ASSET_OWN, "TS-0002", true);
        stubAssets(List.of(syncedProfile(), own));
        when(assetClient.revisions(eq(ASSET_SYNCED), anyInt(), anyInt()))
                .thenThrow(maintenanceError(404, "APP-404", "CatenaryAsset not found: " + ASSET_SYNCED));
        when(assetClient.revisions(eq(ASSET_OWN), anyInt(), anyInt()))
                .thenReturn(page(List.of(revision(6, RevisionOperation.UPDATED, "mantenimiento.tecnico", "HTTP", own)), 0, 20));

        UI.getCurrent().navigate(MaintenanceRoutes.ASSETS);
        Grid<Object> assets = gridWithId("assets-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(assets, 0, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-history-" + ASSET_SYNCED)));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._get(dialog, Span.class, spec -> spec.withId("revisions-empty"));
        assertTrue(LocatorJ._find(dialog, Grid.class).isEmpty(), "sin revisiones no hay tabla");
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "un 404 aqui es «sin historial», no un error");
        dialog.close();

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(assets, 1, AssetsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("asset-history-" + ASSET_OWN)));
        List<String> row = GridKt._getFormattedRow(gridWithId("revisions-grid"), 0);
        assertTrue(row.contains("Tramo TS-0002 · KP 12.1 - 13.45 · activo · Tramo propio"), row.toString());
    }

    // --- Notificaciones y actividad --------------------------------------------------------------

    private static final UUID NOTIFICATION1 = UUID.fromString("5e6f7a8b-0000-4000-8000-000000000501");
    private static final UUID NOTIFICATION2 = UUID.fromString("5e6f7a8b-0000-4000-8000-000000000502");
    private static final UUID EVENT1 = UUID.fromString("5e6f7a8b-0000-4000-8000-000000000511");
    private static final UUID EVENT2 = UUID.fromString("5e6f7a8b-0000-4000-8000-000000000512");
    private static final Instant NOTIFIED_AT = Instant.parse("2026-09-28T06:07:00Z");

    private static InboxItemDto notification(UUID id, String title, String link, boolean read, ActivitySeverity severity,
                                             ActivityCategory category) {
        return new InboxItemDto(id, "rule-" + id, category, severity, title, "Detalle de " + title, link, "order", "MO-000012", EVENT1,
                NOTIFIED_AT, read, read ? NOTIFIED_AT.plusSeconds(3600) : null);
    }

    private static InboxItemDto read(InboxItemDto item) {
        return new InboxItemDto(item.id(), item.ruleKey(), item.category(), item.severity(), item.title(), item.body(), item.link(),
                item.subjectType(), item.subjectId(), item.activityEventId(), item.createdAt(), true, NOTIFIED_AT.plusSeconds(60));
    }

    private static ActivityEventDto event(UUID id, ActivityCategory category, String type, ActivitySeverity severity, Map<String, Object> payload) {
        return new ActivityEventDto(id, 118L, "mto-maintenance", "f0000000-0000-4000-8000-000000000001", category, type, severity, NOTIFIED_AT,
                NOTIFIED_AT.plusSeconds(1), new ActorDto(ActorKind.PERSON, "mantenimiento.tecnico", "a-1"), new SubjectDto("order", "MO-000012", null),
                "corr-1", 1, payload, null);
    }

    private static AccessEventDto access(UUID id, String type, AccessOutcome outcome, ActivitySeverity severity, int count) {
        return new AccessEventDto(id, 7L, type, severity, outcome, NOTIFIED_AT, NOTIFIED_AT.plusSeconds(20), "config.lector", "a-41", "10.0.0.7",
                null, count, Map.of("error", "invalid_user_credentials"));
    }

    private void stubInbox(List<InboxItemDto> items) {
        when(notificationClient.inbox(any(InboxFilter.class), anyInt(), anyInt(), anyList()))
                .thenAnswer(call -> page(items, call.getArgument(1), call.getArgument(2)));
    }

    @SuppressWarnings("unchecked")
    private static Grid<InboxItemDto> inboxGrid() {
        return LocatorJ._get(Grid.class, spec -> spec.withId("inbox-grid"));
    }

    @SuppressWarnings("unchecked")
    private static Grid<ActivityEventDto> activityGrid() {
        return LocatorJ._get(Grid.class, spec -> spec.withId("activity-grid"));
    }

    @SuppressWarnings("unchecked")
    private static Grid<AccessEventDto> accessGrid() {
        return LocatorJ._get(Grid.class, spec -> spec.withId("access-grid"));
    }

    private static Span bellBadge() {
        return LocatorJ._get(Span.class, spec -> spec.withId(InboxBell.COUNT_ID));
    }

    @Test
    void theBellShowsTheUnreadCountAndOpensTheInbox() {
        loginAs("mantenimiento.tecnico", "ROLE_NOTIFICATION_INBOX", "ROLE_MAINTENANCE_READ", "ROLE_REALM_MTO_MAINTENANCE_TECHNICIAN");
        when(notificationClient.unreadCount()).thenReturn(new UnreadCountDto(3, false));

        UI.getCurrent().navigate(HomeView.class);

        assertEquals("3", bellBadge().getText());
        List<String> labels = menuLabels();
        assertTrue(labels.contains("Notificaciones"), labels.toString());
        assertFalse(labels.contains("Actividad"), "sin activity-read no hay registro: " + labels);
        assertFalse(labels.contains("Accesos"), labels.toString());
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(InboxBell.ID)));
        LocatorJ._get(NotificationsView.class);
    }

    /** La campana es de quien puede leer su bandeja, que es un rol de cliente; un rol de realm con su nombre no la da. */
    @Test
    void theBellIsOnlyForWhoCanReadTheInbox() {
        loginAs("config.lector", "ROLE_CONFIG_READ", "ROLE_REALM_NOTIFICATION_INBOX");

        UI.getCurrent().navigate(HomeView.class);

        assertTrue(LocatorJ._find(InboxBell.class).isEmpty());
        verify(notificationClient, never()).unreadCount();
        assertFalse(menuLabels().contains("Notificaciones"), menuLabels().toString());
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(NotificationRoutes.INBOX));
        assertTrue(LocatorJ._find(NotificationsView.class).isEmpty());
    }

    @Test
    void theBellHidesTheNumberWithNothingUnreadAndIsRefreshedFromTheSharedThreadWithPush() {
        loginAs("almacen.responsable", "ROLE_NOTIFICATION_INBOX");

        UI.getCurrent().navigate(HomeView.class);

        LocatorJ._get(Button.class, spec -> spec.withId(InboxBell.ID));
        List<Span> badges = LocatorJ._find(Span.class, spec -> spec.withId(InboxBell.COUNT_ID));
        assertTrue(badges.isEmpty() || !badges.getFirst().isVisible(), "sin nada sin leer no hay numero");

        InboxBell bell = LocatorJ._get(InboxBell.class);
        when(notificationClient.unreadCount()).thenReturn(new UnreadCountDto(100, true));
        bell.pollOnce();
        MockVaadin.clientRoundtrip();
        assertEquals("100+", bellBadge().getText(), "acotado: cien o mas");

        // Un fallo en una pasada no molesta: el numero se queda como estaba y no hay notificacion.
        when(notificationClient.unreadCount()).thenThrow(BackofficeApiException.of(HttpStatus.SERVICE_UNAVAILABLE, ApiProblem.empty(), "corr-9",
                Duration.ofSeconds(30), "GET /api/notifications/inbox/unread-count"));
        bell.pollOnce();
        MockVaadin.clientRoundtrip();
        assertEquals("100+", bellBadge().getText());
        assertTrue(NotificationsKt.getNotifications().isEmpty());
    }

    @Test
    void theMenuOffersTheInboxTheLogAndTheAccessesEachByItsPermission() {
        loginAs("auditor", "ROLE_NOTIFICATION_INBOX", "ROLE_NOTIFICATION_ACTIVITY_READ", "ROLE_NOTIFICATION_ACCESS_READ", "ROLE_REALM_MTO_AUDITOR");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.containsAll(List.of("Notificaciones", "Actividad", "Accesos")), labels.toString());
        SideNavItem activity = LocatorJ._get(SideNavItem.class, spec -> spec.withLabel("Actividad"));
        assertEquals(NotificationRoutes.ACTIVITY, activity.getPath(), "el registro es el nodo del grupo");
        assertEquals(List.of("Accesos"), activity.getItems().stream().map(SideNavItem::getLabel).toList());
    }

    /** Los accesos llevan usuario e IP: su permiso no viene con el del registro, ni al reves. */
    @Test
    void theAccessesHaveTheirOwnPermissionThatTheLogDoesNotGive() {
        loginAs("usuarios.responsable", "ROLE_NOTIFICATION_ACTIVITY_READ");

        UI.getCurrent().navigate(HomeView.class);

        List<String> labels = menuLabels();
        assertTrue(labels.contains("Actividad"), labels.toString());
        assertFalse(labels.contains("Accesos"), "sin access-read no se ofrecen: " + labels);
        assertFalse(labels.contains("Notificaciones"), labels.toString());
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(NotificationRoutes.ACCESS));
        assertTrue(LocatorJ._find(AccessView.class).isEmpty());
    }

    @Test
    void aRealmRoleNamedLikeANotificationPermissionOpensNeitherTheLogNorTheAccesses() {
        loginAs("impostor", "ROLE_REALM_NOTIFICATION_ACTIVITY_READ", "ROLE_REALM_NOTIFICATION_ACCESS_READ");

        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(NotificationRoutes.ACTIVITY));
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(NotificationRoutes.ACCESS));

        assertTrue(LocatorJ._find(ActivityView.class).isEmpty());
        assertTrue(LocatorJ._find(AccessView.class).isEmpty());
    }

    @Test
    void theInboxOpensWithTheUnreadOnesAndFiltersAndSortsInTheServer() {
        loginAs("mantenimiento.responsable", "ROLE_NOTIFICATION_INBOX");
        stubInbox(List.of(
                notification(NOTIFICATION1, "Orden urgente MO-000012", "/mantenimiento/ordenes/" + ORDER1, false, ActivitySeverity.CRITICAL,
                        ActivityCategory.MAINTENANCE),
                notification(NOTIFICATION2, "Material GA70 bajo minimo", "/almacen/materiales", true, ActivitySeverity.WARNING, ActivityCategory.STOCK)));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);

        Grid<InboxItemDto> grid = inboxGrid();
        assertEquals(2, GridKt._size(grid));
        verify(notificationClient, atLeastOnce()).inbox(eq(new InboxFilter(true, null, null, null, null)), eq(0), anyInt(),
                eq(List.of("createdAt,desc")));
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertTrue(row.containsAll(List.of("Critica", "Mantenimiento", "Orden urgente MO-000012", "Detalle de Orden urgente MO-000012")), row.toString());
        assertEquals("Nueva", ((Span) GridKt._getCellComponent(grid, 0, "state")).getText());
        assertEquals("", ((Span) GridKt._getCellComponent(grid, 1, "state")).getText(), "una leida no lleva marca");
        LocatorJ._get(Span.class, spec -> spec.withText("2 sin leer"));

        clearInvocations(notificationClient);
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("inbox-unread-only")), false);
        GridKt._size(grid);
        verify(notificationClient, atLeastOnce()).inbox(eq(InboxFilter.NONE), anyInt(), anyInt(), eq(List.of("createdAt,desc")));
        LocatorJ._get(Span.class, spec -> spec.withText("2 notificaciones"));

        clearInvocations(notificationClient);
        LocatorJ._setValue(LocatorJ._get(ComboBox.class, spec -> spec.withId("inbox-category")), ActivityCategory.STOCK);
        LocatorJ._setValue(LocatorJ._get(ComboBox.class, spec -> spec.withId("inbox-severity")), ActivitySeverity.WARNING);
        GridKt._size(grid);
        verify(notificationClient, atLeastOnce()).inbox(eq(new InboxFilter(null, ActivityCategory.STOCK, ActivitySeverity.WARNING, null, null)),
                anyInt(), anyInt(), anyList());

        // La pagina lleva el orden de la columna, y con ella llega el recuento (LazyPages).
        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("severity"), SortDirection.DESCENDING)));
        GridKt._get(grid, 0);
        verify(notificationClient, atLeastOnce()).inbox(any(InboxFilter.class), anyInt(), anyInt(), eq(List.of("severity,desc")));
        assertFalse(grid.getColumnByKey("state").isSortable(), "el estado de lectura es de cada persona: el servicio no ordena por el");
    }

    /** El enlace de una regla es una ruta de esta aplicacion con sus filtros: se abre y los aplica, tras marcar la notificacion como leida. */
    @Test
    void openingANotificationMarksItReadRefreshesTheBellAndFollowsItsLinkWithItsFilters() {
        loginAs("config.ops", "ROLE_NOTIFICATION_INBOX", "ROLE_NOTIFICATION_ACTIVITY_READ");
        InboxItemDto stalled = notification(NOTIFICATION1, "Fuente parada", "/actividad?category=SYSTEM&type=system.source.stalled", false,
                ActivitySeverity.CRITICAL, ActivityCategory.SYSTEM);
        stubInbox(List.of(stalled));
        when(notificationClient.markRead(NOTIFICATION1)).thenReturn(read(stalled));
        when(notificationClient.unreadCount()).thenReturn(new UnreadCountDto(1, false));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);
        assertEquals("1", bellBadge().getText());
        when(notificationClient.unreadCount()).thenReturn(new UnreadCountDto(0, false));
        clearInvocations(notificationClient);
        Component actions = GridKt._getCellComponent(inboxGrid(), 0, NotificationsView.ACTIONS_COLUMN);
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withId("open-" + NOTIFICATION1)));

        verify(notificationClient).markRead(NOTIFICATION1);
        verify(notificationClient, atLeastOnce()).unreadCount();
        LocatorJ._get(ActivityView.class);
        assertEquals(ActivityCategory.SYSTEM, LocatorJ._get(ComboBox.class, spec -> spec.withId("activity-category")).getValue());
        assertEquals("system.source.stalled", LocatorJ._get(TextField.class, spec -> spec.withId("activity-type")).getValue());
        GridKt._size(activityGrid());
        verify(notificationClient, atLeastOnce()).activity(eq(new ActivityFilter(ActivityCategory.SYSTEM, "system.source.stalled", null, null, null,
                null, null, null, null, false)), eq(0), anyInt(), eq(List.of("occurredAt,desc")));

        // Otro enlace con la pantalla ya abierta parte de cero: el tipo de antes no se queda.
        NotificationLinks.open(UI.getCurrent(), "/actividad?category=MAINTENANCE");
        assertEquals(ActivityCategory.MAINTENANCE, LocatorJ._get(ComboBox.class, spec -> spec.withId("activity-category")).getValue());
        assertEquals("", LocatorJ._get(TextField.class, spec -> spec.withId("activity-type")).getValue());
    }

    /**
     * El enlace de una notificacion, como en mto-frontend: una ruta empieza por una sola barra y una
     * direccion http(s) se abre en otra pestana; cualquier otra cosa no es un destino y no lleva
     * flecha. Un acceso nunca ofrece su linea del registro, que seria un 404 ACT-404.
     */
    @Test
    void onlyARouteOrAnHttpAddressIsALinkToOpenAndAnAccessOffersNoLogLine() {
        loginAs("auditor", "ROLE_NOTIFICATION_INBOX", "ROLE_NOTIFICATION_ACTIVITY_READ");
        UUID external = UUID.fromString("5e6f7a8b-0000-4000-8000-000000000503");
        UUID access = UUID.fromString("5e6f7a8b-0000-4000-8000-000000000504");
        stubInbox(List.of(
                notification(NOTIFICATION1, "Raro", "javascript:alert(1)", false, ActivitySeverity.INFO, ActivityCategory.CONFIGURATION),
                notification(NOTIFICATION2, "Sin barra", "mantenimiento/ordenes", false, ActivitySeverity.INFO, ActivityCategory.MAINTENANCE),
                notification(external, "Fuera", "https://estado.mto.local", false, ActivitySeverity.INFO, ActivityCategory.SYSTEM),
                notification(access, "Acceso fallido", "/actividad/accesos?username=config.lector", false, ActivitySeverity.WARNING,
                        ActivityCategory.ACCESS)));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);
        Grid<InboxItemDto> grid = inboxGrid();
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, NotificationsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("open-" + NOTIFICATION1)).isEmpty(), "otro esquema se descarta");
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 1, NotificationsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("open-" + NOTIFICATION2)).isEmpty(), "una ruta empieza por una sola barra");
        Component externalActions = GridKt._getCellComponent(grid, 2, NotificationsView.ACTIONS_COLUMN);
        LocatorJ._get(externalActions, Button.class, spec -> spec.withId("open-" + external));
        LocatorJ._get(externalActions, Button.class, spec -> spec.withId("event-" + external));
        Component accessActions = GridKt._getCellComponent(grid, 3, NotificationsView.ACTIONS_COLUMN);
        LocatorJ._get(accessActions, Button.class, spec -> spec.withId("open-" + access));
        assertTrue(LocatorJ._find(accessActions, Button.class, spec -> spec.withId("event-" + access)).isEmpty(),
                "un acceso nunca sale por el registro");
        assertTrue(NotificationLinks.target("//otro.sitio/x").isEmpty());
        assertTrue(NotificationLinks.target(" /mantenimiento ").map(NotificationLinks.Target::external).map(external1 -> !external1).orElse(false));
    }

    @Test
    void aReadNotificationIsNotMarkedAgainAndTheCheckMarksOneWithoutFollowingAnything() {
        loginAs("config.lector", "ROLE_NOTIFICATION_INBOX");
        InboxItemDto alreadyRead = notification(NOTIFICATION1, "Ya leida", "/", true, ActivitySeverity.INFO, ActivityCategory.CONFIGURATION);
        InboxItemDto withoutLink = notification(NOTIFICATION2, "Sin enlace", null, false, ActivitySeverity.INFO, ActivityCategory.CONFIGURATION);
        stubInbox(List.of(alreadyRead, withoutLink));
        when(notificationClient.markRead(NOTIFICATION2)).thenReturn(read(withoutLink));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);
        Component readActions = GridKt._getCellComponent(inboxGrid(), 0, NotificationsView.ACTIONS_COLUMN);
        assertTrue(LocatorJ._find(readActions, Button.class, spec -> spec.withId("read-" + NOTIFICATION1)).isEmpty(), "una leida no se vuelve a marcar");
        assertTrue(LocatorJ._find(readActions, Button.class, spec -> spec.withId("event-" + NOTIFICATION1)).isEmpty(),
                "sin activity-read no se ofrece la linea del registro");
        Component unreadActions = GridKt._getCellComponent(inboxGrid(), 1, NotificationsView.ACTIONS_COLUMN);
        assertTrue(LocatorJ._find(unreadActions, Button.class, spec -> spec.withId("open-" + NOTIFICATION2)).isEmpty(), "sin enlace no hay flecha");

        clearInvocations(notificationClient);
        LocatorJ._click(LocatorJ._get(unreadActions, Button.class, spec -> spec.withId("read-" + NOTIFICATION2)));
        verify(notificationClient).markRead(NOTIFICATION2);
        GridKt._size(inboxGrid());
        verify(notificationClient, atLeastOnce()).inbox(any(InboxFilter.class), anyInt(), anyInt(), anyList());
        LocatorJ._get(NotificationsView.class);

        clearInvocations(notificationClient);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(inboxGrid(), 0, NotificationsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("open-" + NOTIFICATION1)));
        verify(notificationClient, never()).markRead(any());
        LocatorJ._get(HomeView.class);
    }

    @Test
    void markingAllAsReadGoesToTheServiceAndRefreshesTheListAndTheBell() {
        loginAs("config.lector", "ROLE_NOTIFICATION_INBOX");
        stubInbox(List.of(notification(NOTIFICATION1, "Una", null, false, ActivitySeverity.INFO, ActivityCategory.USERS)));
        when(notificationClient.markAllRead()).thenReturn(new ReadAllDto(NOTIFIED_AT));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);
        GridKt._size(inboxGrid());
        clearInvocations(notificationClient);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("inbox-read-all")));

        verify(notificationClient).markAllRead();
        verify(notificationClient, atLeastOnce()).unreadCount();
        NotificationsKt.expectNotifications("Todas las notificaciones quedan como leidas");
        GridKt._size(inboxGrid());
        verify(notificationClient, atLeastOnce()).inbox(any(InboxFilter.class), anyInt(), anyInt(), anyList());
    }

    /** 404 NTF-404: ya no va dirigida a mi (o nunca fue). Se dice y no se abre nada. */
    @Test
    void aNotificationThatIsNoLongerMineIsSaidAsNotFoundAndNothingOpens() {
        loginAs("config.lector", "ROLE_NOTIFICATION_INBOX");
        stubInbox(List.of(notification(NOTIFICATION1, "Ajena", "/", false, ActivitySeverity.INFO, ActivityCategory.USERS)));
        ApiProblem problem = new ApiProblem(null, "NOT_FOUND", 404, "Notification " + NOTIFICATION1 + " is not addressed to config.lector", null,
                "NTF-404", null, "corr-n4", null, null, null, null);
        when(notificationClient.markRead(NOTIFICATION1)).thenThrow(BackofficeApiException.of(HttpStatus.NOT_FOUND, problem, "corr-n4", null,
                "POST /api/notifications/inbox/" + NOTIFICATION1 + "/read"));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);
        GridKt._doubleClickItem(inboxGrid(), 0, 1, false, false, false, false);

        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class,
                spec -> spec.withText("Esa notificacion ya no existe o no va dirigida a ti."));
        LocatorJ._get(NotificationsView.class);
        assertTrue(LocatorJ._find(HomeView.class).isEmpty(), "sin marcar no se sigue el enlace");
    }

    @Test
    void theLineBehindANotificationOpensWithItsPayloadOnlyWithActivityRead() {
        loginAs("config.ops", "ROLE_NOTIFICATION_INBOX", "ROLE_NOTIFICATION_ACTIVITY_READ");
        stubInbox(List.of(notification(NOTIFICATION1, "Orden urgente", null, true, ActivitySeverity.CRITICAL, ActivityCategory.MAINTENANCE)));
        when(notificationClient.activityEvent(EVENT1)).thenReturn(event(EVENT1, ActivityCategory.MAINTENANCE, "maintenance.order.created",
                ActivitySeverity.CRITICAL, Map.of("type", "URGENT", "code", "MO-000012")));

        UI.getCurrent().navigate(NotificationRoutes.INBOX);
        Component actions = GridKt._getCellComponent(inboxGrid(), 0, NotificationsView.ACTIONS_COLUMN);
        LocatorJ._click(LocatorJ._get(actions, Button.class, spec -> spec.withId("event-" + NOTIFICATION1)));

        Dialog dialog = LocatorJ._get(Dialog.class, spec -> spec.withId(EventDetailDialog.ID));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("maintenance.order.created"));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("mantenimiento.tecnico (Persona)"));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("order MO-000012"));
        @SuppressWarnings("unchecked")
        Grid<EventDetailDialog.PayloadEntry> payload = LocatorJ._get(dialog, Grid.class, spec -> spec.withId(EventDetailDialog.PAYLOAD_ID));
        assertEquals(2, GridKt._size(payload));
        assertEquals(new EventDetailDialog.PayloadEntry("code", "MO-000012"), GridKt._get(payload, 0));
        assertEquals(new EventDetailDialog.PayloadEntry("type", "URGENT"), GridKt._get(payload, 1));
        dialog.close();

        // Si la linea ya no existe (ACT-404), su dialogo lo dice dentro, tambien desde la notificacion.
        doThrow(BackofficeApiException.of(HttpStatus.NOT_FOUND,
                new ApiProblem(null, "Not Found", 404, "ActivityEvent was not found", null, "ACT-404", null, null, null, false, null, null),
                "corr-n8", null, "GET /api/notifications/activity/" + EVENT1)).when(notificationClient).activityEvent(EVENT1);
        NotificationsKt.clearNotifications();
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(inboxGrid(), 0, NotificationsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("event-" + NOTIFICATION1)));
        Dialog missing = LocatorJ._get(Dialog.class, spec -> spec.withId(EventDetailDialog.ID));
        LocatorJ._get(missing, Span.class, spec -> spec.withId(EventDetailDialog.ERROR_ID).withText("Esa linea del registro ya no existe."));
        LocatorJ._get(missing, Span.class, spec -> spec.withText("Referencia: corr-n8"));
        assertTrue(NotificationsKt.getNotifications().isEmpty());
    }

    @Test
    void theActivityLogIsFilteredAndSortedInTheServerAndNeverOffersTheAccesses() {
        loginAs("auditor", "ROLE_NOTIFICATION_ACTIVITY_READ");
        ActivityEventDto created = event(EVENT1, ActivityCategory.MAINTENANCE, "maintenance.order.created", ActivitySeverity.CRITICAL, Map.of());
        ActivityEventDto burst = new ActivityEventDto(EVENT2, 119L, "mto-configuration", "burst:7", ActivityCategory.of("FIELD"),
                "configuration.profile.updated", ActivitySeverity.INFO, NOTIFIED_AT, NOTIFIED_AT, new ActorDto(ActorKind.SYSTEM, null, null),
                new SubjectDto("profile", null, null), "job-1", 12645, Map.of("sampleIds", List.of(1, 2)), EVENT1);
        when(notificationClient.activity(any(ActivityFilter.class), anyInt(), anyInt(), anyList()))
                .thenAnswer(call -> page(List.of(created, burst), call.getArgument(1), call.getArgument(2)));

        UI.getCurrent().navigate(NotificationRoutes.ACTIVITY);

        Grid<ActivityEventDto> grid = activityGrid();
        assertEquals(2, GridKt._size(grid));
        verify(notificationClient, atLeastOnce()).activity(eq(ActivityFilter.NONE), eq(0), anyInt(), eq(List.of("occurredAt,desc")));
        LocatorJ._get(Span.class, spec -> spec.withText("2 eventos"));
        List<String> first = GridKt._getFormattedRow(grid, 0);
        assertTrue(first.containsAll(List.of("Mantenimiento", "maintenance.order.created", "Critica", "mantenimiento.tecnico", "order MO-000012",
                "mto-maintenance")), first.toString());
        List<String> second = GridKt._getFormattedRow(grid, 1);
        assertTrue(second.contains("Desconocido"), "una categoria nueva se pinta como desconocida: " + second);
        assertTrue(second.containsAll(List.of("Sistema", "x12645", "Fundida")), second.toString());
        @SuppressWarnings("unchecked")
        ComboBox<ActivityCategory> category = LocatorJ._get(ComboBox.class, spec -> spec.withId("activity-category"));
        List<ActivityCategory> offered = category.getListDataView().getItems().toList();
        assertFalse(offered.contains(ActivityCategory.ACCESS), "los accesos van por su pantalla; aqui el servicio los rechaza: " + offered);
        assertFalse(offered.contains(ActivityCategory.UNKNOWN), offered.toString());
        assertTrue(offered.containsAll(List.of(ActivityCategory.USERS, ActivityCategory.SYSTEM)), offered.toString());

        clearInvocations(notificationClient);
        LocatorJ._setValue(LocatorJ._get(ComboBox.class, spec -> spec.withId("activity-severity")), ActivitySeverity.CRITICAL);
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("activity-actor")), "mantenimiento.tecnico");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("activity-source")), " mto-maintenance ");
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("activity-include-superseded")), true);
        GridKt._size(grid);
        verify(notificationClient, atLeastOnce()).activity(eq(new ActivityFilter(null, null, "mantenimiento.tecnico", null, null,
                ActivitySeverity.CRITICAL, "mto-maintenance", null, null, true)), anyInt(), anyInt(), anyList());

        grid.sort(List.of(new GridSortOrder<>(grid.getColumnByKey("type"), SortDirection.ASCENDING)));
        GridKt._get(grid, 0);
        verify(notificationClient, atLeastOnce()).activity(any(ActivityFilter.class), anyInt(), anyInt(), eq(List.of("type,asc")));

        // La fila no trae el payload: la linea entera se pide a su id, como en mto-frontend.
        when(notificationClient.activityEvent(EVENT1)).thenReturn(event(EVENT1, ActivityCategory.MAINTENANCE, "maintenance.order.created",
                ActivitySeverity.CRITICAL, Map.of("code", "MO-000012")));
        GridKt._clickItem(grid, 0, 1, false, false, false, false);
        verify(notificationClient, never()).activityEvent(any());
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, RowActions.OPEN_COLUMN), Button.class,
                spec -> spec.withId("open-" + EVENT1)));
        verify(notificationClient).activityEvent(EVENT1);
        Dialog dialog = LocatorJ._get(Dialog.class, spec -> spec.withId(EventDetailDialog.ID));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("corr-1"));
        Grid<?> payload = LocatorJ._get(dialog, Grid.class, spec -> spec.withId(EventDetailDialog.PAYLOAD_ID));
        assertEquals(1, GridKt._size(payload));
        dialog.close();

        // Una linea que ya no existe (ACT-404) abre su dialogo, que lo dice dentro con su referencia, sin
        // aviso aparte, como en mto-frontend.
        ApiProblem gone = new ApiProblem(null, "Not Found", 404, "ActivityEvent was not found", null, "ACT-404", null, null, null, false, null, null);
        when(notificationClient.activityEvent(EVENT2)).thenThrow(BackofficeApiException.of(HttpStatus.NOT_FOUND, gone, "corr-n7", null,
                "GET /api/notifications/activity/" + EVENT2));
        NotificationsKt.clearNotifications();
        GridKt._doubleClickItem(grid, 1, 1, false, false, false, false);
        Dialog missing = LocatorJ._get(Dialog.class, spec -> spec.withId(EventDetailDialog.ID));
        assertEquals("Linea del registro", missing.getHeaderTitle());
        LocatorJ._get(missing, Span.class, spec -> spec.withId(EventDetailDialog.ERROR_ID).withText("Esa linea del registro ya no existe."));
        LocatorJ._get(missing, Span.class, spec -> spec.withText("Referencia: corr-n7"));
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "el error esta en el dialogo, no en un aviso");
    }

    /** Los enlaces de las reglas llegan con el usuario o la IP en la URL; el resto de filtros se manda al servicio. */
    @Test
    void theAccessesAreListedWithTheUserFromTheLinkAndFilteredInTheServer() {
        loginAs("usuarios.responsable", "ROLE_NOTIFICATION_ACCESS_READ");
        AccessEventDto failed = access(EVENT1, "access.login.failed", AccessOutcome.FAILURE, ActivitySeverity.WARNING, 1);
        AccessEventDto streak = access(EVENT2, "access.login.streak", AccessOutcome.of("BLOCKED"), ActivitySeverity.CRITICAL, 3);
        when(notificationClient.access(any(AccessFilter.class), anyInt(), anyInt(), anyList()))
                .thenAnswer(call -> page(List.of(failed, streak), call.getArgument(1), call.getArgument(2)));

        NotificationLinks.open(UI.getCurrent(), "/actividad/accesos?username=config.lector");

        LocatorJ._get(AccessView.class);
        Grid<AccessEventDto> grid = accessGrid();
        assertEquals("config.lector", LocatorJ._get(TextField.class, spec -> spec.withId("access-username")).getValue());
        assertEquals(2, GridKt._size(grid));
        verify(notificationClient, atLeastOnce()).access(eq(new AccessFilter("config.lector", null, null, null, null, null)), eq(0),
                anyInt(), eq(List.of("occurredAt,desc")));
        List<String> row = GridKt._getFormattedRow(grid, 0);
        assertTrue(row.containsAll(List.of("access.login.failed", "Fallido", "config.lector", "10.0.0.7", "Aviso")), row.toString());
        List<String> streakRow = GridKt._getFormattedRow(grid, 1);
        assertTrue(streakRow.containsAll(List.of("Desconocido", "x3")), "un resultado nuevo se pinta como desconocido: " + streakRow);
        LocatorJ._get(Span.class, spec -> spec.withText("2 accesos"));

        clearInvocations(notificationClient);
        LocatorJ._setValue(LocatorJ._get(ComboBox.class, spec -> spec.withId("access-outcome")), AccessOutcome.FAILURE);
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("access-ip")), "10.0.0.7");
        GridKt._size(grid);
        verify(notificationClient, atLeastOnce()).access(eq(new AccessFilter("config.lector", "10.0.0.7", null, AccessOutcome.FAILURE, null, null)),
                anyInt(), anyInt(), anyList());

        // Una IP se busca entera, como en mto-frontend: a medio escribir no se pide nada y la lista sigue.
        clearInvocations(notificationClient);
        TextField ip = LocatorJ._get(TextField.class, spec -> spec.withId("access-ip"));
        LocatorJ._setValue(ip, "10.0.0");
        assertEquals(2, GridKt._size(grid));
        assertTrue(ip.isInvalid());
        verify(notificationClient, never()).access(argThat(filter -> filter != null && "10.0.0".equals(filter.ipAddress())), anyInt(), anyInt(),
                anyList());
        assertTrue(AccessView.isIpLiteral("::ffff:10.0.0.7") && AccessView.isIpLiteral("fe80::1") && !AccessView.isIpLiteral("10.0.0.256")
                && !AccessView.isIpLiteral("intranet.local"));
        LocatorJ._setValue(ip, "10.0.0.7");
        assertFalse(ip.isInvalid());

        GridKt._doubleClickItem(grid, 0, 1, false, false, false, false);
        Dialog dialog = LocatorJ._get(Dialog.class, spec -> spec.withId(EventDetailDialog.ID));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("10.0.0.7"));
        LocatorJ._get(dialog, Span.class, spec -> spec.withText("Fallido"));
        @SuppressWarnings("unchecked")
        Grid<EventDetailDialog.PayloadEntry> payload = LocatorJ._get(dialog, Grid.class, spec -> spec.withId(EventDetailDialog.PAYLOAD_ID));
        assertEquals(new EventDetailDialog.PayloadEntry("error", "invalid_user_credentials"), GridKt._get(payload, 0));
        dialog.close();

        // Entrar por otro enlace parte de cero: lo que no viene en la URL se queda vacio.
        NotificationLinks.open(UI.getCurrent(), "/actividad/accesos?ipAddress=10.0.0.9");
        assertEquals("", LocatorJ._get(TextField.class, spec -> spec.withId("access-username")).getValue());
        assertEquals("10.0.0.9", LocatorJ._get(TextField.class, spec -> spec.withId("access-ip")).getValue());
        assertNull(LocatorJ._get(ComboBox.class, spec -> spec.withId("access-outcome")).getValue());
    }
}

package com.alejandro.mtobackoffice.client;

import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationSelection;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationOutcome;
import com.alejandro.mtobackoffice.client.dto.maintenance.DisconnectorDrive;
import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.JobsClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackAccessPointClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobFamily;
import com.alejandro.mtobackoffice.client.dto.jobs.JobStatus;
import com.alejandro.mtobackoffice.client.dto.jobs.JobType;
import com.alejandro.mtobackoffice.client.dto.jobs.UploadedFile;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftVehicleDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.VehicleParkingRequest;
import com.alejandro.mtobackoffice.client.dto.master.BusinessEntityDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointKind;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointSide;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.alejandro.mtobackoffice.client.dto.resources.AccessPointDto;
import com.alejandro.mtobackoffice.client.dto.resources.AccessPointKind;
import com.alejandro.mtobackoffice.client.dto.resources.DrivingLicence;
import com.alejandro.mtobackoffice.client.dto.resources.FleetPositionDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentFilter;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentRequest;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationTreeNodeDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitRequest;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteFilter;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonEmployer;
import com.alejandro.mtobackoffice.client.dto.resources.PersonFilter;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationKind;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeRequest;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.RailGearType;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberLeaveRequest;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberRequest;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMembersRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleAttachment;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCapability;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationSource;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOperationalStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOwner;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.WorkDiscipline;
import com.alejandro.mtobackoffice.client.error.ApiErrorDecoder;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.error.ServiceUnavailableApiException;
import com.alejandro.mtobackoffice.client.error.SessionExpiredApiException;
import com.alejandro.mtobackoffice.client.error.TooManyRequestsApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.maintenance.OperationClient;
import com.alejandro.mtobackoffice.client.resources.AccessPointClient;
import com.alejandro.mtobackoffice.client.resources.InstrumentClient;
import com.alejandro.mtobackoffice.client.resources.OrganizationClient;
import com.alejandro.mtobackoffice.client.resources.ParkingSiteClient;
import com.alejandro.mtobackoffice.client.resources.PersonClient;
import com.alejandro.mtobackoffice.client.resources.QualificationTypeClient;
import com.alejandro.mtobackoffice.client.resources.VehicleClient;
import com.alejandro.mtobackoffice.client.resources.VehicleTypeClient;
import com.alejandro.mtobackoffice.configuration.client.GatewayClientConfiguration;
import com.alejandro.mtobackoffice.configuration.client.UserTokenProvider;
import com.alejandro.mtobackoffice.configuration.security.CurrentPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDriveType;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDto;
import java.util.ArrayList;
import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentDirection;
import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyAvailabilityDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyComponentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyDto;
import com.alejandro.mtobackoffice.client.dto.stock.AssemblyRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.EntryRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialStockDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementType;
import com.alejandro.mtobackoffice.client.dto.stock.OutputRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationStatus;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.RevisionOperation;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierDto;
import com.alejandro.mtobackoffice.client.dto.stock.TransferRequest;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseDto;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.MovementClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.ReservationClient;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.client.dto.users.ClientDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleDto;
import com.alejandro.mtobackoffice.client.dto.users.CreateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.ExecuteActionsEmailRequest;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileDto;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileSummaryDto;
import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.ResetPasswordRequest;
import com.alejandro.mtobackoffice.client.dto.users.RoleNamesRequest;
import com.alejandro.mtobackoffice.client.dto.users.UpdateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserCredentialDto;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserRolesDto;
import com.alejandro.mtobackoffice.client.dto.users.UserSessionDto;
import com.alejandro.mtobackoffice.client.dto.users.UsersPage;
import com.alejandro.mtobackoffice.client.error.ConflictApiException;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.maintenance.AssetClient;
import com.alejandro.mtobackoffice.client.maintenance.MaintenanceCatalogClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.client.maintenance.ShiftClient;
import com.alejandro.mtobackoffice.client.maintenance.DefectClient;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.client.dto.notification.AccessEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.AccessFilter;
import com.alejandro.mtobackoffice.client.dto.notification.AccessOutcome;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityCategory;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityFilter;
import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.dto.notification.ActorKind;
import com.alejandro.mtobackoffice.client.dto.notification.InboxFilter;
import com.alejandro.mtobackoffice.client.dto.notification.InboxItemDto;
import com.alejandro.mtobackoffice.client.dto.notification.ReadAllDto;
import com.alejandro.mtobackoffice.client.dto.notification.UnreadCountDto;
import com.alejandro.mtobackoffice.client.maintenance.InspectionClient;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateCorrectiveOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateDefectFromInspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionKind;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ResolveDefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.StockRequestType;
import com.alejandro.mtobackoffice.client.dto.maintenance.StockSyncStatus;
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
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskMaterialRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssignOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.CommentRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceTaskStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.PlanOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StatusHistoryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.FunctionalGroup;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.SectionInsulatorInstallation;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUnit;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TrackKind;
import com.alejandro.mtobackoffice.client.dto.maintenance.MonthlyReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ProgressReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReportFormat;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskEquipment;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.maintenance.ReportClient;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import com.alejandro.mtobackoffice.client.dto.master.TrackSchematicDto;
import com.alejandro.mtobackoffice.client.dto.resources.AbsenceKind;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PositionDiscrepancyDto;
import com.alejandro.mtobackoffice.client.dto.resources.PositionDiscrepancyReportDto;
import com.alejandro.mtobackoffice.client.dto.resources.RepairKind;
import com.alejandro.mtobackoffice.client.dto.resources.RepairStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairUpdateRequest;
import com.alejandro.mtobackoffice.client.resources.ResourcesReportClient;
import com.alejandro.mtobackoffice.ui.maintenance.TaskGeneration;

/**
 * Las interfaces {@code @HttpExchange} y el RestClient reales contra un servidor simulado: sin
 * contexto de Spring, con la misma fabrica que produccion, para que se prueben el Bearer, la
 * correlacion y el traductor de errores de verdad.
 */
class ClientLayerTest {

    private static final String GATEWAY = "http://gateway";
    private static final String PRINCIPAL = "config.responsable";
    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private MockRestServiceServer server;
    private RestClient restClient;
    private LovClient lovClient;
    private StationClient stationClient;
    private TrackClient trackClient;
    private ProfileClient profileClient;
    private BusinessEntityClient businessEntityClient;
    private JobsClient jobsClient;
    private UsersClient usersClient;
    private MaterialClient materialClient;
    private WarehouseClient warehouseClient;
    private SupplierClient supplierClient;
    private ProjectClient projectClient;
    private AssemblyClient assemblyClient;
    private MovementClient movementClient;
    private ReservationClient reservationClient;
    private OrderClient orderClient;
    private AssetClient assetClient;
    private MaintenanceCatalogClient maintenanceCatalogClient;
    private ShiftClient shiftClient;
    private InspectionClient inspectionClient;
    private DefectClient defectClient;
    private ReportClient reportClient;
    private NotificationClient notificationClient;
    private PersonClient personClient;
    private OrganizationClient organizationClient;
    private VehicleTypeClient vehicleTypeClient;
    private ParkingSiteClient parkingSiteClient;
    private VehicleClient vehicleClient;
    private OperationClient operationClient;
    private QualificationTypeClient qualificationTypeClient;
    private InstrumentClient instrumentClient;
    private ResourcesReportClient resourcesReportClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        UserTokenProvider tokens = new UserTokenProvider(null, "keycloak") {
            @Override
            public String accessToken(String principalName) {
                return "token-for-" + principalName;
            }
        };
        ApiErrorDecoder decoder = new ApiErrorDecoder(new JsonMapper(), "X-Correlation-Id");
        restClient = GatewayClientConfiguration.gatewayRestClient(builder, GATEWAY, "X-Correlation-Id", tokens, decoder);
        lovClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(LovClient.class);
        stationClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(StationClient.class);
        trackClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(TrackClient.class);
        profileClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ProfileClient.class);
        businessEntityClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(BusinessEntityClient.class);
        jobsClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(JobsClient.class);
        usersClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(UsersClient.class);
        materialClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(MaterialClient.class);
        warehouseClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(WarehouseClient.class);
        supplierClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(SupplierClient.class);
        projectClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ProjectClient.class);
        assemblyClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(AssemblyClient.class);
        movementClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(MovementClient.class);
        reservationClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ReservationClient.class);
        orderClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(OrderClient.class);
        assetClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(AssetClient.class);
        maintenanceCatalogClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(MaintenanceCatalogClient.class);
        shiftClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ShiftClient.class);
        inspectionClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(InspectionClient.class);
        defectClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(DefectClient.class);
        reportClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ReportClient.class);
        notificationClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(NotificationClient.class);
        personClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(PersonClient.class);
        organizationClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(OrganizationClient.class);
        vehicleTypeClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(VehicleTypeClient.class);
        parkingSiteClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ParkingSiteClient.class);
        vehicleClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(VehicleClient.class);
        operationClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(OperationClient.class);
        qualificationTypeClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(QualificationTypeClient.class);
        instrumentClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(InstrumentClient.class);
        resourcesReportClient = GatewayClientConfiguration.proxyFactory(restClient).createClient(ResourcesReportClient.class);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static <T> T asUser(Supplier<T> call) {
        return CurrentPrincipal.callAs(PRINCIPAL, call);
    }

    @Test
    void lovListIsReadThroughTheGatewayPrefixAndKeepsWhatItDoesNotModel() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profile-statuses"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":1,"code":"DRAFT","description":"Borrador","type":"ProfileStatus","enabled":true,
                          "createDate":"2026-08-01T10:15:30","versionDate":"2026-08-02T11:00:00","versionUser":"config.responsable",
                          "versionNumber":0,"unknownTomorrow":{"x":1}}]
                        """, MediaType.APPLICATION_JSON));

        List<LovDto> statuses = asUser(() -> lovClient.findAll(LovResource.PROFILE_STATUSES));

        assertEquals(1, statuses.size());
        LovDto status = statuses.getFirst();
        assertEquals(new LovDto(1L, "DRAFT", "Borrador", true, 0, LocalDateTime.parse("2026-08-02T11:00:00"), "config.responsable",
                status.extras()), status);
        // Lo que no tiene campo se guarda para devolverlo en un PUT, que sustituye la entrada entera.
        assertEquals(Set.of("type", "createDate", "unknownTomorrow"), status.extras().keySet());
        server.verify();
    }

    @Test
    void anUpdateSendsBackWhatTheEntryDoesNotModelAndTheParentTypeById() {
        server.expect(requestTo(GATEWAY + "/api/configuration/foundations"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":5,"code":"C-1","description":"Cimentacion 1","enabled":true,"versionNumber":2,
                          "drawingNumber":"D-12","foundationType":{"id":3,"code":"FT-A","description":"Tipo A"}}]
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/foundations/5"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.description").value("Cimentacion uno"))
                .andExpect(jsonPath("$.versionNumber").value(2))
                .andExpect(jsonPath("$.drawingNumber").value("D-12"))
                .andExpect(jsonPath("$.foundationType.id").value(7))
                .andExpect(jsonPath("$.foundationType.code").doesNotExist())
                .andExpect(jsonPath("$.extras").doesNotExist())
                .andRespond(withSuccess("""
                        {"id":5,"code":"C-1","description":"Cimentacion uno","enabled":true,"versionNumber":3,
                          "drawingNumber":"D-12","foundationType":{"id":7,"code":"FT-B","description":"Tipo B"}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/foundations/bulk"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$[0].enabled").value(false))
                .andExpect(jsonPath("$[0].drawingNumber").value("D-12"))
                .andExpect(jsonPath("$[0].foundationType.id").value(7))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        LovDto read = asUser(() -> lovClient.findAll(LovResource.FOUNDATIONS)).getFirst();
        assertEquals("FT-A", read.parent("foundationType").orElseThrow().code());
        LovDto saved = asUser(() -> lovClient.update("foundations", 5L,
                read.withValues("C-1", "Cimentacion uno", true).withParent("foundationType", 7L)));
        assertEquals("FT-B", saved.parent("foundationType").orElseThrow().code());
        asUser(() -> lovClient.bulkUpdate("foundations", List.of(saved.withEnabled(false))));
        server.verify();
    }

    @Test
    void createPostsOnlyTheFilledFieldsAndReadsThe201Body() {
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.code").value("PT9"))
                .andExpect(jsonPath("$.description").value("Poste tipo 9"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.versionDate").doesNotExist())
                .andExpect(jsonPath("$.versionNumber").doesNotExist())
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"id":42,"code":"PT9","description":"Poste tipo 9","enabled":true,"versionNumber":0}
                                """));

        LovDto created = asUser(() -> lovClient.create("pole-types", LovDto.forCreate("PT9", "Poste tipo 9", true)));

        assertEquals(42L, created.id());
        assertTrue(created.isEnabled());
    }

    @Test
    void updatePutsToTheIdPathWithTheVersionReadAndDeleteIsA204() {
        // El versionNumber leido viaja tal cual: es el bloqueo optimista del servicio.
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/42"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.enabled").value(false))
                .andExpect(jsonPath("$.versionNumber").value(3))
                .andRespond(withSuccess("""
                        {"id":42,"code":"PT9","description":"Poste tipo 9 (baja)","enabled":false,"versionNumber":4}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/42"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        LovDto existing = new LovDto(42L, "PT9", "Poste tipo 9", true, 3, null, null);
        LovDto updated = asUser(() -> lovClient.update("pole-types", 42L, existing.withValues("PT9", "Poste tipo 9 (baja)", false)));
        assertFalse(updated.isEnabled());
        assertEquals(4, updated.versionNumber(), "la respuesta trae la version que se acaba de escribir");

        asUser(() -> {
            lovClient.delete("pole-types", 42L);
            return null;
        });
        server.verify();
    }

    @Test
    void bulkCreateAndBulkUpdateUseTheBulkPath() {
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/bulk"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].code").value("PT2"))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\":1,\"code\":\"PT1\",\"enabled\":true,\"versionNumber\":1},"
                                + "{\"id\":2,\"code\":\"PT2\",\"enabled\":true,\"versionNumber\":1}]"));
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/bulk"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].enabled").value(false))
                .andExpect(jsonPath("$[0].versionNumber").value(1))
                .andRespond(withSuccess("[{\"id\":1,\"code\":\"PT1\",\"enabled\":false,\"versionNumber\":2}]", MediaType.APPLICATION_JSON));

        List<LovDto> created = asUser(() -> lovClient.bulkCreate("pole-types",
                List.of(LovDto.forCreate("PT1", "Uno", true), LovDto.forCreate("PT2", "Dos", true))));
        assertEquals(2, created.size());

        List<LovDto> updated = asUser(() -> lovClient.bulkUpdate("pole-types", List.of(created.getFirst().withEnabled(false))));
        assertFalse(updated.getFirst().isEnabled());
        server.verify();
    }

    /**
     * Los dos 409 de mto-configuration son la misma excepcion y solo el codigo los distingue:
     * CON-001 es una version vieja (recargar lo arregla) y BUS-002, un valor unico repetido o una
     * entrada en uso (recargar no lo arregla). Cuerpos tal como los escribe su RestExceptionHandler.
     */
    @Test
    void aStaleVersionAndARepeatedValueAreBothConflictsToldApartByTheirCode() {
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/42"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.versionNumber").value(3))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("""
                                {"type":"https://api.mto-configuration/errors/con-001","title":"Conflicto de concurrencia",
                                 "status":409,"detail":"Conflicto de concurrencia detectado. Inténtelo de nuevo.",
                                 "instance":"/api/v1/configuration/pole-types/42","code":"CON-001","traceId":"t-409",
                                 "retryable":true}
                                """));
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("""
                                {"type":"https://api.mto-configuration/errors/bus-002","title":"Regla de negocio incumplida",
                                 "status":409,"detail":"La operación entra en conflicto con un registro existente: valor único repetido o referencia en uso",
                                 "instance":"/api/v1/configuration/pole-types","code":"BUS-002","traceId":"t-dup",
                                 "retryable":false}
                                """));

        LovDto read = new LovDto(42L, "PT9", "Poste tipo 9", true, 3, null, null);
        ConflictApiException stale = assertThrows(ConflictApiException.class,
                () -> asUser(() -> lovClient.update("pole-types", 42L, read.withValues("PT9", "Otra", true))));
        ConflictApiException repeated = assertThrows(ConflictApiException.class,
                () -> asUser(() -> lovClient.create("pole-types", LovDto.forCreate("PT9", "Repetido", true))));

        assertEquals("CON-001", stale.getProblem().code());
        assertEquals(Boolean.TRUE, stale.getProblem().retryable());
        assertEquals("t-409", stale.getReference());
        assertEquals("BUS-002", repeated.getProblem().code());
        assertEquals(Boolean.FALSE, repeated.getProblem().retryable());
        server.verify();
    }

    @Test
    void findByIdAndFindByCodeHaveTheirOwnPaths() {
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/7"))
                .andRespond(withSuccess("{\"id\":7,\"code\":\"PT7\",\"enabled\":true}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/code/PT7"))
                .andRespond(withSuccess("{\"id\":7,\"code\":\"PT7\",\"enabled\":true}", MediaType.APPLICATION_JSON));

        assertEquals("PT7", asUser(() -> lovClient.findById("pole-types", 7L)).code());
        assertEquals(7L, asUser(() -> lovClient.findByCode("pole-types", "PT7")).id());
        server.verify();
    }

    @Test
    void bearerAndCorrelationHeadersTravelWithEveryCall() {
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-for-" + PRINCIPAL))
                .andExpect(header("X-Correlation-Id", matchesRegex(UUID_PATTERN)))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertTrue(asUser(() -> lovClient.findAll(LovResource.POLE_TYPES)).isEmpty());
        server.verify();
    }

    @Test
    void withoutAPrincipalNothingLeavesTheApplication() {
        SessionExpiredApiException exception = assertThrows(SessionExpiredApiException.class,
                () -> lovClient.findAll(LovResource.POLE_TYPES));

        assertTrue(exception.getMessage().contains("CurrentPrincipal.runAs"));
        server.verify();
    }

    @Test
    void pageResponseReadsTheDtoShapeAndToleratesFirstAndLast() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/paged?page=0&size=20"))
                .andRespond(withSuccess("""
                        {"content":[{"id":7,"code":"P-7"}],
                         "page":{"size":20,"number":0,"totalElements":11715,"totalPages":586,"first":true,"last":false}}
                        """, MediaType.APPLICATION_JSON));

        PageResponse<LovDto> page = asUser(() -> restClient.get()
                .uri("/api/configuration/profiles/paged?page=0&size=20")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));

        assertEquals(1, page.content().size());
        assertEquals(20, page.page().size());
        assertEquals(0, page.page().number());
        assertEquals(11715L, page.page().totalElements());
        assertEquals(586, page.page().totalPages());
    }

    @Test
    void problemJsonFromConfigurationBecomesATypedException() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/7"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .header("X-Correlation-Id", "corr-1")
                        .body("""
                                {"type":"https://api.mto-configuration/errors/not-001","title":"Recurso no encontrado",
                                 "status":404,"detail":"Perfil 7 no existe","instance":"/api/v1/configuration/profiles/7",
                                 "code":"NOT-001","traceId":"abc123","timestamp":"2026-09-20T10:15:30.123456Z","retryable":false}
                                """));

        NotFoundApiException exception = assertThrows(NotFoundApiException.class,
                () -> asUser(() -> restClient.get().uri("/api/configuration/profiles/7").retrieve().body(String.class)));

        assertEquals("NOT-001", exception.getProblem().code());
        assertEquals("Perfil 7 no existe", exception.getProblem().detail());
        assertEquals(Instant.parse("2026-09-20T10:15:30.123456Z"), exception.getProblem().timestamp());
        assertEquals(Boolean.FALSE, exception.getProblem().retryable());
        assertEquals("abc123", exception.getReference(), "el traceId del servicio manda sobre la correlacion");
        assertEquals("corr-1", exception.getCorrelationId());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("GET /api/configuration/profiles/7"));
    }

    @Test
    void validationErrorsAreMappedFieldByField() {
        server.expect(requestTo(GATEWAY + "/api/configuration/tracks"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("""
                                {"type":"https://api.mto-configuration/errors/val-000","title":"Peticion invalida","status":400,
                                 "detail":"Validation failed","code":"VAL-000","traceId":"t-1","retryable":false,
                                 "errors":[{"field":"name","code":"VAL-001","message":"El nombre es obligatorio"},
                                           {"field":"tracks[0].name","code":"VAL-002","message":"Formato invalido"}]}
                                """));

        ValidationApiException exception = assertThrows(ValidationApiException.class,
                () -> asUser(() -> restClient.post().uri("/api/configuration/tracks")
                        .contentType(MediaType.APPLICATION_JSON).body("{}").retrieve().body(String.class)));

        assertEquals(2, exception.getProblem().errors().size());
        assertEquals("name", exception.getProblem().errors().getFirst().field());
        assertEquals("VAL-001", exception.getProblem().errors().getFirst().code());
        assertEquals("Formato invalido", exception.getProblem().errors().get(1).message());
    }

    @Test
    void gatewayProblemWithoutCodeIsTolerated() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profile-statuses"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("""
                                {"type":"about:blank","title":"Unauthorized","status":401,"detail":"Unauthorized",
                                 "instance":"/api/configuration/profile-statuses","correlationId":"corr-9"}
                                """));

        SessionExpiredApiException exception = assertThrows(SessionExpiredApiException.class,
                () -> asUser(() -> lovClient.findAll(LovResource.PROFILE_STATUSES)));

        assertNull(exception.getProblem().code());
        assertEquals("corr-9", exception.getReference());
    }

    @Test
    void gatewayFallback503CarriesRetryAfterAndTheService() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profile-statuses"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .header(HttpHeaders.RETRY_AFTER, "30")
                        .header("X-Correlation-Id", "corr-5")
                        .body("""
                                {"type":"about:blank","title":"Service Unavailable","status":503,
                                 "detail":"El servicio mto-configuration no est\\u00e1 disponible en este momento.",
                                 "instance":"/api/configuration/profile-statuses","service":"mto-configuration","correlationId":"corr-5"}
                                """));

        ServiceUnavailableApiException exception = assertThrows(ServiceUnavailableApiException.class,
                () -> asUser(() -> lovClient.findAll(LovResource.PROFILE_STATUSES)));

        assertEquals(Duration.ofSeconds(30), exception.getRetryAfter().orElseThrow());
        assertEquals("mto-configuration", exception.getProblem().service());
        assertEquals("corr-5", exception.getCorrelationId());
    }

    @Test
    void aNonJsonErrorBodyStillBecomesAnException() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profile-statuses"))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY)
                        .contentType(MediaType.TEXT_HTML)
                        .body("<html>Bad Gateway</html>"));

        BackofficeApiException exception = assertThrows(ServiceUnavailableApiException.class,
                () -> asUser(() -> lovClient.findAll(LovResource.PROFILE_STATUSES)));

        assertTrue(exception.getProblem().detail().contains("Bad Gateway"));
        assertFalse(exception.getProblem().hasFieldErrors());
    }

    // --- Maestros de infraestructura ------------------------------------------------------------

    /** La subinterfaz vacia pone la ruta y el tipo; Spring resuelve el generico y la pagina llega tipada. */
    @Test
    void masterListsPostTheFilterWithSpringDataPagingAndComeBackTyped() {
        server.expect(requestTo(matchesRegex("http://gateway/api/configuration/stations/filter\\?page=1&size=20&sort=name(,|%2C)asc&sort=id(,|%2C)desc")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.searchText").value("ato"))
                .andExpect(jsonPath("$.enabled").doesNotExist())
                .andExpect(jsonPath("$.name").doesNotExist())
                .andRespond(withSuccess("""
                        {"content":[{"id":4,"name":"ATOCHA","executionPackageId":100,"tracks":null,"disconnectors":null,
                                     "sectionInsulators":null,"versionNumber":3,"versionUser":"config.responsable",
                                     "versionDate":"2026-08-02T11:00:00","brandNewField":"x"}],
                         "page":{"size":20,"number":1,"totalElements":21,"totalPages":2}}
                        """, MediaType.APPLICATION_JSON));

        PageResponse<StationDto> page = asUser(() -> stationClient.filter(1, 20,
                MasterFilters.sort(List.of(new QuerySortOrder("name", SortDirection.ASCENDING), new QuerySortOrder("id", SortDirection.DESCENDING))),
                MasterFilters.of("searchText", " ato ", "enabled", null, "name", "  ")));

        StationDto station = page.content().getFirst();
        assertEquals("ATOCHA", station.getName());
        assertEquals(100L, station.getExecutionPackageId());
        assertEquals(3, station.getVersionNumber());
        assertNull(station.getTracks(), "la fila de una lista llega sin hijos");
        assertEquals("x", station.extras().get("brandNewField"), "lo que la UI no conoce se guarda aparte");
        assertEquals(21L, page.page().totalElements());
        server.verify();
    }

    /**
     * Sin orden no viaja {@code sort}, y el servicio aplica el suyo. Es lo que pide el recuento de cada
     * lista, y la primera pagina mientras nadie ordena por una columna. Con {@code sort} obligatorio, la
     * interfaz HTTP rechazaba la lista vacia antes de llamar ({@code Missing request parameter value
     * 'sort'}), y las seis listas de Infraestructura decian «Error interno» al abrirse.
     */
    @Test
    void aMasterListWithoutSortOrderGoesOutWithoutTheSortParameter() {
        server.expect(requestTo(GATEWAY + "/api/configuration/tracks/filter?page=0&size=1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"content":[{"id":98,"name":"TRACK 3 TLV SAVIDOR","executionPackageId":8,"stationIds":[7],
                                     "profiles":null,"versionNumber":1}],
                         "page":{"size":1,"number":0,"totalElements":176,"totalPages":176}}
                        """, MediaType.APPLICATION_JSON));

        PageResponse<TrackDto> page = asUser(() -> trackClient.filter(0, 1, MasterFilters.sort(List.of()), MasterFilters.of()));

        assertEquals(176L, page.page().totalElements());
        server.verify();
    }

    /** README_API §4: lee, modifica sobre lo leido y devuelvelo entero; los hijos que no se tocan van a null. */
    @Test
    void updatingAMasterSendsBackWhatWasReadWithItsUnknownFieldsAndItsChildrenLeftAlone() {
        server.expect(requestTo(GATEWAY + "/api/configuration/tracks/3"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":3,"name":"VIA 1","enabled":true,"executionPackageId":100,"stationIds":[12,13],
                         "profiles":[{"id":1,"profileId":"P-001","kp":"10.500","cantilevers":[]}],
                         "versionNumber":7,"createUser":"importador","fieldOfTomorrow":{"deep":[1,2]}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/tracks/3"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("VIA PRINCIPAL"))
                .andExpect(jsonPath("$.versionNumber").value(7))
                .andExpect(jsonPath("$.stationIds[0]").value(12))
                .andExpect(jsonPath("$.stationIds[1]").value(13))
                .andExpect(jsonPath("$.profiles").value(nullValue()))
                .andExpect(jsonPath("$.fieldOfTomorrow.deep[1]").value(2))
                .andExpect(jsonPath("$.createUser").value("importador"))
                .andRespond(withSuccess("""
                        {"id":3,"name":"VIA PRINCIPAL","enabled":true,"executionPackageId":100,"stationIds":[12,13],"profiles":null,"versionNumber":8}
                        """, MediaType.APPLICATION_JSON));

        TrackDto track = asUser(() -> trackClient.findById(3L));
        assertEquals(1, track.getProfiles().size(), "el detalle trae los hijos");
        track.setName("VIA PRINCIPAL");
        track.forgetChildren();
        TrackDto saved = asUser(() -> trackClient.save(track));

        assertEquals(8, saved.getVersionNumber());
        server.verify();
    }

    /** README_API §6 del servicio: el esquema de una via es una llamada, con sus records anidados y lo desconocido fuera. */
    @Test
    void theSchematicOfATrackIsOneCallWithItsNestedRecords() {
        server.expect(requestTo(GATEWAY + "/api/configuration/tracks/3/schematic"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-for-" + PRINCIPAL))
                .andExpect(header("X-Correlation-Id", matchesRegex(UUID_PATTERN)))
                .andRespond(withSuccess("""
                        {"trackId":3,"trackName":"VIA 1","enabled":true,"executionPackageName":"EP4","stations":["ATOCHA","CHAMARTIN"],
                         "profiles":[
                           {"id":7,"code":"P-007","kp":"12.345","orderInTrack":1,"span":"55.000","poleType":"HEB","profileStatus":"OK",
                            "railPoleDistance":"-2.500","sectionings":["S1"],
                            "cantilevers":[{"id":21,"type":"PT1","stagger":"-200","cwHeight":"5300","catenaryHeight":"1400",
                                            "steadyArmType":"SA1","steadyArmLength":1200}],
                            "disconnector":{"id":40,"name":"SEC-40","onLoad":true,"function":"FEED","station":"ATOCHA",
                                            "track":"VIA 3","connectedTrack":"VIA 2"},
                            "disconnectors":[{"id":40,"name":"SEC-40","onLoad":true,"function":"FEED","station":"ATOCHA",
                                              "track":"VIA 3","connectedTrack":"VIA 2"},
                                             {"id":41,"name":"SEC-41","onLoad":false,"function":"SurgeA"}],
                            "fieldOfTomorrow":{"deep":[1]}},
                           {"id":8,"code":"P-008","kp":"70.000","orderInTrack":2,"sectionings":[],"cantilevers":[]},
                           {"id":9,"code":"P-009","kp":"80.000","orderInTrack":3,"sectionings":[],"cantilevers":[],
                            "disconnector":{"id":42,"name":"SEC-42"}}],
                         "sectionInsulators":[
                           {"id":50,"name":"AIS-50","kp":"15.000","installationType":"TRACK_CONNECTION","enabled":true,
                            "station":"ATOCHA","track":"VIA 1","connectedTrack":"VIA 2",
                            "switches":[{"id":60,"code":"W31","kp":"15.500","turnoutDenominator":9,"track":"VIA 1"}]}]}
                        """, MediaType.APPLICATION_JSON));

        TrackSchematicDto schematic = asUser(() -> trackClient.schematic(3L));

        assertEquals("VIA 1", schematic.trackName());
        assertEquals("EP4", schematic.executionPackageName());
        assertEquals(List.of("ATOCHA", "CHAMARTIN"), schematic.stations());
        assertEquals(3, schematic.profiles().size());
        TrackSchematicDto.ProfileNode first = schematic.profiles().getFirst();
        assertEquals("P-007", first.code());
        assertEquals("12.345", first.kp());
        assertEquals("-2.500", first.railPoleDistance());
        assertEquals(List.of("S1"), first.sectionings());
        assertEquals("PT1", first.cantilevers().getFirst().type());
        assertEquals(1200L, first.cantilevers().getFirst().steadyArmLength());
        assertEquals("SEC-40", first.disconnector().name());
        assertEquals("ATOCHA", first.disconnector().station());
        assertEquals("VIA 2", first.disconnector().connectedTrack(), "la otra via de uno que pone dos en paralelo (V27)");
        assertEquals("VIA 3", first.disconnector().track(), "la via que maniobra cuando no es la del poste");
        assertEquals(List.of("SEC-40", "SEC-41"), first.disconnectors().stream().map(TrackSchematicDto.DisconnectorMark::name).toList(),
                "un poste admite varios (V29), en el orden del servicio");
        assertEquals("SurgeA", first.disconnectors().get(1).function());
        TrackSchematicDto.ProfileNode second = schematic.profiles().get(1);
        assertNull(second.disconnector(), "lo que el servicio no manda es null");
        assertTrue(second.disconnectors().isEmpty(), "sin seccionadores, la lista vacia");
        assertTrue(second.cantilevers().isEmpty());
        TrackSchematicDto.ProfileNode third = schematic.profiles().get(2);
        assertEquals(List.of(42L), third.disconnectors().stream().map(TrackSchematicDto.DisconnectorMark::id).toList(),
                "un servicio anterior al V29 solo manda disconnector: es la lista de uno");
        TrackSchematicDto.InsulatorMark insulator = schematic.sectionInsulators().getFirst();
        assertEquals("AIS-50", insulator.name());
        assertEquals("VIA 2", insulator.connectedTrack());
        assertEquals("W31", insulator.switches().getFirst().code());
        assertEquals(9, insulator.switches().getFirst().turnoutDenominator());
        server.verify();
    }

    @Test
    void lovReferencesTravelAsIdAndCodeAndACreateHasNoId() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.profileId").value("P-9"))
                .andExpect(jsonPath("$.kp").value("10.500"))
                .andExpect(jsonPath("$.trackId").value(3))
                .andExpect(jsonPath("$.poleType.id").value(5))
                .andExpect(jsonPath("$.poleType.code").value("PT1"))
                .andExpect(jsonPath("$.poleType.description").doesNotExist())
                .andExpect(jsonPath("$.sectionings[1].code").value("P50"))
                .andExpect(jsonPath("$.span").value(47.97))
                .andExpect(jsonPath("$.id").value(nullValue()))
                .andExpect(jsonPath("$.cantilevers").value(nullValue()))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("""
                        {"id":99,"profileId":"P-9","kp":"10.500","trackId":3,
                         "poleType":{"id":5,"code":"PT1","description":"Poste 1","type":"PoleType","enabled":true},
                         "sectionings":[{"id":1,"code":"A/S"},{"id":2,"code":"P50"}],"cantilevers":[],"versionNumber":0}
                        """));

        ProfileDto profile = new ProfileDto();
        profile.setProfileId("P-9");
        profile.setKp("10.500");
        profile.setTrackId(3L);
        profile.setPoleType(new LovRef(5L, "PT1", null));
        profile.setSectionings(List.of(new LovRef(1L, "A/S", "Aguja"), new LovRef(2L, "P50", null)));
        profile.setSpan(new BigDecimal("47.970"));

        ProfileDto created = asUser(() -> profileClient.save(profile));

        assertEquals(99L, created.getId());
        assertEquals(new LovRef(5L, "PT1", null), created.getPoleType(), "la igualdad de una referencia es por id");
        assertEquals("Poste 1", created.getPoleType().description());
        assertEquals(2, created.getSectionings().size());
        server.verify();
    }

    @Test
    void businessEntitiesAreListedForTheExecutionPackagePicker() {
        server.expect(requestTo(GATEWAY + "/api/configuration/business-entities"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":1,"identificationNumber":"A12345678","name":"Constructora Norte","code":"CN","comercialEntityType":{"code":"X"}}]
                        """, MediaType.APPLICATION_JSON));

        List<BusinessEntityDto> companies = asUser(() -> businessEntityClient.findAll());

        assertEquals("Constructora Norte (A12345678)", companies.getFirst().label());
    }

    // --- Trabajos en segundo plano ---------------------------------------------------------------

    private static final UUID JOB_ID = UUID.fromString("6f1c0000-0000-4000-8000-000000000001");

    @Test
    void importPostsTheFileAsAMultipartPartWithDryRunInTheQuery() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/jobs/import?dryRun=true"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.MULTIPART_FORM_DATA))
                .andExpect(content().string(containsString("name=\"file\"; filename=\"profile-master.xlsx\"")))
                .andExpect(content().string(containsString("PK-xlsx-bytes")))
                .andRespond(withStatus(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body("""
                        {"id":"6f1c0000-0000-4000-8000-000000000001","type":"PROFILE_IMPORT","status":"PENDING",
                         "createdAt":"2026-08-27T09:12:03Z","processedItems":0,"successfulItems":0,"failedItems":0}
                        """));

        UploadedFile file = new UploadedFile("profile-master.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "PK-xlsx-bytes".getBytes(StandardCharsets.UTF_8));
        JobDto job = asUser(() -> jobsClient.importProfiles(file.asResource(), true));

        assertEquals(JOB_ID, job.id());
        assertEquals(JobType.PROFILE_IMPORT, job.type());
        assertEquals(JobStatus.PENDING, job.status());
        assertEquals(Optional.of(JobFamily.PROFILE_JOBS), job.family());
        assertTrue(job.itemErrors().isEmpty(), "lo omitido llega vacio, no nulo");
        assertFalse(job.isDownloadable());
        server.verify();
    }

    /** README_ASYNC_JOBS §4: sin cupo el servicio responde 429, y el trabajo rechazado viaja en el cuerpo. */
    @Test
    void a429CarriesTheRejectedJobAndTheRetryAfter() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/jobs/export?trackId=3&mapperType=basic"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.RETRY_AFTER, "30")
                        .header("X-Correlation-Id", "corr-7")
                        .body("""
                                {"id":"6f1c0000-0000-4000-8000-000000000001","type":"PROFILE_EXPORT","status":"REJECTED",
                                 "createdAt":"2026-08-27T09:12:03Z","trackId":3,"mapperType":"basic",
                                 "processedItems":0,"successfulItems":0,"failedItems":0}
                                """));

        TooManyRequestsApiException rejected = assertThrows(TooManyRequestsApiException.class,
                () -> asUser(() -> jobsClient.exportProfiles(3L, "basic")));

        assertEquals(Duration.ofSeconds(30), rejected.getRetryAfter().orElseThrow());
        assertEquals("corr-7", rejected.getCorrelationId());
        JobDto job = new JsonMapper().readValue(rejected.getBody(), JobDto.class);
        assertEquals(JobStatus.REJECTED, job.status());
        assertEquals(3L, job.trackId());
        server.verify();
    }

    @Test
    void statusIsAskedUnderTheFamilyPrefixAndAFileComesBackWithItsHeaders() {
        server.expect(requestTo(GATEWAY + "/api/configuration/master-data/republish/" + JOB_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":"6f1c0000-0000-4000-8000-000000000001","type":"MASTER_DATA_REPUBLISH","status":"RUNNING",
                         "createdAt":"2026-08-27T09:12:03Z","startedAt":"2026-08-27T09:12:04Z","totalItems":11715,
                         "processedItems":500,"successfulItems":500,"failedItems":0}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/lovs/jobs/" + JOB_ID + "/file"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"dryRun\":true}", MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lov-import-report.json\""));

        JobDto running = asUser(() -> jobsClient.status(JobFamily.REPUBLISH, JOB_ID));
        assertEquals(11715, running.totalItems());
        assertFalse(running.isTerminal());
        assertFalse(running.isDownloadable(), "un republicado no produce fichero");

        ResponseEntity<byte[]> file = asUser(() -> jobsClient.file(JobFamily.LOV_JOBS, JOB_ID));
        assertEquals("{\"dryRun\":true}", new String(file.getBody(), StandardCharsets.UTF_8));
        assertEquals("lov-import-report.json", file.getHeaders().getContentDisposition().getFilename());
        assertThrows(IllegalArgumentException.class, () -> jobsClient.file(JobFamily.REPUBLISH, JOB_ID));
        server.verify();
    }

    /** Los puntos de acceso a la via se republican como los perfiles: acotados por via, sin estacion. */
    @Test
    void republishingTheAccessPointsCanBeNarrowedToATrack() {
        server.expect(requestTo(GATEWAY + "/api/configuration/master-data/republish?entity=track-access-point&trackId=3"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body("""
                        {"id":"6f1c0000-0000-4000-8000-000000000001","type":"MASTER_DATA_REPUBLISH","status":"PENDING",
                         "createdAt":"2026-10-17T09:12:03Z","trackId":3,"totalItems":4,"processedItems":0,"successfulItems":0,"failedItems":0}
                        """));

        JobDto job = asUser(() -> jobsClient.republish("track-access-point", 3L, null));

        assertEquals(JobType.MASTER_DATA_REPUBLISH, job.type());
        assertEquals(Optional.of(JobFamily.REPUBLISH), job.family());
        assertEquals(3L, job.trackId());
        server.verify();
    }

    @Test
    void whatIsDownloadableDependsOnTheTypeAndTheStatus() {
        JobDto export = new JobDto(JOB_ID, JobType.PROFILE_EXPORT, JobStatus.COMPLETED, null, null, null, 3L, "basic", 10, 10, 10, 0, null, null, null);
        JobDto importWithErrors = new JobDto(JOB_ID, JobType.PROFILE_IMPORT, JobStatus.COMPLETED_WITH_ERRORS, null, null, null, null, null, 10, 10, 8, 2, null, null, null);
        JobDto exportWithErrors = new JobDto(JOB_ID, JobType.PROFILE_EXPORT, JobStatus.COMPLETED_WITH_ERRORS, null, null, null, 3L, null, 10, 10, 8, 2, null, null, null);

        assertTrue(export.isDownloadable());
        assertEquals("perfiles-via-3.csv", export.suggestedFileName());
        assertTrue(importWithErrors.isDownloadable(), "el fichero de una importacion es el informe de sus errores");
        assertFalse(exportWithErrors.isDownloadable(), "un CSV a medias no es un CSV");
    }

    @Test
    void theJobHistoryIsReadPagedWithOptionalTypeAndStatus() {
        server.expect(requestTo(GATEWAY + "/api/configuration/jobs?page=0&size=20&type=PROFILE_EXPORT&status=COMPLETED"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"content":[{"id":"6f1c0000-0000-4000-8000-000000000001","type":"PROFILE_EXPORT","status":"COMPLETED",
                          "createdAt":"2026-08-27T09:12:03Z","finishedAt":"2026-08-27T09:12:09Z","trackId":3,"mapperType":"basic",
                          "totalItems":10,"processedItems":10,"successfulItems":10,"failedItems":0}],
                         "page":{"size":20,"number":0,"totalElements":41,"totalPages":3}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/jobs?page=2&size=20"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"content":[],"page":{"size":20,"number":2,"totalElements":41,"totalPages":3}}
                        """, MediaType.APPLICATION_JSON));

        PageResponse<JobDto> page = asUser(() -> jobsClient.list(0, 20, JobType.PROFILE_EXPORT, JobStatus.COMPLETED));
        assertEquals(41, page.page().totalElements());
        JobDto job = page.content().getFirst();
        assertEquals(JOB_ID, job.id());
        assertTrue(job.isDownloadable(), "la fila basta para saber si hay fichero");
        assertTrue(job.itemErrors().isEmpty(), "la fila no trae los errores por elemento");

        PageResponse<JobDto> last = asUser(() -> jobsClient.list(2, 20, null, null));
        assertTrue(last.content().isEmpty());
        server.verify();
    }

    /**
     * La lista ensena los trabajos de todas las familias, asi que un tipo o un estado que
     * mto-configuration estrene llega antes que esta aplicacion: se lee como {@code UNKNOWN} y no
     * rompe la pagina. Un tipo desconocido no tiene familia, asi que ni se consulta por separado ni
     * se descarga; un estado desconocido se da por terminado y deja de consultarse.
     */
    @Test
    void aJobOfATypeOrStatusThisVersionDoesNotKnowIsReadAsUnknown() {
        server.expect(requestTo(GATEWAY + "/api/configuration/jobs?page=0&size=20"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"content":[{"id":"6f1c0000-0000-4000-8000-000000000001","type":"TRACK_EXPORT","status":"COMPLETED",
                                     "processedItems":10,"successfulItems":10,"failedItems":0},
                                    {"id":"6f1c0000-0000-4000-8000-000000000002","type":"PROFILE_EXPORT","status":"PAUSED",
                                     "trackId":3,"processedItems":4,"successfulItems":4,"failedItems":0}],
                         "page":{"size":20,"number":0,"totalElements":2,"totalPages":1}}
                        """, MediaType.APPLICATION_JSON));

        List<JobDto> jobs = asUser(() -> jobsClient.list(0, 20, null, null)).content();

        JobDto newType = jobs.get(0);
        assertEquals(JobType.UNKNOWN, newType.type());
        assertEquals("Desconocido", newType.type().label());
        assertTrue(newType.family().isEmpty());
        assertFalse(newType.isDownloadable(), "sin familia no hay a quien pedir el fichero");
        JobDto newStatus = jobs.get(1);
        assertEquals(JobStatus.UNKNOWN, newStatus.status());
        assertTrue(newStatus.isTerminal(), "un estado desconocido deja de consultarse");
        assertFalse(newStatus.isDownloadable());
        assertEquals(Optional.of(JobFamily.PROFILE_JOBS), newStatus.family());
        assertFalse(JobType.selectable().contains(JobType.UNKNOWN));
        assertEquals(6, JobType.selectable().size());
        assertFalse(JobStatus.selectable().contains(JobStatus.UNKNOWN));
        assertEquals(6, JobStatus.selectable().size());
        server.verify();
    }

    /** README_API.md §4: en una referencia a catalogo, null es «no la toques» y {} la vacia. */
    @Test
    void aClearedLovReferenceTravelsAsAnEmptyObjectAndAnUntouchedOneAsNull() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/7"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.poleType").isMap())
                .andExpect(jsonPath("$.poleType").isEmpty())
                .andExpect(jsonPath("$.supportType.id").value(5))
                .andExpect(jsonPath("$.foundation").value(nullValue()))
                .andRespond(withSuccess("{\"id\":7,\"versionNumber\":3}", MediaType.APPLICATION_JSON));

        ProfileDto profile = new ProfileDto();
        profile.setId(7L);
        profile.setPoleType(new LovRef(null, null, null));
        profile.setSupportType(new LovRef(5L, "ST1", "Soporte 1"));

        asUser(() -> profileClient.update(7L, profile));
        server.verify();
    }

    /**
     * mto-configuration V25: el estado normal y el accionamiento van y vuelven, y un seccionador sin
     * poste (los de los porticos de subestacion, los de puesta a tierra) lleva el perfil a null.
     */
    @Test
    void aDisconnectorTravelsWithItsNormalStateItsDriveAndWithoutAPole() {
        DisconnectorClient disconnectors = GatewayClientConfiguration.proxyFactory(restClient).createClient(DisconnectorClient.class);
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":5,"name":"HSA-FP1.1","onLoad":false,"normallyOpen":true,"driveType":"MOTOR",
                         "stationId":12,"profileId":null,"kp":"98375.5","trackId":3,
                         "disconnectorFunction":{"id":9,"code":"Disc"},"versionNumber":2}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.normallyOpen").value(false))
                .andExpect(jsonPath("$.driveType").value("MANUAL"))
                .andExpect(jsonPath("$.profileId").value(nullValue()))
                .andExpect(jsonPath("$.kp").value("98375.5"))
                .andExpect(jsonPath("$.trackId").value(3))
                .andExpect(jsonPath("$.versionNumber").value(2))
                .andRespond(withSuccess("{\"id\":5,\"versionNumber\":3}", MediaType.APPLICATION_JSON));

        DisconnectorDto read = asUser(() -> disconnectors.findById(5L));
        assertEquals(Boolean.TRUE, read.getNormallyOpen());
        assertEquals(DisconnectorDriveType.MOTOR, read.getDriveType());
        assertNull(read.getProfileId());
        assertEquals("", read.profileLabel(), "sin poste no hay perfil que ensenar");
        assertEquals("98375.5", read.getKp(), "sin poste, su propio KP (V26)");
        assertEquals(3L, read.getTrackId());

        read.setNormallyOpen(false);
        read.setDriveType(DisconnectorDriveType.MANUAL);
        asUser(() -> disconnectors.update(5L, read));
        server.verify();
    }

    /**
     * Un seccionador en plena via, en una zona neutra o en una subestacion no es de ninguna estacion:
     * llega con {@code stationId} a null y vuelve asi, y quitarsela a uno que la tenia es mandarla a
     * {@code null}, porque el {@code PUT} sustituye la fila entera.
     */
    @Test
    void aDisconnectorTravelsWithoutAStation() {
        DisconnectorClient disconnectors = GatewayClientConfiguration.proxyFactory(restClient).createClient(DisconnectorClient.class);
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":5,"name":"KAF-NS1","onLoad":false,"stationId":null,"profileId":null,"kp":"98375.5",
                         "trackId":3,"disconnectorFunction":{"id":9,"code":"Disc"},"versionNumber":2}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.stationId").value(nullValue()))
                .andExpect(jsonPath("$.trackId").value(3))
                .andRespond(withSuccess("{\"id\":5,\"versionNumber\":3}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/6"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":6,"name":"SEC-6","onLoad":true,"stationId":12,"profileId":7,"profileCode":"P-007",
                         "disconnectorFunction":{"id":9,"code":"Disc"},"versionNumber":4}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/6"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.stationId").value(nullValue()))
                .andExpect(jsonPath("$.profileId").value(7))
                .andRespond(withSuccess("{\"id\":6,\"versionNumber\":5}", MediaType.APPLICATION_JSON));

        DisconnectorDto withoutStation = asUser(() -> disconnectors.findById(5L));
        assertNull(withoutStation.getStationId());
        assertEquals(3L, withoutStation.getTrackId(), "su via propia es lo que lo situa");
        asUser(() -> disconnectors.update(5L, withoutStation));

        DisconnectorDto onAPole = asUser(() -> disconnectors.findById(6L));
        onAPole.setStationId(null);
        asUser(() -> disconnectors.update(6L, onAPole));
        server.verify();
    }

    /**
     * mto-configuration V27: uno que pone dos vias en paralelo lleva la otra, tambien con poste. Va y
     * vuelve por id, y quitarla es mandarla a {@code null}: el {@code PUT} sustituye la fila entera.
     */
    @Test
    void aParallelingDisconnectorTravelsWithItsConnectedTrack() {
        DisconnectorClient disconnectors = GatewayClientConfiguration.proxyFactory(restClient).createClient(DisconnectorClient.class);
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":5,"name":"SEC-B01","onLoad":false,"stationId":12,"profileId":7,"profileCode":"P-007",
                         "profileKp":"12.345","kp":null,"trackId":null,"connectedTrackId":4,
                         "disconnectorFunction":{"id":9,"code":"Disc/PP"},"versionNumber":2}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.profileId").value(7))
                .andExpect(jsonPath("$.connectedTrackId").value(4))
                .andRespond(withSuccess("{\"id\":5,\"connectedTrackId\":4,\"versionNumber\":3}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/disconnectors/5"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.connectedTrackId").value(nullValue()))
                .andExpect(jsonPath("$.versionNumber").value(3))
                .andRespond(withSuccess("{\"id\":5,\"versionNumber\":4}", MediaType.APPLICATION_JSON));

        DisconnectorDto read = asUser(() -> disconnectors.findById(5L));
        assertEquals(7L, read.getProfileId());
        assertEquals(4L, read.getConnectedTrackId(), "con poste tambien: es del seccionador, no del perfil");

        DisconnectorDto saved = asUser(() -> disconnectors.update(5L, read));
        assertEquals(4L, saved.getConnectedTrackId());
        read.setConnectedTrackId(null);
        read.setVersionNumber(saved.getVersionNumber());
        asUser(() -> disconnectors.update(5L, read));
        server.verify();
    }

    /**
     * mto-configuration V28 (README_API.md §4 quinquies): los puntos de acceso a la via se filtran por la
     * via, la clase y el estado en el cuerpo del {@code /filter}; sus decimales son numeros JSON (el KP en
     * metros, las toneladas y las coordenadas), y vuelven enteros, con su version y lo que la UI no conoce.
     * Quitarle la estacion es mandarla a {@code null}; la clase y el lado vuelven como se leyeron, tambien
     * uno que esta version no conoce; un nombre repetido en la via es el 409 BUS-002, y borrar es logico (204).
     */
    @Test
    void trackAccessPointsAreFilteredAndTravelWholeWithTheirDecimalsAsNumbers() {
        TrackAccessPointClient accessPoints = GatewayClientConfiguration.proxyFactory(restClient).createClient(TrackAccessPointClient.class);
        server.expect(requestTo(matchesRegex("http://gateway/api/configuration/track-access-points/filter\\?page=0&size=50&sort=kp(,|%2C)asc")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"searchText\":\"km\",\"trackId\":3,\"kind\":\"PREPARED_ACCESS\",\"enabled\":true}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess("""
                        {"content":[{"id":77,"name":"Acceso km 110+176","trackId":3,"stationId":12,"kp":110176.000,"kind":"PREPARED_ACCESS",
                                     "side":"LEFT","roadRailAccess":true,"maxAxleLoadT":12.500,"maxGrossWeightT":40.000,
                                     "latitude":31.669342,"longitude":34.574119,"enabled":true,"versionNumber":4,
                                     "versionDate":"2026-10-17T07:00:00","brandNewField":"x"}],
                         "page":{"size":50,"number":0,"totalElements":1,"totalPages":1}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/track-access-points/77"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.name").value("Acceso km 110+180"))
                .andExpect(jsonPath("$.trackId").value(3))
                .andExpect(jsonPath("$.stationId").value(nullValue()))
                .andExpect(jsonPath("$.kind").value("PREPARED_ACCESS"))
                .andExpect(jsonPath("$.side").value("BOTH"))
                .andExpect(jsonPath("$.roadRailAccess").value(true))
                .andExpect(jsonPath("$.versionNumber").value(4))
                .andExpect(jsonPath("$.brandNewField").value("x"))
                .andExpect(content().string(containsString("\"kp\":110176.000")))
                .andExpect(content().string(containsString("\"maxAxleLoadT\":12.500")))
                .andExpect(content().string(containsString("\"latitude\":31.669342")))
                .andExpect(content().string(containsString("\"longitude\":34.574119")))
                .andRespond(withSuccess("{\"id\":77,\"name\":\"Acceso km 110+180\",\"trackId\":3,\"kind\":\"PREPARED_ACCESS\",\"side\":\"BOTH\","
                        + "\"roadRailAccess\":true,\"enabled\":true,\"versionNumber\":5}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/track-access-points/78"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":78,"name":"Rampa del puerto","trackId":3,"stationId":null,"kp":null,"kind":"FERRY_RAMP","side":"UPSTREAM",
                         "roadRailAccess":false,"enabled":true,"versionNumber":1}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/track-access-points/78"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.kind").value("FERRY_RAMP"))
                .andExpect(jsonPath("$.side").value("UPSTREAM"))
                .andExpect(jsonPath("$.roadRailAccess").value(false))
                .andRespond(withSuccess("{\"id\":78,\"versionNumber\":2}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/track-access-points"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.id").value(nullValue()))
                .andExpect(jsonPath("$.roadRailAccess").value(true))
                .andExpect(jsonPath("$.enabled").value(true))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("""
                                {"type":"https://api.mto-configuration/errors/bus-002","title":"Regla de negocio incumplida","status":409,
                                 "detail":"La operación entra en conflicto con un registro existente: valor único repetido o referencia en uso",
                                 "instance":"/api/v1/configuration/track-access-points","code":"BUS-002","traceId":"t-tap"}
                                """));
        server.expect(requestTo(GATEWAY + "/api/configuration/track-access-points/77"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        PageResponse<TrackAccessPointDto> page = asUser(() -> accessPoints.filter(0, 50, List.of("kp,asc"),
                MasterFilters.of("searchText", " km ", "trackId", 3L, "kind", TrackAccessPointKind.PREPARED_ACCESS.name(), "enabled", true,
                        "stationName", " ")));
        TrackAccessPointDto read = page.content().getFirst();
        assertEquals("Acceso km 110+176", read.getName());
        assertEquals(new java.math.BigDecimal("110176.000"), read.getKp());
        assertEquals("PREPARED_ACCESS", read.getKind());
        assertEquals("Acceso preparado", TrackAccessPointKind.labelOf(read.getKind()));
        assertEquals("LEFT", read.getSide());
        assertEquals("Izquierda", TrackAccessPointSide.labelOf(read.getSide()));
        assertEquals(new java.math.BigDecimal("12.500"), read.getMaxAxleLoadT());
        assertEquals(new java.math.BigDecimal("31.669342"), read.getLatitude());
        assertEquals("x", read.extras().get("brandNewField"));

        read.setName("Acceso km 110+180");
        read.setStationId(null);
        read.setSide(TrackAccessPointSide.BOTH.name());
        TrackAccessPointDto saved = asUser(() -> accessPoints.save(read));
        assertEquals(5, saved.getVersionNumber());

        TrackAccessPointDto unknown = asUser(() -> accessPoints.findById(78L));
        assertEquals("FERRY_RAMP", TrackAccessPointKind.labelOf(unknown.getKind()), "un valor nuevo se ensena crudo");
        assertEquals("UPSTREAM", TrackAccessPointSide.labelOf(unknown.getSide()));
        asUser(() -> accessPoints.save(unknown));

        TrackAccessPointDto repeated = new TrackAccessPointDto();
        repeated.setName("Acceso km 110+180");
        repeated.setTrackId(3L);
        repeated.setKind(TrackAccessPointKind.LEVEL_CROSSING.name());
        repeated.setSide(TrackAccessPointSide.RIGHT.name());
        ConflictApiException conflict = assertThrows(ConflictApiException.class, () -> asUser(() -> accessPoints.save(repeated)));
        assertEquals("BUS-002", conflict.getProblem().code(), "un nombre repetido en la via");
        asUser(() -> {
            accessPoints.delete(77L);
            return null;
        });
        server.verify();
    }

    /**
     * README_API.md §4: la coleccion de hijos que se manda es el estado final. Los seccionadores del
     * poste (varios desde el V29) no son hijos: el servicio los ignora al escribir y vuelven como se leyeron.
     */
    @Test
    void aProfileTravelsWithItsTypedCantileversTheirSteadyArmAndItsDisconnectors() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/7"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":7,"profileId":"P-007","kp":"12.345","trackId":3,"versionNumber":2,
                         "disconnector":{"id":5,"name":"SEC-1","onLoad":true,"stationId":12,"profileId":7,
                                         "profileCode":"P-007","profileKp":"12.345","disconnectorFunction":{"id":9,"code":"Disc"}},
                         "disconnectors":[{"id":5,"name":"SEC-1","onLoad":true,"stationId":12,"profileId":7,
                                           "profileCode":"P-007","profileKp":"12.345","disconnectorFunction":{"id":9,"code":"Disc"}},
                                          {"id":6,"name":"SEC-2","profileId":7,"disconnectorFunction":{"id":10,"code":"SurgeA"}}],
                         "cantilevers":[{"id":21,"cwHeight":5300,"stagger":200,"cantileverType":{"id":4,"code":"CT1"},"profileId":7,
                                         "steadyArm":{"id":31,"length":1200,"steadyArmType":{"id":6,"code":"SA1"},"cantileverId":21,"armFieldOfTomorrow":true}}]}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/7"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.cantilevers.length()").value(2))
                .andExpect(jsonPath("$.cantilevers[0].id").value(21))
                .andExpect(jsonPath("$.cantilevers[0].cantileverType.code").value("CT1"))
                .andExpect(jsonPath("$.cantilevers[0].steadyArm.length").value(1200))
                .andExpect(jsonPath("$.cantilevers[0].steadyArm.armFieldOfTomorrow").value(true))
                .andExpect(jsonPath("$.cantilevers[1].id").value(nullValue()))
                .andExpect(jsonPath("$.cantilevers[1].cantileverType.code").value("CT2"))
                .andExpect(jsonPath("$.cantilevers[1].steadyArm").value(nullValue()))
                .andExpect(jsonPath("$.disconnector.id").value(5))
                .andExpect(jsonPath("$.disconnector.name").value("SEC-1"))
                .andExpect(jsonPath("$.disconnectors.length()").value(2))
                .andExpect(jsonPath("$.disconnectors[1].id").value(6))
                .andExpect(jsonPath("$.disconnectors[1].disconnectorFunction.code").value("SurgeA"))
                .andExpect(jsonPath("$.disconnectorsOnThePole").doesNotExist())
                .andRespond(withSuccess("{\"id\":7,\"versionNumber\":3}", MediaType.APPLICATION_JSON));

        ProfileDto profile = asUser(() -> profileClient.findById(7L));
        assertEquals("SEC-1", profile.getDisconnector().getName());
        assertEquals("P-007 (kp 12.345)", profile.getDisconnector().profileLabel());
        assertEquals(List.of("SEC-1", "SEC-2"), profile.getDisconnectors().stream().map(DisconnectorDto::getName).toList());
        assertFalse(profile.extras().containsKey("disconnectors"), "es un campo, no un extra que viaje dos veces");
        assertEquals(1200L, profile.getCantilevers().getFirst().getSteadyArm().getLength());

        CantileverDto added = new CantileverDto();
        added.setCantileverType(new LovRef(8L, "CT2", null));
        List<CantileverDto> cantilevers = new ArrayList<>(profile.getCantilevers());
        cantilevers.add(added);
        profile.setCantilevers(cantilevers);
        ProfileDto saved = asUser(() -> profileClient.update(7L, profile));

        assertEquals(3, saved.getVersionNumber());
        server.verify();
    }

    // --- Usuarios: mto-users a traves del gateway --------------------------------------------------

    private static final String USERS = GATEWAY + "/api/users";

    private static final String ANA = """
            {"id":"u-1","username":"ana.nueva","firstName":"Ana","lastName":"Nueva","email":"ana@mto.local",
             "emailVerified":true,"enabled":true,"createdAt":"2026-09-21T10:00:00Z",
             "attributes":{"dept":["taller"]},"requiredActions":["UPDATE_PASSWORD"],"unknownTomorrow":1}
            """;

    private static CreateUserRequest newUser(String username) {
        return new CreateUserRequest(username, null, null, null, null, null, null, null, null);
    }

    @Test
    void userSearchSendsTheKeycloakStyleOffsetAndReadsThePageWithItsTotal() {
        server.expect(requestTo(USERS + "?search=ana&enabled=true&first=50&max=50"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"content\":[" + ANA + "],\"first\":50,\"max\":50,\"total\":123}", MediaType.APPLICATION_JSON));

        UsersPage<UserDto> page = asUser(() -> usersClient.search("ana", null, null, true, null, null, 50, 50));

        assertEquals(123, page.total());
        assertEquals(50, page.first());
        UserDto ana = page.content().getFirst();
        assertEquals("u-1", ana.id());
        assertEquals("Ana Nueva", ana.fullName());
        assertEquals(List.of("taller"), ana.attributes().get("dept"));
        assertEquals(List.of("UPDATE_PASSWORD"), ana.requiredActions());
        assertTrue(ana.isEnabled());
        server.verify();
    }

    @Test
    void attributeFiltersRepeatTheParameterAndNeverTravelWithSearch() {
        server.expect(requestTo(matchesRegex(".*/api/users\\?attribute=dept(:|%3A)taller&attribute=turno(:|%3A)noche&first=0&max=20")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"content\":[],\"first\":0,\"max\":20,\"total\":0}", MediaType.APPLICATION_JSON));

        UsersPage<UserDto> page = asUser(() -> usersClient.search(null, null, null, null, null, List.of("dept:taller", "turno:noche"), 0, 20));

        assertTrue(page.content().isEmpty());
        server.verify();
    }

    @Test
    void creatingAUserPostsOnlyWhatIsFilledAndReadsThe201() {
        server.expect(requestTo(USERS))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("ana.nueva"))
                .andExpect(jsonPath("$.temporaryPassword").value("Cambiame.123"))
                .andExpect(jsonPath("$.requiredActions[0]").value("UPDATE_PASSWORD"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.firstName").doesNotExist())
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.LOCATION, GATEWAY + "/api/users/u-1").body(ANA));

        CreateUserRequest request = new CreateUserRequest("ana.nueva", null, null, "ana@mto.local", null, true, null,
                List.of(RequiredAction.UPDATE_PASSWORD), "Cambiame.123");
        UserDto created = asUser(() -> usersClient.create(request));

        assertEquals("u-1", created.id());
        assertFalse(request.toString().contains("Cambiame.123"), "una contrasena nunca llega a un log");
        server.verify();
    }

    @Test
    void updatingAUserSendsOnlyTheEditableFieldsAndEnabledHasItsOwnPatch() {
        server.expect(requestTo(USERS + "/u-1"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.email").value(""))
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.lastName").doesNotExist())
                .andRespond(withSuccess(ANA, MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/u-1/enabled"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"enabled\":false}"))
                .andRespond(withSuccess(ANA.replace("\"enabled\":true", "\"enabled\":false"), MediaType.APPLICATION_JSON));

        UserDto updated = asUser(() -> usersClient.update("u-1", new UpdateUserRequest("Ana", null, "", null, null)));
        UserDto disabled = asUser(() -> usersClient.setEnabled("u-1", new UserEnabledRequest(false)));

        assertEquals("Ana", updated.firstName());
        assertFalse(disabled.isEnabled());
        server.verify();
    }

    /** README de mto-users: anadir es un PUT aditivo; quitar, un DELETE con los nombres en el cuerpo. */
    @Test
    void removingClientRolesIsADeleteWithAJsonBody() {
        String roles = "{\"realmRoles\":[\"mto-users-viewer\"],\"clientRoles\":[{\"clientId\":\"mto-users-api\",\"roles\":[\"users-write\"]}]}";
        server.expect(requestTo(USERS + "/u-1/roles/clients/mto-users-api"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"roles\":[\"users-write\"]}"))
                .andRespond(withSuccess(roles, MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/u-1/roles/clients/mto-users-api"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"roles\":[\"users-read\"]}"))
                .andRespond(withSuccess(roles, MediaType.APPLICATION_JSON));

        UserRolesDto added = asUser(() -> usersClient.addClientRoles("u-1", "mto-users-api", new RoleNamesRequest(List.of("users-write"))));
        UserRolesDto removed = asUser(() -> usersClient.removeClientRoles("u-1", "mto-users-api", new RoleNamesRequest(List.of("users-read"))));

        assertEquals(List.of("users-write"), added.clientRoles().getFirst().roles());
        assertEquals(List.of("mto-users-viewer"), removed.realmRoles());
        server.verify();
    }

    @Test
    void profilesAreAssignedWithABodilessPutAndTheUsersProfilesComeBack() {
        String profiles = "[{\"name\":\"mto-users-viewer\",\"description\":\"Lectura\"}]";
        server.expect(requestTo(USERS + "/u-1/profiles/mto-users-viewer")).andExpect(method(HttpMethod.PUT))
                .andExpect(content().string(""))
                .andRespond(withSuccess(profiles, MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/u-1/profiles/mto-users-viewer")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/u-1/profiles")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(profiles, MediaType.APPLICATION_JSON));

        List<RealmProfileSummaryDto> assigned = asUser(() -> usersClient.assignProfile("u-1", "mto-users-viewer"));
        List<RealmProfileSummaryDto> removed = asUser(() -> usersClient.removeProfile("u-1", "mto-users-viewer"));
        List<RealmProfileSummaryDto> current = asUser(() -> usersClient.userProfiles("u-1"));

        assertEquals("mto-users-viewer", assigned.getFirst().name());
        assertTrue(removed.isEmpty());
        assertEquals(1, current.size());
        server.verify();
    }

    @Test
    void sessionsCredentialsAndTheirRevocationsUseTheirPaths() {
        server.expect(requestTo(USERS + "/u-1/sessions")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":"s-1","username":"ana.nueva","ipAddress":"10.0.0.7","startedAt":"2026-09-21T09:00:00Z",
                          "lastAccessAt":"2026-09-21T09:30:00Z","clients":["mto-backoffice","mto-frontend"]}]
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/u-1/sessions")).andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(USERS + "/u-1/offline-sessions/o-9")).andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(USERS + "/u-1/credentials")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"c-1\",\"type\":\"otp\",\"userLabel\":\"movil\",\"createdAt\":\"2026-09-21T09:00:00Z\"}]",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/u-1/credentials/c-1")).andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));

        List<UserSessionDto> sessions = asUser(() -> usersClient.sessions("u-1"));
        asUser(() -> {
            usersClient.revokeAllSessions("u-1");
            usersClient.revokeOfflineSession("u-1", "o-9");
            return null;
        });
        List<UserCredentialDto> credentials = asUser(() -> usersClient.credentials("u-1"));
        asUser(() -> {
            usersClient.deleteCredential("u-1", "c-1");
            return null;
        });

        assertEquals(Instant.parse("2026-09-21T09:30:00Z"), sessions.getFirst().lastAccessAt());
        assertEquals(List.of("mto-backoffice", "mto-frontend"), sessions.getFirst().clients());
        assertEquals("Segundo factor (OTP)", credentials.getFirst().typeLabel());
        assertFalse(credentials.getFirst().isPassword());
        server.verify();
    }

    @Test
    void resetPasswordAndActionsEmailPostTheirBodies() {
        server.expect(requestTo(USERS + "/u-1/reset-password")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"password\":\"Secreta.123\",\"temporary\":true}"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(USERS + "/u-1/execute-actions-email")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"actions\":[\"UPDATE_PASSWORD\",\"VERIFY_EMAIL\"],\"lifespanSeconds\":3600}"))
                .andExpect(jsonPath("$.clientId").doesNotExist())
                .andRespond(withStatus(HttpStatus.ACCEPTED));

        ResetPasswordRequest reset = new ResetPasswordRequest("Secreta.123", true);
        asUser(() -> {
            usersClient.resetPassword("u-1", reset);
            usersClient.executeActionsEmail("u-1", new ExecuteActionsEmailRequest(
                    List.of(RequiredAction.UPDATE_PASSWORD, RequiredAction.VERIFY_EMAIL), 3600, null, null));
            return null;
        });

        assertFalse(reset.toString().contains("Secreta.123"));
        server.verify();
    }

    @Test
    void roleAndProfileMembersArePlainListsWithoutATotal() {
        server.expect(requestTo(USERS + "/roles/clients/mto-users-api/users-read/users?first=0&max=50")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + ANA + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/profiles/mto-users-admin/users?first=50&max=50")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        List<UserDto> holders = asUser(() -> usersClient.clientRoleMembers("mto-users-api", "users-read", 0, 50));
        List<UserDto> secondPage = asUser(() -> usersClient.profileMembers("mto-users-admin", 50, 50));

        assertEquals("ana.nueva", holders.getFirst().username());
        assertTrue(secondPage.isEmpty());
        server.verify();
    }

    @Test
    void theRolesAndProfilesCataloguesAreReadTyped() {
        server.expect(requestTo(USERS + "/roles/clients")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"clientId\":\"mto-users-api\",\"name\":\"\",\"description\":\"Usuarios\"},"
                        + "{\"clientId\":\"mto-configuration-api\",\"name\":\"Configuracion\",\"description\":null}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/roles/clients/mto-users-api")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"name\":\"users-read\",\"description\":\"Consulta\",\"composite\":false}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/profiles")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"name\":\"mto-users-admin\",\"description\":\"Todo\"}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(USERS + "/profiles/mto-users-admin")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"name\":\"mto-users-admin\",\"description\":\"Todo\","
                        + "\"clientRoles\":[{\"clientId\":\"mto-users-api\",\"roles\":[\"users-read\",\"users-delete\"]}],\"realmRoles\":[]}", MediaType.APPLICATION_JSON));

        List<ClientDto> clients = asUser(() -> usersClient.clients());
        List<ClientRoleDto> roles = asUser(() -> usersClient.clientRoles("mto-users-api"));
        List<RealmProfileSummaryDto> profiles = asUser(() -> usersClient.profiles());
        RealmProfileDto admin = asUser(() -> usersClient.profile("mto-users-admin"));

        assertEquals("mto-users-api", clients.getFirst().label(), "sin nombre, el clientId");
        assertEquals("Configuracion", clients.get(1).label());
        assertEquals("users-read", roles.getFirst().name());
        assertEquals("mto-users-admin", profiles.getFirst().name());
        assertEquals(List.of("users-read", "users-delete"), admin.clientRoles().getFirst().roles());
        server.verify();
    }

    /** El problem+json de mto-users usa errorCode y validationErrors[{field, message}]: caen en code y errors. */
    @Test
    void theUsersProblemJsonIsReadThroughItsAliases() {
        server.expect(requestTo(USERS)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .header("X-Correlation-Id", "corr-u1")
                        .body("""
                                {"type":"urn:problem:mto-users:REQ-VALIDATION","title":"Bad Request","status":400,
                                 "detail":"Validation failed","instance":"/api/v1/users","errorCode":"REQ-VALIDATION",
                                 "timestamp":"2026-09-21T10:00:00Z","correlationId":"corr-u1",
                                 "validationErrors":[{"field":"username","message":"may only contain letters, digits, '.', '_', '@' and '-'"}]}
                                """));

        ValidationApiException exception = assertThrows(ValidationApiException.class,
                () -> asUser(() -> usersClient.create(newUser("ana nueva"))));

        assertEquals("REQ-VALIDATION", exception.getProblem().code());
        assertTrue(exception.getProblem().hasFieldErrors());
        assertEquals("username", exception.getProblem().errors().getFirst().field());
        assertNull(exception.getProblem().errors().getFirst().code(), "mto-users no manda codigo por campo");
        assertEquals(Instant.parse("2026-09-21T10:00:00Z"), exception.getProblem().timestamp());
        assertEquals("corr-u1", exception.getReference());
        server.verify();
    }

    @Test
    void theUsersServiceUnavailableCarriesItsRetryAfterAndAConflictIsAConflict() {
        server.expect(requestTo(USERS + "/profiles")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .header(HttpHeaders.RETRY_AFTER, "10")
                        .body("{\"status\":503,\"title\":\"Service Unavailable\",\"detail\":\"Keycloak no responde\","
                                + "\"errorCode\":\"KC-503\",\"correlationId\":\"corr-u2\"}"));
        server.expect(requestTo(USERS)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body("{\"status\":409,\"title\":\"Conflict\",\"detail\":\"User exists with same username\",\"errorCode\":\"USR-409\"}"));

        ServiceUnavailableApiException unavailable = assertThrows(ServiceUnavailableApiException.class,
                () -> asUser(() -> usersClient.profiles()));
        ConflictApiException conflict = assertThrows(ConflictApiException.class,
                () -> asUser(() -> usersClient.create(newUser("ana.nueva"))));

        assertEquals(Duration.ofSeconds(10), unavailable.getRetryAfter().orElseThrow());
        assertEquals("KC-503", unavailable.getProblem().code());
        assertEquals("corr-u2", unavailable.getReference());
        assertEquals("USR-409", conflict.getProblem().code());
        server.verify();
    }

    // --- Almacen: mto-stock a traves del gateway ----------------------------------------------------

    private static final String STOCK = GATEWAY + "/api/stock";
    private static final String MAT_ID = "1a2b3c4d-0000-4000-8000-000000000001";
    private static final String WH_ID = "1a2b3c4d-0000-4000-8000-000000000002";
    private static final String WH2_ID = "1a2b3c4d-0000-4000-8000-000000000003";
    private static final String PRJ_ID = "1a2b3c4d-0000-4000-8000-000000000004";
    private static final String RES_ID = "1a2b3c4d-0000-4000-8000-000000000005";
    private static final String AUDIT = "\"audit\":{\"createdAt\":\"2026-09-01T08:00:00Z\",\"updatedAt\":\"2026-09-02T08:00:00Z\","
            + "\"createdBy\":\"almacen.responsable\",\"updatedBy\":\"almacen.operario\"}";
    private static final String MATERIAL_SUMMARY = "{\"id\":\"" + MAT_ID + "\",\"code\":\"MAT-001\",\"name\":\"Hilo de contacto\",\"unitOfMeasure\":\"m\",\"active\":true}";
    private static final String WAREHOUSE_SUMMARY = "{\"id\":\"" + WH_ID + "\",\"code\":\"WH-001\",\"name\":\"Central\",\"active\":true}";
    private static final String MOVEMENT = "{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000a\",\"material\":" + MATERIAL_SUMMARY
            + ",\"warehouse\":" + WAREHOUSE_SUMMARY + ",\"type\":\"OUTPUT\",\"quantity\":3,\"signedQuantity\":-3,"
            + "\"occurredAt\":\"2026-09-10T10:00:00Z\",\"supplier\":null,\"project\":{\"id\":\"" + PRJ_ID + "\",\"code\":\"EP-42\",\"name\":\"Tramo\",\"active\":true},"
            + "\"reservation\":null,\"relatedMovement\":null,\"externalReference\":\"OT-7\",\"notes\":null," + AUDIT + "}";

    private static String stockPage(String content, int number, int size, long total) {
        int pages = (int) Math.max(1, (total + size - 1) / size);
        return "{\"content\":[" + content + "],\"page\":{\"number\":" + number + ",\"size\":" + size + ",\"totalElements\":" + total
                + ",\"totalPages\":" + pages + ",\"first\":" + (number == 0) + ",\"last\":" + (number >= pages - 1) + "}}";
    }

    @Test
    void stockCataloguesSendSearchActiveAndPageableAndReadTheNestedPage() {
        server.expect(requestTo(STOCK + "/warehouses?search=cen&active=true&page=1&size=20&sort=code%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-for-" + PRINCIPAL))
                .andRespond(withSuccess(stockPage("{\"id\":\"" + WH_ID + "\",\"code\":\"WH-001\",\"name\":\"Central\",\"active\":true," + AUDIT + "}", 1, 20, 21),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/suppliers?page=0&size=50&sort=name%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("", 0, 50, 0), MediaType.APPLICATION_JSON));

        PageResponse<WarehouseDto> warehouses = asUser(() -> warehouseClient.search("cen", true, 1, 20, List.of("code,asc")));
        PageResponse<SupplierDto> suppliers = asUser(() -> supplierClient.search(null, null, 0, 50, List.of("name,desc")));

        assertEquals(21, warehouses.page().totalElements());
        assertEquals(2, warehouses.page().totalPages());
        WarehouseDto central = warehouses.content().getFirst();
        assertEquals("WH-001", central.code());
        assertEquals("WH-001 - Central", central.summary().label());
        assertEquals("almacen.operario", central.audit().updatedBy());
        assertTrue(suppliers.content().isEmpty());
        server.verify();
    }

    @Test
    void stockCatalogueEntriesAreCreatedUpdatedAndReadWithTheirFlags() {
        server.expect(requestTo(STOCK + "/suppliers")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"SUP-001\",\"name\":\"Rail Supplier\"}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000b\",\"code\":\"SUP-001\",\"name\":\"Rail Supplier\",\"active\":true," + AUDIT + "}"));
        server.expect(requestTo(STOCK + "/projects/" + PRJ_ID)).andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"code\":\"PRJ-001\",\"name\":\"Renovacion\",\"active\":false}", true))
                .andRespond(withSuccess("{\"id\":\"" + PRJ_ID + "\",\"code\":\"PRJ-001\",\"name\":\"Renovacion\",\"active\":false,"
                        + "\"sourceService\":null,\"synchronizedFromMasterData\":false," + AUDIT + "}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/projects/" + RES_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":\"" + RES_ID + "\",\"code\":\"EP-42\",\"name\":\"Tramo\",\"active\":true,"
                        + "\"sourceService\":\"mto-configuration\",\"synchronizedFromMasterData\":true,\"newTomorrow\":1," + AUDIT + "}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/materials")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"MAT-001\",\"name\":\"Hilo de contacto\",\"unitOfMeasure\":\"m\",\"minimumStockLevel\":100}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":\"" + MAT_ID + "\",\"code\":\"MAT-001\",\"name\":\"Hilo de contacto\",\"unitOfMeasure\":\"m\",\"minimumStockLevel\":100,\"active\":true," + AUDIT + "}"));

        SupplierDto supplier = asUser(() -> supplierClient.create(new CatalogueRequest("SUP-001", "Rail Supplier")));
        ProjectDto retired = asUser(() -> projectClient.update(UUID.fromString(PRJ_ID), new CatalogueUpdateRequest("PRJ-001", "Renovacion", false)));
        ProjectDto synchronized_ = asUser(() -> projectClient.findById(UUID.fromString(RES_ID)));
        MaterialDto material = asUser(() -> materialClient.create(new MaterialRequest("MAT-001", "Hilo de contacto", "m", new BigDecimal("100"))));

        assertTrue(supplier.isEnabled());
        assertFalse(retired.isEnabled());
        assertFalse(retired.isSynchronized());
        assertTrue(synchronized_.isSynchronized());
        assertEquals("mto-configuration", synchronized_.sourceService());
        assertEquals(0, new BigDecimal("100").compareTo(material.minimumStockLevel()));
        server.verify();
    }

    @Test
    void theMaterialClientFiltersByStockAndReadsTheStockAndTheLedgerOfOneMaterial() {
        server.expect(requestTo(STOCK + "/materials?search=hilo&active=true&warehouseId=" + WH_ID + "&belowMinimum=true&page=0&size=20&sort=code%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("", 0, 20, 0), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/materials/low-stock?page=0&size=20&sort=code%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("", 0, 20, 0), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/materials/" + MAT_ID + "/stock?warehouseId=" + WH_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"material\":" + MATERIAL_SUMMARY + ",\"warehouse\":" + WAREHOUSE_SUMMARY
                        + ",\"onHandQuantity\":12.5,\"activeReservedQuantity\":2,\"availableQuantity\":10.5,\"minimumStockLevel\":100,"
                        + "\"lowStock\":true,\"calculatedAt\":\"2026-09-21T10:00:00Z\"}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(matchesRegex(".*/api/stock/materials/" + MAT_ID + "/movements\\?dateFrom=2026-09-01T00(:|%3A)00(:|%3A)00Z&page=0&size=20&sort=occurredAt(,|%2C)desc")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(MOVEMENT, 0, 20, 1), MediaType.APPLICATION_JSON));

        asUser(() -> materialClient.filter("hilo", true, UUID.fromString(WH_ID), true, 0, 20, List.of("code,asc")));
        asUser(() -> materialClient.lowStock(null, 0, 20, List.of("code,asc")));
        MaterialStockDto stock = asUser(() -> materialClient.stock(UUID.fromString(MAT_ID), UUID.fromString(WH_ID)));
        PageResponse<MovementDto> ledger = asUser(() -> materialClient.movements(UUID.fromString(MAT_ID), null,
                Instant.parse("2026-09-01T00:00:00Z"), null, null, 0, 20, List.of("occurredAt,desc")));

        assertEquals("MAT-001 - Hilo de contacto", stock.material().label());
        assertEquals("WH-001", stock.warehouse().code());
        assertEquals(0, new BigDecimal("10.5").compareTo(stock.availableQuantity()));
        assertTrue(stock.isLowStock());
        MovementDto output = ledger.content().getFirst();
        assertEquals(MovementType.OUTPUT, output.type());
        assertEquals(-1, output.type().sign());
        assertEquals(0, new BigDecimal("-3").compareTo(output.signedQuantity()));
        assertEquals("EP-42", output.project().code());
        assertNull(output.supplier());
        server.verify();
    }

    @Test
    void movementsArePostedToTheirOwnPathsAndATransferReturnsTwoRows() {
        String entry = MOVEMENT.replace("\"type\":\"OUTPUT\",\"quantity\":3,\"signedQuantity\":-3", "\"type\":\"ENTRY\",\"quantity\":10,\"signedQuantity\":10");
        String outgoing = MOVEMENT.replace("\"type\":\"OUTPUT\"", "\"type\":\"OUTGOING_TRANSFER\"");
        String incoming = MOVEMENT.replace("\"type\":\"OUTPUT\",\"quantity\":3,\"signedQuantity\":-3", "\"type\":\"INCOMING_TRANSFER\",\"quantity\":3,\"signedQuantity\":3")
                .replace("\"relatedMovement\":null", "\"relatedMovement\":{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000a\",\"type\":\"OUTGOING_TRANSFER\",\"quantity\":3,\"occurredAt\":\"2026-09-10T10:00:00Z\",\"externalReference\":\"OT-7\"}");
        server.expect(requestTo(STOCK + "/movements/entries")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"materialId\":\"" + MAT_ID + "\",\"warehouseId\":\"" + WH_ID + "\",\"quantity\":10}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(entry));
        server.expect(requestTo(STOCK + "/movements/outputs")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"materialId\":\"" + MAT_ID + "\",\"warehouseId\":\"" + WH_ID + "\",\"reservationId\":\"" + RES_ID
                        + "\",\"quantity\":3,\"externalReference\":\"OT-7\"}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(MOVEMENT));
        server.expect(requestTo(STOCK + "/movements/adjustments")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"materialId\":\"" + MAT_ID + "\",\"warehouseId\":\"" + WH_ID + "\",\"direction\":\"NEGATIVE\",\"quantity\":1,\"notes\":\"Rotura\"}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(MOVEMENT));
        server.expect(requestTo(STOCK + "/movements/transfers")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"materialId\":\"" + MAT_ID + "\",\"sourceWarehouseId\":\"" + WH_ID + "\",\"targetWarehouseId\":\"" + WH2_ID + "\",\"quantity\":3}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("[" + outgoing + "," + incoming + "]"));

        MovementDto entered = asUser(() -> movementClient.entry(new EntryRequest(UUID.fromString(MAT_ID), UUID.fromString(WH_ID), null, BigDecimal.TEN, null, null, null)));
        MovementDto consumed = asUser(() -> movementClient.output(new OutputRequest(UUID.fromString(MAT_ID), UUID.fromString(WH_ID), null, UUID.fromString(RES_ID),
                new BigDecimal("3"), null, "OT-7", null)));
        MovementDto adjusted = asUser(() -> movementClient.adjustment(new AdjustmentRequest(UUID.fromString(MAT_ID), UUID.fromString(WH_ID), AdjustmentDirection.NEGATIVE,
                BigDecimal.ONE, null, null, "Rotura")));
        List<MovementDto> transferred = asUser(() -> movementClient.transfer(new TransferRequest(UUID.fromString(MAT_ID), UUID.fromString(WH_ID), UUID.fromString(WH2_ID),
                new BigDecimal("3"), null, null, null)));

        assertEquals(MovementType.ENTRY, entered.type());
        assertEquals(0, BigDecimal.TEN.compareTo(entered.signedQuantity()));
        assertEquals(MovementType.OUTPUT, consumed.type());
        assertEquals(MovementType.OUTPUT, adjusted.type());
        assertEquals(2, transferred.size());
        assertEquals(MovementType.OUTGOING_TRANSFER, transferred.get(0).type());
        assertEquals(MovementType.INCOMING_TRANSFER, transferred.get(1).type());
        assertEquals(MovementType.OUTGOING_TRANSFER, transferred.get(1).relatedMovement().type());
        server.verify();
    }

    @Test
    void reservationsAreCancelledWithADeleteThatReturnsTheBodyAndChangeStateWithBodilessPosts() {
        String reservation = "{\"id\":\"" + RES_ID + "\",\"material\":" + MATERIAL_SUMMARY + ",\"warehouse\":" + WAREHOUSE_SUMMARY
                + ",\"project\":{\"id\":\"" + PRJ_ID + "\",\"code\":\"EP-42\",\"name\":\"Tramo\",\"active\":true},\"quantity\":2,"
                + "\"status\":\"%s\",\"reservedAt\":\"2026-09-10T10:00:00Z\",\"releasedAt\":%s,\"active\":%s," + AUDIT + "}";
        server.expect(requestTo(STOCK + "/reservations")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"materialId\":\"" + MAT_ID + "\",\"warehouseId\":\"" + WH_ID + "\",\"projectId\":\"" + PRJ_ID + "\",\"quantity\":2}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(reservation.formatted("ACTIVE", "null", "true")));
        server.expect(requestTo(STOCK + "/reservations/" + RES_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(reservation.formatted("CANCELLED", "\"2026-09-11T10:00:00Z\"", "false"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/reservations/" + RES_ID + "/release")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(reservation.formatted("RELEASED", "\"2026-09-11T10:00:00Z\"", "false"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/reservations/" + RES_ID + "/consume")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(reservation.formatted("CONSUMED", "\"2026-09-11T10:00:00Z\"", "false"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/reservations?warehouseId=" + WH_ID + "&status=ACTIVE&page=0&size=20&sort=reservedAt%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(reservation.formatted("ACTIVE", "null", "true"), 0, 20, 1), MediaType.APPLICATION_JSON));

        ReservationDto created = asUser(() -> reservationClient.create(new ReservationRequest(UUID.fromString(MAT_ID), UUID.fromString(WH_ID), UUID.fromString(PRJ_ID),
                new BigDecimal("2"), null)));
        ReservationDto cancelled = asUser(() -> reservationClient.cancel(UUID.fromString(RES_ID)));
        ReservationDto released = asUser(() -> reservationClient.release(UUID.fromString(RES_ID)));
        ReservationDto consumed = asUser(() -> reservationClient.consume(UUID.fromString(RES_ID)));
        PageResponse<ReservationDto> active = asUser(() -> reservationClient.search(UUID.fromString(WH_ID), ReservationStatus.ACTIVE, null, null, 0, 20, List.of("reservedAt,desc")));

        assertTrue(created.isActive());
        assertEquals(ReservationStatus.CANCELLED, cancelled.status());
        assertEquals(Instant.parse("2026-09-11T10:00:00Z"), cancelled.releasedAt());
        assertEquals(ReservationStatus.RELEASED, released.status());
        assertEquals(ReservationStatus.CONSUMED, consumed.status());
        assertEquals(1, active.page().totalElements());
        assertEquals("Hilo de contacto", active.content().getFirst().material().name());
        server.verify();
    }

    @Test
    void assembliesCarryTheirBomAndTheAvailabilityMarksTheLimitingComponent() {
        String assembly = "{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000c\",\"code\":\"ASM-001\",\"name\":\"Mensula\",\"active\":true,"
                + "\"components\":[{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000d\",\"material\":" + MATERIAL_SUMMARY + ",\"quantity\":2," + AUDIT + "}]," + AUDIT + "}";
        server.expect(requestTo(STOCK + "/assemblies")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"ASM-001\",\"name\":\"Mensula\",\"components\":[{\"materialId\":\"" + MAT_ID + "\",\"quantity\":2}]}", true))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(assembly));
        server.expect(requestTo(STOCK + "/assemblies/1a2b3c4d-0000-4000-8000-00000000000c/availability?warehouseId=" + WH_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"assembly\":{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000c\",\"code\":\"ASM-001\",\"name\":\"Mensula\",\"active\":true},"
                        + "\"warehouse\":" + WAREHOUSE_SUMMARY + ",\"availableQuantity\":5,\"components\":[{\"material\":" + MATERIAL_SUMMARY
                        + ",\"requiredQuantityPerAssembly\":2,\"onHandQuantity\":12,\"activeReservedQuantity\":2,\"availableQuantity\":10,"
                        + "\"producibleAssemblyQuantity\":5,\"limitingComponent\":true}],\"calculatedAt\":\"2026-09-21T10:00:00Z\"}", MediaType.APPLICATION_JSON));

        AssemblyDto created = asUser(() -> assemblyClient.create(new AssemblyRequest("ASM-001", "Mensula",
                List.of(new AssemblyComponentRequest(UUID.fromString(MAT_ID), new BigDecimal("2"))))));
        AssemblyAvailabilityDto availability = asUser(() -> assemblyClient.availability(created.id(), UUID.fromString(WH_ID)));

        assertEquals(1, created.components().size());
        assertEquals("MAT-001", created.components().getFirst().material().code());
        assertEquals(0, new BigDecimal("5").compareTo(availability.availableQuantity()));
        assertTrue(availability.components().getFirst().isLimiting());
        assertEquals("Central", availability.warehouse().name());
        server.verify();
    }

    /** El generico del historial se resuelve contra la subinterfaz: la foto sale tipada, no como un mapa. */
    @Test
    void stockRevisionsComeTypedForEachResource() {
        server.expect(requestTo(STOCK + "/materials/" + MAT_ID + "/revisions?page=0&size=10")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("{\"revision\":{\"revision\":3,\"revisionAt\":\"2026-09-02T08:00:00Z\",\"operation\":\"UPDATED\","
                        + "\"author\":\"almacen.operario\",\"source\":\"HTTP\",\"correlationId\":\"c-1\"},"
                        + "\"entity\":{\"id\":\"" + MAT_ID + "\",\"code\":\"MAT-001\",\"name\":\"Hilo de contacto\",\"unitOfMeasure\":\"m\",\"minimumStockLevel\":100,\"active\":true,"
                        + "\"audit\":{\"createdAt\":null,\"updatedAt\":null,\"createdBy\":null,\"updatedBy\":null}}}", 0, 10, 3), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/reservations/" + RES_ID + "/revisions?page=0&size=10")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("{\"revision\":{\"revision\":1,\"revisionAt\":\"2026-09-01T08:00:00Z\",\"operation\":\"CREATED\","
                        + "\"author\":\"system\",\"source\":\"BASELINE\",\"correlationId\":null},"
                        + "\"entity\":{\"id\":\"" + RES_ID + "\",\"material\":" + MATERIAL_SUMMARY + ",\"warehouse\":" + WAREHOUSE_SUMMARY
                        + ",\"project\":null,\"quantity\":2,\"status\":\"ACTIVE\",\"reservedAt\":\"2026-09-01T08:00:00Z\",\"releasedAt\":null,\"active\":true,\"audit\":null}}", 0, 10, 1),
                        MediaType.APPLICATION_JSON));

        PageResponse<RevisionDto<MaterialDto>> material = asUser(() -> materialClient.revisions(UUID.fromString(MAT_ID), 0, 10));
        PageResponse<RevisionDto<ReservationDto>> reservation = asUser(() -> reservationClient.revisions(UUID.fromString(RES_ID), 0, 10));

        RevisionDto<MaterialDto> revision = material.content().getFirst();
        assertEquals(3, revision.revision().revision());
        assertEquals(RevisionOperation.UPDATED, revision.revision().operation());
        assertEquals("almacen.operario", revision.revision().author());
        assertEquals("MAT-001", revision.entity().code());
        assertNull(revision.entity().audit().createdBy(), "las columnas de auditoria no se auditan");
        assertEquals("BASELINE", reservation.content().getFirst().revision().source());
        assertEquals(ReservationStatus.ACTIVE, reservation.content().getFirst().entity().status());
        server.verify();
    }

    /**
     * Un valor que mto-stock estrene no rompe la pagina: el tipo de un apunte (tambien el del apunte
     * enlazado), el estado de una reserva y la operacion de una revision se leen como
     * {@code UNKNOWN}, que no se ofrece en ningun filtro. Una reserva desconocida no es activa, asi
     * que su fila no ofrece acciones.
     */
    @Test
    void aStockValueThisVersionDoesNotKnowIsReadAsUnknown() {
        String returned = MOVEMENT.replace("\"type\":\"OUTPUT\"", "\"type\":\"RETURN_TO_SUPPLIER\"")
                .replace("\"relatedMovement\":null", "\"relatedMovement\":{\"id\":\"1a2b3c4d-0000-4000-8000-00000000000b\",\"type\":\"RETURN_TO_SUPPLIER\","
                        + "\"quantity\":3,\"occurredAt\":\"2026-09-10T10:00:00Z\",\"externalReference\":null}");
        server.expect(requestTo(STOCK + "/movements?page=0&size=20&sort=occurredAt%2Cdesc")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(MOVEMENT + "," + returned, 0, 20, 2), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/reservations?page=0&size=20&sort=reservedAt%2Cdesc")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("{\"id\":\"" + RES_ID + "\",\"material\":" + MATERIAL_SUMMARY + ",\"warehouse\":" + WAREHOUSE_SUMMARY
                        + ",\"project\":null,\"quantity\":2,\"status\":\"EXPIRED\",\"reservedAt\":\"2026-09-10T10:00:00Z\",\"releasedAt\":null,"
                        + "\"active\":false," + AUDIT + "}", 0, 20, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(STOCK + "/reservations/" + RES_ID + "/revisions?page=0&size=10")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("{\"revision\":{\"revision\":4,\"revisionAt\":\"2026-09-03T08:00:00Z\",\"operation\":\"RESTORED\","
                        + "\"author\":\"almacen.responsable\",\"source\":\"HTTP\",\"correlationId\":\"c-2\"},\"entity\":null}", 0, 10, 1),
                        MediaType.APPLICATION_JSON));

        List<MovementDto> ledger = asUser(() -> movementClient.search(null, null, null, null, null, null, null, 0, 20, List.of("occurredAt,desc"))).content();
        ReservationDto reservation = asUser(() -> reservationClient.search(null, null, null, null, 0, 20, List.of("reservedAt,desc"))).content().getFirst();
        RevisionDto<ReservationDto> revision = asUser(() -> reservationClient.revisions(UUID.fromString(RES_ID), 0, 10)).content().getFirst();

        assertEquals(MovementType.OUTPUT, ledger.get(0).type());
        MovementDto unknown = ledger.get(1);
        assertEquals(MovementType.UNKNOWN, unknown.type());
        assertEquals("Desconocido", unknown.type().label());
        assertEquals(0, unknown.type().sign(), "el signo de un tipo desconocido no se sabe; la cantidad con signo la da el servicio");
        assertEquals(0, new BigDecimal("-3").compareTo(unknown.signedQuantity()));
        assertEquals(MovementType.UNKNOWN, unknown.relatedMovement().type());
        assertEquals(ReservationStatus.UNKNOWN, reservation.status());
        assertFalse(reservation.isActive());
        assertEquals(RevisionOperation.UNKNOWN, revision.revision().operation());
        assertFalse(MovementType.selectable().contains(MovementType.UNKNOWN));
        assertEquals(6, MovementType.selectable().size());
        assertFalse(ReservationStatus.selectable().contains(ReservationStatus.UNKNOWN));
        assertEquals(4, ReservationStatus.selectable().size());
        assertEquals(List.of(RevisionOperation.CREATED, RevisionOperation.UPDATED, RevisionOperation.DELETED), RevisionOperation.selectable());
        server.verify();
    }

    /** mto-stock no manda problem+json: error y message caen en title y detail, y lo demas por los alias de siempre. */
    @Test
    void theStockErrorJsonIsReadThroughItsAliases() {
        server.expect(requestTo(STOCK + "/materials")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Correlation-Id", "corr-s1")
                        .body("{\"timestamp\":\"2026-09-21T10:00:00Z\",\"status\":400,\"error\":\"BAD_REQUEST\",\"message\":\"Request validation failed.\","
                                + "\"path\":\"/api/v1/inventory/materials\",\"method\":\"POST\",\"errorCode\":\"REQ-VALIDATION\",\"correlationId\":\"corr-s1\","
                                + "\"validationErrors\":[{\"field\":\"code\",\"message\":\"must not be blank\"}]}"));
        server.expect(requestTo(STOCK + "/movements/outputs")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Insufficient stock for material " + MAT_ID + " in warehouse " + WH_ID
                                + ": requested 5, available 2\",\"errorCode\":\"STK-001\",\"correlationId\":null,\"validationErrors\":[]}"));
        server.expect(requestTo(STOCK + "/reservations/" + RES_ID + "/release")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_CONTENT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":422,\"error\":\"UNPROCESSABLE_CONTENT\",\"message\":\"Only active reservations can be changed\",\"errorCode\":\"RES-001\",\"validationErrors\":[]}"));

        ValidationApiException validation = assertThrows(ValidationApiException.class,
                () -> asUser(() -> materialClient.create(new MaterialRequest("", "Hilo", "m", BigDecimal.ONE))));
        ConflictApiException insufficient = assertThrows(ConflictApiException.class,
                () -> asUser(() -> movementClient.output(new OutputRequest(UUID.fromString(MAT_ID), UUID.fromString(WH_ID), null, null, new BigDecimal("5"), null, null, null))));
        ValidationApiException rule = assertThrows(ValidationApiException.class,
                () -> asUser(() -> reservationClient.release(UUID.fromString(RES_ID))));

        assertEquals("REQ-VALIDATION", validation.getProblem().code());
        assertEquals("BAD_REQUEST", validation.getProblem().title());
        assertEquals("Request validation failed.", validation.getProblem().detail());
        assertEquals("code", validation.getProblem().errors().getFirst().field());
        assertEquals("corr-s1", validation.getReference());
        assertEquals("STK-001", insufficient.getProblem().code());
        assertTrue(insufficient.getProblem().detail().contains("requested 5, available 2"));
        assertEquals(422, rule.getStatus().value());
        assertEquals("RES-001", rule.getProblem().code());
        assertFalse(rule.getProblem().hasFieldErrors());
        assertEquals("Only active reservations can be changed", rule.getProblem().detail());
        server.verify();
    }

    // --- Mantenimiento --------------------------------------------------------------------------

    private static final String MAINTENANCE = GATEWAY + "/api/maintenance";
    private static final String ORDER_ID = "2b3c4d5e-0000-4000-8000-000000000001";
    private static final String ASSET_ID = "2b3c4d5e-0000-4000-8000-000000000002";
    private static final String TEAM_ID = "2b3c4d5e-0000-4000-8000-000000000003";

    /** Una orden como la devuelve mto-maintenance; {@code status} y {@code priority} van tal cual, para probar los desconocidos. */
    private static String orderJson(String id, String code, String status, String priority) {
        return "{\"id\":\"" + id + "\",\"code\":\"" + code + "\",\"title\":\"Revision tramo 12\",\"description\":null,"
                + "\"type\":\"PREVENTIVE\",\"status\":\"" + status + "\",\"priority\":\"" + priority + "\","
                + "\"asset\":{\"id\":\"" + ASSET_ID + "\",\"code\":\"TS-0001\",\"name\":\"Tramo 12\",\"type\":\"TRACK_SECTION\","
                + "\"trackId\":12,\"startKp\":12.100,\"endKp\":13.450,\"sectioning\":null,\"enabled\":true},"
                + "\"executionPackageId\":3,\"trackId\":12,\"stationId\":null,\"startKp\":12.100,\"endKp\":13.450,"
                + "\"plannedDate\":\"2026-09-14\",\"actualStartDate\":\"2026-09-14T22:30:00Z\",\"actualEndDate\":null,"
                + "\"team\":{\"id\":\"" + TEAM_ID + "\",\"code\":\"EQ-01\",\"name\":\"Brigada norte\",\"baseName\":\"Base Norte\"},"
                + "\"assignedUser\":\"mantenimiento.tecnico\",\"closingNotes\":null,\"cancellationReason\":null,"
                + "\"originInspectionId\":null,\"originDefectId\":null,\"stockProjectId\":null,"
                + "\"taskCount\":10,\"completedTaskCount\":3,\"estimatedMinutes\":450.00,\"estimatedShifts\":2,"
                + "\"futureField\":{\"x\":1},\"audit\":{\"createdAt\":\"2026-09-01T08:00:00Z\",\"updatedAt\":\"2026-09-14T22:30:00Z\","
                + "\"createdBy\":\"mantenimiento.responsable\",\"updatedBy\":\"mantenimiento.tecnico\"}}";
    }

    /**
     * Una modificacion de mantenimiento es un merge-patch: lo cambiado con su valor, lo vaciado a
     * {@code null} y la version leida, y nada mas. Un campo a vaciar que el record no tiene se rechaza
     * antes de llamar: un nombre mal escrito no vaciaria nada y no debe salir. El 409 {@code CON-001}
     * de una version vieja llega con su codigo.
     */
    @Test
    void aMergePatchCarriesWhatChangedWhatWasClearedAndTheVersionRead() {
        OrderUpdateRequest nothing = new OrderUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null);
        MergePatch<OrderUpdateRequest> patch = new MergePatch<>(new OrderUpdateRequest(null, null, MaintenancePriority.HIGH, null, null, null,
                null, null, null, null, null, null, null), Set.of("teamId", "description"), 3L);
        java.util.Map<String, Object> expected = new java.util.LinkedHashMap<>();
        expected.put("priority", MaintenancePriority.HIGH);
        expected.put("description", null);
        expected.put("teamId", null);
        expected.put("version", 3L);
        assertEquals(expected, patch.body());
        assertEquals(List.of("priority", "description", "teamId", "version"), List.copyOf(patch.body().keySet()),
                "lo cambiado, lo vaciado por orden alfabetico y la version");
        assertFalse(patch.changesNothing());
        assertTrue(MergePatch.of(nothing, 3L).changesNothing(), "solo la version no es un cambio");
        assertFalse(new MergePatch<>(nothing, Set.of("description"), 3L).changesNothing(), "vaciar si lo es");
        assertThrows(IllegalArgumentException.class, () -> new MergePatch<>(nothing, Set.of("titel"), 3L));

        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID)).andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Maintenance order MO-000001 was changed by someone else\","
                                + "\"errorCode\":\"CON-001\",\"validationErrors\":[]}"));
        ConflictApiException stale = assertThrows(ConflictApiException.class,
                () -> asUser(() -> orderClient.update(UUID.fromString(ORDER_ID), patch)));
        assertEquals("CON-001", stale.getProblem().code());
        server.verify();
    }

    /**
     * Los filtros viajan como el servicio los lee: el enumerado por su nombre, la fecha en ISO (sin
     * {@code @DateTimeFormat} saldria con el formato corto de la maquina), el texto en blanco no
     * viaja y el orden se repite. Un valor que esta version no conoce se lee como {@code UNKNOWN} y
     * no rompe la pagina.
     */
    @Test
    void ordersArePagedWithTheirFiltersAndAnUnknownValueIsReadAsUnknown() {
        server.expect(requestTo(MAINTENANCE + "/orders?status=IN_PROGRESS&type=PREVENTIVE&assetType=TRACK_SECTION&trackId=12"
                        + "&plannedFrom=2026-09-01&plannedTo=2026-09-30&teamId=" + TEAM_ID + "&code=MO-0000&page=1&size=50"
                        + "&sort=plannedDate%2Casc&sort=code%2Cdesc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-for-" + PRINCIPAL))
                .andRespond(withSuccess(stockPage(orderJson(ORDER_ID, "MO-000001", "IN_PROGRESS", "HIGH") + ","
                        + orderJson("2b3c4d5e-0000-4000-8000-000000000009", "MO-000002", "ON_HOLD", "SOMEDAY"), 1, 50, 52), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "IN_PROGRESS", "HIGH"), MediaType.APPLICATION_JSON));

        OrderFilter filter = new OrderFilter(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE, null, null,
                CatenaryAssetType.TRACK_SECTION, 12L, null, null, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), "  ",
                UUID.fromString(TEAM_ID), " MO-0000 ");
        PageResponse<OrderDto> page = asUser(() -> orderClient.search(filter, 1, 50, List.of("plannedDate,asc", "code,desc")));
        OrderDto one = asUser(() -> orderClient.findById(UUID.fromString(ORDER_ID)));

        assertEquals(52, page.page().totalElements());
        OrderDto order = page.content().getFirst();
        assertEquals("MO-000001", order.code());
        assertEquals(MaintenanceOrderType.PREVENTIVE, order.type());
        assertEquals(MaintenancePriority.HIGH, order.priority());
        assertEquals("TS-0001 - Tramo 12", order.asset().label());
        assertEquals(CatenaryAssetType.TRACK_SECTION, order.asset().type());
        assertEquals("EQ-01 - Brigada norte", order.team().label());
        assertEquals(LocalDate.of(2026, 9, 14), order.plannedDate());
        assertEquals(3, order.completedTaskCount());
        assertEquals(new BigDecimal("450.00"), order.estimatedMinutes());
        assertEquals("mantenimiento.responsable", order.audit().createdBy());
        OrderDto unknown = page.content().get(1);
        assertEquals(MaintenanceOrderStatus.UNKNOWN, unknown.status());
        assertEquals(MaintenancePriority.UNKNOWN, unknown.priority());
        assertEquals("Desconocido", unknown.status().label());
        assertFalse(MaintenanceOrderStatus.selectable().contains(MaintenanceOrderStatus.UNKNOWN));
        assertEquals(6, MaintenanceOrderStatus.selectable().size());
        assertEquals(order, one);
        server.verify();
    }

    /** mto-maintenance manda el mismo JSON de error que mto-stock, con path y method de mas: se lee por los mismos alias. */
    @Test
    void theMaintenanceErrorJsonIsReadThroughItsAliases() {
        server.expect(requestTo(MAINTENANCE + "/orders?page=0&size=50&sort=nope%2Casc&sort=id%2Casc")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON)
                        .header("X-Correlation-Id", "corr-m1")
                        .body("{\"timestamp\":\"2026-09-26T10:00:00Z\",\"status\":400,\"error\":\"BAD_REQUEST\",\"message\":\"Invalid request parameter.\","
                                + "\"path\":\"/api/v1/maintenance/orders\",\"method\":\"GET\",\"errorCode\":\"REQ-400\",\"correlationId\":\"corr-m1\","
                                + "\"validationErrors\":[{\"field\":\"sort\",\"message\":\"unknown property 'nope'\"}]}"));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":404,\"error\":\"NOT_FOUND\",\"message\":\"MaintenanceOrder with id " + ORDER_ID + " was not found\","
                                + "\"path\":\"/api/v1/maintenance/orders/" + ORDER_ID + "\",\"method\":\"GET\",\"errorCode\":\"ORD-404\","
                                + "\"correlationId\":null,\"validationErrors\":[]}"));

        ValidationApiException sort = assertThrows(ValidationApiException.class,
                () -> asUser(() -> orderClient.search(OrderFilter.NONE, 0, 50, List.of("nope,asc"))));
        NotFoundApiException missing = assertThrows(NotFoundApiException.class,
                () -> asUser(() -> orderClient.findById(UUID.fromString(ORDER_ID))));

        assertEquals("REQ-400", sort.getProblem().code());
        assertEquals("sort", sort.getProblem().errors().getFirst().field());
        assertEquals("unknown property 'nope'", sort.getProblem().errors().getFirst().message());
        assertEquals("corr-m1", sort.getReference());
        assertEquals("ORD-404", missing.getProblem().code());
        assertTrue(missing.getProblem().detail().contains("was not found"));
        server.verify();
    }

    private static final String INSULATOR_JSON = "{\"id\":\"" + ASSET_ID + "\",\"code\":\"SI-0007\",\"name\":\"AS-7\",\"type\":\"SECTION_INSULATOR\","
            + "\"description\":null,\"executionPackageId\":3,\"trackId\":12,\"stationId\":4,\"startKp\":12.400,\"endKp\":12.400,"
            + "\"profileSourceId\":null,\"sectioning\":null,\"trackKind\":null,\"connectedTrackId\":13,\"installationType\":\"TRACK_CONNECTION\","
            + "\"switches\":[{\"id\":\"2b3c4d5e-0000-4000-8000-00000000000a\",\"code\":\"W31\",\"kp\":12.410,\"turnoutDenominator\":9,"
            + "\"turnoutRate\":\"1:9\",\"trackId\":13,\"enabled\":false}],"
            + "\"sourceService\":\"mto-configuration\",\"sourceEntityId\":\"77\",\"sourceSequenceNumber\":41,\"enabled\":true,"
            + "\"enabledAtSource\":true,\"disabledLocally\":false,"
            + "\"preventiveIntervalDays\":180,\"lastPreventiveCompletedAt\":null,\"nextPreventiveDueAt\":\"2026-10-01T00:00:00Z\",\"audit\":null}";

    /**
     * Activos: la busqueda con sus filtros (el instante en ISO), el alta de un tramo sin lo vacio,
     * la modificacion parcial con solo lo que cambio, la reactivacion, la baja con un DELETE sin
     * cuerpo y las ordenes de un activo. Un tipo que esta version no conoce se lee como UNKNOWN.
     */
    @Test
    void assetsAreSearchedCreatedPartiallyUpdatedAndDisabled() {
        server.expect(requestTo(MAINTENANCE + "/assets?type=SECTION_INSULATOR&trackId=12&enabled=true&name=AS&preventiveDueBefore=2026-10-01T22%3A00%3A00Z"
                        + "&page=0&size=50&sort=trackId%2Casc&sort=startKp%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(INSULATOR_JSON + "," + INSULATOR_JSON.replace("SECTION_INSULATOR", "CANTILEVER")
                        .replace(ASSET_ID, "2b3c4d5e-0000-4000-8000-00000000000b")
                        .replace("\"enabled\":true,\"enabledAtSource\":true,\"disabledLocally\":false",
                                "\"enabled\":false,\"enabledAtSource\":false,\"disabledLocally\":true"), 0, 50, 2), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.code").value("TS-0002"))
                .andExpect(jsonPath("$.trackId").value(12))
                .andExpect(jsonPath("$.startKp").value(13.45))
                .andExpect(jsonPath("$.trackKind").value("DIVERTED"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.stationId").doesNotExist())
                .andRespond(withSuccess(INSULATOR_JSON.replace("SI-0007", "TS-0002"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"preventiveIntervalDays\":90,\"description\":null,\"stationId\":null,\"version\":4}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(INSULATOR_JSON, MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"enabled\":true,\"version\":4}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(INSULATOR_JSON, MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID + "/orders?page=0&size=20&sort=createdAt%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(orderJson(ORDER_ID, "MO-000001", "COMPLETED", "LOW"), 0, 20, 1), MediaType.APPLICATION_JSON));

        PageResponse<AssetDto> page = asUser(() -> assetClient.search(new AssetFilter(CatenaryAssetType.SECTION_INSULATOR, 12L, null, null, true,
                " AS ", Instant.parse("2026-10-01T22:00:00Z")), 0, 50, List.of("trackId,asc", "startKp,asc")));
        AssetDto created = asUser(() -> assetClient.create(new AssetRequest("TS-0002", "Tramo 13", null, 3L, 12L, null,
                new BigDecimal("13.45"), new BigDecimal("14.2"), TrackKind.DIVERTED, null)));
        asUser(() -> assetClient.update(UUID.fromString(ASSET_ID), new MergePatch<>(
                new AssetUpdateRequest(null, null, null, 90, null, null, null, null, null, null), Set.of("description", "stationId"), 4L)));
        asUser(() -> assetClient.update(UUID.fromString(ASSET_ID), MergePatch.of(AssetUpdateRequest.enabled(true), 4L)));
        asUser(() -> {
            assetClient.disable(UUID.fromString(ASSET_ID));
            return null;
        });
        PageResponse<OrderDto> orders = asUser(() -> assetClient.orders(UUID.fromString(ASSET_ID), 0, 20, List.of("createdAt,desc")));

        AssetDto insulator = page.content().getFirst();
        assertTrue(insulator.isSynchronized());
        assertEquals(SectionInsulatorInstallation.TRACK_CONNECTION, insulator.installationType());
        assertEquals("W31 1:9", insulator.switches().getFirst().label());
        assertFalse(insulator.switches().getFirst().enabled(), "una aguja dada de baja sigue apareciendo, marcada");
        assertEquals(Instant.parse("2026-10-01T00:00:00Z"), insulator.nextPreventiveDueAt());
        assertEquals(CatenaryAssetType.UNKNOWN, page.content().get(1).type());
        assertFalse(insulator.isDisabledAtSource() || insulator.isDisabledLocally());
        AssetDto disabled = page.content().get(1);
        assertFalse(disabled.isEnabled());
        assertTrue(disabled.isDisabledAtSource(), "mto-configuration lo tiene desactivado");
        assertTrue(disabled.isDisabledLocally(), "y mantenimiento tambien");
        assertEquals("TS-0002", created.code());
        assertEquals(MaintenanceOrderStatus.COMPLETED, orders.content().getFirst().status());
        assertTrue(AssetUpdateRequest.enabled(false).equals(new AssetUpdateRequest(null, null, false, null, null, null, null, null, null, null)));
        server.verify();
    }

    /**
     * Los catalogos: equipos (el PUT es completo, asi que base y vehiculo vaciados viajan como
     * null), tipos de tarea filtrados por grupo y plantillas con sus puntos.
     */
    @Test
    void maintenanceCataloguesAreReadAndTeamsAreWrittenWhole() {
        String team = "{\"id\":\"" + TEAM_ID + "\",\"code\":\"EQ-01\",\"name\":\"Brigada norte\",\"baseName\":\"Base Norte\","
                + "\"vehicle\":\"DR-2\",\"active\":true,\"executionPackageIds\":[3,5],\"audit\":null}";
        // Uno sincronizado desde mto-resources: su unidad alli, su dotacion y su vehiculo; sin base.
        String synced = "{\"id\":\"2b3c4d5e-0000-4000-8000-000000000040\",\"code\":\"A\",\"name\":\"Equipo A\",\"baseName\":null,"
                + "\"vehicle\":\"DR-01\",\"active\":true,\"executionPackageIds\":[4],\"synchronizedFromResources\":true,"
                + "\"sourceEntityId\":\"5e6f7081-0000-4000-8000-000000000001\",\"leader\":{\"id\":\"3c4d5e6f-0000-4000-8000-000000000001\","
                + "\"code\":\"PER-000003\",\"fullName\":\"Yossi Cohen\",\"username\":\"equipo.jefe\",\"category\":\"TEAM_LEADER\"},"
                + "\"supervisor\":null,\"members\":[{\"id\":\"3c4d5e6f-0000-4000-8000-000000000011\",\"code\":\"PER-000011\","
                + "\"fullName\":\"Lineman 1\",\"username\":null,\"category\":\"FOREMAN_TRAINEE\"}],"
                + "\"defaultVehicleId\":\"9d0e1f20-0000-4000-8000-000000000001\",\"audit\":null}";
        server.expect(requestTo(MAINTENANCE + "/teams")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + team + "," + synced + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/teams")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.code").value("EQ-02"))
                .andExpect(jsonPath("$.executionPackageIds[0]").value(3))
                .andRespond(withSuccess(team.replace("EQ-01", "EQ-02"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/teams/" + TEAM_ID)).andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.baseName").value(nullValue()))
                .andExpect(jsonPath("$.vehicle").value(nullValue()))
                .andExpect(jsonPath("$.active").value(false))
                .andRespond(withSuccess(team, MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/task-types?functionalGroup=OVERHEAD_CONDUCTORS")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"2b3c4d5e-0000-4000-8000-000000000020\",\"code\":\"RG-04\",\"description\":\"Revision del hilo de contacto\","
                        + "\"functionalGroup\":\"OVERHEAD_CONDUCTORS\",\"standardMinutesPerUnit\":12.50,\"unit\":\"SPAN\",\"fixedMinutes\":null,"
                        + "\"requiresFullPossession\":true,\"diagnostic\":false,\"active\":true,\"orderIndex\":4}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/inspection-templates")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"2b3c4d5e-0000-4000-8000-000000000030\",\"assetType\":\"PROFILE\",\"version\":2,"
                        + "\"name\":\"Perfil\",\"active\":true,\"items\":[{\"id\":\"2b3c4d5e-0000-4000-8000-000000000031\",\"code\":\"P-01\","
                        + "\"label\":\"Altura del hilo\",\"unit\":\"mm\",\"minValue\":5300,\"maxValue\":5700,\"requiresMeasure\":true,\"orderIndex\":1}]}]",
                        MediaType.APPLICATION_JSON));

        List<TeamDto> teams = asUser(() -> maintenanceCatalogClient.teams());
        TeamDto created = asUser(() -> maintenanceCatalogClient.createTeam(new TeamRequest("EQ-02", "Brigada sur", null, null, true,
                new java.util.TreeSet<>(Set.of(3L)))));
        asUser(() -> maintenanceCatalogClient.updateTeam(UUID.fromString(TEAM_ID), new TeamRequest("EQ-01", "Brigada norte", null, null, false,
                Set.of(3L, 5L))));
        List<TaskTypeDto> types = asUser(() -> maintenanceCatalogClient.taskTypes(FunctionalGroup.OVERHEAD_CONDUCTORS, null, null));
        List<InspectionTemplateDto> templates = asUser(() -> maintenanceCatalogClient.inspectionTemplates());

        assertEquals(Set.of(3L, 5L), teams.getFirst().executionPackageIds());
        assertEquals("EQ-01 - Brigada norte", teams.getFirst().label());
        assertFalse(teams.getFirst().fromResources(), "uno de antes, sin las claves nuevas, es local");
        TeamDto fromResources = teams.get(1);
        assertTrue(fromResources.fromResources());
        assertEquals(UUID.fromString("5e6f7081-0000-4000-8000-000000000001"), fromResources.sourceEntityId());
        assertEquals("PER-000003 · Yossi Cohen", fromResources.leader().label());
        assertEquals(PersonCategory.UNKNOWN, fromResources.members().getFirst().category(), "una categoria nueva no rompe la lista");
        assertEquals(UUID.fromString("9d0e1f20-0000-4000-8000-000000000001"), fromResources.defaultVehicleId());
        assertEquals("EQ-02", created.code());
        assertEquals(TaskUnit.SPAN, types.getFirst().unit());
        assertTrue(types.getFirst().requiresFullPossession());
        assertEquals(new BigDecimal("12.50"), types.getFirst().standardMinutesPerUnit());
        assertEquals(CatenaryAssetType.PROFILE, templates.getFirst().assetType());
        assertEquals(new BigDecimal("5300"), templates.getFirst().items().getFirst().minValue());
        server.verify();
    }

    private static final String STRICT_EMPTY = "{}";

    /**
     * El ciclo de una orden en sus rutas: alta sin lo vacio, modificacion con solo lo cambiado, y
     * cada transicion con su cuerpo (la fecha en ISO, lo vacio fuera, iniciar con un {} ). Un
     * TRN-001 llega como conflicto con su codigo.
     */
    @Test
    void ordersAreCreatedUpdatedAndMovedThroughTheirStates() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        server.expect(requestTo(MAINTENANCE + "/orders")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"title\":\"Revision tramo 12\",\"type\":\"PREVENTIVE\",\"priority\":\"MEDIUM\",\"assetId\":\""
                        + ASSET_ID + "\"}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "DRAFT", "MEDIUM"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"priority\":\"HIGH\",\"plannedDate\":\"2026-10-05\",\"teamId\":null,\"version\":3}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "DRAFT", "HIGH"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/plan")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"plannedDate\":\"2026-10-05\",\"comment\":\"noche del lunes\"}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "PLANNED", "HIGH"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/assign")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"teamId\":\"" + TEAM_ID + "\"}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "ASSIGNED", "HIGH"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/start")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(STRICT_EMPTY, strict))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Order MO-000001 cannot be started from ASSIGNED\","
                                + "\"errorCode\":\"TRN-001\",\"validationErrors\":[]}"));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/complete")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"closingNotes\":\"Sin incidencias\",\"force\":true}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "COMPLETED", "HIGH"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/cancel")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"reason\":\"Duplicada\"}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000001", "CANCELLED", "HIGH"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/history")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"2b3c4d5e-0000-4000-8000-000000000040\",\"previousStatus\":null,\"newStatus\":\"DRAFT\","
                        + "\"changedAt\":\"2026-09-20T08:00:00Z\",\"changedBy\":\"mantenimiento.tecnico\",\"comment\":\"Order created\"},"
                        + "{\"id\":\"2b3c4d5e-0000-4000-8000-000000000041\",\"previousStatus\":\"DRAFT\",\"newStatus\":\"PLANNED\","
                        + "\"changedAt\":\"2026-09-21T08:00:00Z\",\"changedBy\":\"mantenimiento.tecnico\",\"comment\":null}]", MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(ORDER_ID);
        OrderDto created = asUser(() -> orderClient.create(new OrderRequest("Revision tramo 12", null, MaintenanceOrderType.PREVENTIVE,
                MaintenancePriority.MEDIUM, UUID.fromString(ASSET_ID), null, null, null, null)));
        asUser(() -> orderClient.update(id, new MergePatch<>(new OrderUpdateRequest(null, null, MaintenancePriority.HIGH, LocalDate.of(2026, 10, 5),
                null, null, null, null, null, null, null, null, null), Set.of("teamId"), 3L)));
        OrderDto planned = asUser(() -> orderClient.plan(id, new PlanOrderRequest(LocalDate.of(2026, 10, 5), "noche del lunes")));
        asUser(() -> orderClient.assign(id, new AssignOrderRequest(UUID.fromString(TEAM_ID), null, null)));
        ConflictApiException refused = assertThrows(ConflictApiException.class, () -> asUser(() -> orderClient.start(id, new CommentRequest(null))));
        OrderDto completed = asUser(() -> orderClient.complete(id, new CompleteOrderRequest("Sin incidencias", true, null)));
        OrderDto cancelled = asUser(() -> orderClient.cancel(id, new ReasonRequest("Duplicada")));
        List<StatusHistoryDto> history = asUser(() -> orderClient.history(id));

        assertEquals(MaintenanceOrderStatus.DRAFT, created.status());
        assertEquals(MaintenanceOrderStatus.PLANNED, planned.status());
        assertEquals("TRN-001", refused.getProblem().code());
        assertEquals(MaintenanceOrderStatus.COMPLETED, completed.status());
        assertEquals(MaintenanceOrderStatus.CANCELLED, cancelled.status());
        assertNull(history.getFirst().previousStatus(), "el alta no tiene estado anterior");
        assertEquals("PLANNED", history.get(1).newStatus());
        assertTrue(new OrderUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null).changesNothing());
        server.verify();
    }

    private static final String TASK_ID = "2b3c4d5e-0000-4000-8000-000000000050";

    private static String taskJson(String status) {
        return "{\"id\":\"" + TASK_ID + "\",\"orderId\":\"" + ORDER_ID + "\",\"sequence\":3,\"description\":\"Perfil 12-2.27\","
                + "\"status\":\"" + status + "\",\"assignedUser\":null,\"asset\":{\"id\":\"" + ASSET_ID + "\",\"code\":\"PRF-0001\","
                + "\"name\":\"12-2.27\",\"type\":\"PROFILE\",\"trackId\":12,\"startKp\":12.270,\"endKp\":12.270,\"sectioning\":\"S-3\","
                + "\"enabled\":true},\"shiftId\":null,\"startedAt\":null,\"completedAt\":null,\"defectsFound\":null,\"notes\":null,"
                + "\"photoRefs\":[],\"taskTypeCodes\":[\"RG-01\",\"RG-04\"],\"checkItems\":[{\"id\":\"2b3c4d5e-0000-4000-8000-000000000051\","
                + "\"code\":\"P-01\",\"label\":\"Altura del hilo\",\"unit\":\"mm\",\"minValue\":5300,\"maxValue\":5700,\"requiresMeasure\":true,"
                + "\"measuredValue\":null,\"adjusted\":null,\"valueAfterAdjustment\":null,\"itemResult\":null,\"notes\":null,\"orderIndex\":1,"
                + "\"outOfRange\":false}],\"audit\":null}";
    }

    @Test
    void tasksAreListedCreatedGeneratedUpdatedAndCancelled() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String tasks = MAINTENANCE + "/orders/" + ORDER_ID + "/tasks";
        server.expect(requestTo(tasks)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + taskJson("PENDING") + "," + taskJson("ON_HOLD") + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(tasks)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"description\":\"Revisar la mensula\",\"assetId\":\"" + ASSET_ID
                        + "\",\"taskTypeCodes\":[\"RG-04\"],\"withChecklist\":true}", strict))
                .andRespond(withSuccess(taskJson("PENDING"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(tasks + "/generate")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(STRICT_EMPTY, strict))
                .andRespond(withSuccess("{\"createdTasks\":14,\"skippedProfiles\":2,\"totalTasks\":16,\"estimatedMinutes\":720.0,\"estimatedShifts\":3}",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(tasks + "/" + TASK_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"taskTypeCodes\":[\"RG-01\"],\"notes\":\"Falta la llave\",\"assignedUser\":null,\"version\":2}", strict))
                .andRespond(withSuccess(taskJson("PENDING"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(tasks + "/" + TASK_ID + "/cancel")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"reason\":\"Perfil desmontado\"}", strict))
                .andRespond(withSuccess(taskJson("CANCELLED"), MediaType.APPLICATION_JSON));

        UUID orderId = UUID.fromString(ORDER_ID);
        UUID taskId = UUID.fromString(TASK_ID);
        List<TaskDto> listed = asUser(() -> orderClient.tasks(orderId));
        asUser(() -> orderClient.createTask(orderId, new TaskRequest("Revisar la mensula", UUID.fromString(ASSET_ID), null, List.of("RG-04"), true)));
        GenerateTasksResultDto generated = asUser(() -> orderClient.generateTasks(orderId, GenerateTasksRequest.ofSection(null, null, null, null)));
        asUser(() -> orderClient.updateTask(orderId, taskId, new MergePatch<>(new TaskUpdateRequest(null, null, List.of("RG-01"), "Falta la llave",
                null, null), Set.of("assignedUser"), 2L)));
        TaskDto cancelled = asUser(() -> orderClient.cancelTask(orderId, taskId, new ReasonRequest("Perfil desmontado")));

        TaskDto task = listed.getFirst();
        assertEquals(List.of("RG-01", "RG-04"), task.taskTypeCodes());
        assertEquals("12-2.27", task.asset().name());
        assertEquals(new BigDecimal("5300"), task.checkItems().getFirst().minValue());
        assertNull(task.checkItems().getFirst().itemResult());
        assertTrue(task.isOpen());
        assertEquals(MaintenanceTaskStatus.UNKNOWN, listed.get(1).status());
        assertFalse(listed.get(1).isOpen(), "un estado desconocido no abre ninguna accion");
        assertEquals(14, generated.createdTasks());
        assertEquals(3, generated.estimatedShifts());
        assertEquals(MaintenanceTaskStatus.CANCELLED, cancelled.status());
        assertEquals(CheckItemResult.UNKNOWN, CheckItemResult.of("MAYBE"));
        server.verify();
    }

    /**
     * Generar en varias vias del EP: una seleccion por sitio, con sus tipos y lo vacio fuera, y
     * {@code dryRun}; la previsualizacion trae cada activo, con lo creado por tipo de activo, y lo que
     * el cliente no conoce (un resultado nuevo, un tipo de activo nuevo como clave) se lee igual.
     */
    @Test
    void generatingSendsASelectionPerPlaceAndThePreviewBringsEachAsset() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/tasks/generate")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"selections\":[{\"assetType\":\"PROFILE\",\"sectionId\":\"" + ASSET_ID + "\"},"
                        + "{\"assetType\":\"SECTION_INSULATOR\",\"trackId\":13,\"fromKp\":100,\"toKp\":200.5},"
                        + "{\"assetType\":\"DISCONNECTOR\",\"stationId\":4,\"taskTypeCodes\":[\"RP-06\"]},"
                        + "{\"assetType\":\"DISCONNECTOR\",\"assetIds\":[\"" + ASSET_ID + "\"]}],\"dryRun\":true}", strict))
                .andRespond(withSuccess("{\"createdTasks\":1,\"skippedProfiles\":0,\"totalTasks\":1,\"estimatedMinutes\":45,\"estimatedShifts\":1,"
                        + "\"skippedAssets\":0,\"createdByAssetType\":{\"DISCONNECTOR\":1,\"PROFILE\":0},\"skippedInOtherOrders\":1,"
                        + "\"otherOrderCodes\":[\"MO-000042\"],\"skippedOtherPackage\":2,\"skippedWithoutTaskTypes\":0,\"dryRun\":true,\"assets\":["
                        + "{\"assetId\":\"" + ASSET_ID + "\",\"code\":\"DSC-1\",\"name\":\"HSA-NS5\",\"type\":\"DISCONNECTOR\",\"trackId\":13,"
                        + "\"connectedTrackId\":null,\"kp\":12.9,\"taskTypeCodes\":[\"RP-06\"],\"withChecklist\":false,\"outcome\":\"CREATE\","
                        + "\"otherOrderCode\":null},{\"assetId\":\"" + ASSET_ID + "\",\"code\":\"PRF-1\",\"type\":\"PROFILE\","
                        + "\"outcome\":\"SOMETHING_NEW\",\"futureField\":1}]}", MediaType.APPLICATION_JSON));

        UUID asset = UUID.fromString(ASSET_ID);
        com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType disconnector =
                com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType.DISCONNECTOR;
        GenerateTasksResultDto preview = asUser(() -> orderClient.generateTasks(UUID.fromString(ORDER_ID), new GenerateTasksRequest(null, null, null, List.of(
                new GenerationSelection(com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType.PROFILE, asset, null, null, null, null, null, null),
                new GenerationSelection(com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType.SECTION_INSULATOR, null, 13L,
                        new BigDecimal("100"), new BigDecimal("200.5"), null, null, null),
                new GenerationSelection(disconnector, null, null, null, null, 4L, null, List.of("RP-06")),
                new GenerationSelection(disconnector, null, null, null, null, null, List.of(asset), null)), true)));

        assertEquals(Integer.valueOf(1), preview.createdByAssetType().get(disconnector));
        assertEquals(1, preview.inOtherOrders());
        assertEquals(List.of("MO-000042"), preview.otherOrderCodes());
        assertEquals(2, preview.otherPackage());
        assertEquals(Boolean.TRUE, preview.dryRun());
        assertEquals(List.of("RP-06"), preview.assets().getFirst().taskTypeCodes());
        assertEquals("DSC-1 - HSA-NS5", preview.assets().getFirst().label());
        assertEquals(GenerationOutcome.UNKNOWN, preview.assets().get(1).outcome(), "un resultado nuevo se lee como desconocido");
        assertEquals(List.of(), preview.assets().get(1).taskTypeCodes());
        assertEquals(DisconnectorDrive.MOTOR, DisconnectorDrive.of("MOTOR"));
        assertEquals(DisconnectorDrive.UNKNOWN, DisconnectorDrive.of("HYDRAULIC"));
        server.verify();
    }

    /**
     * Un aislador que une dos vias: su resumen trae la conectada, sus turnos se piden con las dos
     * ({@code trackId} repetido), generar con los aisladores manda los dos tipos, y el parte trae las
     * inspecciones del turno. Lo que un servicio anterior no manda se lee igual.
     */
    @Test
    void insulatorsJoiningTwoTracksAreWorkedFromEitherAndTheShiftReportBringsItsInspections() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/tasks/generate")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"taskTypeCodes\":[\"RG-03\"],\"assetTypes\":[\"PROFILE\",\"SECTION_INSULATOR\"]}", strict))
                .andRespond(withSuccess("{\"createdTasks\":3,\"skippedProfiles\":14,\"totalTasks\":19,\"estimatedMinutes\":810.0,"
                        + "\"estimatedShifts\":3,\"skippedAssets\":16}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/tasks/generate")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"createdTasks\":0,\"skippedProfiles\":14,\"totalTasks\":14,\"estimatedMinutes\":720.0,"
                        + "\"estimatedShifts\":3}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/shifts?trackId=13&trackId=12&status=IN_PROGRESS&page=0&size=50&sort=shiftDate%2Cdesc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"content\":[" + shiftJson("IN_PROGRESS") + "],\"page\":{\"size\":50,\"number\":0,"
                        + "\"totalElements\":1,\"totalPages\":1}}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/shifts/" + SHIFT_ID + "/report")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"shift\":" + shiftJson("CLOSED") + ",\"tasksCompleted\":0,\"tasksPending\":0,\"profilesReviewed\":0,"
                        + "\"defectsFound\":0,\"defectsResolved\":0,\"rows\":[],\"inspectionsRecorded\":1,\"inspections\":[{\"inspectionId\":\""
                        + INSPECTION_ID + "\",\"code\":\"INS-000040\",\"inspectionDate\":\"2026-10-06\",\"assetCode\":\"SIN-0041\","
                        + "\"assetName\":\"SI-W41\",\"assetType\":\"SECTION_INSULATOR\",\"trackId\":13,\"kp\":12.921,\"inspectionKind\":\"VISUAL\","
                        + "\"result\":\"UNSAFE\",\"inspector\":\"dana\",\"detectedDefects\":\"Patin quemado\",\"generatedDefectCode\":\"DEF-000007\","
                        + "\"generatedOrderCode\":\"MO-000031\",\"futureField\":1}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/inspections")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"assetId\":\"" + ASSET_ID + "\",\"inspectionDate\":\"2026-10-06\",\"inspectionKind\":\"VISUAL\","
                        + "\"result\":\"OK\",\"shiftId\":\"" + SHIFT_ID + "\"}", strict))
                .andRespond(withSuccess("{\"id\":\"" + INSPECTION_ID + "\",\"code\":\"INS-000041\",\"shiftId\":\"" + SHIFT_ID + "\"}",
                        MediaType.APPLICATION_JSON));

        UUID orderId = UUID.fromString(ORDER_ID);
        GenerateTasksResultDto generated = asUser(() -> orderClient.generateTasks(orderId, GenerateTasksRequest.ofSection(List.of("RG-03"), null,
                GenerateTasksRequest.WITH_INSULATORS, null)));
        GenerateTasksResultDto fromAnOlderService = asUser(() -> orderClient.generateTasks(orderId, GenerateTasksRequest.ofSection(null, null, null, null)));
        com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto insulator = new com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto(
                UUID.fromString(ASSET_ID), "SIN-0041", "SI-W41", com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType.SECTION_INSULATOR,
                13L, null, null, null, true, 12L, null, null, null);
        PageResponse<ShiftDto> shifts = asUser(() -> shiftClient.search(ShiftFilter.inProgressOn(insulator.tracks(99L)), 0, 50,
                List.of("shiftDate,desc")));
        com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportDto report = asUser(() -> reportClient.shiftReport(UUID.fromString(SHIFT_ID)));
        asUser(() -> inspectionClient.create(new InspectionRequest(UUID.fromString(ASSET_ID), LocalDate.of(2026, 10, 6), null, InspectionKind.VISUAL,
                InspectionResult.OK, null, null, null, null, null, UUID.fromString(SHIFT_ID))));

        assertEquals(16, generated.skipped());
        assertEquals(14, fromAnOlderService.skipped(), "sin skippedAssets cuenta los perfiles, como antes");
        assertEquals(0, fromAnOlderService.inOtherOrders(), "ni lo saltado nuevo");
        assertEquals(List.of(), fromAnOlderService.assets());
        assertEquals(1, shifts.content().size());
        assertEquals(List.of(12L), com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto.tracksOf(null, 12L));
        assertEquals(1, report.inspectionsRecorded());
        assertEquals("SIN-0041 - SI-W41", report.inspections().getFirst().assetLabel());
        assertEquals(InspectionResult.UNSAFE, report.inspections().getFirst().result());
        assertEquals("MO-000031", report.inspections().getFirst().generatedOrderCode());
        server.verify();
    }

    private static final String SHIFT_ID = "2b3c4d5e-0000-4000-8000-000000000060";
    private static final String DISCONNECTOR_ID = "2b3c4d5e-0000-4000-8000-000000000061";

    private static String shiftJson(String status) {
        return "{\"id\":\"" + SHIFT_ID + "\",\"code\":\"SH-000001\",\"shiftDate\":\"2026-10-05\",\"team\":{\"id\":\"" + TEAM_ID
                + "\",\"code\":\"EQ-01\",\"name\":\"Brigada norte\",\"baseName\":\"Base Norte\"},\"baseName\":\"Base Norte\",\"vehicle\":\"DR-2\","
                + "\"possessionType\":\"FULL\",\"plannedStart\":\"2026-10-05T21:30:00Z\",\"plannedEnd\":\"2026-10-06T04:30:00Z\","
                + "\"actualStart\":null,\"actualEnd\":null,\"voltageCutoffAt\":null,\"netWorkMinutes\":null,"
                + "\"blockingDisconnectors\":[{\"id\":\"" + DISCONNECTOR_ID + "\",\"code\":\"DIS-0005\",\"name\":\"HSA-NS5\","
                + "\"type\":\"DISCONNECTOR\",\"trackId\":12,\"startKp\":12.000,\"endKp\":12.000,\"sectioning\":null,\"enabled\":true}],"
                + "\"earthingPoints\":\"P12-3, P12-9\",\"parkingPlace\":\"Apartadero km 11\",\"executionPackageId\":3,\"trackIds\":[12,13],"
                + "\"startKp\":12.000,\"endKp\":14.000,\"personnel\":\"4 operarios\",\"measurementEquipment\":null,\"status\":\"" + status + "\","
                + "\"observations\":null,\"audit\":null}";
    }

    /**
     * Turnos: la busqueda con sus fechas en ISO, el alta sin lo vacio, la modificacion con los
     * conjuntos enteros (un conjunto vacio de seccionadores es «ninguno»), las transiciones con sus
     * cuerpos, sus tareas y perfiles, y asignar una tarea con un POST sin cuerpo.
     */
    @Test
    void shiftsAreSearchedCreatedMovedAndGiveTheirTasksAndProfiles() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String shifts = MAINTENANCE + "/shifts";
        server.expect(requestTo(shifts + "?dateFrom=2026-10-01&dateTo=2026-10-31&trackId=12&status=IN_PROGRESS&possessionType=FULL"
                        + "&page=0&size=50&sort=shiftDate%2Cdesc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(shiftJson("IN_PROGRESS"), 0, 50, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"shiftDate\":\"2026-10-05\",\"possessionType\":\"PARTIAL\",\"trackIds\":[12],"
                        + "\"plannedStart\":\"2026-10-05T21:30:00Z\"}", strict))
                .andRespond(withSuccess(shiftJson("PLANNED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"trackIds\":[12,13],\"blockingDisconnectorIds\":null,\"version\":5}", strict))
                .andRespond(withSuccess(shiftJson("PLANNED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/start")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json(STRICT_EMPTY, strict))
                .andRespond(withSuccess(shiftJson("IN_PROGRESS"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/close")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"netWorkMinutes\":240,\"observations\":\"Sin incidencias\"}", strict))
                .andRespond(withSuccess(shiftJson("CLOSED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/cancel")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"reason\":\"Lluvia\"}", strict))
                .andRespond(withSuccess(shiftJson("CANCELLED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/tasks?status=PENDING")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + taskJson("PENDING") + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/profiles")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"" + ASSET_ID + "\",\"code\":\"PRF-0001\",\"name\":\"12-2.27\",\"type\":\"PROFILE\","
                        + "\"trackId\":12,\"startKp\":12.270,\"endKp\":12.270,\"sectioning\":\"S-3\",\"enabled\":true}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/tasks/" + TASK_ID)).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(taskJson("PENDING"), MediaType.APPLICATION_JSON));
        // Sacarla del turno es un POST sin cuerpo, como asignarla (un DELETE pediria maintenance-delete).
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/tasks/" + TASK_ID + "/unassign")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(taskJson("PENDING"), MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(SHIFT_ID);
        PageResponse<ShiftDto> page = asUser(() -> shiftClient.search(new ShiftFilter(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), null,
                List.of(12L), null, ShiftStatus.IN_PROGRESS, PossessionType.FULL), 0, 50, List.of("shiftDate,desc")));
        asUser(() -> shiftClient.create(new ShiftRequest(LocalDate.of(2026, 10, 5), null, null, null, PossessionType.PARTIAL,
                Instant.parse("2026-10-05T21:30:00Z"), null, null, null, null, null, Set.of(12L), null, null, null, null, null)));
        asUser(() -> shiftClient.update(id, new MergePatch<>(new ShiftUpdateRequest(null, null, null, null, null, null, null, null, null, null, null,
                new java.util.TreeSet<>(Set.of(12L, 13L)), null, null, null, null, null), Set.of("blockingDisconnectorIds"), 5L)));
        ShiftDto started = asUser(() -> shiftClient.start(id, new StartShiftRequest(null, null)));
        ShiftDto closed = asUser(() -> shiftClient.close(id, new CloseShiftRequest(null, null, 240, "Sin incidencias")));
        ShiftDto cancelled = asUser(() -> shiftClient.cancel(id, new ReasonRequest("Lluvia")));
        List<TaskDto> tasks = asUser(() -> shiftClient.tasks(id, MaintenanceTaskStatus.PENDING));
        List<com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto> profiles = asUser(() -> shiftClient.profiles(id, null));
        TaskDto assigned = asUser(() -> shiftClient.assignTask(id, UUID.fromString(TASK_ID)));
        TaskDto unassigned = asUser(() -> shiftClient.unassignTask(id, UUID.fromString(TASK_ID)));

        ShiftDto shift = page.content().getFirst();
        assertEquals(List.of(12L, 13L), shift.trackIds());
        assertEquals("HSA-NS5", shift.blockingDisconnectors().getFirst().name());
        assertEquals(PossessionType.FULL, shift.possessionType());
        assertEquals("EQ-01 - Brigada norte", shift.team().label());
        assertEquals(ShiftStatus.IN_PROGRESS, started.status());
        assertEquals(ShiftStatus.CLOSED, closed.status());
        assertEquals(ShiftStatus.CANCELLED, cancelled.status());
        assertEquals(1, tasks.size());
        assertEquals("12-2.27", profiles.getFirst().name());
        assertEquals(UUID.fromString(TASK_ID), assigned.id());
        assertEquals(MaintenanceTaskStatus.PENDING, unassigned.status());
        server.verify();
    }

    /**
     * La dotacion y los vehiculos de un turno, de mto-resources: se leen con su forma (la movilidad
     * desconocida como UNKNOWN, el aparcamiento null hasta cerrarlo), el alta y el merge-patch mandan sus
     * ids (los linemen quitados a null) y el cierre dice donde quedo cada vehiculo, sin lo que no se dijo.
     */
    @Test
    void aShiftCarriesItsCrewAndVehiclesAndClosingSaysWhereEachIsLeft() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String shifts = MAINTENANCE + "/shifts";
        String leader = "3c4d5e6f-0000-4000-8000-0000000000a1";
        String lineman = "3c4d5e6f-0000-4000-8000-0000000000a2";
        String vehicle = "9d0e1f20-0000-4000-8000-0000000000a1";
        String site = "8c9d0e1f-0000-4000-8000-0000000000a1";
        String withCrew = shiftJson("CLOSED").replace("\"audit\":null}", "\"audit\":null,\"version\":4,"
                + "\"teamLeader\":{\"id\":\"" + leader + "\",\"code\":\"PER-000003\",\"fullName\":\"Yossi Cohen\",\"username\":\"equipo.jefe\","
                + "\"category\":\"TEAM_LEADER\"},\"supervisor\":null,\"linemen\":[{\"id\":\"" + lineman + "\",\"code\":\"PER-000011\","
                + "\"fullName\":\"Lineman Uno\",\"username\":null,\"category\":\"WORKER\"}],\"vehicles\":[{\"vehicleId\":\"" + vehicle + "\","
                + "\"code\":\"DR-01\",\"name\":\"Dresina 1\",\"mobility\":\"RAIL_ONLY\",\"category\":\"CATENARY_TOWER_CAR\",\"available\":true,"
                + "\"parking\":{\"siteId\":\"" + site + "\",\"trackId\":12,\"kp\":4.250,\"description\":null}},{\"vehicleId\":\"" + VEHICLE_ID
                + "\",\"code\":\"HV-01\",\"name\":null,\"mobility\":\"HOVER\",\"category\":\"HOVERCRAFT\",\"available\":false,\"parking\":null}]}");
        server.expect(requestTo(shifts)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"shiftDate\":\"2026-10-05\",\"possessionType\":\"PARTIAL\",\"trackIds\":[12],"
                        + "\"vehicleIds\":[\"" + vehicle + "\"],\"teamLeaderPersonId\":\"" + leader + "\",\"linemanPersonIds\":[\"" + lineman + "\"]}", strict))
                .andRespond(withSuccess(withCrew, MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"vehicleIds\":[\"" + vehicle + "\"],\"linemanPersonIds\":null,\"version\":4}", strict))
                .andRespond(withSuccess(withCrew, MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/close")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"parkings\":[{\"vehicleId\":\"" + vehicle + "\",\"parkingSiteId\":\"" + site + "\",\"trackId\":12,"
                        + "\"kp\":4.250},{\"vehicleId\":\"" + VEHICLE_ID + "\",\"description\":\"Puerta 3\"}]}", strict))
                .andRespond(withSuccess(withCrew, MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(SHIFT_ID);
        ShiftDto created = asUser(() -> shiftClient.create(new ShiftRequest(LocalDate.of(2026, 10, 5), null, null, null, PossessionType.PARTIAL,
                null, null, null, null, null, null, Set.of(12L), null, null, null, null, null, Set.of(UUID.fromString(vehicle)), UUID.fromString(leader),
                null, Set.of(UUID.fromString(lineman)), null)));
        asUser(() -> shiftClient.update(id, new MergePatch<>(new ShiftUpdateRequest(null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, Set.of(UUID.fromString(vehicle)), null, null, null, null), Set.of("linemanPersonIds"), 4L)));
        asUser(() -> shiftClient.close(id, new CloseShiftRequest(null, null, null, null, List.of(
                new VehicleParkingRequest(UUID.fromString(vehicle), UUID.fromString(site), 12L, new BigDecimal("4.250"), null),
                new VehicleParkingRequest(UUID.fromString(VEHICLE_ID), null, null, null, "Puerta 3")))));

        assertEquals("PER-000003 · Yossi Cohen", created.teamLeader().label());
        assertNull(created.supervisor());
        assertEquals(List.of("PER-000011"), created.linemen().stream().map(PersonSummaryDto::code).toList());
        ShiftVehicleDto dresina = created.vehicles().getFirst();
        assertEquals(new ShiftVehicleDto.Parking(UUID.fromString(site), 12L, new BigDecimal("4.250"), null), dresina.parking());
        assertEquals(new VehicleSummaryDto(UUID.fromString(vehicle), "DR-01", "Dresina 1", VehicleMobility.RAIL_ONLY, VehicleCategory.CATENARY_TOWER_CAR),
                dresina.summary());
        ShiftVehicleDto unknown = created.vehicles().get(1);
        assertEquals(VehicleMobility.UNKNOWN, unknown.mobility(), "una movilidad nueva no rompe el turno");
        assertEquals(VehicleCategory.UNKNOWN, unknown.category());
        assertNull(unknown.parking());
        assertEquals("HV-01", unknown.label());
        server.verify();
    }

    /**
     * Generar de verdad va con Prefer: respond-async. Si no termina en su espera, el 202 trae la operacion
     * (su Location es la ruta interna del servicio y no se sigue), que se pregunta por su id y al terminar
     * trae el mismo cuerpo que la llamada sincrona; si termina, el 200 de siempre. Un estado nuevo se lee
     * como UNKNOWN y se da por terminado.
     */
    @Test
    void aLongGenerationAnswersItsOperationWhichIsAskedByItsId() {
        String generate = MAINTENANCE + "/orders/" + ORDER_ID + "/tasks/generate";
        String operation = "2b3c4d5e-0000-4000-8000-0000000000c1";
        String result = "{\"createdTasks\":412,\"skippedProfiles\":0,\"totalTasks\":412,\"estimatedMinutes\":6000.0,\"estimatedShifts\":12,"
                + "\"skippedAssets\":0,\"dryRun\":false}";
        server.expect(requestTo(generate)).andExpect(method(HttpMethod.POST)).andExpect(header("Prefer", TaskGeneration.PREFER))
                .andRespond(withStatus(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.LOCATION, "/api/v1/maintenance/operations/" + operation).header("Preference-Applied", "respond-async")
                        .body("{\"id\":\"" + operation + "\",\"kind\":\"GENERATE_TASKS\",\"scope\":\"EP-3\",\"status\":\"QUEUED\","
                                + "\"requestedBy\":\"mantenimiento.tecnico\",\"createdAt\":\"2026-10-16T21:00:00Z\"}"));
        server.expect(requestTo(MAINTENANCE + "/operations/" + operation)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":\"" + operation + "\",\"kind\":\"GENERATE_TASKS\",\"status\":\"SUCCEEDED\",\"resultStatus\":200,"
                        + "\"result\":" + result + "}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(generate)).andExpect(method(HttpMethod.POST)).andExpect(header("Prefer", TaskGeneration.PREFER))
                .andRespond(withSuccess(result.replace("412", "14"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/operations/" + operation)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":\"" + operation + "\",\"status\":\"PAUSED\",\"error\":{\"status\":500,\"errorCode\":\"OPS-410\","
                        + "\"message\":\"Abandoned\"}}", MediaType.APPLICATION_JSON));

        TaskGeneration generation = new TaskGeneration(orderClient, operationClient, new JsonMapper(), null);
        UUID orderId = UUID.fromString(ORDER_ID);
        TaskGeneration.Started slow = asUser(() -> generation.start(orderId, GenerateTasksRequest.ofSection(null, null, null, null)));
        OperationDto<GenerateTasksResultDto> queued = assertInstanceOf(TaskGeneration.Following.class, slow).operation();
        assertEquals(UUID.fromString(operation), queued.id());
        assertEquals(OperationStatus.QUEUED, queued.status());
        assertFalse(queued.status().isFinished());
        OperationDto<GenerateTasksResultDto> done = asUser(() -> generation.status(UUID.fromString(operation)));
        assertEquals(OperationStatus.SUCCEEDED, done.status());
        assertEquals(412, done.result().createdTasks());
        TaskGeneration.Started quick = asUser(() -> generation.start(orderId, GenerateTasksRequest.ofSection(null, null, null, null)));
        assertEquals(14, assertInstanceOf(TaskGeneration.Done.class, quick).result().createdTasks());
        OperationDto<GenerateTasksResultDto> unknown = asUser(() -> generation.status(UUID.fromString(operation)));
        assertEquals(OperationStatus.UNKNOWN, unknown.status());
        assertTrue(unknown.status().isFinished(), "un estado desconocido no se pregunta sin fin");
        assertEquals("OPS-410", TaskGeneration.failureOf(unknown).getProblem().code());
        assertEquals(500, TaskGeneration.failureOf(unknown).getStatus().value());
        server.verify();
    }

    /** Trabajar una tarea: iniciarla en un turno, contestar su checklist y completarla con defectos y materiales. */
    @Test
    void aTaskIsStartedCheckedAndCompletedWithItsDefectsAndMaterials() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String task = MAINTENANCE + "/orders/" + ORDER_ID + "/tasks/" + TASK_ID;
        server.expect(requestTo(task + "/start")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"shiftId\":\"" + SHIFT_ID + "\"}", strict))
                .andRespond(withSuccess(taskJson("IN_PROGRESS"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(task + "/check-items/2b3c4d5e-0000-4000-8000-000000000051")).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"measuredValue\":5250,\"itemResult\":\"OK\",\"notes\":null,\"version\":1}", strict))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_CONTENT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":422,\"error\":\"UNPROCESSABLE_CONTENT\",\"message\":\"Item P-01 is out of range (5250 mm) and cannot be OK unless adjusted into range\","
                                + "\"errorCode\":\"INS-001\",\"validationErrors\":[]}"));
        server.expect(requestTo(task + "/complete")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"shiftId\":\"" + SHIFT_ID + "\",\"workComplete\":false,\"repairPlannedDate\":\"2026-10-12\","
                        + "\"inlineDefects\":[{\"severity\":\"HIGH\",\"description\":\"Pendola rota\"}],"
                        + "\"materials\":[{\"materialId\":\"" + MAT_ID + "\",\"warehouseId\":\"" + WH_ID + "\",\"quantity\":2,\"unit\":\"ud\"}]}", strict))
                .andRespond(withSuccess(taskJson("COMPLETED"), MediaType.APPLICATION_JSON));

        UUID orderId = UUID.fromString(ORDER_ID);
        UUID taskId = UUID.fromString(TASK_ID);
        TaskDto started = asUser(() -> orderClient.startTask(orderId, taskId, new StartTaskRequest(UUID.fromString(SHIFT_ID), null)));
        ValidationApiException outOfRange = assertThrows(ValidationApiException.class, () -> asUser(() -> orderClient.updateCheckItem(orderId,
                taskId, UUID.fromString("2b3c4d5e-0000-4000-8000-000000000051"), new MergePatch<>(new CheckItemUpdateRequest(new BigDecimal("5250"),
                        null, null, com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemResult.OK, null), Set.of("notes"), 1L))));
        TaskDto completed = asUser(() -> orderClient.completeTask(orderId, taskId, new CompleteTaskRequest(UUID.fromString(SHIFT_ID), null, null,
                null, false, LocalDate.of(2026, 10, 12), List.of(new InlineDefectRequest(DefectSeverity.HIGH, "Pendola rota", null, null, null)),
                List.of(new TaskMaterialRequest(UUID.fromString(MAT_ID), null, UUID.fromString(WH_ID), new BigDecimal("2"), "ud")), null)));

        assertEquals(MaintenanceTaskStatus.IN_PROGRESS, started.status());
        assertEquals("INS-001", outOfRange.getProblem().code());
        assertEquals(MaintenanceTaskStatus.COMPLETED, completed.status());
        server.verify();
    }

    private static final String INSPECTION_ID = "2b3c4d5e-0000-4000-8000-000000000070";
    private static final String DEFECT_ID = "2b3c4d5e-0000-4000-8000-000000000071";

    private static String inspectionJson(String result, String generatedDefect) {
        return "{\"id\":\"" + INSPECTION_ID + "\",\"code\":\"INS-000001\",\"asset\":{\"id\":\"" + ASSET_ID + "\",\"code\":\"PRF-0001\","
                + "\"name\":\"12-2.27\",\"type\":\"PROFILE\",\"trackId\":12,\"startKp\":12.270,\"endKp\":12.270,\"sectioning\":null,\"enabled\":true},"
                + "\"executionPackageId\":3,\"trackId\":12,\"stationId\":null,\"kp\":12.270,\"inspectionDate\":\"2026-09-20\",\"inspector\":\"ana\","
                + "\"inspectionKind\":\"TECHNICAL\",\"templateId\":null,\"result\":\"" + result + "\",\"description\":null,\"detectedDefects\":\"Pendola rota\","
                + "\"recommendedActions\":null,\"generatedDefectId\":" + (generatedDefect == null ? "null" : "\"" + generatedDefect + "\"")
                + ",\"generatedOrderId\":null,\"originOrderId\":\"" + ORDER_ID + "\",\"shiftId\":null,\"items\":[],\"audit\":null}";
    }

    private static String defectJson(String status) {
        return "{\"id\":\"" + DEFECT_ID + "\",\"code\":\"DEF-000001\",\"asset\":{\"id\":\"" + ASSET_ID + "\",\"code\":\"PRF-0001\","
                + "\"name\":\"12-2.27\",\"type\":\"PROFILE\",\"trackId\":12,\"startKp\":12.270,\"endKp\":12.270,\"sectioning\":null,\"enabled\":true},"
                + "\"inspectionId\":\"" + INSPECTION_ID + "\",\"orderId\":null,\"severity\":\"HIGH\",\"status\":\"" + status + "\","
                + "\"description\":\"Pendola rota\",\"technicalNotes\":null,\"detectedAt\":\"2026-09-20T00:00:00Z\",\"resolvedAt\":null,"
                + "\"resolutionNotes\":null,\"discardReason\":null,\"executionPackageId\":3,\"trackId\":12,\"stationId\":null,\"startKp\":12.270,"
                + "\"endKp\":12.270,\"correctionType\":null,\"partsReplaced\":null,\"resolvedInShiftId\":null,\"repairPlannedDate\":null,"
                + "\"foundInTaskId\":null,\"photoRefs\":[],\"audit\":null}";
    }

    /** Inspecciones: busqueda con fechas ISO y orden de origen, alta, modificacion, un punto y lo que generan (idempotente). */
    @Test
    void inspectionsAreSearchedCreatedAndGenerateTheirDefectAndOrder() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String inspections = MAINTENANCE + "/inspections";
        server.expect(requestTo(inspections + "?result=MAJOR_DEFECT&inspectionFrom=2026-09-01&inspectionTo=2026-09-30&originOrderId=" + ORDER_ID
                        + "&page=0&size=50&sort=inspectionDate%2Cdesc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(inspectionJson("MAJOR_DEFECT", null), 0, 50, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(inspections)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"assetId\":\"" + ASSET_ID + "\",\"inspectionDate\":\"2026-09-20\",\"inspectionKind\":\"TECHNICAL\","
                        + "\"result\":\"MAJOR_DEFECT\",\"originOrderId\":\"" + ORDER_ID + "\"}", strict))
                .andRespond(withSuccess(inspectionJson("MAJOR_DEFECT", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(inspections + "/" + INSPECTION_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"result\":\"MINOR_DEFECT\",\"kp\":null,\"version\":6}", strict))
                .andRespond(withSuccess(inspectionJson("MINOR_DEFECT", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(inspections + "/" + INSPECTION_ID + "/items/2b3c4d5e-0000-4000-8000-000000000072")).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"itemResult\":\"DEFECT\",\"notes\":\"Rota\",\"version\":0}", strict))
                .andRespond(withSuccess(inspectionJson("MINOR_DEFECT", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(inspections + "/" + INSPECTION_ID + "/create-defect")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"force\":true}", strict))
                .andRespond(withSuccess(defectJson("OPEN"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(inspections + "/" + INSPECTION_ID + "/create-corrective-order")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"priority\":\"HIGH\"}", strict))
                .andRespond(withSuccess(orderJson(ORDER_ID, "MO-000002", "DRAFT", "HIGH"), MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(INSPECTION_ID);
        PageResponse<InspectionDto> page = asUser(() -> inspectionClient.search(new InspectionFilter(InspectionResult.MAJOR_DEFECT, null, null, null,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), " ", UUID.fromString(ORDER_ID)), 0, 50, List.of("inspectionDate,desc")));
        asUser(() -> inspectionClient.create(new InspectionRequest(UUID.fromString(ASSET_ID), LocalDate.of(2026, 9, 20), null, InspectionKind.TECHNICAL,
                InspectionResult.MAJOR_DEFECT, null, null, null, null, UUID.fromString(ORDER_ID), null)));
        asUser(() -> inspectionClient.update(id, new MergePatch<>(new InspectionUpdateRequest(null, null, null, InspectionResult.MINOR_DEFECT,
                null, null, null, null), Set.of("kp"), 6L)));
        asUser(() -> inspectionClient.updateItem(id, UUID.fromString("2b3c4d5e-0000-4000-8000-000000000072"),
                MergePatch.of(new com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemUpdateRequest(null, null, null,
                        com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemResult.DEFECT, "Rota"), 0L)));
        DefectDto defect = asUser(() -> inspectionClient.createDefect(id, new CreateDefectFromInspectionRequest(null, null, null, true)));
        OrderDto order = asUser(() -> inspectionClient.createCorrectiveOrder(id, new CreateCorrectiveOrderRequest(null, null, MaintenancePriority.HIGH,
                null, null)));

        InspectionDto inspection = page.content().getFirst();
        assertEquals(InspectionKind.TECHNICAL, inspection.inspectionKind());
        assertEquals(UUID.fromString(ORDER_ID), inspection.originOrderId());
        assertTrue(inspection.result().foundSomething());
        assertFalse(InspectionResult.OK.foundSomething());
        assertEquals("DEF-000001", defect.code());
        assertEquals("MO-000002", order.code());
        server.verify();
    }

    /** Defectos: busqueda por instantes, alta, modificacion y sus transiciones con sus cuerpos; vincular es un POST sin cuerpo. */
    @Test
    void defectsAreSearchedCreatedAndMovedThroughTheirStates() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String defects = MAINTENANCE + "/defects";
        server.expect(requestTo(defects + "?severity=HIGH&status=OPEN&trackId=12&detectedFrom=2026-09-01T00%3A00%3A00Z&page=0&size=50"
                        + "&sort=detectedAt%2Cdesc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(defectJson("OPEN"), 0, 50, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"assetId\":\"" + ASSET_ID + "\",\"severity\":\"HIGH\",\"description\":\"Pendola rota\",\"orderId\":\""
                        + ORDER_ID + "\"}", strict))
                .andRespond(withSuccess(defectJson("IN_PROGRESS"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects + "/" + DEFECT_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"repairPlannedDate\":\"2026-10-12\",\"technicalNotes\":null,\"version\":7}", strict))
                .andRespond(withSuccess(defectJson("OPEN"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects + "/" + DEFECT_ID + "/link-order/" + ORDER_ID)).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(defectJson("IN_PROGRESS"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects + "/" + DEFECT_ID + "/resolve")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"resolutionNotes\":\"Pendola cambiada\",\"resolvedInShiftId\":\"" + SHIFT_ID + "\"}", strict))
                .andRespond(withSuccess(defectJson("RESOLVED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects + "/" + DEFECT_ID + "/close")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"reason\":\"Verificado\"}", strict))
                .andRespond(withSuccess(defectJson("CLOSED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects + "/" + DEFECT_ID + "/discard")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"reason\":\"Duplicado\"}", strict))
                .andRespond(withSuccess(defectJson("DISCARDED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(defects + "/" + DEFECT_ID + "/history")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"2b3c4d5e-0000-4000-8000-000000000073\",\"previousStatus\":null,\"newStatus\":\"OPEN\","
                        + "\"changedAt\":\"2026-09-20T08:00:00Z\",\"changedBy\":\"ana\",\"comment\":\"Created from inspection INS-000001\"}]",
                        MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(DEFECT_ID);
        PageResponse<DefectDto> page = asUser(() -> defectClient.search(new DefectFilter(com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity.HIGH,
                DefectStatus.OPEN, null, null, 12L, null, Instant.parse("2026-09-01T00:00:00Z"), null), 0, 50, List.of("detectedAt,desc")));
        asUser(() -> defectClient.create(new DefectRequest(UUID.fromString(ASSET_ID), com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity.HIGH,
                "Pendola rota", null, null, null, UUID.fromString(ORDER_ID), null, null, null, null, null, null)));
        asUser(() -> defectClient.update(id, new MergePatch<>(new DefectUpdateRequest(null, null, null, null, null, LocalDate.of(2026, 10, 12), null),
                Set.of("technicalNotes"), 7L)));
        DefectDto linked = asUser(() -> defectClient.linkOrder(id, UUID.fromString(ORDER_ID)));
        DefectDto resolved = asUser(() -> defectClient.resolve(id, new ResolveDefectRequest("Pendola cambiada", UUID.fromString(SHIFT_ID), null, null)));
        DefectDto closed = asUser(() -> defectClient.close(id, new ReasonRequest("Verificado")));
        DefectDto discarded = asUser(() -> defectClient.discard(id, new ReasonRequest("Duplicado")));
        List<StatusHistoryDto> history = asUser(() -> defectClient.history(id));

        assertEquals(UUID.fromString(INSPECTION_ID), page.content().getFirst().inspectionId());
        assertEquals(DefectStatus.IN_PROGRESS, linked.status());
        assertEquals(DefectStatus.RESOLVED, resolved.status());
        assertEquals(DefectStatus.CLOSED, closed.status());
        assertEquals(DefectStatus.DISCARDED, discarded.status());
        assertEquals("OPEN", history.getFirst().newStatus());
        assertTrue(DefectStatus.OPEN.isPending() && !DefectStatus.CLOSED.isEditable());
        server.verify();
    }

    private static final String LINE_ID = "2b3c4d5e-0000-4000-8000-000000000080";

    private static String lineJson(String status, String error) {
        return "{\"id\":\"" + LINE_ID + "\",\"orderId\":\"" + ORDER_ID + "\",\"taskId\":null,\"materialId\":\"" + MAT_ID + "\","
                + "\"materialCode\":\"MAT-001\",\"materialDescriptionSnapshot\":\"Hilo de contacto\",\"warehouseId\":\"" + WH_ID + "\","
                + "\"plannedQuantity\":4.000000,\"consumedQuantity\":null,\"unit\":\"m\",\"allowOverConsumption\":false,"
                + "\"stockReservationId\":" + ("RESERVED".equals(status) ? "\"" + RES_ID + "\"" : "null") + ",\"stockSyncStatus\":\"" + status + "\","
                + "\"stockSyncError\":" + (error == null ? "null" : "\"" + error + "\"") + ",\"audit\":null}";
    }

    /** La misma linea, con la peticion al almacen que se quedo sin respuesta. */
    private static String lineJson(String status, String error, String inDoubt) {
        String line = lineJson(status, error);
        return line.substring(0, line.length() - 1) + ",\"stockRequestInDoubt\":" + (inDoubt == null ? "null" : "\"" + inDoubt + "\"") + "}";
    }

    /**
     * Una linea dice que peticion mando al almacen y se quedo sin respuesta: con una reserva se puede
     * quitar (el servicio la confirma para liberarla); con una salida, o con una peticion que este
     * cliente no conoce, no. Sin peticion en duda, todo sigue como antes.
     */
    @Test
    void aLineSaysWhichRequestToStockIsInDoubtAndAnUnknownOneOpensNothing() {
        String materials = MAINTENANCE + "/orders/" + ORDER_ID + "/materials";
        server.expect(requestTo(materials)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + lineJson("FAILED", "reserve: Read timed out", "RESERVATION") + ","
                        + lineJson("FAILED", "consume: Read timed out", "OUTPUT") + "," + lineJson("FAILED", "reserve: stock down", "CANCELLATION") + ","
                        + lineJson("RESERVED", null) + "]", MediaType.APPLICATION_JSON));

        List<MaterialUsageDto> lines = asUser(() -> orderClient.materials(UUID.fromString(ORDER_ID)));

        assertEquals(Arrays.asList(StockRequestType.RESERVATION, StockRequestType.OUTPUT, StockRequestType.UNKNOWN, null),
                lines.stream().map(MaterialUsageDto::stockRequestInDoubt).toList());
        assertEquals(List.of(true, true, true, false), lines.stream().map(MaterialUsageDto::isInDoubt).toList());
        assertEquals(List.of(true, false, false, true), lines.stream().map(MaterialUsageDto::isRemovable).toList());
        assertEquals(List.of(StockRequestType.RESERVATION, StockRequestType.OUTPUT), StockRequestType.selectable());
        server.verify();
    }

    /**
     * Lineas de material: la lista, el alta sin lo vacio, la modificacion con solo lo cambiado,
     * sincronizar y quitar sin cuerpo (204), y el 503 STK-503 de un almacen caido al quitarla.
     */
    @Test
    void materialLinesAreRegisteredUpdatedSyncedAndRemoved() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String materials = MAINTENANCE + "/orders/" + ORDER_ID + "/materials";
        server.expect(requestTo(materials)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + lineJson("FAILED", "Stock service unavailable") + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(materials)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"materialId\":\"" + MAT_ID + "\",\"warehouseId\":\"" + WH_ID + "\",\"plannedQuantity\":4,"
                        + "\"unit\":\"m\"}", strict))
                .andRespond(withSuccess(lineJson("NOT_REQUESTED", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(materials + "/" + LINE_ID)).andExpect(method(HttpMethod.PATCH)).andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"consumedQuantity\":3,\"version\":2}", strict))
                .andRespond(withSuccess(lineJson("RESERVED", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(materials + "/" + LINE_ID + "/sync")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(lineJson("RESERVED", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(materials + "/" + LINE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(materials + "/" + LINE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":503,\"error\":\"SERVICE_UNAVAILABLE\",\"message\":\"Stock service unavailable\",\"errorCode\":\"STK-503\","
                                + "\"validationErrors\":[]}"));

        UUID orderId = UUID.fromString(ORDER_ID);
        UUID lineId = UUID.fromString(LINE_ID);
        List<MaterialUsageDto> lines = asUser(() -> orderClient.materials(orderId));
        asUser(() -> orderClient.registerMaterial(orderId, new MaterialUsageRequest(UUID.fromString(MAT_ID), null, UUID.fromString(WH_ID),
                new BigDecimal("4"), "m", null, null)));
        MaterialUsageDto updated = asUser(() -> orderClient.updateMaterial(orderId, lineId,
                MergePatch.of(new MaterialUsageUpdateRequest(null, new BigDecimal("3"), null), 2L)));
        MaterialUsageDto synced = asUser(() -> orderClient.syncMaterial(orderId, lineId));
        asUser(() -> {
            orderClient.removeMaterial(orderId, lineId);
            return null;
        });
        ServiceUnavailableApiException down = assertThrows(ServiceUnavailableApiException.class, () -> asUser(() -> {
            orderClient.removeMaterial(orderId, lineId);
            return null;
        }));

        MaterialUsageDto line = lines.getFirst();
        assertEquals(StockSyncStatus.FAILED, line.stockSyncStatus());
        assertEquals("Stock service unavailable", line.stockSyncError());
        assertEquals("MAT-001 - Hilo de contacto", line.materialLabel());
        assertFalse(line.isReserved());
        assertTrue(updated.isReserved());
        assertEquals(StockSyncStatus.RESERVED, synced.stockSyncStatus());
        assertEquals("STK-503", down.getProblem().code());
        assertEquals(503, down.getStatus().value());
        server.verify();
    }

    /**
     * Una linea que el almacen rechazo llega {@code REJECTED} con el motivo de stock, y sincronizarla
     * otra vez responde con su codigo: 422 {@code STK-422} si dice que no por otro motivo, 409
     * {@code STK-001} si faltan existencias. Fallida y rechazada son las dos lo que stock no hizo.
     */
    @Test
    void aRejectedLineCarriesStocksReasonAndSyncingItAgainAnswersWithItsCode() {
        String materials = MAINTENANCE + "/orders/" + ORDER_ID + "/materials";
        String reason = "mto-stock rejected 'reserve' with 422 WH-001: Warehouse WH-001 is inactive";
        server.expect(requestTo(materials)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + lineJson("REJECTED", reason) + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(materials + "/" + LINE_ID + "/sync")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":422,\"error\":\"UNPROCESSABLE_ENTITY\",\"message\":\"" + reason + "\",\"errorCode\":\"STK-422\","
                                + "\"validationErrors\":[]}"));
        server.expect(requestTo(materials + "/" + LINE_ID + "/sync")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"mto-stock rejected 'reserve' with 409 STK-001: Insufficient stock\","
                                + "\"errorCode\":\"STK-001\",\"validationErrors\":[]}"));

        UUID orderId = UUID.fromString(ORDER_ID);
        UUID lineId = UUID.fromString(LINE_ID);
        MaterialUsageDto line = asUser(() -> orderClient.materials(orderId)).getFirst();
        ValidationApiException rejected = assertThrows(ValidationApiException.class, () -> asUser(() -> orderClient.syncMaterial(orderId, lineId)));
        ConflictApiException noStock = assertThrows(ConflictApiException.class, () -> asUser(() -> orderClient.syncMaterial(orderId, lineId)));

        assertEquals(StockSyncStatus.REJECTED, line.stockSyncStatus());
        assertEquals(reason, line.stockSyncError());
        assertTrue(StockSyncStatus.REJECTED.isSyncFailed());
        assertTrue(StockSyncStatus.FAILED.isSyncFailed());
        assertFalse(StockSyncStatus.RESERVED.isSyncFailed());
        assertEquals("STK-422", rejected.getProblem().code());
        assertFalse(rejected.getProblem().hasFieldErrors());
        assertEquals(reason, rejected.getProblem().detail());
        assertEquals("STK-001", noStock.getProblem().code());
        server.verify();
    }

    /**
     * Informes: el JSON y el fichero de cada uno por la misma ruta, que se distinguen por
     * {@code format}. Los instantes del avance van en ISO, el mes como {@code 2026-09} y el fichero
     * vuelve con su nombre y su tipo. El avance es una fraccion (0.4500) y se lee tal cual; un tipo
     * de activo que esta version no conoce no rompe el informe.
     */
    @Test
    void reportsComeAsJsonOrAsAFileWithItsNameThroughTheSameRoute() {
        String progress = MAINTENANCE + "/reports/progress?executionPackageId=3&trackId=12&assetType=PROFILE"
                + "&from=2026-09-01T00%3A00%3A00Z&to=2026-09-30T23%3A59%3A59.999Z";
        server.expect(requestTo(progress)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"from\":\"2026-09-01T00:00:00Z\",\"to\":\"2026-09-30T23:59:59.999Z\",\"totalAssets\":40,"
                        + "\"checkedAssets\":18,\"completionRatio\":0.4500,\"coveredKm\":5.400,\"totalKm\":12.000,\"rows\":["
                        + "{\"executionPackageId\":3,\"trackId\":12,\"assetType\":\"PROFILE\",\"totalAssets\":40,\"checkedAssets\":18,"
                        + "\"completionRatio\":0.4500,\"coveredKm\":5.400,\"totalKm\":12.000},"
                        + "{\"executionPackageId\":3,\"trackId\":12,\"assetType\":\"CROSSOVER\",\"totalAssets\":0,\"checkedAssets\":0,"
                        + "\"completionRatio\":0.0000,\"coveredKm\":0,\"totalKm\":0}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(progress + "&format=xlsx")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("PK", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"progress-report-2026-09-30.xlsx\""));
        server.expect(requestTo(MAINTENANCE + "/reports/monthly?month=2026-09")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"month\":\"2026-09\",\"executionPackageId\":null,\"shiftsPlanned\":8,\"shiftsClosed\":6,"
                        + "\"shiftsCancelled\":1,\"netWorkMinutes\":1440,\"averageNetMinutesPerShift\":240.00,\"ordersCompleted\":3,"
                        + "\"tasksCompleted\":45,\"profilesChecked\":44,\"coveredKm\":2.900,\"defectsDetected\":5,\"defectsResolved\":3,"
                        + "\"correctiveOrdersCreated\":2,\"materials\":[{\"materialId\":\"" + MAT_ID + "\",\"materialCode\":\"MAT-001\","
                        + "\"unit\":\"m\",\"consumedQuantity\":12.500000}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/reports/monthly?month=2026-09&executionPackageId=3&format=pdf")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("%PDF-1.7", MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"monthly-report-2026-09.pdf\""));
        server.expect(requestTo(MAINTENANCE + "/shifts/" + SHIFT_ID + "/report")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"shift\":" + shiftJson("CLOSED") + ",\"tasksCompleted\":2,\"tasksPending\":1,"
                        + "\"profilesReviewed\":2,\"defectsFound\":1,\"defectsResolved\":0,\"rows\":[{\"number\":1,\"taskId\":\"" + TASK_ID + "\","
                        + "\"orderCode\":\"MO-000001\",\"executionPackageId\":3,\"trackId\":12,\"profileCode\":\"PRF-0001\","
                        + "\"profileName\":\"12-2.27\",\"kp\":12.270,\"sectioning\":\"S-3\",\"switches\":[],\"taskTypeCodes\":[\"RG-01\",\"RG-04\"],"
                        + "\"worksPerformed\":\"Revision general\",\"defectsFound\":\"DEF-000001\",\"materials\":[\"MAT-001 2 m\"],"
                        + "\"startedAt\":\"2026-10-05T22:10:00Z\",\"completedAt\":\"2026-10-05T23:00:00Z\",\"status\":\"COMPLETED\","
                        + "\"workComplete\":true,\"repairPlannedDate\":null,\"photoRefs\":null}]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/shifts/" + SHIFT_ID + "/report?format=xlsx")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("PK", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"shift-report-SH-000001-2026-10-05.xlsx\""));

        Instant from = Instant.parse("2026-09-01T00:00:00Z");
        Instant to = Instant.parse("2026-09-30T23:59:59.999Z");
        ProgressReportDto report = asUser(() -> reportClient.progress(3L, 12L, CatenaryAssetType.PROFILE, from, to));
        ResponseEntity<byte[]> progressFile = asUser(() -> reportClient.progressFile(3L, 12L, CatenaryAssetType.PROFILE, from, to,
                ReportFormat.XLSX.parameter()));
        MonthlyReportDto monthly = asUser(() -> reportClient.monthly(YearMonth.of(2026, 9), null));
        ResponseEntity<byte[]> monthlyFile = asUser(() -> reportClient.monthlyFile(YearMonth.of(2026, 9), 3L, ReportFormat.PDF.parameter()));
        ShiftReportDto shiftReport = asUser(() -> reportClient.shiftReport(UUID.fromString(SHIFT_ID)));
        ResponseEntity<byte[]> shiftFile = asUser(() -> reportClient.shiftReportFile(UUID.fromString(SHIFT_ID), ReportFormat.XLSX.parameter()));

        assertEquals(18, report.checkedAssets());
        assertEquals(new BigDecimal("0.4500"), report.completionRatio());
        assertEquals(from, report.from());
        assertEquals(CatenaryAssetType.PROFILE, report.rows().getFirst().assetType());
        assertEquals(CatenaryAssetType.UNKNOWN, report.rows().get(1).assetType());
        assertEquals("progress-report-2026-09-30.xlsx", progressFile.getHeaders().getContentDisposition().getFilename());
        assertEquals("PK", new String(progressFile.getBody(), StandardCharsets.UTF_8));
        assertEquals(YearMonth.of(2026, 9), monthly.month());
        assertEquals(new BigDecimal("240.00"), monthly.averageNetMinutesPerShift());
        assertEquals("MAT-001", monthly.materials().getFirst().materialCode());
        assertEquals(MediaType.APPLICATION_PDF, monthlyFile.getHeaders().getContentType());
        assertEquals("monthly-report-2026-09.pdf", monthlyFile.getHeaders().getContentDisposition().getFilename());
        assertEquals("SH-000001", shiftReport.shift().code());
        assertEquals(List.of("RG-01", "RG-04"), shiftReport.rows().getFirst().taskTypeCodes());
        assertEquals(List.of(), shiftReport.rows().getFirst().photoRefs(), "una lista a null se lee vacia");
        assertEquals(MaintenanceTaskStatus.COMPLETED, shiftReport.rows().getFirst().status());
        assertEquals("shift-report-SH-000001-2026-10-05.xlsx", shiftFile.getHeaders().getContentDisposition().getFilename());
        server.verify();
    }

    private static String maintenanceRevision(long number, String operation, String source, String entity) {
        return "{\"revision\":{\"revision\":" + number + ",\"revisionAt\":\"2026-10-06T05:00:00Z\",\"operation\":\"" + operation + "\","
                + "\"author\":\"mantenimiento.tecnico\",\"source\":\"" + source + "\",\"correlationId\":\"c-" + number + "\"},\"entity\":" + entity + "}";
    }

    /**
     * El historial de cada recurso de mantenimiento, con la foto tipada (el generico se resuelve en
     * cada interfaz) y la mas reciente primero tal como llega. Un activo que solo ha llegado por datos
     * maestros no tiene ninguna revision, y el servicio responde 404.
     */
    @Test
    void maintenanceRevisionsComeTypedForEachResourceAndNoneIsANotFound() {
        server.expect(requestTo(MAINTENANCE + "/orders/" + ORDER_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(maintenanceRevision(7, "UPDATED", "HTTP", orderJson(ORDER_ID, "MO-000001", "IN_PROGRESS", "HIGH"))
                        + "," + maintenanceRevision(2, "CREATED", "HTTP", orderJson(ORDER_ID, "MO-000001", "DRAFT", "MEDIUM")), 0, 20, 2),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/shifts/" + SHIFT_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(maintenanceRevision(9, "UPDATED", "HTTP", shiftJson("CLOSED")), 0, 20, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/inspections/" + INSPECTION_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(maintenanceRevision(4, "UPDATED", "HTTP", inspectionJson("MINOR_DEFECT", DEFECT_ID)), 0, 20, 1),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/defects/" + DEFECT_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(maintenanceRevision(5, "UPDATED", "SYSTEM", defectJson("RESOLVED")), 0, 20, 1),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":404,\"error\":\"NOT_FOUND\",\"message\":\"CatenaryAsset not found: " + ASSET_ID + "\","
                                + "\"errorCode\":\"APP-404\",\"validationErrors\":[]}"));

        PageResponse<RevisionDto<OrderDto>> orders = asUser(() -> orderClient.revisions(UUID.fromString(ORDER_ID), 0, 20));
        PageResponse<RevisionDto<ShiftDto>> shifts = asUser(() -> shiftClient.revisions(UUID.fromString(SHIFT_ID), 0, 20));
        PageResponse<RevisionDto<InspectionDto>> inspections = asUser(() -> inspectionClient.revisions(UUID.fromString(INSPECTION_ID), 0, 20));
        PageResponse<RevisionDto<DefectDto>> defects = asUser(() -> defectClient.revisions(UUID.fromString(DEFECT_ID), 0, 20));
        NotFoundApiException none = assertThrows(NotFoundApiException.class,
                () -> asUser(() -> assetClient.revisions(UUID.fromString(ASSET_ID), 0, 20)));

        assertEquals(2, orders.page().totalElements());
        RevisionDto<OrderDto> newest = orders.content().getFirst();
        assertEquals(7, newest.revision().revision());
        assertEquals(RevisionOperation.UPDATED, newest.revision().operation());
        assertEquals("c-7", newest.revision().correlationId());
        assertEquals(MaintenanceOrderStatus.IN_PROGRESS, newest.entity().status());
        assertEquals(MaintenanceOrderStatus.DRAFT, orders.content().get(1).entity().status());
        assertEquals(RevisionOperation.CREATED, orders.content().get(1).revision().operation());
        assertEquals(ShiftStatus.CLOSED, shifts.content().getFirst().entity().status());
        assertEquals(InspectionResult.MINOR_DEFECT, inspections.content().getFirst().entity().result());
        assertEquals("SYSTEM", defects.content().getFirst().revision().source());
        assertEquals(DefectStatus.RESOLVED, defects.content().getFirst().entity().status());
        assertEquals("APP-404", none.getProblem().code());
        server.verify();
    }

    // --- Notificaciones (mto-notification) ------------------------------------------------------

    private static final String NOTIFICATIONS = GATEWAY + "/api/notifications";
    private static final String NOTIFICATION_ID = "5e6f7a8b-0000-4000-8000-000000000501";
    private static final String NOTIFICATION_ID_2 = "5e6f7a8b-0000-4000-8000-000000000502";
    private static final String EVENT_ID = "5e6f7a8b-0000-4000-8000-000000000511";

    /** Una notificacion como la devuelve mto-notification; {@code severity} va tal cual, para probar las desconocidas. */
    private static String inboxItemJson(String id, boolean read, String severity) {
        return "{\"id\":\"" + id + "\",\"ruleKey\":\"maintenance-order-urgent\",\"category\":\"MAINTENANCE\",\"severity\":\"" + severity
                + "\",\"title\":\"Orden urgente MO-000012\",\"body\":\"Creada por mantenimiento.tecnico\","
                + "\"link\":\"/mantenimiento/ordenes/" + ORDER_ID + "\",\"subjectType\":\"order\",\"subjectId\":\"" + ORDER_ID + "\","
                + "\"activityEventId\":\"" + EVENT_ID + "\",\"createdAt\":\"2026-09-28T06:07:00Z\",\"read\":" + read
                + ",\"readAt\":" + (read ? "\"2026-09-28T07:00:00Z\"" : "null") + ",\"unknownTomorrow\":1}";
    }

    private static String activityEventJson(String id, String category, String actorKind, String supersededBy) {
        return "{\"id\":\"" + id + "\",\"seq\":118,\"sourceService\":\"mto-users\",\"sourceEventId\":\"f0000000-0000-4000-8000-000000000001\","
                + "\"category\":\"" + category + "\",\"type\":\"users.user.created\",\"severity\":\"INFO\","
                + "\"occurredAt\":\"2026-09-28T06:07:00Z\",\"recordedAt\":\"2026-09-28T06:07:01Z\","
                + "\"actor\":{\"kind\":\"" + actorKind + "\",\"username\":\"usuarios.responsable\",\"id\":\"a0000000-0000-4000-8000-000000000042\"},"
                + "\"subject\":{\"type\":\"user\",\"id\":\"u-1\",\"label\":\"nueva.persona\"},\"correlationId\":\"corr-n7\",\"eventCount\":1,"
                + "\"payload\":{\"targetUsername\":\"nueva.persona\",\"temporaryCredential\":true,\"actions\":[\"VERIFY_EMAIL\"]},"
                + "\"supersededBy\":" + (supersededBy == null ? "null" : "\"" + supersededBy + "\"") + ",\"unknownTomorrow\":1}";
    }

    @Test
    void theInboxIsReadWithItsFiltersAndAnUnknownSeverityIsReadAsUnknown() {
        server.expect(requestTo(NOTIFICATIONS + "/inbox?unread=true&category=MAINTENANCE&from=2026-09-01T00%3A00%3A00Z&page=0&size=20"
                        + "&sort=createdAt%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-for-" + PRINCIPAL))
                .andExpect(header("X-Correlation-Id", matchesRegex(UUID_PATTERN)))
                .andRespond(withSuccess(stockPage(inboxItemJson(NOTIFICATION_ID, false, "CRITICAL") + "," + inboxItemJson(NOTIFICATION_ID_2, true, "FATAL"),
                        0, 20, 2), MediaType.APPLICATION_JSON));
        // Sin filtros no viaja ninguno: para el servicio un filtro ausente no filtra.
        server.expect(requestTo(NOTIFICATIONS + "/inbox?page=1&size=20&sort=severity%2Casc&sort=createdAt%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("", 1, 20, 0), MediaType.APPLICATION_JSON));

        PageResponse<InboxItemDto> page = asUser(() -> notificationClient.inbox(new InboxFilter(true, ActivityCategory.MAINTENANCE, null,
                Instant.parse("2026-09-01T00:00:00Z"), null), 0, 20, List.of("createdAt,desc")));
        PageResponse<InboxItemDto> empty = asUser(() -> notificationClient.inbox(InboxFilter.NONE, 1, 20, List.of("severity,asc")));

        assertEquals(2, page.page().totalElements());
        InboxItemDto unread = page.content().getFirst();
        assertEquals(UUID.fromString(NOTIFICATION_ID), unread.id());
        assertEquals(ActivityCategory.MAINTENANCE, unread.category());
        assertEquals(ActivitySeverity.CRITICAL, unread.severity());
        assertEquals("/mantenimiento/ordenes/" + ORDER_ID, unread.link());
        assertTrue(unread.hasLink());
        assertEquals(UUID.fromString(EVENT_ID), unread.activityEventId());
        assertFalse(unread.read());
        assertNull(unread.readAt());
        InboxItemDto read = page.content().get(1);
        assertTrue(read.read());
        assertEquals(Instant.parse("2026-09-28T07:00:00Z"), read.readAt());
        assertEquals(ActivitySeverity.UNKNOWN, read.severity(), "una gravedad nueva en el servicio no rompe la bandeja");
        assertTrue(empty.content().isEmpty());
        server.verify();
    }

    @Test
    void theUnreadCountIsCappedAndTheMarksArePostsWithoutBody() {
        server.expect(requestTo(NOTIFICATIONS + "/inbox/unread-count")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"count\":100,\"capped\":true}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(NOTIFICATIONS + "/inbox/" + NOTIFICATION_ID + "/read")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(inboxItemJson(NOTIFICATION_ID, true, "CRITICAL"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(NOTIFICATIONS + "/inbox/read-all")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess("{\"allReadUntil\":\"2026-09-28T06:07:00Z\"}", MediaType.APPLICATION_JSON));
        // El JSON de error de mto-notification es el de mto-maintenance: errorCode y message, por alias.
        server.expect(requestTo(NOTIFICATIONS + "/inbox/" + NOTIFICATION_ID_2 + "/read")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).header("X-Correlation-Id", "corr-n1")
                        .body("{\"timestamp\":\"2026-09-28T06:07:00Z\",\"status\":404,\"error\":\"NOT_FOUND\","
                                + "\"message\":\"Notification " + NOTIFICATION_ID_2 + " is not addressed to config.responsable\","
                                + "\"path\":\"/api/v1/notifications/inbox/" + NOTIFICATION_ID_2 + "/read\",\"method\":\"POST\","
                                + "\"errorCode\":\"NTF-404\",\"correlationId\":\"corr-n1\",\"validationErrors\":[]}"));

        UnreadCountDto count = asUser(() -> notificationClient.unreadCount());
        InboxItemDto marked = asUser(() -> notificationClient.markRead(UUID.fromString(NOTIFICATION_ID)));
        ReadAllDto all = asUser(() -> notificationClient.markAllRead());
        NotFoundApiException notMine = assertThrows(NotFoundApiException.class,
                () -> asUser(() -> notificationClient.markRead(UUID.fromString(NOTIFICATION_ID_2))));

        assertEquals(100, count.count());
        assertTrue(count.capped(), "cien o mas, no un total");
        assertTrue(marked.read());
        assertEquals(Instant.parse("2026-09-28T06:07:00Z"), all.allReadUntil());
        assertEquals("NTF-404", notMine.getProblem().code());
        assertTrue(notMine.getProblem().detail().contains("is not addressed to"), notMine.getProblem().detail());
        assertEquals("corr-n1", notMine.getReference());
        server.verify();
    }

    @Test
    void theActivityLogIsSearchedWithEveryFilterAndTheDetailBringsThePayload() {
        server.expect(requestTo(NOTIFICATIONS + "/activity?category=USERS&type=users.user.created&actorUsername=usuarios.responsable"
                        + "&subjectType=user&subjectId=u-1&severity=INFO&sourceService=mto-users&from=2026-09-01T00%3A00%3A00Z"
                        + "&to=2026-09-30T23%3A59%3A59.999Z&includeSuperseded=true&page=0&size=50&sort=occurredAt%2Cdesc&sort=seq%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(activityEventJson(EVENT_ID, "USERS", "PERSON", null) + ","
                        + activityEventJson(NOTIFICATION_ID_2, "TELEMETRY", "ROBOT", EVENT_ID), 0, 50, 2), MediaType.APPLICATION_JSON));
        // includeSuperseded solo viaja cuando es verdadero: el servicio ya esconde lo fundido por defecto.
        server.expect(requestTo(NOTIFICATIONS + "/activity?page=0&size=50&sort=occurredAt%2Cdesc&sort=seq%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("", 0, 50, 0), MediaType.APPLICATION_JSON));
        server.expect(requestTo(NOTIFICATIONS + "/activity/" + EVENT_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(activityEventJson(EVENT_ID, "USERS", "SERVICE", null), MediaType.APPLICATION_JSON));

        PageResponse<ActivityEventDto> page = asUser(() -> notificationClient.activity(new ActivityFilter(ActivityCategory.USERS,
                "users.user.created", "usuarios.responsable", "user", "u-1", ActivitySeverity.INFO, "mto-users",
                Instant.parse("2026-09-01T00:00:00Z"), Instant.parse("2026-09-30T23:59:59.999Z"), true), 0, 50, List.of("occurredAt,desc")));
        PageResponse<ActivityEventDto> visible = asUser(() -> notificationClient.activity(ActivityFilter.NONE, 0, 50,
                List.of("occurredAt,desc", "seq,desc")));
        ActivityEventDto detail = asUser(() -> notificationClient.activityEvent(UUID.fromString(EVENT_ID)));

        ActivityEventDto created = page.content().getFirst();
        assertEquals(118L, created.seq());
        assertEquals(ActivityCategory.USERS, created.category());
        assertEquals("users.user.created", created.type());
        assertEquals(ActorKind.PERSON, created.actor().kind());
        assertEquals("usuarios.responsable", created.actor().describe());
        assertEquals("user nueva.persona", created.subject().describe());
        assertEquals("corr-n7", created.correlationId());
        assertEquals(true, created.payload().get("temporaryCredential"));
        assertEquals(List.of("VERIFY_EMAIL"), created.payload().get("actions"));
        assertNull(created.supersededBy());
        ActivityEventDto superseded = page.content().get(1);
        assertEquals(ActivityCategory.UNKNOWN, superseded.category(), "una categoria nueva no rompe el registro");
        assertEquals(ActorKind.UNKNOWN, superseded.actor().kind());
        assertEquals(UUID.fromString(EVENT_ID), superseded.supersededBy());
        assertTrue(visible.content().isEmpty());
        assertEquals("usuarios.responsable (Servicio)", detail.actor().describe() + " (" + detail.actor().kind().label() + ")");
        assertEquals("nueva.persona", detail.payload().get("targetUsername"));
        server.verify();
    }

    @Test
    void theAccessesAreSearchedByUserIpTypeAndOutcome() {
        server.expect(requestTo(NOTIFICATIONS + "/access?username=config.lector&ipAddress=10.0.0.7&type=access.login.failed&outcome=FAILURE"
                        + "&page=0&size=50&sort=occurredAt%2Cdesc&sort=seq%2Cdesc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(
                        "{\"id\":\"" + EVENT_ID + "\",\"seq\":7,\"type\":\"access.login.failed\",\"severity\":\"WARNING\",\"outcome\":\"FAILURE\","
                                + "\"occurredAt\":\"2026-09-28T06:07:00Z\",\"recordedAt\":\"2026-09-28T06:07:20Z\",\"username\":\"config.lector\","
                                + "\"userId\":\"a0000000-0000-4000-8000-000000000041\",\"ipAddress\":\"10.0.0.7\",\"correlationId\":null,\"eventCount\":1,"
                                + "\"payload\":{\"error\":\"invalid_user_credentials\",\"username\":\"config.lector\"}},"
                                + "{\"id\":\"" + NOTIFICATION_ID_2 + "\",\"seq\":8,\"type\":\"access.login.streak\",\"severity\":\"CRITICAL\","
                                + "\"outcome\":\"BLOCKED\",\"occurredAt\":\"2026-09-28T06:08:00Z\",\"recordedAt\":\"2026-09-28T06:08:20Z\","
                                + "\"username\":\"config.lector\",\"userId\":null,\"ipAddress\":\"10.0.0.7\",\"correlationId\":null,\"eventCount\":3,"
                                + "\"payload\":{\"dimension\":\"username\",\"value\":\"config.lector\",\"count\":3}}", 0, 50, 2),
                        MediaType.APPLICATION_JSON));

        PageResponse<AccessEventDto> page = asUser(() -> notificationClient.access(new AccessFilter("config.lector", "10.0.0.7",
                "access.login.failed", AccessOutcome.FAILURE, null, null), 0, 50, List.of("occurredAt,desc")));

        AccessEventDto failed = page.content().getFirst();
        assertEquals(AccessOutcome.FAILURE, failed.outcome());
        assertEquals("10.0.0.7", failed.ipAddress());
        assertEquals("config.lector", failed.username());
        assertEquals("invalid_user_credentials", failed.payload().get("error"));
        AccessEventDto streak = page.content().get(1);
        assertEquals(AccessOutcome.UNKNOWN, streak.outcome(), "un resultado nuevo se lee como desconocido");
        assertEquals(3, streak.eventCount());
        assertEquals(3, streak.payload().get("count"));
        server.verify();
    }

    /** Un {@code sort} que el servicio no admite es 400 {@code REQ-400} sin errores por campo: una regla, no un formulario. */
    @Test
    void anUnknownSortIsA400OfTheNotificationServiceReadByAlias() {
        server.expect(requestTo(NOTIFICATIONS + "/activity?page=0&size=50&sort=payload%2Casc&sort=seq%2Cdesc")).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).header("X-Correlation-Id", "corr-n2")
                        .body("{\"timestamp\":\"2026-09-28T06:07:00Z\",\"status\":400,\"error\":\"BAD_REQUEST\","
                                + "\"message\":\"Unknown sort property 'payload'\",\"path\":\"/api/v1/notifications/activity\",\"method\":\"GET\","
                                + "\"errorCode\":\"REQ-400\",\"correlationId\":\"corr-n2\",\"validationErrors\":[]}"));

        ValidationApiException invalid = assertThrows(ValidationApiException.class,
                () -> asUser(() -> notificationClient.activity(ActivityFilter.NONE, 0, 50, List.of("payload,asc"))));

        assertEquals("REQ-400", invalid.getProblem().code());
        assertEquals("Unknown sort property 'payload'", invalid.getProblem().detail());
        assertFalse(invalid.getProblem().hasFieldErrors());
        assertEquals("corr-n2", invalid.getReference());
        server.verify();
    }

    // --- Recursos (mto-resources) -----------------------------------------------------------------

    private static final String RESOURCES = GATEWAY + "/api/resources";
    private static final String PERSON_ID = "3c4d5e6f-0000-4000-8000-000000000001";

    private static String personJson(String id, String code, String category, boolean active) {
        return "{\"id\":\"" + id + "\",\"code\":\"" + code + "\",\"employeeNumber\":\"E-1042\",\"firstName\":\"Yossi\","
                + "\"lastName\":\"Cohen\",\"fullName\":\"Yossi Cohen\",\"username\":\"equipo.jefe\",\"keycloakUserId\":null,"
                + "\"category\":\"" + category + "\",\"employer\":\"SUBCONTRACTOR\",\"company\":\"Catenarias del Norte\","
                + "\"phone\":\"+972 50 000 0000\",\"active\":" + active + ",\"version\":3,\"audit\":null}";
    }

    /**
     * Personas: la busqueda con la categoria repetida (cualquiera de ellas, en el orden del enumerado),
     * la unidad y el estado, con el id para desempatar; el alta sin lo vacio; la modificacion con lo
     * cambiado, lo vaciado a {@code null} y la version; la reactivacion; la baja con un {@code DELETE};
     * el historial. Una categoria nueva se lee como {@code UNKNOWN}.
     */
    @Test
    void peopleAreSearchedByRepeatedCategoryCreatedPatchedDeactivatedAndReactivated() {
        UUID unitId = UUID.fromString("4d5e6f70-0000-4000-8000-000000000002");
        server.expect(requestTo(RESOURCES + "/people?search=cohen&category=WORKER&category=TEAM_LEADER&unitId=" + unitId
                        + "&active=true&page=0&size=50&sort=lastName%2Casc&sort=firstName%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(personJson(PERSON_ID, "PER-000003", "TEAM_LEADER", true) + ","
                        + personJson("3c4d5e6f-0000-4000-8000-000000000002", "PER-000004", "SAFETY_OFFICER", false), 0, 50, 2),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/people")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"firstName\":\"Yossi\",\"lastName\":\"Cohen\",\"category\":\"WORKER\",\"employer\":\"OWN\"}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(personJson(PERSON_ID, "PER-000003", "WORKER", true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/people/" + PERSON_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"category\":\"SUPERVISOR\",\"phone\":null,\"version\":3}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(personJson(PERSON_ID, "PER-000003", "SUPERVISOR", true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/people/" + PERSON_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(RESOURCES + "/people/" + PERSON_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"active\":true,\"version\":4}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(personJson(PERSON_ID, "PER-000003", "SUPERVISOR", true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/people/" + PERSON_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(maintenanceRevision(2, "UPDATED", "HTTP", personJson(PERSON_ID, "PER-000003", "SUPERVISOR", true)),
                        0, 20, 1), MediaType.APPLICATION_JSON));

        PageResponse<PersonDto> page = asUser(() -> personClient.search(new PersonFilter(" cohen ",
                Set.of(PersonCategory.TEAM_LEADER, PersonCategory.WORKER), unitId, true), 0, 50, List.of("lastName,asc", "firstName,asc")));
        PersonDto created = asUser(() -> personClient.create(new PersonRequest("Yossi", "Cohen", PersonCategory.WORKER, null, null,
                PersonEmployer.OWN, null, null)));
        asUser(() -> personClient.update(UUID.fromString(PERSON_ID), new MergePatch<>(
                new PersonUpdateRequest(null, null, PersonCategory.SUPERVISOR, null, null, null, null, null, null), Set.of("phone"), 3L)));
        asUser(() -> {
            personClient.deactivate(UUID.fromString(PERSON_ID));
            return null;
        });
        asUser(() -> personClient.update(UUID.fromString(PERSON_ID), MergePatch.of(PersonUpdateRequest.active(true), 4L)));
        PageResponse<RevisionDto<PersonDto>> revisions = asUser(() -> personClient.revisions(UUID.fromString(PERSON_ID), 0, 20));

        PersonDto leader = page.content().getFirst();
        assertEquals("PER-000003 · Yossi Cohen", leader.label());
        assertEquals(PersonCategory.TEAM_LEADER, leader.category());
        assertEquals(PersonEmployer.SUBCONTRACTOR, leader.employer());
        assertEquals("Catenarias del Norte", leader.company());
        assertEquals(3L, leader.version());
        assertEquals(PersonCategory.UNKNOWN, page.content().get(1).category(), "una categoria nueva se lee como desconocida");
        assertFalse(page.content().get(1).isActive());
        assertEquals(PersonCategory.WORKER, created.category());
        assertEquals(PersonCategory.SUPERVISOR, revisions.content().getFirst().entity().category());
        assertEquals(List.of(PersonCategory.TEAM_LEADER, PersonCategory.SUPERVISOR, PersonCategory.CHIEF_SUPERVISOR,
                PersonCategory.CHIEF_MAINTENANCE, PersonCategory.CHIEF_CONSTRUCTION, PersonCategory.PROJECT_MANAGER), PersonCategory.atLeast(2));
        server.verify();
    }

    /** El 409 de un usuario repetido de mto-resources llega con la misma forma que los de mantenimiento. */
    @Test
    void aRepeatedUsernameIsAConflictOfTheResourcesServiceReadByAlias() {
        server.expect(requestTo(RESOURCES + "/people")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).header("X-Correlation-Id", "corr-r1")
                        .body("{\"timestamp\":\"2026-10-16T06:07:00Z\",\"status\":409,\"error\":\"CONFLICT\","
                                + "\"message\":\"Username equipo.jefe is already in use\",\"path\":\"/api/v1/resources/people\",\"method\":\"POST\","
                                + "\"errorCode\":\"PER-409\",\"correlationId\":\"corr-r1\",\"validationErrors\":[]}"));

        ConflictApiException conflict = assertThrows(ConflictApiException.class, () -> asUser(() -> personClient.create(
                new PersonRequest("Otra", "Persona", PersonCategory.WORKER, null, "equipo.jefe", PersonEmployer.OWN, null, null))));

        assertEquals("PER-409", conflict.getProblem().code());
        assertEquals("Username equipo.jefe is already in use", conflict.getProblem().detail());
        assertEquals("corr-r1", conflict.getReference());
        server.verify();
    }

    private static final String UNIT_TEAM_ID = "5e6f7081-0000-4000-8000-000000000001";
    private static final String SUPERVISION_ID = "5e6f7081-0000-4000-8000-000000000002";
    private static final String LINEMAN_A = "3c4d5e6f-0000-4000-8000-00000000000a";
    private static final String LINEMAN_B = "3c4d5e6f-0000-4000-8000-00000000000b";

    private static String personSummary(String id, String code, String name, String category) {
        return "{\"id\":\"" + id + "\",\"code\":\"" + code + "\",\"fullName\":\"" + name + "\",\"username\":null,\"category\":\"" + category + "\"}";
    }

    private static String teamJson(int linemen, boolean active) {
        StringBuilder members = new StringBuilder();
        for (int i = 0; i < linemen; i++) {
            members.append(i == 0 ? "" : ",").append("{\"id\":\"6f708192-0000-4000-8000-00000000000").append(i)
                    .append("\",\"person\":").append(personSummary("3c4d5e6f-0000-4000-8000-00000000001" + i, "PER-00001" + i,
                            "Lineman " + i, "WORKER")).append(",\"joinedOn\":\"2026-10-01\",\"leftOn\":null}");
        }
        return "{\"id\":\"" + UNIT_TEAM_ID + "\",\"code\":\"A\",\"name\":\"Equipo A\",\"kind\":\"TEAM\",\"discipline\":\"MAINTENANCE\","
                + "\"parent\":{\"id\":\"" + SUPERVISION_ID + "\",\"code\":\"SUP-N\",\"name\":\"Supervision norte\",\"kind\":\"SUPERVISION\"},"
                + "\"head\":" + personSummary(PERSON_ID, "PER-000003", "Yossi Cohen", "TEAM_LEADER") + ",\"executionPackageIds\":[6,4,5],"
                + "\"defaultVehicle\":{\"id\":\"7a8b9c0d-0000-4000-8000-000000000001\",\"code\":\"DR-01\",\"name\":\"Dresina 1\","
                + "\"mobility\":\"RAIL_ONLY\",\"category\":\"CATENARY_TOWER_CAR\"},\"members\":[" + members + "],\"linemen\":" + linemen
                + ",\"complete\":" + (linemen >= 2) + ",\"supervisor\":" + personSummary("3c4d5e6f-0000-4000-8000-000000000020", "PER-000020",
                "Dana Levi", "SUPERVISOR") + ",\"chiefSupervisor\":null,\"active\":" + active + ",\"version\":7,\"audit\":null}";
    }

    /**
     * El organigrama: el arbol con las inactivas, una unidad nivel desconocido leido como {@code UNKNOWN},
     * un equipo con sus linemen, su supervisor y su vehiculo; el alta de un equipo completo sin lo vacio;
     * la modificacion con los EP como conjunto y el jefe vaciado; las hijas por codigo con el id para
     * desempatar; la baja; los linemen enteros, uno que entra y uno que sale, cada uno con su fecha; la
     * reactivacion de un equipo con sus linemen.
     */
    @Test
    void theOrganizationIsReadAsATreeAndATeamChangesItsLinemenWithoutLeavingTheRule() {
        server.expect(requestTo(RESOURCES + "/units/tree?includeInactive=true")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"5e6f7081-0000-4000-8000-000000000009\",\"code\":\"PRJ\",\"name\":\"Proyecto\",\"kind\":\"PROJECT\","
                        + "\"discipline\":null,\"head\":null,\"executionPackageIds\":[],\"linemen\":null,\"active\":true,\"children\":["
                        + "{\"id\":\"" + SUPERVISION_ID + "\",\"code\":\"SUP-N\",\"name\":\"Supervision norte\",\"kind\":\"SUPERVISION\","
                        + "\"discipline\":\"MAINTENANCE\",\"head\":null,\"executionPackageIds\":[4],\"linemen\":null,\"active\":true,\"children\":["
                        + "{\"id\":\"" + UNIT_TEAM_ID + "\",\"code\":\"A\",\"name\":\"Equipo A\",\"kind\":\"TEAM\",\"discipline\":\"MAINTENANCE\","
                        + "\"head\":" + personSummary(PERSON_ID, "PER-000003", "Yossi Cohen", "TEAM_LEADER") + ",\"executionPackageIds\":[4,5],"
                        + "\"linemen\":4,\"active\":false,\"children\":[]}]},"
                        + "{\"id\":\"5e6f7081-0000-4000-8000-000000000008\",\"code\":\"ZONA\",\"name\":\"Zona\",\"kind\":\"ZONE\",\"discipline\":null,"
                        + "\"head\":null,\"executionPackageIds\":null,\"linemen\":null,\"active\":true,\"children\":null}]}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(teamJson(2, true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"B\",\"name\":\"Equipo B\",\"kind\":\"TEAM\",\"discipline\":\"MAINTENANCE\","
                        + "\"parentId\":\"" + SUPERVISION_ID + "\",\"headPersonId\":\"" + PERSON_ID + "\",\"executionPackageIds\":[9,11],"
                        + "\"memberPersonIds\":[\"" + LINEMAN_A + "\",\"" + LINEMAN_B + "\"],\"joinedOn\":\"2026-10-16\"}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(teamJson(2, true)));
        server.expect(requestTo(RESOURCES + "/units")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"SUP-S\",\"name\":\"Supervision sur\",\"kind\":\"SUPERVISION\",\"discipline\":\"MAINTENANCE\","
                        + "\"parentId\":\"" + SUPERVISION_ID + "\"}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(teamJson(2, true)));
        server.expect(requestTo(RESOURCES + "/units/" + SUPERVISION_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"executionPackageIds\":[4,6],\"headPersonId\":null,\"version\":2}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(teamJson(2, true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units?parentId=" + SUPERVISION_ID + "&page=0&size=200&sort=code%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(stockPage(teamJson(4, true), 0, 200, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID + "/members")).andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"memberPersonIds\":[\"" + LINEMAN_A + "\",\"" + LINEMAN_B + "\"],\"effectiveOn\":\"2026-10-20\"}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(teamJson(2, true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID + "/members")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"personId\":\"" + LINEMAN_A + "\"}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(teamJson(3, true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID + "/members/" + LINEMAN_A + "/leave")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"leftOn\":\"2026-10-21\"}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(teamJson(2, true), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID + "/members?includeFormer=true")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{\"id\":\"6f708192-0000-4000-8000-0000000000aa\",\"person\":"
                        + personSummary(LINEMAN_A, "PER-00000A", "Avi Mizrahi", "WORKER") + ",\"joinedOn\":\"2026-10-01\",\"leftOn\":\"2026-10-21\"}]",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID)).andExpect(method(HttpMethod.DELETE)).andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(RESOURCES + "/units/" + UNIT_TEAM_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"active\":true,\"memberPersonIds\":[\"" + LINEMAN_A + "\",\"" + LINEMAN_B + "\"],\"version\":8}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(teamJson(2, true), MediaType.APPLICATION_JSON));

        List<OrganizationTreeNodeDto> tree = asUser(() -> organizationClient.tree(true));
        OrganizationUnitDto team = asUser(() -> organizationClient.findById(UUID.fromString(UNIT_TEAM_ID)));
        UUID supervision = UUID.fromString(SUPERVISION_ID);
        List<UUID> linemen = List.of(UUID.fromString(LINEMAN_A), UUID.fromString(LINEMAN_B));
        asUser(() -> organizationClient.create(new OrganizationUnitRequest("B", "Equipo B", OrganizationUnitKind.TEAM, WorkDiscipline.MAINTENANCE,
                supervision, UUID.fromString(PERSON_ID), new java.util.TreeSet<>(Set.of(9L, 11L)), null, linemen, java.time.LocalDate.of(2026, 10, 16))));
        asUser(() -> organizationClient.create(new OrganizationUnitRequest("SUP-S", "Supervision sur", OrganizationUnitKind.SUPERVISION,
                WorkDiscipline.MAINTENANCE, supervision, null, Set.of(), null, List.of(), null)));
        asUser(() -> organizationClient.update(supervision, new MergePatch<>(new OrganizationUnitUpdateRequest(null, null, null,
                new java.util.TreeSet<>(Set.of(6L, 4L)), null, null, null, null), Set.of("headPersonId"), 2L)));
        PageResponse<OrganizationUnitDto> children = asUser(() -> organizationClient.children(supervision, 0, 200));
        asUser(() -> organizationClient.replaceMembers(team.id(), new TeamMembersRequest(linemen, java.time.LocalDate.of(2026, 10, 20))));
        asUser(() -> organizationClient.addMember(team.id(), new TeamMemberRequest(linemen.getFirst(), null)));
        asUser(() -> organizationClient.leave(team.id(), linemen.getFirst(), new TeamMemberLeaveRequest(java.time.LocalDate.of(2026, 10, 21))));
        List<TeamMemberDto> former = asUser(() -> organizationClient.members(team.id(), true));
        asUser(() -> {
            organizationClient.deactivate(team.id());
            return null;
        });
        asUser(() -> organizationClient.update(team.id(), MergePatch.of(OrganizationUnitUpdateRequest.reactivateTeam(linemen, null), 8L)));

        OrganizationTreeNodeDto project = tree.getFirst();
        assertEquals(OrganizationUnitKind.PROJECT, project.kind());
        assertEquals(List.of("SUP-N", "ZONA"), project.children().stream().map(OrganizationTreeNodeDto::code).toList());
        assertEquals(OrganizationUnitKind.UNKNOWN, project.children().get(1).kind(), "un nivel nuevo se lee como desconocido");
        assertTrue(project.children().get(1).children().isEmpty());
        OrganizationTreeNodeDto teamNode = project.children().getFirst().children().getFirst();
        assertEquals(4, teamNode.linemen());
        assertFalse(teamNode.isActive());
        assertEquals(Set.of(4L, 5L), teamNode.executionPackageIds());
        assertTrue(team.isTeam());
        assertEquals("A · Equipo A", team.label());
        assertEquals("SUP-N", team.parent().code());
        assertEquals("PER-000003 · Yossi Cohen", team.head().label());
        assertEquals("Dana Levi", team.supervisor().fullName());
        assertEquals("DR-01", team.defaultVehicle().code());
        assertEquals(2, team.currentMembers().size());
        assertEquals(7L, team.version());
        assertEquals(4, children.content().getFirst().linemen());
        assertFalse(former.getFirst().isCurrent());
        assertEquals(java.time.LocalDate.of(2026, 10, 21), former.getFirst().leftOn());
        server.verify();
    }

    /** Las reglas copiadas del servicio para no ofrecer lo que va a fallar: de quien cuelga cada nivel y quien lo dirige. */
    @Test
    void eachLevelHangsFromAHigherOneAndIsLedByItsCategories() {
        assertTrue(OrganizationUnitKind.TEAM.canHangFrom(OrganizationUnitKind.SUPERVISION));
        assertTrue(OrganizationUnitKind.TEAM.canHangFrom(OrganizationUnitKind.DEPARTMENT), "se admite saltar niveles");
        assertFalse(OrganizationUnitKind.TEAM.canHangFrom(OrganizationUnitKind.TEAM));
        assertFalse(OrganizationUnitKind.SUPERVISION.canHangFrom(OrganizationUnitKind.SUPERVISION));
        assertFalse(OrganizationUnitKind.PROJECT.canHangFrom(OrganizationUnitKind.PROJECT), "un proyecto no cuelga de nada");
        assertFalse(OrganizationUnitKind.TEAM.canHangFrom(OrganizationUnitKind.UNKNOWN));
        assertEquals(List.of(PersonCategory.CHIEF_MAINTENANCE, PersonCategory.PROJECT_MANAGER),
                OrganizationUnitKind.DEPARTMENT.headCategories(WorkDiscipline.MAINTENANCE));
        assertEquals(List.of(PersonCategory.CHIEF_CONSTRUCTION, PersonCategory.PROJECT_MANAGER),
                OrganizationUnitKind.DEPARTMENT.headCategories(WorkDiscipline.CONSTRUCTION));
        assertEquals(PersonCategory.atLeast(2), OrganizationUnitKind.TEAM.headCategories(WorkDiscipline.MAINTENANCE));
        assertEquals(List.of(PersonCategory.PROJECT_MANAGER), OrganizationUnitKind.PROJECT.headCategories(null));
        assertEquals(List.of(), OrganizationUnitKind.UNKNOWN.headCategories(null));
        assertEquals(List.of(PersonCategory.WORKER, PersonCategory.TEAM_LEADER), OrganizationUnitKind.LINEMAN_CATEGORIES);
    }

    private static final String VEHICLE_TYPE_ID = "7b8c9d0e-0000-4000-8000-000000000001";
    private static final String PARKING_SITE_ID = "8c9d0e1f-0000-4000-8000-000000000001";

    private static String vehicleTypeJson(String category, String capabilities) {
        return "{\"id\":\"" + VEHICLE_TYPE_ID + "\",\"code\":\"UNIMOG-RR\",\"name\":\"Unimog bivial\",\"manufacturer\":\"Mercedes-Benz\","
                + "\"model\":\"U423\",\"mobility\":\"ROAD_RAIL\",\"category\":\"" + category + "\",\"selfPropelled\":true,\"portable\":false,"
                + "\"lengthM\":6.20,\"seats\":3,\"trackGaugeMm\":1435,\"railGearType\":\"RAIL_WHEEL_DRIVE\",\"onTrackingMinutes\":10,"
                + "\"needsPreparedAccess\":false,\"maxSpeedRoadKmh\":90,\"requiredLicence\":\"C\",\"capabilities\":" + capabilities + ","
                + "\"craneCapacityT\":5.00,\"towingCapacityT\":40.00,\"active\":true,\"version\":4,\"audit\":null}";
    }

    /**
     * Los modelos: la busqueda con sus filtros y el id para desempatar; el alta sin lo vacio (las
     * capacidades en orden); la modificacion con lo cambiado, lo vaciado y la version; retirar. Una
     * categoria nueva se lee como UNKNOWN y una capacidad nueva vuelve tal cual, como texto.
     */
    @Test
    void vehicleModelsAreSearchedCreatedPatchedAndRetiredAndAnUnknownCapabilitySurvives() {
        server.expect(requestTo(RESOURCES + "/vehicle-types?search=unimog&mobility=ROAD_RAIL&category=ROAD_RAIL_TRUCK&capability=CRANE&active=true"
                        + "&page=0&size=50&sort=code%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(vehicleTypeJson("ROAD_RAIL_TRUCK", "[\"TOWING\",\"CRANE\",\"LASER_SCANNER\"]") + ","
                        + vehicleTypeJson("HOVERCRAFT", "[]"), 0, 50, 2), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicle-types")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"VAN-1\",\"name\":\"Furgoneta\",\"mobility\":\"ROAD_ONLY\",\"category\":\"VAN\","
                        + "\"selfPropelled\":true,\"portable\":false,\"seats\":3,\"capabilities\":[\"CREW_CABIN\",\"EARTHING\"]}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(vehicleTypeJson("ROAD_RAIL_TRUCK", "[]")));
        server.expect(requestTo(RESOURCES + "/vehicle-types/" + VEHICLE_TYPE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"capabilities\":[\"LASER_SCANNER\",\"TOWING\"],\"craneCapacityT\":null,\"version\":4}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(vehicleTypeJson("ROAD_RAIL_TRUCK", "[]"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicle-types/" + VEHICLE_TYPE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        PageResponse<VehicleTypeDto> page = asUser(() -> vehicleTypeClient.search(new VehicleTypeFilter(" unimog ", VehicleMobility.ROAD_RAIL,
                VehicleCategory.ROAD_RAIL_TRUCK, "CRANE", true), 0, 50, List.of("code,asc")));
        asUser(() -> vehicleTypeClient.create(new VehicleTypeRequest("VAN-1", "Furgoneta", null, null, VehicleMobility.ROAD_ONLY,
                VehicleCategory.VAN, true, false, null, null, null, null, null, null, null, 3, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, new java.util.TreeSet<>(Set.of("EARTHING", "CREW_CABIN")), null, null, null, null, null,
                null, null, null)));
        VehicleTypeDto unimog = page.content().getFirst();
        asUser(() -> vehicleTypeClient.update(unimog.id(), new MergePatch<>(new VehicleTypeUpdateRequest(null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, new java.util.TreeSet<>(Set.of("TOWING", "LASER_SCANNER")), null, null, null, null, null, null, null, null, null),
                Set.of("craneCapacityT"), 4L)));
        asUser(() -> {
            vehicleTypeClient.deactivate(unimog.id());
            return null;
        });

        assertEquals("UNIMOG-RR · Unimog bivial", unimog.label());
        assertEquals(VehicleMobility.ROAD_RAIL, unimog.mobility());
        assertEquals(RailGearType.RAIL_WHEEL_DRIVE, unimog.railGearType());
        assertEquals(DrivingLicence.C, unimog.requiredLicence());
        assertEquals(new java.math.BigDecimal("5.00"), unimog.craneCapacityT());
        assertEquals(Set.of("CRANE", "LASER_SCANNER", "TOWING"), unimog.capabilities(), "una capacidad nueva se queda como texto");
        assertEquals("LASER_SCANNER", VehicleCapability.labelOf("LASER_SCANNER"));
        assertEquals("Grua", VehicleCapability.labelOf("CRANE"));
        assertEquals(VehicleCategory.UNKNOWN, page.content().get(1).category(), "una categoria nueva se lee como desconocida");
        assertTrue(VehicleMobility.ROAD_RAIL.runsOnTrack() && VehicleMobility.ROAD_RAIL.runsOnRoad());
        assertFalse(VehicleMobility.RAIL_ONLY.runsOnRoad());
        assertFalse(VehicleMobility.ROAD_ONLY.runsOnTrack());
        assertEquals(List.of(VehicleCategory.VAN, VehicleCategory.PICKUP, VehicleCategory.CREW_BUS, VehicleCategory.CRANE_TRUCK,
                VehicleCategory.TRUCK, VehicleCategory.LOW_LOADER, VehicleCategory.CAR), VehicleCategory.of(VehicleMobility.ROAD_ONLY));
        server.verify();
    }

    /** Los sitios de aparcamiento: la busqueda con sus filtros, el alta sin lo vacio, la modificacion y retirar. */
    @Test
    void parkingSitesAreSearchedCreatedPatchedAndRetired() {
        String site = "{\"id\":\"" + PARKING_SITE_ID + "\",\"code\":\"APT-RSH\",\"name\":\"Apartadero de Rishpon\",\"kind\":\"SIDING\","
                + "\"executionPackageId\":4,\"stationId\":null,\"trackId\":12,\"kp\":12.300,\"railAccess\":true,\"roadAccess\":false,"
                + "\"address\":null,\"latitude\":32.200000,\"longitude\":34.820000,\"notes\":null,\"active\":true,\"version\":2,\"audit\":null}";
        server.expect(requestTo(RESOURCES + "/parking-sites?kind=SIDING&executionPackageId=4&active=true&page=0&size=50&sort=code%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(stockPage(site, 0, 50, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/parking-sites")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"APT-RSH\",\"name\":\"Apartadero de Rishpon\",\"kind\":\"SIDING\",\"trackId\":12,"
                        + "\"kp\":12.300,\"railAccess\":true,\"roadAccess\":false}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(site));
        server.expect(requestTo(RESOURCES + "/parking-sites/" + PARKING_SITE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"roadAccess\":true,\"latitude\":null,\"longitude\":null,\"version\":2}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(site, MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/parking-sites/" + PARKING_SITE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        PageResponse<ParkingSiteDto> page = asUser(() -> parkingSiteClient.search(new ParkingSiteFilter(" ", ParkingSiteKind.SIDING, 4L, null, null,
                null, true), 0, 50, List.of("code,asc")));
        asUser(() -> parkingSiteClient.create(new ParkingSiteRequest("APT-RSH", "Apartadero de Rishpon", ParkingSiteKind.SIDING, null, null, 12L,
                new java.math.BigDecimal("12.300"), true, false, null, null, null, null)));
        asUser(() -> parkingSiteClient.update(UUID.fromString(PARKING_SITE_ID), new MergePatch<>(new ParkingSiteUpdateRequest(null, null, null,
                null, null, null, null, true, null, null, null, null, null), Set.of("latitude", "longitude"), 2L)));
        asUser(() -> {
            parkingSiteClient.deactivate(UUID.fromString(PARKING_SITE_ID));
            return null;
        });

        ParkingSiteDto rishpon = page.content().getFirst();
        assertEquals("APT-RSH · Apartadero de Rishpon", rishpon.label());
        assertEquals(ParkingSiteKind.SIDING, rishpon.kind());
        assertEquals(12L, rishpon.trackId());
        assertEquals(new java.math.BigDecimal("12.300"), rishpon.kp());
        server.verify();
    }

    private static final String VEHICLE_ID = "9d0e1f20-0000-4000-8000-000000000001";
    private static final String CARRIER_ID = "9d0e1f20-0000-4000-8000-000000000002";
    private static final String LOCATION_SHIFT_ID = "a0b1c2d3-0000-4000-8000-000000000001";

    private static String locationJson(String kind, String extra) {
        return "{\"id\":\"b1c2d3e4-0000-4000-8000-000000000001\",\"kind\":\"" + kind + "\",\"parkingSite\":{\"id\":\"" + PARKING_SITE_ID
                + "\",\"code\":\"APT-RSH\",\"name\":\"Apartadero\",\"kind\":\"SIDING\"},\"executionPackageId\":4,\"trackId\":12,\"kp\":12.300,"
                + "\"latitude\":null,\"longitude\":null,\"accessPoint\":null,\"trackAccessPoint\":null,\"description\":null,\"carrier\":null,"
                + "\"attachment\":null,"
                + "\"source\":\"SHIFT\",\"sourceReference\":\"mto-maintenance:shift:" + LOCATION_SHIFT_ID + "\",\"observedAt\":\"2026-10-16T03:40:00Z\","
                + "\"recordedAt\":\"2026-10-16T03:40:02Z\",\"recordedBy\":\"service-account-mto-maintenance-svc\"" + extra + "}";
    }

    private static String vehicleJson(String position) {
        return "{\"id\":\"" + VEHICLE_ID + "\",\"code\":\"DR-01\",\"name\":\"Dresina 1\",\"type\":{\"id\":\"" + VEHICLE_TYPE_ID + "\","
                + "\"code\":\"DIC-30\",\"name\":\"Plasser DIC-30\",\"mobility\":\"RAIL_ONLY\",\"category\":\"CATENARY_TOWER_CAR\","
                + "\"selfPropelled\":true,\"seats\":6,\"railGearType\":null,\"transportModeAuthorised\":true,\"capabilities\":[\"PANTOGRAPH\"]},"
                + "\"registrationPlate\":null,\"railVehicleNumber\":\"99 80 9427 001-1\",\"serialNumber\":null,\"manufactureYear\":2019,"
                + "\"owner\":\"OWN\",\"ownerCompany\":null,\"operationalStatus\":\"LIMITED\",\"statusReason\":\"Plataforma en revision\","
                + "\"available\":true,\"odometerKm\":null,\"engineHours\":1234.5,\"notes\":null,\"position\":" + position + ",\"active\":true,"
                + "\"version\":6,\"audit\":null}";
    }

    /**
     * Los vehiculos: la busqueda con sus filtros, el alta sin lo vacio, la modificacion, retirar, el
     * historial, registrar donde esta (sin lo vacio y con la hora en ISO), sus ubicaciones entre dos
     * instantes y el tablero por via y kp. Un turno firma su ubicacion con su id; un tipo de ubicacion
     * nuevo se lee como desconocido.
     */
    @Test
    void vehiclesAreSearchedWrittenLocatedAndShownOnTheBoard() {
        server.expect(requestTo(RESOURCES + "/vehicles?search=dr&mobility=RAIL_ONLY&status=LIMITED&available=true&active=true"
                        + "&page=0&size=50&sort=code%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(vehicleJson(locationJson("PARKED", "")), 0, 50, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicles")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"DR-02\",\"typeId\":\"" + VEHICLE_TYPE_ID + "\",\"railVehicleNumber\":\"99 80 9427 002-9\","
                        + "\"owner\":\"OWN\",\"operationalStatus\":\"OPERATIONAL\"}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(vehicleJson("null")));
        server.expect(requestTo(RESOURCES + "/vehicles/" + VEHICLE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"operationalStatus\":\"OPERATIONAL\",\"statusReason\":null,\"version\":6}",
                        org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withSuccess(vehicleJson("null"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicles/" + VEHICLE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(RESOURCES + "/vehicles/" + VEHICLE_ID + "/locations")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"kind\":\"ATTACHED\",\"carrierVehicleId\":\"" + CARRIER_ID + "\",\"attachment\":\"TOWED\","
                        + "\"observedAt\":\"2026-10-16T03:40:00Z\"}", org.springframework.test.json.JsonCompareMode.STRICT))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(locationJson("ATTACHED",
                        ",\"extra\":1").replace("\"carrier\":null", "\"carrier\":{\"id\":\"" + CARRIER_ID + "\",\"code\":\"DR-02\",\"name\":null,"
                        + "\"mobility\":\"RAIL_ONLY\",\"category\":\"TRACK_MAINTENANCE_CAR\"}").replace("\"attachment\":null", "\"attachment\":\"TOWED\"")));
        server.expect(requestTo(RESOURCES + "/vehicles/" + VEHICLE_ID + "/locations?from=2026-10-01T00%3A00%3A00Z&to=2026-10-17T00%3A00%3A00Z"
                        + "&page=0&size=20"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(locationJson("TELEPORTED", ""), 0, 20, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicles/positions?trackId=12&page=0&size=50&sort=trackId%2Casc&sort=kp%2Casc&sort=vehicleId%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("{\"vehicle\":{\"id\":\"" + VEHICLE_ID + "\",\"code\":\"DR-01\",\"name\":\"Dresina 1\","
                        + "\"mobility\":\"RAIL_ONLY\",\"category\":\"CATENARY_TOWER_CAR\"},\"operationalStatus\":\"OPERATIONAL\",\"available\":true,"
                        + "\"position\":" + locationJson("ATTACHED", "") + ",\"effective\":" + locationJson("ON_TRACK", "") + "}", 0, 50, 1),
                        MediaType.APPLICATION_JSON));

        PageResponse<VehicleDto> page = asUser(() -> vehicleClient.search(new VehicleFilter(" dr ", null, VehicleMobility.RAIL_ONLY, null,
                VehicleOperationalStatus.LIMITED, true, true), 0, 50, List.of("code,asc")));
        asUser(() -> vehicleClient.create(new VehicleRequest("DR-02", null, UUID.fromString(VEHICLE_TYPE_ID), null, "99 80 9427 002-9", null, null,
                VehicleOwner.OWN, null, VehicleOperationalStatus.OPERATIONAL, null, null, null, null, null)));
        asUser(() -> vehicleClient.update(UUID.fromString(VEHICLE_ID), new MergePatch<>(new VehicleUpdateRequest(null, null, null, null, null, null,
                null, null, VehicleOperationalStatus.OPERATIONAL, null, null, null, null, null, null), Set.of("statusReason"), 6L)));
        asUser(() -> {
            vehicleClient.deactivate(UUID.fromString(VEHICLE_ID));
            return null;
        });
        VehicleLocationDto towed = asUser(() -> vehicleClient.recordLocation(UUID.fromString(VEHICLE_ID), new VehicleLocationRequest(
                VehicleLocationKind.ATTACHED, null, null, null, null, null, null, null, null, null, UUID.fromString(CARRIER_ID), VehicleAttachment.TOWED,
                java.time.Instant.parse("2026-10-16T03:40:00Z"))));
        PageResponse<VehicleLocationDto> history = asUser(() -> vehicleClient.locations(UUID.fromString(VEHICLE_ID),
                java.time.Instant.parse("2026-10-01T00:00:00Z"), java.time.Instant.parse("2026-10-17T00:00:00Z"), 0, 20));
        PageResponse<FleetPositionDto> board = asUser(() -> vehicleClient.positions(null, 12L, null, 0, 50, VehicleClient.BY_TRACK_AND_KP));

        VehicleDto dresina = page.content().getFirst();
        assertEquals("DR-01 · Dresina 1", dresina.label());
        assertEquals(VehicleMobility.RAIL_ONLY, dresina.mobility());
        assertTrue(dresina.type().transportModeAuthorised());
        assertEquals(VehicleOperationalStatus.LIMITED, dresina.operationalStatus());
        assertEquals(new java.math.BigDecimal("1234.5"), dresina.engineHours());
        assertEquals("APT-RSH", dresina.position().parkingSite().code());
        assertEquals(UUID.fromString(LOCATION_SHIFT_ID), dresina.position().shiftId(), "la ubicacion que deja un turno lo nombra");
        assertEquals(VehicleLocationSource.SHIFT, dresina.position().source());
        assertEquals("DR-02", towed.carrier().code());
        assertEquals(VehicleAttachment.TOWED, towed.attachment());
        assertEquals(VehicleLocationKind.UNKNOWN, history.content().getFirst().kind(), "un tipo nuevo se lee como desconocido");
        assertEquals(VehicleLocationKind.ON_TRACK, board.content().getFirst().where().kind(), "uno remolcado esta donde su portador");
        assertEquals(List.of(VehicleLocationKind.PARKED, VehicleLocationKind.ON_TRACK, VehicleLocationKind.IN_WORKSHOP, VehicleLocationKind.ATTACHED,
                VehicleLocationKind.UNKNOWN), VehicleLocationKind.recordable(VehicleMobility.RAIL_ONLY));
        assertFalse(VehicleLocationKind.recordable(VehicleMobility.ROAD_ONLY).contains(VehicleLocationKind.ON_TRACK));
        server.verify();
    }

    // --- Recursos: los puntos de acceso a la via (fase 4, bloque D) ---------------------------------------

    /**
     * mto-resources V9: los puntos de acceso de una via son una lista entera, sin paginar, de lo que ha
     * recibido de mto-configuration (una clase nueva se lee como desconocida y lo que no se usa se ignora; si
     * llegara una pagina, se lee su {@code content}). Registrar donde esta un vehiculo lleva el elegido como
     * {@code accessPointId}, y la ubicacion lo dice en {@code trackAccessPoint {id, name, kind}}, con el texto
     * libre en {@code accessPoint}, como siempre: se ensena el nombre del maestro ({@code #id} sin el) y, sin
     * el, el texto. Uno que no conoce es el 404 TAP-404.
     */
    @Test
    void theAccessPointsOfATrackComeFromResourcesAndALocationNamesTheOneItUsed() {
        AccessPointClient accessPoints = GatewayClientConfiguration.proxyFactory(restClient).createClient(AccessPointClient.class);
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String locations = RESOURCES + "/vehicles/" + VEHICLE_ID + "/locations";
        server.expect(requestTo(RESOURCES + "/track-access-points?trackId=12&enabled=true"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":77,"name":"Acceso km 110+176","trackId":12,"executionPackageId":4,"stationId":7,"kp":110176.000,
                          "kind":"PREPARED_ACCESS","side":"LEFT","roadRailAccess":true,"maxAxleLoadT":12.500,"maxGrossWeightT":40.000,
                          "latitude":31.669342,"longitude":34.574119,"enabled":true},
                         {"id":78,"name":"Embarcadero","trackId":12,"kp":null,"kind":"FERRY_RAMP","side":"BOTH","roadRailAccess":false,
                          "maxAxleLoadT":null,"maxGrossWeightT":null,"enabled":true}]
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/track-access-points?trackId=13&enabled=true"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage("{\"id\":80,\"name\":\"Base Lod\",\"trackId\":13,\"kp\":5.000,\"kind\":\"DEPOT\","
                        + "\"roadRailAccess\":true,\"enabled\":true}", 0, 50, 1), MediaType.APPLICATION_JSON));
        server.expect(requestTo(locations)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"kind\":\"ON_TRACK\",\"trackId\":12,\"kp\":10.5,\"accessPointId\":77}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(locationJson("ON_TRACK", "")
                        .replace("\"trackAccessPoint\":null",
                                "\"trackAccessPoint\":{\"id\":77,\"name\":\"Acceso km 110+176\",\"kind\":\"PREPARED_ACCESS\"}")));
        server.expect(requestTo(locations)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":404,\"error\":\"NOT_FOUND\",\"message\":\"Track access point 99 not found\","
                                + "\"errorCode\":\"TAP-404\",\"correlationId\":\"corr-tap\",\"validationErrors\":[]}"));
        server.expect(requestTo(locations + "?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(String.join(",",
                        locationJson("ON_TRACK", "").replace("\"accessPoint\":null", "\"accessPoint\":\"LC-31\""),
                        locationJson("ON_TRACK", "").replace("\"trackAccessPoint\":null", "\"trackAccessPoint\":{\"id\":77,\"name\":\"Acceso km 110+176\","
                                + "\"kind\":\"DEPOT\"}").replace("\"accessPoint\":null", "\"accessPoint\":\"Camino de la base\""),
                        locationJson("PARKED", "").replace("\"trackAccessPoint\":null", "\"trackAccessPoint\":{\"id\":81,\"name\":null,\"kind\":null}"),
                        locationJson("PARKED", "")), 0, 20, 4), MediaType.APPLICATION_JSON));

        List<AccessPointDto> offered = asUser(() -> accessPoints.enabledOnTrack(12L));
        List<AccessPointDto> paged = asUser(() -> accessPoints.enabledOnTrack(13L));
        UUID vehicle = UUID.fromString(VEHICLE_ID);
        VehicleLocationDto recorded = asUser(() -> vehicleClient.recordLocation(vehicle, new VehicleLocationRequest(VehicleLocationKind.ON_TRACK, null,
                null, 12L, new java.math.BigDecimal("10.5"), null, null, 77L, null, null, null, null, null)));
        NotFoundApiException unknown = assertThrows(NotFoundApiException.class, () -> asUser(() -> vehicleClient.recordLocation(vehicle,
                new VehicleLocationRequest(VehicleLocationKind.ON_TRACK, null, null, 12L, null, null, null, 99L, null, null, null, null, null))));
        PageResponse<VehicleLocationDto> history = asUser(() -> vehicleClient.locations(vehicle, null, null, 0, 20));

        assertEquals(2, offered.size());
        assertEquals("Acceso km 110+176 · Acceso preparado · kp 110176.000", offered.getFirst().label());
        assertEquals(Boolean.TRUE, offered.getFirst().roadRailAccess());
        assertEquals(new java.math.BigDecimal("40.000"), offered.getFirst().maxGrossWeightT());
        assertEquals(AccessPointKind.UNKNOWN, offered.get(1).kind(), "una clase nueva se lee como desconocida");
        assertEquals("Embarcadero · Desconocido · sin biviales", offered.get(1).label());
        assertEquals(List.of("Base Lod · Base o cochera · kp 5.000"), paged.stream().map(AccessPointDto::label).toList(), "una pagina, por su content");
        assertEquals("Acceso km 110+176", recorded.accessPointLabel());
        assertEquals(77L, recorded.trackAccessPoint().id());
        assertEquals(AccessPointKind.PREPARED_ACCESS, recorded.trackAccessPoint().kind());
        assertEquals("TAP-404", unknown.getProblem().code());
        assertEquals("LC-31", history.content().getFirst().accessPointLabel(), "el texto libre");
        assertNull(history.content().getFirst().trackAccessPoint());
        assertEquals("Acceso km 110+176", history.content().get(1).accessPointLabel(), "el del maestro manda sobre el texto");
        assertEquals("Camino de la base", history.content().get(1).accessPoint());
        assertEquals(AccessPointKind.DEPOT, history.content().get(1).trackAccessPoint().kind());
        assertEquals("#81", history.content().get(2).accessPointLabel(), "sin nombre, su id");
        assertNull(history.content().get(3).accessPointLabel(), "sin punto de acceso");
        server.verify();
    }

    // --- Recursos: cualificaciones, documentos e instrumentos (fase 4, bloque C) -------------------------

    private static final String QUALIFICATION_TYPE_ID = "6a7b8c9d-0000-4000-8000-000000000001";
    private static final String QUALIFICATION_ID = "6a7b8c9d-0000-4000-8000-0000000000a1";
    private static final String DOCUMENT_ID = "6a7b8c9d-0000-4000-8000-0000000000d1";
    private static final String INSTRUMENT_ID = "6a7b8c9d-0000-4000-8000-0000000000e1";

    private static String qualificationTypeJson(String id, String code, String kind, String licence) {
        return "{\"id\":\"" + id + "\",\"code\":\"" + code + "\",\"name\":\"Driving licence C\",\"kind\":\"" + kind + "\",\"drivingLicence\":"
                + (licence == null ? "null" : "\"" + licence + "\"") + ",\"validityMonths\":120,\"description\":null,\"active\":true,"
                + "\"version\":2,\"audit\":null}";
    }

    /**
     * El catalogo de tipos de cualificacion: la busqueda con sus filtros y el id para desempatar, el alta
     * sin lo vacio, la modificacion con lo cambiado, lo vaciado (el permiso de uno que deja de ser un
     * permiso) y la version, y retirar con un DELETE. Una clase nueva se lee como UNKNOWN.
     */
    @Test
    void qualificationTypesAreSearchedCreatedPatchedAndRetiredAndAnUnknownKindIsRead() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String types = RESOURCES + "/qualification-types";
        server.expect(requestTo(types + "?search=lic&kind=DRIVING_LICENCE&active=true&page=0&size=50&sort=code%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(qualificationTypeJson(QUALIFICATION_TYPE_ID, "LIC-C", "DRIVING_LICENCE", "C") + ","
                        + qualificationTypeJson("6a7b8c9d-0000-4000-8000-000000000002", "MED-1", "MEDICAL", null), 0, 50, 2), MediaType.APPLICATION_JSON));
        server.expect(requestTo(types)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"LIC-C\",\"name\":\"Driving licence C\",\"kind\":\"DRIVING_LICENCE\",\"drivingLicence\":\"C\","
                        + "\"validityMonths\":120}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body(qualificationTypeJson(QUALIFICATION_TYPE_ID, "LIC-C", "DRIVING_LICENCE", "C")));
        server.expect(requestTo(types + "/" + QUALIFICATION_TYPE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"kind\":\"SAFETY\",\"drivingLicence\":null,\"validityMonths\":null,\"version\":2}", strict))
                .andRespond(withSuccess(qualificationTypeJson(QUALIFICATION_TYPE_ID, "LIC-C", "SAFETY", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(types + "/" + QUALIFICATION_TYPE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(types + "/" + QUALIFICATION_TYPE_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(qualificationTypeJson(QUALIFICATION_TYPE_ID, "LIC-C", "DRIVING_LICENCE", "C"), MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(QUALIFICATION_TYPE_ID);
        PageResponse<QualificationTypeDto> page = asUser(() -> qualificationTypeClient.search(new QualificationTypeFilter(" lic ",
                QualificationKind.DRIVING_LICENCE, true), 0, 50, List.of("code,asc")));
        asUser(() -> qualificationTypeClient.create(new QualificationTypeRequest("LIC-C", "Driving licence C", QualificationKind.DRIVING_LICENCE,
                DrivingLicence.C, 120, null)));
        QualificationTypeDto patched = asUser(() -> qualificationTypeClient.update(id, new MergePatch<>(new QualificationTypeUpdateRequest(null,
                QualificationKind.SAFETY, null, null, null, null), Set.of("drivingLicence", "validityMonths"), 2L)));
        asUser(() -> {
            qualificationTypeClient.deactivate(id);
            return null;
        });
        QualificationTypeDto read = asUser(() -> qualificationTypeClient.findById(id));

        QualificationTypeDto licence = page.content().getFirst();
        assertEquals("LIC-C · Driving licence C", licence.label());
        assertEquals(DrivingLicence.C, licence.drivingLicence());
        assertEquals(120, licence.validityMonths());
        assertEquals(new QualificationTypeSummaryDto(id, "otro codigo", null, null), licence.summary(), "el resumen es el mismo por su id");
        assertEquals(QualificationKind.UNKNOWN, page.content().get(1).kind(), "una clase nueva se lee como desconocida");
        assertFalse(QualificationKind.selectable().contains(QualificationKind.UNKNOWN));
        assertEquals(QualificationKind.SAFETY, patched.kind());
        assertNull(patched.drivingLicence());
        assertTrue(read.isActive());
        server.verify();
    }

    private static String qualificationJson(String id, String typeCode, String kind, String expiresOn, boolean expired) {
        return "{\"id\":\"" + id + "\",\"personId\":\"" + PERSON_ID + "\",\"qualificationType\":{\"id\":\"" + QUALIFICATION_TYPE_ID + "\","
                + "\"code\":\"" + typeCode + "\",\"name\":\"Rail auxiliary vehicle driver\",\"kind\":\"" + kind + "\"},\"reference\":\"RD-118\","
                + "\"issuedOn\":\"2026-04-01\",\"expiresOn\":" + (expiresOn == null ? "null" : "\"" + expiresOn + "\"") + ",\"notes\":null,"
                + "\"expired\":" + expired + ",\"version\":3,\"audit\":null}";
    }

    /**
     * Las cualificaciones de una persona: la lista entera, el alta sin lo vacio y con sus fechas en ISO,
     * la modificacion (renovar es cambiar la caducidad; vaciarla es que no caduca) con la version, y
     * quitar con un DELETE. Que ya tenga ese tipo es un 409 QUA-001; la clase nueva de un tipo se lee como
     * UNKNOWN, y {@code expired} es la del servicio.
     */
    @Test
    void aPersonsQualificationsAreListedAddedRenewedAndRemoved() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String qualifications = RESOURCES + "/people/" + PERSON_ID + "/qualifications";
        server.expect(requestTo(qualifications)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + qualificationJson(QUALIFICATION_ID, "RAIL-DRIVER", "VEHICLE_OPERATION", "2027-03-31", false) + ","
                        + qualificationJson("6a7b8c9d-0000-4000-8000-0000000000a2", "FIRST-AID", "HEALTH", "2026-01-31", true) + "]",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(qualifications)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"qualificationTypeId\":\"" + QUALIFICATION_TYPE_ID + "\",\"reference\":\"RD-118\","
                        + "\"issuedOn\":\"2026-04-01\",\"expiresOn\":\"2027-03-31\"}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body(qualificationJson(QUALIFICATION_ID, "RAIL-DRIVER", "VEHICLE_OPERATION", "2027-03-31", false)));
        server.expect(requestTo(qualifications)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Person PER-000003 already has RAIL-DRIVER\","
                                + "\"errorCode\":\"QUA-001\",\"correlationId\":\"corr-q1\",\"validationErrors\":[]}"));
        server.expect(requestTo(qualifications + "/" + QUALIFICATION_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"expiresOn\":\"2028-03-31\",\"reference\":null,\"version\":3}", strict))
                .andRespond(withSuccess(qualificationJson(QUALIFICATION_ID, "RAIL-DRIVER", "VEHICLE_OPERATION", "2028-03-31", false),
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(qualifications + "/" + QUALIFICATION_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        UUID person = UUID.fromString(PERSON_ID);
        UUID qualification = UUID.fromString(QUALIFICATION_ID);
        List<PersonQualificationDto> listed = asUser(() -> personClient.qualifications(person));
        PersonQualificationRequest request = new PersonQualificationRequest(UUID.fromString(QUALIFICATION_TYPE_ID), "RD-118",
                LocalDate.of(2026, 4, 1), LocalDate.of(2027, 3, 31), null);
        asUser(() -> personClient.addQualification(person, request));
        ConflictApiException repeated = assertThrows(ConflictApiException.class, () -> asUser(() -> personClient.addQualification(person, request)));
        PersonQualificationDto renewed = asUser(() -> personClient.updateQualification(person, qualification, new MergePatch<>(
                new PersonQualificationUpdateRequest(null, null, LocalDate.of(2028, 3, 31), null), Set.of("reference"), 3L)));
        asUser(() -> {
            personClient.removeQualification(person, qualification);
            return null;
        });

        PersonQualificationDto driver = listed.getFirst();
        assertEquals("RAIL-DRIVER · Rail auxiliary vehicle driver", driver.label());
        assertEquals(QualificationKind.VEHICLE_OPERATION, driver.qualificationType().kind());
        assertEquals(LocalDate.of(2027, 3, 31), driver.expiresOn());
        assertFalse(driver.expired());
        assertEquals(QualificationKind.UNKNOWN, listed.get(1).qualificationType().kind(), "una clase nueva se lee como desconocida");
        assertTrue(listed.get(1).expired(), "si ha caducado lo dice el servicio");
        assertEquals("QUA-001", repeated.getProblem().code());
        assertEquals(LocalDate.of(2028, 3, 31), renewed.expiresOn());
        server.verify();
    }

    private static String documentJson(String id, String kind, String reference, String expiresOn, boolean expired) {
        return "{\"id\":\"" + id + "\",\"vehicleId\":\"" + VEHICLE_ID + "\",\"kind\":\"" + kind + "\",\"reference\":\"" + reference + "\","
                + "\"issuedOn\":\"2026-02-28\",\"expiresOn\":\"" + expiresOn + "\",\"notes\":null,\"expired\":" + expired + ",\"version\":1,"
                + "\"audit\":null}";
    }

    /**
     * Los documentos de un vehiculo: la lista entera, el alta sin lo vacio, la modificacion con la version
     * (renovar es cambiar la caducidad) y quitar. Uno de otro tipo que ya tiene es un 409 VDO-409; un tipo
     * nuevo se lee como UNKNOWN.
     */
    @Test
    void aVehiclesDocumentsAreListedAddedRenewedAndRemovedAndAnUnknownKindIsRead() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String documents = RESOURCES + "/vehicles/" + VEHICLE_ID + "/documents";
        server.expect(requestTo(documents)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + documentJson(DOCUMENT_ID, "ROAD_INSPECTION", "ITV-2026-118", "2027-02-28", false) + ","
                        + documentJson("6a7b8c9d-0000-4000-8000-0000000000d2", "TACHOGRAPH", "TC-9", "2026-09-30", true) + "]",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(documents)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"kind\":\"INSURANCE\",\"reference\":\"POL-77\",\"expiresOn\":\"2027-01-31\"}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body(documentJson(DOCUMENT_ID, "INSURANCE", "POL-77", "2027-01-31", false)));
        server.expect(requestTo(documents)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Vehicle DR-01 already has INSURANCE\","
                                + "\"errorCode\":\"VDO-409\",\"correlationId\":\"corr-d1\",\"validationErrors\":[]}"));
        server.expect(requestTo(documents + "/" + DOCUMENT_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"expiresOn\":\"2028-02-29\",\"notes\":null,\"version\":1}", strict))
                .andRespond(withSuccess(documentJson(DOCUMENT_ID, "ROAD_INSPECTION", "ITV-2026-118", "2028-02-29", false), MediaType.APPLICATION_JSON));
        server.expect(requestTo(documents + "/" + DOCUMENT_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        UUID vehicle = UUID.fromString(VEHICLE_ID);
        UUID document = UUID.fromString(DOCUMENT_ID);
        List<VehicleDocumentDto> listed = asUser(() -> vehicleClient.documents(vehicle));
        VehicleDocumentRequest insurance = new VehicleDocumentRequest(VehicleDocumentKind.INSURANCE, "POL-77", null, LocalDate.of(2027, 1, 31), null);
        asUser(() -> vehicleClient.addDocument(vehicle, insurance));
        ConflictApiException repeated = assertThrows(ConflictApiException.class, () -> asUser(() -> vehicleClient.addDocument(vehicle, insurance)));
        VehicleDocumentDto renewed = asUser(() -> vehicleClient.updateDocument(vehicle, document, new MergePatch<>(
                new VehicleDocumentUpdateRequest(null, null, LocalDate.of(2028, 2, 29), null), Set.of("notes"), 1L)));
        asUser(() -> {
            vehicleClient.removeDocument(vehicle, document);
            return null;
        });

        assertEquals(VehicleDocumentKind.ROAD_INSPECTION, listed.getFirst().kind());
        assertEquals("ITV-2026-118", listed.getFirst().reference());
        assertEquals(VehicleDocumentKind.UNKNOWN, listed.get(1).kind(), "un tipo nuevo se lee como desconocido");
        assertTrue(listed.get(1).expired());
        assertEquals("VDO-409", repeated.getProblem().code());
        assertEquals(LocalDate.of(2028, 2, 29), renewed.expiresOn());
        assertFalse(VehicleDocumentKind.selectable().contains(VehicleDocumentKind.UNKNOWN));
        server.verify();
    }

    private static String instrumentJson(String kind, String expiresOn, boolean expired) {
        return "{\"id\":\"" + INSTRUMENT_ID + "\",\"code\":\"PT-01\",\"name\":\"Pertiga 1\",\"kind\":\"" + kind + "\",\"serialNumber\":\"SN-5\","
                + "\"calibratedOn\":\"2026-03-31\",\"calibrationExpiresOn\":\"" + expiresOn + "\",\"calibrationExpired\":" + expired + ","
                + "\"notes\":null,\"active\":true,\"version\":5,\"audit\":null}";
    }

    /**
     * Los instrumentos: la busqueda con «calibracion caduca antes de» como fecha ISO y el id para
     * desempatar, el alta sin lo vacio, la modificacion con lo cambiado, lo vaciado y la version, retirar
     * y el historial. Un tipo nuevo se lee como UNKNOWN.
     */
    @Test
    void instrumentsAreSearchedByCalibrationDateCreatedPatchedRetiredAndHaveHistory() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String instruments = RESOURCES + "/instruments";
        server.expect(requestTo(instruments + "?search=pt&kind=EARTHING_POLE&active=true&calibrationExpiresBefore=2026-11-30"
                        + "&page=0&size=50&sort=code%2Casc&sort=id%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(instrumentJson("EARTHING_POLE", "2026-11-15", false) + "," + instrumentJson("THERMAL_CAMERA",
                        "2026-01-31", true), 0, 50, 2), MediaType.APPLICATION_JSON));
        server.expect(requestTo(instruments)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"PT-01\",\"name\":\"Pertiga 1\",\"kind\":\"EARTHING_POLE\",\"calibratedOn\":\"2026-03-31\","
                        + "\"calibrationExpiresOn\":\"2027-03-31\"}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(instrumentJson("EARTHING_POLE", "2027-03-31", false)));
        server.expect(requestTo(instruments + "/" + INSTRUMENT_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"calibrationExpiresOn\":\"2027-11-15\",\"serialNumber\":null,\"version\":5}", strict))
                .andRespond(withSuccess(instrumentJson("EARTHING_POLE", "2027-11-15", false), MediaType.APPLICATION_JSON));
        server.expect(requestTo(instruments + "/" + INSTRUMENT_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        server.expect(requestTo(instruments + "/" + INSTRUMENT_ID + "/revisions?page=0&size=20")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(maintenanceRevision(3, "UPDATED", "HTTP", instrumentJson("EARTHING_POLE", "2027-11-15", false)),
                        0, 20, 1), MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(INSTRUMENT_ID);
        PageResponse<InstrumentDto> page = asUser(() -> instrumentClient.search(new InstrumentFilter(" pt ", InstrumentKind.EARTHING_POLE, true,
                LocalDate.of(2026, 11, 30)), 0, 50, List.of("code,asc")));
        asUser(() -> instrumentClient.create(new InstrumentRequest("PT-01", "Pertiga 1", InstrumentKind.EARTHING_POLE, null, LocalDate.of(2026, 3, 31),
                LocalDate.of(2027, 3, 31), null)));
        asUser(() -> instrumentClient.update(id, new MergePatch<>(new InstrumentUpdateRequest(null, null, null, null, LocalDate.of(2027, 11, 15),
                null, null), Set.of("serialNumber"), 5L)));
        asUser(() -> {
            instrumentClient.deactivate(id);
            return null;
        });
        PageResponse<RevisionDto<InstrumentDto>> revisions = asUser(() -> instrumentClient.revisions(id, 0, 20));

        InstrumentDto pole = page.content().getFirst();
        assertEquals("PT-01 · Pertiga 1", pole.label());
        assertEquals(LocalDate.of(2026, 11, 15), pole.calibrationExpiresOn());
        assertFalse(pole.calibrationExpired());
        assertEquals(new InstrumentSummaryDto(id, "PT-01", "Pertiga 1", InstrumentKind.EARTHING_POLE, LocalDate.of(2026, 11, 15)), pole.summary());
        assertEquals(InstrumentKind.UNKNOWN, page.content().get(1).kind(), "un tipo nuevo se lee como desconocido");
        assertTrue(page.content().get(1).calibrationExpired(), "si la calibracion caduco lo dice el servicio");
        assertEquals(LocalDate.of(2027, 11, 15), revisions.content().getFirst().entity().calibrationExpiresOn());
        server.verify();
    }

    /**
     * Un modelo dice que habilitacion exige a su conductor: se lee como un resumen del tipo, viaja como su
     * id en el alta y se vacia con null en la modificacion (no exige ninguna).
     */
    @Test
    void aVehicleModelCarriesTheQualificationItsDriverNeeds() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String withDriver = vehicleTypeJson("ROAD_RAIL_TRUCK", "[]").replace("\"audit\":null}", "\"audit\":null,"
                + "\"requiredDriverQualification\":{\"id\":\"" + QUALIFICATION_TYPE_ID + "\",\"code\":\"RR-OPERATOR\","
                + "\"name\":\"Road-rail machine operator\",\"kind\":\"VEHICLE_OPERATION\"}}");
        server.expect(requestTo(RESOURCES + "/vehicle-types/" + VEHICLE_TYPE_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(withDriver, MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicle-types")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"code\":\"DR-9\",\"name\":\"Dresina\",\"mobility\":\"RAIL_ONLY\",\"category\":\"CATENARY_TOWER_CAR\","
                        + "\"selfPropelled\":true,\"portable\":false,\"requiredDriverQualificationTypeId\":\"" + QUALIFICATION_TYPE_ID + "\"}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(withDriver));
        server.expect(requestTo(RESOURCES + "/vehicle-types/" + VEHICLE_TYPE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"requiredDriverQualificationTypeId\":null,\"version\":4}", strict))
                .andRespond(withSuccess(vehicleTypeJson("ROAD_RAIL_TRUCK", "[]"), MediaType.APPLICATION_JSON));

        VehicleTypeDto read = asUser(() -> vehicleTypeClient.findById(UUID.fromString(VEHICLE_TYPE_ID)));
        asUser(() -> vehicleTypeClient.create(new VehicleTypeRequest("DR-9", "Dresina", null, null, VehicleMobility.RAIL_ONLY,
                VehicleCategory.CATENARY_TOWER_CAR, true, false, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, Set.of(), null, null, null, null, null, null, null,
                UUID.fromString(QUALIFICATION_TYPE_ID))));
        VehicleTypeDto cleared = asUser(() -> vehicleTypeClient.update(UUID.fromString(VEHICLE_TYPE_ID), new MergePatch<>(
                new VehicleTypeUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
                Set.of("requiredDriverQualificationTypeId"), 4L)));

        assertEquals("RR-OPERATOR · Road-rail machine operator", read.requiredDriverQualification().label());
        assertEquals(QualificationKind.VEHICLE_OPERATION, read.requiredDriverQualification().kind());
        assertNull(cleared.requiredDriverQualification(), "un modelo de antes, o que no exige ninguna, la lee nula");
        server.verify();
    }

    /**
     * Un turno lleva sus instrumentos de mto-resources: viajan sus ids en el alta, quitarlos todos viaja a
     * null en el merge-patch, y la respuesta (tambien la del parte) los trae por codigo con su calibracion;
     * un tipo nuevo se lee como UNKNOWN. Un instrumento que aun no ha llegado a mantenimiento es un 404
     * MSI-404.
     */
    @Test
    void aShiftCarriesItsInstrumentsAndOneNotYetInMaintenanceIsANotFound() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String shifts = MAINTENANCE + "/shifts";
        String other = "6a7b8c9d-0000-4000-8000-0000000000e2";
        String withInstruments = shiftJson("PLANNED").replace("\"audit\":null}", "\"audit\":null,\"version\":2,\"instruments\":["
                + "{\"id\":\"" + INSTRUMENT_ID + "\",\"code\":\"PT-01\",\"name\":\"Pertiga 1\",\"kind\":\"EARTHING_POLE\","
                + "\"calibrationExpiresOn\":\"2027-03-31\"},{\"id\":\"" + other + "\",\"code\":\"TC-01\",\"name\":null,\"kind\":\"THERMAL_CAMERA\","
                + "\"calibrationExpiresOn\":null}]}");
        server.expect(requestTo(shifts)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"shiftDate\":\"2026-10-05\",\"possessionType\":\"PARTIAL\",\"trackIds\":[12],"
                        + "\"instrumentIds\":[\"" + INSTRUMENT_ID + "\"]}", strict))
                .andRespond(withSuccess(withInstruments, MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"instrumentIds\":null,\"version\":2}", strict))
                .andRespond(withSuccess(shiftJson("PLANNED"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(shifts + "/" + SHIFT_ID)).andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":404,\"error\":\"NOT_FOUND\",\"message\":\"Maintenance instrument " + other + " not found\","
                                + "\"errorCode\":\"MSI-404\",\"correlationId\":\"corr-i1\",\"validationErrors\":[]}"));
        server.expect(requestTo(shifts + "/" + SHIFT_ID + "/report")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"shift\":" + withInstruments + ",\"tasksCompleted\":0,\"tasksPending\":0,\"profilesReviewed\":0,"
                        + "\"defectsFound\":0,\"defectsResolved\":0,\"rows\":[],\"inspectionsRecorded\":0,\"inspections\":[]}", MediaType.APPLICATION_JSON));

        UUID id = UUID.fromString(SHIFT_ID);
        ShiftDto created = asUser(() -> shiftClient.create(new ShiftRequest(LocalDate.of(2026, 10, 5), null, null, null, PossessionType.PARTIAL,
                null, null, null, null, null, null, Set.of(12L), null, null, null, null, null, null, null, null, null,
                Set.of(UUID.fromString(INSTRUMENT_ID)))));
        ShiftUpdateRequest nothingElse = new ShiftUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null);
        ShiftDto emptied = asUser(() -> shiftClient.update(id, new MergePatch<>(nothingElse, Set.of("instrumentIds"), 2L)));
        NotFoundApiException missing = assertThrows(NotFoundApiException.class, () -> asUser(() -> shiftClient.update(id,
                new MergePatch<>(new ShiftUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, Set.of(UUID.fromString(other))), Set.of(), 2L))));
        ShiftReportDto report = asUser(() -> reportClient.shiftReport(id));

        assertEquals(List.of("PT-01", "TC-01"), created.instruments().stream().map(InstrumentSummaryDto::code).toList());
        assertEquals(new InstrumentSummaryDto(UUID.fromString(INSTRUMENT_ID), "PT-01", "Pertiga 1", InstrumentKind.EARTHING_POLE,
                LocalDate.of(2027, 3, 31)), created.instruments().getFirst());
        assertEquals(LocalDate.of(2027, 3, 31), created.instruments().getFirst().calibrationExpiresOn());
        assertEquals(InstrumentKind.UNKNOWN, created.instruments().get(1).kind(), "un tipo nuevo no rompe el turno");
        assertEquals("TC-01", created.instruments().get(1).label());
        assertTrue(emptied.instruments().isEmpty(), "un turno sin instrumentos, o de un servicio anterior, trae la lista vacia");
        assertEquals("MSI-404", missing.getProblem().code());
        assertEquals(2, report.shift().instruments().size(), "el parte los lleva con su turno");
        server.verify();
    }

    private static final String ABSENCE_ID = "7b8c9d0e-0000-4000-8000-0000000000a1";
    private static final String REPAIR_ID = "7b8c9d0e-0000-4000-8000-0000000000b1";

    private static String absenceJson(String id, String kind, String startsOn, String endsOn, String notes) {
        return "{\"id\":\"" + id + "\",\"personId\":\"" + PERSON_ID + "\",\"kind\":\"" + kind + "\",\"startsOn\":\"" + startsOn + "\","
                + "\"endsOn\":\"" + endsOn + "\",\"notes\":" + (notes == null ? "null" : "\"" + notes + "\"") + ",\"version\":2,\"audit\":null}";
    }

    /**
     * Las ausencias de una persona: la lista entera, el alta sin lo vacio y con sus fechas en ISO, la
     * modificacion con lo cambiado, las notas vaciadas a {@code null} y la version, y quitar con un DELETE.
     * Una que se solapa con otra suya es un 409 ABS-001; una clase nueva se lee como UNKNOWN. Los dias
     * cuentan los dos extremos.
     */
    @Test
    void aPersonsAbsencesAreListedAddedPatchedAndRemovedAndAnOverlapIsAConflict() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String absences = RESOURCES + "/people/" + PERSON_ID + "/absences";
        server.expect(requestTo(absences)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + absenceJson(ABSENCE_ID, "VACATION", "2026-11-02", "2026-11-06", "Verano") + ","
                        + absenceJson("7b8c9d0e-0000-4000-8000-0000000000a2", "SABBATICAL", "2026-12-01", "2026-12-31", null) + "]",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(absences)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"kind\":\"TRAINING\",\"startsOn\":\"2026-11-16\",\"endsOn\":\"2026-11-18\"}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body(absenceJson(ABSENCE_ID, "TRAINING", "2026-11-16", "2026-11-18", null)));
        server.expect(requestTo(absences)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"PER-000003 is already absent from 2026-11-02 to 2026-11-06\","
                                + "\"errorCode\":\"ABS-001\",\"correlationId\":\"corr-a1\",\"validationErrors\":[]}"));
        server.expect(requestTo(absences + "/" + ABSENCE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"endsOn\":\"2026-11-09\",\"notes\":null,\"version\":2}", strict))
                .andRespond(withSuccess(absenceJson(ABSENCE_ID, "VACATION", "2026-11-02", "2026-11-09", null), MediaType.APPLICATION_JSON));
        server.expect(requestTo(absences + "/" + ABSENCE_ID)).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        UUID person = UUID.fromString(PERSON_ID);
        UUID absence = UUID.fromString(ABSENCE_ID);
        List<PersonAbsenceDto> listed = asUser(() -> personClient.absences(person));
        PersonAbsenceRequest training = new PersonAbsenceRequest(AbsenceKind.TRAINING, LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 18), null);
        asUser(() -> personClient.addAbsence(person, training));
        ConflictApiException overlap = assertThrows(ConflictApiException.class, () -> asUser(() -> personClient.addAbsence(person, training)));
        PersonAbsenceDto extended = asUser(() -> personClient.updateAbsence(person, absence, new MergePatch<>(
                new PersonAbsenceUpdateRequest(null, null, LocalDate.of(2026, 11, 9), null), Set.of("notes"), 2L)));
        asUser(() -> {
            personClient.removeAbsence(person, absence);
            return null;
        });

        assertEquals(AbsenceKind.VACATION, listed.getFirst().kind());
        assertEquals(5, listed.getFirst().days(), "del lunes al viernes, los dos incluidos");
        assertEquals("Verano", listed.getFirst().notes());
        assertEquals(AbsenceKind.UNKNOWN, listed.get(1).kind(), "una clase nueva se lee como desconocida");
        assertFalse(AbsenceKind.selectable().contains(AbsenceKind.UNKNOWN));
        assertEquals("ABS-001", overlap.getProblem().code());
        assertEquals(LocalDate.of(2026, 11, 9), extended.endsOn());
        assertNull(extended.notes());
        server.verify();
    }

    private static String repairJson(String status, String kind, boolean immobilises, String workshop, String cost) {
        return "{\"id\":\"" + REPAIR_ID + "\",\"code\":\"REP-000004\",\"vehicleId\":\"" + VEHICLE_ID + "\",\"kind\":\"" + kind + "\","
                + "\"status\":\"" + status + "\",\"immobilises\":" + immobilises + ",\"description\":\"Fuga en el circuito hidraulico\","
                + "\"workshop\":" + (workshop == null ? "null" : "\"" + workshop + "\"") + ",\"plannedFrom\":\"2026-11-03\",\"plannedTo\":\"2026-11-05\","
                + "\"openedAt\":" + ("PLANNED".equals(status) ? "null" : "\"2026-11-03T06:00:00Z\"") + ",\"closedAt\":null,\"odometerKm\":12000,"
                + "\"engineHours\":3500.5,\"cost\":" + cost + ",\"notes\":null,\"version\":1,\"audit\":null}";
    }

    /**
     * El taller de un vehiculo: la lista entera, el alta (planificada, con su inicio) sin lo vacio, la
     * modificacion con lo cambiado, lo vaciado y la version, y abrir, cerrar y cancelar como POST sin cuerpo.
     * Lo que su estado no admite es un 409 REP-001; un estado y una clase nuevos se leen como UNKNOWN, que
     * no admiten nada.
     */
    @Test
    void aVehiclesRepairsAreListedAddedPatchedAndMovedThroughTheirStatesWithoutABody() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String repairs = RESOURCES + "/vehicles/" + VEHICLE_ID + "/repairs";
        server.expect(requestTo(repairs)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + repairJson("PLANNED", "BREAKDOWN", true, "Taller de Lod", "null") + ","
                        + repairJson("ON_HOLD", "WARRANTY", false, null, "120.50") + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(repairs)).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"kind\":\"BREAKDOWN\",\"status\":\"PLANNED\",\"immobilises\":true,"
                        + "\"description\":\"Fuga en el circuito hidraulico\",\"workshop\":\"Taller de Lod\",\"plannedFrom\":\"2026-11-03\","
                        + "\"plannedTo\":\"2026-11-05\",\"engineHours\":3500.5}", strict))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body(repairJson("PLANNED", "BREAKDOWN", true, "Taller de Lod", "null")));
        server.expect(requestTo(repairs + "/" + REPAIR_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"cost\":980.25,\"workshop\":null,\"version\":1}", strict))
                .andRespond(withSuccess(repairJson("PLANNED", "BREAKDOWN", true, null, "980.25"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(repairs + "/" + REPAIR_ID + "/open")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(repairJson("OPEN", "BREAKDOWN", true, null, "980.25"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(repairs + "/" + REPAIR_ID + "/close")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Repair REP-000004 is PLANNED, not OPEN\","
                                + "\"errorCode\":\"REP-001\",\"correlationId\":\"corr-r1\",\"validationErrors\":[]}"));
        server.expect(requestTo(repairs + "/" + REPAIR_ID + "/cancel")).andExpect(method(HttpMethod.POST))
                .andExpect(content().string(""))
                .andRespond(withSuccess(repairJson("CANCELLED", "BREAKDOWN", true, null, "980.25"), MediaType.APPLICATION_JSON));

        UUID vehicle = UUID.fromString(VEHICLE_ID);
        UUID repair = UUID.fromString(REPAIR_ID);
        List<VehicleRepairDto> listed = asUser(() -> vehicleClient.repairs(vehicle));
        asUser(() -> vehicleClient.addRepair(vehicle, new VehicleRepairRequest(RepairKind.BREAKDOWN, RepairStatus.PLANNED, true,
                "Fuga en el circuito hidraulico", "Taller de Lod", LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 5), null,
                new BigDecimal("3500.5"), null, null)));
        VehicleRepairDto patched = asUser(() -> vehicleClient.updateRepair(vehicle, repair, new MergePatch<>(new VehicleRepairUpdateRequest(null,
                null, null, null, null, null, null, null, new BigDecimal("980.25"), null), Set.of("workshop"), 1L)));
        VehicleRepairDto opened = asUser(() -> vehicleClient.openRepair(vehicle, repair));
        ConflictApiException refused = assertThrows(ConflictApiException.class, () -> asUser(() -> vehicleClient.closeRepair(vehicle, repair)));
        VehicleRepairDto cancelled = asUser(() -> vehicleClient.cancelRepair(vehicle, repair));

        VehicleRepairDto planned = listed.getFirst();
        assertEquals("REP-000004", planned.code());
        assertEquals(RepairStatus.PLANNED, planned.state());
        assertTrue(planned.isImmobilising());
        assertTrue(planned.state().canOpen() && planned.state().canCancel() && !planned.state().canClose());
        assertEquals(RepairStatus.UNKNOWN, listed.get(1).state(), "un estado nuevo se lee como desconocido");
        assertEquals(RepairKind.UNKNOWN, listed.get(1).kind(), "una clase nueva tambien");
        assertFalse(listed.get(1).state().isChangeable() || listed.get(1).state().canCancel(), "y no admite nada");
        assertEquals(0, new BigDecimal("980.25").compareTo(patched.cost()));
        assertNull(patched.workshop());
        assertEquals(RepairStatus.OPEN, opened.state());
        assertTrue(opened.state().canClose() && !opened.state().canOpen());
        assertEquals("REP-001", refused.getProblem().code());
        assertEquals(RepairStatus.CANCELLED, cancelled.state());
        assertTrue(cancelled.state().isChangeable() && !cancelled.state().datesChange() && !cancelled.state().canCancel());
        assertFalse(RepairStatus.selectable().contains(RepairStatus.UNKNOWN));
        server.verify();
    }

    /**
     * El dispositivo GPS de un vehiculo viaja en el alta y en la modificacion, y vaciarlo es {@code null}
     * en el merge-patch. Una posicion que llega por el GPS tiene su origen, {@code TELEMATICS}, que se
     * llama «GPS».
     */
    @Test
    void aVehicleCarriesItsGpsDeviceAndEmptyingItTravelsAsNull() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        server.expect(requestTo(RESOURCES + "/vehicles/" + VEHICLE_ID)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"id\":\"" + VEHICLE_ID + "\",\"code\":\"FG-01\",\"telematicsDeviceId\":\"GPS-118\",\"active\":true,"
                        + "\"version\":6}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(RESOURCES + "/vehicles")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.telematicsDeviceId").value("GPS-119"))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\":\"" + VEHICLE_ID + "\",\"code\":\"FG-02\",\"telematicsDeviceId\":\"GPS-119\",\"version\":0}"));
        server.expect(requestTo(RESOURCES + "/vehicles/" + VEHICLE_ID)).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"telematicsDeviceId\":null,\"version\":6}", strict))
                .andRespond(withSuccess("{\"id\":\"" + VEHICLE_ID + "\",\"code\":\"FG-01\",\"telematicsDeviceId\":null,\"version\":7}",
                        MediaType.APPLICATION_JSON));

        VehicleDto read = asUser(() -> vehicleClient.findById(UUID.fromString(VEHICLE_ID)));
        VehicleDto created = asUser(() -> vehicleClient.create(new VehicleRequest("FG-02", null, UUID.fromString(VEHICLE_TYPE_ID), "4321-XYZ", null,
                null, null, VehicleOwner.OWN, null, VehicleOperationalStatus.OPERATIONAL, null, null, null, null, "GPS-119")));
        VehicleDto emptied = asUser(() -> vehicleClient.update(UUID.fromString(VEHICLE_ID), new MergePatch<>(new VehicleUpdateRequest(null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null), Set.of("telematicsDeviceId"), 6L)));

        assertEquals("GPS-118", read.telematicsDeviceId());
        assertEquals("GPS-119", created.telematicsDeviceId());
        assertNull(emptied.telematicsDeviceId());
        assertEquals("GPS", VehicleLocationSource.TELEMATICS.label());
        assertEquals(VehicleLocationSource.TELEMATICS, VehicleLocationSource.of("TELEMATICS"));
        server.verify();
    }

    private static String discrepancyJson(String personId, String code, String username, String category, String expected, String profiles) {
        return "{\"personId\":" + (personId == null ? "null" : "\"" + personId + "\"") + ",\"personCode\":" + (code == null ? "null" : "\"" + code + "\"")
                + ",\"fullName\":" + (code == null ? "null" : "\"Yossi Cohen\"") + ",\"username\":\"" + username + "\",\"category\":"
                + (category == null ? "null" : "\"" + category + "\"") + ",\"personActive\":" + (personId != null) + ",\"expectedProfile\":"
                + (expected == null ? "null" : "\"" + expected + "\"") + ",\"profiles\":" + profiles + "}";
    }

    /**
     * El informe de puestos y perfiles de mto-resources: sus cifras y sus cuatro listas (una que no llega se
     * lee vacia; una persona sin ficha, sin id; una categoria nueva, como desconocida). Con Keycloak caido es
     * un 503 KCK-503, que es transitorio.
     */
    @Test
    void thePositionDiscrepancyReportIsReadAndKeycloakDownIsATransientUnavailable() {
        String report = RESOURCES + "/reports/position-discrepancies";
        server.expect(requestTo(report)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"generatedAt\":\"2026-10-19T08:00:00Z\",\"peopleChecked\":42,\"usersWithPosition\":37,"
                        + "\"wrongProfile\":[" + discrepancyJson(PERSON_ID, "PER-000003", "y.cohen", "TEAM_LEADER", "mto-position-team-leader",
                        "[\"mto-position-worker\"]") + "],"
                        + "\"withoutProfile\":[" + discrepancyJson("3c4d5e6f-0000-4000-8000-000000000002", "PER-000004", "d.levi", "APPRENTICE",
                        null, "[]") + "],"
                        + "\"withoutRecord\":[" + discrepancyJson(null, null, "ext.contractor", null, null, "[\"mto-position-supervisor\"]") + "]}",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(report)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":503,\"error\":\"SERVICE_UNAVAILABLE\",\"message\":\"Keycloak cannot be read now\","
                                + "\"errorCode\":\"KCK-503\",\"correlationId\":\"corr-k1\",\"validationErrors\":[]}"));

        PositionDiscrepancyReportDto read = asUser(() -> resourcesReportClient.positionDiscrepancies());
        ServiceUnavailableApiException down = assertThrows(ServiceUnavailableApiException.class,
                () -> asUser(() -> resourcesReportClient.positionDiscrepancies()));

        assertEquals(java.time.Instant.parse("2026-10-19T08:00:00Z"), read.generatedAt());
        assertEquals(42, read.peopleChecked());
        assertEquals(37, read.usersWithPosition());
        PositionDiscrepancyDto wrong = read.wrongProfile().getFirst();
        assertEquals(UUID.fromString(PERSON_ID), wrong.personId());
        assertEquals("PER-000003 · Yossi Cohen", wrong.personLabel());
        assertEquals(PersonCategory.TEAM_LEADER, wrong.category());
        assertEquals(List.of("mto-position-worker"), wrong.profiles());
        assertEquals(List.of(), read.severalProfiles(), "una lista que no llega se lee vacia");
        assertEquals(PersonCategory.UNKNOWN, read.withoutProfile().getFirst().category(), "una categoria nueva no rompe el informe");
        PositionDiscrepancyDto withoutRecord = read.withoutRecord().getFirst();
        assertNull(withoutRecord.personId());
        assertEquals("", withoutRecord.personLabel());
        assertEquals("ext.contractor", withoutRecord.username());
        assertEquals("KCK-503", down.getProblem().code());
        assertEquals(503, down.getStatus().value());
        server.verify();
    }

    /**
     * El equipo que exige un tipo de tarea llega con sus nombres tal cual (uno que esta aplicacion no
     * conoce, tambien) y se cambia con un merge-patch por su codigo, con la version: el conjunto entero, o
     * {@code null} para no exigir ninguno. Una version vieja es un 409 CON-001. Uno de antes, sin la clave,
     * no exige nada.
     */
    @Test
    void aTaskTypeCarriesItsRequiredEquipmentAndIsPatchedByCodeWithItsVersion() {
        org.springframework.test.json.JsonCompareMode strict = org.springframework.test.json.JsonCompareMode.STRICT;
        String type = "{\"id\":\"2b3c4d5e-0000-4000-8000-000000000020\",\"code\":\"RG-04\",\"description\":\"Revision del hilo de contacto\","
                + "\"functionalGroup\":\"OVERHEAD_CONDUCTORS\",\"standardMinutesPerUnit\":12.50,\"unit\":\"SPAN\",\"fixedMinutes\":null,"
                + "\"requiresFullPossession\":true,\"diagnostic\":false,\"active\":true,\"orderIndex\":4,";
        server.expect(requestTo(MAINTENANCE + "/task-types")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + type + "\"requiredEquipment\":[\"INSULATED_PLATFORM\",\"SAFETY_HARNESS\"],\"version\":3},"
                        + type.replace("RG-04", "RG-05") + "\"version\":0}]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/task-types/RG-04")).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentTypeCompatibleWith(MergePatch.MEDIA_TYPE))
                .andExpect(content().json("{\"requiredEquipment\":[\"EARTHING\",\"SAFETY_HARNESS\"],\"version\":3}", strict))
                .andRespond(withSuccess(type + "\"requiredEquipment\":[\"EARTHING\",\"SAFETY_HARNESS\"],\"version\":4}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/task-types/RG-04")).andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json("{\"requiredEquipment\":null,\"version\":4}", strict))
                .andRespond(withSuccess(type + "\"requiredEquipment\":[],\"version\":5}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/task-types/RG-04")).andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON)
                        .body("{\"status\":409,\"error\":\"CONFLICT\",\"message\":\"Stale version 3 of RG-04\","
                                + "\"errorCode\":\"CON-001\",\"correlationId\":\"corr-t1\",\"validationErrors\":[]}"));

        List<TaskTypeDto> types = asUser(() -> maintenanceCatalogClient.taskTypes(null, null, null));
        TaskTypeDto earthing = asUser(() -> maintenanceCatalogClient.updateTaskType("RG-04", new MergePatch<>(new TaskTypeUpdateRequest(
                new java.util.TreeSet<>(Set.of("EARTHING", "SAFETY_HARNESS"))), Set.of(), 3L)));
        TaskTypeDto none = asUser(() -> maintenanceCatalogClient.updateTaskType("RG-04", new MergePatch<>(new TaskTypeUpdateRequest(null),
                Set.of("requiredEquipment"), 4L)));
        ConflictApiException stale = assertThrows(ConflictApiException.class, () -> asUser(() -> maintenanceCatalogClient.updateTaskType("RG-04",
                new MergePatch<>(new TaskTypeUpdateRequest(null), Set.of("requiredEquipment"), 3L))));

        assertEquals(Set.of("INSULATED_PLATFORM", "SAFETY_HARNESS"), types.getFirst().requiredEquipment());
        assertEquals(3L, types.getFirst().version());
        assertEquals(Set.of(), types.get(1).requiredEquipment(), "uno de antes, sin la clave, no exige nada");
        assertEquals("Plataforma aislada", TaskEquipment.labelOf("INSULATED_PLATFORM"));
        assertEquals("SAFETY_HARNESS", TaskEquipment.labelOf("SAFETY_HARNESS"), "uno desconocido se nombra tal cual llego");
        assertEquals(Set.of("EARTHING", "SAFETY_HARNESS"), earthing.requiredEquipment());
        assertEquals(Set.of(), none.requiredEquipment());
        assertEquals("CON-001", stale.getProblem().code());
        server.verify();
    }
}

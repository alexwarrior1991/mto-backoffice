package com.alejandro.mtobackoffice.ui;

import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationOutcome;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationPreviewLineDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerationSelection;
import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.DisconnectorClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.JobsClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.SectionInsulatorClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackAccessPointClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageMetadata;
import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobItemError;
import com.alejandro.mtobackoffice.client.dto.jobs.JobStatus;
import com.alejandro.mtobackoffice.client.dto.jobs.JobType;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationErrorDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftVehicleDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.VehicleParkingRequest;
import com.alejandro.mtobackoffice.client.dto.master.ExecutionPackageDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
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
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteFilter;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteSummaryDto;
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
import com.alejandro.mtobackoffice.client.dto.resources.UnitSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleAttachment;
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
import com.alejandro.mtobackoffice.client.dto.resources.VehiclePower;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.WorkDiscipline;
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
import com.alejandro.mtobackoffice.client.maintenance.OperationClient;
import com.alejandro.mtobackoffice.client.resources.InstrumentClient;
import com.alejandro.mtobackoffice.client.resources.OrganizationClient;
import com.alejandro.mtobackoffice.client.resources.ParkingSiteClient;
import com.alejandro.mtobackoffice.client.resources.PersonClient;
import com.alejandro.mtobackoffice.client.resources.QualificationTypeClient;
import com.alejandro.mtobackoffice.client.resources.AccessPointClient;
import com.alejandro.mtobackoffice.client.resources.VehicleClient;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointKind;
import com.alejandro.mtobackoffice.client.dto.master.TrackAccessPointSide;
import com.alejandro.mtobackoffice.client.dto.resources.AccessPointDto;
import com.alejandro.mtobackoffice.client.dto.resources.AccessPointKind;
import com.alejandro.mtobackoffice.client.dto.resources.AccessPointSummaryDto;
import com.alejandro.mtobackoffice.ui.master.TrackAccessPointsView;
import com.alejandro.mtobackoffice.client.resources.VehicleTypeClient;
import com.alejandro.mtobackoffice.ui.maintenance.TaskGeneration;
import com.alejandro.mtobackoffice.ui.resources.Expiry;
import com.alejandro.mtobackoffice.ui.resources.FleetBoardView;
import com.alejandro.mtobackoffice.ui.resources.InstrumentEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.InstrumentsView;
import com.alejandro.mtobackoffice.ui.resources.OrganizationView;
import com.alejandro.mtobackoffice.ui.resources.ParkingSiteEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.ParkingSitesView;
import com.alejandro.mtobackoffice.ui.resources.PeopleView;
import com.alejandro.mtobackoffice.ui.resources.PersonDetailView;
import com.alejandro.mtobackoffice.ui.resources.PersonEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.PersonQualificationDialog;
import com.alejandro.mtobackoffice.ui.resources.QualificationTypeEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.QualificationTypesView;
import com.alejandro.mtobackoffice.ui.resources.ResourcesRoutes;
import com.alejandro.mtobackoffice.ui.resources.TeamMembersDialog;
import com.alejandro.mtobackoffice.ui.resources.UnitDetailView;
import com.alejandro.mtobackoffice.ui.resources.UnitEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.VehicleDetailView;
import com.alejandro.mtobackoffice.ui.resources.VehicleDocumentDialog;
import com.alejandro.mtobackoffice.ui.resources.VehicleEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.VehicleLocationDialog;
import com.alejandro.mtobackoffice.ui.resources.VehicleTypeEditorDialog;
import com.alejandro.mtobackoffice.ui.resources.VehicleTypesView;
import com.alejandro.mtobackoffice.ui.resources.VehiclesView;
import com.alejandro.mtobackoffice.ui.support.RevisionsDialog;
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
import com.alejandro.mtobackoffice.ui.support.SharedPolling;
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
import com.alejandro.mtobackoffice.ui.master.ProfileEditor;
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
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportInspectionDto;
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
import com.alejandro.mtobackoffice.client.dto.MergePatch;
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
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeUpdateRequest;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.mock.env.MockEnvironment;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
import com.alejandro.mtobackoffice.client.dto.master.DisconnectorDriveType;
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
import java.util.Arrays;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
import com.alejandro.mtobackoffice.ui.maintenance.TaskTypeEquipmentDialog;
import com.alejandro.mtobackoffice.ui.master.SchematicDrawing;
import com.alejandro.mtobackoffice.ui.master.TrackSchematicDialog;
import com.alejandro.mtobackoffice.ui.resources.PersonAbsenceDialog;
import com.alejandro.mtobackoffice.ui.resources.VehicleRepairDialog;
import java.util.Objects;
import com.vaadin.flow.router.Route;
import tools.jackson.databind.ObjectMapper;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.EnumSet;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.Svg;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

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
    @Autowired
    private ObjectMapper jsonMapper;

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
    private TrackAccessPointClient trackAccessPointClient;
    @MockitoBean
    private AccessPointClient accessPointClient;
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
    @MockitoBean
    private PersonClient personClient;
    @MockitoBean
    private OrganizationClient organizationClient;
    @MockitoBean
    private VehicleTypeClient vehicleTypeClient;
    @MockitoBean
    private ParkingSiteClient parkingSiteClient;
    @MockitoBean
    private VehicleClient vehicleClient;
    @MockitoBean
    private OperationClient operationClient;
    @MockitoBean
    private QualificationTypeClient qualificationTypeClient;
    @MockitoBean
    private InstrumentClient instrumentClient;
    @MockitoBean
    private ResourcesReportClient resourcesReportClient;

    @BeforeEach
    void setUp() {
        stubEmptyMasters();
        // Generar de verdad va con Prefer: respond-async; sin decir otra cosa, el servicio termina dentro de
        // su espera y contesta 200 con lo mismo que la llamada sincrona (que es lo que stubbea cada caso).
        when(orderClient.generateTasksAsync(any(), any(), any())).thenAnswer(call -> ResponseEntity.ok(
                jsonMapper.valueToTree(orderClient.generateTasks(call.getArgument(0), call.getArgument(1)))));
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
    void theMenuGroupsTheSevenMastersUnderInfrastructure() {
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
    void theSpaLinkLeavesOutTheContinueThatSpringSecurityAddsAfterTheLogin() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        when(lovClient.findAll(PROFILE_STATUSES)).thenReturn(threeStatuses());

        // Tras entrar por el SSO, Spring Security vuelve a la URL pedida con su marca "continue": es
        // suya, no de la pantalla, y la SPA no tiene por que recibirla.
        UI.getCurrent().navigate(CATALOGUE_ROUTE, QueryParameters.fromString("q=borrador&continue"));

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
        assertEquals(8, audiences.size());
        long present = audiences.stream().filter(item -> "true".equals(item.getElement().getAttribute("data-present"))).count();
        assertEquals(4, present);
        assertTrue(audiences.stream().anyMatch(item -> "mto-notification-api".equals(item.getElement().getAttribute("data-audience"))
                && "false".equals(item.getElement().getAttribute("data-present"))));
        assertTrue(audiences.stream().anyMatch(item -> "mto-resources-api".equals(item.getElement().getAttribute("data-audience"))
                && "false".equals(item.getElement().getAttribute("data-present"))));
        assertTrue(audiences.stream().anyMatch(item -> "mto-field-api".equals(item.getElement().getAttribute("data-audience"))
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
        when(trackAccessPointClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.<TrackAccessPointDto>of(), 0, 50));
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
        when(qualificationTypeClient.search(any(QualificationTypeFilter.class), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.<QualificationTypeDto>of(), 0, 50));
        when(instrumentClient.search(any(InstrumentFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.<InstrumentDto>of(), 0, 50));
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
        // Cuelga de un poste de VIA 1 y une otras dos: un haz de vias.
        var disconnector = new TrackSchematicDto.DisconnectorMark(40L, "SEC-40", true, "FEED", "ATOCHA", "VIA 3", "VIA 2");
        var p1 = new TrackSchematicDto.ProfileNode(1L, "P-001", "10.000", 1, "55.000", "HEB", null, "OK", "-2.500",
                List.of("S1"), List.of(arm), null, List.of());
        // Solo `disconnector`, como un servicio anterior al V29: se lee como una lista de uno.
        var p2 = new TrackSchematicDto.ProfileNode(2L, "P-002", "20.000", 2, null, null, null, null, "2.500",
                List.of(), List.of(), disconnector, null);
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
        assertTrue(svg.contains("id=\"disconnector-40\"") && svg.contains(">SEC-40<") && svg.contains(">ATOCHA · maniobra VIA 3<"),
                "el seccionador sobre su poste, con su estacion y la via que maniobra, que no es la del poste");
        assertTrue(svg.contains("Seccionador SEC-40 · en carga · funcion FEED · estacion ATOCHA · maniobra la via VIA 3 · en paralelo con VIA 2"),
                "la via que maniobra y la otra de uno que pone dos en paralelo, en su titulo");
        assertTrue(svg.contains("id=\"insulator-50\"") && svg.contains(">AIS-50<"), "el aislador sobre la via");
        assertTrue(svg.contains(">conexion de vias · ↔ VIA 2 · ATOCHA<") && svg.contains(">W31 1:9<"), "la otra via, la estacion y las agujas del aislador");
        assertTrue(LocatorJ._find(dialog, Span.class, spec -> spec.withId(TrackSchematicDialog.EMPTY_ID)).isEmpty());
    }

    @Test
    void whatTheServiceSendsIsEscapedBeforeItBecomesSvg() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        var first = new TrackSchematicDto.DisconnectorMark(1L, "\"SEC\" & co", null, null, null, "<i>", null);
        var second = new TrackSchematicDto.DisconnectorMark(2L, "<u>SEC-2</u>", null, null, "<s>", null, null);
        var evil = new TrackSchematicDto.ProfileNode(9L, "<script>P</script>", "1.000", 1, null, null, null, null, null,
                List.of("<b>"), List.of(), first, List.of(first, second));
        when(trackClient.schematic(3L)).thenReturn(new TrackSchematicDto(3L, "VIA \"1\" & <b>", true, null, List.of(), List.of(evil), List.of()));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(schematicButton(0, 3L));

        Dialog dialog = LocatorJ._get(Dialog.class);
        assertEquals("Esquema · VIA \"1\" & <b>", dialog.getHeaderTitle(), "la cabecera es texto: Vaadin la escapa sola");
        String svg = drawing(dialog);
        assertFalse(svg.contains("<script>") || svg.contains("<b>") || svg.contains("<i>"), svg);
        assertTrue(svg.contains("&lt;script&gt;P&lt;/script&gt;"));
        assertTrue(svg.contains("VIA &quot;1&quot; &amp; &lt;b&gt;"));
        assertTrue(svg.contains("&quot;SEC&quot; &amp; co"));
        assertTrue(svg.contains("maniobra &lt;i&gt;"), "la via que maniobra tambien");
        assertFalse(svg.contains("<u>") || svg.contains("<s>"), "y el segundo seccionador del poste: " + svg);
        assertTrue(svg.contains("&lt;u&gt;SEC-2&lt;/u&gt;") && svg.contains("&lt;s&gt;"));
    }

    /**
     * Un poste admite varios seccionadores (V29 de mto-configuration): se dibujan todos, en el orden
     * en que llegan, apilados al otro lado del poste sin pisarse, y el resumen los cuenta a todos.
     */
    @Test
    void aPoleWithTwoDisconnectorsDrawsBothStackedAndTheSummaryCountsThem() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        stubTracks(threeTracks());
        var lower = new TrackSchematicDto.DisconnectorMark(41L, "NAT-NS3", true, "Disc", "NAT", null, null);
        var upper = new TrackSchematicDto.DisconnectorMark(42L, "NAT-NS4", false, "Disc", "NAT", "VIA 3", null);
        var pole = new TrackSchematicDto.ProfileNode(1L, "68-2.05", "10.000", 1, null, null, null, null, "2.500",
                List.of(), List.of(), lower, List.of(lower, upper));
        var plain = new TrackSchematicDto.ProfileNode(2L, "68-2.06", "20.000", 2, null, null, null, null, "2.500",
                List.of(), List.of(), null, List.of());
        when(trackClient.schematic(3L)).thenReturn(new TrackSchematicDto(3L, "VIA 1", true, "EP5", List.of("NAT"),
                List.of(pole, plain), List.of()));

        UI.getCurrent().navigate(TRACKS_ROUTE);
        LocatorJ._click(schematicButton(0, 3L));

        Dialog dialog = LocatorJ._get(Dialog.class);
        assertEquals("2 perfiles · 0 mensulas · 2 seccionadores · 0 aisladores · Estaciones: NAT",
                LocatorJ._get(dialog, Span.class, spec -> spec.withId(TrackSchematicDialog.SUMMARY_ID)).getText(),
                "dos seccionadores en un poste son dos, no un poste con seccionador");
        String svg = drawing(dialog);
        int first = svg.indexOf("id=\"disconnector-41\"");
        int second = svg.indexOf("id=\"disconnector-42\"");
        assertTrue(first > 0 && second > first, "los dos, en el orden en que llegan: " + svg);
        assertTrue(svg.indexOf("id=\"pole-2\"") > second, "dentro de su poste");
        assertTrue(svg.contains("Seccionador NAT-NS3 · en carga · funcion Disc · estacion NAT"));
        assertTrue(svg.contains("Seccionador NAT-NS4 · sin carga · funcion Disc · estacion NAT · maniobra la via VIA 3"));
        // El rombo de cada uno, centrado en su altura: el segundo, un escalon por encima.
        String y0 = String.format(java.util.Locale.ROOT, "%.1f", SchematicDrawing.disconnectorY(0));
        String y1 = String.format(java.util.Locale.ROOT, "%.1f", SchematicDrawing.disconnectorY(1));
        assertTrue(svg.substring(first, second).contains(" " + y0 + " L"), "el primero, a la altura de siempre");
        assertTrue(svg.substring(second).contains(" " + y1 + " L"), "el segundo, encima");
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

    /**
     * Con los hilos virtuales, cada pasada del sondeo compartido corre en su propio hilo virtual: una
     * pantalla cuyo gateway tarda no retrasa la de las demas, y la suya no se pisa consigo misma (sus
     * tics se saltan mientras la anterior espera, y vuelve cuando termina).
     */
    @Test
    void theSharedPollingRunsEachScreenOnItsOwnVirtualThreadSoASlowOneHoldsNoOtherBack() throws Exception {
        SharedPolling polling = new SharedPolling(new MockEnvironment().withProperty("spring.threads.virtual.enabled", "true"));
        CountDownLatch slowStarted = new CountDownLatch(1);
        CountDownLatch slowAgain = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch fastRanThreeTimes = new CountDownLatch(3);
        AtomicInteger slowRuns = new AtomicInteger();
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger maxInFlight = new AtomicInteger();
        AtomicReference<Thread> slowThread = new AtomicReference<>();
        try {
            polling.every(Duration.ofMillis(10), () -> {
                maxInFlight.accumulateAndGet(inFlight.incrementAndGet(), Math::max);
                slowThread.set(Thread.currentThread());
                slowRuns.incrementAndGet();
                slowStarted.countDown();
                slowAgain.countDown();
                try {
                    release.await(5, TimeUnit.SECONDS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                } finally {
                    inFlight.decrementAndGet();
                }
            });
            assertTrue(slowStarted.await(5, TimeUnit.SECONDS));
            polling.every(Duration.ofMillis(10), fastRanThreeTimes::countDown);

            assertTrue(fastRanThreeTimes.await(5, TimeUnit.SECONDS), "la pasada que espera no retrasa la de otra pantalla");
            assertEquals(1, slowRuns.get(), "mientras espera, sus tics se saltan");
            assertTrue(slowThread.get().isVirtual(), slowThread.get().getName());
            assertTrue(slowThread.get().getName().startsWith("shared-polling-"), slowThread.get().getName());

            release.countDown();
            assertTrue(slowAgain.await(5, TimeUnit.SECONDS), "al terminar, vuelve a su ritmo");
            assertEquals(1, maxInFlight.get(), "nunca dos pasadas de la misma pantalla a la vez");
        } finally {
            release.countDown();
            polling.shutdown();
        }
    }

    /** Sin hilos virtuales, las pasadas corren en el hilo del reloj, una detras de otra, como antes. */
    @Test
    void withoutVirtualThreadsTheSharedPollingRunsOnItsClockThread() throws Exception {
        SharedPolling polling = new SharedPolling(new MockEnvironment().withProperty("spring.threads.virtual.enabled", "false"));
        AtomicReference<Thread> thread = new AtomicReference<>();
        CountDownLatch ran = new CountDownLatch(1);
        try {
            polling.every(Duration.ofMillis(10), () -> {
                thread.set(Thread.currentThread());
                ran.countDown();
            });

            assertTrue(ran.await(5, TimeUnit.SECONDS));
            assertFalse(thread.get().isVirtual());
            assertEquals("shared-polling", thread.get().getName());
        } finally {
            polling.shutdown();
        }
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
        var reinicio = new TrackSchematicDto.ProfileNode(3L, "P-003", "5.000", 3, null, null, null, null, null, List.of(), List.of(), null, null);
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
     * Los seccionadores de un perfil se ensenan de solo lectura y vuelven como se leyeron: el vinculo es
     * de cada seccionador, y el servicio ignora los que llegan dentro del perfil (mto-configuration#31).
     * Un poste admite varios (V29): se ensenan todos, por el orden en que llegan.
     */
    @Test
    void theDisconnectorsOfAProfileAreReadOnlyAndTravelAsRead() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(5L, "PT1", "Poste tipo 1", true, null, null, null)));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        ProfileDto profile = profileWithOneCantilever();
        DisconnectorDto hanging = disconnectorOnPole(5L, "SEC-1", "Disc");
        DisconnectorDto other = disconnectorOnPole(6L, "SEC-2", "SurgeA");
        profile.setDisconnector(hanging);
        profile.setDisconnectors(List.of(hanging, other));
        when(profileClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(profile), 0, 50));
        when(profileClient.update(eq(7L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(PROFILES_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<ProfileDto> profiles = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(profiles, 0, "actions"), Button.class, spec -> spec.withId("edit-7")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        TextField disconnectors = LocatorJ._get(dialog, TextField.class, spec -> spec.withId("profile-disconnectors"));
        assertTrue(disconnectors.isReadOnly(), "el vinculo se cambia desde Seccionadores");
        assertEquals("SEC-1 (Disc), SEC-2 (SurgeA)", disconnectors.getValue());
        assertEquals("Los que cuelgan de este perfil. Cada uno se vincula desde Seccionadores, en su editor.",
                disconnectors.getHelperText());
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(profileClient).update(eq(7L), argThat(dto -> dto.getDisconnector() != null
                && Long.valueOf(5L).equals(dto.getDisconnector().getId())
                && Long.valueOf(7L).equals(dto.getDisconnector().getProfileId())
                && dto.getDisconnectors().stream().map(DisconnectorDto::getId).toList().equals(List.of(5L, 6L))));
        verifyNoInteractions(disconnectorClient);
    }

    /** Un servicio anterior al V29 solo manda {@code disconnector}: se ensena como el unico del poste, y sin el, «Ninguno». */
    @Test
    void aProfileWithOnlyTheOldDisconnectorKeyShowsItAndOneWithoutAnyShowsNone() {
        ProfileDto old = profileWithOneCantilever();
        old.setDisconnector(disconnectorOnPole(5L, "SEC-1", "Disc"));
        assertEquals("SEC-1 (Disc)", ProfileEditor.disconnectorsText(old));
        assertEquals(1, old.disconnectorsOnThePole().size());

        ProfileDto none = profileWithOneCantilever();
        none.setDisconnectors(List.of());
        assertEquals("Ninguno", ProfileEditor.disconnectorsText(none));
        assertEquals("Ninguno", ProfileEditor.disconnectorsText(profileWithOneCantilever()));
    }

    private static DisconnectorDto disconnectorOnPole(long id, String name, String function) {
        DisconnectorDto disconnector = new DisconnectorDto();
        disconnector.setId(id);
        disconnector.setName(name);
        disconnector.setProfileId(7L);
        disconnector.setDisconnectorFunction(new LovRef(9L, function, null));
        return disconnector;
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

    // --- Accesos a via (mto-configuration V28) -----------------------------------------------------

    private static TrackAccessPointDto accessPointRow() {
        TrackAccessPointDto dto = new TrackAccessPointDto();
        dto.setId(77L);
        dto.setName("Acceso km 110+176");
        dto.setTrackId(3L);
        dto.setStationId(12L);
        dto.setKp(new BigDecimal("110176.000"));
        dto.setKind("PREPARED_ACCESS");
        dto.setSide("LEFT");
        dto.setRoadRailAccess(true);
        dto.setMaxAxleLoadT(new BigDecimal("12.500"));
        dto.setMaxGrossWeightT(new BigDecimal("40.000"));
        dto.setLatitude(new BigDecimal("31.669342"));
        dto.setLongitude(new BigDecimal("34.574119"));
        dto.setEnabled(true);
        dto.setVersionNumber(4);
        dto.putExtra("brandNewField", "x");
        return dto;
    }

    private void stubAccessPointReferences(List<TrackAccessPointDto> rows) {
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(executionPackage(100L, "EP4")), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(trackAccessPointClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(rows, 0, 50));
    }

    /**
     * Los accesos a via son un maestro mas de Infraestructura: la lista con los nombres de su via y su
     * estacion, sus cifras tal como llegan (un tipo que esta version no conoce, crudo), y los filtros (via,
     * tipo y estado) en el cuerpo del /filter. Quien solo lee no tiene controles de escritura.
     */
    @Test
    void theAccessPointsAreListedWithTheNamesOfTheirTrackAndStationAndFilteredInTheService() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        TrackAccessPointDto ferry = accessPointRow();
        ferry.setId(78L);
        ferry.setName("Rampa del puerto");
        ferry.setStationId(null);
        ferry.setKind("FERRY_RAMP");
        ferry.setSide("BOTH");
        ferry.setRoadRailAccess(false);
        stubAccessPointReferences(List.of(accessPointRow(), ferry));

        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().contains("Accesos a via"), menuLabels().toString());
        UI.getCurrent().navigate(TrackAccessPointsView.ROUTE);

        LocatorJ._get(H2.class, spec -> spec.withText("Accesos a via"));
        @SuppressWarnings("unchecked")
        Grid<TrackAccessPointDto> grid = LocatorJ._get(Grid.class);
        assertEquals(List.of("Acceso km 110+176", "TRACK 1 (EP4)", "ATOCHA (EP4)", "110176.000", "Acceso preparado", "Izquierda", "Si", "12.500",
                "40.000", "Si"), GridKt._getFormattedRow(grid, 0).subList(0, 10));
        assertEquals(List.of("Rampa del puerto", "", "FERRY_RAMP", "Los dos lados", "No"),
                List.of(GridKt._getFormattedRow(grid, 1).get(0), GridKt._getFormattedRow(grid, 1).get(2), GridKt._getFormattedRow(grid, 1).get(4),
                        GridKt._getFormattedRow(grid, 1).get(5), GridKt._getFormattedRow(grid, 1).get(6)), "un tipo que no se conoce, crudo");
        LocatorJ._get(Span.class, spec -> spec.withText("2 accesos a via"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withText("Nuevo")).isEmpty(), "quien solo lee no da de alta");
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, "actions"), Button.class).isEmpty(), "ni modifica ni borra");

        LocatorJ._setValue(comboWithId("access-points-track"), new RefItem(3L, "TRACK 1 (EP4)"));
        LocatorJ._setValue(comboWithId("access-points-kind"), TrackAccessPointKind.PREPARED_ACCESS);
        @SuppressWarnings("unchecked")
        Select<EnabledFilter> state = LocatorJ._get(Select.class, spec -> spec.withId("access-points-state"));
        LocatorJ._setValue(state, EnabledFilter.DISABLED);
        GridKt._size(grid);
        verify(trackAccessPointClient, atLeastOnce()).filter(anyInt(), anyInt(), anyList(),
                eq(Map.of("trackId", 3L, "kind", "PREPARED_ACCESS", "enabled", false)));
    }

    /**
     * El editor comprueba lo que dice la columna antes de llamar (el KP y las toneladas no negativos y con
     * sus cifras, las coordenadas en su rango y juntas: se marca la que falta) y devuelve entera la copia
     * de la fila, con su version y lo que la UI no conoce. Un nombre repetido en la via es el BUS-002 con
     * el dialogo abierto, y borrar pide confirmacion.
     */
    @Test
    void anAccessPointIsCheckedBeforeCallingAndSentBackWholeWithItsVersion() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE", "ROLE_CONFIG_DELETE");
        stubAccessPointReferences(List.of(accessPointRow()));
        when(trackAccessPointClient.create(any())).thenAnswer(call -> call.getArgument(0));
        when(trackAccessPointClient.update(eq(77L), any()))
                .thenThrow(configurationConflict("BUS-002", "La operación entra en conflicto con un registro existente: valor único repetido"))
                .thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(TrackAccessPointsView.ROUTE);
        LocatorJ._click(button("Nuevo"));
        Dialog dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withId("access-point-name")), "Paso a nivel LC-31");
        LocatorJ._setValue(comboWithId("access-point-track"), new RefItem(3L, "TRACK 1 (EP4)"));
        assertEquals(List.of("Acceso preparado", "Paso a nivel", "Base o cochera"), ViewLayerTest.<String>comboWithId("access-point-kind")
                .getListDataView().getItems().map(TrackAccessPointKind::labelOf).toList());
        LocatorJ._setValue(comboWithId("access-point-kind"), TrackAccessPointKind.LEVEL_CROSSING.name());
        LocatorJ._setValue(comboWithId("access-point-side"), TrackAccessPointSide.BOTH.name());
        assertTrue(LocatorJ._get(dialog, Checkbox.class, spec -> spec.withId("access-point-road-rail")).getValue(), "por defecto admite biviales");
        BigDecimalField kp = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("access-point-kp"));
        BigDecimalField axle = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("access-point-max-axle-load"));
        BigDecimalField gross = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("access-point-max-gross-weight"));
        BigDecimalField latitude = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("access-point-latitude"));
        BigDecimalField longitude = LocatorJ._get(dialog, BigDecimalField.class, spec -> spec.withId("access-point-longitude"));
        LocatorJ._setValue(kp, new BigDecimal("-1"));
        LocatorJ._setValue(axle, new BigDecimal("12.5005"));
        LocatorJ._setValue(gross, new BigDecimal("100000"));
        LocatorJ._setValue(latitude, new BigDecimal("91"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(kp.isInvalid(), "el KP no es negativo");
        assertTrue(axle.isInvalid(), "tres decimales como mucho");
        assertTrue(gross.isInvalid(), "cinco cifras enteras como mucho");
        assertTrue(latitude.isInvalid(), "entre -90 y 90");
        verify(trackAccessPointClient, never()).create(any());

        LocatorJ._setValue(kp, new BigDecimal("98375.5"));
        LocatorJ._setValue(axle, new BigDecimal("12.5"));
        LocatorJ._setValue(gross, new BigDecimal("40"));
        LocatorJ._setValue(latitude, new BigDecimal("31.669342"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(longitude.isInvalid(), "las coordenadas van juntas: se marca la que falta");
        assertEquals("Con la latitud va tambien la longitud", longitude.getErrorMessage());
        LocatorJ._setValue(longitude, new BigDecimal("34.5741195"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(longitude.isInvalid(), "seis decimales como mucho");
        verify(trackAccessPointClient, never()).create(any());
        LocatorJ._setValue(longitude, new BigDecimal("34.574119"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        verify(trackAccessPointClient).create(argThat(dto -> dto.getId() == null && "Paso a nivel LC-31".equals(dto.getName())
                && Long.valueOf(3L).equals(dto.getTrackId()) && dto.getStationId() == null && new BigDecimal("98375.5").equals(dto.getKp())
                && "LEVEL_CROSSING".equals(dto.getKind()) && "BOTH".equals(dto.getSide())
                && Boolean.TRUE.equals(dto.getRoadRailAccess()) && Boolean.TRUE.equals(dto.getEnabled())
                && new BigDecimal("12.5").equals(dto.getMaxAxleLoadT()) && new BigDecimal("34.574119").equals(dto.getLongitude())));
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo se cierra al guardar");

        @SuppressWarnings("unchecked")
        Grid<TrackAccessPointDto> grid = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-77")));
        dialog = LocatorJ._get(Dialog.class);
        LocatorJ._setValue(LocatorJ._get(dialog, TextField.class, spec -> spec.withId("access-point-name")), "Acceso km 110+180");
        LocatorJ._setValue(comboWithId("access-point-station"), null);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Ya existe otro registro con ese valor (un codigo que no se puede repetir), o la entrada esta en uso."));
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        verify(trackAccessPointClient, times(2)).update(eq(77L), argThat(dto -> "Acceso km 110+180".equals(dto.getName())
                && dto.getStationId() == null && Integer.valueOf(4).equals(dto.getVersionNumber()) && "x".equals(dto.extras().get("brandNewField"))
                && new BigDecimal("110176.000").equals(dto.getKp()) && "LEFT".equals(dto.getSide())
                && new BigDecimal("31.669342").equals(dto.getLatitude())));
        assertEquals("Acceso km 110+176", GridKt._get(grid, 0).getName(), "la fila sigue siendo lo que llego del servicio");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("delete-77")));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(trackAccessPointClient).delete(77L);
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

    /**
     * El poste de un seccionador es opcional: los de los porticos de subestacion y los de puesta a
     * tierra no estan en uno. El estado normal y el accionamiento viajan con el, y vaciarlos es «sin
     * dato» (mto-configuration V25).
     */
    @Test
    void aDisconnectorIsSavedWithoutAPoleAndWithItsNormalStateAndItsDrive() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(9L, "Disc", "Seccionador", true, 1, null, null)));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of(12L))), 0, 50));
        when(profileClient.findById(7L)).thenReturn(profileWithOneCantilever());
        DisconnectorDto read = new DisconnectorDto();
        read.setId(5L);
        read.setName("SEC-1");
        read.setOnLoad(true);
        read.setStationId(12L);
        read.setProfileId(7L);
        read.setProfileCode("P-007");
        read.setProfileKp("12.345");
        read.setDisconnectorFunction(new LovRef(9L, "Disc", "Seccionador"));
        read.setVersionNumber(3);
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(read), 0, 50));
        when(disconnectorClient.update(eq(5L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(DISCONNECTORS_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<DisconnectorDto> grid = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-5")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> profile = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Perfil"));
        assertFalse(profile.isRequiredIndicatorVisible(), "hay seccionadores que no estan en un poste");
        assertEquals(7L, profile.getValue().id());
        @SuppressWarnings("unchecked")
        ComboBox<Boolean> normallyOpen = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Estado normal"));
        assertNull(normallyOpen.getValue(), "sin dato todavia");
        assertEquals("Normalmente abierto", normallyOpen.getItemLabelGenerator().apply(Boolean.TRUE));
        @SuppressWarnings("unchecked")
        ComboBox<DisconnectorDriveType> drive = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Accionamiento"));
        TextField kp = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("KP propio (m)"));
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via propia"));
        assertFalse(kp.isEnabled(), "con poste, el KP es el del perfil");
        assertTrue(track.isEnabled(), "con poste tambien: la via que maniobra, si no es la del poste");

        LocatorJ._setValue(profile, null);
        assertTrue(kp.isEnabled(), "sin poste, el seccionador lleva su propio KP");
        assertTrue(track.isEnabled());
        LocatorJ._setValue(normallyOpen, Boolean.TRUE);
        LocatorJ._setValue(drive, DisconnectorDriveType.MOTOR);
        LocatorJ._setValue(kp, "98+375");
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(kp.isInvalid(), "el KP va en metros con punto decimal, como el del perfil");
        verify(disconnectorClient, never()).update(any(), any());

        LocatorJ._setValue(kp, " 98375.5 ");
        LocatorJ._setValue(track, new RefItem(3L, "TRACK 1"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(disconnectorClient).update(eq(5L), argThat(dto -> dto.getProfileId() == null
                && "98375.5".equals(dto.getKp())
                && Long.valueOf(3L).equals(dto.getTrackId())
                && Boolean.TRUE.equals(dto.getNormallyOpen())
                && dto.getDriveType() == DisconnectorDriveType.MOTOR
                && Long.valueOf(12L).equals(dto.getStationId())
                && Integer.valueOf(3).equals(dto.getVersionNumber())));
    }

    /**
     * Uno que pone dos vias en paralelo lleva la otra (V27 de mto-configuration), con poste o sin el:
     * la lista y el editor la ensenan, y que no sea la suya lo dice el servicio, sobre su campo.
     */
    @Test
    void aParallelingDisconnectorKeepsItsConnectedTrackAndTheServiceJudgesIt() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(9L, "Disc/PP", "Puesta en paralelo", true, 1, null, null)));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(
                track(3L, "TRACK 1", true, 100L, List.of(12L)), track(4L, "TRACK 2", true, 100L, List.of(12L))), 0, 50));
        when(profileClient.findById(7L)).thenReturn(profileWithOneCantilever());
        DisconnectorDto read = new DisconnectorDto();
        read.setId(5L);
        read.setName("SEC-B01");
        read.setOnLoad(false);
        read.setStationId(12L);
        read.setProfileId(7L);
        read.setProfileCode("P-007");
        read.setProfileKp("12.345");
        read.setConnectedTrackId(4L);
        read.setDisconnectorFunction(new LovRef(9L, "Disc/PP", "Puesta en paralelo"));
        read.setVersionNumber(3);
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(read), 0, 50));
        ApiProblem problem = new ApiProblem("https://api.mto-configuration/errors/val-000", "Peticion invalida", 400,
                "La peticion tiene 1 errores de validacion", null, "VAL-000", "t-4", null, null, false,
                List.of(new ApiFieldError("connectedTrackId", "BUS-001", "Es la via de su poste")), null);
        // El editor guarda siempre sobre el mismo objeto: se apunta lo que llevaba cada envio.
        List<Long> sentConnectedTracks = new ArrayList<>();
        when(disconnectorClient.update(eq(5L), any())).thenAnswer(call -> {
            DisconnectorDto sent = call.getArgument(1);
            sentConnectedTracks.add(sent.getConnectedTrackId());
            if (Long.valueOf(3L).equals(sent.getConnectedTrackId())) {
                throw BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-4", null,
                        "PUT /api/configuration/disconnectors/5");
            }
            return sent;
        });

        UI.getCurrent().navigate(DISCONNECTORS_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<DisconnectorDto> grid = LocatorJ._get(Grid.class);
        // La via se nombra con su paquete, que aqui no se carga: #100.
        assertTrue(GridKt._getFormattedRow(grid, 0).contains("TRACK 2 (#100)"), GridKt._getFormattedRow(grid, 0).toString());
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-5")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> connected = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via conectada"));
        assertEquals(4L, connected.getValue().id());
        assertTrue(connected.isEnabled(), "con poste tambien: es del seccionador, no del perfil");

        LocatorJ._setValue(connected, new RefItem(3L, "TRACK 1"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(connected.isInvalid());
        assertEquals("Es la via de su poste", connected.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");

        LocatorJ._setValue(connected, new RefItem(4L, "TRACK 2"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertEquals(List.of(3L, 4L), sentConnectedTracks, "la rechazada y la corregida, en ese orden");
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "guardado, el dialogo se cierra");
        verify(disconnectorClient, times(2)).update(eq(5L), argThat(dto -> Long.valueOf(7L).equals(dto.getProfileId())
                && Integer.valueOf(3).equals(dto.getVersionNumber())));
    }

    /**
     * La estacion de un seccionador es opcional: uno en plena via, en una zona neutra o en una
     * subestacion no es de ninguna. Tiene que estar en algun sitio (con su estacion, en un poste o con
     * su via propia), pero eso lo dice el servicio, con un 400 sobre la estacion y el dialogo abierto.
     */
    @Test
    void aDisconnectorMayBelongToNoStationAndTheServiceSaysWhereItMustBe() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(9L, "Disc", "Seccionador", true, 1, null, null)));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of(12L))), 0, 50));
        DisconnectorDto read = new DisconnectorDto();
        read.setId(5L);
        read.setName("KAF-NS1");
        read.setOnLoad(false);
        read.setStationId(12L);
        read.setKp("98375.5");
        read.setTrackId(3L);
        read.setDisconnectorFunction(new LovRef(9L, "Disc", "Seccionador"));
        read.setVersionNumber(3);
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(read), 0, 50));
        ApiProblem problem = new ApiProblem("https://api.mto-configuration/errors/val-000", "Peticion invalida", 400,
                "La peticion tiene 1 errores de validacion", null, "VAL-000", "t-5", null, null, false,
                List.of(new ApiFieldError("stationId", "BUS-001", "Regla de negocio violada: stationId")), null);
        // El editor guarda siempre sobre el mismo objeto: se apunta lo que llevaba cada envio.
        List<List<Long>> sent = new ArrayList<>();
        when(disconnectorClient.update(eq(5L), any())).thenAnswer(call -> {
            DisconnectorDto dto = call.getArgument(1);
            sent.add(Arrays.asList(dto.getStationId(), dto.getTrackId()));
            if (dto.getStationId() == null && dto.getProfileId() == null && dto.getTrackId() == null) {
                throw BackofficeApiException.of(HttpStatus.BAD_REQUEST, problem, "corr-5", null,
                        "PUT /api/configuration/disconnectors/5");
            }
            return dto;
        });

        UI.getCurrent().navigate(DISCONNECTORS_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<DisconnectorDto> grid = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-5")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> station = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Estacion"));
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via propia"));
        assertFalse(station.isRequiredIndicatorVisible(), "hay seccionadores que no son de ninguna estacion");
        assertTrue(station.getHelperText().contains("Vacia si no es de ninguna estacion"), station.getHelperText());
        assertEquals(12L, station.getValue().id());

        LocatorJ._setValue(station, null);
        LocatorJ._setValue(track, null);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertTrue(station.isInvalid(), "sin estacion, sin poste y sin via no queda en ningun sitio");
        assertEquals("Regla de negocio violada: stationId", station.getErrorMessage());
        assertFalse(LocatorJ._find(Dialog.class).isEmpty(), "el dialogo sigue abierto para corregir");

        LocatorJ._setValue(track, new RefItem(3L, "TRACK 1"));
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));
        assertEquals(List.of(Arrays.asList(null, null), Arrays.asList(null, 3L)), sent,
                "el rechazado y el que guarda sin estacion con su via, en ese orden");
        assertTrue(LocatorJ._find(Dialog.class).isEmpty(), "guardado, el dialogo se cierra");
    }

    /** Con poste, el KP y la via son los del perfil (V26 de mto-configuration): elegirlo vacia los propios. */
    /**
     * Elegir un poste vacia el KP propio, que es el suyo, pero no la via: con poste es la que
     * maniobra si no es la del poste, y quien la tenia la ve y decide. Si es la del poste, el servicio
     * la guarda vacia.
     */
    @Test
    void choosingAPoleClearsTheOwnKpButKeepsTheTrack() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(9L, "Disc", "Seccionador", true, 1, null, null)));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of(12L))), 0, 50));
        when(profileClient.findById(7L)).thenReturn(profileWithOneCantilever());
        DisconnectorDto read = new DisconnectorDto();
        read.setId(5L);
        read.setName("HSA-FP1.1");
        read.setOnLoad(false);
        read.setStationId(12L);
        read.setKp("98375.5");
        read.setTrackId(3L);
        read.setDisconnectorFunction(new LovRef(9L, "Disc", "Seccionador"));
        read.setVersionNumber(2);
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(read), 0, 50));
        when(disconnectorClient.update(eq(5L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(DISCONNECTORS_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<DisconnectorDto> grid = LocatorJ._get(Grid.class);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-5")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        TextField kp = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("KP propio (m)"));
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via propia"));
        assertEquals("98375.5", kp.getValue());
        assertEquals(3L, track.getValue().id());
        assertTrue(kp.isEnabled());

        @SuppressWarnings("unchecked")
        ComboBox<RefItem> profile = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Perfil"));
        LocatorJ._setValue(profile, new RefItem(7L, "P-007 (kp 12.345)"));
        assertEquals("", kp.getValue());
        assertFalse(kp.isEnabled());
        assertEquals(3L, track.getValue().id());
        assertTrue(track.isEnabled());
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(disconnectorClient).update(eq(5L), argThat(dto -> Long.valueOf(7L).equals(dto.getProfileId())
                && dto.getKp() == null
                && Long.valueOf(3L).equals(dto.getTrackId())));
    }

    /**
     * En un haz de vias (talleres, cocheras) un seccionador cuelga a veces del poste de una via y
     * maniobra otra, o une dos que no son la del poste: con poste, su via propia es la que maniobra.
     * La lista la ensena junto a la conectada, el editor la ofrece con poste, y vaciarla es volver a
     * la del poste.
     */
    @Test
    void aDisconnectorOnAPoleShowsAndKeepsTheTrackItSwitches() {
        loginAs("config.responsable", "ROLE_CONFIG_READ", "ROLE_CONFIG_WRITE");
        when(lovClient.findAll(anyString())).thenReturn(List.of(new LovDto(9L, "Disc/PP", "Puesta en paralelo", true, 1, null, null)));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(
                track(3L, "TRACK 1", true, 100L, List.of(12L)), track(4L, "TRACK 2", true, 100L, List.of(12L)),
                track(5L, "TRACK 3", true, 100L, List.of(12L))), 0, 50));
        when(profileClient.findById(7L)).thenReturn(profileWithOneCantilever());
        DisconnectorDto read = new DisconnectorDto();
        read.setId(5L);
        read.setName("DEP-12");
        read.setOnLoad(false);
        read.setStationId(12L);
        read.setProfileId(7L);
        read.setProfileCode("P-007");
        read.setProfileKp("12.345");
        read.setTrackId(4L);
        read.setConnectedTrackId(5L);
        read.setDisconnectorFunction(new LovRef(9L, "Disc/PP", "Puesta en paralelo"));
        read.setVersionNumber(3);
        when(disconnectorClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(read), 0, 50));
        when(disconnectorClient.update(eq(5L), any())).thenAnswer(call -> call.getArgument(1));

        UI.getCurrent().navigate(DISCONNECTORS_ROUTE);
        @SuppressWarnings("unchecked")
        Grid<DisconnectorDto> grid = LocatorJ._get(Grid.class);
        // Las vias se nombran con su paquete, que aqui no se carga: #100.
        assertEquals("TRACK 2 (#100)", GridKt._getFormatted(grid, 0, "track"));
        assertEquals("TRACK 3 (#100)", GridKt._getFormatted(grid, 0, "connectedTrack"));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("edit-5")));
        Dialog dialog = LocatorJ._get(Dialog.class);
        TextField kp = LocatorJ._get(dialog, TextField.class, spec -> spec.withLabel("KP propio (m)"));
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> track = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via propia"));
        @SuppressWarnings("unchecked")
        ComboBox<RefItem> connected = LocatorJ._get(dialog, ComboBox.class, spec -> spec.withLabel("Via conectada"));
        assertFalse(kp.isEnabled(), "con poste, el KP es el del perfil");
        assertTrue(track.isEnabled());
        assertEquals(4L, track.getValue().id());
        assertEquals(5L, connected.getValue().id());

        LocatorJ._setValue(track, null);
        LocatorJ._click(LocatorJ._get(dialog, Button.class, spec -> spec.withText("Guardar")));

        verify(disconnectorClient).update(eq(5L), argThat(dto -> Long.valueOf(7L).equals(dto.getProfileId())
                && dto.getTrackId() == null
                && Long.valueOf(5L).equals(dto.getConnectedTrackId())
                && Integer.valueOf(3).equals(dto.getVersionNumber())));
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

    /**
     * Republicar ofrece tambien los puntos de acceso a via, antes de «Todo», que se acotan por via como los
     * perfiles: pasar de los perfiles a ellos conserva la via elegida (los seccionadores y los aisladores
     * se acotan por estacion, y la vaciarian).
     */
    @Test
    void theAccessPointsAreRepublishedNarrowedToATrackLikeTheProfiles() {
        loginAs("config.editor", "ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT");
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(track(3L, "TRACK 1", true, 100L, List.of())), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(12L, "ATOCHA", 100L)), 0, 50));
        when(jobsClient.republish(any(), any(), any()))
                .thenReturn(job(JOB_ID, JobType.MASTER_DATA_REPUBLISH, JobStatus.PENDING, Instant.parse("2026-10-17T09:12:03Z")));

        UI.getCurrent().navigate(JobsView.ROUTE);
        @SuppressWarnings("unchecked")
        Select<String> target = LocatorJ._get(Select.class, spec -> spec.withId("republish-target"));
        assertEquals(List.of("profile", "disconnector", "section-insulator", "track-access-point", "all"), ComboBoxKt.getSuggestionItems(target));
        assertEquals("Puntos de acceso a via", target.getItemLabelGenerator().apply("track-access-point"));
        LocatorJ._setValue(comboWithId("republish-track"), new RefItem(3L, "TRACK 1"));
        LocatorJ._setValue(target, "track-access-point");
        assertEquals(new RefItem(3L, "TRACK 1"), comboWithId("republish-track").getValue(), "de los perfiles a los accesos, la via se queda");
        assertTrue(comboWithId("republish-track").isEnabled());
        assertFalse(comboWithId("republish-station").isEnabled());
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("republish")));
        verify(jobsClient).republish("track-access-point", 3L, null);
        NotificationsKt.expectNotifications("Trabajo encolado: Republicado de puntos de acceso a via");

        LocatorJ._setValue(target, "disconnector");
        assertNull(comboWithId("republish-track").getValue(), "por estacion, la via se vacia");
        assertFalse(comboWithId("republish-track").isEnabled());
        assertTrue(comboWithId("republish-station").isEnabled());
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
                new BigDecimal("12.100"), new BigDecimal("13.450"), null, true, null, null, null, null);
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

    /**
     * Los de mto-resources, que llegan con la misma forma que los de mantenimiento: una regla del
     * organigrama o de la flota dice cual con el detalle del servicio, un valor repetido no pide
     * recargar, y el tope de operaciones de fondo (de los dos servicios) dice cuando reintentar.
     */
    @Test
    void resourcesErrorsSayWhatBlocksTheChange() {
        assertEquals("El organigrama no admite ese cambio. Team EQ-NORTE would have 1 linemen: between 2 and 6",
                UiErrors.message(maintenanceError(409, "UNI-001", "Team EQ-NORTE would have 1 linemen: between 2 and 6")));
        assertEquals("La flota no admite ese cambio. A rail-only vehicle is never on the road",
                UiErrors.message(maintenanceError(409, "VEH-001", "A rail-only vehicle is never on the road")));
        for (String duplicated : List.of("PER-409", "UNI-409", "VEH-409", "VTY-409", "PRK-409")) {
            assertEquals("Ya existe otro con ese valor (codigo, usuario, numero de empleado, matricula o numero ferroviario)."
                    + " Username equipo.jefe is already in use", UiErrors.message(maintenanceError(409, duplicated,
                    "Username equipo.jefe is already in use")), duplicated);
        }
        ApiProblem tooMany = new ApiProblem(null, "TOO_MANY_REQUESTS", 429, "There are already 4 background operations",
                null, "OPS-429", null, null, null, false, null, null);
        assertEquals("Hay demasiadas operaciones en marcha. Intentalo en 3 s.", UiErrors.message(BackofficeApiException.of(
                HttpStatusCode.valueOf(429), tooMany, "corr-o1", Duration.ofSeconds(3), "POST /api/maintenance/orders/1/tasks/generate")));
        // Lo que mantenimiento dice de lo que es de mto-resources: un equipo sincronizado, y una persona o un
        // vehiculo que aun no ha llegado por evento (reintentar vale; recargar la pantalla no hace falta).
        assertEquals("El equipo es de mto-resources: se cambia en Recursos > Organigrama.",
                UiErrors.message(maintenanceError(409, "TEA-001", "Team A is synchronized from mto-resources")));
        assertEquals("Esa persona aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento.",
                UiErrors.message(maintenanceError(404, "CRW-404", "Crew person not found")));
        assertEquals("Ese vehiculo aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento.",
                UiErrors.message(maintenanceError(404, "FLT-404", "Fleet vehicle not found")));
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
                new BigDecimal("12.270"), "501", "S-3", null, null, null, null, List.of(), "mto-configuration", "501",
                enabledAtSource && !disabledLocally, enabledAtSource, disabledLocally, 180, null, Instant.parse("2026-10-01T00:00:00Z"), null, 7L);
    }

    private static AssetDto ownSection(UUID id, String code, boolean enabled) {
        return new AssetDto(id, code, "Tramo " + code, CatenaryAssetType.TRACK_SECTION, "Tramo propio", 3L, 12L, null,
                new BigDecimal("12.100"), new BigDecimal("13.450"), null, null, TrackKind.MAIN, null, null, null, List.of(), null, null, enabled,
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
                true, 4, Set.of(), 1L)));
        when(maintenanceCatalogClient.inspectionTemplates()).thenReturn(List.of(
                new InspectionTemplateDto(UUID.randomUUID(), CatenaryAssetType.PROFILE, 1, "Perfil", false, List.of()),
                new InspectionTemplateDto(UUID.randomUUID(), CatenaryAssetType.PROFILE, 2, "Perfil", true, List.of(
                        new InspectionTemplateItemDto(UUID.randomUUID(), "P-01", "Altura del hilo", "mm", new BigDecimal("5300"),
                                new BigDecimal("5700"), true, 1)))));

        UI.getCurrent().navigate(MaintenanceRoutes.TEAMS);
        assertEquals(List.of("EQ-01", "Brigada norte", "Local", "", "", "", "Base Norte", "DR-2", "PAQ NORTE, PAQ SUR", "Activo"),
                GridKt._getFormattedRow(gridWithId("teams-grid"), 0).subList(0, 10));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("teams-grid"), 0, TeamsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("team-edit-" + TEAM1)));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("team-base")), "");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamEditorDialog.SAVE_ID)));
        verify(maintenanceCatalogClient).updateTeam(TEAM1, new TeamRequest("EQ-01", "Brigada norte", null, "DR-2", true,
                new java.util.TreeSet<>(Set.of(3L, 5L))));

        UI.getCurrent().navigate(MaintenanceRoutes.TASK_TYPES);
        assertEquals(List.of("RG-04", "Revision del hilo de contacto", "Conductores aereos", "Vano", "12.5", "", "Si", "No", "—", "Si"),
                GridKt._getFormattedRow(gridWithId("task-types-grid"), 0).subList(0, 10));
        LocatorJ._setValue(comboWithId("task-types-group"), FunctionalGroup.OVERHEAD_CONDUCTORS);
        verify(maintenanceCatalogClient).taskTypes(FunctionalGroup.OVERHEAD_CONDUCTORS, null, null);

        UI.getCurrent().navigate(MaintenanceRoutes.TEMPLATES);
        assertEquals(2, GridKt._size(gridWithId("templates-grid")));
        LocatorJ._get(H3.class, spec -> spec.withText("Puntos de Perfil (version 2)"));
        assertEquals(List.of("P-01", "Altura del hilo", "Si", "mm", "5300", "5700"), GridKt._getFormattedRow(gridWithId("template-items-grid"), 0));
    }

    /**
     * Un equipo de mto-resources se lee: su origen, su jefe, su supervisor y sus linemen en la fila, y en
     * vez de «Modificar» los botones a su unidad y a su vehiculo (con resources-read). El 409 TEA-001 de
     * un equipo que se sincronizo entre medias dice donde se cambia.
     */
    @Test
    void aSynchronizedTeamIsOnlyReadAndLeadsToItsUnitAndItsVehicle() {
        loginAs("mantenimiento.responsable", "ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ", "ROLE_STOCK_READ",
                "ROLE_RESOURCES_READ");
        stubReferencesForMaintenance();
        stubOrganization(true);
        when(organizationClient.findById(TEAM_UNIT)).thenReturn(team(2, true));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(2));
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true)));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.of(), 0, 20));
        PersonSummaryDto leader = new PersonSummaryDto(UUID.randomUUID(), "PER-000003", "Yossi Cohen", "equipo.jefe", PersonCategory.TEAM_LEADER);
        PersonSummaryDto supervisor = new PersonSummaryDto(UUID.randomUUID(), "PER-000020", "Dana Levi", null, PersonCategory.SUPERVISOR);
        List<PersonSummaryDto> members = List.of(new PersonSummaryDto(UUID.randomUUID(), "PER-000011", "Lineman 1", null, PersonCategory.WORKER),
                new PersonSummaryDto(UUID.randomUUID(), "PER-000012", "Lineman 2", null, PersonCategory.WORKER));
        TeamDto synced = new TeamDto(TEAM1, "A", "Equipo A", null, "DR-01", true, Set.of(3L), true, TEAM_UNIT, leader, supervisor, members,
                VEHICLE1, null);
        TeamDto local = new TeamDto(UUID.randomUUID(), "B", "Equipo B", "Base Sur", null, true, Set.of(5L), null);
        when(maintenanceCatalogClient.teams()).thenReturn(List.of(local, synced));
        when(maintenanceCatalogClient.updateTeam(any(), any())).thenThrow(maintenanceError(409, "TEA-001", "Team B is synchronized from mto-resources"));

        UI.getCurrent().navigate(MaintenanceRoutes.TEAMS);
        Grid<Object> grid = gridWithId("teams-grid");
        assertEquals(List.of("A", "Equipo A", "mto-resources", "PER-000003 · Yossi Cohen", "PER-000020 · Dana Levi", "2", "", "DR-01"),
                GridKt._getFormattedRow(grid, 0).subList(0, 8));
        assertEquals(List.of("B", "Equipo B", "Local", "", "", "", "Base Sur"), GridKt._getFormattedRow(grid, 1).subList(0, 7));
        Component syncedActions = GridKt._getCellComponent(grid, 0, TeamsView.ACTIONS_COLUMN);
        assertTrue(LocatorJ._find(syncedActions, Button.class, spec -> spec.withId("team-edit-" + TEAM1)).isEmpty(), "uno sincronizado no se modifica");

        LocatorJ._click(LocatorJ._get(syncedActions, Button.class, spec -> spec.withId("team-vehicle-" + TEAM1)));
        assertEquals(ResourcesRoutes.FLEET + "/" + VEHICLE1, UI.getCurrent().getInternals().getActiveViewLocation().getPath());
        UI.getCurrent().navigate(MaintenanceRoutes.TEAMS);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("teams-grid"), 0, TeamsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("team-unit-" + TEAM1)));
        assertEquals(ResourcesRoutes.ORGANIZATION + "/" + TEAM_UNIT, UI.getCurrent().getInternals().getActiveViewLocation().getPath());

        // Se sincronizo mientras se editaba: el servicio lo rechaza, lo dice y el dialogo sigue abierto.
        UI.getCurrent().navigate(MaintenanceRoutes.TEAMS);
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("teams-grid"), 1, TeamsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("team-edit-" + local.id())));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamEditorDialog.SAVE_ID)));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El equipo es de mto-resources: se cambia en Recursos > Organigrama."));
        LocatorJ._get(TeamEditorDialog.class);
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
                new BigDecimal("12.270"), new BigDecimal("12.270"), "S-3", true, null, null, null, null);
        return new TaskDto(id, ORDER1, sequence, "Perfil 12-2.27", status, null, profile, null, null, null, null, null, List.of(),
                List.of("RG-01"), List.of(), null, 2L);
    }

    private static List<TaskTypeDto> twoTaskTypes() {
        return List.of(
                new TaskTypeDto(UUID.randomUUID(), "RG-01", "Revision visual", FunctionalGroup.STRUCTURAL_SUPPORTS, BigDecimal.TEN, TaskUnit.PROFILE,
                        null, false, false, true, 1, Set.of(), 1L),
                new TaskTypeDto(UUID.randomUUID(), "RG-04", "Revision del hilo de contacto", FunctionalGroup.OVERHEAD_CONDUCTORS,
                        new BigDecimal("12.5"), TaskUnit.SPAN, null, true, false, true, 4, Set.of("INSULATED_PLATFORM"), 3L));
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

        click("order-assign");
        assertEquals("Vacia: el jefe del equipo, si es de Recursos",
                LocatorJ._get(TextField.class, spec -> spec.withId("order-transition-assigned-user")).getHelperText(),
                "sin persona, el servicio asigna la orden de un equipo de Recursos a su jefe");
        LocatorJ._get(OrderTransitionDialog.class).close();

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
        when(orderClient.generateTasks(eq(ORDER1), any())).thenReturn(new GenerateTasksResultDto(14, 2, 16, new BigDecimal("720.0"), 3, 2, null, null, null, null, null, null, null));
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
        verify(orderClient).generateTasks(ORDER1, GenerateTasksRequest.ofSection(null, null, null, null));
        NotificationsKt.expectNotifications("Tareas nuevas: 14; ya en la orden: 2. Total: 16, unos 720 min en 3 turnos.");

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
                new BigDecimal("12.000"), new BigDecimal("12.000"), null, true, null, null, null, null);
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
        verify(shiftClient, atLeastOnce()).search(eq(new ShiftFilter(LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), TEAM1, List.of(12L), 3L,
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

    // --- Mantenimiento: la dotacion y los vehiculos de un turno (mto-resources) ----------------------

    private static final UUID CREW_LEADER = UUID.fromString("3c4d5e6f-0000-4000-8000-0000000000a1");
    private static final UUID CREW_A = UUID.fromString("3c4d5e6f-0000-4000-8000-0000000000a2");
    private static final UUID CREW_B = UUID.fromString("3c4d5e6f-0000-4000-8000-0000000000a3");
    private static final UUID VAN = UUID.fromString("9d0e1f20-0000-4000-8000-0000000000a1");
    private static final String[] SHIFT_CREW_WRITER = {"ROLE_MAINTENANCE_READ", "ROLE_MAINTENANCE_WRITE", "ROLE_CONFIG_READ", "ROLE_STOCK_READ",
            "ROLE_RESOURCES_READ"};

    private static PersonSummaryDto crew(UUID id, String code, String name, PersonCategory category) {
        return new PersonSummaryDto(id, code, name, null, category);
    }

    /** Un turno con su jefe, dos linemen, una dresina y una furgoneta; {@code parking}, donde quedo la dresina. */
    private static ShiftDto shiftWithCrew(ShiftStatus status, ShiftVehicleDto.Parking parking) {
        ShiftDto base = shiftOf(status);
        return new ShiftDto(base.id(), base.code(), base.shiftDate(), base.team(), base.baseName(), base.vehicle(), base.possessionType(),
                base.plannedStart(), base.plannedEnd(), base.actualStart(), base.actualEnd(), base.voltageCutoffAt(), base.netWorkMinutes(),
                base.blockingDisconnectors(), base.earthingPoints(), base.parkingPlace(), base.executionPackageId(), base.trackIds(),
                base.startKp(), base.endKp(), base.personnel(), base.measurementEquipment(), base.status(), base.observations(), base.audit(), 4L,
                crew(CREW_LEADER, "PER-000003", "Yossi Cohen", PersonCategory.TEAM_LEADER), null,
                List.of(crew(CREW_A, "PER-000011", "Lineman Uno", PersonCategory.WORKER), crew(CREW_B, "PER-000012", "Lineman Dos", PersonCategory.WORKER)),
                List.of(new ShiftVehicleDto(VEHICLE1, "DR-01", "Dresina 1", VehicleMobility.RAIL_ONLY, VehicleCategory.CATENARY_TOWER_CAR, true, parking),
                        new ShiftVehicleDto(VAN, "VAN-01", "Furgoneta", VehicleMobility.ROAD_ONLY, VehicleCategory.VAN, true, null)), List.of());
    }

    /**
     * El alta elige la dotacion y los vehiculos en mto-resources: el jefe entre cualquiera menos un operario,
     * los linemen entre operarios y jefes de equipo, los vehiculos entre los disponibles; viajan sus ids, y lo
     * que se deja vacio no viaja (lo copia el servicio del equipo).
     */
    @Test
    void aNewShiftPicksItsCrewAndVehiclesInMtoResources() {
        loginAs("mantenimiento.tecnico", SHIFT_CREW_WRITER);
        stubReferencesForMaintenance();
        stubPeople(List.of(person(CREW_LEADER, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true),
                person(CREW_A, "PER-000011", "Lineman", "Uno", PersonCategory.WORKER, true)));
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true)));
        when(shiftClient.create(any())).thenReturn(shiftWithCrew(ShiftStatus.PLANNED, null));
        when(shiftClient.findById(SHIFT1)).thenReturn(shiftWithCrew(ShiftStatus.PLANNED, null));
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());

        UI.getCurrent().navigate(MaintenanceRoutes.SHIFTS);
        click("shift-create");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("shift-date")), LocalDate.of(2026, 10, 5));
        LocatorJ._setValue(comboWithId("shift-possession"), PossessionType.FULL);
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RefItem> tracks = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-tracks"));
        LocatorJ._setValue(tracks, Set.of(new RefItem(12L, "VIA 1 (PAQ NORTE)")));
        ComboBox<PersonSummaryDto> leader = LocatorJ._get(ComboBox.class, spec -> spec.withId("shift-leader"));
        assertEquals("Vacio: el del equipo, si es de mto-resources", leader.getHelperText());
        assertEquals(List.of("PER-000003 · Yossi Cohen", "PER-000011 · Lineman Uno"),
                ComboBoxKt.getSuggestionItems(leader).stream().map(PersonSummaryDto::label).toList());
        verify(personClient, atLeastOnce()).search(argThat((PersonFilter filter) -> filter != null
                && filter.categories().equals(Set.copyOf(PersonCategory.atLeast(2))) && Boolean.TRUE.equals(filter.active())), anyInt(), anyInt(), anyList());
        LocatorJ._setValue(leader, crew(CREW_LEADER, "PER-000003", "Yossi Cohen", PersonCategory.TEAM_LEADER));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<PersonSummaryDto> linemen = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-linemen"));
        LocatorJ._setValue(linemen, Set.of(crew(CREW_A, "PER-000011", "Lineman Uno", PersonCategory.WORKER)));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<VehicleSummaryDto> vehicles = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-vehicles"));
        @SuppressWarnings("unchecked")
        DataProvider<VehicleSummaryDto, String> offered = (DataProvider<VehicleSummaryDto, String>) vehicles.getDataProvider();
        assertEquals(List.of("DR-01 · Vehiculo DR-01"), offered.fetch(new Query<>(0, 50, List.of(), null, "")).map(VehicleSummaryDto::label).toList());
        verify(vehicleClient, atLeastOnce()).search(eq(VehicleFilter.availableMatching("")), anyInt(), anyInt(), anyList());
        LocatorJ._setValue(vehicles, Set.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true).summary()));
        click(ShiftEditorDialog.SAVE_ID);

        verify(shiftClient).create(new ShiftRequest(LocalDate.of(2026, 10, 5), null, null, null, PossessionType.FULL, null, null, null,
                null, null, null, Set.of(12L), null, null, null, null, null, Set.of(VEHICLE1), CREW_LEADER, null, Set.of(CREW_A), null));
        LocatorJ._get(ShiftDetailView.class);
        assertEquals("Jefe: PER-000003 · Yossi Cohen · Supervisor: - · Linemen (2): PER-000011 · Lineman Uno, PER-000012 · Lineman Dos",
                LocatorJ._get(Div.class, spec -> spec.withId("shift-crew")).getText());
    }

    /**
     * Modificar la dotacion: los linemen y los vehiculos son conjuntos, quitarlos todos viaja a null, y una
     * persona que aun no ha llegado a mantenimiento (CRW-404) se dice con el dialogo abierto.
     */
    @Test
    void theCrewOfAShiftChangesAsSets() {
        loginAs("mantenimiento.tecnico", SHIFT_CREW_WRITER);
        stubReferencesForMaintenance();
        stubPeople(List.of());
        stubVehicles(List.of());
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(shiftClient.update(eq(SHIFT1), any())).thenThrow(maintenanceError(404, "CRW-404", "Crew person not found"))
                .thenReturn(shiftWithCrew(ShiftStatus.PLANNED, null));
        openShift(shiftWithCrew(ShiftStatus.PLANNED, null));

        click("shift-edit");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<PersonSummaryDto> linemen = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-linemen"));
        assertEquals(2, linemen.getValue().size());
        LocatorJ._setValue(linemen, Set.of());
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<VehicleSummaryDto> vehicles = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-vehicles"));
        LocatorJ._setValue(vehicles, Set.of(vehicles.getValue().iterator().next()));
        click(ShiftEditorDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Esa persona aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento."));
        LocatorJ._get(ShiftEditorDialog.class);
        click(ShiftEditorDialog.SAVE_ID);
        verify(shiftClient, times(2)).update(eq(SHIFT1), argThat(patch -> patch.cleared().equals(Set.of("linemanPersonIds"))
                && patch.values().vehicleIds().size() == 1 && patch.values().teamLeaderPersonId() == null && patch.version() == 4L));
    }

    /** Sin resources-read la dotacion de un turno se lee como esta, no se pide nada a mto-resources y no viaja. */
    @Test
    void withoutResourcesReadTheCrewOfAShiftIsOnlyReadAndDoesNotTravel() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(shiftClient.update(eq(SHIFT1), any())).thenReturn(shiftWithCrew(ShiftStatus.PLANNED, null));
        openShift(shiftWithCrew(ShiftStatus.PLANNED, null));
        click("shift-edit");
        assertTrue(LocatorJ._find(MultiSelectComboBox.class, spec -> spec.withId("shift-linemen")).isEmpty());
        assertEquals("Jefe: PER-000003 · Yossi Cohen · Supervisor: - · Linemen: PER-000011 · Lineman Uno, PER-000012 · Lineman Dos"
                + " · Vehiculos: DR-01 · Dresina 1, VAN-01 · Furgoneta", LocatorJ._get(Div.class, spec -> spec.withId("shift-crew-readonly")).getText());
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("shift-observations")), "Con lluvia");
        click(ShiftEditorDialog.SAVE_ID);
        verify(shiftClient).update(eq(SHIFT1), argThat(patch -> patch.cleared().isEmpty() && "Con lluvia".equals(patch.values().observations())
                && patch.values().vehicleIds() == null && patch.values().linemanPersonIds() == null));
        verify(personClient, never()).search(any(PersonFilter.class), anyInt(), anyInt(), anyList());
    }

    /**
     * Al cerrar, cada vehiculo dice donde se deja: un sitio que alcanza por su movilidad, una via y su kp
     * (no en uno de solo carretera) o un texto. Lo evidente se dice en su fila antes de llamar, solo viaja
     * lo rellenado, y un SHF-001 deja el dialogo abierto. La ficha dice luego donde quedo, con sus nombres.
     */
    @Test
    void closingAShiftSaysWhereEachVehicleIsLeftAndTheDetailNamesIt() {
        loginAs("mantenimiento.tecnico", SHIFT_CREW_WRITER);
        stubReferencesForMaintenance();
        ParkingSiteDto road = new ParkingSiteDto(UUID.fromString("8c9d0e1f-0000-4000-8000-0000000000a2"), "PRK-RSH", "Parking de Rishpon",
                ParkingSiteKind.ROAD_PARKING, 3L, null, null, null, false, true, null, null, null, null, true, 1L);
        when(parkingSiteClient.search(any(ParkingSiteFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(siding(true), road), 0, 200));
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        ShiftVehicleDto.Parking parked = new ShiftVehicleDto.Parking(PARKING_SITE1, 12L, new BigDecimal("4.250"), null);
        when(shiftClient.close(eq(SHIFT1), any())).thenThrow(maintenanceError(409, "SHF-001", "Vehicle DR-01 is rail-only: it is left on a track"))
                .thenReturn(shiftWithCrew(ShiftStatus.CLOSED, parked));
        openShift(shiftWithCrew(ShiftStatus.IN_PROGRESS, null));

        click("shift-close");
        assertEquals("DR-01 · Dresina 1", LocatorJ._get(Div.class, spec -> spec.withId("parking-vehicle-0")).getText());
        ComboBox<ParkingSiteDto> railSite = LocatorJ._get(ComboBox.class, spec -> spec.withId("parking-site-0"));
        assertEquals(List.of("APT-RSH"), ComboBoxKt.getSuggestionItems(railSite).stream().map(ParkingSiteDto::code).toList(), "uno de solo via, con acceso por via");
        ComboBox<ParkingSiteDto> roadSite = LocatorJ._get(ComboBox.class, spec -> spec.withId("parking-site-1"));
        assertEquals(List.of("PRK-RSH"), ComboBoxKt.getSuggestionItems(roadSite).stream().map(ParkingSiteDto::code).toList());
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("parking-track-1")).isEmpty(), "uno de solo carretera no se deja en una via");

        TextField railText = LocatorJ._get(TextField.class, spec -> spec.withId("parking-description-0"));
        LocatorJ._setValue(railText, "En la playa de vias");
        click(ShiftTransitionDialog.CONFIRM_ID);
        assertTrue(railText.isInvalid());
        assertEquals("Uno de solo via se deja sobre una via: un sitio o una via", railText.getErrorMessage());
        LocatorJ._setValue(railText, "");
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-kp-0")), new BigDecimal("4.250"));
        click(ShiftTransitionDialog.CONFIRM_ID);
        ComboBox<RefItem> railTrack = LocatorJ._get(ComboBox.class, spec -> spec.withId("parking-track-0"));
        assertEquals("Un KP va con su via", railTrack.getErrorMessage());
        verify(shiftClient, never()).close(any(), any());

        LocatorJ._setValue(railTrack, new RefItem(12L, "VIA 1 (PAQ NORTE)"));
        LocatorJ._setValue(railSite, siding(true));
        click(ShiftTransitionDialog.CONFIRM_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El turno no admite ese trabajo. Vehicle DR-01 is rail-only: it is left on a track"));
        LocatorJ._get(ShiftTransitionDialog.class);
        click(ShiftTransitionDialog.CONFIRM_ID);
        verify(shiftClient, times(2)).close(SHIFT1, new CloseShiftRequest(null, null, null, null,
                List.of(new VehicleParkingRequest(VEHICLE1, PARKING_SITE1, 12L, new BigDecimal("4.250"), null))));

        assertEquals("Cerrado", spanText("shift-status"));
        assertEquals("Vehiculos: DR-01 · Dresina 1 (aparcado: APT-RSH · VIA 1 (PAQ NORTE) kp 4.25), VAN-01 · Furgoneta",
                LocatorJ._get(Div.class, spec -> spec.withId("shift-vehicles-summary")).getText());
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

    /** Una tarea abierta se quita del turno y vuelve a su orden, sin confirmacion; lo completado se queda. */
    @Test
    void anOpenTaskIsTakenOutOfTheShiftAndWhatWasDoneStays() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of(task(TASK1, 1, MaintenanceTaskStatus.IN_PROGRESS),
                task(TASK2, 2, MaintenanceTaskStatus.COMPLETED)));
        when(orderClient.findById(ORDER1)).thenReturn(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));
        when(shiftClient.unassignTask(SHIFT1, TASK1)).thenReturn(task(TASK1, 1, MaintenanceTaskStatus.PENDING));
        openShift(shiftOf(ShiftStatus.IN_PROGRESS));

        Grid<Object> tasks = gridWithId("shift-tasks-grid");
        assertTrue(LocatorJ._find(GridKt._getCellComponent(tasks, 1, "actions"), Button.class,
                spec -> spec.withId("shift-task-unassign-" + TASK2)).isEmpty(), "lo completado se queda en el turno");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tasks, 0, "actions"), Button.class, spec -> spec.withId("shift-task-unassign-" + TASK1)));

        verify(shiftClient).unassignTask(SHIFT1, TASK1);
        NotificationsKt.expectNotifications("Tarea 1 quitada del turno: vuelve a su orden");
    }

    @Test
    void aClosedShiftNoLongerLetsATaskOut() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of(task(TASK1, 1, MaintenanceTaskStatus.PENDING)));
        when(orderClient.findById(ORDER1)).thenReturn(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));
        openShift(shiftOf(ShiftStatus.CLOSED));

        assertTrue(LocatorJ._find(GridKt._getCellComponent(gridWithId("shift-tasks-grid"), 0, "actions"), Button.class,
                spec -> spec.withId("shift-task-unassign-" + TASK1)).isEmpty());
        verify(shiftClient, never()).unassignTask(any(), any());
    }

    /** El filtro de los perfiles siempre lleva un estado: sin el, el servicio daria los completados y no «todas». */
    @Test
    void theShiftProfilesAlwaysFilterByAStatus() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        AssetSummaryDto profile = new AssetSummaryDto(ASSET_SYNCED, "PRF-0001", "12-2.27", CatenaryAssetType.PROFILE, 12L,
                new BigDecimal("12.270"), new BigDecimal("12.270"), "S-3", true, null, null, null, null);
        when(shiftClient.profiles(SHIFT1, MaintenanceTaskStatus.COMPLETED)).thenReturn(List.of(profile));
        when(shiftClient.profiles(SHIFT1, MaintenanceTaskStatus.PENDING)).thenReturn(List.of());
        openShift(shiftOf(ShiftStatus.IN_PROGRESS));
        selectTab(1);

        @SuppressWarnings("unchecked")
        Select<MaintenanceTaskStatus> status = LocatorJ._get(Select.class, spec -> spec.withId("shift-profiles-status"));
        assertEquals(MaintenanceTaskStatus.COMPLETED, status.getValue());
        assertFalse(status.isEmptySelectionAllowed(), "no se vacia");
        assertEquals(1, GridKt._size(gridWithId("shift-profiles-grid")));
        LocatorJ._setValue(status, MaintenanceTaskStatus.PENDING);
        assertEquals(0, GridKt._size(gridWithId("shift-profiles-grid")));
        verify(shiftClient).profiles(SHIFT1, MaintenanceTaskStatus.PENDING);
        verify(shiftClient, never()).profiles(SHIFT1, null);
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

    /**
     * La tarea de un aislador que une dos vias se completa desde un turno de cualquiera: desde la orden
     * se ofrecen los en curso de las dos, en una sola busqueda con las dos vias.
     */
    @Test
    void completingAnInsulatorTaskFromTheOrderOffersTheShiftsOfBothItsTracks() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        AssetSummaryDto insulator = new AssetSummaryDto(ASSET_SYNCED, "SIN-0041", "SI-W41", CatenaryAssetType.SECTION_INSULATOR, 13L,
                new BigDecimal("12.920"), new BigDecimal("12.960"), null, true, 12L, null, null, null);
        TaskDto task = new TaskDto(TASK1, ORDER1, 1, "Aislador SI-W41", MaintenanceTaskStatus.PENDING, null, insulator, null, null, null, null, null,
                List.of(), List.of("RG-03"), List.of(), null, 2L);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of(task));
        when(shiftClient.search(eq(ShiftFilter.inProgressOn(13L, 12L)), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(shiftOf(ShiftStatus.IN_PROGRESS)), 0, 50));
        when(orderClient.completeTask(eq(ORDER1), eq(TASK1), any())).thenReturn(task(TASK1, 1, MaintenanceTaskStatus.COMPLETED));
        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.PREVENTIVE));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(gridWithId("order-tasks-grid"), 0, "actions"), Button.class,
                spec -> spec.withId("task-complete-" + TASK1)));
        assertEquals("Los turnos en curso de sus dos vias", comboWithId("complete-task-shift").getHelperText());
        ComboBoxKt.selectByLabel(comboWithId("complete-task-shift"), "SH-000001 · 05/10/2026 · EQ-01 - Brigada norte");
        click(CompleteTaskDialog.CONFIRM_ID);

        verify(shiftClient, atLeastOnce()).search(eq(ShiftFilter.inProgressOn(13L, 12L)), eq(0), anyInt(), anyList());
        verify(orderClient).completeTask(ORDER1, TASK1, new CompleteTaskRequest(SHIFT1, null, null, null, null, null, null, null, null));
    }

    /** Generar los aisladores del tramo es una fila mas, y viaja la peticion de siempre con los dos tipos de activo. */
    @Test
    void generatingTheInsulatorsOfTheSectionAddsARowAndSendsBothAssetTypes() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(orderClient.generateTasks(eq(ORDER1), any())).thenReturn(new GenerateTasksResultDto(3, 14, 19, new BigDecimal("810.0"), 3, 16, null, null, null, null, null, null, null));
        openOrder(orderOf(MaintenanceOrderStatus.PLANNED, MaintenanceOrderType.PREVENTIVE));

        click("task-generate");
        assertTrue(LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("generate-types")).getHelperText()
                .contains("RG-03 y RP-02 en cada aislador (y RG-04 y RP-03 si tiene agujas); RP-07 en cada seccionador (y RP-06 si "
                        + "tiene motor, RG-06 si es una autovalvula)"), "La ayuda dice los tipos que pone el servicio sin elegir ninguno");
        click(GenerateTasksDialog.ADD_ID);
        assertEquals(CatenaryAssetType.SECTION_INSULATOR, ViewLayerTest.<CatenaryAssetType>combo("generate-row-2-type").getValue());
        assertEquals("El tramo de la orden", labelOf(combo("generate-row-2-scope")));
        click(GenerateTasksDialog.CONFIRM_ID);

        verify(orderClient).generateTasks(ORDER1, GenerateTasksRequest.ofSection(null, null, GenerateTasksRequest.WITH_INSULATORS, null));
        NotificationsKt.expectNotifications("Tareas nuevas: 3; ya en la orden: 16. Total: 19, unos 810 min en 3 turnos.");
    }

    /** Una orden mezcla su tramo con los aisladores de otra via y los seccionadores del EP, y antes lo previsualiza. */
    @Test
    void anOrderMixesItsSectionWithInsulatorsOfAnotherTrackAndDisconnectorsOfItsPackageAndPreviewsIt() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(executionPackage(3L, "PAQ NORTE"), executionPackage(5L, "PAQ SUR")), 0, 50));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(track(12L, "VIA 1", true, 3L, List.of()),
                track(14L, "VIA 2", true, 3L, List.of()), track(20L, "VIA 9", true, 5L, List.of())), 0, 50));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(4L, "Sants", 3L)), 0, 50));
        List<GenerationPreviewLineDto> lines = List.of(
                new GenerationPreviewLineDto(ASSET_SYNCED, "PRF-0001", "12-2.27", CatenaryAssetType.PROFILE, 12L, null, new BigDecimal("12.270"),
                        List.of("RG-01"), false, GenerationOutcome.CREATE, null),
                new GenerationPreviewLineDto(ASSET1, "SIN-0002", "SI-W41", CatenaryAssetType.SECTION_INSULATOR, 14L, 12L, new BigDecimal("150"),
                        List.of("RG-03"), false, GenerationOutcome.IN_OTHER_ORDER, "MO-000042"),
                new GenerationPreviewLineDto(ASSET_OWN, "DSC-0004", "HSA-NS6", CatenaryAssetType.DISCONNECTOR, 12L, null, new BigDecimal("12.900"),
                        List.of(), false, GenerationOutcome.NO_TASK_TYPES, null));
        when(orderClient.generateTasks(eq(ORDER1), any())).thenAnswer(call -> {
            GenerateTasksRequest request = call.getArgument(1);
            return new GenerateTasksResultDto(1, 0, 1, new BigDecimal("60"), 1, 0,
                    Map.of(CatenaryAssetType.PROFILE, 1, CatenaryAssetType.SECTION_INSULATOR, 0, CatenaryAssetType.DISCONNECTOR, 0), 1,
                    List.of("MO-000042"), 0, 1, request.dryRun(), Boolean.TRUE.equals(request.dryRun()) ? lines : List.of());
        });
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        click("task-generate");
        click(GenerateTasksDialog.ADD_ID);
        chooseByLabel(combo("generate-row-2-scope"), "Una via del EP");
        ComboBox<RefItem> track = combo("generate-row-2-track");
        assertEquals(List.of("VIA 1 (PAQ NORTE)", "VIA 2 (PAQ NORTE)"), track.getListDataView().getItems().map(RefItem::label).toList(),
                "Solo las vias del EP de la orden");
        chooseByLabel(track, "VIA 2 (PAQ NORTE)");
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("generate-row-2-from")), new BigDecimal("100"));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("generate-row-2-to")), new BigDecimal("200.5"));
        click(GenerateTasksDialog.ADD_ID);
        LocatorJ._setValue(ViewLayerTest.<CatenaryAssetType>combo("generate-row-3-type"), CatenaryAssetType.DISCONNECTOR);
        chooseByLabel(combo("generate-row-3-scope"), "Todo el EP");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<TaskTypeDto> ownTypes = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("generate-row-3-types"));
        LocatorJ._setValue(ownTypes, Set.of(ownTypes.getListDataView().getItems().filter(type -> type.code().equals("RG-04")).findFirst().orElseThrow()));

        click(GenerateTasksDialog.PREVIEW_ID);
        List<GenerationSelection> selections = List.of(
                new GenerationSelection(CatenaryAssetType.PROFILE, ASSET1, null, null, null, null, null, null),
                new GenerationSelection(CatenaryAssetType.SECTION_INSULATOR, null, 14L, new BigDecimal("100"), new BigDecimal("200.5"), null, null, null),
                new GenerationSelection(CatenaryAssetType.DISCONNECTOR, null, null, null, null, null, null, List.of("RG-04")));
        verify(orderClient).generateTasks(ORDER1, new GenerateTasksRequest(null, null, null, selections, true));
        Grid<Object> preview = gridWithId(GenerateTasksDialog.PREVIEW_GRID_ID);
        assertTrue(preview.isVisible());
        assertEquals(3, GridKt._size(preview));
        assertEquals("En MO-000042", GridKt._getFormatted(preview, 1, "outcome"));
        assertEquals("VIA 2 (PAQ NORTE)", GridKt._getFormatted(preview, 1, "track"));
        assertEquals("Sin tipo de tarea: elige tipos", GridKt._getFormatted(preview, 2, "outcome"));
        assertEquals("Se crearian: 1 (perfil: 1, aislador de seccion: 0, seccionador: 0); ya en la orden: 0; en otras ordenes: 1 (MO-000042); "
                + "sin tipo de tarea: 1. Total: 1, unos 60 min en 1 turno.",
                LocatorJ._get(Paragraph.class, spec -> spec.withId(GenerateTasksDialog.PREVIEW_SUMMARY_ID)).getText());

        // Cambiar algo borra la previsualizacion: ya no dice lo que se pediria.
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("generate-with-checklist")), true);
        assertTrue(LocatorJ._find(Grid.class, spec -> spec.withId(GenerateTasksDialog.PREVIEW_GRID_ID)).isEmpty());

        click(GenerateTasksDialog.CONFIRM_ID);
        verify(orderClient).generateTasks(ORDER1, new GenerateTasksRequest(null, true, null, selections, null));
        NotificationsKt.expectNotifications("Tareas nuevas: 1 (perfil: 1, aislador de seccion: 0, seccionador: 0); ya en la orden: 0; "
                + "en otras ordenes: 1 (MO-000042); sin tipo de tarea: 1. Total: 1, unos 60 min en 1 turno.");
    }

    /** Una orden sobre un aislador tambien genera, desde su EP; lo que falta en una fila se dice antes de llamar, y un 400 deja el dialogo abierto. */
    @Test
    void anOrderOnAnInsulatorGeneratesFromItsPackageAndAMissingSectionIsSaidBeforeCalling() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        BackofficeApiException rejected = maintenanceError(400, "REQ-VALIDATION",
                "selections[0].sectionId: TS-0009 is not in the execution package of order MO-000001; it goes in another order");
        when(orderClient.generateTasks(eq(ORDER1), any())).thenThrow(rejected);
        OrderDto base = orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE);
        AssetSummaryDto insulator = new AssetSummaryDto(ASSET_SYNCED, "SIN-0001", "SI-W31", CatenaryAssetType.SECTION_INSULATOR, 12L,
                new BigDecimal("12.500"), new BigDecimal("12.600"), null, true, 14L, 3L, null, null);
        openOrder(withAsset(base, insulator));

        click("task-generate");
        ComboBox<Object> scope = combo("generate-row-1-scope");
        assertEquals("Todo el EP", labelOf(scope));
        assertEquals(List.of("Otro tramo del EP", "Una via del EP", "Una estacion del EP", "Todo el EP", "Activos elegidos"),
                scope.getListDataView().getItems().map(item -> scope.getItemLabelGenerator().apply(item)).toList());
        chooseByLabel(scope, "Otro tramo del EP");

        click(GenerateTasksDialog.PREVIEW_ID);
        ComboBox<AssetSummaryDto> section = combo("generate-row-1-section");
        assertTrue(section.isInvalid());
        assertEquals("Elige un tramo", section.getErrorMessage());
        verify(orderClient, never()).generateTasks(any(), any());

        LocatorJ._setValue(section, new AssetSummaryDto(ASSET1, "TS-0009", "Tramo 9", CatenaryAssetType.TRACK_SECTION, 14L,
                new BigDecimal("1.000"), new BigDecimal("2.000"), null, true, null, 3L, null, null));
        click(GenerateTasksDialog.CONFIRM_ID);
        verify(orderClient).generateTasks(ORDER1, new GenerateTasksRequest(null, null, null,
                List.of(new GenerationSelection(CatenaryAssetType.PROFILE, ASSET1, null, null, null, null, null, null)), null));
        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText(UiErrors.message(rejected)));
        assertTrue(hasButton(GenerateTasksDialog.CONFIRM_ID), "El dialogo sigue abierto");
    }

    /** Una orden sin EP solo genera su tramo: la peticion de siempre, sin tipos por fila. */
    @Test
    void anOrderWithoutPackageOnlyGeneratesItsSection() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        when(orderClient.generateTasks(eq(ORDER1), any())).thenReturn(new GenerateTasksResultDto(2, 0, 2, new BigDecimal("60"), 1, 0, null, null, null, null, null, null, null));
        OrderDto base = order(ORDER1, "MO-000001", MaintenanceOrderStatus.DRAFT, 12L, null);
        openOrder(base);

        click("task-generate");
        click(GenerateTasksDialog.ADD_ID);
        LocatorJ._setValue(ViewLayerTest.<CatenaryAssetType>combo("generate-row-2-type"), CatenaryAssetType.DISCONNECTOR);
        ComboBox<Object> scope = combo("generate-row-2-scope");
        assertEquals(List.of("El tramo de la orden"), scope.getListDataView().getItems().map(item -> scope.getItemLabelGenerator().apply(item)).toList());
        assertTrue(LocatorJ._find(MultiSelectComboBox.class, spec -> spec.withId("generate-row-2-types")).isEmpty(),
                "Sin EP no hay tipos por fila: viaja la peticion de siempre");
        click(GenerateTasksDialog.CONFIRM_ID);

        verify(orderClient).generateTasks(ORDER1, GenerateTasksRequest.ofSection(null, null,
                List.of(CatenaryAssetType.PROFILE, CatenaryAssetType.DISCONNECTOR), null));
    }

    private static final UUID OPERATION1 = UUID.fromString("3c3c3c3c-0000-4000-8000-0000000000c1");

    private static OperationDto<GenerateTasksResultDto> generation(OperationStatus status, GenerateTasksResultDto result, OperationErrorDto error) {
        return new OperationDto<>(OPERATION1, "GENERATE_TASKS", "EP-3", status, "mantenimiento.tecnico", Instant.parse("2026-10-16T21:00:00Z"),
                null, null, result == null ? null : 200, result, error);
    }

    private void stubGenerationAccepted() {
        // doReturn y no when(...): when llamaria a la respuesta por defecto del setUp, que llama a otro metodo del doble.
        doReturn(ResponseEntity.status(HttpStatus.ACCEPTED).body(jsonMapper.valueToTree(generation(OperationStatus.QUEUED, null, null))))
                .when(orderClient).generateTasksAsync(eq(ORDER1), any(), any());
    }

    /**
     * Una generacion que no termina en su espera se sigue en el propio dialogo: el 202 trae la operacion,
     * cada pasada pregunta por ella, y al terminar dice lo mismo que la llamada sincrona y relee las tareas.
     */
    @Test
    void aLongGenerationIsFollowedInTheDialogUntilItsResult() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        stubGenerationAccepted();
        GenerateTasksResultDto result = new GenerateTasksResultDto(412, 0, 412, new BigDecimal("6000"), 12, 0, null, null, null, null, null, null, null);
        AtomicReference<OperationDto<GenerateTasksResultDto>> state = new AtomicReference<>(generation(OperationStatus.RUNNING, null, null));
        when(operationClient.generation(OPERATION1)).thenAnswer(call -> state.get());
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        click("task-generate");
        click(GenerateTasksDialog.CONFIRM_ID);
        verify(orderClient).generateTasksAsync(ORDER1, GenerateTasksRequest.ofSection(null, null, null, null), TaskGeneration.PREFER);
        verify(orderClient, never()).generateTasks(any(), any());
        GenerateTasksDialog dialog = LocatorJ._get(GenerateTasksDialog.class);
        Paragraph progress = LocatorJ._get(Paragraph.class, spec -> spec.withId(GenerateTasksDialog.OPERATION_ID));
        assertEquals("La generacion sigue en el servicio (en cola): se comprueba cada 2 s. Si cierras el dialogo, continua alli.", progress.getText());
        assertFalse(LocatorJ._get(Button.class, spec -> spec.withId(GenerateTasksDialog.CONFIRM_ID)).isEnabled(), "no se lanza otra encima");

        dialog.pollOnce();
        MockVaadin.clientRoundtrip();
        assertTrue(progress.getText().contains("(en marcha)"));
        state.set(generation(OperationStatus.SUCCEEDED, result, null));
        dialog.pollOnce();
        MockVaadin.clientRoundtrip();
        assertTrue(LocatorJ._find(GenerateTasksDialog.class).isEmpty(), "terminada, el dialogo se cierra");
        NotificationsKt.expectNotifications("Tareas nuevas: 412; ya en la orden: 0. Total: 412, unos 6000 min en 12 turnos.");
        verify(orderClient, atLeast(2)).tasks(ORDER1);
    }

    /**
     * Una generacion que falla en el servicio dice su error como la llamada sincrona, con el dialogo abierto;
     * cerrar uno que sigue una operacion deja de seguirla y avisa de que continua alli.
     */
    @Test
    void aFailedGenerationSaysItsErrorAndClosingWhileFollowingWarnsItGoesOn() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        stubGenerationAccepted();
        when(operationClient.generation(OPERATION1)).thenReturn(generation(OperationStatus.FAILED, null,
                new OperationErrorDto(409, "TRN-001", "Order MO-000001 is COMPLETED: no tasks can be generated")));
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        click("task-generate");
        click(GenerateTasksDialog.CONFIRM_ID);
        LocatorJ._get(GenerateTasksDialog.class).pollOnce();
        MockVaadin.clientRoundtrip();
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "El estado actual no permite esta operacion. Order MO-000001 is COMPLETED: no tasks can be generated"));
        assertTrue(LocatorJ._get(Button.class, spec -> spec.withId(GenerateTasksDialog.CONFIRM_ID)).isEnabled(), "se puede volver a lanzar");
        assertTrue(LocatorJ._find(Paragraph.class, spec -> spec.withId(GenerateTasksDialog.OPERATION_ID)).isEmpty(), "ya no se sigue nada");

        click(GenerateTasksDialog.CONFIRM_ID);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withText("Volver")));
        assertTrue(LocatorJ._find(GenerateTasksDialog.class).isEmpty());
        assertEquals(GenerateTasksDialog.STILL_RUNNING, NotificationsKt.getNotifications().getLast().getElement().getProperty("text"));
        assertEquals("La operacion se interrumpio en el servicio (se paro la instancia que la llevaba): vuelve a lanzarla.",
                UiErrors.message(TaskGeneration.failureOf(generation(OperationStatus.FAILED, null, new OperationErrorDto(500, "OPS-410", "Abandoned")))));
    }

    /** El tope de operaciones del despliegue es un 429 OPS-429: dice cuando reintentar, con el dialogo abierto. */
    @Test
    void aGenerationBeyondTheOperationsCapSaysWhenToRetry() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(twoTaskTypes());
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        ApiProblem tooMany = new ApiProblem(null, "TOO_MANY_REQUESTS", 429, "There are already 8 background operations", null, "OPS-429",
                null, null, null, false, null, null);
        doThrow(BackofficeApiException.of(HttpStatusCode.valueOf(429), tooMany, "corr-o2", Duration.ofSeconds(5),
                "POST /api/maintenance/orders/" + ORDER1 + "/tasks/generate")).when(orderClient).generateTasksAsync(eq(ORDER1), any(), any());
        openOrder(orderOf(MaintenanceOrderStatus.DRAFT, MaintenanceOrderType.PREVENTIVE));

        click("task-generate");
        click(GenerateTasksDialog.CONFIRM_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Hay demasiadas operaciones en marcha. Intentalo en 5 s."));
        LocatorJ._get(GenerateTasksDialog.class);
    }

    private static <T> void chooseByLabel(ComboBox<T> combo, String label) {
        LocatorJ._setValue(combo, combo.getListDataView().getItems()
                .filter(item -> label.equals(combo.getItemLabelGenerator().apply(item))).findFirst().orElseThrow());
    }

    private static <T> String labelOf(ComboBox<T> combo) {
        return combo.getValue() == null ? "" : combo.getItemLabelGenerator().apply(combo.getValue());
    }

    private static OrderDto withAsset(OrderDto base, AssetSummaryDto asset) {
        return new OrderDto(base.id(), base.code(), base.title(), base.description(), base.type(), base.status(), base.priority(), asset,
                base.executionPackageId(), base.trackId(), base.stationId(), base.startKp(), base.endKp(), base.plannedDate(),
                base.actualStartDate(), base.actualEndDate(), base.team(), base.assignedUser(), base.closingNotes(), base.cancellationReason(),
                base.originInspectionId(), base.originDefectId(), base.stockProjectId(), base.taskCount(), base.completedTaskCount(),
                base.estimatedMinutes(), base.estimatedShifts(), base.audit(), base.version());
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
                new BigDecimal("12.270"), "S-3", true, null, null, null, null);
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

    /**
     * El alta puede llevar el turno en que se hizo: los recientes de la via de su activo que se han
     * trabajado (en curso o cerrados); uno planificado no se ofrece.
     */
    @Test
    void anInspectionIsRecordedInAShiftThatWorkedOnTheTrackOfItsAsset() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        when(orderClient.tasks(ORDER1)).thenReturn(List.of());
        InspectionDto created = inspectionOf(InspectionResult.OK, null, null);
        when(inspectionClient.create(any())).thenReturn(created);
        when(shiftClient.search(eq(ShiftFilter.on(List.of(12L))), anyInt(), anyInt(), anyList()))
                .thenReturn(page(List.of(shiftOf(ShiftStatus.CLOSED), shiftOf(ShiftStatus.PLANNED)), 0, 50));
        openOrder(orderOf(MaintenanceOrderStatus.IN_PROGRESS, MaintenanceOrderType.INSPECTION));

        selectTab(3);
        click("order-inspection-create");
        ComboBox<ShiftDto> shift = comboWithId(InspectionEditorDialog.SHIFT_ID);
        assertEquals(List.of(ShiftStatus.CLOSED), ComboBoxKt.getSuggestionItems(shift).stream().map(ShiftDto::status).toList(),
                "un turno planificado no se ha trabajado");
        ComboBoxKt.selectByLabel(shift, "SH-000001 · 05/10/2026 · EQ-01 - Brigada norte · Cerrado");
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("inspection-date")), LocalDate.of(2026, 10, 6));
        click(InspectionEditorDialog.SAVE_ID);

        verify(inspectionClient).create(new InspectionRequest(ASSET1, LocalDate.of(2026, 10, 6), null, InspectionKind.VISUAL, InspectionResult.OK,
                null, null, null, null, ORDER1, SHIFT1));
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
        when(shiftClient.search(eq(ShiftFilter.on(List.of(12L))), anyInt(), anyInt(), anyList()))
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
        // Un servicio que todavia no cuenta las inspecciones: el resumen sigue siendo el de antes.
        when(reportClient.shiftReport(SHIFT1)).thenReturn(new ShiftReportDto(shift, 2, 1, 2, 1, 0, List.of(row), null, null));
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
        assertEquals(0, GridKt._size(gridWithId("shift-report-inspections")));
    }

    /**
     * Las inspecciones registradas en el turno salen en su parte, debajo de las tareas, con el recuento
     * del servicio y un boton que abre cada una.
     */
    @Test
    void theShiftReportListsTheInspectionsRecordedInTheShiftAndOpensThem() {
        loginAs("mantenimiento.lector", MAINTENANCE_READER);
        stubReferencesForMaintenance();
        ShiftDto shift = shiftOf(ShiftStatus.CLOSED);
        ShiftReportInspectionDto recorded = new ShiftReportInspectionDto(INSPECTION1, "INS-000040", LocalDate.of(2026, 10, 6), "SIN-0041", "SI-W41",
                CatenaryAssetType.SECTION_INSULATOR, 13L, new BigDecimal("12.921"), InspectionKind.VISUAL, InspectionResult.UNSAFE, "dana",
                "Patin quemado", "DEF-000007", "MO-000031");
        when(reportClient.shiftReport(SHIFT1)).thenReturn(new ShiftReportDto(shift, 0, 0, 0, 0, 0, List.of(), 1, List.of(recorded)));
        when(inspectionClient.findById(INSPECTION1)).thenReturn(inspectionOf(InspectionResult.UNSAFE, null, null));

        openShift(shift);
        selectTab(2);

        assertTrue(textOf("shift-report-summary").endsWith(" · Inspecciones: 1"), textOf("shift-report-summary"));
        Grid<Object> grid = gridWithId("shift-report-inspections");
        assertEquals(List.of("INS-000040", "06/10/2026", "SIN-0041 - SI-W41", "12.921", "Visual", "Insegura", "dana", "Patin quemado",
                        "DEF-000007, MO-000031"),
                GridKt._getFormattedRow(grid, 0).subList(0, 9));
        Button open = LocatorJ._get(GridKt._getCellComponent(grid, 0, RowActions.OPEN_COLUMN), Button.class,
                spec -> spec.withId("open-" + INSPECTION1));
        assertEquals("Abrir INS-000040", open.getTooltip().getText());
        LocatorJ._click(open);
        LocatorJ._get(H2.class, spec -> spec.withText("INS-000001 · 20/09/2026"));
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
        ActivityEventDto burst = new ActivityEventDto(EVENT2, 119L, "mto-configuration", "burst:7", ActivityCategory.of("TELEMETRY"),
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
        assertTrue(offered.containsAll(List.of(ActivityCategory.USERS, ActivityCategory.FIELD, ActivityCategory.RESOURCES, ActivityCategory.SYSTEM)), offered.toString());

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

    // --- Recursos: personal ----------------------------------------------------------------------

    private static final String[] RESOURCES_READER = {"ROLE_RESOURCES_READ", "ROLE_CONFIG_READ", "ROLE_NOTIFICATION_INBOX",
            "ROLE_REALM_MTO_RESOURCES_VIEWER"};
    private static final String[] RESOURCES_MANAGER = {"ROLE_RESOURCES_READ", "ROLE_RESOURCES_WRITE", "ROLE_RESOURCES_FLEET_WRITE",
            "ROLE_RESOURCES_LOCATION_WRITE", "ROLE_CONFIG_READ", "ROLE_NOTIFICATION_INBOX", "ROLE_REALM_MTO_RESOURCES_MANAGER"};
    private static final String[] RESOURCES_ADMIN = {"ROLE_RESOURCES_READ", "ROLE_RESOURCES_WRITE", "ROLE_RESOURCES_FLEET_WRITE",
            "ROLE_RESOURCES_LOCATION_WRITE", "ROLE_RESOURCES_DELETE", "ROLE_CONFIG_READ", "ROLE_NOTIFICATION_INBOX"};
    private static final UUID PERSON1 = UUID.fromString("3c4d5e6f-0000-4000-8000-000000000001");
    private static final UUID PERSON2 = UUID.fromString("3c4d5e6f-0000-4000-8000-000000000002");

    private static PersonDto person(UUID id, String code, String first, String last, PersonCategory category, boolean active) {
        return new PersonDto(id, code, "E-" + code.substring(4), first, last, first + " " + last, null, category, PersonEmployer.OWN, null,
                "+972 50 000 0000", active, 2L);
    }

    /**
     * Las rutas de recursos son las de mto-frontend, que tiene la misma lista copiada en
     * src/test/fixtures/backoffice-routes.json: una ruta nueva o renombrada aqui se lleva alli en el mismo
     * cambio, y la SPA lo comprueba contra ese fichero. Las literales (tablero, modelos, aparcamientos)
     * ganan a las de parametro en los dos routers. La ficha de una persona, la flota y los instrumentos son
     * ademas los enlaces de los avisos de caducidad de mto-notification.
     */
    @Test
    void theResourcesRoutesAreTheOnesOfTheSpa() {
        List<String> routes = ROUTES.getRoutes().stream()
                .map(view -> view.getAnnotation(Route.class))
                .filter(Objects::nonNull)
                .map(Route::value)
                .filter(route -> route.startsWith(ResourcesRoutes.PREFIX + "/"))
                .sorted()
                .toList();
        assertEquals(List.of("recursos/cualificaciones", "recursos/discrepancias", "recursos/flota", "recursos/flota/:vehicleId", "recursos/flota/aparcamientos",
                "recursos/flota/modelos", "recursos/flota/tablero", "recursos/instrumentos", "recursos/organigrama", "recursos/organigrama/:unitId",
                "recursos/personal", "recursos/personal/:personId"), routes);
    }

    private void stubPeople(List<PersonDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(personClient).search(any(PersonFilter.class), anyInt(), anyInt(), anyList());
    }

    @Test
    void theResourcesGroupShowsWithResourcesRead() {
        loginAs("recursos.lector", RESOURCES_READER);
        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().containsAll(List.of("Recursos", "Personal")), menuLabels().toString());
    }

    @Test
    void withoutResourcesReadThereIsNoResourcesGroupAndARealmRoleOpensNothing() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        UI.getCurrent().navigate(HomeView.class);
        assertFalse(menuLabels().contains("Recursos"), menuLabels().toString());
        assertFalse(menuLabels().contains("Personal"), menuLabels().toString());

        loginAs("recursos.impostor", "ROLE_REALM_RESOURCES_READ", "ROLE_REALM_MTO_POSITION_TEAM_LEADER");
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(ResourcesRoutes.PEOPLE));
        assertTrue(LocatorJ._find(PeopleView.class).isEmpty());
        verify(personClient, never()).search(any(PersonFilter.class), anyInt(), anyInt(), anyList());
    }

    /** La lista se pide al servicio con sus filtros (la categoria, cualquiera de las elegidas) y quien solo lee ve solo el historial. */
    @Test
    void thePeopleAreSearchedAndFilteredInTheServerAndAReaderOnlySeesTheHistory() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubPeople(List.of(person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true),
                person(PERSON2, "PER-000004", "Dana", "Levi", PersonCategory.WORKER, false)));

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE);

        Grid<Object> grid = gridWithId("people-grid");
        assertEquals(List.of("PER-000003", "Yossi Cohen", "Jefe de equipo", "Propio", "", "E-000003", "+972 50 000 0000", "Activa"),
                GridKt._getFormattedRow(grid, 0).subList(0, 8));
        assertEquals("Inactiva", GridKt._getFormattedRow(grid, 1).get(7));
        verify(personClient, atLeastOnce()).search(eq(PersonFilter.NONE), eq(0), anyInt(), eq(List.of("lastName,asc", "firstName,asc")));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("people-search")), "cohen");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<PersonCategory> categories = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("people-categories"));
        LocatorJ._setValue(categories, Set.of(PersonCategory.WORKER, PersonCategory.TEAM_LEADER));
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("people-state")), EnabledFilter.ENABLED);
        GridKt._size(grid);
        verify(personClient, atLeastOnce()).search(eq(new PersonFilter("cohen", Set.of(PersonCategory.WORKER, PersonCategory.TEAM_LEADER),
                null, true)), eq(0), anyInt(), anyList());

        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("person-create")).isEmpty(), "sin resources-write no hay alta");
        assertEquals(List.of("person-open-" + PERSON1, "person-history-" + PERSON1), LocatorJ._find(GridKt._getCellComponent(grid, 0,
                PeopleView.ACTIONS_COLUMN), Button.class).stream().map(button -> button.getId().orElse("")).toList(),
                "quien solo lee abre la ficha y ve el historial, y nada mas");
    }

    /**
     * El alta exige lo evidente antes de llamar (una subcontrata dice su empresa) y un error del
     * servicio cae en su campo con el dialogo abierto; viaja solo lo escrito.
     */
    @Test
    void aPersonIsCreatedWithItsCompanyIfSubcontractedAndTheServiceErrorsFallInTheirFields() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        stubPeople(List.of());
        when(personClient.create(any()))
                .thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, new ApiProblem(null, "BAD_REQUEST", 400, "Validation failed", null,
                        "REQ-VALIDATION", null, null, null, false, List.of(new ApiFieldError("username", null, "size must be between 0 and 100")),
                        null), "corr-r2", null, "POST /api/resources/people"))
                .thenReturn(person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.WORKER, true));

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("person-create")));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("person-first-name")), " Yossi ");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("person-last-name")), "Cohen");
        LocatorJ._setValue(comboWithId("person-category"), PersonCategory.WORKER);
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("person-employer")), PersonEmployer.SUBCONTRACTOR);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(PersonEditorDialog.SAVE_ID)));

        verify(personClient, never()).create(any());
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("person-company")).isInvalid(), "una subcontrata dice su empresa");

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("person-company")), "Catenarias del Norte");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("person-username")), "equipo.nuevo");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(PersonEditorDialog.SAVE_ID)));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("person-username")).isInvalid(), "el error del servicio, en su campo");
        assertFalse(LocatorJ._find(PersonEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(PersonEditorDialog.SAVE_ID)));
        verify(personClient, times(2)).create(new PersonRequest("Yossi", "Cohen", PersonCategory.WORKER, null, "equipo.nuevo",
                PersonEmployer.SUBCONTRACTOR, "Catenarias del Norte", null));
        assertTrue(LocatorJ._find(PersonEditorDialog.class).isEmpty());
    }

    /** Modificar manda lo cambiado, lo vaciado y la version leida; una version vieja deja el dialogo abierto y pide recargar. */
    @Test
    void aPersonIsPatchedWithWhatChangedAndAStaleVersionKeepsTheDialogOpen() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        PersonDto yossi = person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.WORKER, true);
        stubPeople(List.of(yossi));
        when(personClient.update(any(), any()))
                .thenThrow(maintenanceError(409, "CON-001", "Person PER-000003 was changed by someone else"))
                .thenReturn(yossi);

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE);
        Grid<Object> grid = gridWithId("people-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-edit-" + PERSON1)));
        LocatorJ._setValue(comboWithId("person-category"), PersonCategory.TEAM_LEADER);
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("person-phone")), "  ");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(PersonEditorDialog.SAVE_ID)));

        MergePatch<PersonUpdateRequest> expected = new MergePatch<>(new PersonUpdateRequest(null, null, PersonCategory.TEAM_LEADER, null,
                null, null, null, null, null), Set.of("phone"), 2L);
        verify(personClient).update(PERSON1, expected);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Conflicto con otro cambio: recarga y vuelve a intentarlo."));
        assertFalse(LocatorJ._find(PersonEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(PersonEditorDialog.SAVE_ID)));
        verify(personClient, times(2)).update(PERSON1, expected);
        assertTrue(LocatorJ._find(PersonEditorDialog.class).isEmpty());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-edit-" + PERSON1)));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(PersonEditorDialog.SAVE_ID)));
        verify(personClient, times(2)).update(any(), any());
        assertTrue(LocatorJ._find(PersonEditorDialog.class).isEmpty(), "sin cambios se cierra sin llamar");
    }

    /**
     * Desactivar pide resources-delete y confirmacion, y la regla de 2 a 6 la dice el servicio;
     * reactivar es un PATCH active=true con resources-write. El historial se abre desde la fila.
     */
    @Test
    void aPersonIsDeactivatedWithConfirmationReactivatedByPatchAndItsHistoryOpensFromTheRow() {
        loginAs("recursos.responsable", RESOURCES_ADMIN);
        stubPeople(List.of(person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.WORKER, true),
                person(PERSON2, "PER-000004", "Dana", "Levi", PersonCategory.WORKER, false)));
        doThrow(maintenanceError(409, "UNI-001", "Team EQ-NORTE would have 1 linemen: between 2 and 6")).doNothing()
                .when(personClient).deactivate(PERSON1);
        when(personClient.update(any(), any())).thenReturn(person(PERSON2, "PER-000004", "Dana", "Levi", PersonCategory.WORKER, true));
        when(personClient.revisions(PERSON1, 0, RevisionsDialog.PAGE_SIZE)).thenReturn(page(List.of(new RevisionDto<>(
                new RevisionMetadataDto(4L, Instant.parse("2026-10-16T06:00:00Z"), RevisionOperation.UPDATED, "recursos.gestor", "HTTP",
                        "c-4"), person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true))), 0, RevisionsDialog.PAGE_SIZE));

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE);
        Grid<Object> grid = gridWithId("people-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-deactivate-" + PERSON1)));
        verify(personClient, never()).deactivate(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El organigrama no admite ese cambio. Team EQ-NORTE would have 1 linemen: between 2 and 6"));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-deactivate-" + PERSON1)));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(personClient, times(2)).deactivate(PERSON1);

        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 1, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-deactivate-" + PERSON2)).isEmpty(), "una inactiva no se desactiva otra vez");
        NotificationsKt.clearNotifications();
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-activate-" + PERSON2)));
        verify(personClient).update(PERSON2, MergePatch.of(PersonUpdateRequest.active(true), 2L));
        NotificationsKt.expectNotifications("Reactivada PER-000004 · Dana Levi");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-history-" + PERSON1)));
        Grid<Object> revisions = gridWithId("revisions-grid");
        assertEquals(1, GridKt._size(revisions));
        assertEquals("Yossi Cohen · Jefe de equipo · Propio · activa", GridKt._getFormattedRow(revisions, 0).get(5),
                "como quedo la persona en esa revision");
        verify(personClient, atLeastOnce()).revisions(PERSON1, 0, RevisionsDialog.PAGE_SIZE);
    }

    // --- Recursos: organigrama -------------------------------------------------------------------

    private static final UUID PROJECT_UNIT = UUID.fromString("5e6f7081-0000-4000-8000-000000000009");
    private static final UUID DEPARTMENT_UNIT = UUID.fromString("5e6f7081-0000-4000-8000-000000000008");
    private static final UUID SUPERVISION_UNIT = UUID.fromString("5e6f7081-0000-4000-8000-000000000002");
    private static final UUID TEAM_UNIT = UUID.fromString("5e6f7081-0000-4000-8000-000000000001");
    private static final PersonSummaryDto LEADER = new PersonSummaryDto(PERSON1, "PER-000003", "Yossi Cohen", "equipo.jefe",
            PersonCategory.TEAM_LEADER);

    private static PersonSummaryDto lineman(int n) {
        return new PersonSummaryDto(UUID.fromString("3c4d5e6f-0000-4000-8000-00000000001" + n), "PER-00001" + n, "Lineman " + n, null,
                PersonCategory.WORKER);
    }

    private static List<TeamMemberDto> linemen(int count) {
        List<TeamMemberDto> members = new java.util.ArrayList<>();
        for (int n = 0; n < count; n++) {
            members.add(new TeamMemberDto(UUID.fromString("6f708192-0000-4000-8000-00000000000" + n), lineman(n), LocalDate.of(2026, 10, 1), null));
        }
        return members;
    }

    private static OrganizationTreeNodeDto node(UUID id, String code, String name, OrganizationUnitKind kind, WorkDiscipline discipline,
                                                PersonSummaryDto head, Set<Long> packages, Integer linemen, boolean active,
                                                OrganizationTreeNodeDto... children) {
        return new OrganizationTreeNodeDto(id, code, name, kind, discipline, head, packages, linemen, active, List.of(children));
    }

    private static List<OrganizationTreeNodeDto> organization(boolean teamActive) {
        return List.of(node(PROJECT_UNIT, "PRJ", "Proyecto", OrganizationUnitKind.PROJECT, null, null, Set.of(), null, true,
                node(DEPARTMENT_UNIT, "MNT", "Mantenimiento", OrganizationUnitKind.DEPARTMENT, WorkDiscipline.MAINTENANCE, null, Set.of(), null, true,
                        node(SUPERVISION_UNIT, "SUP-N", "Supervision norte", OrganizationUnitKind.SUPERVISION, WorkDiscipline.MAINTENANCE, null,
                                Set.of(4L), null, true,
                                node(TEAM_UNIT, "A", "Equipo A", OrganizationUnitKind.TEAM, WorkDiscipline.MAINTENANCE, LEADER, Set.of(5L, 4L), 4,
                                        teamActive)))));
    }

    private static OrganizationUnitDto team(int count, boolean active) {
        List<TeamMemberDto> members = active ? linemen(count) : List.of();
        return new OrganizationUnitDto(TEAM_UNIT, "A", "Equipo A", OrganizationUnitKind.TEAM, WorkDiscipline.MAINTENANCE,
                new UnitSummaryDto(SUPERVISION_UNIT, "SUP-N", "Supervision norte", OrganizationUnitKind.SUPERVISION), LEADER, Set.of(4L, 5L), null,
                members, members.size(), members.size() >= 2,
                new PersonSummaryDto(PERSON2, "PER-000020", "Dana Levi", null, PersonCategory.SUPERVISOR), null, active, 7L);
    }

    private static OrganizationUnitDto supervision(boolean active) {
        return new OrganizationUnitDto(SUPERVISION_UNIT, "SUP-N", "Supervision norte", OrganizationUnitKind.SUPERVISION, WorkDiscipline.MAINTENANCE,
                new UnitSummaryDto(DEPARTMENT_UNIT, "MNT", "Mantenimiento", OrganizationUnitKind.DEPARTMENT),
                new PersonSummaryDto(PERSON2, "PER-000020", "Dana Levi", null, PersonCategory.SUPERVISOR), Set.of(4L), null, List.of(), null, null,
                null, null, active, 2L);
    }

    private void stubOrganization(boolean teamActive) {
        when(organizationClient.tree(anyBoolean())).thenReturn(organization(teamActive));
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(executionPackage(4L, "EP 4"), executionPackage(5L, "EP 5"), executionPackage(6L, "EP 6")), 0, 1000));
        stubPeople(List.of());
    }

    /**
     * El arbol entero y desplegado, con el jefe o «Vacante», los linemen de un equipo frente a la regla
     * y los EP con su nombre; quien solo lee abre las fichas y nada mas; las inactivas se piden aparte.
     */
    @Test
    void theOrganizationIsAnExpandedTreeWithItsHeadsLinemenAndPackagesAndARowOpensItsUnit() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubOrganization(true);
        when(organizationClient.findById(TEAM_UNIT)).thenReturn(team(4, true));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(4));

        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().contains("Organigrama"), menuLabels().toString());
        UI.getCurrent().navigate(ResourcesRoutes.ORGANIZATION);

        @SuppressWarnings("unchecked")
        TreeGrid<OrganizationTreeNodeDto> tree = LocatorJ._get(TreeGrid.class, spec -> spec.withId("organization-tree"));
        assertEquals(4, GridKt._size(tree), "el arbol llega desplegado");
        assertEquals(List.of("A · Equipo A", "Equipo", "Mantenimiento", "PER-000003 · Yossi Cohen", "4 de 2 a 6", "EP 4, EP 5", "Activa"),
                GridKt._getFormattedRow(tree, 3).subList(0, 7));
        assertEquals("Vacante", GridKt._getFormattedRow(tree, 1).get(3));
        assertEquals("", GridKt._getFormattedRow(tree, 1).get(4), "solo un equipo cuenta linemen");
        LocatorJ._get(Span.class, spec -> spec.withText("4 unidades"));
        verify(organizationClient).tree(false);

        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("unit-create")).isEmpty(), "sin resources-write no hay alta");
        assertEquals(List.of("unit-open-" + SUPERVISION_UNIT), LocatorJ._find(GridKt._getCellComponent(tree, 2, OrganizationView.ACTIONS_COLUMN),
                Button.class).stream().map(button -> button.getId().orElse("")).toList());

        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("organization-include-inactive")), true);
        verify(organizationClient).tree(true);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tree, 3, OrganizationView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("unit-open-" + TEAM_UNIT)));
        LocatorJ._get(UnitDetailView.class);
        LocatorJ._get(H2.class, spec -> spec.withText("A · Equipo A"));
        LocatorJ._get(Div.class, spec -> spec.withId("unit-team-summary").withText("Linemen: 4 de 2 a 6 · Completo: Si · "
                + "Supervisor: PER-000020 · Dana Levi · Jefe de supervisores: - · Vehiculo: -"));
        Grid<Object> members = gridWithId("members-grid");
        assertEquals(4, GridKt._size(members), "la pestana de linemen se pide al abrirse");
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("member-add")).stream().noneMatch(Button::isVisible),
                "quien solo lee no cambia los linemen");
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("unit-edit")).isEmpty());
    }

    /** Una ficha que no existe se dice y vuelve al organigrama, como las de mantenimiento; un id que no es un UUID, igual. */
    @Test
    void aUnitThatDoesNotExistIsSaidAndGoesBackToTheTree() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubOrganization(true);
        UUID missing = UUID.fromString("5e6f7081-0000-4000-8000-0000000000ff");
        when(organizationClient.findById(missing)).thenThrow(maintenanceError(404, "UNI-404", "Organization unit not found"));

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(missing));
        LocatorJ._get(OrganizationView.class);
        NotificationsKt.expectNotifications("No existe la unidad " + missing);

        NotificationsKt.clearNotifications();
        UI.getCurrent().navigate(ResourcesRoutes.ORGANIZATION + "/no-es-un-id");
        LocatorJ._get(OrganizationView.class);
        NotificationsKt.expectNotifications("No existe la unidad no-es-un-id");
    }

    /**
     * Un equipo nace completo: con su jefe (de las categorias del nivel) y de 2 a 6 linemen que no
     * sean el jefe, comprobados antes de llamar; el padre sale del arbol (de un nivel superior) y la
     * disciplina es la suya; los EP viajan como ids. Un error del servicio cae en su campo.
     */
    @Test
    void aTeamIsCreatedCompleteUnderItsParentAndTheRuleOfTwoToSixIsCheckedBeforeCalling() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        stubOrganization(true);
        when(organizationClient.create(any()))
                .thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, new ApiProblem(null, "BAD_REQUEST", 400, "Validation failed", null,
                        "REQ-VALIDATION", null, null, null, false, List.of(new ApiFieldError("code", null, "must match [A-Za-z0-9_-]+")), null),
                        "corr-r3", null, "POST /api/resources/units"))
                .thenReturn(team(2, true));

        UI.getCurrent().navigate(ResourcesRoutes.ORGANIZATION);
        @SuppressWarnings("unchecked")
        TreeGrid<OrganizationTreeNodeDto> tree = LocatorJ._get(TreeGrid.class, spec -> spec.withId("organization-tree"));
        assertTrue(LocatorJ._find(GridKt._getCellComponent(tree, 3, OrganizationView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("unit-create-under-" + TEAM_UNIT)).isEmpty(), "de un equipo no cuelga nada");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(tree, 2, OrganizationView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("unit-create-under-" + SUPERVISION_UNIT)));

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("unit-code")), "B");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("unit-name")), " Equipo B ");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("unit-kind")), OrganizationUnitKind.TEAM);
        ComboBox<UnitSummaryDto> parent = comboWithId("unit-parent");
        assertEquals("SUP-N", parent.getValue().code(), "el padre de la fila");
        assertEquals(List.of("PRJ", "MNT", "SUP-N"), ComboBoxKt.getSuggestionItems(parent).stream().map(UnitSummaryDto::code).toList(),
                "un equipo cuelga de cualquier nivel superior, nunca de otro equipo");
        @SuppressWarnings("unchecked")
        Select<WorkDiscipline> discipline = LocatorJ._get(Select.class, spec -> spec.withId("unit-discipline"));
        assertEquals(WorkDiscipline.MAINTENANCE, discipline.getValue());
        assertTrue(discipline.isReadOnly(), "la disciplina es la del padre");
        ComboBox<PersonSummaryDto> head = comboWithId("unit-head");
        ComboBoxKt.getSuggestionItems(head);
        verify(personClient, atLeastOnce()).search(eq(PersonFilter.activeOf("", EnumSet.copyOf(PersonCategory.atLeast(2)))), eq(0), anyInt(),
                anyList());

        @SuppressWarnings("unchecked")
        MultiSelectComboBox<PersonSummaryDto> members = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("unit-members"));
        LocatorJ._setValue(members, Set.of(lineman(1)));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        verify(organizationClient, never()).create(any());
        assertTrue(head.isInvalid(), "un equipo trabaja siempre con su jefe");
        assertTrue(members.isInvalid(), "con un lineman no nace");
        assertEquals("1 de 2 a 6", members.getHelperText());

        LocatorJ._setValue(head, LEADER);
        LocatorJ._setValue(members, new java.util.LinkedHashSet<>(List.of(lineman(1), LEADER)));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        verify(organizationClient, never()).create(any());
        assertTrue(members.isInvalid(), "el jefe no va tambien de lineman");

        LocatorJ._setValue(members, new java.util.LinkedHashSet<>(List.of(lineman(1), lineman(2))));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RefItem> packages = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("unit-packages"));
        LocatorJ._setValue(packages, Set.of(new RefItem(6L, "EP 6"), new RefItem(4L, "EP 4")));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("unit-code")).isInvalid(), "el error del servicio, en su campo");
        assertFalse(LocatorJ._find(UnitEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        verify(organizationClient, times(2)).create(argThat(request -> request.code().equals("B") && request.name().equals("Equipo B")
                && request.kind() == OrganizationUnitKind.TEAM && request.discipline() == WorkDiscipline.MAINTENANCE
                && request.parentId().equals(SUPERVISION_UNIT) && request.headPersonId().equals(PERSON1)
                && request.executionPackageIds().equals(Set.of(4L, 6L)) && Set.copyOf(request.memberPersonIds()).equals(Set.of(lineman(1).id(),
                lineman(2).id())) && request.joinedOn() == null && request.defaultVehicleId() == null));
        assertTrue(LocatorJ._find(UnitEditorDialog.class).isEmpty());
    }

    /**
     * Modificar desde la ficha manda lo cambiado (los EP como conjunto, el jefe vaciado) y la version
     * leida; una version vieja deja el dialogo abierto. Sin config-read los EP se ven como #id y no
     * viajan. El historial dice como quedo la unidad.
     */
    @Test
    void aUnitIsPatchedWithItsPackagesAsASetAndWithoutConfigReadTheyStayAsTheyAre() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        stubOrganization(true);
        when(organizationClient.findById(SUPERVISION_UNIT)).thenReturn(supervision(true));
        when(organizationClient.children(eq(SUPERVISION_UNIT), anyInt(), anyInt())).thenReturn(page(List.of(team(4, true)), 0, 200));
        when(organizationClient.update(any(), any()))
                .thenThrow(maintenanceError(409, "CON-001", "Organization unit SUP-N was changed by someone else"))
                .thenReturn(supervision(true));
        when(organizationClient.revisions(SUPERVISION_UNIT, 0, RevisionsDialog.PAGE_SIZE)).thenReturn(page(List.of(new RevisionDto<>(
                new RevisionMetadataDto(3L, Instant.parse("2026-10-16T06:00:00Z"), RevisionOperation.UPDATED, "recursos.gestor", "HTTP", "c-5"),
                supervision(true))), 0, RevisionsDialog.PAGE_SIZE));

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(SUPERVISION_UNIT));
        assertEquals(List.of("A · Equipo A", "Equipo", "PER-000003 · Yossi Cohen", "4 de 2 a 6", "EP 4, EP 5", "Activa"),
                GridKt._getFormattedRow(gridWithId("unit-children-grid"), 0).subList(0, 6), "la pestana de unidades, con sus hijas");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-edit")));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("unit-code")).isReadOnly());
        ComboBox<UnitSummaryDto> parent = comboWithId("unit-parent");
        assertEquals(List.of("PRJ", "MNT"), ComboBoxKt.getSuggestionItems(parent).stream().map(UnitSummaryDto::code).toList());
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<RefItem> packages = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("unit-packages"));
        assertEquals(Set.of(new RefItem(4L, "EP 4")), packages.getValue());
        LocatorJ._setValue(packages, Set.of(new RefItem(6L, "EP 6"), new RefItem(4L, "EP 4")));
        LocatorJ._setValue(comboWithId("unit-head"), null);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        MergePatch<OrganizationUnitUpdateRequest> expected = new MergePatch<>(new OrganizationUnitUpdateRequest(null, null, null,
                new java.util.TreeSet<>(Set.of(4L, 6L)), null, null, null, null), Set.of("headPersonId"), 2L);
        verify(organizationClient).update(SUPERVISION_UNIT, expected);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Conflicto con otro cambio: recarga y vuelve a intentarlo."));
        assertFalse(LocatorJ._find(UnitEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        verify(organizationClient, times(2)).update(SUPERVISION_UNIT, expected);

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-history")));
        assertEquals("Supervision norte · Supervision · Mantenimiento · en MNT · jefe per-000020 · dana levi · EP 4 · activa",
                GridKt._getFormattedRow(gridWithId("revisions-grid"), 0).get(5));
    }

    /** Sin config-read no se llama a mto-configuration: los EP se ven como #id, de solo lectura, y no viajan. */
    @Test
    void withoutConfigReadTheUnitPackagesAreIdsAndDoNotTravel() {
        loginAs("recursos.gestor", "ROLE_RESOURCES_READ", "ROLE_RESOURCES_WRITE");
        stubOrganization(true);
        when(organizationClient.findById(SUPERVISION_UNIT)).thenReturn(supervision(true));
        when(organizationClient.children(eq(SUPERVISION_UNIT), anyInt(), anyInt())).thenReturn(page(List.of(), 0, 200));
        when(organizationClient.update(any(), any())).thenReturn(supervision(true));

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(SUPERVISION_UNIT));
        LocatorJ._get(Div.class, spec -> spec.withText("Disciplina: Mantenimiento · Depende de: MNT · Mantenimiento · "
                + "Jefe: PER-000020 · Dana Levi · EP: #4"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-edit")));
        assertEquals("#4", LocatorJ._get(TextField.class, spec -> spec.withId("unit-packages-readonly")).getValue());
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("unit-name")), "Supervision del norte");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        verify(organizationClient).update(SUPERVISION_UNIT, new MergePatch<>(new OrganizationUnitUpdateRequest("Supervision del norte", null, null,
                null, null, null, null, null), Set.of(), 2L));
        verify(executionPackageClient, never()).filter(anyInt(), anyInt(), anyList(), anyMap());
    }

    /**
     * La regla de 2 a 6 en pantalla: con 6 no se ofrece anadir, con 2 no se ofrece que salga nadie, y
     * cambiar varios es la lista entera de una vez, con su contador y su fecha. Lo que el servicio
     * rechaza (409 UNI-001) deja el dialogo abierto.
     */
    @Test
    void aTeamAddsLinemenUpToSixLetsThemLeaveDownToTwoAndChangesThemAllAtOnce() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        stubOrganization(true);
        when(organizationClient.findById(TEAM_UNIT)).thenReturn(team(6, true));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(6));

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(TEAM_UNIT));
        Button add = LocatorJ._get(Button.class, spec -> spec.withId("member-add"));
        assertFalse(add.isEnabled(), "con 6 no se anade otro");
        Grid<Object> members = gridWithId("members-grid");
        assertTrue(LocatorJ._get(GridKt._getCellComponent(members, 0, "actions"), Button.class,
                spec -> spec.withId("member-leave-" + lineman(0).id())).isEnabled());

        when(organizationClient.findById(TEAM_UNIT)).thenReturn(team(2, true));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(2));
        when(organizationClient.addMember(eq(TEAM_UNIT), any()))
                .thenThrow(maintenanceError(409, "UNI-001", "Lineman 5 is already a lineman of team B"))
                .thenReturn(team(3, true));
        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(TEAM_UNIT));
        members = gridWithId("members-grid");
        assertFalse(LocatorJ._get(GridKt._getCellComponent(members, 0, "actions"), Button.class,
                spec -> spec.withId("member-leave-" + lineman(0).id())).isEnabled(), "con 2 no sale nadie");
        add = LocatorJ._get(Button.class, spec -> spec.withId("member-add"));
        assertTrue(add.isEnabled());
        LocatorJ._click(add);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("member-add-save")));
        verify(organizationClient, never()).addMember(any(), any());
        LocatorJ._setValue(comboWithId("member-person"), lineman(5));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("member-add-save")));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El organigrama no admite ese cambio. Lineman 5 is already a lineman of team B"));
        assertFalse(LocatorJ._find(Button.class, spec -> spec.withId("member-add-save")).isEmpty(), "el dialogo sigue abierto");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("member-add-save")));
        verify(organizationClient, times(2)).addMember(TEAM_UNIT, new TeamMemberRequest(lineman(5).id(), null));
        LocatorJ._get(H2.class, spec -> spec.withText("A · Equipo A"));
        LocatorJ._get(Div.class, spec -> spec.withId("unit-team-summary").withText("Linemen: 3 de 2 a 6 · Completo: Si · "
                + "Supervisor: PER-000020 · Dana Levi · Jefe de supervisores: - · Vehiculo: -"));

        when(organizationClient.findById(TEAM_UNIT)).thenReturn(team(3, true));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(3));
        when(organizationClient.leave(eq(TEAM_UNIT), any(), any())).thenReturn(team(2, true));
        when(organizationClient.replaceMembers(eq(TEAM_UNIT), any())).thenReturn(team(2, true));
        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(TEAM_UNIT));
        members = gridWithId("members-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(members, 2, "actions"), Button.class,
                spec -> spec.withId("member-leave-" + lineman(2).id())));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("member-left-on")), LocalDate.of(2026, 10, 21));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(2));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("member-leave-save")));
        verify(organizationClient).leave(TEAM_UNIT, lineman(2).id(), new TeamMemberLeaveRequest(LocalDate.of(2026, 10, 21)));

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("members-replace")));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<PersonSummaryDto> chosen = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("members-people"));
        assertEquals(Set.of(lineman(0), lineman(1)), chosen.getValue(), "parte de los linemen de ahora");
        LocatorJ._get(Span.class, spec -> spec.withId("members-count").withText("2 de 2 a 6"));
        LocatorJ._setValue(chosen, Set.of(lineman(0)));
        LocatorJ._get(Span.class, spec -> spec.withId("members-count").withText("1 de 2 a 6"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamMembersDialog.SAVE_ID)));
        verify(organizationClient, never()).replaceMembers(any(), any());
        assertTrue(chosen.isInvalid());
        LocatorJ._setValue(chosen, new java.util.LinkedHashSet<>(List.of(lineman(0), lineman(3), lineman(4))));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("members-date")), LocalDate.of(2026, 10, 22));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamMembersDialog.SAVE_ID)));
        verify(organizationClient).replaceMembers(eq(TEAM_UNIT), argThat(request -> Set.copyOf(request.memberPersonIds())
                .equals(Set.of(lineman(0).id(), lineman(3).id(), lineman(4).id())) && request.effectiveOn().equals(LocalDate.of(2026, 10, 22))));
    }

    /**
     * Desactivar pide resources-delete y confirmacion; lo que el servicio no deja (unidades activas
     * debajo) se dice. Un equipo vuelve completo: reactivarlo pide sus linemen en su dialogo; otra
     * unidad vuelve con un PATCH active=true.
     */
    @Test
    void aDeactivatedTeamComesBackWithItsLinemenAndAnotherUnitByPatch() {
        loginAs("recursos.responsable", RESOURCES_ADMIN);
        stubOrganization(false);
        when(organizationClient.findById(SUPERVISION_UNIT)).thenReturn(supervision(true));
        when(organizationClient.children(eq(SUPERVISION_UNIT), anyInt(), anyInt())).thenReturn(page(List.of(team(0, false)), 0, 200));
        doThrow(maintenanceError(409, "UNI-001", "SUP-N has active units under it")).when(organizationClient).deactivate(SUPERVISION_UNIT);

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(SUPERVISION_UNIT));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-deactivate")));
        verify(organizationClient, never()).deactivate(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(organizationClient).deactivate(SUPERVISION_UNIT);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El organigrama no admite ese cambio. SUP-N has active units under it"));

        when(organizationClient.findById(TEAM_UNIT)).thenReturn(team(0, false));
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(List.of());
        when(organizationClient.update(eq(TEAM_UNIT), any())).thenReturn(team(2, true));
        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(TEAM_UNIT));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("unit-deactivate")).isEmpty(), "una inactiva no se desactiva otra vez");
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("member-add")).stream().noneMatch(Button::isVisible),
                "un equipo inactivo no cambia sus linemen: vuelve con ellos");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-reactivate")));
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<PersonSummaryDto> chosen = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("members-people"));
        assertTrue(chosen.getValue().isEmpty(), "sus linemen salieron al desactivarlo");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamMembersDialog.SAVE_ID)));
        verify(organizationClient, never()).update(any(), any());
        LocatorJ._setValue(chosen, new java.util.LinkedHashSet<>(List.of(lineman(1), lineman(2))));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(TeamMembersDialog.SAVE_ID)));
        verify(organizationClient).update(eq(TEAM_UNIT), argThat(patch -> patch.version() == 7L && Boolean.TRUE.equals(patch.values().active())
                && Set.copyOf(patch.values().memberPersonIds()).equals(Set.of(lineman(1).id(), lineman(2).id()))));
        LocatorJ._get(Span.class, spec -> spec.withId("unit-state").withText("Activa"));

        when(organizationClient.findById(SUPERVISION_UNIT)).thenReturn(supervision(false));
        when(organizationClient.update(eq(SUPERVISION_UNIT), any())).thenReturn(supervision(true));
        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(SUPERVISION_UNIT));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-reactivate")));
        verify(organizationClient).update(SUPERVISION_UNIT, MergePatch.of(OrganizationUnitUpdateRequest.reactivate(), 2L));
    }

    /** Personal toma la unidad de la URL (lo que abre la ficha de una unidad) y la quita al pedirlo. */
    @Test
    void thePeopleOfAUnitComeFromTheUrlAndTheFilterIsRemovedOnRequest() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubOrganization(true);
        when(organizationClient.findById(SUPERVISION_UNIT)).thenReturn(supervision(true));
        when(organizationClient.children(eq(SUPERVISION_UNIT), anyInt(), anyInt())).thenReturn(page(List.of(), 0, 200));

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(SUPERVISION_UNIT));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-people")));
        LocatorJ._get(PeopleView.class);
        LocatorJ._get(Span.class, spec -> spec.withId("people-unit").withText("Unidad: SUP-N · Supervision norte"));
        GridKt._size(gridWithId("people-grid"));
        verify(personClient, atLeastOnce()).search(eq(new PersonFilter(null, Set.of(), SUPERVISION_UNIT, null)), eq(0), anyInt(), anyList());

        clearInvocations(personClient);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("people-unit-clear")));
        assertTrue(LocatorJ._find(Span.class, spec -> spec.withId("people-unit")).isEmpty(), "sin unidad no hay filtro de unidad");
        GridKt._size(gridWithId("people-grid"));
        verify(personClient, atLeastOnce()).search(eq(PersonFilter.NONE), eq(0), anyInt(), anyList());

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE, QueryParameters.of(PeopleView.UNIT_PARAMETER, "no-es-un-id"));
        assertTrue(LocatorJ._find(Span.class, spec -> spec.withId("people-unit")).isEmpty(), "un id que no es un UUID no filtra");
    }

    // --- Recursos: modelos de vehiculo y sitios de aparcamiento -------------------------------------

    private static final UUID VEHICLE_TYPE1 = UUID.fromString("7b8c9d0e-0000-4000-8000-000000000001");
    private static final UUID VEHICLE_TYPE2 = UUID.fromString("7b8c9d0e-0000-4000-8000-000000000002");
    private static final UUID PARKING_SITE1 = UUID.fromString("8c9d0e1f-0000-4000-8000-000000000001");
    private static final String[] FLEET_MANAGER = {"ROLE_RESOURCES_READ", "ROLE_RESOURCES_WRITE", "ROLE_RESOURCES_FLEET_WRITE",
            "ROLE_CONFIG_READ", "ROLE_NOTIFICATION_INBOX"};

    private static VehicleTypeDto unimog(Set<String> capabilities, BigDecimal crane, boolean active) {
        return unimog(capabilities, crane, active, null);
    }

    /** La bivial con la habilitacion que exige a su conductor ({@code null}: ninguna). */
    private static VehicleTypeDto unimog(Set<String> capabilities, BigDecimal crane, boolean active, QualificationTypeSummaryDto driver) {
        return new VehicleTypeDto(VEHICLE_TYPE1, "UNIMOG-RR", "Unimog bivial", "Mercedes-Benz", "U423", VehicleMobility.ROAD_RAIL,
                VehicleCategory.ROAD_RAIL_TRUCK, true, false, new BigDecimal("6.20"), null, null, null, null, null, 2, 3, null, VehiclePower.DIESEL,
                1435, 20, 40, null, null, null, false, RailGearType.RAIL_WHEEL_DRIVE, 10, null, 90, DrivingLicence.C, capabilities, crane, null,
                null, null, null, null, null, active, 4L, driver);
    }

    private static VehicleTypeDto dresina() {
        return new VehicleTypeDto(VEHICLE_TYPE2, "DIC-30", "Plasser DIC-30", "Plasser", "DIC-30", VehicleMobility.RAIL_ONLY,
                VehicleCategory.CATENARY_TOWER_CAR, true, false, null, null, null, null, null, null, 4, 6, null, VehiclePower.DIESEL, 1435, 10, 80,
                null, null, null, true, null, null, null, null, null, Set.of("ELEVATING_PLATFORM", "PANTOGRAPH"), null, null, null, null, null,
                null, null, true, 2L, null);
    }

    private void stubVehicleTypes(List<VehicleTypeDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(vehicleTypeClient).search(any(VehicleTypeFilter.class), anyInt(), anyInt(), anyList());
    }

    private void stubConfigurationNames() {
        when(executionPackageClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(executionPackage(4L, "EP 4"), executionPackage(5L, "EP 5")), 0, 1000));
        when(trackClient.filter(anyInt(), anyInt(), anyList(), anyMap()))
                .thenReturn(page(List.of(track(12L, "VIA 1", true, 4L, List.of()), track(20L, "VIA 9", true, 5L, List.of())), 0, 1000));
        when(stationClient.filter(anyInt(), anyInt(), anyList(), anyMap())).thenReturn(page(List.of(station(7L, "Rishpon", 4L)), 0, 1000));
    }

    /** Los modelos se piden al servicio con sus filtros; quien solo lee no ve controles y el menu ofrece la flota. */
    @Test
    void theVehicleModelsAreFilteredInTheServerAndAReaderHasNoControls() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubVehicleTypes(List.of(unimog(Set.of("CRANE", "LASER_SCANNER"), new BigDecimal("5.00"), true), dresina()));

        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().containsAll(List.of("Modelos de vehiculo", "Sitios de aparcamiento")), menuLabels().toString());
        UI.getCurrent().navigate(ResourcesRoutes.VEHICLE_TYPES);

        Grid<Object> grid = gridWithId("vehicle-types-grid");
        assertEquals(List.of("UNIMOG-RR", "Unimog bivial", "Mercedes-Benz · U423", "Bivial", "Camion bivial", "3", "Grua, LASER_SCANNER", "",
                "Activo"), GridKt._getFormattedRow(grid, 0).subList(0, 9), "una capacidad que no se conoce se pinta con su codigo");
        verify(vehicleTypeClient, atLeastOnce()).search(eq(VehicleTypeFilter.NONE), eq(0), anyInt(), eq(List.of("code,asc")));

        @SuppressWarnings("unchecked")
        Select<VehicleMobility> mobility = LocatorJ._get(Select.class, spec -> spec.withId("vehicle-types-mobility"));
        LocatorJ._setValue(mobility, VehicleMobility.RAIL_ONLY);
        ComboBox<VehicleCategory> category = comboWithId("vehicle-types-category");
        assertEquals(VehicleCategory.of(VehicleMobility.RAIL_ONLY), ComboBoxKt.getSuggestionItems(category), "las categorias de esa movilidad");
        LocatorJ._setValue(category, VehicleCategory.CATENARY_TOWER_CAR);
        LocatorJ._setValue(comboWithId("vehicle-types-capability"), "PANTOGRAPH");
        GridKt._size(grid);
        verify(vehicleTypeClient, atLeastOnce()).search(eq(new VehicleTypeFilter(null, VehicleMobility.RAIL_ONLY, VehicleCategory.CATENARY_TOWER_CAR,
                "PANTOGRAPH", null)), eq(0), anyInt(), anyList());

        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("vehicle-type-create")).isEmpty());
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, VehicleTypesView.ACTIONS_COLUMN), Button.class).isEmpty());
    }

    /**
     * El alta va por secciones segun la movilidad: uno de solo carretera no ofrece cifras de via ni de
     * bivial; una bivial dice su equipo ferroviario; cada capacidad ensena sus cifras; los numeros se
     * comprueban con su columna antes de llamar.
     */
    @Test
    void aModelIsCreatedBySectionsOfItsMobilityAndItsFiguresAreCheckedBeforeCalling() {
        loginAs("recursos.gestor", FLEET_MANAGER);
        stubVehicleTypes(List.of());
        when(vehicleTypeClient.create(any())).thenReturn(unimog(Set.of("CRANE"), new BigDecimal("5.00"), true));

        UI.getCurrent().navigate(ResourcesRoutes.VEHICLE_TYPES);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("vehicle-type-create")));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-type-code")), "UNIMOG-RR");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-type-name")), "Unimog bivial");
        @SuppressWarnings("unchecked")
        Select<VehicleMobility> mobility = LocatorJ._get(Select.class, spec -> spec.withId("vehicle-type-mobility"));
        LocatorJ._setValue(mobility, VehicleMobility.ROAD_ONLY);
        assertTrue(LocatorJ._find(VerticalLayout.class, spec -> spec.withId("vehicle-type-section-track")).isEmpty(), "sin cifras de via");
        assertTrue(LocatorJ._find(VerticalLayout.class, spec -> spec.withId("vehicle-type-section-road-rail")).isEmpty(), "ni de bivial");
        LocatorJ._get(VerticalLayout.class, spec -> spec.withId("vehicle-type-section-road"));
        ComboBox<VehicleCategory> category = comboWithId("vehicle-type-category");
        assertEquals(VehicleCategory.of(VehicleMobility.ROAD_ONLY), ComboBoxKt.getSuggestionItems(category));
        LocatorJ._setValue(category, VehicleCategory.VAN);

        LocatorJ._setValue(mobility, VehicleMobility.ROAD_RAIL);
        assertNull(category.getValue(), "una furgoneta no es una bivial");
        LocatorJ._get(VerticalLayout.class, spec -> spec.withId("vehicle-type-section-track"));
        LocatorJ._get(VerticalLayout.class, spec -> spec.withId("vehicle-type-section-road-rail"));
        LocatorJ._setValue(category, VehicleCategory.ROAD_RAIL_TRUCK);
        assertTrue(LocatorJ._find(BigDecimalField.class, spec -> spec.withId("vehicle-type-craneCapacityT")).isEmpty(), "sin grua no hay cifras de grua");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<String> capabilities = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("vehicle-type-capabilities"));
        LocatorJ._setValue(capabilities, Set.of("CRANE"));
        BigDecimalField crane = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("vehicle-type-craneCapacityT"));
        LocatorJ._setValue(crane, new BigDecimal("5.5"));
        BigDecimalField length = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("vehicle-type-lengthM"));
        LocatorJ._setValue(length, new BigDecimal("12345"));
        LocatorJ._setValue(LocatorJ._get(IntegerField.class, spec -> spec.withId("vehicle-type-trackGaugeMm")), 1435);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleTypeEditorDialog.SAVE_ID)));
        verify(vehicleTypeClient, never()).create(any());
        assertEquals("Como mucho 4 cifras enteras", length.getErrorMessage());
        assertTrue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-type-rail-gear")).isInvalid(), "una bivial dice su equipo ferroviario");

        LocatorJ._setValue(length, new BigDecimal("6.20"));
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-type-rail-gear")), RailGearType.RAIL_WHEEL_DRIVE);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleTypeEditorDialog.SAVE_ID)));
        verify(vehicleTypeClient).create(argThat(request -> request.code().equals("UNIMOG-RR") && request.mobility() == VehicleMobility.ROAD_RAIL
                && request.category() == VehicleCategory.ROAD_RAIL_TRUCK && request.railGearType() == RailGearType.RAIL_WHEEL_DRIVE
                && Boolean.FALSE.equals(request.needsPreparedAccess()) && Boolean.FALSE.equals(request.transportModeAuthorised())
                && request.trackGaugeMm() == 1435 && new BigDecimal("6.20").equals(request.lengthM())
                && new BigDecimal("5.5").equals(request.craneCapacityT()) && request.capabilities().equals(Set.of("CRANE"))
                && Boolean.TRUE.equals(request.selfPropelled())));
        assertTrue(LocatorJ._find(VehicleTypeEditorDialog.class).isEmpty());
    }

    /**
     * Modificar no cambia el codigo ni la movilidad, manda lo cambiado y la version, y quitar una
     * capacidad vacia sus cifras; una capacidad que no se conoce vuelve tal cual. Lo que el servicio
     * dice de un modelo incoherente se notifica con el dialogo abierto.
     */
    @Test
    void aModelIsPatchedWithWhatChangedAndRemovingACapabilityEmptiesItsFigures() {
        loginAs("recursos.gestor", FLEET_MANAGER);
        VehicleTypeDto existing = unimog(Set.of("CRANE", "LASER_SCANNER"), new BigDecimal("5.00"), true);
        stubVehicleTypes(List.of(existing));
        when(vehicleTypeClient.update(any(), any()))
                .thenThrow(maintenanceError(400, "VAL-001", "Crane figures (craneCapacityT, craneReachM) need the CRANE capability"))
                .thenReturn(existing);

        UI.getCurrent().navigate(ResourcesRoutes.VEHICLE_TYPES);
        Grid<Object> grid = gridWithId("vehicle-types-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, VehicleTypesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("vehicle-type-edit-" + VEHICLE_TYPE1)));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-type-code")).isReadOnly());
        assertTrue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-type-mobility")).isReadOnly());
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<String> capabilities = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("vehicle-type-capabilities"));
        assertEquals(Set.of("CRANE", "LASER_SCANNER"), capabilities.getValue());
        LocatorJ._setValue(capabilities, Set.of("LASER_SCANNER", "TOWING"));
        assertTrue(LocatorJ._find(BigDecimalField.class, spec -> spec.withId("vehicle-type-craneCapacityT")).isEmpty());
        LocatorJ._setValue(LocatorJ._get(IntegerField.class, spec -> spec.withId("vehicle-type-seats")), 5);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleTypeEditorDialog.SAVE_ID)));

        MergePatch<VehicleTypeUpdateRequest> expected = new MergePatch<>(new VehicleTypeUpdateRequest(null, null, null, null, null, null, null,
                null, null, null, null, null, null, 5, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                Set.of("LASER_SCANNER", "TOWING"), null, null, null, null, null, null, null, null, null), Set.of("craneCapacityT"), 4L);
        verify(vehicleTypeClient).update(VEHICLE_TYPE1, expected);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("La peticion no es valida. Crane figures (craneCapacityT, craneReachM) need the CRANE capability"));
        assertFalse(LocatorJ._find(VehicleTypeEditorDialog.class).isEmpty(), "el dialogo sigue abierto con lo escrito");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleTypeEditorDialog.SAVE_ID)));
        verify(vehicleTypeClient, times(2)).update(VEHICLE_TYPE1, expected);
    }

    /** Retirar un modelo pide resources-delete y confirmacion; volver a ofrecerlo es un PATCH active=true. */
    @Test
    void aModelIsRetiredWithConfirmationAndOfferedAgainByPatch() {
        loginAs("recursos.responsable", RESOURCES_ADMIN);
        VehicleTypeDto retired = unimog(Set.of(), null, false);
        stubVehicleTypes(List.of(dresina(), retired));
        when(vehicleTypeClient.update(any(), any())).thenReturn(unimog(Set.of(), null, true));

        UI.getCurrent().navigate(ResourcesRoutes.VEHICLE_TYPES);
        Grid<Object> grid = gridWithId("vehicle-types-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, VehicleTypesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("vehicle-type-retire-" + VEHICLE_TYPE2)));
        verify(vehicleTypeClient, never()).deactivate(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(vehicleTypeClient).deactivate(VEHICLE_TYPE2);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, VehicleTypesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("vehicle-type-activate-" + VEHICLE_TYPE1)));
        verify(vehicleTypeClient).update(VEHICLE_TYPE1, MergePatch.of(VehicleTypeUpdateRequest.active(true), 4L));
    }

    private static ParkingSiteDto siding(boolean active) {
        return new ParkingSiteDto(PARKING_SITE1, "APT-RSH", "Apartadero de Rishpon", ParkingSiteKind.SIDING, 4L, 7L, 12L, new BigDecimal("12.300"),
                true, false, null, new BigDecimal("32.200000"), new BigDecimal("34.820000"), null, active, 2L);
    }

    /**
     * Un sitio se alcanza por via, por carretera o por las dos; con acceso por via dice su via; las
     * coordenadas van juntas. Las vias que se ofrecen son las del EP elegido.
     */
    @Test
    void aParkingSiteNeedsAnAccessAndATrackForRailAccessAndItsCoordinatesTogether() {
        loginAs("recursos.gestor", FLEET_MANAGER);
        stubConfigurationNames();
        when(parkingSiteClient.search(any(ParkingSiteFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(siding(true)), 0, 50));
        when(parkingSiteClient.create(any())).thenReturn(siding(true));

        UI.getCurrent().navigate(ResourcesRoutes.PARKING_SITES);
        assertEquals(List.of("APT-RSH", "Apartadero de Rishpon", "Apartadero", "EP 4", "VIA 1 (EP 4) · kp 12.300", "Via", "Activo"),
                GridKt._getFormattedRow(gridWithId("parking-sites-grid"), 0).subList(0, 7));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("parking-site-create")));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("parking-site-code")), "APT-RSH");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("parking-site-name")), "Apartadero de Rishpon");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("parking-site-kind")), ParkingSiteKind.SIDING);
        Checkbox rail = LocatorJ._get(Checkbox.class, spec -> spec.withId("parking-site-rail-access"));
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("parking-site-road-access")), false);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(ParkingSiteEditorDialog.SAVE_ID)));
        verify(parkingSiteClient, never()).create(any());
        assertEquals("Un sitio se alcanza por via, por carretera o por las dos", rail.getErrorMessage());

        LocatorJ._setValue(rail, true);
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-site-latitude")), new BigDecimal("32.2"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(ParkingSiteEditorDialog.SAVE_ID)));
        verify(parkingSiteClient, never()).create(any());
        ComboBox<RefItem> track = comboWithId("parking-site-track");
        assertTrue(track.isInvalid(), "con acceso por via dice su via");
        assertTrue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-site-latitude")).isInvalid(), "las coordenadas van juntas");

        LocatorJ._setValue(comboWithId("parking-site-package"), new RefItem(4L, "EP 4"));
        assertEquals(List.of("VIA 1 (EP 4)"), ComboBoxKt.getSuggestionItems(track).stream().map(RefItem::label).toList(), "las vias del EP");
        LocatorJ._setValue(track, new RefItem(12L, "VIA 1 (EP 4)"));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-site-latitude")), null);
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-site-kp")), new BigDecimal("12.300"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(ParkingSiteEditorDialog.SAVE_ID)));
        verify(parkingSiteClient).create(new ParkingSiteRequest("APT-RSH", "Apartadero de Rishpon", ParkingSiteKind.SIDING, 4L, null, 12L,
                new BigDecimal("12.300"), true, false, null, null, null, null));
    }

    /** Modificar un sitio manda lo cambiado; sin config-read su EP, estacion y via se ven como #id y no viajan. */
    @Test
    void withoutConfigReadAParkingSiteKeepsItsPlacesAndOnlyWhatChangedTravels() {
        loginAs("recursos.gestor", "ROLE_RESOURCES_READ", "ROLE_RESOURCES_WRITE");
        when(parkingSiteClient.search(any(ParkingSiteFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(siding(true)), 0, 50));
        when(parkingSiteClient.update(any(), any())).thenReturn(siding(true));

        UI.getCurrent().navigate(ResourcesRoutes.PARKING_SITES);
        Grid<Object> grid = gridWithId("parking-sites-grid");
        assertEquals(List.of("#4", "#12 · kp 12.300"), GridKt._getFormattedRow(grid, 0).subList(3, 5));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, ParkingSitesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("parking-site-edit-" + PARKING_SITE1)));
        assertEquals("#4 · #7 · #12", LocatorJ._get(TextField.class, spec -> spec.withId("parking-site-where-readonly")).getValue());
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("parking-site-road-access")), true);
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-site-latitude")), null);
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("parking-site-longitude")), null);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(ParkingSiteEditorDialog.SAVE_ID)));
        verify(parkingSiteClient).update(PARKING_SITE1, new MergePatch<>(new ParkingSiteUpdateRequest(null, null, null, null, null, null, null,
                true, null, null, null, null, null), Set.of("latitude", "longitude"), 2L));
        verify(executionPackageClient, never()).filter(anyInt(), anyInt(), anyList(), anyMap());
    }

    // --- Recursos: vehiculos y ubicaciones ----------------------------------------------------------

    private static final UUID VEHICLE1 = UUID.fromString("9d0e1f20-0000-4000-8000-000000000001");
    private static final UUID VEHICLE2 = UUID.fromString("9d0e1f20-0000-4000-8000-000000000002");
    private static final UUID LOCATION_SHIFT = UUID.fromString("a0b1c2d3-0000-4000-8000-000000000001");
    private static final String[] FLEET_OPERATOR = {"ROLE_RESOURCES_READ", "ROLE_RESOURCES_FLEET_WRITE", "ROLE_RESOURCES_LOCATION_WRITE",
            "ROLE_RESOURCES_DELETE", "ROLE_CONFIG_READ", "ROLE_MAINTENANCE_READ", "ROLE_NOTIFICATION_INBOX"};
    private static final VehicleTypeSummaryDto RAIL_MODEL = new VehicleTypeSummaryDto(VEHICLE_TYPE2, "DIC-30", "Plasser DIC-30",
            VehicleMobility.RAIL_ONLY, VehicleCategory.CATENARY_TOWER_CAR, true, 6, null, true, Set.of("PANTOGRAPH"));
    private static final VehicleTypeSummaryDto ROAD_RAIL_MODEL = new VehicleTypeSummaryDto(VEHICLE_TYPE1, "UNIMOG-RR", "Unimog bivial",
            VehicleMobility.ROAD_RAIL, VehicleCategory.ROAD_RAIL_TRUCK, true, 3, RailGearType.RAIL_WHEEL_DRIVE, false, Set.of("TOWING"));

    private static VehicleLocationDto location(VehicleLocationKind kind, Long trackId, String kp, VehicleSummaryDto carrier, String reference) {
        return new VehicleLocationDto(UUID.randomUUID(), kind, kind == VehicleLocationKind.PARKED
                ? new ParkingSiteSummaryDto(PARKING_SITE1, "APT-RSH", "Apartadero de Rishpon", ParkingSiteKind.SIDING) : null, 4L, trackId,
                kp == null ? null : new BigDecimal(kp), null, null, null, null, null, carrier, carrier == null ? null : VehicleAttachment.TOWED,
                reference == null ? VehicleLocationSource.MANUAL : VehicleLocationSource.SHIFT, reference, Instant.parse("2026-10-16T03:40:00Z"),
                Instant.parse("2026-10-16T03:40:02Z"), reference == null ? "recursos.gestor" : "service-account-mto-maintenance-svc");
    }

    private static VehicleDto vehicle(UUID id, String code, VehicleTypeSummaryDto type, VehicleLocationDto position, boolean active) {
        return new VehicleDto(id, code, "Vehiculo " + code, type, type.mobility() == VehicleMobility.RAIL_ONLY ? null : "1234-ABC",
                type.mobility() == VehicleMobility.ROAD_ONLY ? null : "99 80 9427 001-1", null, 2019, VehicleOwner.OWN, null,
                VehicleOperationalStatus.OPERATIONAL, null, true, null, null, null, position, active, 6L, null);
    }

    private void stubVehicles(List<VehicleDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(vehicleClient).search(any(VehicleFilter.class), anyInt(), anyInt(), anyList());
        for (VehicleDto one : all) {
            when(vehicleClient.findById(one.id())).thenReturn(one);
        }
    }

    /** La lista dice donde esta cada vehiculo y se filtra en el servicio; quien solo lee solo abre las fichas. */
    @Test
    void theVehiclesSayWhereEachIsAndAReaderOnlyOpensThem() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubConfigurationNames();
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, location(VehicleLocationKind.PARKED, 12L, "12.300", null, LOCATION_SHIFT.toString()), true),
                vehicle(VEHICLE2, "UN-01", ROAD_RAIL_MODEL, null, false)));

        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().contains("Vehiculos"), menuLabels().toString());
        UI.getCurrent().navigate(ResourcesRoutes.FLEET);
        Grid<Object> grid = gridWithId("vehicles-grid");
        assertEquals(List.of("DR-01", "Vehiculo DR-01", "DIC-30", "Solo via", "", "99 80 9427 001-1", "Operativo",
                "Aparcado · APT-RSH · VIA 1 (EP 4) · kp 12.300", "Activo"), GridKt._getFormattedRow(grid, 0).subList(0, 9));
        assertEquals(List.of("Sin ubicacion", "Retirado"), GridKt._getFormattedRow(grid, 1).subList(7, 9));
        verify(vehicleClient, atLeastOnce()).search(eq(VehicleFilter.NONE), eq(0), anyInt(), eq(List.of("code,asc")));

        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicles-mobility")), VehicleMobility.RAIL_ONLY);
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicles-available")), EnabledFilter.ENABLED);
        GridKt._size(grid);
        verify(vehicleClient, atLeastOnce()).search(eq(new VehicleFilter(null, null, VehicleMobility.RAIL_ONLY, null, null, true, null)), eq(0),
                anyInt(), anyList());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("vehicle-create")).isEmpty());
        assertEquals(List.of("vehicle-open-" + VEHICLE1), LocatorJ._find(GridKt._getCellComponent(grid, 0, VehiclesView.ACTIONS_COLUMN),
                Button.class).stream().map(button -> button.getId().orElse("")).toList());
    }

    /**
     * El editor exige lo que pide su modelo: matricula si va por carretera (ninguna en uno de solo via),
     * numero ferroviario si su modelo va por via abierta, la empresa de uno que no es propio y el motivo
     * de uno que no esta operativo.
     */
    @Test
    void aVehicleAsksForWhatItsModelNeedsBeforeCalling() {
        loginAs("recursos.gestor", FLEET_OPERATOR);
        stubConfigurationNames();
        stubVehicles(List.of());
        when(vehicleClient.create(any())).thenReturn(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true));

        UI.getCurrent().navigate(ResourcesRoutes.FLEET);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("vehicle-create")));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-code")), "DR-01");
        LocatorJ._setValue(comboWithId("vehicle-type"), RAIL_MODEL);
        assertTrue(LocatorJ._find(TextField.class, spec -> spec.withId("vehicle-plate")).isEmpty(), "uno de solo via no lleva matricula");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-owner")), VehicleOwner.SUBCONTRACTOR);
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-status")), VehicleOperationalStatus.UNDER_REPAIR);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleEditorDialog.SAVE_ID)));
        verify(vehicleClient, never()).create(any());
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-rail-number")).isInvalid(), "su modelo va por via abierta");
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-owner-company")).isInvalid());
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-status-reason")).isInvalid());

        LocatorJ._setValue(comboWithId("vehicle-type"), ROAD_RAIL_MODEL);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleEditorDialog.SAVE_ID)));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-plate")).isInvalid(), "una bivial va por carretera");
        assertFalse(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-rail-number")).isInvalid(), "su modelo no va por via abierta");

        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-plate")), "1234-ABC");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-owner-company")), "Alquileres del Norte");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-status-reason")), "Gato hidraulico");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleEditorDialog.SAVE_ID)));
        verify(vehicleClient).create(new VehicleRequest("DR-01", null, VEHICLE_TYPE1, "1234-ABC", null, null, null, VehicleOwner.SUBCONTRACTOR,
                "Alquileres del Norte", VehicleOperationalStatus.UNDER_REPAIR, "Gato hidraulico", null, null, null, null));
    }

    /**
     * La ficha dice donde esta: uno que va remolcado dice con quien y donde esta su portador. Sus
     * ubicaciones se filtran por dias (el fin viaja como el principio del dia siguiente) y la que dejo
     * un turno enlaza con el turno.
     */
    @Test
    void theVehicleDetailSaysWhereItIsAndATowedOneWhereItsCarrierIs() {
        loginAs("recursos.gestor", FLEET_OPERATOR);
        stubConfigurationNames();
        VehicleDto carrier = vehicle(VEHICLE2, "DR-02", RAIL_MODEL, location(VehicleLocationKind.ON_TRACK, 12L, "10.5", null, null), true);
        VehicleDto towed = vehicle(VEHICLE1, "PL-01", RAIL_MODEL, location(VehicleLocationKind.ATTACHED, null, null, carrier.summary(), null), true);
        stubVehicles(List.of(towed, carrier));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.of(
                location(VehicleLocationKind.PARKED, 12L, "12.300", null, VehicleLocationDto.SHIFT_REFERENCE + LOCATION_SHIFT)), 0, 20));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        LocatorJ._get(Div.class, spec -> spec.withId("vehicle-position-place").withText("Remolcado por DR-02"));
        LocatorJ._get(Div.class, spec -> spec.withId("vehicle-carrier-position").withText("DR-02 esta: En via · VIA 1 (EP 4) · kp 10.5"));
        Grid<Object> locations = gridWithId("locations-grid");
        assertEquals(List.of("Aparcado · APT-RSH · VIA 1 (EP 4) · kp 12.300", "", "Turno", "service-account-mto-maintenance-svc"),
                GridKt._getFormattedRow(locations, 0).subList(1, 5));
        verify(vehicleClient, atLeastOnce()).locations(VEHICLE1, null, null, 0, 20);
        LocatorJ._get(GridKt._getCellComponent(locations, 0, "actions"), Button.class, spec -> spec.withId("location-shift-" + ((VehicleLocationDto) GridKt._get(locations, 0)).id()));

        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("locations-from")), LocalDate.of(2026, 10, 1));
        LocatorJ._setValue(LocatorJ._get(DatePicker.class, spec -> spec.withId("locations-to")), LocalDate.of(2026, 10, 16));
        GridKt._size(locations);
        verify(vehicleClient, atLeastOnce()).locations(VEHICLE1, Formats.startOfDay(LocalDate.of(2026, 10, 1)),
                Formats.startOfDay(LocalDate.of(2026, 10, 17)), 0, 20);
    }

    /** Una ficha que no existe se dice y vuelve a la flota. */
    @Test
    void aVehicleThatDoesNotExistGoesBackToTheFleet() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubConfigurationNames();
        stubVehicles(List.of());
        UUID missing = UUID.fromString("9d0e1f20-0000-4000-8000-0000000000ff");
        when(vehicleClient.findById(missing)).thenThrow(maintenanceError(404, "VEH-404", "Vehicle not found"));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(missing));
        LocatorJ._get(VehiclesView.class);
        NotificationsKt.expectNotifications("No existe el vehiculo " + missing);
    }

    /**
     * Registrar donde esta: solo los tipos que admite su movilidad y los campos de cada uno; en uno de
     * solo via, los sitios con acceso por via. Lo evidente se comprueba antes de llamar y lo que el
     * servicio rechaza (409 VEH-001) deja el dialogo abierto.
     */
    @Test
    void aLocationOffersOnlyWhatItsMobilityAdmitsAndTheServiceSaysTheRest() {
        loginAs("recursos.gestor", FLEET_OPERATOR);
        stubConfigurationNames();
        VehicleDto dresina = vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true);
        stubVehicles(List.of(dresina));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.of(), 0, 20));
        when(parkingSiteClient.search(any(ParkingSiteFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(siding(true)), 0, 200));
        when(vehicleClient.recordLocation(eq(VEHICLE1), any()))
                .thenThrow(maintenanceError(409, "VEH-001", "Vehicle DR-01 is a rail-only vehicle: it is parked on a track"))
                .thenReturn(location(VehicleLocationKind.ON_TRACK, 12L, "10.5", null, null));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("vehicle-locate")));
        verify(parkingSiteClient).search(eq(new ParkingSiteFilter(null, null, null, null, true, null, true)), eq(0), eq(200),
                eq(List.of("code,asc")));
        @SuppressWarnings("unchecked")
        Select<VehicleLocationKind> kind = LocatorJ._get(Select.class, spec -> spec.withId("location-kind"));
        assertEquals(VehicleLocationKind.recordable(VehicleMobility.RAIL_ONLY), ComboBoxKt.getSuggestionItems(kind), "uno de solo via no va por carretera");
        assertTrue(LocatorJ._find(BigDecimalField.class, spec -> spec.withId("location-latitude")).size() == 1);
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("location-description")), "En la playa de vias");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        verify(vehicleClient, never()).recordLocation(any(), any());
        assertTrue(comboWithId("location-site").isInvalid(), "uno de solo via se aparca sobre una via");

        LocatorJ._setValue(kind, VehicleLocationKind.ON_TRACK);
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("location-site")).isEmpty(), "en via no hay sitio");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        assertTrue(comboWithId("location-track").isInvalid(), "en via dice su via");

        LocatorJ._setValue(comboWithId("location-track"), new RefItem(12L, "VIA 1 (EP 4)"));
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("location-kp")), new BigDecimal("10.5"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("La flota no admite ese cambio. Vehicle DR-01 is a rail-only vehicle: it is parked on a track"));
        assertFalse(LocatorJ._find(VehicleLocationDialog.class).isEmpty(), "el dialogo sigue abierto");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        verify(vehicleClient, times(2)).recordLocation(VEHICLE1, new VehicleLocationRequest(VehicleLocationKind.ON_TRACK, null, null, 12L,
                new BigDecimal("10.5"), null, null, null, null, "En la playa de vias", null, null, null));
        assertTrue(LocatorJ._find(VehicleLocationDialog.class).isEmpty());
    }

    private static VehicleLocationDto withAccessPoint(VehicleLocationDto location, String accessPoint, AccessPointSummaryDto trackAccessPoint) {
        return new VehicleLocationDto(location.id(), location.kind(), location.parkingSite(), location.executionPackageId(), location.trackId(),
                location.kp(), location.latitude(), location.longitude(), accessPoint, trackAccessPoint, location.description(), location.carrier(),
                location.attachment(), location.source(), location.sourceReference(), location.observedAt(), location.recordedAt(), location.recordedBy());
    }

    /**
     * El punto de acceso por el que encarrilo: donde se ofrece la via y en cuanto hay una elegida, los
     * activos de esa via, pedidos a mto-resources cada vez que cambia (que lo vacia); un fallo al pedirlos
     * lo dice su ayuda, sin aviso. Viaja como accessPointId; el 404 TAP-404 vuelve a pedir la lista y se dice
     * en su campo, y el 409 VEH-001 (no le vale al vehiculo), con el dialogo abierto. La ficha y el historial
     * dicen su nombre, y el texto libre, si no hay otro.
     */
    @Test
    void aLocationOffersTheAccessPointsOfItsTrackAndTheVehicleSaysWhichOneItUsed() {
        loginAs("recursos.gestor", FLEET_OPERATOR);
        stubConfigurationNames();
        AccessPointSummaryDto used = new AccessPointSummaryDto(77L, "Acceso km 110+176", AccessPointKind.PREPARED_ACCESS);
        VehicleLocationDto onTrack = withAccessPoint(location(VehicleLocationKind.ON_TRACK, 12L, "10.5", null, null), "Camino de la base", used);
        stubVehicles(List.of(vehicle(VEHICLE1, "UN-01", ROAD_RAIL_MODEL, onTrack, true)));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.of(onTrack,
                withAccessPoint(location(VehicleLocationKind.ON_TRACK, 12L, "9.0", null, null), "LC-31", null)), 0, 20));
        when(parkingSiteClient.search(any(ParkingSiteFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(), 0, 200));
        AccessPointDto prepared = new AccessPointDto(77L, "Acceso km 110+176", 12L, new BigDecimal("110176.000"), AccessPointKind.PREPARED_ACCESS, true,
                new BigDecimal("12.500"), new BigDecimal("40.000"), true);
        AccessPointDto crossing = new AccessPointDto(78L, "Paso a nivel LC-31", 12L, new BigDecimal("98375.500"), AccessPointKind.LEVEL_CROSSING, false,
                null, null, true);
        when(accessPointClient.enabledOnTrack(12L)).thenReturn(List.of(prepared, crossing), List.of(prepared, crossing), List.of(prepared));
        when(accessPointClient.enabledOnTrack(20L)).thenThrow(maintenanceError(503, "SVC-503", "mto-resources is not available"));
        when(vehicleClient.recordLocation(eq(VEHICLE1), any()))
                .thenThrow(maintenanceError(404, "TAP-404", "Track access point 78 not found"))
                .thenThrow(maintenanceError(409, "VEH-001", "Access point Acceso km 110+176 is not on track 12"))
                .thenReturn(onTrack);

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        LocatorJ._get(Div.class, spec -> spec.withText("EP EP 4 · Acceso: Acceso km 110+176"));
        Grid<Object> locations = gridWithId("locations-grid");
        assertEquals("Acceso km 110+176", GridKt._getFormattedRow(locations, 0).get(2), "el del maestro manda sobre el texto");
        assertEquals("LC-31", GridKt._getFormattedRow(locations, 1).get(2), "sin el, el texto libre");

        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("vehicle-locate")));
        @SuppressWarnings("unchecked")
        Select<VehicleLocationKind> kind = LocatorJ._get(Select.class, spec -> spec.withId("location-kind"));
        LocatorJ._setValue(kind, VehicleLocationKind.ON_ROAD);
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("location-access-point")).isEmpty(), "por carretera no hay via");
        assertEquals("Acceso (texto)", LocatorJ._get(TextField.class, spec -> spec.withId("location-access-point-text")).getLabel());
        LocatorJ._setValue(kind, VehicleLocationKind.ON_TRACK);
        assertTrue(LocatorJ._find(ComboBox.class, spec -> spec.withId("location-access-point")).isEmpty(), "sin via elegida no se ofrece");
        verify(accessPointClient, never()).enabledOnTrack(any());

        LocatorJ._setValue(comboWithId("location-track"), new RefItem(12L, "VIA 1 (EP 4)"));
        ComboBox<AccessPointDto> accessPoint = comboWithId("location-access-point");
        assertEquals(List.of("Acceso km 110+176 · Acceso preparado · kp 110176.000", "Paso a nivel LC-31 · Paso a nivel · kp 98375.500 · sin biviales"),
                ComboBoxKt.getSuggestionItems(accessPoint).stream().map(AccessPointDto::label).toList());
        LocatorJ._setValue(accessPoint, crossing);
        LocatorJ._setValue(comboWithId("location-track"), new RefItem(20L, "VIA 9 (EP 5)"));
        assertNull(accessPoint.getValue(), "cambiar de via lo vacia: era de la otra");
        assertTrue(ComboBoxKt.getSuggestionItems(accessPoint).isEmpty());
        assertTrue(accessPoint.getHelperText().startsWith("No se han podido leer los puntos de acceso de la via"), accessPoint.getHelperText());
        assertTrue(NotificationsKt.getNotifications().isEmpty(), "un fallo al pedirlos no se avisa");

        LocatorJ._setValue(comboWithId("location-track"), new RefItem(12L, "VIA 1 (EP 4)"));
        LocatorJ._setValue(accessPoint, crossing);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        verify(accessPointClient, times(3)).enabledOnTrack(12L);
        assertEquals(List.of(prepared), ComboBoxKt.getSuggestionItems(accessPoint), "el TAP-404 vuelve a pedir la lista");
        assertNull(accessPoint.getValue());
        assertTrue(accessPoint.isInvalid(), "y se dice en su campo");
        assertEquals("Ese punto de acceso ya no existe o aun no ha llegado a Recursos.", accessPoint.getErrorMessage());
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Ese punto de acceso ya no existe o aun no ha llegado a Recursos."));
        assertFalse(LocatorJ._find(VehicleLocationDialog.class).isEmpty(), "el dialogo sigue abierto");
        LocatorJ._setValue(accessPoint, prepared);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        assertFalse(accessPoint.isInvalid());
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("La flota no admite ese cambio. Access point Acceso km 110+176 is not on track 12"));
        assertFalse(LocatorJ._find(VehicleLocationDialog.class).isEmpty(), "el dialogo sigue abierto");
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(VehicleLocationDialog.SAVE_ID)));
        verify(vehicleClient, times(2)).recordLocation(VEHICLE1, new VehicleLocationRequest(VehicleLocationKind.ON_TRACK, null, null, 12L, null, null,
                null, 77L, null, null, null, null, null));
        assertTrue(LocatorJ._find(VehicleLocationDialog.class).isEmpty());
    }

    /** Retirar pide confirmacion, y lo que el servicio no deja (el vehiculo por defecto de un equipo) se dice; reactivar es un PATCH. */
    @Test
    void aVehicleIsRetiredWithConfirmationAndTheDefaultVehicleOfATeamIsSaid() {
        loginAs("recursos.gestor", FLEET_OPERATOR);
        stubConfigurationNames();
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true), vehicle(VEHICLE2, "DR-02", RAIL_MODEL, null, false)));
        doThrow(maintenanceError(409, "VEH-001", "Vehicle DR-01 is the default vehicle of team A")).when(vehicleClient).deactivate(VEHICLE1);
        when(vehicleClient.update(any(), any())).thenReturn(vehicle(VEHICLE2, "DR-02", RAIL_MODEL, null, true));

        UI.getCurrent().navigate(ResourcesRoutes.FLEET);
        Grid<Object> grid = gridWithId("vehicles-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, VehiclesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("vehicle-retire-" + VEHICLE1)));
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(vehicleClient).deactivate(VEHICLE1);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("La flota no admite ese cambio. Vehicle DR-01 is the default vehicle of team A"));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, VehiclesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("vehicle-activate-" + VEHICLE2)));
        verify(vehicleClient).update(VEHICLE2, MergePatch.of(VehicleUpdateRequest.active(true), 6L));
    }

    // --- Recursos: tablero de la flota y vehiculo por defecto ------------------------------------------

    /**
     * El tablero va por via y kp, con el vehiculo para desempatar; la via y el kp de uno remolcado son
     * los de su portador y la fila dice con quien va. Filtra por EP y via en el servicio, «Actualizar»
     * vuelve a pedirlo, y registrar donde esta lee antes el vehiculo.
     */
    @Test
    void theFleetBoardGoesByTrackAndKpAndATowedVehicleIsWhereItsCarrierIs() {
        loginAs("recursos.gestor", FLEET_OPERATOR);
        stubConfigurationNames();
        VehicleDto carrier = vehicle(VEHICLE2, "DR-02", RAIL_MODEL, location(VehicleLocationKind.ON_TRACK, 12L, "10.5", null, null), true);
        VehicleLocationDto towedAt = location(VehicleLocationKind.ATTACHED, null, null, carrier.summary(), null);
        stubVehicles(List.of(vehicle(VEHICLE1, "PL-01", RAIL_MODEL, towedAt, true), carrier));
        when(parkingSiteClient.search(any(ParkingSiteFilter.class), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(), 0, 200));
        when(vehicleClient.positions(any(), any(), any(), anyInt(), anyInt(), anyList())).thenReturn(page(List.of(
                new FleetPositionDto(carrier.summary(), VehicleOperationalStatus.OPERATIONAL, true, carrier.position(), null),
                new FleetPositionDto(new VehicleSummaryDto(VEHICLE1, "PL-01", "Vehiculo PL-01", VehicleMobility.RAIL_ONLY, VehicleCategory.RAIL_WAGON),
                        VehicleOperationalStatus.OPERATIONAL, true, towedAt, carrier.position())), 0, 50));

        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().contains("Tablero de la flota"), menuLabels().toString());
        UI.getCurrent().navigate(ResourcesRoutes.BOARD);
        Grid<Object> grid = gridWithId("board-grid");
        assertEquals(List.of("VIA 1 (EP 4)", "10.5", "DR-02 · Vehiculo DR-02", "Solo via", "En via", "", "Operativo"),
                GridKt._getFormattedRow(grid, 0).subList(0, 7));
        assertEquals(List.of("VIA 1 (EP 4)", "10.5", "PL-01 · Vehiculo PL-01", "Solo via", "Remolcado por DR-02"),
                GridKt._getFormattedRow(grid, 1).subList(0, 5), "uno remolcado esta donde su portador");
        verify(vehicleClient, atLeastOnce()).positions(null, null, null, 0, 50, VehicleClient.BY_TRACK_AND_KP);

        LocatorJ._setValue(comboWithId("board-package"), new RefItem(4L, "EP 4"));
        ComboBox<RefItem> track = comboWithId("board-track");
        assertEquals(List.of("VIA 1 (EP 4)"), ComboBoxKt.getSuggestionItems(track).stream().map(RefItem::label).toList(), "las vias del EP");
        LocatorJ._setValue(track, new RefItem(12L, "VIA 1 (EP 4)"));
        GridKt._size(grid);
        verify(vehicleClient, atLeastOnce()).positions(4L, 12L, null, 0, 50, VehicleClient.BY_TRACK_AND_KP);
        clearInvocations(vehicleClient);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("board-refresh")));
        GridKt._size(grid);
        verify(vehicleClient, atLeastOnce()).positions(4L, 12L, null, 0, 50, VehicleClient.BY_TRACK_AND_KP);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, FleetBoardView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("board-locate-" + VEHICLE1)));
        verify(vehicleClient).findById(VEHICLE1);
        LocatorJ._get(VehicleLocationDialog.class);
    }

    /** El vehiculo por defecto de un equipo se elige en su editor y viaja en el merge-patch; vaciarlo viaja a null. */
    @Test
    void aTeamCarriesItsDefaultVehicleAndItIsEmptiedByPatch() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        stubOrganization(true);
        OrganizationUnitDto withVehicle = new OrganizationUnitDto(TEAM_UNIT, "A", "Equipo A", OrganizationUnitKind.TEAM, WorkDiscipline.MAINTENANCE,
                new UnitSummaryDto(SUPERVISION_UNIT, "SUP-N", "Supervision norte", OrganizationUnitKind.SUPERVISION), LEADER, Set.of(4L, 5L),
                new VehicleSummaryDto(VEHICLE1, "DR-01", "Dresina 1", VehicleMobility.RAIL_ONLY, VehicleCategory.CATENARY_TOWER_CAR), linemen(2), 2,
                true, null, null, true, 7L);
        when(organizationClient.findById(TEAM_UNIT)).thenReturn(withVehicle);
        when(organizationClient.members(TEAM_UNIT, false)).thenReturn(linemen(2));
        when(organizationClient.update(any(), any())).thenReturn(withVehicle);
        stubVehicles(List.of(vehicle(VEHICLE2, "DR-02", RAIL_MODEL, null, true)));

        UI.getCurrent().navigate(UnitDetailView.class, UnitDetailView.parametersOf(TEAM_UNIT));
        LocatorJ._get(Div.class, spec -> spec.withId("unit-team-summary").withText("Linemen: 2 de 2 a 6 · Completo: Si · Supervisor: - · "
                + "Jefe de supervisores: - · Vehiculo: DR-01 · Dresina 1"));
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId("unit-edit")));
        ComboBox<VehicleSummaryDto> vehicle = comboWithId("unit-default-vehicle");
        assertEquals("DR-01", vehicle.getValue().code());
        assertEquals(List.of("DR-02"), ComboBoxKt.getSuggestionItems(vehicle).stream().map(VehicleSummaryDto::code).toList());
        verify(vehicleClient, atLeastOnce()).search(eq(VehicleFilter.availableMatching("")), eq(0), anyInt(), anyList());
        LocatorJ._setValue(vehicle, null);
        LocatorJ._click(LocatorJ._get(Button.class, spec -> spec.withId(UnitEditorDialog.SAVE_ID)));
        verify(organizationClient).update(TEAM_UNIT, new MergePatch<>(new OrganizationUnitUpdateRequest(null, null, null, null, null, null, null,
                null), Set.of("defaultVehicleId"), 7L));
    }

    // --- Recursos: cualificaciones, documentos e instrumentos (fase 4, bloque C) -------------------------

    private static final UUID QTYPE_RAIL = UUID.fromString("6a7b8c9d-0000-4000-8000-000000000001");
    private static final UUID QTYPE_LIC_C = UUID.fromString("6a7b8c9d-0000-4000-8000-000000000002");
    private static final UUID QTYPE_RR = UUID.fromString("6a7b8c9d-0000-4000-8000-000000000003");
    private static final UUID QTYPE_SAFETY = UUID.fromString("6a7b8c9d-0000-4000-8000-000000000004");
    private static final UUID QUAL_RAIL = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000a1");
    private static final UUID QUAL_LIC = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000a2");
    private static final UUID QUAL_SAFETY = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000a3");
    private static final UUID DOC_ITV = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000d1");
    private static final UUID DOC_INSURANCE = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000d2");
    private static final UUID INSTRUMENT1 = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000e1");
    private static final UUID INSTRUMENT2 = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000e2");
    private static final UUID INSTRUMENT3 = UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000e3");
    private static final QualificationTypeDto RAIL_DRIVER = new QualificationTypeDto(QTYPE_RAIL, "RAIL-DRIVER", "Rail auxiliary vehicle driver",
            QualificationKind.VEHICLE_OPERATION, null, 60, null, true, 1L);
    private static final QualificationTypeDto LICENCE_C = new QualificationTypeDto(QTYPE_LIC_C, "LIC-C", "Driving licence C",
            QualificationKind.DRIVING_LICENCE, DrivingLicence.C, 120, null, true, 2L);
    private static final QualificationTypeDto RR_OPERATOR = new QualificationTypeDto(QTYPE_RR, "RR-OPERATOR", "Road-rail machine operator",
            QualificationKind.VEHICLE_OPERATION, null, null, null, true, 1L);

    private static PersonQualificationDto qualification(UUID id, QualificationTypeDto type, LocalDate expiresOn, Boolean expired) {
        return new PersonQualificationDto(id, PERSON1, type.summary(), "RD-118", LocalDate.of(2026, 4, 1), expiresOn, null, expired, 3L);
    }

    private static VehicleDocumentDto document(UUID id, VehicleDocumentKind kind, String reference, LocalDate expiresOn, boolean expired) {
        return new VehicleDocumentDto(id, VEHICLE1, kind, reference, LocalDate.of(2026, 2, 28), expiresOn, null, expired, 1L);
    }

    private static InstrumentDto instrument(UUID id, String code, InstrumentKind kind, LocalDate expiresOn, Boolean expired, boolean active) {
        return new InstrumentDto(id, code, "Instrumento " + code, kind, "SN-" + code, LocalDate.of(2026, 3, 31), expiresOn, expired, null, active, 5L);
    }

    private void stubQualificationTypes(List<QualificationTypeDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(qualificationTypeClient).search(any(QualificationTypeFilter.class), anyInt(), anyInt(), anyList());
    }

    private void stubInstruments(List<InstrumentDto> all) {
        doAnswer(call -> page(all, call.getArgument(1), call.getArgument(2)))
                .when(instrumentClient).search(any(InstrumentFilter.class), anyInt(), anyInt(), anyList());
    }

    private static DatePicker datePicker(String id) {
        return LocatorJ._get(DatePicker.class, spec -> spec.withId(id));
    }

    /** El estado de una caducidad lo dice el servicio ({@code expired}); los dias que faltan, hasta 30, son solo presentacion. */
    @Test
    void theExpiryStateIsTheServicesAndOnlyTheDaysLeftAreCounted() {
        LocalDate today = LocalDate.of(2026, 10, 17);
        assertEquals("Caducada", Expiry.state(today.minusDays(1), true, today, "Caducada", "No caduca"));
        assertEquals("Caducada", Expiry.state(today.plusDays(2), true, today, "Caducada", "No caduca"), "manda el servicio, con su fecha y su zona");
        assertEquals("Caducada", Expiry.state(today.minusDays(1), null, today, "Caducada", "No caduca"), "un servicio anterior se lee por la fecha");
        assertEquals("Caduca hoy", Expiry.state(today, false, today, "Caducada", "No caduca"));
        assertEquals("Caduca hoy", Expiry.state(today.minusDays(1), false, today, "Caducada", "No caduca"), "si el servicio dice que no, aun no");
        assertEquals("Caduca en 1 dia", Expiry.state(today.plusDays(1), false, today, "Caducada", "No caduca"));
        assertEquals("Caduca en 30 dias", Expiry.state(today.plusDays(30), false, today, "Caducada", "No caduca"));
        assertEquals("Vigente", Expiry.state(today.plusDays(31), false, today, "Caducada", "No caduca"));
        assertEquals("No caduca", Expiry.state(null, false, today, "Caducada", "No caduca"));
        assertEquals("Sin calibracion", Expiry.state(null, null, today, "Caducada", "Sin calibracion"));
    }

    /** Los codigos nuevos de mto-resources y de mantenimiento dicen que pasa; los 409 no piden recargar. */
    @Test
    void qualificationsDocumentsAndInstrumentsErrorsSayWhatBlocksTheChange() {
        assertEquals("Ya existe otro tipo de cualificacion con ese codigo.",
                UiErrors.message(maintenanceError(409, "QTY-409", "Qualification type LIC-C already exists")));
        assertEquals("La persona ya tiene esa cualificacion, o su tipo esta retirado: para renovarla, cambia su caducidad. "
                + "Person PER-000003 already has LIC-C", UiErrors.message(maintenanceError(409, "QUA-001", "Person PER-000003 already has LIC-C")));
        assertEquals("El vehiculo ya tiene un documento de ese tipo: para renovarlo, cambia su caducidad.",
                UiErrors.message(maintenanceError(409, "VDO-409", "Vehicle DR-01 already has INSURANCE")));
        assertEquals("Ya existe otro instrumento con ese codigo.", UiErrors.message(maintenanceError(409, "INT-409", "Instrument PT-01 already exists")));
        assertEquals("Esa cualificacion ya no existe.", UiErrors.message(maintenanceError(404, "QUA-404", "Qualification not found")));
        assertEquals("Ese documento del vehiculo ya no existe.", UiErrors.message(maintenanceError(404, "VDO-404", "Document not found")));
        assertEquals("Ese instrumento no existe.", UiErrors.message(maintenanceError(404, "INT-404", "Instrument not found")));
        assertEquals("Ese tipo de cualificacion no existe.", UiErrors.message(maintenanceError(404, "QTY-404", "Qualification type not found")));
        assertEquals("Ese punto de acceso ya no existe o aun no ha llegado a Recursos.",
                UiErrors.message(maintenanceError(404, "TAP-404", "Track access point 77 not found")));
        assertEquals("Ese instrumento aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento.",
                UiErrors.message(maintenanceError(404, "MSI-404", "Maintenance instrument not found")));
        assertEquals("No se ha encontrado lo que se pedia. Something", UiErrors.message(maintenanceError(404, null, "Something")),
                "un 404 sin codigo sigue siendo el de siempre");
    }

    /** Cualificaciones e Instrumentos estan en el grupo Recursos con resources-read. */
    @Test
    void theQualificationsAndTheInstrumentsAreInTheResourcesMenu() {
        loginAs("recursos.lector", RESOURCES_READER);
        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().containsAll(List.of("Recursos", "Cualificaciones", "Instrumentos")), menuLabels().toString());
    }

    /** Sin resources-read no estan en el menu, y un rol de realm que se llama como el permiso no abre ni ellas ni la ficha de una persona. */
    @Test
    void withoutResourcesReadNeitherTheQualificationsNorTheInstrumentsOpen() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        UI.getCurrent().navigate(HomeView.class);
        assertFalse(menuLabels().contains("Cualificaciones"), menuLabels().toString());
        assertFalse(menuLabels().contains("Instrumentos"), menuLabels().toString());

        loginAs("recursos.impostor", "ROLE_REALM_RESOURCES_READ");
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(ResourcesRoutes.QUALIFICATION_TYPES));
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(ResourcesRoutes.INSTRUMENTS));
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(PERSON1)));
        verify(qualificationTypeClient, never()).search(any(QualificationTypeFilter.class), anyInt(), anyInt(), anyList());
        verify(instrumentClient, never()).search(any(InstrumentFilter.class), anyInt(), anyInt(), anyList());
        verify(personClient, never()).findById(any());
    }

    /**
     * Una fila de Personal abre la ficha de la persona, con doble clic o con su boton: su cabecera y la
     * pestana Cualificaciones con el estado de cada una. Quien solo lee no ve nada que escribir, y una
     * persona que no existe se dice y vuelve a la lista.
     */
    @Test
    void aRowOfThePeopleOpensTheDetailWithItsQualificationsAndAReaderOnlyReadsThem() {
        loginAs("recursos.lector", RESOURCES_READER);
        PersonDto yossi = person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true);
        stubPeople(List.of(yossi));
        when(personClient.findById(PERSON1)).thenReturn(yossi);
        LocalDate soon = LocalDate.now().plusDays(10);
        LocalDate later = LocalDate.now().plusYears(2);
        when(personClient.qualifications(PERSON1)).thenReturn(List.of(qualification(QUAL_RAIL, RAIL_DRIVER, soon, false),
                qualification(QUAL_LIC, LICENCE_C, LocalDate.now().minusDays(1), true),
                new PersonQualificationDto(QUAL_SAFETY, PERSON1, new QualificationTypeSummaryDto(QTYPE_SAFETY, "SAFE-1", "Trabajos en altura",
                        QualificationKind.UNKNOWN), null, null, null, null, false, 1L),
                qualification(UUID.randomUUID(), RR_OPERATOR, later, false)));

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE);
        GridKt._doubleClickItem(gridWithId("people-grid"), 0, 1, false, false, false, false);

        LocatorJ._get(PersonDetailView.class);
        LocatorJ._get(H2.class, spec -> spec.withText("PER-000003 · Yossi Cohen"));
        assertEquals("Jefe de equipo", spanText("person-category-badge"));
        assertEquals("Activa", spanText("person-state"));
        assertTrue(textOf("person-summary").contains("Codigo: PER-000003 · Empresa: Propio · Usuario: -"), textOf("person-summary"));
        Grid<Object> qualifications = gridWithId("qualifications-grid");
        assertEquals(List.of("RAIL-DRIVER · Rail auxiliary vehicle driver", "Manejo de vehiculos", "RD-118", "01/04/2026", Formats.date(soon),
                "Caduca en 10 dias"), GridKt._getFormattedRow(qualifications, 0).subList(0, 6));
        assertEquals("Caducada", GridKt._getFormattedRow(qualifications, 1).get(5), "si ha caducado lo dice el servicio");
        assertEquals(List.of("SAFE-1 · Trabajos en altura", "Desconocido", "", "", "", "No caduca"),
                GridKt._getFormattedRow(qualifications, 2).subList(0, 6), "una clase nueva se lee como desconocida");
        assertEquals("Vigente", GridKt._getFormattedRow(qualifications, 3).get(5));
        assertEquals("4 cualificaciones", spanText("qualifications-count"));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("qualification-add")).isEmpty(), "sin resources-write no se anade");
        assertTrue(LocatorJ._find(GridKt._getCellComponent(qualifications, 0, "actions"), Button.class).isEmpty());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("person-edit")).isEmpty());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("person-deactivate")).isEmpty());
        LocatorJ._get(Button.class, spec -> spec.withId("person-history"));

        UI.getCurrent().navigate(ResourcesRoutes.PEOPLE);
        Button open = LocatorJ._get(GridKt._getCellComponent(gridWithId("people-grid"), 0, PeopleView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("person-open-" + PERSON1));
        assertEquals("Abrir PER-000003", open.getTooltip().getText());
        LocatorJ._click(open);
        LocatorJ._get(PersonDetailView.class);

        UUID missing = UUID.fromString("3c4d5e6f-0000-4000-8000-0000000000ff");
        when(personClient.findById(missing)).thenThrow(maintenanceError(404, "PER-404", "Person not found"));
        UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(missing));
        LocatorJ._get(PeopleView.class);
    }

    /**
     * El alta de una cualificacion ofrece los tipos activos que la persona no tiene, dice la vigencia
     * habitual del tipo, comprueba lo evidente antes de llamar y deja el dialogo abierto con el error del
     * servicio en su campo o con su QUA-001. Sin resources-delete no se quita.
     */
    @Test
    void aQualificationIsAddedWithItsTypeAndTheServiceSaysWhatItRejects() {
        loginAs("recursos.gestor", RESOURCES_MANAGER);
        PersonDto yossi = person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true);
        when(personClient.findById(PERSON1)).thenReturn(yossi);
        when(personClient.qualifications(PERSON1)).thenReturn(List.of(qualification(QUAL_RAIL, RAIL_DRIVER, LocalDate.now().plusYears(1), false)));
        stubQualificationTypes(List.of(LICENCE_C, RAIL_DRIVER));
        PersonQualificationRequest expected = new PersonQualificationRequest(QTYPE_LIC_C, "C-12345", LocalDate.of(2026, 4, 1),
                LocalDate.of(2036, 4, 1), null);
        ApiProblem invalid = new ApiProblem(null, "Bad Request", 400, "Validation failed", null, "VAL-001", null, null, null, false,
                List.of(new ApiFieldError("expiresOn", null, "must be within 20 years")), null);
        when(personClient.addQualification(eq(PERSON1), any()))
                .thenThrow(BackofficeApiException.of(HttpStatus.BAD_REQUEST, invalid, "corr-q2", null, "POST /api/resources/people/x/qualifications"))
                .thenThrow(maintenanceError(409, "QUA-001", "Person PER-000003 already has LIC-C"))
                .thenReturn(qualification(QUAL_LIC, LICENCE_C, LocalDate.of(2036, 4, 1), false));

        UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(PERSON1));
        Grid<Object> grid = gridWithId("qualifications-grid");
        LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("qualification-edit-" + QUAL_RAIL));
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, "actions"), Button.class,
                spec -> spec.withId("qualification-remove-" + QUAL_RAIL)).isEmpty(), "quitar pide resources-delete");

        click("qualification-add");
        ComboBox<QualificationTypeDto> type = comboWithId("qualification-type");
        assertEquals(List.of("LIC-C · Driving licence C"), ComboBoxKt.getSuggestions(type), "los activos que aun no tiene");
        verify(qualificationTypeClient).search(QualificationTypeFilter.ACTIVE, 0, 200, List.of("code,asc"));
        click(PersonQualificationDialog.SAVE_ID);
        verify(personClient, never()).addQualification(any(), any());
        assertTrue(type.isInvalid(), "el tipo es obligatorio");

        LocatorJ._setValue(type, LICENCE_C);
        assertEquals("Vacia: no caduca. Vigencia habitual: 120 meses", datePicker("qualification-expires-on").getHelperText());
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("qualification-reference")), " C-12345 ");
        LocatorJ._setValue(datePicker("qualification-issued-on"), LocalDate.of(2026, 4, 1));
        LocatorJ._setValue(datePicker("qualification-expires-on"), LocalDate.of(2026, 3, 1));
        click(PersonQualificationDialog.SAVE_ID);
        verify(personClient, never()).addQualification(any(), any());
        assertEquals("No puede caducar antes de expedirse", datePicker("qualification-expires-on").getErrorMessage());

        LocatorJ._setValue(datePicker("qualification-expires-on"), LocalDate.of(2036, 4, 1));
        click(PersonQualificationDialog.SAVE_ID);
        assertTrue(datePicker("qualification-expires-on").isInvalid(), "el error del servicio, en su campo");
        assertEquals("must be within 20 years", datePicker("qualification-expires-on").getErrorMessage());
        LocatorJ._get(PersonQualificationDialog.class);

        click(PersonQualificationDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "La persona ya tiene esa cualificacion, o su tipo esta retirado: para renovarla, cambia su caducidad. Person PER-000003 already has LIC-C"));
        LocatorJ._get(PersonQualificationDialog.class);

        click(PersonQualificationDialog.SAVE_ID);
        verify(personClient, times(3)).addQualification(PERSON1, expected);
        assertTrue(LocatorJ._find(PersonQualificationDialog.class).isEmpty());
        verify(personClient, times(2)).qualifications(PERSON1);
    }

    /**
     * Renovar una cualificacion manda lo cambiado, lo vaciado y la version leida; sin cambios no se
     * llama. Quitarla pide resources-delete y confirmacion.
     */
    @Test
    void aQualificationIsRenewedWithWhatChangedAndRemovedWithConfirmation() {
        loginAs("recursos.responsable", RESOURCES_ADMIN);
        PersonDto yossi = person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true);
        when(personClient.findById(PERSON1)).thenReturn(yossi);
        PersonQualificationDto rail = qualification(QUAL_RAIL, RAIL_DRIVER, LocalDate.of(2027, 3, 31), false);
        when(personClient.qualifications(PERSON1)).thenReturn(List.of(rail));
        when(personClient.updateQualification(eq(PERSON1), eq(QUAL_RAIL), any())).thenReturn(rail);

        UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(PERSON1));
        Grid<Object> grid = gridWithId("qualifications-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("qualification-edit-" + QUAL_RAIL)));
        TextField type = LocatorJ._get(TextField.class, spec -> spec.withId("qualification-type-readonly"));
        assertTrue(type.isReadOnly());
        assertEquals("RAIL-DRIVER · Rail auxiliary vehicle driver", type.getValue());
        LocatorJ._setValue(datePicker("qualification-expires-on"), LocalDate.of(2028, 3, 31));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("qualification-reference")), "");
        click(PersonQualificationDialog.SAVE_ID);
        verify(personClient).updateQualification(PERSON1, QUAL_RAIL, new MergePatch<>(new PersonQualificationUpdateRequest(null, null,
                LocalDate.of(2028, 3, 31), null), Set.of("reference"), 3L));
        assertTrue(LocatorJ._find(PersonQualificationDialog.class).isEmpty());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("qualification-edit-" + QUAL_RAIL)));
        click(PersonQualificationDialog.SAVE_ID);
        verify(personClient, times(1)).updateQualification(any(), any(), any());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("qualification-remove-" + QUAL_RAIL)));
        verify(personClient, never()).removeQualification(any(), any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(personClient).removeQualification(PERSON1, QUAL_RAIL);
        NotificationsKt.expectNotifications("Guardada RAIL-DRIVER · Rail auxiliary vehicle driver", "Quitada RAIL-DRIVER · Rail auxiliary vehicle driver");
    }

    /** El catalogo se pide al servicio con sus filtros; quien solo lee no ve controles. */
    @Test
    void theQualificationTypesAreFilteredInTheServerAndAReaderHasNoControls() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubQualificationTypes(List.of(LICENCE_C, RAIL_DRIVER));

        UI.getCurrent().navigate(ResourcesRoutes.QUALIFICATION_TYPES);
        Grid<Object> grid = gridWithId("qualification-types-grid");
        assertEquals(List.of("LIC-C", "Driving licence C", "Permiso de conducir", "C", "120 meses", "Activo"),
                GridKt._getFormattedRow(grid, 0).subList(0, 6));
        assertEquals(List.of("RAIL-DRIVER", "Manejo de vehiculos", ""), List.of(GridKt._getFormattedRow(grid, 1).get(0),
                GridKt._getFormattedRow(grid, 1).get(2), GridKt._getFormattedRow(grid, 1).get(3)));
        verify(qualificationTypeClient, atLeastOnce()).search(eq(QualificationTypeFilter.NONE), eq(0), anyInt(), eq(List.of("code,asc")));

        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("qualification-types-kind")), QualificationKind.VEHICLE_OPERATION);
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("qualification-types-state")), EnabledFilter.ENABLED);
        GridKt._size(grid);
        verify(qualificationTypeClient, atLeastOnce()).search(eq(new QualificationTypeFilter(null, QualificationKind.VEHICLE_OPERATION, true)),
                eq(0), anyInt(), anyList());
        @SuppressWarnings("unchecked")
        Select<QualificationKind> kind = LocatorJ._get(Select.class, spec -> spec.withId("qualification-types-kind"));
        assertFalse(kind.getListDataView().getItems().toList().contains(QualificationKind.UNKNOWN));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("qualification-type-create")).isEmpty());
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, QualificationTypesView.ACTIONS_COLUMN), Button.class).isEmpty());
    }

    /**
     * Un tipo se da de alta con el permiso solo si es un permiso de conducir (y entonces es obligatorio),
     * un codigo repetido se dice sin pedir recargar, modificar manda lo cambiado (pasar a otra clase vacia
     * el permiso) y retirar pide confirmacion.
     */
    @Test
    void aQualificationTypeIsCreatedPatchedAndRetiredAndTheLicenceOnlyGoesWithADrivingLicence() {
        loginAs("recursos.responsable", RESOURCES_ADMIN);
        stubQualificationTypes(List.of(LICENCE_C));
        when(qualificationTypeClient.create(any())).thenThrow(maintenanceError(409, "QTY-409", "Qualification type LIC-CE already exists"))
                .thenReturn(LICENCE_C);
        when(qualificationTypeClient.update(any(), any())).thenReturn(LICENCE_C);

        UI.getCurrent().navigate(ResourcesRoutes.QUALIFICATION_TYPES);
        click("qualification-type-create");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("qualification-type-code")), " lic-ce ");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("qualification-type-name")), "Driving licence C+E");
        assertTrue(LocatorJ._find(Select.class, spec -> spec.withId("qualification-type-licence")).isEmpty(), "sin clase no se pide permiso");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("qualification-type-kind")), QualificationKind.DRIVING_LICENCE);
        click(QualificationTypeEditorDialog.SAVE_ID);
        verify(qualificationTypeClient, never()).create(any());
        assertTrue(LocatorJ._get(Select.class, spec -> spec.withId("qualification-type-licence")).isInvalid(), "un permiso de conducir dice cual");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("qualification-type-licence")), DrivingLicence.C_E);
        click(QualificationTypeEditorDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText("Ya existe otro tipo de cualificacion con ese codigo."));
        LocatorJ._get(QualificationTypeEditorDialog.class);
        click(QualificationTypeEditorDialog.SAVE_ID);
        verify(qualificationTypeClient, times(2)).create(new QualificationTypeRequest("LIC-CE", "Driving licence C+E", QualificationKind.DRIVING_LICENCE,
                DrivingLicence.C_E, null, null));
        assertTrue(LocatorJ._find(QualificationTypeEditorDialog.class).isEmpty());

        Grid<Object> grid = gridWithId("qualification-types-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, QualificationTypesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("qualification-type-edit-" + QTYPE_LIC_C)));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("qualification-type-code")).isReadOnly());
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("qualification-type-kind")), QualificationKind.SAFETY);
        assertTrue(LocatorJ._find(Select.class, spec -> spec.withId("qualification-type-licence")).isEmpty());
        click(QualificationTypeEditorDialog.SAVE_ID);
        verify(qualificationTypeClient).update(QTYPE_LIC_C, new MergePatch<>(new QualificationTypeUpdateRequest(null, QualificationKind.SAFETY, null,
                null, null, null), Set.of("drivingLicence"), 2L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, QualificationTypesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("qualification-type-retire-" + QTYPE_LIC_C)));
        verify(qualificationTypeClient, never()).deactivate(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(qualificationTypeClient).deactivate(QTYPE_LIC_C);
    }

    /**
     * Los instrumentos se piden al servicio con sus filtros, tambien «calibracion caduca antes de», y dicen
     * como esta su calibracion; quien solo lee ve el historial y nada mas.
     */
    @Test
    void theInstrumentsAreFilteredByCalibrationAndSayHowTheirCalibrationIs() {
        loginAs("recursos.lector", RESOURCES_READER);
        LocalDate soon = LocalDate.now().plusDays(10);
        stubInstruments(List.of(instrument(INSTRUMENT1, "PT-01", InstrumentKind.EARTHING_POLE, soon, false, true),
                instrument(INSTRUMENT2, "DT-01", InstrumentKind.VOLTAGE_DETECTOR, LocalDate.now().minusDays(3), true, true),
                instrument(INSTRUMENT3, "TC-01", InstrumentKind.UNKNOWN, null, null, false)));
        when(instrumentClient.revisions(INSTRUMENT1, 0, RevisionsDialog.PAGE_SIZE)).thenReturn(page(List.of(new RevisionDto<>(
                new RevisionMetadataDto(3L, Instant.parse("2026-10-16T06:00:00Z"), RevisionOperation.UPDATED, "recursos.gestor", "HTTP", "c-3"),
                instrument(INSTRUMENT1, "PT-01", InstrumentKind.EARTHING_POLE, LocalDate.of(2027, 3, 31), false, true))), 0, RevisionsDialog.PAGE_SIZE));

        UI.getCurrent().navigate(ResourcesRoutes.INSTRUMENTS);
        Grid<Object> grid = gridWithId("instruments-grid");
        assertEquals(List.of("PT-01", "Instrumento PT-01", "Pertiga de puesta a tierra", "SN-PT-01", "31/03/2026", Formats.date(soon),
                "Caduca en 10 dias", "Activo"), GridKt._getFormattedRow(grid, 0).subList(0, 8));
        assertEquals("Caducada", GridKt._getFormattedRow(grid, 1).get(6), "si la calibracion caduco lo dice el servicio");
        assertEquals(List.of("Desconocido", "Sin calibracion", "Retirado"), List.of(GridKt._getFormattedRow(grid, 2).get(2),
                GridKt._getFormattedRow(grid, 2).get(6), GridKt._getFormattedRow(grid, 2).get(7)));
        verify(instrumentClient, atLeastOnce()).search(eq(InstrumentFilter.NONE), eq(0), anyInt(), eq(List.of("code,asc")));

        LocatorJ._setValue(datePicker("instruments-expires-before"), LocalDate.of(2026, 11, 30));
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("instruments-kind")), InstrumentKind.EARTHING_POLE);
        GridKt._size(grid);
        verify(instrumentClient, atLeastOnce()).search(eq(new InstrumentFilter(null, InstrumentKind.EARTHING_POLE, null, LocalDate.of(2026, 11, 30))),
                eq(0), anyInt(), anyList());

        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("instrument-create")).isEmpty());
        assertEquals(List.of("instrument-history-" + INSTRUMENT1), LocatorJ._find(GridKt._getCellComponent(grid, 0, InstrumentsView.ACTIONS_COLUMN),
                Button.class).stream().map(button -> button.getId().orElse("")).toList());
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, InstrumentsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("instrument-history-" + INSTRUMENT1)));
        assertEquals("Instrumento PT-01 · Pertiga de puesta a tierra · N. serie SN-PT-01 · calibrado hasta el 31/03/2027 · activo",
                GridKt._getFormattedRow(gridWithId("revisions-grid"), 0).get(5), "como quedo el instrumento en esa revision");
    }

    /**
     * Un instrumento se da de alta con su calibracion (que no caduca antes de hacerse), un codigo repetido
     * se dice con el dialogo abierto, renovar la calibracion manda lo cambiado y la version, y retirar pide
     * resources-delete y confirmacion.
     */
    @Test
    void anInstrumentIsCreatedRenewedAndRetiredAndARepeatedCodeIsSaid() {
        loginAs("recursos.flota", FLEET_OPERATOR);
        InstrumentDto pole = instrument(INSTRUMENT1, "PT-01", InstrumentKind.EARTHING_POLE, LocalDate.of(2027, 3, 31), false, true);
        stubInstruments(List.of(pole));
        when(instrumentClient.create(any())).thenThrow(maintenanceError(409, "INT-409", "Instrument PT-02 already exists")).thenReturn(pole);
        when(instrumentClient.update(any(), any())).thenReturn(pole);

        UI.getCurrent().navigate(ResourcesRoutes.INSTRUMENTS);
        click("instrument-create");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("instrument-code")), "pt-02");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("instrument-name")), "Pertiga 2");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("instrument-kind")), InstrumentKind.EARTHING_POLE);
        LocatorJ._setValue(datePicker("instrument-calibrated-on"), LocalDate.of(2026, 10, 1));
        LocatorJ._setValue(datePicker("instrument-calibration-expires-on"), LocalDate.of(2026, 9, 1));
        click(InstrumentEditorDialog.SAVE_ID);
        verify(instrumentClient, never()).create(any());
        assertEquals("La calibracion no caduca antes de hacerse", datePicker("instrument-calibration-expires-on").getErrorMessage());
        LocatorJ._setValue(datePicker("instrument-calibration-expires-on"), LocalDate.of(2027, 10, 1));
        click(InstrumentEditorDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText("Ya existe otro instrumento con ese codigo."));
        LocatorJ._get(InstrumentEditorDialog.class);
        click(InstrumentEditorDialog.SAVE_ID);
        verify(instrumentClient, times(2)).create(new InstrumentRequest("PT-02", "Pertiga 2", InstrumentKind.EARTHING_POLE, null,
                LocalDate.of(2026, 10, 1), LocalDate.of(2027, 10, 1), null));

        Grid<Object> grid = gridWithId("instruments-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, InstrumentsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("instrument-edit-" + INSTRUMENT1)));
        assertTrue(LocatorJ._get(TextField.class, spec -> spec.withId("instrument-code")).isReadOnly());
        LocatorJ._setValue(datePicker("instrument-calibration-expires-on"), LocalDate.of(2028, 3, 31));
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("instrument-serial")), "");
        click(InstrumentEditorDialog.SAVE_ID);
        verify(instrumentClient).update(INSTRUMENT1, new MergePatch<>(new InstrumentUpdateRequest(null, null, null, null, LocalDate.of(2028, 3, 31),
                null, null), Set.of("serialNumber"), 5L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, InstrumentsView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("instrument-retire-" + INSTRUMENT1)));
        verify(instrumentClient, never()).deactivate(any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(instrumentClient).deactivate(INSTRUMENT1);
    }

    /**
     * La pestana Documentos de un vehiculo pide sus datos al abrirse; el alta ofrece los tipos que aun no
     * tiene y exige la caducidad, un VDO-409 deja el dialogo abierto, renovar manda lo cambiado y quitar
     * pide confirmacion.
     */
    @Test
    void theVehicleDocumentsTabAddsRenewsAndRemovesOneOfEachKind() {
        loginAs("recursos.flota", FLEET_OPERATOR);
        stubConfigurationNames();
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true)));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.<VehicleLocationDto>of(), 0, 20));
        LocalDate later = LocalDate.now().plusYears(1);
        VehicleDocumentDto itv = document(DOC_ITV, VehicleDocumentKind.ROAD_INSPECTION, "ITV-2026-118", later, false);
        VehicleDocumentDto insurance = document(DOC_INSURANCE, VehicleDocumentKind.INSURANCE, "POL-77", LocalDate.now().minusDays(1), true);
        when(vehicleClient.documents(VEHICLE1)).thenReturn(List.of(itv, insurance));
        VehicleDocumentRequest rail = new VehicleDocumentRequest(VehicleDocumentKind.RAIL_INSPECTION, "RIV-9", null, LocalDate.of(2027, 6, 30), null);
        when(vehicleClient.addDocument(eq(VEHICLE1), any())).thenThrow(maintenanceError(409, "VDO-409", "Vehicle DR-01 already has RAIL_INSPECTION"))
                .thenReturn(document(UUID.randomUUID(), VehicleDocumentKind.RAIL_INSPECTION, "RIV-9", LocalDate.of(2027, 6, 30), false));
        when(vehicleClient.updateDocument(eq(VEHICLE1), eq(DOC_ITV), any())).thenReturn(itv);

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        verify(vehicleClient, never()).documents(any());
        selectTab(1);
        Grid<Object> grid = gridWithId("documents-grid");
        assertEquals(List.of("Inspeccion de carretera (ITV)", "ITV-2026-118", "28/02/2026", Formats.date(later), "Vigente"),
                GridKt._getFormattedRow(grid, 0).subList(0, 5));
        assertEquals("Caducado", GridKt._getFormattedRow(grid, 1).get(4));

        click("document-add");
        @SuppressWarnings("unchecked")
        Select<VehicleDocumentKind> kind = LocatorJ._get(Select.class, spec -> spec.withId("document-kind"));
        assertEquals(List.of(VehicleDocumentKind.RAIL_INSPECTION, VehicleDocumentKind.CRANE_CERTIFICATE, VehicleDocumentKind.PLATFORM_CERTIFICATE,
                VehicleDocumentKind.OTHER), kind.getListDataView().getItems().toList(), "los tipos que aun no tiene");
        LocatorJ._setValue(kind, VehicleDocumentKind.RAIL_INSPECTION);
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("document-reference")), "RIV-9");
        click(VehicleDocumentDialog.SAVE_ID);
        verify(vehicleClient, never()).addDocument(any(), any());
        assertTrue(datePicker("document-expires-on").isInvalid(), "la caducidad es obligatoria");
        LocatorJ._setValue(datePicker("document-expires-on"), LocalDate.of(2027, 6, 30));
        click(VehicleDocumentDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("El vehiculo ya tiene un documento de ese tipo: para renovarlo, cambia su caducidad."));
        LocatorJ._get(VehicleDocumentDialog.class);
        click(VehicleDocumentDialog.SAVE_ID);
        verify(vehicleClient, times(2)).addDocument(VEHICLE1, rail);
        assertTrue(LocatorJ._find(VehicleDocumentDialog.class).isEmpty());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("document-edit-" + DOC_ITV)));
        assertTrue(LocatorJ._get(Select.class, spec -> spec.withId("document-kind")).isReadOnly(), "el tipo no cambia");
        LocatorJ._setValue(datePicker("document-expires-on"), LocalDate.of(2028, 2, 29));
        click(VehicleDocumentDialog.SAVE_ID);
        verify(vehicleClient).updateDocument(VEHICLE1, DOC_ITV, new MergePatch<>(new VehicleDocumentUpdateRequest(null, null, LocalDate.of(2028, 2, 29),
                null), Set.of(), 1L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, "actions"), Button.class, spec -> spec.withId("document-remove-" + DOC_INSURANCE)));
        verify(vehicleClient, never()).removeDocument(any(), any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(vehicleClient).removeDocument(VEHICLE1, DOC_INSURANCE);
    }

    /** Quien solo lee ve los documentos sin controles, y uno de un tipo que no se conoce no abre nada. */
    @Test
    void aReaderOnlyReadsTheDocumentsOfAVehicleAndAnUnknownKindOpensNothing() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubConfigurationNames();
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true)));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.<VehicleLocationDto>of(), 0, 20));
        when(vehicleClient.documents(VEHICLE1)).thenReturn(List.of(document(DOC_ITV, VehicleDocumentKind.UNKNOWN, "TC-9", LocalDate.now().plusDays(5),
                false)));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        selectTab(1);
        Grid<Object> grid = gridWithId("documents-grid");
        assertEquals(List.of("Desconocido", "TC-9"), GridKt._getFormattedRow(grid, 0).subList(0, 2));
        assertEquals("Caduca en 5 dias", GridKt._getFormattedRow(grid, 0).get(4));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("document-add")).isEmpty());
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, "actions"), Button.class).isEmpty());

        loginAs("recursos.flota", FLEET_OPERATOR);
        UI.getCurrent().navigate(HomeView.class);
        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        selectTab(1);
        assertTrue(LocatorJ._find(GridKt._getCellComponent(gridWithId("documents-grid"), 0, "actions"), Button.class).isEmpty(),
                "un tipo desconocido no se modifica ni se quita");
        LocatorJ._get(Button.class, spec -> spec.withId("document-add"));
    }

    /**
     * El editor de modelos ofrece los tipos de cualificacion activos para el conductor (y el leido aunque
     * este retirado); vacia en el alta no viaja, y vaciarla al modificar viaja a null. La lista dice cual
     * exige cada modelo.
     */
    @Test
    void theModelEditorPicksTheQualificationOfItsDriverAndEmptyingItClearsIt() {
        loginAs("recursos.gestor", FLEET_MANAGER);
        QualificationTypeSummaryDto retired = new QualificationTypeSummaryDto(UUID.fromString("6a7b8c9d-0000-4000-8000-0000000000ff"), "OLD-RR",
                "Old road-rail operator", QualificationKind.VEHICLE_OPERATION);
        VehicleTypeDto existing = unimog(Set.of(), null, true, retired);
        stubVehicleTypes(List.of(existing));
        stubQualificationTypes(List.of(LICENCE_C, RR_OPERATOR));
        when(vehicleTypeClient.update(any(), any())).thenReturn(existing);
        when(vehicleTypeClient.create(any())).thenReturn(existing);

        UI.getCurrent().navigate(ResourcesRoutes.VEHICLE_TYPES);
        Grid<Object> grid = gridWithId("vehicle-types-grid");
        assertEquals("OLD-RR", GridKt._getFormattedRow(grid, 0).get(7));
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, VehicleTypesView.ACTIONS_COLUMN), Button.class,
                spec -> spec.withId("vehicle-type-edit-" + VEHICLE_TYPE1)));
        ComboBox<QualificationTypeSummaryDto> driver = comboWithId("vehicle-type-driver-qualification");
        assertEquals(retired, driver.getValue());
        assertEquals(List.of("OLD-RR · Old road-rail operator", "LIC-C · Driving licence C", "RR-OPERATOR · Road-rail machine operator"),
                ComboBoxKt.getSuggestions(driver), "los activos, y el leido aunque ya no lo este");
        assertEquals("Vacia: no exige ninguna", driver.getHelperText());
        LocatorJ._setValue(driver, null);
        click(VehicleTypeEditorDialog.SAVE_ID);
        verify(vehicleTypeClient).update(eq(VEHICLE_TYPE1), argThat(patch -> patch.cleared().equals(Set.of("requiredDriverQualificationTypeId"))
                && patch.body().keySet().equals(Set.of("requiredDriverQualificationTypeId", "version")) && patch.version() == 4L));

        click("vehicle-type-create");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-type-code")), "RR-2");
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-type-name")), "Bivial 2");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-type-mobility")), VehicleMobility.ROAD_RAIL);
        LocatorJ._setValue(comboWithId("vehicle-type-category"), VehicleCategory.ROAD_RAIL_TRUCK);
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("vehicle-type-rail-gear")), RailGearType.RAIL_WHEEL_DRIVE);
        ComboBox<QualificationTypeSummaryDto> newDriver = comboWithId("vehicle-type-driver-qualification");
        assertEquals("Vacia: la que corresponde a su movilidad y a su permiso", newDriver.getHelperText());
        LocatorJ._setValue(newDriver, RR_OPERATOR.summary());
        click(VehicleTypeEditorDialog.SAVE_ID);
        verify(vehicleTypeClient).create(argThat(request -> QTYPE_RR.equals(request.requiredDriverQualificationTypeId())
                && "RR-2".equals(request.code())));
    }

    /** Un turno con sus instrumentos de mto-resources; {@code instruments}, en el orden del servicio. */
    private static ShiftDto withInstruments(ShiftDto base, List<InstrumentSummaryDto> instruments) {
        return new ShiftDto(base.id(), base.code(), base.shiftDate(), base.team(), base.baseName(), base.vehicle(), base.possessionType(),
                base.plannedStart(), base.plannedEnd(), base.actualStart(), base.actualEnd(), base.voltageCutoffAt(), base.netWorkMinutes(),
                base.blockingDisconnectors(), base.earthingPoints(), base.parkingPlace(), base.executionPackageId(), base.trackIds(),
                base.startKp(), base.endKp(), base.personnel(), base.measurementEquipment(), base.status(), base.observations(), base.audit(),
                base.version(), base.teamLeader(), base.supervisor(), base.linemen(), base.vehicles(), instruments);
    }

    /**
     * Los instrumentos de un turno: la ficha y el parte los ensenan con su calibracion, el editor los elige
     * entre los activos de mto-resources como un conjunto, uno que aun no ha llegado a mantenimiento
     * (MSI-404) se dice con el dialogo abierto, y quitarlos todos viaja a null.
     */
    @Test
    void aShiftPicksItsInstrumentsAsASetAndTheDetailAndTheReportShowThem() {
        loginAs("mantenimiento.tecnico", SHIFT_CREW_WRITER);
        stubReferencesForMaintenance();
        stubPeople(List.of());
        stubVehicles(List.of());
        InstrumentDto pole = instrument(INSTRUMENT1, "PT-01", InstrumentKind.EARTHING_POLE, LocalDate.of(2027, 3, 31), false, true);
        InstrumentDto detector = instrument(INSTRUMENT2, "DT-01", InstrumentKind.VOLTAGE_DETECTOR, null, null, true);
        InstrumentDto gauge = instrument(INSTRUMENT3, "MG-01", InstrumentKind.MEASURING, LocalDate.of(2026, 12, 31), false, true);
        stubInstruments(List.of(pole, detector, gauge));
        ShiftDto shift = withInstruments(shiftWithCrew(ShiftStatus.PLANNED, null), List.of(pole.summary(), detector.summary()));
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(reportClient.shiftReport(SHIFT1)).thenReturn(new ShiftReportDto(shift, 0, 0, 0, 0, 0, List.of(), 0, List.of()));
        when(shiftClient.update(eq(SHIFT1), any())).thenThrow(maintenanceError(404, "MSI-404", "Maintenance instrument " + INSTRUMENT3 + " not found"))
                .thenReturn(shiftWithCrew(ShiftStatus.PLANNED, null));

        openShift(shift);
        assertEquals("Instrumentos: PT-01 · Instrumento PT-01 (calibrado hasta 31/03/2027), DT-01 · Instrumento DT-01 (sin calibracion)",
                textOf("shift-instruments-summary"));
        selectTab(2);
        assertEquals(List.of("PT-01", "Instrumento PT-01", "Pertiga de puesta a tierra", "31/03/2027"),
                GridKt._getFormattedRow(gridWithId("shift-report-instruments"), 0));
        assertEquals(2, GridKt._size(gridWithId("shift-report-instruments")));

        click("shift-edit");
        @SuppressWarnings("unchecked")
        MultiSelectComboBox<InstrumentSummaryDto> instruments = LocatorJ._get(MultiSelectComboBox.class, spec -> spec.withId("shift-instruments"));
        assertEquals(Set.of(pole.summary(), detector.summary()), instruments.getValue());
        @SuppressWarnings("unchecked")
        DataProvider<InstrumentSummaryDto, String> offered = (DataProvider<InstrumentSummaryDto, String>) instruments.getDataProvider();
        assertEquals(List.of("PT-01", "DT-01", "MG-01"), offered.fetch(new Query<>(0, 50, List.of(), null, "")).map(InstrumentSummaryDto::code).toList());
        verify(instrumentClient, atLeastOnce()).search(eq(InstrumentFilter.activeMatching("")), anyInt(), anyInt(), eq(List.of("code,asc")));
        assertEquals("MG-01 · Instrumento MG-01 (calibrado hasta 31/12/2026)", instruments.getItemLabelGenerator().apply(gauge.summary()));

        LocatorJ._setValue(instruments, Set.of(pole.summary(), gauge.summary()));
        click(ShiftEditorDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Ese instrumento aun no ha llegado a mantenimiento desde mto-resources: intentalo en un momento."));
        LocatorJ._get(ShiftEditorDialog.class);
        verify(shiftClient).update(eq(SHIFT1), argThat(patch -> Set.of(INSTRUMENT1, INSTRUMENT3).equals(patch.values().instrumentIds())
                && patch.cleared().isEmpty() && patch.version() == 4L));

        LocatorJ._setValue(instruments, Set.of());
        click(ShiftEditorDialog.SAVE_ID);
        verify(shiftClient).update(eq(SHIFT1), argThat(patch -> patch.cleared().equals(Set.of("instrumentIds"))
                && patch.values().instrumentIds() == null && patch.version() == 4L));
        assertTrue(LocatorJ._find(ShiftEditorDialog.class).isEmpty());
    }

    /** Sin resources-read los instrumentos de un turno se leen como estan, no se pide nada a mto-resources y no viajan. */
    @Test
    void withoutResourcesReadTheInstrumentsOfAShiftAreOnlyReadAndDoNotTravel() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        stubReferencesForMaintenance();
        InstrumentSummaryDto pole = new InstrumentSummaryDto(INSTRUMENT1, "PT-01", "Pertiga 1", InstrumentKind.EARTHING_POLE, LocalDate.of(2027, 3, 31));
        when(shiftClient.tasks(SHIFT1, null)).thenReturn(List.of());
        when(shiftClient.update(eq(SHIFT1), any())).thenReturn(shiftWithCrew(ShiftStatus.PLANNED, null));
        openShift(withInstruments(shiftWithCrew(ShiftStatus.PLANNED, null), List.of(pole)));

        click("shift-edit");
        assertTrue(LocatorJ._find(MultiSelectComboBox.class, spec -> spec.withId("shift-instruments")).isEmpty());
        assertTrue(LocatorJ._get(Div.class, spec -> spec.withId("shift-crew-readonly")).getText().endsWith(" · Instrumentos: PT-01 · Pertiga 1"),
                LocatorJ._get(Div.class, spec -> spec.withId("shift-crew-readonly")).getText());
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("shift-observations")), "Con lluvia");
        click(ShiftEditorDialog.SAVE_ID);
        verify(shiftClient).update(eq(SHIFT1), argThat(patch -> patch.cleared().isEmpty() && patch.values().instrumentIds() == null
                && "Con lluvia".equals(patch.values().observations())));
        verify(instrumentClient, never()).search(any(InstrumentFilter.class), anyInt(), anyInt(), anyList());
    }

    // --- Fase 5: ausencias, taller, GPS, equipo de las tareas y puestos y perfiles -----------------

    private static final UUID ABSENCE1 = UUID.fromString("7b8c9d0e-0000-4000-8000-0000000000a1");
    private static final UUID ABSENCE2 = UUID.fromString("7b8c9d0e-0000-4000-8000-0000000000a2");
    private static final UUID REPAIR1 = UUID.fromString("7b8c9d0e-0000-4000-8000-0000000000b1");
    private static final UUID REPAIR2 = UUID.fromString("7b8c9d0e-0000-4000-8000-0000000000b2");
    private static final UUID REPAIR3 = UUID.fromString("7b8c9d0e-0000-4000-8000-0000000000b3");
    private static final UUID REPAIR4 = UUID.fromString("7b8c9d0e-0000-4000-8000-0000000000b4");

    /** Los codigos de la fase 5: lo que bloquea se dice, el ambito no es «no tienes permiso» y Keycloak caido es transitorio. */
    @Test
    void phaseFiveErrorsSayWhatBlocksTheChange() {
        assertEquals("La persona ya esta ausente en esas fechas: cambia esa ausencia en vez de anadir otra. PER-000003 is already absent",
                UiErrors.message(maintenanceError(409, "ABS-001", "PER-000003 is already absent")));
        assertEquals("La reparacion no admite ese cambio en su estado. Repair REP-000004 is not OPEN",
                UiErrors.message(maintenanceError(409, "REP-001", "Repair REP-000004 is not OPEN")));
        assertEquals("Esa ausencia ya no existe.", UiErrors.message(maintenanceError(404, "ABS-404", "Absence not found")));
        assertEquals("Esa reparacion ya no existe.", UiErrors.message(maintenanceError(404, "REP-404", "Repair not found")));
        assertEquals("Queda fuera de tu ambito. Execution package 7 is outside your scope (3, 5)",
                UiErrors.message(maintenanceError(403, "SCP-403", "Execution package 7 is outside your scope (3, 5)")));
        assertEquals("No tienes permiso para esta operacion.", UiErrors.message(maintenanceError(403, null, "Forbidden")),
                "un 403 sin codigo sigue siendo el de siempre");
        assertEquals("Keycloak no responde ahora: el informe no se puede calcular. Intentalo mas tarde.",
                UiErrors.message(maintenanceError(503, "KCK-503", "Keycloak cannot be read now")));
    }

    private static PersonAbsenceDto absence(UUID id, AbsenceKind kind, LocalDate from, LocalDate to, String notes) {
        return new PersonAbsenceDto(id, PERSON1, kind, from, to, notes, 2L);
    }

    /**
     * La pestana Ausencias pide sus datos al abrirse: los dias cuentan los dos extremos, una clase nueva
     * se ve pero no abre nada, el alta comprueba lo evidente antes de llamar y un ABS-001 deja el dialogo
     * abierto; modificar manda lo cambiado y las notas vaciadas a null, y quitar pide confirmacion.
     */
    @Test
    void theAbsencesTabAddsPatchesAndRemovesAndAnOverlapKeepsTheDialogOpen() {
        loginAs("recursos.responsable", RESOURCES_ADMIN);
        PersonDto yossi = person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true);
        when(personClient.findById(PERSON1)).thenReturn(yossi);
        when(personClient.qualifications(PERSON1)).thenReturn(List.of());
        PersonAbsenceDto vacation = absence(ABSENCE1, AbsenceKind.VACATION, LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 6), "Verano");
        when(personClient.absences(PERSON1)).thenReturn(List.of(vacation,
                absence(ABSENCE2, AbsenceKind.UNKNOWN, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 1), null)));
        PersonAbsenceRequest training = new PersonAbsenceRequest(AbsenceKind.TRAINING, LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 18), null);
        when(personClient.addAbsence(eq(PERSON1), any())).thenThrow(maintenanceError(409, "ABS-001", "PER-000003 is already absent"))
                .thenReturn(absence(UUID.randomUUID(), AbsenceKind.TRAINING, LocalDate.of(2026, 11, 16), LocalDate.of(2026, 11, 18), null));
        when(personClient.updateAbsence(eq(PERSON1), eq(ABSENCE1), any())).thenReturn(vacation);

        UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(PERSON1));
        verify(personClient, never()).absences(any());
        selectTab(1);
        Grid<Object> grid = gridWithId("absences-grid");
        assertEquals(List.of("Vacaciones", "02/11/2026", "06/11/2026", "5", "Verano"), GridKt._getFormattedRow(grid, 0).subList(0, 5));
        assertEquals(List.of("Desconocido", "01/12/2026", "01/12/2026", "1"), GridKt._getFormattedRow(grid, 1).subList(0, 4));
        assertEquals("2 ausencias", spanText("absences-count"));
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 1, "actions"), Button.class).isEmpty(), "una clase desconocida no abre nada");

        click("absence-add");
        click(PersonAbsenceDialog.SAVE_ID);
        verify(personClient, never()).addAbsence(any(), any());
        assertTrue(LocatorJ._get(Select.class, spec -> spec.withId("absence-kind")).isInvalid(), "la clase es obligatoria");
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("absence-kind")), AbsenceKind.TRAINING);
        LocatorJ._setValue(datePicker("absence-starts-on"), LocalDate.of(2026, 11, 18));
        LocatorJ._setValue(datePicker("absence-ends-on"), LocalDate.of(2026, 11, 16));
        click(PersonAbsenceDialog.SAVE_ID);
        verify(personClient, never()).addAbsence(any(), any());
        assertEquals("El fin no puede ser anterior al inicio", datePicker("absence-ends-on").getErrorMessage());
        LocatorJ._setValue(datePicker("absence-starts-on"), LocalDate.of(2026, 11, 16));
        LocatorJ._setValue(datePicker("absence-ends-on"), LocalDate.of(2026, 11, 18));
        click(PersonAbsenceDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText(
                "La persona ya esta ausente en esas fechas: cambia esa ausencia en vez de anadir otra. PER-000003 is already absent"));
        LocatorJ._get(PersonAbsenceDialog.class);
        click(PersonAbsenceDialog.SAVE_ID);
        verify(personClient, times(2)).addAbsence(PERSON1, training);
        assertTrue(LocatorJ._find(PersonAbsenceDialog.class).isEmpty());
        verify(personClient, times(2)).absences(PERSON1);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("absence-edit-" + ABSENCE1)));
        click(PersonAbsenceDialog.SAVE_ID);
        verify(personClient, never()).updateAbsence(any(), any(), any());
        assertTrue(LocatorJ._find(PersonAbsenceDialog.class).isEmpty(), "sin cambios no se llama");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("absence-edit-" + ABSENCE1)));
        LocatorJ._setValue(datePicker("absence-ends-on"), LocalDate.of(2026, 11, 9));
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("absence-notes")), "");
        click(PersonAbsenceDialog.SAVE_ID);
        verify(personClient).updateAbsence(PERSON1, ABSENCE1, new MergePatch<>(new PersonAbsenceUpdateRequest(null, null, LocalDate.of(2026, 11, 9),
                null), Set.of("notes"), 2L));

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("absence-remove-" + ABSENCE1)));
        verify(personClient, never()).removeAbsence(any(), any());
        ConfirmDialogKt._fireConfirm(LocatorJ._get(ConfirmDialog.class));
        verify(personClient).removeAbsence(PERSON1, ABSENCE1);
    }

    /** Sin resources-write no se anade ni se modifica una ausencia, y sin resources-delete no se quita. */
    @Test
    void aReaderOnlyReadsTheAbsencesAndRemovingNeedsResourcesDelete() {
        loginAs("recursos.lector", RESOURCES_READER);
        when(personClient.findById(PERSON1)).thenReturn(person(PERSON1, "PER-000003", "Yossi", "Cohen", PersonCategory.TEAM_LEADER, true));
        when(personClient.qualifications(PERSON1)).thenReturn(List.of());
        when(personClient.absences(PERSON1)).thenReturn(List.of(absence(ABSENCE1, AbsenceKind.SICK_LEAVE, LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 3), null)));

        UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(PERSON1));
        selectTab(1);
        assertEquals(List.of("Baja", "02/11/2026", "03/11/2026", "2"), GridKt._getFormattedRow(gridWithId("absences-grid"), 0).subList(0, 4));
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("absence-add")).isEmpty());
        assertTrue(LocatorJ._find(GridKt._getCellComponent(gridWithId("absences-grid"), 0, "actions"), Button.class).isEmpty());

        loginAs("recursos.gestor", RESOURCES_MANAGER);
        UI.getCurrent().navigate(HomeView.class);
        UI.getCurrent().navigate(PersonDetailView.class, PersonDetailView.parametersOf(PERSON1));
        selectTab(1);
        Component actions = GridKt._getCellComponent(gridWithId("absences-grid"), 0, "actions");
        LocatorJ._get(actions, Button.class, spec -> spec.withId("absence-edit-" + ABSENCE1));
        assertTrue(LocatorJ._find(actions, Button.class, spec -> spec.withId("absence-remove-" + ABSENCE1)).isEmpty(), "quitar pide resources-delete");
    }

    private static VehicleRepairDto repair(UUID id, String code, RepairStatus status, boolean immobilises, String workshop, BigDecimal cost) {
        return new VehicleRepairDto(id, code, VEHICLE1, status == RepairStatus.UNKNOWN ? RepairKind.UNKNOWN : RepairKind.BREAKDOWN, status,
                immobilises, "Fuga en el circuito hidraulico", workshop, LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 5),
                status == RepairStatus.PLANNED ? null : Instant.parse("2026-11-03T06:00:00Z"),
                status == RepairStatus.CLOSED ? Instant.parse("2026-11-05T14:00:00Z") : null, 12000, new BigDecimal("3500.5"), cost, null, 1L);
    }

    private void stubWorkshop(List<VehicleRepairDto> repairs) {
        stubConfigurationNames();
        stubVehicles(List.of(vehicle(VEHICLE1, "DR-01", RAIL_MODEL, null, true)));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.<VehicleLocationDto>of(), 0, 20));
        when(vehicleClient.repairs(VEHICLE1)).thenReturn(repairs);
    }

    private static List<String> repairButtons(Grid<Object> grid, int row) {
        return LocatorJ._find(GridKt._getCellComponent(grid, row, "actions"), Button.class).stream()
                .map(button -> button.getId().orElse("").replaceFirst("-[0-9a-f-]{36}$", "")).toList();
    }

    /**
     * La pestana Taller pide sus datos al abrirse y ofrece en cada fila lo que su estado admite:
     * planificada, abrir, modificar y cancelar; abierta, cerrar, modificar y cancelar; cerrada o cancelada,
     * modificar; desconocida, nada. Abrir, cerrar y cancelar se confirman diciendo su efecto, y tras cada
     * una se relee la ficha del vehiculo; un REP-001 se dice sin pedir recargar.
     */
    @Test
    void theWorkshopTabOffersWhatEachStatusAdmitsAndRereadsTheVehicle() {
        loginAs("recursos.flota", FLEET_OPERATOR);
        VehicleRepairDto planned = repair(REPAIR1, "REP-000001", RepairStatus.PLANNED, true, "Taller de Lod", null);
        VehicleRepairDto open = repair(REPAIR2, "REP-000002", RepairStatus.OPEN, true, null, null);
        stubWorkshop(List.of(planned, open, repair(REPAIR3, "REP-000003", RepairStatus.CLOSED, false, null, new BigDecimal("980.25")),
                repair(REPAIR4, "REP-000004", RepairStatus.UNKNOWN, true, null, null)));
        when(vehicleClient.openRepair(VEHICLE1, REPAIR1)).thenReturn(repair(REPAIR1, "REP-000001", RepairStatus.OPEN, true, null, null));
        when(vehicleClient.closeRepair(VEHICLE1, REPAIR2)).thenThrow(maintenanceError(409, "REP-001", "Repair REP-000002 is not OPEN"));
        when(vehicleClient.cancelRepair(VEHICLE1, REPAIR1)).thenReturn(repair(REPAIR1, "REP-000001", RepairStatus.CANCELLED, true, null, null));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        verify(vehicleClient, never()).repairs(any());
        selectTab(2);
        Grid<Object> grid = gridWithId("repairs-grid");
        assertEquals(List.of("REP-000001", "Averia", "Planificada", "Si", "03/11/2026 – 05/11/2026", "", "", "Taller de Lod", ""),
                GridKt._getFormattedRow(grid, 0).subList(0, 9));
        assertEquals(List.of("REP-000003", "Averia", "Cerrada", "No"), GridKt._getFormattedRow(grid, 2).subList(0, 4));
        assertEquals("980.25", GridKt._getFormattedRow(grid, 2).get(8));
        assertEquals(List.of("Desconocido", "Desconocido"), GridKt._getFormattedRow(grid, 3).subList(1, 3));
        assertEquals("4 reparaciones", spanText("repairs-count"));
        assertEquals(List.of("repair-open", "repair-edit", "repair-cancel"), repairButtons(grid, 0));
        assertEquals(List.of("repair-close", "repair-edit", "repair-cancel"), repairButtons(grid, 1));
        assertEquals(List.of("repair-edit"), repairButtons(grid, 2));
        assertEquals(List.of(), repairButtons(grid, 3), "un estado desconocido no admite nada");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("repair-open-" + REPAIR1)));
        ConfirmDialog confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message", "").contains("pasa a En reparacion"), confirm.getElement().getProperty("message", ""));
        verify(vehicleClient, never()).openRepair(any(), any());
        ConfirmDialogKt._fireConfirm(confirm);
        verify(vehicleClient).openRepair(VEHICLE1, REPAIR1);
        verify(vehicleClient, times(2)).findById(VEHICLE1);
        verify(vehicleClient, times(2)).repairs(VEHICLE1);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, "actions"), Button.class, spec -> spec.withId("repair-close-" + REPAIR2)));
        confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message", "").contains("vuelve a Operativo"), confirm.getElement().getProperty("message", ""));
        ConfirmDialogKt._fireConfirm(confirm);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("La reparacion no admite ese cambio en su estado. Repair REP-000002 is not OPEN"));
        verify(vehicleClient, times(3)).findById(VEHICLE1);

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("repair-cancel-" + REPAIR1)));
        confirm = LocatorJ._get(ConfirmDialog.class);
        assertTrue(confirm.getElement().getProperty("message", "").contains("vuelve a entrar en los turnos de esas fechas"),
                confirm.getElement().getProperty("message", ""));
        ConfirmDialogKt._fireConfirm(confirm);
        verify(vehicleClient).cancelRepair(VEHICLE1, REPAIR1);
    }

    /**
     * El alta de una reparacion nace abierta ahora o planificada (con su inicio, obligatorio) y marcada como
     * que inmoviliza; lo evidente se comprueba antes de llamar (la clase, la descripcion, el fin previsto, el
     * coste con su columna) y un VEH-001 deja el dialogo abierto. Guardar relee la ficha del vehiculo.
     */
    @Test
    void aRepairIsCreatedPlannedOrOpenWithWhatIsCheckedBeforeCalling() {
        loginAs("recursos.flota", FLEET_OPERATOR);
        stubWorkshop(List.of());
        when(vehicleClient.addRepair(eq(VEHICLE1), any())).thenThrow(maintenanceError(409, "VEH-001", "DR-01 is retired"))
                .thenReturn(repair(REPAIR1, "REP-000001", RepairStatus.PLANNED, true, null, new BigDecimal("1200.5")));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        selectTab(2);
        assertEquals("0 reparaciones", spanText("repairs-count"));
        click("repair-add");
        @SuppressWarnings("unchecked")
        RadioButtonGroup<RepairStatus> status = LocatorJ._get(RadioButtonGroup.class, spec -> spec.withId("repair-status"));
        assertEquals(RepairStatus.OPEN, status.getValue(), "por defecto se abre ahora");
        assertEquals(List.of(RepairStatus.PLANNED, RepairStatus.OPEN), status.getListDataView().getItems().toList());
        assertTrue(LocatorJ._get(Checkbox.class, spec -> spec.withId("repair-immobilises")).getValue(), "por defecto inmoviliza");
        click(VehicleRepairDialog.SAVE_ID);
        verify(vehicleClient, never()).addRepair(any(), any());
        assertTrue(LocatorJ._get(Select.class, spec -> spec.withId("repair-kind")).isInvalid(), "la clase es obligatoria");
        assertTrue(LocatorJ._get(TextArea.class, spec -> spec.withId("repair-description")).isInvalid(), "la descripcion tambien");

        LocatorJ._setValue(status, RepairStatus.PLANNED);
        LocatorJ._setValue(LocatorJ._get(Select.class, spec -> spec.withId("repair-kind")), RepairKind.BREAKDOWN);
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("repair-description")), " Fuga en el circuito hidraulico ");
        click(VehicleRepairDialog.SAVE_ID);
        verify(vehicleClient, never()).addRepair(any(), any());
        assertEquals("Una planificada dice desde cuando", datePicker("repair-planned-from").getErrorMessage());

        LocatorJ._setValue(datePicker("repair-planned-from"), LocalDate.of(2026, 11, 5));
        LocatorJ._setValue(datePicker("repair-planned-to"), LocalDate.of(2026, 11, 3));
        click(VehicleRepairDialog.SAVE_ID);
        verify(vehicleClient, never()).addRepair(any(), any());
        assertEquals("El fin previsto no puede ser anterior al inicio", datePicker("repair-planned-to").getErrorMessage());

        LocatorJ._setValue(datePicker("repair-planned-to"), LocalDate.of(2026, 11, 7));
        BigDecimalField cost = LocatorJ._get(BigDecimalField.class, spec -> spec.withId("repair-cost"));
        LocatorJ._setValue(cost, new BigDecimal("-5"));
        click(VehicleRepairDialog.SAVE_ID);
        assertTrue(cost.isInvalid(), "el coste no es negativo");
        LocatorJ._setValue(cost, new BigDecimal("1200.555"));
        click(VehicleRepairDialog.SAVE_ID);
        assertTrue(cost.isInvalid(), "dos decimales como mucho");
        verify(vehicleClient, never()).addRepair(any(), any());

        LocatorJ._setValue(cost, new BigDecimal("1200.5"));
        click(VehicleRepairDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class, spec -> spec.withText("La flota no admite ese cambio. DR-01 is retired"));
        LocatorJ._get(VehicleRepairDialog.class);
        click(VehicleRepairDialog.SAVE_ID);
        verify(vehicleClient, times(2)).addRepair(VEHICLE1, new VehicleRepairRequest(RepairKind.BREAKDOWN, RepairStatus.PLANNED, true,
                "Fuga en el circuito hidraulico", null, LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 7), null, null, new BigDecimal("1200.5"), null));
        assertTrue(LocatorJ._find(VehicleRepairDialog.class).isEmpty());
        verify(vehicleClient, times(2)).findById(VEHICLE1);
        verify(vehicleClient, times(2)).repairs(VEHICLE1);
    }

    /**
     * Modificar una reparacion manda lo cambiado, lo vaciado y la version leida; el estado no se elige. Las
     * fechas previstas cambian planificada o abierta, y si inmoviliza, solo planificada.
     */
    @Test
    void aRepairIsModifiedWithWhatChangedAndItsDatesOnlyWhilePlannedOrOpen() {
        loginAs("recursos.flota", FLEET_OPERATOR);
        VehicleRepairDto planned = repair(REPAIR1, "REP-000001", RepairStatus.PLANNED, true, "Taller de Lod", null);
        stubWorkshop(List.of(planned, repair(REPAIR2, "REP-000002", RepairStatus.OPEN, true, null, null),
                repair(REPAIR3, "REP-000003", RepairStatus.CLOSED, false, null, new BigDecimal("980.25"))));
        when(vehicleClient.updateRepair(eq(VEHICLE1), any(), any())).thenReturn(planned);

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        selectTab(2);
        Grid<Object> grid = gridWithId("repairs-grid");
        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("repair-edit-" + REPAIR1)));
        assertTrue(LocatorJ._find(RadioButtonGroup.class, spec -> spec.withId("repair-status")).isEmpty(), "el estado tiene sus transiciones");
        assertFalse(LocatorJ._get(Checkbox.class, spec -> spec.withId("repair-immobilises")).isReadOnly());
        assertFalse(datePicker("repair-planned-from").isReadOnly());
        LocatorJ._setValue(LocatorJ._get(TextField.class, spec -> spec.withId("repair-workshop")), "");
        LocatorJ._setValue(LocatorJ._get(BigDecimalField.class, spec -> spec.withId("repair-cost")), new BigDecimal("980.25"));
        click(VehicleRepairDialog.SAVE_ID);
        verify(vehicleClient).updateRepair(VEHICLE1, REPAIR1, new MergePatch<>(new VehicleRepairUpdateRequest(null, null, null, null, null, null, null,
                null, new BigDecimal("980.25"), null), Set.of("workshop"), 1L));
        assertTrue(LocatorJ._find(VehicleRepairDialog.class).isEmpty());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, "actions"), Button.class, spec -> spec.withId("repair-edit-" + REPAIR2)));
        assertTrue(LocatorJ._get(Checkbox.class, spec -> spec.withId("repair-immobilises")).isReadOnly(), "si inmoviliza, solo planificada");
        assertFalse(datePicker("repair-planned-to").isReadOnly(), "abierta, sus fechas aun cambian");
        click(VehicleRepairDialog.SAVE_ID);
        assertTrue(LocatorJ._find(VehicleRepairDialog.class).isEmpty(), "sin cambios no se llama");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 2, "actions"), Button.class, spec -> spec.withId("repair-edit-" + REPAIR3)));
        assertTrue(datePicker("repair-planned-from").isReadOnly(), "cerrada, sus fechas ya no cambian");
        assertTrue(datePicker("repair-planned-to").isReadOnly());
        assertTrue(LocatorJ._get(Checkbox.class, spec -> spec.withId("repair-immobilises")).isReadOnly());
        LocatorJ._setValue(LocatorJ._get(TextArea.class, spec -> spec.withId("repair-notes")), "Factura 2026/118");
        click(VehicleRepairDialog.SAVE_ID);
        verify(vehicleClient).updateRepair(eq(VEHICLE1), eq(REPAIR3), argThat(patch -> "Factura 2026/118".equals(patch.values().notes())
                && patch.cleared().isEmpty() && patch.values().plannedFrom() == null && patch.version() == 1L));
        verify(vehicleClient, times(2)).updateRepair(any(), any(), any());
    }

    /** Quien solo lee ve el taller sin controles. */
    @Test
    void aReaderOnlyReadsTheWorkshopOfAVehicle() {
        loginAs("recursos.lector", RESOURCES_READER);
        stubWorkshop(List.of(repair(REPAIR1, "REP-000001", RepairStatus.PLANNED, true, null, null)));

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        selectTab(2);
        assertEquals("REP-000001", GridKt._getFormattedRow(gridWithId("repairs-grid"), 0).getFirst());
        assertTrue(LocatorJ._find(Button.class, spec -> spec.withId("repair-add")).isEmpty());
        assertEquals(List.of(), repairButtons(gridWithId("repairs-grid"), 0));
    }

    private static VehicleDto withGps(VehicleDto vehicle, VehicleLocationDto position, String device) {
        return new VehicleDto(vehicle.id(), vehicle.code(), vehicle.name(), vehicle.type(), vehicle.registrationPlate(), vehicle.railVehicleNumber(),
                vehicle.serialNumber(), vehicle.manufactureYear(), vehicle.owner(), vehicle.ownerCompany(), vehicle.operationalStatus(),
                vehicle.statusReason(), vehicle.available(), vehicle.odometerKm(), vehicle.engineHours(), vehicle.notes(), position, vehicle.active(),
                vehicle.version(), device);
    }

    /**
     * El dispositivo GPS de un vehiculo: la ficha lo dice («GPS: …») y una posicion que llego por el dice
     * su origen como «GPS»; el editor comprueba su forma antes de llamar y vaciarlo viaja a null.
     */
    @Test
    void theGpsDeviceIsCheckedBeforeCallingEmptiedToNullAndShownInTheDetail() {
        loginAs("recursos.flota", FLEET_OPERATOR);
        VehicleLocationDto byGps = new VehicleLocationDto(UUID.randomUUID(), VehicleLocationKind.ON_TRACK, null, 4L, 12L, new BigDecimal("12.300"),
                null, null, null, null, null, null, null, VehicleLocationSource.TELEMATICS, null, Instant.parse("2026-10-16T03:40:00Z"),
                Instant.parse("2026-10-16T03:40:02Z"), "service-account-mto-telematics");
        VehicleDto gps = withGps(vehicle(VEHICLE1, "UN-01", ROAD_RAIL_MODEL, null, true), byGps, "GPS-118");
        stubConfigurationNames();
        stubVehicles(List.of(gps));
        when(vehicleClient.locations(eq(VEHICLE1), any(), any(), anyInt(), anyInt())).thenReturn(page(List.of(byGps), 0, 20));
        when(vehicleClient.update(eq(VEHICLE1), any())).thenReturn(gps);

        UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(VEHICLE1));
        assertTrue(textOf("vehicle-summary").contains("GPS: GPS-118"), textOf("vehicle-summary"));
        assertTrue(textOf("vehicle-position").contains("GPS · "), textOf("vehicle-position"));
        assertEquals("GPS", GridKt._getFormattedRow(gridWithId("locations-grid"), 0).stream().filter("GPS"::equals).findFirst().orElse(""),
                "el origen de una posicion del GPS");

        click("vehicle-edit");
        TextField device = LocatorJ._get(TextField.class, spec -> spec.withId("vehicle-telematics-device"));
        assertEquals("GPS-118", device.getValue());
        assertEquals(64, device.getMaxLength());
        LocatorJ._setValue(device, "GPS 118/b");
        click(VehicleEditorDialog.SAVE_ID);
        verify(vehicleClient, never()).update(any(), any());
        assertEquals("Solo letras sin tildes, cifras, '.', ':', '-' y '_'", device.getErrorMessage());
        LocatorJ._setValue(device, "");
        click(VehicleEditorDialog.SAVE_ID);
        verify(vehicleClient).update(eq(VEHICLE1), argThat(patch -> patch.cleared().equals(Set.of("telematicsDeviceId"))
                && patch.values().telematicsDeviceId() == null && patch.version() == 6L));
    }

    private static TaskTypeDto taskType(String code, Set<String> equipment, long version) {
        return new TaskTypeDto(UUID.randomUUID(), code, "Revision del hilo de contacto", FunctionalGroup.OVERHEAD_CONDUCTORS, new BigDecimal("12.5"),
                TaskUnit.SPAN, null, true, false, true, 4, equipment, version);
    }

    /**
     * El equipo que exige un tipo de tarea se ve en su columna (uno desconocido, tal cual) y solo lo cambia
     * quien tiene maintenance-supervise y maintenance-write: el conjunto como merge-patch con la version, lo
     * desconocido conservado, quitarlos todos a null y un CON-001 con el dialogo abierto.
     */
    @Test
    void theEquipmentOfATaskTypeIsChangedOnlyWithSuperviseAndWriteAndKeepsTheUnknown() {
        loginAs("mantenimiento.tecnico", MAINTENANCE_TECHNICIAN);
        TaskTypeDto platform = taskType("RG-04", Set.of("INSULATED_PLATFORM", "SAFETY_HARNESS"), 3L);
        TaskTypeDto earthing = taskType("RG-05", Set.of("EARTHING"), 1L);
        when(maintenanceCatalogClient.taskTypes(any(), any(), any())).thenReturn(List.of(platform, earthing, taskType("RG-06", Set.of(), 0L)));
        when(maintenanceCatalogClient.updateTaskType(eq("RG-04"), any())).thenThrow(maintenanceError(409, "CON-001", "Stale version of RG-04"))
                .thenReturn(platform);
        when(maintenanceCatalogClient.updateTaskType(eq("RG-05"), any())).thenReturn(earthing);

        UI.getCurrent().navigate(MaintenanceRoutes.TASK_TYPES);
        Grid<Object> grid = gridWithId("task-types-grid");
        assertEquals("Plataforma aislada, SAFETY_HARNESS", GridKt._getFormattedRow(grid, 0).get(8));
        assertEquals("Puesta a tierra", GridKt._getFormattedRow(grid, 1).get(8));
        assertEquals("—", GridKt._getFormattedRow(grid, 2).get(8));
        assertTrue(LocatorJ._find(GridKt._getCellComponent(grid, 0, "actions"), Button.class).isEmpty(), "sin maintenance-supervise no se cambia");

        loginAs("mantenimiento.responsable", MAINTENANCE_MANAGER);
        UI.getCurrent().navigate(HomeView.class);
        UI.getCurrent().navigate(MaintenanceRoutes.TASK_TYPES);
        grid = gridWithId("task-types-grid");
        Button equipment = LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("task-type-equipment-RG-04"));
        assertEquals("Equipo de RG-04", equipment.getTooltip().getText());
        LocatorJ._click(equipment);
        Checkbox insulated = LocatorJ._get(Checkbox.class, spec -> spec.withId("task-equipment-INSULATED_PLATFORM"));
        Checkbox earth = LocatorJ._get(Checkbox.class, spec -> spec.withId("task-equipment-EARTHING"));
        assertTrue(insulated.getValue());
        assertFalse(earth.getValue());
        assertEquals("Exige tambien, y se conserva: SAFETY_HARNESS", LocatorJ._get(Paragraph.class, spec -> spec.withId("task-equipment-unknown")).getText());
        click(TaskTypeEquipmentDialog.SAVE_ID);
        verify(maintenanceCatalogClient, never()).updateTaskType(any(), any());
        assertTrue(LocatorJ._find(TaskTypeEquipmentDialog.class).isEmpty(), "sin cambios no se llama");

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 0, "actions"), Button.class, spec -> spec.withId("task-type-equipment-RG-04")));
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("task-equipment-INSULATED_PLATFORM")), false);
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("task-equipment-EARTHING")), true);
        click(TaskTypeEquipmentDialog.SAVE_ID);
        LocatorJ._get(NotificationsKt.getNotifications().getLast(), Span.class,
                spec -> spec.withText("Conflicto con otro cambio: recarga y vuelve a intentarlo."));
        LocatorJ._get(TaskTypeEquipmentDialog.class);
        click(TaskTypeEquipmentDialog.SAVE_ID);
        verify(maintenanceCatalogClient, times(2)).updateTaskType("RG-04", new MergePatch<>(new TaskTypeUpdateRequest(
                Set.of("EARTHING", "SAFETY_HARNESS")), Set.of(), 3L));
        assertTrue(LocatorJ._find(TaskTypeEquipmentDialog.class).isEmpty());

        LocatorJ._click(LocatorJ._get(GridKt._getCellComponent(grid, 1, "actions"), Button.class, spec -> spec.withId("task-type-equipment-RG-05")));
        LocatorJ._setValue(LocatorJ._get(Checkbox.class, spec -> spec.withId("task-equipment-EARTHING")), false);
        click(TaskTypeEquipmentDialog.SAVE_ID);
        verify(maintenanceCatalogClient).updateTaskType(eq("RG-05"), argThat(patch -> patch.values().requiredEquipment() == null
                && patch.cleared().equals(Set.of("requiredEquipment")) && patch.version() == 1L));
    }

    private static PositionDiscrepancyReportDto discrepancies() {
        PositionDiscrepancyDto yossi = new PositionDiscrepancyDto(PERSON1, "PER-000003", "Yossi Cohen", "y.cohen", PersonCategory.TEAM_LEADER, true,
                "mto-position-team-leader", List.of("mto-position-worker"));
        PositionDiscrepancyDto david = new PositionDiscrepancyDto(PERSON2, "PER-000004", "David Levi", "d.levi", PersonCategory.WORKER, true,
                "mto-position-worker", List.of());
        PositionDiscrepancyDto external = new PositionDiscrepancyDto(null, null, null, "ext.contractor", null, null, null,
                List.of("mto-position-supervisor"));
        return new PositionDiscrepancyReportDto(Instant.parse("2026-10-19T08:00:00Z"), 42, 37, List.of(yossi), List.of(), List.of(david),
                List.of(external));
    }

    /**
     * Puestos y perfiles: las cifras del informe y sus cuatro secciones, cada persona como enlace a su ficha
     * (la que no tiene ficha, no), una seccion vacia dice «Nada», y «Actualizar» lo vuelve a pedir.
     */
    @Test
    void thePositionDiscrepanciesShowTheirFourSectionsAndLinkToThePerson() {
        loginAs("recursos.lector", RESOURCES_READER);
        when(resourcesReportClient.positionDiscrepancies()).thenReturn(discrepancies());

        UI.getCurrent().navigate(ResourcesRoutes.DISCREPANCIES);
        assertEquals("Calculado el " + Formats.dateTime(Instant.parse("2026-10-19T08:00:00Z")) + " · 42 personas revisadas · "
                + "37 usuarios con perfil de puesto", textOf("discrepancies-figures"));
        LocatorJ._get(H4.class, spec -> spec.withText("Con otro perfil (1)"));
        LocatorJ._get(H4.class, spec -> spec.withText("Con varios perfiles (0)"));
        LocatorJ._get(H4.class, spec -> spec.withText("Sin perfil de puesto (1)"));
        LocatorJ._get(H4.class, spec -> spec.withText("Con perfil y sin ficha (1)"));
        assertEquals(List.of("y.cohen", "Jefe de equipo", "mto-position-team-leader", "mto-position-worker"),
                GridKt._getFormattedRow(gridWithId("discrepancies-wrong-profile-grid"), 0).subList(1, 5));
        assertEquals("Nada", spanText("discrepancies-several-profiles-empty"));
        assertEquals(List.of("d.levi", "Operario", "mto-position-worker", ""),
                GridKt._getFormattedRow(gridWithId("discrepancies-without-profile-grid"), 0).subList(1, 5));
        assertEquals(List.of("ext.contractor", "", "", "mto-position-supervisor"),
                GridKt._getFormattedRow(gridWithId("discrepancies-without-record-grid"), 0).subList(1, 5));
        RouterLink yossi = (RouterLink) GridKt._getCellComponent(gridWithId("discrepancies-wrong-profile-grid"), 0, "person");
        assertEquals("discrepancies-wrong-profile-person-" + PERSON1, yossi.getId().orElseThrow());
        assertEquals("PER-000003 · Yossi Cohen", yossi.getText());
        assertEquals(ResourcesRoutes.PEOPLE + "/" + PERSON1, yossi.getHref());
        assertTrue(LocatorJ._find(GridKt._getCellComponent(gridWithId("discrepancies-without-record-grid"), 0, "person"), RouterLink.class).isEmpty(),
                "sin ficha no hay nada que abrir");

        click("discrepancies-refresh");
        verify(resourcesReportClient, times(2)).positionDiscrepancies();
    }

    /** Con Keycloak caido el informe dice por que en su sitio, con «Reintentar» y sin notificacion. */
    @Test
    void keycloakDownIsSaidInPlaceWithARetryAndNoNotification() {
        loginAs("recursos.lector", RESOURCES_READER);
        when(resourcesReportClient.positionDiscrepancies()).thenThrow(maintenanceError(503, "KCK-503", "Keycloak cannot be read now"))
                .thenReturn(discrepancies());

        UI.getCurrent().navigate(ResourcesRoutes.DISCREPANCIES);
        assertEquals("Keycloak no responde ahora: el informe no se puede calcular. Intentalo mas tarde.", textOf("discrepancies-unavailable"));
        assertTrue(NotificationsKt.getNotifications().isEmpty(), NotificationsKt.getNotifications().toString());
        assertTrue(LocatorJ._find(Grid.class, spec -> spec.withId("discrepancies-wrong-profile-grid")).isEmpty());

        click("discrepancies-retry");
        assertTrue(LocatorJ._find(Div.class, spec -> spec.withId("discrepancies-unavailable")).isEmpty());
        LocatorJ._get(Grid.class, spec -> spec.withId("discrepancies-wrong-profile-grid"));
        verify(resourcesReportClient, times(2)).positionDiscrepancies();
    }

    /** Puestos y perfiles esta en el grupo Recursos con resources-read. */
    @Test
    void thePositionDiscrepanciesAreInTheResourcesMenu() {
        loginAs("recursos.lector", RESOURCES_READER);
        UI.getCurrent().navigate(HomeView.class);
        assertTrue(menuLabels().containsAll(List.of("Recursos", "Puestos y perfiles")), menuLabels().toString());
    }

    /** Sin resources-read no esta en el menu, y un rol de realm que se llama como el permiso no lo abre. */
    @Test
    void withoutResourcesReadThePositionDiscrepanciesDoNotOpen() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        UI.getCurrent().navigate(HomeView.class);
        assertFalse(menuLabels().contains("Puestos y perfiles"), menuLabels().toString());

        loginAs("recursos.impostor", "ROLE_REALM_RESOURCES_READ");
        assertThrows(Throwable.class, () -> UI.getCurrent().navigate(ResourcesRoutes.DISCREPANCIES));
        verify(resourcesReportClient, never()).positionDiscrepancies();
    }
}

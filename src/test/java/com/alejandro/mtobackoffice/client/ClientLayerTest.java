package com.alejandro.mtobackoffice.client;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.JobsClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobFamily;
import com.alejandro.mtobackoffice.client.dto.jobs.JobStatus;
import com.alejandro.mtobackoffice.client.dto.jobs.JobType;
import com.alejandro.mtobackoffice.client.dto.jobs.UploadedFile;
import com.alejandro.mtobackoffice.client.dto.master.BusinessEntityDto;
import com.alejandro.mtobackoffice.client.dto.master.LovRef;
import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import com.alejandro.mtobackoffice.client.dto.master.StationDto;
import com.alejandro.mtobackoffice.client.dto.master.TrackDto;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.alejandro.mtobackoffice.client.error.ApiErrorDecoder;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.error.ServiceUnavailableApiException;
import com.alejandro.mtobackoffice.client.error.SessionExpiredApiException;
import com.alejandro.mtobackoffice.client.error.TooManyRequestsApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
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
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.maintenance.AssetClient;
import com.alejandro.mtobackoffice.client.maintenance.MaintenanceCatalogClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static <T> T asUser(Supplier<T> call) {
        return CurrentPrincipal.callAs(PRINCIPAL, call);
    }

    @Test
    void lovListIsReadThroughTheGatewayPrefixAndUnknownFieldsAreIgnored() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profile-statuses"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"id":1,"code":"DRAFT","description":"Borrador","type":"ProfileStatus","enabled":true,
                          "createDate":"2026-08-01T10:15:30","versionDate":"2026-08-02T11:00:00","versionUser":"config.responsable",
                          "versionNumber":0,"unknownTomorrow":{"x":1}}]
                        """, MediaType.APPLICATION_JSON));

        List<LovDto> statuses = asUser(() -> lovClient.findAll(LovResource.PROFILE_STATUSES));

        assertEquals(1, statuses.size());
        assertEquals(new LovDto(1L, "DRAFT", "Borrador", true, 0, LocalDateTime.parse("2026-08-02T11:00:00"), "config.responsable"),
                statuses.getFirst());
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
                            "disconnector":{"id":40,"name":"SEC-40","onLoad":true,"function":"FEED","station":"ATOCHA"},
                            "fieldOfTomorrow":{"deep":[1]}},
                           {"id":8,"code":"P-008","kp":"70.000","orderInTrack":2,"sectionings":[],"cantilevers":[]}],
                         "sectionInsulators":[
                           {"id":50,"name":"AIS-50","kp":"15.000","installationType":"TRACK_CONNECTION","enabled":true,
                            "station":"ATOCHA","track":"VIA 1","connectedTrack":"VIA 2",
                            "switches":[{"id":60,"code":"W31","kp":"15.500","turnoutDenominator":9,"track":"VIA 1"}]}]}
                        """, MediaType.APPLICATION_JSON));

        TrackSchematicDto schematic = asUser(() -> trackClient.schematic(3L));

        assertEquals("VIA 1", schematic.trackName());
        assertEquals("EP4", schematic.executionPackageName());
        assertEquals(List.of("ATOCHA", "CHAMARTIN"), schematic.stations());
        assertEquals(2, schematic.profiles().size());
        TrackSchematicDto.ProfileNode first = schematic.profiles().getFirst();
        assertEquals("P-007", first.code());
        assertEquals("12.345", first.kp());
        assertEquals("-2.500", first.railPoleDistance());
        assertEquals(List.of("S1"), first.sectionings());
        assertEquals("PT1", first.cantilevers().getFirst().type());
        assertEquals(1200L, first.cantilevers().getFirst().steadyArmLength());
        assertEquals("SEC-40", first.disconnector().name());
        assertEquals("ATOCHA", first.disconnector().station());
        TrackSchematicDto.ProfileNode second = schematic.profiles().get(1);
        assertNull(second.disconnector(), "lo que el servicio no manda es null");
        assertTrue(second.cantilevers().isEmpty());
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
        assertEquals(JobFamily.PROFILE_JOBS, job.family());
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

    /** README_API.md §4: la coleccion de hijos que se manda es el estado final; el 1:1 se manda entero para mantenerlo. */
    @Test
    void aProfileTravelsWithItsTypedCantileversTheirSteadyArmAndItsDisconnector() {
        server.expect(requestTo(GATEWAY + "/api/configuration/profiles/7"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"id":7,"profileId":"P-007","kp":"12.345","trackId":3,"versionNumber":2,
                         "disconnector":{"id":5,"name":"SEC-1","onLoad":true,"stationId":12,"profileId":7,
                                         "profileCode":"P-007","profileKp":"12.345","disconnectorFunction":{"id":9,"code":"Disc"}},
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
                .andRespond(withSuccess("{\"id\":7,\"versionNumber\":3}", MediaType.APPLICATION_JSON));

        ProfileDto profile = asUser(() -> profileClient.findById(7L));
        assertEquals("SEC-1", profile.getDisconnector().getName());
        assertEquals("P-007 (kp 12.345)", profile.getDisconnector().profileLabel());
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
     * Los filtros viajan como el servicio los lee: el enumerado por su nombre, la fecha en ISO (sin
     * {@code @DateTimeFormat} saldria con el formato corto de la maquina), el texto en blanco no
     * viaja y el orden se repite. Un valor que esta version no conoce se lee como {@code UNKNOWN} y
     * no rompe la pagina.
     */
    @Test
    void ordersArePagedWithTheirFiltersAndAnUnknownValueIsReadAsUnknown() {
        server.expect(requestTo(MAINTENANCE + "/orders?status=IN_PROGRESS&type=PREVENTIVE&assetType=TRACK_SECTION&trackId=12"
                        + "&plannedFrom=2026-09-01&plannedTo=2026-09-30&teamId=" + TEAM_ID + "&code=MO-0000&page=1&size=50"
                        + "&sort=plannedDate%2Casc&sort=code%2Cdesc"))
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
        server.expect(requestTo(MAINTENANCE + "/orders?page=0&size=50&sort=nope%2Casc")).andExpect(method(HttpMethod.GET))
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
            + "\"preventiveIntervalDays\":180,\"lastPreventiveCompletedAt\":null,\"nextPreventiveDueAt\":\"2026-10-01T00:00:00Z\",\"audit\":null}";

    /**
     * Activos: la busqueda con sus filtros (el instante en ISO), el alta de un tramo sin lo vacio,
     * la modificacion parcial con solo lo que cambio, la reactivacion, la baja con un DELETE sin
     * cuerpo y las ordenes de un activo. Un tipo que esta version no conoce se lee como UNKNOWN.
     */
    @Test
    void assetsAreSearchedCreatedPartiallyUpdatedAndDisabled() {
        server.expect(requestTo(MAINTENANCE + "/assets?type=SECTION_INSULATOR&trackId=12&enabled=true&name=AS&preventiveDueBefore=2026-10-01T22%3A00%3A00Z"
                        + "&page=0&size=50&sort=trackId%2Casc&sort=startKp%2Casc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(stockPage(INSULATOR_JSON + "," + INSULATOR_JSON.replace("SECTION_INSULATOR", "CANTILEVER")
                        .replace(ASSET_ID, "2b3c4d5e-0000-4000-8000-00000000000b"), 0, 50, 2), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.code").value("TS-0002"))
                .andExpect(jsonPath("$.trackId").value(12))
                .andExpect(jsonPath("$.startKp").value(13.45))
                .andExpect(jsonPath("$.trackKind").value("DIVERTED"))
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.stationId").doesNotExist())
                .andRespond(withSuccess(INSULATOR_JSON.replace("SI-0007", "TS-0002"), MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID)).andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.preventiveIntervalDays").value(90))
                .andExpect(jsonPath("$.description").value(""))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.enabled").doesNotExist())
                .andExpect(jsonPath("$.trackId").doesNotExist())
                .andRespond(withSuccess(INSULATOR_JSON, MediaType.APPLICATION_JSON));
        server.expect(requestTo(MAINTENANCE + "/assets/" + ASSET_ID)).andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"enabled\":true}", org.springframework.test.json.JsonCompareMode.STRICT))
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
        asUser(() -> assetClient.update(UUID.fromString(ASSET_ID),
                new AssetUpdateRequest(null, "", null, 90, null, null, null, null, null, null)));
        asUser(() -> assetClient.update(UUID.fromString(ASSET_ID), AssetUpdateRequest.enabled(true)));
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
        server.expect(requestTo(MAINTENANCE + "/teams")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[" + team + "]", MediaType.APPLICATION_JSON));
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
        assertEquals("EQ-02", created.code());
        assertEquals(TaskUnit.SPAN, types.getFirst().unit());
        assertTrue(types.getFirst().requiresFullPossession());
        assertEquals(new BigDecimal("12.50"), types.getFirst().standardMinutesPerUnit());
        assertEquals(CatenaryAssetType.PROFILE, templates.getFirst().assetType());
        assertEquals(new BigDecimal("5300"), templates.getFirst().items().getFirst().minValue());
        server.verify();
    }
}

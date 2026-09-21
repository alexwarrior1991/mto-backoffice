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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.alejandro.mtobackoffice.client.dto.master.CantileverDto;
import java.util.ArrayList;
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
        assertEquals(new LovDto(1L, "DRAFT", "Borrador", true, LocalDateTime.parse("2026-08-02T11:00:00"), "config.responsable"),
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
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {"id":42,"code":"PT9","description":"Poste tipo 9","enabled":true,"versionNumber":0}
                                """));

        LovDto created = asUser(() -> lovClient.create("pole-types", LovDto.forCreate("PT9", "Poste tipo 9", true)));

        assertEquals(42L, created.id());
        assertTrue(created.isEnabled());
    }

    @Test
    void updatePutsToTheIdPathAndDeleteIsA204() {
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/42"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.enabled").value(false))
                .andRespond(withSuccess("""
                        {"id":42,"code":"PT9","description":"Poste tipo 9 (baja)","enabled":false}
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/42"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        LovDto existing = new LovDto(42L, "PT9", "Poste tipo 9", true, null, null);
        LovDto updated = asUser(() -> lovClient.update("pole-types", 42L, existing.withValues("PT9", "Poste tipo 9 (baja)", false)));
        assertFalse(updated.isEnabled());

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
                        .body("[{\"id\":1,\"code\":\"PT1\",\"enabled\":true},{\"id\":2,\"code\":\"PT2\",\"enabled\":true}]"));
        server.expect(requestTo(GATEWAY + "/api/configuration/pole-types/bulk"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].enabled").value(false))
                .andRespond(withSuccess("[{\"id\":1,\"code\":\"PT1\",\"enabled\":false}]", MediaType.APPLICATION_JSON));

        List<LovDto> created = asUser(() -> lovClient.bulkCreate("pole-types",
                List.of(LovDto.forCreate("PT1", "Uno", true), LovDto.forCreate("PT2", "Dos", true))));
        assertEquals(2, created.size());

        List<LovDto> updated = asUser(() -> lovClient.bulkUpdate("pole-types", List.of(created.getFirst().withEnabled(false))));
        assertFalse(updated.getFirst().isEnabled());
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
}

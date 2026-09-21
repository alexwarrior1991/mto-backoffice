package com.alejandro.mtobackoffice.client;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.configuration.ProfileClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
}

package com.alejandro.mtobackoffice.client;

import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.matchesRegex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
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
                          "createDate":"2026-08-01T10:15:30","versionNumber":0,"unknownTomorrow":{"x":1}}]
                        """, MediaType.APPLICATION_JSON));

        List<LovDto> statuses = asUser(() -> lovClient.findAll(LovResource.PROFILE_STATUSES));

        assertEquals(1, statuses.size());
        assertEquals(new LovDto(1L, "DRAFT", "Borrador", "ProfileStatus", true), statuses.getFirst());
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
}

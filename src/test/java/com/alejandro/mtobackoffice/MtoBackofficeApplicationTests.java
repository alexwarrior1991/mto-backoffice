package com.alejandro.mtobackoffice;

import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.MovementClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.ReservationClient;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.KeycloakProperties;
import com.alejandro.mtobackoffice.ui.maintenance.MaintenanceClients;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ClassUtils;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * El contexto completo arranca sin Keycloak ni gateway escuchando, y la cadena de filtros manda a
 * quien no ha entrado al login de Keycloak sin haber descubierto nada por HTTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MtoBackofficeApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoadsWithoutKeycloakListening() {
        assertNotNull(context.getBean(LovClient.class));
        assertNotNull(context.getBean(UsersClient.class));
        for (Class<?> stockClient : List.of(MaterialClient.class, WarehouseClient.class, SupplierClient.class, ProjectClient.class,
                AssemblyClient.class, MovementClient.class, ReservationClient.class)) {
            assertNotNull(context.getBean(stockClient), stockClient.getSimpleName());
        }
        assertNotNull(context.getBean(OrderClient.class));
        assertNotNull(context.getBean(MaintenanceClients.class));
        assertEquals(List.of("mto-configuration-api", "mto-users-api", "mto-stock-api", "mto-maintenance-api"),
                context.getBean(KeycloakProperties.class).rolesClientIds(), "los cuatro clientes cuyos roles son permisos");
        ClientRegistration keycloak = context.getBean(ClientRegistrationRepository.class).findByRegistrationId("keycloak");
        assertNotNull(keycloak);
        assertEquals("http://localhost:8082/realms/mto/protocol/openid-connect/token", keycloak.getProviderDetails().getTokenUri());
    }

    /** Lo que hace que el token se pueda pedir por nombre de principal desde cualquier hilo. */
    @Test
    void theAuthorizedClientIsStoredByPrincipalAndRefreshedWithoutARequest() {
        assertInstanceOf(AuthenticatedPrincipalOAuth2AuthorizedClientRepository.class,
                context.getBean(OAuth2AuthorizedClientRepository.class));
        assertInstanceOf(AuthorizedClientServiceOAuth2AuthorizedClientManager.class,
                context.getBean(OAuth2AuthorizedClientManager.class));
    }

    @Test
    void anonymousRequestsAreSentToTheKeycloakLogin() throws Exception {
        mockMvc.perform(get("/catalogos/pole-types"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/keycloak"));
    }

    @Test
    void theLoginRouteRedirectsToTheRealmWithTheClientAndTheCallback() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/keycloak"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", startsWith("http://localhost:8082/realms/mto/protocol/openid-connect/auth?")))
                .andExpect(header().string("Location", containsString("client_id=mto-backoffice")))
                .andExpect(header().string("Location", containsString("redirect_uri=http://localhost/login/oauth2/code/keycloak")))
                .andExpect(header().string("Location", containsString("scope=openid%20profile%20email")));
    }

    @Test
    void healthProbeIsOpenWithoutAToken() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    /** Solo componentes Apache 2.0: ningun artefacto comercial de Vaadin en el classpath. */
    @Test
    void noCommercialVaadinArtifactIsOnTheClasspath() {
        for (String commercial : List.of(
                "com.vaadin.flow.component.charts.Chart",
                "com.vaadin.flow.component.gridpro.GridPro",
                "com.vaadin.flow.component.crud.Crud",
                "com.vaadin.flow.component.map.Map",
                "com.vaadin.flow.component.dashboard.Dashboard",
                "com.vaadin.flow.component.spreadsheet.Spreadsheet",
                "com.vaadin.testbench.TestBench",
                "com.vaadin.testbench.unit.UIUnitTest")) {
            assertFalse(ClassUtils.isPresent(commercial, null), commercial + " esta en el classpath");
        }
    }
}

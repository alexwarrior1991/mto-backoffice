package com.alejandro.mtobackoffice.ui;

import com.alejandro.mtobackoffice.client.configuration.LovClient;
import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;
import com.alejandro.mtobackoffice.client.error.ApiProblem;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.BackofficeUser;
import com.alejandro.mtobackoffice.configuration.security.JwtClaimNames;
import com.alejandro.mtobackoffice.ui.views.HomeView;
import com.alejandro.mtobackoffice.ui.views.ProfileStatusesView;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributesting.v10.LocatorJ;
import com.github.mvysny.kaributesting.v10.MockAccessDeniedException;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.NotificationsKt;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringSecurity;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Autowired
    private ApplicationContext context;

    @MockitoBean
    private LovClient lovClient;

    @BeforeEach
    void setUp() {
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

    @Test
    void profileStatusesViewShowsTheRowsFromTheClient() {
        loginAs("config.lector", "ROLE_CONFIG_READ", "ROLE_REALM_MTO_VIEWER");
        when(lovClient.findAll(LovResource.PROFILE_STATUSES)).thenReturn(List.of(
                new LovDto(1L, "DRAFT", "Borrador", "ProfileStatus", true),
                new LovDto(2L, "PROVISIONAL", "Provisional", "ProfileStatus", true),
                new LovDto(3L, "DEFINITIVE", "Definitivo", "ProfileStatus", false)));

        UI.getCurrent().navigate(ProfileStatusesView.class);

        @SuppressWarnings("unchecked")
        Grid<LovDto> grid = LocatorJ._get(Grid.class);
        assertEquals(3, GridKt._size(grid));
        assertEquals("DRAFT", GridKt._get(grid, 0).code());
        assertEquals("Definitivo", GridKt._get(grid, 2).description());
    }

    private String menuDiagnostic() {
        return "menu=" + menuLabels()
                + " vaadin=" + MenuConfiguration.getMenuEntries().stream().map(MenuEntry::title).toList()
                + " hasAccess(ProfileStatusesView)=" + context.getBean(AccessAnnotationChecker.class).hasAccess(ProfileStatusesView.class)
                + " auth=" + SecurityContextHolder.getContext().getAuthentication();
    }

    @Test
    void theMenuHidesWhatThePersonCannotOpen() {
        loginAs("almacen.lector", "ROLE_STOCK_READ", "ROLE_REALM_MTO_WAREHOUSE_VIEWER");

        UI.getCurrent().navigate(HomeView.class);

        assertTrue(menuLabels().contains("Inicio"), menuDiagnostic());
        assertFalse(menuLabels().contains("Estados de perfil"), menuDiagnostic());
    }

    @Test
    void theMenuOffersWhatThePersonCanOpen() {
        loginAs("config.lector", "ROLE_CONFIG_READ", "ROLE_REALM_MTO_VIEWER");

        UI.getCurrent().navigate(HomeView.class);

        assertTrue(menuLabels().contains("Estados de perfil"), menuDiagnostic());
    }

    /** Un rol de realm con el mismo nombre que el permiso no abre la pantalla: solo el rol de cliente. */
    @Test
    void aProtectedViewIsNotReachableWithARealmRoleOnly() {
        loginAs("config.impostor", "ROLE_REALM_CONFIG_READ");

        // Karibu convierte la denegacion del control de navegacion en un error explicito.
        assertThrows(MockAccessDeniedException.class, () -> UI.getCurrent().navigate(ProfileStatusesView.class));

        assertTrue(LocatorJ._find(ProfileStatusesView.class).isEmpty());
    }

    @Test
    void anApiErrorBecomesANotificationWithItsReference() {
        loginAs("config.lector", "ROLE_CONFIG_READ");
        ApiProblem problem = new ApiProblem("about:blank", "Service Unavailable", 503,
                "El servicio mto-configuration no esta disponible en este momento.", null,
                null, null, "corr-5", null, null, null, "mto-configuration");
        when(lovClient.findAll(LovResource.PROFILE_STATUSES)).thenThrow(BackofficeApiException.of(
                HttpStatus.SERVICE_UNAVAILABLE, problem, "corr-5", Duration.ofSeconds(30), "GET /api/configuration/profile-statuses"));

        UI.getCurrent().navigate(ProfileStatusesView.class);

        List<Notification> notifications = NotificationsKt.getNotifications();
        assertEquals(1, notifications.size());
        LocatorJ._get(notifications.getFirst(), Span.class,
                spec -> spec.withText("El servicio no esta disponible ahora mismo. Intentalo en 30 s."));
        LocatorJ._get(notifications.getFirst(), Span.class, spec -> spec.withText("Referencia: corr-5"));
        @SuppressWarnings("unchecked")
        Grid<LovDto> grid = LocatorJ._get(Grid.class);
        assertEquals(0, GridKt._size(grid));
    }

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
}

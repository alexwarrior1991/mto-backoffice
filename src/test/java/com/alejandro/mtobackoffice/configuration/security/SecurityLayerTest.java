package com.alejandro.mtobackoffice.configuration.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** El mapeo de roles, el registro OIDC y la resolucion del principal, sin contexto de Spring. */
class SecurityLayerTest {

    private static final String CLIENT_ID = "mto-configuration-api";
    private static final String USERS_CLIENT_ID = "mto-users-api";
    private static final String STOCK_CLIENT_ID = "mto-stock-api";
    private static final String MAINTENANCE_CLIENT_ID = "mto-maintenance-api";
    private static final String NOTIFICATION_CLIENT_ID = "mto-notification-api";
    private static final String RESOURCES_CLIENT_ID = "mto-resources-api";
    private static final List<String> LISTED_CLIENTS = List.of(CLIENT_ID, USERS_CLIENT_ID, STOCK_CLIENT_ID, MAINTENANCE_CLIENT_ID,
            NOTIFICATION_CLIENT_ID, RESOURCES_CLIENT_ID);
    private static final KeycloakProperties PROPERTIES = new KeycloakProperties("http://localhost:8082/realms/mto/",
            "mto-backoffice", "secret", LISTED_CLIENTS);

    private final KeycloakRoleMapper mapper = new KeycloakRoleMapper(LISTED_CLIENTS);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static Set<String> authorities(Iterable<? extends GrantedAuthority> granted) {
        Set<String> names = new java.util.HashSet<>();
        granted.forEach(authority -> names.add(authority.getAuthority()));
        return names;
    }

    @Test
    void clientRolesBecomeRolePrefixedAuthoritiesNormalizedToUpperCase() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.RESOURCE_ACCESS, Map.of(CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("config-read", "lov manage"))))));

        assertTrue(names.containsAll(Set.of(
                "ROLE_CONFIG_READ", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_READ", "ROLE_LOV_MANAGE", "ROLE_CLIENT_MTO_CONFIGURATION_API_LOV_MANAGE")));
    }

    /**
     * Si los roles de realm se emitieran tambien como {@code ROLE_}, crear en Keycloak un rol de
     * realm llamado {@code config-write} bastaria para que el backoffice ofreciera las altas a
     * cualquiera: quien administra el realm no es necesariamente quien escribe el codigo.
     */
    @Test
    void realmRolesNeverProduceThePlainRolePrefixReservedForClientRoles() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("config-write", "mto-admin")))));

        assertTrue(names.contains("ROLE_REALM_CONFIG_WRITE"));
        assertTrue(names.contains("ROLE_REALM_MTO_ADMIN"));
        assertFalse(names.contains("ROLE_CONFIG_WRITE"));
    }

    /**
     * Un cliente que no esta en la lista no aporta nada, aunque venga en el token: el access token del
     * backoffice lleva la audiencia del gateway, y sus {@code ops-*} no son permisos de ninguna vista.
     */
    @Test
    void rolesOfAClientThatIsNotListedAndScopesAreKeptApart() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.SCOPE, "openid profile email",
                JwtClaimNames.RESOURCE_ACCESS, Map.of("mto-gateway-api", Map.of(JwtClaimNames.ROLES, List.of("ops-write"))))));

        assertEquals(Set.of("SCOPE_openid", "SCOPE_profile", "SCOPE_email"), names);
    }

    @Test
    void rolesOfEveryListedClientAreMappedAndQualifiedByTheirClient() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.RESOURCE_ACCESS, Map.of(
                        CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("config-read")),
                        USERS_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("users-read", "users-sessions-write")),
                        STOCK_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("stock-adjust")),
                        MAINTENANCE_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("maintenance-supervise")),
                        NOTIFICATION_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("notification-inbox"))))));

        assertEquals(Set.of(
                "ROLE_CONFIG_READ", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_READ",
                "ROLE_USERS_READ", "ROLE_CLIENT_MTO_USERS_API_USERS_READ",
                "ROLE_USERS_SESSIONS_WRITE", "ROLE_CLIENT_MTO_USERS_API_USERS_SESSIONS_WRITE",
                "ROLE_STOCK_ADJUST", "ROLE_CLIENT_MTO_STOCK_API_STOCK_ADJUST",
                "ROLE_MAINTENANCE_SUPERVISE", "ROLE_CLIENT_MTO_MAINTENANCE_API_MAINTENANCE_SUPERVISE",
                "ROLE_NOTIFICATION_INBOX", "ROLE_CLIENT_MTO_NOTIFICATION_API_NOTIFICATION_INBOX"), names);
    }

    /** La regla que protege config-write vale igual para users-read: un rol de realm nunca abre el modulo. */
    @Test
    void aRealmRoleNamedLikeAUsersPermissionNeverOpensTheModule() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("users-read", "mto-users-admin")))));

        assertTrue(names.contains("ROLE_REALM_USERS_READ"));
        assertTrue(names.contains("ROLE_REALM_MTO_USERS_ADMIN"));
        assertFalse(names.contains("ROLE_USERS_READ"));
    }

    @Test
    void theRegistrationDerivesEveryEndpointFromTheIssuerWithoutDiscovery() {
        ClientRegistration registration = KeycloakClientRegistrations.keycloak(PROPERTIES);
        ClientRegistration.ProviderDetails provider = registration.getProviderDetails();

        assertEquals("keycloak", registration.getRegistrationId());
        assertEquals("http://localhost:8082/realms/mto", provider.getIssuerUri());
        assertEquals("http://localhost:8082/realms/mto/protocol/openid-connect/auth", provider.getAuthorizationUri());
        assertEquals("http://localhost:8082/realms/mto/protocol/openid-connect/token", provider.getTokenUri());
        assertEquals("http://localhost:8082/realms/mto/protocol/openid-connect/certs", provider.getJwkSetUri());
        assertEquals("http://localhost:8082/realms/mto/protocol/openid-connect/logout",
                provider.getConfigurationMetadata().get("end_session_endpoint"));
        assertEquals(JwtClaimNames.PREFERRED_USERNAME, provider.getUserInfoEndpoint().getUserNameAttributeName());
        assertEquals("{baseUrl}/login/oauth2/code/{registrationId}", registration.getRedirectUri());
        assertEquals(Set.of("openid", "profile", "email"), registration.getScopes());
    }

    @Test
    void oidcUserServiceReadsRolesAndAudienceFromTheAccessTokenNotTheIdToken() {
        Instant now = Instant.now();
        OidcIdToken idToken = OidcIdToken.withTokenValue("id-token")
                .issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim("sub", "u-1").claim(JwtClaimNames.PREFERRED_USERNAME, "config.editor")
                .build();
        ClientRegistration registration = KeycloakClientRegistrations.keycloak(PROPERTIES);
        OidcUserRequest request = new OidcUserRequest(registration,
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token", now, now.plusSeconds(300)), idToken);
        JwtDecoder decoder = token -> Jwt.withTokenValue(token)
                .header("alg", "RS256")
                .subject("u-1").issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim(JwtClaimNames.AUDIENCE, List.of("mto-configuration-api", "mto-stock-api", "mto-maintenance-api",
                        "mto-users-api", "mto-notification-api", "mto-gateway-api"))
                .claim(JwtClaimNames.SCOPE, "openid profile email")
                .claim(JwtClaimNames.RESOURCE_ACCESS, Map.of(CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("config-read", "config-write"))))
                .claim(JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("mto-editor")))
                .build();
        BackofficeOidcUserService service = new BackofficeOidcUserService(
                userRequest -> new DefaultOidcUser(Set.of(new OidcUserAuthority(idToken)), idToken, JwtClaimNames.PREFERRED_USERNAME),
                new AccessTokenReader(mapper, ignored -> decoder), new LiveAuthorities(new AccessTokenReader(mapper, ignored -> decoder)));

        OidcUser user = service.loadUser(request);

        Set<String> names = authorities(user.getAuthorities());
        assertTrue(names.containsAll(Set.of("ROLE_CONFIG_READ", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_WRITE", "ROLE_REALM_MTO_EDITOR", "OIDC_USER")));
        assertFalse(names.contains("ROLE_MTO_EDITOR"));
        assertEquals("config.editor", user.getName());
        BackofficeUser backofficeUser = assertInstanceOf(BackofficeUser.class, user);
        assertEquals(6, backofficeUser.getAccessTokenAudience().size());
    }

    @Test
    void anAccessTokenThatCannotBeVerifiedRejectsTheLogin() {
        Instant now = Instant.now();
        OidcIdToken idToken = OidcIdToken.withTokenValue("id-token").issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim("sub", "u-1").build();
        OidcUserRequest request = new OidcUserRequest(KeycloakClientRegistrations.keycloak(PROPERTIES),
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "forged", now, now.plusSeconds(300)), idToken);
        BackofficeOidcUserService service = new BackofficeOidcUserService(
                userRequest -> new DefaultOidcUser(Set.of(new OidcUserAuthority(idToken)), idToken),
                readerOf(), new LiveAuthorities(readerOf()));

        OAuth2AuthenticationException exception = assertThrows(OAuth2AuthenticationException.class, () -> service.loadUser(request));

        assertEquals("invalid_token", exception.getError().getErrorCode());
    }

    @Test
    void currentPrincipalPrefersTheExplicitScopeAndFallsBackToTheSecurityContext() {
        assertEquals(Optional.empty(), CurrentPrincipal.name());

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("config.lector", "n/a", "ROLE_CONFIG_READ"));
        assertEquals(Optional.of("config.lector"), CurrentPrincipal.name());

        assertEquals(Optional.of("job-runner"), CurrentPrincipal.callAs("job-runner", CurrentPrincipal::name));
        assertEquals(Optional.of("config.lector"), CurrentPrincipal.name(), "el scope termina con la tarea");
    }

    @Test
    void securityRolesMatchTheClientRolesOfTheRealmOnceNormalized() {
        Set<String> declared = Set.of(SecurityRoles.CONFIG_READ, SecurityRoles.CONFIG_WRITE, SecurityRoles.CONFIG_DELETE,
                SecurityRoles.CONFIG_IMPORT, SecurityRoles.LOV_MANAGE, SecurityRoles.CONFIG_AUDIT);
        Set<String> fromRealm = Set.of("config-read", "config-write", "config-delete", "config-import", "lov-manage", "config-audit")
                .stream().map(KeycloakRoleMapper::normalize).collect(Collectors.toSet());

        assertEquals(fromRealm, declared);
    }

    @Test
    void userRolesMatchTheClientRolesOfMtoUsersApiOnceNormalized() {
        Set<String> declared = Set.of(UserRoles.USERS_READ, UserRoles.USERS_WRITE, UserRoles.USERS_DELETE,
                UserRoles.USERS_ROLES_WRITE, UserRoles.USERS_PASSWORD_RESET, UserRoles.USERS_PROFILES_WRITE,
                UserRoles.USERS_SESSIONS_WRITE, UserRoles.USERS_CREDENTIALS_WRITE);
        Set<String> fromRealm = Set.of("users-read", "users-write", "users-delete", "users-roles-write", "users-password-reset",
                        "users-profiles-write", "users-sessions-write", "users-credentials-write")
                .stream().map(KeycloakRoleMapper::normalize).collect(Collectors.toSet());

        assertEquals(fromRealm, declared);
    }

    @Test
    void stockRolesMatchTheClientRolesOfMtoStockApiOnceNormalized() {
        Set<String> declared = Set.of(StockRoles.STOCK_READ, StockRoles.STOCK_WRITE, StockRoles.STOCK_DELETE, StockRoles.STOCK_ADJUST);
        Set<String> fromRealm = Set.of("stock-read", "stock-write", "stock-delete", "stock-adjust")
                .stream().map(KeycloakRoleMapper::normalize).collect(Collectors.toSet());

        assertEquals(fromRealm, declared);
    }

    /**
     * Emitir ROLE_ para los seis clientes solo es inocuo mientras sus nombres de rol no se solapen.
     * Los {@code ops-*} se repiten en todos a proposito (son de operacion, no de pantalla) y ninguna
     * vista los comprueba, asi que no hay constante que los nombre.
     */
    @Test
    void theRoleNamesOfTheListedClientsAreDisjoint() {
        Set<String> configuration = Set.of(SecurityRoles.CONFIG_READ, SecurityRoles.CONFIG_WRITE, SecurityRoles.CONFIG_DELETE,
                SecurityRoles.CONFIG_IMPORT, SecurityRoles.LOV_MANAGE, SecurityRoles.CONFIG_AUDIT);
        Set<String> users = Set.of(UserRoles.USERS_READ, UserRoles.USERS_WRITE, UserRoles.USERS_DELETE,
                UserRoles.USERS_ROLES_WRITE, UserRoles.USERS_PASSWORD_RESET, UserRoles.USERS_PROFILES_WRITE,
                UserRoles.USERS_SESSIONS_WRITE, UserRoles.USERS_CREDENTIALS_WRITE);
        Set<String> stock = Set.of(StockRoles.STOCK_READ, StockRoles.STOCK_WRITE, StockRoles.STOCK_DELETE, StockRoles.STOCK_ADJUST);
        Set<String> maintenance = Set.of(MaintenanceRoles.MAINTENANCE_READ, MaintenanceRoles.MAINTENANCE_WRITE,
                MaintenanceRoles.MAINTENANCE_DELETE, MaintenanceRoles.MAINTENANCE_SUPERVISE);
        Set<String> notification = Set.of(NotificationRoles.NOTIFICATION_INBOX, NotificationRoles.NOTIFICATION_ACTIVITY_READ,
                NotificationRoles.NOTIFICATION_ACCESS_READ, NotificationRoles.NOTIFICATION_ADMIN);
        Set<String> resources = Set.of(ResourcesRoles.RESOURCES_READ, ResourcesRoles.RESOURCES_WRITE, ResourcesRoles.RESOURCES_FLEET_WRITE,
                ResourcesRoles.RESOURCES_LOCATION_WRITE, ResourcesRoles.RESOURCES_DELETE, ResourcesRoles.RESOURCES_TELEMATICS_WRITE);

        List<Set<String>> sets = List.of(configuration, users, stock, maintenance, notification, resources);
        for (int i = 0; i < sets.size(); i++) {
            for (int j = i + 1; j < sets.size(); j++) {
                assertTrue(java.util.Collections.disjoint(sets.get(i), sets.get(j)), sets.get(i) + " / " + sets.get(j));
            }
        }
        sets.forEach(set -> assertTrue(set.stream().noneMatch(role -> role.startsWith("OPS_")), set.toString()));
    }

    /** Un rol de realm llamado como un permiso de almacen tampoco abre nada: solo ROLE_REALM_. */
    @Test
    void aRealmRoleNamedLikeAStockPermissionNeverOpensTheModule() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("stock-read", "mto-warehouse-admin")),
                JwtClaimNames.RESOURCE_ACCESS, Map.of(STOCK_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("stock-write"))))));

        assertTrue(names.contains("ROLE_REALM_STOCK_READ"));
        assertTrue(names.contains("ROLE_REALM_MTO_WAREHOUSE_ADMIN"));
        assertTrue(names.contains("ROLE_STOCK_WRITE"));
        assertTrue(names.contains("ROLE_CLIENT_MTO_STOCK_API_STOCK_WRITE"));
        assertFalse(names.contains("ROLE_STOCK_READ"));
    }

    @Test
    void maintenanceRolesMatchTheClientRolesOfMtoMaintenanceApiOnceNormalized() {
        Set<String> declared = Set.of(MaintenanceRoles.MAINTENANCE_READ, MaintenanceRoles.MAINTENANCE_WRITE,
                MaintenanceRoles.MAINTENANCE_DELETE, MaintenanceRoles.MAINTENANCE_SUPERVISE);
        Set<String> fromRealm = Set.of("maintenance-read", "maintenance-write", "maintenance-delete", "maintenance-supervise")
                .stream().map(KeycloakRoleMapper::normalize).collect(Collectors.toSet());

        assertEquals(fromRealm, declared);
    }

    /**
     * Los roles de realm de mantenimiento ({@code mto-maintenance-technician}...) son composites: lo
     * que abre el modulo es el rol de cliente que traen dentro, nunca un rol de realm con su nombre.
     */
    @Test
    void aRealmRoleNamedLikeAMaintenancePermissionNeverOpensTheModule() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("maintenance-supervise", "mto-maintenance-manager")),
                JwtClaimNames.RESOURCE_ACCESS, Map.of(MAINTENANCE_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("maintenance-read"))))));

        assertTrue(names.contains("ROLE_REALM_MAINTENANCE_SUPERVISE"));
        assertTrue(names.contains("ROLE_REALM_MTO_MAINTENANCE_MANAGER"));
        assertTrue(names.contains("ROLE_MAINTENANCE_READ"));
        assertTrue(names.contains("ROLE_CLIENT_MTO_MAINTENANCE_API_MAINTENANCE_READ"));
        assertFalse(names.contains("ROLE_MAINTENANCE_SUPERVISE"));
    }

    @Test
    void notificationRolesMatchTheClientRolesOfMtoNotificationApiOnceNormalized() {
        Set<String> declared = Set.of(NotificationRoles.NOTIFICATION_INBOX, NotificationRoles.NOTIFICATION_ACTIVITY_READ,
                NotificationRoles.NOTIFICATION_ACCESS_READ, NotificationRoles.NOTIFICATION_ADMIN);
        Set<String> fromRealm = Set.of("notification-inbox", "notification-activity-read", "notification-access-read", "notification-admin")
                .stream().map(KeycloakRoleMapper::normalize).collect(Collectors.toSet());

        assertEquals(fromRealm, declared);
    }

    /**
     * Los perfiles del dominio llevan {@code notification-inbox} dentro (la campana es de todo el
     * mundo), pero lo que abre la bandeja es ese rol de cliente, nunca un rol de realm con su nombre;
     * y los accesos piden el suyo, que no viene con el registro.
     */
    @Test
    void aRealmRoleNamedLikeANotificationPermissionNeverOpensTheModule() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("notification-access-read", "mto-auditor")),
                JwtClaimNames.RESOURCE_ACCESS, Map.of(NOTIFICATION_CLIENT_ID,
                        Map.of(JwtClaimNames.ROLES, List.of("notification-inbox", "notification-activity-read"))))));

        assertTrue(names.contains("ROLE_REALM_NOTIFICATION_ACCESS_READ"));
        assertTrue(names.contains("ROLE_REALM_MTO_AUDITOR"));
        assertTrue(names.contains("ROLE_NOTIFICATION_INBOX"));
        assertTrue(names.contains("ROLE_CLIENT_MTO_NOTIFICATION_API_NOTIFICATION_ACTIVITY_READ"));
        assertFalse(names.contains("ROLE_NOTIFICATION_ACCESS_READ"));
    }

    @Test
    void resourcesRolesMatchTheClientRolesOfMtoResourcesApiOnceNormalized() {
        Set<String> declared = Set.of(ResourcesRoles.RESOURCES_READ, ResourcesRoles.RESOURCES_WRITE, ResourcesRoles.RESOURCES_FLEET_WRITE,
                ResourcesRoles.RESOURCES_LOCATION_WRITE, ResourcesRoles.RESOURCES_DELETE, ResourcesRoles.RESOURCES_TELEMATICS_WRITE);
        Set<String> fromRealm = Set.of("resources-read", "resources-write", "resources-fleet-write", "resources-location-write",
                        "resources-delete", "resources-telematics-write")
                .stream().map(KeycloakRoleMapper::normalize).collect(Collectors.toSet());

        assertEquals(fromRealm, declared);
    }

    /**
     * Los perfiles de puesto ({@code mto-position-team-leader}...) y los de recursos son composites:
     * lo que abre Recursos es el rol de cliente que traen dentro, nunca un rol de realm con su nombre.
     */
    @Test
    void aRealmRoleNamedLikeAResourcesPermissionNeverOpensTheModule() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("resources-read", "mto-position-team-leader")),
                JwtClaimNames.RESOURCE_ACCESS, Map.of(RESOURCES_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("resources-location-write"))))));

        assertTrue(names.contains("ROLE_REALM_RESOURCES_READ"));
        assertTrue(names.contains("ROLE_REALM_MTO_POSITION_TEAM_LEADER"));
        assertTrue(names.contains("ROLE_RESOURCES_LOCATION_WRITE"));
        assertTrue(names.contains("ROLE_CLIENT_MTO_RESOURCES_API_RESOURCES_LOCATION_WRITE"));
        assertFalse(names.contains("ROLE_RESOURCES_READ"));
    }

    /**
     * {@code resources-telematics-write} es el de la pasarela de GPS ({@code POST /telematics/positions}): se
     * mapea como los demas permisos de mto-resources, aunque ninguna pantalla lo pida.
     */
    @Test
    void theTelematicsRoleOfMtoResourcesIsMappedLikeTheOtherPermissions() {
        Set<String> names = authorities(mapper.authorities(Map.of(
                JwtClaimNames.RESOURCE_ACCESS, Map.of(RESOURCES_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("resources-telematics-write"))))));

        assertTrue(names.contains("ROLE_" + ResourcesRoles.RESOURCES_TELEMATICS_WRITE));
        assertTrue(names.contains("ROLE_CLIENT_MTO_RESOURCES_API_RESOURCES_TELEMATICS_WRITE"));
        assertFalse(names.contains("ROLE_RESOURCES_READ"));
    }

    // --- Los permisos siguen al access token renovado --------------------------------------------

    private static final String PERSON = "config.editor";
    private static final ClientRegistration REGISTRATION = KeycloakClientRegistrations.keycloak(PROPERTIES);

    /** Un access token "firmado": su valor dice los roles de cliente de configuracion y los de realm que lleva. */
    private static Jwt tokenWith(String value, List<String> clientRoles, List<String> realmRoles, List<String> audience) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(value).header("alg", "RS256")
                .subject("u-1").issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim(JwtClaimNames.AUDIENCE, audience)
                .claim(JwtClaimNames.SCOPE, "openid profile email")
                .claim(JwtClaimNames.RESOURCE_ACCESS, Map.of(CLIENT_ID, Map.of(JwtClaimNames.ROLES, clientRoles)))
                .claim(JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, realmRoles))
                .build();
    }

    /** Lee los tokens que se le han dado por su valor; cualquier otro no se puede verificar. */
    private AccessTokenReader readerOf(Jwt... tokens) {
        Map<String, Jwt> known = java.util.Arrays.stream(tokens).collect(Collectors.toMap(Jwt::getTokenValue, token -> token));
        return new AccessTokenReader(mapper, ignored -> value -> {
            Jwt token = known.get(value);
            if (token == null) {
                throw new BadJwtException("bad signature");
            }
            return token;
        });
    }

    /** Entra como en el login de verdad: el servicio OIDC lee el access token y la sesion guarda sus autoridades. */
    private Authentication logIn(AccessTokenReader reader, LiveAuthorities live, String accessToken) {
        Instant now = Instant.now();
        OidcIdToken idToken = OidcIdToken.withTokenValue("id-token").issuedAt(now).expiresAt(now.plusSeconds(300))
                .claim("sub", "u-1").claim(JwtClaimNames.PREFERRED_USERNAME, PERSON).build();
        BackofficeOidcUserService service = new BackofficeOidcUserService(
                userRequest -> new DefaultOidcUser(Set.of(new OidcUserAuthority(idToken)), idToken, JwtClaimNames.PREFERRED_USERNAME),
                reader, live);
        OidcUser user = service.loadUser(new OidcUserRequest(REGISTRATION,
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, now.plusSeconds(300)), idToken));
        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(user, user.getAuthorities(), "keycloak");
        authentication.setDetails("detalles del login");
        return authentication;
    }

    private static Authentication current() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Lo que hacia falta: se retira un permiso en Keycloak, el siguiente access token ya no lo trae, y
     * la sesion deja de tenerlo sin volver a entrar; el que se concede, aparece. Es lo que hace
     * mto-frontend, que rehace su sesion con cada token renovado.
     */
    @Test
    void aRefreshedAccessTokenReplacesTheAuthoritiesOfTheSession() {
        AccessTokenReader reader = readerOf(
                tokenWith("login", List.of("config-read", "config-write"), List.of("mto-editor"), List.of("mto-configuration-api")),
                tokenWith("refreshed", List.of("config-read", "config-import"), List.of("mto-viewer", "config-write"),
                        List.of("mto-configuration-api", "mto-stock-api")));
        LiveAuthorities live = new LiveAuthorities(reader);
        Authentication login = logIn(reader, live, "login");
        SecurityContextHolder.getContext().setAuthentication(login);

        live.refreshed(REGISTRATION, PERSON, "refreshed");

        Authentication now = current();
        assertNotSame(login, now);
        Set<String> names = authorities(now.getAuthorities());
        assertTrue(names.containsAll(Set.of("ROLE_CONFIG_READ", "ROLE_CONFIG_IMPORT", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_IMPORT",
                "ROLE_REALM_MTO_VIEWER", "OIDC_USER", "SCOPE_openid")), names.toString());
        assertFalse(names.contains("ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_WRITE"), "el permiso retirado: " + names);
        assertFalse(names.contains("ROLE_REALM_MTO_EDITOR"), names.toString());
        // El rol de realm que se llama como el permiso retirado sigue sin concederlo.
        assertTrue(names.contains("ROLE_REALM_CONFIG_WRITE"), names.toString());
        assertFalse(names.contains("ROLE_CONFIG_WRITE"), names.toString());

        // La persona es la misma: su nombre, su ID token (el logout federado lo usa) y los detalles.
        assertEquals(PERSON, now.getName());
        assertEquals("detalles del login", now.getDetails());
        OAuth2AuthenticationToken token = assertInstanceOf(OAuth2AuthenticationToken.class, now);
        assertEquals("keycloak", token.getAuthorizedClientRegistrationId());
        BackofficeUser user = assertInstanceOf(BackofficeUser.class, now.getPrincipal());
        assertEquals("id-token", user.getIdToken().getTokenValue());
        assertEquals(names, authorities(user.getAuthorities()));
        assertEquals(List.of("mto-configuration-api", "mto-stock-api"), user.getAccessTokenAudience());
    }

    /** Solo se cambia cuando cambian los roles: un token nuevo con los mismos deja la sesion como estaba. */
    @Test
    void aRefreshedAccessTokenWithTheSameRolesLeavesTheSessionAsItWas() {
        AccessTokenReader reader = readerOf(
                tokenWith("login", List.of("config-read"), List.of("mto-viewer"), List.of("mto-configuration-api")),
                tokenWith("refreshed", List.of("config-read"), List.of("mto-viewer"), List.of("mto-configuration-api")));
        LiveAuthorities live = new LiveAuthorities(reader);
        Authentication login = logIn(reader, live, "login");
        SecurityContextHolder.getContext().setAuthentication(login);

        live.refreshed(REGISTRATION, PERSON, "refreshed");

        assertSame(login, current());
    }

    /** Un token que no se puede verificar no concede ni retira nada: se queda lo que habia. */
    @Test
    void aRefreshedAccessTokenThatCannotBeVerifiedIsIgnored() {
        AccessTokenReader reader = readerOf(
                tokenWith("login", List.of("config-read"), List.of(), List.of("mto-configuration-api")));
        LiveAuthorities live = new LiveAuthorities(reader);
        Authentication login = logIn(reader, live, "login");
        SecurityContextHolder.getContext().setAuthentication(login);

        live.refreshed(REGISTRATION, PERSON, "forged");
        live.sync();

        assertSame(login, current());
    }

    /**
     * El token se guarda por persona, asi que lo comparten todas sus sesiones (dos navegadores): la que
     * no estaba en el hilo que lo renovo —o lo renovo un hilo de fondo, sin sesion— lo sigue en su
     * siguiente peticion. La sesion de otra persona no se toca.
     */
    @Test
    void everySessionOfThePersonFollowsTheRefreshedTokenAndNobodyElseDoes() throws Exception {
        AccessTokenReader reader = readerOf(
                tokenWith("login", List.of("config-read", "config-write"), List.of(), List.of("mto-configuration-api")),
                tokenWith("refreshed", List.of("config-read"), List.of(), List.of("mto-configuration-api")));
        LiveAuthorities live = new LiveAuthorities(reader);
        Authentication otherBrowser = logIn(reader, live, "login");
        Authentication somebodyElse = new TestingAuthenticationToken("config.lector", "n/a", "ROLE_CONFIG_WRITE");

        // Lo renueva un hilo de fondo (el sondeo): sin contexto de seguridad, no hay sesion que cambiar alli.
        Thread background = Thread.ofVirtual().start(() -> live.refreshed(REGISTRATION, PERSON, "refreshed"));
        background.join();

        SecurityContextHolder.getContext().setAuthentication(otherBrowser);
        live.sync();
        assertEquals(Set.of("ROLE_CONFIG_READ", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_READ"),
                authorities(current().getAuthorities()).stream().filter(name -> name.startsWith("ROLE_")).collect(Collectors.toSet()));
        Authentication followed = current();
        live.sync();
        assertSame(followed, current(), "ya esta al dia: no se vuelve a cambiar");

        SecurityContextHolder.getContext().setAuthentication(somebodyElse);
        live.sync();
        assertSame(somebodyElse, current());

        SecurityContextHolder.clearContext();
        live.sync();
        assertEquals(null, current(), "sin nadie dentro no hay nada que cambiar");
    }

    /**
     * El manager que renueva guarda el cliente autorizado, como siempre, y ademas avisa de los roles
     * del token nuevo: es el unico sitio por el que pasa un token renovado.
     */
    @Test
    void theRefreshHandlerStoresTheAuthorizedClientAndUpdatesTheAuthorities() {
        AccessTokenReader reader = readerOf(
                tokenWith("login", List.of("config-read"), List.of(), List.of("mto-configuration-api")),
                tokenWith("refreshed", List.of("config-read", "config-write"), List.of(), List.of("mto-configuration-api")));
        LiveAuthorities live = new LiveAuthorities(reader);
        Authentication login = logIn(reader, live, "login");
        SecurityContextHolder.getContext().setAuthentication(login);
        InMemoryOAuth2AuthorizedClientService clients =
                new InMemoryOAuth2AuthorizedClientService(new InMemoryClientRegistrationRepository(REGISTRATION));
        Instant now = Instant.now();
        OAuth2AuthorizedClient refreshed = new OAuth2AuthorizedClient(REGISTRATION, PERSON,
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "refreshed", now, now.plusSeconds(300)));

        // El manager crea el principal solo con el nombre: es lo unico que tiene fuera de una peticion.
        new RefreshedTokenHandler(clients, live).onAuthorizationSuccess(refreshed,
                new TestingAuthenticationToken(PERSON, "n/a"), Map.of());

        assertEquals("refreshed", clients.loadAuthorizedClient("keycloak", PERSON).getAccessToken().getTokenValue());
        assertTrue(authorities(current().getAuthorities()).contains("ROLE_CONFIG_WRITE"), "el permiso concedido aparece");
    }
}

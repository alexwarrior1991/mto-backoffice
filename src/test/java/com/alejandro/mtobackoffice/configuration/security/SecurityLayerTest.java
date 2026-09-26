package com.alejandro.mtobackoffice.configuration.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** El mapeo de roles, el registro OIDC y la resolucion del principal, sin contexto de Spring. */
class SecurityLayerTest {

    private static final String CLIENT_ID = "mto-configuration-api";
    private static final String USERS_CLIENT_ID = "mto-users-api";
    private static final String STOCK_CLIENT_ID = "mto-stock-api";
    private static final String MAINTENANCE_CLIENT_ID = "mto-maintenance-api";
    private static final List<String> LISTED_CLIENTS = List.of(CLIENT_ID, USERS_CLIENT_ID, STOCK_CLIENT_ID, MAINTENANCE_CLIENT_ID);
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
                        MAINTENANCE_CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("maintenance-supervise"))))));

        assertEquals(Set.of(
                "ROLE_CONFIG_READ", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_READ",
                "ROLE_USERS_READ", "ROLE_CLIENT_MTO_USERS_API_USERS_READ",
                "ROLE_USERS_SESSIONS_WRITE", "ROLE_CLIENT_MTO_USERS_API_USERS_SESSIONS_WRITE",
                "ROLE_STOCK_ADJUST", "ROLE_CLIENT_MTO_STOCK_API_STOCK_ADJUST",
                "ROLE_MAINTENANCE_SUPERVISE", "ROLE_CLIENT_MTO_MAINTENANCE_API_MAINTENANCE_SUPERVISE"), names);
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
                        "mto-users-api", "mto-gateway-api"))
                .claim(JwtClaimNames.SCOPE, "openid profile email")
                .claim(JwtClaimNames.RESOURCE_ACCESS, Map.of(CLIENT_ID, Map.of(JwtClaimNames.ROLES, List.of("config-read", "config-write"))))
                .claim(JwtClaimNames.REALM_ACCESS, Map.of(JwtClaimNames.ROLES, List.of("mto-editor")))
                .build();
        BackofficeOidcUserService service = new BackofficeOidcUserService(
                userRequest -> new DefaultOidcUser(Set.of(new OidcUserAuthority(idToken)), idToken, JwtClaimNames.PREFERRED_USERNAME),
                mapper, ignored -> decoder);

        OidcUser user = service.loadUser(request);

        Set<String> names = authorities(user.getAuthorities());
        assertTrue(names.containsAll(Set.of("ROLE_CONFIG_READ", "ROLE_CLIENT_MTO_CONFIGURATION_API_CONFIG_WRITE", "ROLE_REALM_MTO_EDITOR", "OIDC_USER")));
        assertFalse(names.contains("ROLE_MTO_EDITOR"));
        assertEquals("config.editor", user.getName());
        BackofficeUser backofficeUser = assertInstanceOf(BackofficeUser.class, user);
        assertEquals(5, backofficeUser.getAccessTokenAudience().size());
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
                mapper, ignored -> token -> {
                    throw new BadJwtException("bad signature");
                });

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
     * Emitir ROLE_ para los cuatro clientes solo es inocuo mientras sus nombres de rol no se solapen.
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

        List<Set<String>> sets = List.of(configuration, users, stock, maintenance);
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
}

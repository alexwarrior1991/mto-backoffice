package com.alejandro.mtobackoffice.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Traduce los claims de un access token de Keycloak a las autoridades que comprueban las vistas
 * ({@code @RolesAllowed}) y el menu.
 *
 * <p>Es el mismo mapeo que hace {@code KeycloakJwtAuthenticationConverter} en los servicios, y por
 * la misma razon existe aqui: {@code oauth2Login} construye las autoridades desde el ID token, y
 * Keycloak pone {@code resource_access} solo en el access token. Sin esto ninguna vista veria un
 * rol.</p>
 *
 * <p>Se leen los roles de <b>varios</b> clientes ({@code app.keycloak.roles-client-ids}): los de
 * {@code mto-configuration-api} para las pantallas de configuracion, y los de {@code mto-users-api},
 * {@code mto-stock-api} y {@code mto-maintenance-api} para los modulos de usuarios, almacen y
 * mantenimiento. Cada rol sale dos veces: como {@code ROLE_<ROL>}, que es lo que comprueban las
 * vistas, y como {@code ROLE_CLIENT_<CLIENTE>_<ROL>}, cualificado por su cliente. Que un
 * {@code users-read} de un cliente no se confunda con uno de otro depende de que los clientes
 * leidos no compartan nombres de rol ({@code config-*} y {@code lov-manage}, {@code users-*},
 * {@code stock-*}, {@code maintenance-*}); {@code SecurityLayerTest} lo comprueba. Los
 * {@code ops-*} si se repiten en todos, y por eso ninguna vista los comprueba. Los clientes del
 * token que no esten en la lista no aportan nada.</p>
 */
public final class KeycloakRoleMapper {

    private final List<String> rolesClientIds;

    public KeycloakRoleMapper(List<String> rolesClientIds) {
        this.rolesClientIds = List.copyOf(rolesClientIds);
    }

    public Set<GrantedAuthority> authorities(Map<String, Object> claims) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.addAll(extractScopes(claims));
        authorities.addAll(extractRealmRoles(claims));
        authorities.addAll(extractClientRoles(claims));
        return Set.copyOf(authorities);
    }

    private Collection<GrantedAuthority> extractScopes(Map<String, Object> claims) {
        if (!(claims.get(JwtClaimNames.SCOPE) instanceof String scope) || scope.isBlank()) {
            return Set.of();
        }
        return Stream.of(scope.split(" "))
                .filter(value -> !value.isBlank())
                .map(value -> SecurityAuthorityPrefixes.SCOPE_PREFIX + value)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Los roles de realm se emiten <b>solo</b> con el prefijo {@code ROLE_REALM_}, nunca con
     * {@code ROLE_} a secas.
     *
     * <p>Emitir ambos haria que un rol de realm y uno de cliente que se llamaran igual acabaran en
     * la misma autoridad: quien tuviera el de realm pasaria una comprobacion pensada para el de
     * cliente. Y como quien administra el realm no es necesariamente quien escribe el codigo,
     * bastaria con crear alli un rol llamado igual que un permiso para concederselo a cualquiera.
     * Los perfiles de negocio ({@code mto-editor}, {@code mto-admin}, {@code mto-users-manager}...)
     * son roles de realm compuestos que Keycloak expande al emitir el token, asi que sus permisos
     * ya llegan dentro de {@code resource_access}; comprobar un perfil sigue siendo posible
     * nombrandolo: {@code hasRole("REALM_MTO_ADMIN")}.</p>
     */
    private Collection<GrantedAuthority> extractRealmRoles(Map<String, Object> claims) {
        if (!(claims.get(JwtClaimNames.REALM_ACCESS) instanceof Map<?, ?> realmAccess)) {
            return Set.of();
        }
        return extractRoles(realmAccess).stream()
                .map(role -> SecurityAuthorityPrefixes.REALM_ROLE_PREFIX + normalize(role))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Collection<GrantedAuthority> extractClientRoles(Map<String, Object> claims) {
        if (!(claims.get(JwtClaimNames.RESOURCE_ACCESS) instanceof Map<?, ?> resourceAccess)) {
            return Set.of();
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String clientId : rolesClientIds) {
            if (!(resourceAccess.get(clientId) instanceof Map<?, ?> clientAccess)) {
                continue;
            }
            String qualified = SecurityAuthorityPrefixes.CLIENT_ROLE_PREFIX + normalize(clientId) + "_";
            for (String role : extractRoles(clientAccess)) {
                authorities.add(new SimpleGrantedAuthority(SecurityAuthorityPrefixes.ROLE_PREFIX + normalize(role)));
                authorities.add(new SimpleGrantedAuthority(qualified + normalize(role)));
            }
        }
        return authorities;
    }

    private static Set<String> extractRoles(Map<?, ?> accessMap) {
        if (!(accessMap.get(JwtClaimNames.ROLES) instanceof Collection<?> roles)) {
            return Set.of();
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * {@code config-read} se convierte en {@code CONFIG_READ}, listo para {@code hasRole("CONFIG_READ")};
     * {@code mto-users-api} en {@code MTO_USERS_API}. La conversion no es inyectiva
     * ({@code config-read} y {@code config_read} coinciden), motivo para no crear en el mismo
     * cliente dos roles que solo se diferencien en el separador.
     */
    static String normalize(String value) {
        return value.trim().replace('-', '_').replace(' ', '_').toUpperCase();
    }
}

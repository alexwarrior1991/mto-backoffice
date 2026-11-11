package com.alejandro.mtobackoffice.configuration.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Lo que concede un access token ya verificado: las autoridades que salen de sus claims
 * ({@link KeycloakRoleMapper}) y sus audiencias.
 */
public record AccessTokenGrants(Set<GrantedAuthority> authorities, List<String> audience) {

    public AccessTokenGrants {
        authorities = Set.copyOf(authorities);
        audience = audience == null ? List.of() : List.copyOf(audience);
    }

    /** Los nombres de sus roles ({@code ROLE_*}): lo que se compara para saber si los permisos han cambiado. */
    public Set<String> roleNames() {
        return roleNames(authorities);
    }

    static Set<String> roleNames(Iterable<? extends GrantedAuthority> authorities) {
        Set<String> names = new java.util.HashSet<>();
        for (GrantedAuthority authority : authorities) {
            if (isRole(authority)) {
                names.add(authority.getAuthority());
            }
        }
        return names;
    }

    static boolean isRole(GrantedAuthority authority) {
        String name = authority.getAuthority();
        return name != null && name.startsWith(SecurityAuthorityPrefixes.ROLE_PREFIX);
    }

    /** Estas autoridades sobre las de una sesion: sus roles se sustituyen, lo demas (OIDC_USER, scopes) se queda. */
    Set<GrantedAuthority> over(Iterable<? extends GrantedAuthority> current) {
        Set<GrantedAuthority> merged = new java.util.HashSet<>(authorities);
        for (GrantedAuthority authority : current) {
            if (!isRole(authority)) {
                merged.add(authority);
            }
        }
        return merged.stream().collect(Collectors.toUnmodifiableSet());
    }
}

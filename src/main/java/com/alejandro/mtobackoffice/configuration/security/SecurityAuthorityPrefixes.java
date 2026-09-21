package com.alejandro.mtobackoffice.configuration.security;

/**
 * Prefijos de las autoridades que se derivan del access token. Los mismos que en los servicios:
 * los permisos que comprueban las vistas son roles de CLIENTE ({@code ROLE_}), y los roles de realm
 * viven en su propio prefijo para que nunca puedan hacerse pasar por un permiso.
 */
public final class SecurityAuthorityPrefixes {

    private SecurityAuthorityPrefixes() {
    }

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String REALM_ROLE_PREFIX = "ROLE_REALM_";
    /**
     * Sinonimo cualificado por cliente: {@code ROLE_CLIENT_MTO_USERS_API_USERS_READ}. Nombra al
     * cliente para que, si dos clientes leidos compartieran un nombre de rol, una comprobacion
     * pudiera exigir el de uno en concreto.
     */
    public static final String CLIENT_ROLE_PREFIX = "ROLE_CLIENT_";
    public static final String SCOPE_PREFIX = "SCOPE_";
}

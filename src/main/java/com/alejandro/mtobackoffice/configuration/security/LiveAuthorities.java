package com.alejandro.mtobackoffice.configuration.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Los permisos de cada persona siguen a su ultimo access token, no al del login.
 *
 * <p>Las autoridades de la sesion se calculan al entrar, y los access token duran cinco minutos: si
 * se retira un permiso en Keycloak, el siguiente token ya no lo trae y los servicios responden 403,
 * pero el menu, {@code @RolesAllowed} y los botones seguirian pintando con las del login hasta volver
 * a entrar (y al reves, uno concedido no apareceria). mto-frontend rehace su sesion con cada token
 * renovado, y las dos aplicaciones tienen que hacer lo mismo.</p>
 *
 * <p>Aqui se guarda, por persona, lo que concede su ultimo access token verificado: el del login
 * ({@link #issued}) y el de cada renovacion ({@link #refreshed}). Es por persona y no por sesion
 * porque el token tambien lo es (el cliente autorizado se guarda por nombre de principal): dos
 * navegadores de la misma persona comparten token, y por tanto permisos.</p>
 *
 * <p>La sesion se pone al dia con {@link #sync()}, <b>siempre desde un hilo suyo</b>, que es donde
 * el {@code SecurityContextHolder} da su contexto: al empezar cada peticion
 * ({@link LiveAuthoritiesFilter}) y, si la renovacion ocurre en una peticion o dentro de
 * {@code UI.access()}, en ese mismo momento. Un hilo de fondo (el sondeo) no tiene sesion: solo deja
 * apuntado lo que concede el token, y cada sesion de la persona lo recoge en su siguiente peticion.
 * La {@code Authentication} solo se sustituye si los roles han cambiado.</p>
 */
public class LiveAuthorities {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveAuthorities.class);

    private final AccessTokenReader reader;
    private final Map<String, AccessTokenGrants> latest = new ConcurrentHashMap<>();

    public LiveAuthorities(AccessTokenReader reader) {
        this.reader = reader;
    }

    /** Lo que concede un access token ya verificado de esta persona (el del login). */
    public void issued(String principalName, AccessTokenGrants grants) {
        latest.put(principalName, grants);
    }

    /**
     * Un access token renovado. Se verifica como el del login; si no se puede (firma, emisor, el JWK
     * Set que no responde), no concede ni retira nada: se queda lo que habia, y si el token de verdad
     * no vale lo dira el 401 del gateway.
     */
    public void refreshed(ClientRegistration registration, String principalName, String accessToken) {
        AccessTokenGrants grants;
        try {
            grants = reader.read(registration, accessToken);
        } catch (RuntimeException exception) {
            LOGGER.warn("The refreshed access token of '{}' could not be verified; its permissions stay as they were: {}",
                    principalName, exception.getMessage());
            return;
        }
        issued(principalName, grants);
        sync();
    }

    /**
     * Pone la sesion de este hilo al dia con el ultimo token de su persona. No hace nada sin nadie
     * dentro, con una autenticacion que no es la del login OIDC, o si los roles son los mismos.
     */
    public void sync() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (!(context.getAuthentication() instanceof OAuth2AuthenticationToken current)
                || !(current.getPrincipal() instanceof BackofficeUser user)) {
            return;
        }
        AccessTokenGrants grants = latest.get(current.getName());
        if (grants == null) {
            return;
        }
        Set<String> held = AccessTokenGrants.roleNames(current.getAuthorities());
        if (held.equals(grants.roleNames())) {
            return;
        }
        BackofficeUser updatedUser = user.with(grants.over(user.getAuthorities()), grants.audience());
        OAuth2AuthenticationToken updated = new OAuth2AuthenticationToken(updatedUser,
                grants.over(current.getAuthorities()), current.getAuthorizedClientRegistrationId());
        updated.setDetails(current.getDetails());
        context.setAuthentication(updated);
        LOGGER.info("The permissions of '{}' follow the refreshed access token: {} role(s) before, {} now",
                current.getName(), held.size(), grants.roleNames().size());
    }
}

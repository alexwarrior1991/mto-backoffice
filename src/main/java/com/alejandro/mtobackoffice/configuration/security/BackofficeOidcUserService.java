package com.alejandro.mtobackoffice.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.HashSet;
import java.util.Set;

/**
 * Completa al usuario OIDC con los roles del ACCESS token.
 *
 * <p>{@code oauth2Login} construye las autoridades desde el ID token, pero Keycloak pone
 * {@code resource_access} —los roles de cliente, que son los permisos— solo en el access token
 * (todos los mappers del realm son {@code id.token.claim=false}). Sin este servicio,
 * {@code @RolesAllowed} no veria ni un rol. Un {@code GrantedAuthoritiesMapper} no sirve: no recibe
 * el access token.</p>
 *
 * <p>El access token se verifica (firma contra el JWK Set del realm y emisor) antes de fiarse de
 * sus claims, igual que haria un resource server: lo hace {@link AccessTokenReader}, el mismo que lee
 * cada token renovado. Lo que concede el del login se apunta en {@link LiveAuthorities}, para que
 * otra sesion de la misma persona, que desde ahora comparte este token, lo siga tambien.</p>
 */
public class BackofficeOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OAuth2UserService<OidcUserRequest, OidcUser> delegate;
    private final AccessTokenReader accessTokens;
    private final LiveAuthorities liveAuthorities;

    public BackofficeOidcUserService(AccessTokenReader accessTokens, LiveAuthorities liveAuthorities) {
        this(new OidcUserService(), accessTokens, liveAuthorities);
    }

    BackofficeOidcUserService(OAuth2UserService<OidcUserRequest, OidcUser> delegate,
                              AccessTokenReader accessTokens, LiveAuthorities liveAuthorities) {
        this.delegate = delegate;
        this.accessTokens = accessTokens;
        this.liveAuthorities = liveAuthorities;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = delegate.loadUser(userRequest);
        AccessTokenGrants grants = read(userRequest);

        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        authorities.addAll(grants.authorities());

        BackofficeUser backofficeUser = new BackofficeUser(authorities, user.getIdToken(), user.getUserInfo(),
                nameAttribute(userRequest), grants.audience());
        liveAuthorities.issued(backofficeUser.getName(), grants);
        return backofficeUser;
    }

    private AccessTokenGrants read(OidcUserRequest userRequest) {
        try {
            return accessTokens.read(userRequest.getClientRegistration(), userRequest.getAccessToken().getTokenValue());
        } catch (JwtException exception) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token",
                    "The access token could not be verified: " + exception.getMessage(), null), exception);
        }
    }

    private static String nameAttribute(OidcUserRequest userRequest) {
        String configured = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        return configured == null || configured.isBlank() ? IdTokenClaimNames.SUB : configured;
    }
}

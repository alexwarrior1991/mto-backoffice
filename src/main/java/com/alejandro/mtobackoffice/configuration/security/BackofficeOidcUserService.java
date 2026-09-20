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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
 * sus claims, igual que haria un resource server. El decodificador se crea al primer login y se
 * reutiliza.</p>
 */
public class BackofficeOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OAuth2UserService<OidcUserRequest, OidcUser> delegate;
    private final KeycloakRoleMapper roleMapper;
    private final Function<ClientRegistration, JwtDecoder> decoderFactory;
    private final Map<String, JwtDecoder> decoders = new ConcurrentHashMap<>();

    public BackofficeOidcUserService(KeycloakRoleMapper roleMapper) {
        this(new OidcUserService(), roleMapper, BackofficeOidcUserService::decoderFor);
    }

    BackofficeOidcUserService(OAuth2UserService<OidcUserRequest, OidcUser> delegate,
                              KeycloakRoleMapper roleMapper,
                              Function<ClientRegistration, JwtDecoder> decoderFactory) {
        this.delegate = delegate;
        this.roleMapper = roleMapper;
        this.decoderFactory = decoderFactory;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = delegate.loadUser(userRequest);
        Jwt accessToken = decode(userRequest);

        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        authorities.addAll(roleMapper.authorities(accessToken.getClaims()));

        List<String> audience = accessToken.getAudience() == null ? List.of() : accessToken.getAudience();
        return new BackofficeUser(authorities, user.getIdToken(), user.getUserInfo(), nameAttribute(userRequest), audience);
    }

    private Jwt decode(OidcUserRequest userRequest) {
        ClientRegistration registration = userRequest.getClientRegistration();
        JwtDecoder decoder = decoders.computeIfAbsent(registration.getRegistrationId(),
                id -> decoderFactory.apply(registration));
        try {
            return decoder.decode(userRequest.getAccessToken().getTokenValue());
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

    static JwtDecoder decoderFor(ClientRegistration registration) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(registration.getProviderDetails().getJwkSetUri())
                .build();
        String issuer = registration.getProviderDetails().getIssuerUri();
        decoder.setJwtValidator(issuer == null
                ? JwtValidators.createDefault()
                : JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }
}

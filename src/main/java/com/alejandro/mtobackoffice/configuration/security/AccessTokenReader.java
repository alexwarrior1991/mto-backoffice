package com.alejandro.mtobackoffice.configuration.security;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Lee lo que concede un access token, despues de verificarlo.
 *
 * <p>El access token se verifica (firma contra el JWK Set del realm y emisor) antes de fiarse de
 * sus claims, igual que haria un resource server, y sus roles se mapean con
 * {@link KeycloakRoleMapper}. Es el unico sitio que lo hace: lo usan el login
 * ({@link BackofficeOidcUserService}) y cada renovacion ({@link LiveAuthorities}), para que un token
 * renovado se lea exactamente como el del login. El decodificador se crea al primer uso (el JWK Set
 * se pide entonces, no al arrancar) y se reutiliza.</p>
 */
public class AccessTokenReader {

    private final KeycloakRoleMapper roleMapper;
    private final Function<ClientRegistration, JwtDecoder> decoderFactory;
    private final Map<String, JwtDecoder> decoders = new ConcurrentHashMap<>();

    public AccessTokenReader(KeycloakRoleMapper roleMapper) {
        this(roleMapper, AccessTokenReader::decoderFor);
    }

    AccessTokenReader(KeycloakRoleMapper roleMapper, Function<ClientRegistration, JwtDecoder> decoderFactory) {
        this.roleMapper = roleMapper;
        this.decoderFactory = decoderFactory;
    }

    /** @throws JwtException si el token no se puede verificar */
    public AccessTokenGrants read(ClientRegistration registration, String tokenValue) {
        JwtDecoder decoder = decoders.computeIfAbsent(registration.getRegistrationId(),
                id -> decoderFactory.apply(registration));
        Jwt accessToken = decoder.decode(tokenValue);
        return new AccessTokenGrants(roleMapper.authorities(accessToken.getClaims()), accessToken.getAudience());
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

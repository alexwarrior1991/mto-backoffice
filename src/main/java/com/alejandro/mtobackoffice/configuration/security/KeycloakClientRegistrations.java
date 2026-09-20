package com.alejandro.mtobackoffice.configuration.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.Map;

/**
 * El registro OIDC de esta aplicacion, construido a mano a partir de {@code app.keycloak.*}.
 *
 * <p>No se declara con {@code spring.security.oauth2.client.provider.keycloak.issuer-uri} a
 * proposito: con esa clave Spring descubre los endpoints por HTTP mientras crea el bean, y la
 * aplicacion no arrancaria si Keycloak no esta listo todavia (tampoco arrancaria ningun test de
 * contexto). Los endpoints de Keycloak son fijos respecto al issuer, asi que se derivan de el; el
 * JWK Set se descarga al primer login, no antes.</p>
 *
 * <p>{@code end_session_endpoint} va en los metadatos del proveedor porque es de donde lo lee el
 * {@code OidcClientInitiatedLogoutSuccessHandler} que crea {@code VaadinSecurityConfigurer}: sin el,
 * «Salir» cerraria la sesion de esta aplicacion pero no la de Keycloak, y la siguiente entrada no
 * pediria credenciales.</p>
 */
@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakClientRegistrations {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakClientRegistrations.class);

    /** Id del registro: forma la ruta de login ({@code /oauth2/authorization/keycloak}) y la de callback. */
    public static final String REGISTRATION_ID = "keycloak";

    static final String END_SESSION_ENDPOINT = "end_session_endpoint";

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(KeycloakProperties properties) {
        if (properties.clientSecret() == null || properties.clientSecret().isBlank()) {
            LOGGER.warn("app.keycloak.client-secret is empty: nobody will be able to log in until the secret "
                    + "of client '{}' is configured", properties.clientId());
        }
        return new InMemoryClientRegistrationRepository(keycloak(properties));
    }

    static ClientRegistration keycloak(KeycloakProperties properties) {
        String issuer = properties.issuer();
        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId(properties.clientId())
                .clientSecret(properties.clientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .issuerUri(issuer)
                .authorizationUri(issuer + "/protocol/openid-connect/auth")
                .tokenUri(issuer + "/protocol/openid-connect/token")
                .jwkSetUri(issuer + "/protocol/openid-connect/certs")
                // Sin userinfo: preferred_username y email ya vienen en el ID token con los scopes
                // profile y email, y asi entrar no cuesta una llamada mas.
                .userNameAttributeName(JwtClaimNames.PREFERRED_USERNAME)
                .providerConfigurationMetadata(Map.of(END_SESSION_ENDPOINT, issuer + "/protocol/openid-connect/logout"))
                .clientName("Keycloak MTO")
                .build();
    }
}

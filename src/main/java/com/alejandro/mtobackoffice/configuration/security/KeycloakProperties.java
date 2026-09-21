package com.alejandro.mtobackoffice.configuration.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Lo que esta aplicacion necesita saber de Keycloak ({@code app.keycloak.*}).
 *
 * @param issuerUri     realm que emite los tokens; de el se derivan todos los endpoints OIDC
 * @param clientId      cliente confidencial de esta aplicacion en el realm
 * @param clientSecret  su secreto; vacio arranca (con aviso) y falla al entrar, no antes
 * @param rolesClientIds clientes cuyos roles del access token se convierten en autoridades ROLE_*
 *                       (los permisos de configuracion y los del modulo de usuarios)
 */
@Validated
@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProperties(
        @NotBlank String issuerUri,
        @DefaultValue("mto-backoffice") @NotBlank String clientId,
        @DefaultValue("") String clientSecret,
        @DefaultValue({"mto-configuration-api", "mto-users-api"}) @NotEmpty List<String> rolesClientIds
) {

    /** El issuer sin barra final, para concatenar rutas. */
    public String issuer() {
        return issuerUri.endsWith("/") ? issuerUri.substring(0, issuerUri.length() - 1) : issuerUri;
    }
}

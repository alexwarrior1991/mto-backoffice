package com.alejandro.mtobackoffice.configuration.client;

import com.alejandro.mtobackoffice.client.error.SessionExpiredApiException;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * El access token de una persona, pedido por su nombre de principal desde cualquier hilo.
 *
 * <p>Apoyado en {@code AuthorizedClientServiceOAuth2AuthorizedClientManager} y no en el manager
 * ligado a la peticion HTTP: {@code UI.access()}, push y el sondeo de trabajos corren sin
 * {@code HttpServletRequest}. El login ya hizo el authorization_code; aqui solo se refresca
 * (los access token duran cinco minutos). Sin token, o con el refresh caducado, la unica salida es
 * volver a entrar, y eso es lo que dice {@link SessionExpiredApiException}.</p>
 */
public class UserTokenProvider {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final String clientRegistrationId;

    public UserTokenProvider(OAuth2AuthorizedClientManager authorizedClientManager, String clientRegistrationId) {
        this.authorizedClientManager = authorizedClientManager;
        this.clientRegistrationId = clientRegistrationId;
    }

    public String accessToken(String principalName) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal(principalName)
                .build();
        OAuth2AuthorizedClient client;
        try {
            client = authorizedClientManager.authorize(request);
        } catch (ClientAuthorizationException exception) {
            throw new SessionExpiredApiException("The Keycloak session of '" + principalName
                    + "' can no longer be refreshed (" + exception.getError().getErrorCode() + "): log in again", exception);
        }
        if (client == null) {
            throw new SessionExpiredApiException("There is no token for '" + principalName + "': log in again", null);
        }
        return client.getAccessToken().getTokenValue();
    }
}

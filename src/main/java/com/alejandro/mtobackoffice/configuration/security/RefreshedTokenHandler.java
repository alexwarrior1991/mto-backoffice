package com.alejandro.mtobackoffice.configuration.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import java.util.Map;

/**
 * Lo que hace el manager cuando renueva el token de una persona: guardar el cliente autorizado, que
 * es lo que haria por defecto, y avisar a {@link LiveAuthorities} de lo que concede el token nuevo.
 * Solo se llama cuando de verdad se ha renovado, no cada vez que se pide el token.
 */
public class RefreshedTokenHandler implements OAuth2AuthorizationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClients;
    private final LiveAuthorities liveAuthorities;

    public RefreshedTokenHandler(OAuth2AuthorizedClientService authorizedClients, LiveAuthorities liveAuthorities) {
        this.authorizedClients = authorizedClients;
        this.liveAuthorities = liveAuthorities;
    }

    @Override
    public void onAuthorizationSuccess(OAuth2AuthorizedClient authorizedClient, Authentication principal,
                                       Map<String, Object> attributes) {
        authorizedClients.saveAuthorizedClient(authorizedClient, principal);
        liveAuthorities.refreshed(authorizedClient.getClientRegistration(), principal.getName(),
                authorizedClient.getAccessToken().getTokenValue());
    }
}

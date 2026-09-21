package com.alejandro.mtobackoffice.configuration.client;

import com.alejandro.mtobackoffice.client.error.SessionExpiredApiException;
import com.alejandro.mtobackoffice.configuration.security.CurrentPrincipal;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/** Pone el Bearer de la persona en cuyo nombre sale la llamada. Sin principal no hay llamada. */
public class BearerTokenInterceptor implements ClientHttpRequestInterceptor {

    private final UserTokenProvider tokens;

    public BearerTokenInterceptor(UserTokenProvider tokens) {
        this.tokens = tokens;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String principal = CurrentPrincipal.name().orElseThrow(() -> new SessionExpiredApiException(
                "No authenticated user in this thread: the call to " + request.getURI().getPath()
                        + " must run as somebody (CurrentPrincipal.runAs)", null));
        request.getHeaders().setBearerAuth(tokens.accessToken(principal));
        return execution.execute(request, body);
    }
}

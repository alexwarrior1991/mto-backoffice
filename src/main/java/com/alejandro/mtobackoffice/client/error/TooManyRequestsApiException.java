package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatusCode;

import java.time.Duration;
import java.util.Optional;

/**
 * 429: el servicio no tiene cupo para un trabajo en segundo plano. No es un error del cliente: el
 * trabajo existe, persistido como {@code REJECTED}, y viaja en el cuerpo de la respuesta
 * ({@link #getBody()}) junto con un {@code Retry-After}. Quien lo lance lo ensena como rechazado y
 * dice cuando volver a intentarlo.
 */
public class TooManyRequestsApiException extends BackofficeApiException {

    private final transient Duration retryAfter;
    private final String body;

    TooManyRequestsApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId,
                                String operation, Duration retryAfter, String body) {
        super(message, status, problem, correlationId, operation, null);
        this.retryAfter = retryAfter;
        this.body = body;
    }

    public Optional<Duration> getRetryAfter() {
        return Optional.ofNullable(retryAfter);
    }

    /** El cuerpo tal cual llego: en los trabajos, el JSON del trabajo rechazado. */
    public String getBody() {
        return body == null ? "" : body;
    }
}

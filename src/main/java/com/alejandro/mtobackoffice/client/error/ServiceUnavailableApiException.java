package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatusCode;

import java.time.Duration;
import java.util.Optional;

/**
 * El servicio no esta: 503 del fallback del circuit breaker del gateway (con {@code Retry-After}
 * y {@code service}), o 502/504 por un fallo o timeout aguas abajo. Es transitorio: la vista lo
 * dice y ofrece reintentar.
 */
public class ServiceUnavailableApiException extends BackofficeApiException {

    private final transient Duration retryAfter;

    ServiceUnavailableApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId,
                                   String operation, Duration retryAfter) {
        super(message, status, problem, correlationId, operation, null);
        this.retryAfter = retryAfter;
    }

    public Optional<Duration> getRetryAfter() {
        return Optional.ofNullable(retryAfter);
    }
}

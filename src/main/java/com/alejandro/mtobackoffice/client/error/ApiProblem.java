package com.alejandro.mtobackoffice.client.error;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.Instant;
import java.util.List;

/**
 * Cuerpo de error tolerante: la union de los {@code application/problem+json} que pueden llegar.
 *
 * <ul>
 *   <li>mto-configuration: los campos RFC 9457 mas {@code code} (no {@code errorCode}),
 *       {@code traceId} (no {@code correlationId}), {@code retryable}, {@code timestamp} y
 *       {@code errors} de tres campos;</li>
 *   <li>el gateway en 401/403: solo {@code correlationId}, sin {@code code};</li>
 *   <li>el fallback del gateway (503): {@code service} y {@code correlationId};</li>
 *   <li>mto-users: los campos RFC 9457 mas {@code errorCode} (que aqui cae en {@code code}),
 *       {@code correlationId}, {@code timestamp} y {@code validationErrors} de dos campos, sin
 *       codigo por error (que cae en {@code errors}).</li>
 * </ul>
 * Todo es nullable y lo desconocido se ignora, asi que un campo nuevo en cualquiera de ellos no
 * rompe la decodificacion.
 */
public record ApiProblem(
        String type,
        String title,
        Integer status,
        String detail,
        String instance,
        @JsonAlias("errorCode") String code,
        String traceId,
        String correlationId,
        Instant timestamp,
        Boolean retryable,
        @JsonAlias("validationErrors") List<ApiFieldError> errors,
        String service
) {

    public ApiProblem {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public static ApiProblem empty() {
        return new ApiProblem(null, null, null, null, null, null, null, null, null, null, List.of(), null);
    }

    /** Lo que se le puede pedir a alguien que cite: el traceId del servicio o el correlationId del gateway. */
    public String reference() {
        return hasText(traceId) ? traceId : correlationId;
    }

    public boolean hasFieldErrors() {
        return !errors.isEmpty();
    }

    static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

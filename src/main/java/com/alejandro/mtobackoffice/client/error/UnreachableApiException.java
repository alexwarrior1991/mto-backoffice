package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatus;

/**
 * La llamada no ha tenido respuesta: el gateway no acepta la conexion, no se resuelve su nombre o
 * no ha contestado dentro del {@code read-timeout}. No hay estado ni cuerpo que leer, asi que no
 * pasa por {@link ApiErrorDecoder}: lo traduce {@code TransportFailureInterceptor}, y es el
 * {@code NetworkError} de mto-frontend. Es transitorio, y su referencia es el
 * {@code X-Correlation-Id} que se mando, por si la llamada llego al gateway antes de cortarse.
 *
 * <p>{@link #getStatus()} dice 503, que es lo que una descarga servida por esta aplicacion
 * responde al navegador; ningun servicio lo ha dicho.</p>
 */
public class UnreachableApiException extends BackofficeApiException {

    public UnreachableApiException(String operation, String correlationId, Throwable cause) {
        super(describe(operation, cause), HttpStatus.SERVICE_UNAVAILABLE, ApiProblem.empty(), correlationId,
                operation, cause);
    }

    private static String describe(String operation, Throwable cause) {
        StringBuilder message = new StringBuilder(operation == null ? "API call" : operation).append(" -> no response");
        if (cause != null) {
            message.append(" (").append(cause.getClass().getSimpleName());
            if (cause.getMessage() != null) {
                message.append(": ").append(cause.getMessage());
            }
            message.append(')');
        }
        return message.toString();
    }
}

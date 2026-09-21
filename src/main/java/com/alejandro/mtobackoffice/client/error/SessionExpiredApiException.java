package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * No hay un token valido con el que llamar: el gateway ha respondido 401, el refresh token ha
 * caducado (sesion SSO agotada) o no hay principal en el hilo. La unica salida es volver a entrar.
 */
public class SessionExpiredApiException extends BackofficeApiException {

    public SessionExpiredApiException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, ApiProblem.empty(), null, null, cause);
    }

    SessionExpiredApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId,
                               String operation, Throwable cause) {
        super(message, status, problem, correlationId, operation, cause);
    }
}

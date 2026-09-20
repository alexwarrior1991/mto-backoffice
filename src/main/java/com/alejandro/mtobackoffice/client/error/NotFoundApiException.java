package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatusCode;

/** 404 del servicio (el gateway no emite 404 por si mismo salvo ruta desconocida). */
public class NotFoundApiException extends BackofficeApiException {

    NotFoundApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId, String operation) {
        super(message, status, problem, correlationId, operation, null);
    }
}

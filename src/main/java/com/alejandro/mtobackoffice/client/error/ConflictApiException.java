package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatusCode;

/** 409: conflicto de concurrencia o recurso duplicado. */
public class ConflictApiException extends BackofficeApiException {

    ConflictApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId, String operation) {
        super(message, status, problem, correlationId, operation, null);
    }
}

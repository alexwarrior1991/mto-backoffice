package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatusCode;

/** Peticion rechazada por validacion (400) o por una regla de negocio (422); {@link ApiProblem#errors()} trae el detalle campo a campo cuando lo hay. */
public class ValidationApiException extends BackofficeApiException {

    ValidationApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId, String operation) {
        super(message, status, problem, correlationId, operation, null);
    }
}

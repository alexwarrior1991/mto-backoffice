package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatusCode;

/** El servicio ha dicho 403: la persona no tiene el permiso. Lo normal es que la vista ni se ofreciera; si llega aqui, el menu y el realm no coinciden. */
public class ForbiddenApiException extends BackofficeApiException {

    ForbiddenApiException(String message, HttpStatusCode status, ApiProblem problem, String correlationId, String operation) {
        super(message, status, problem, correlationId, operation, null);
    }
}

package com.alejandro.mtobackoffice.client.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.Duration;

/**
 * Fallo de una llamada a la API, ya tipado por lo que la UI tiene que hacer con el. Las subclases
 * son el contrato con las vistas; el estado HTTP y el problema original quedan para el detalle.
 */
public class BackofficeApiException extends RuntimeException {

    private final transient HttpStatusCode status;
    private final transient ApiProblem problem;
    private final String correlationId;
    private final String operation;

    protected BackofficeApiException(String message, HttpStatusCode status, ApiProblem problem,
                                     String correlationId, String operation, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.problem = problem == null ? ApiProblem.empty() : problem;
        this.correlationId = correlationId;
        this.operation = operation;
    }

    /** Traduce una respuesta de error a la excepcion que las vistas saben tratar. */
    public static BackofficeApiException of(HttpStatusCode status, ApiProblem problem, String correlationId,
                                            Duration retryAfter, String operation) {
        return of(status, problem, correlationId, retryAfter, operation, null);
    }

    /**
     * Igual, con el cuerpo tal cual llego: un 429 de los trabajos en segundo plano trae en el
     * cuerpo el trabajo rechazado, que no es un problema y no cabe en {@link ApiProblem}.
     */
    public static BackofficeApiException of(HttpStatusCode status, ApiProblem problem, String correlationId,
                                            Duration retryAfter, String operation, String body) {
        String message = describe(status, problem, operation);
        return switch (status.value()) {
            case 400, 422 -> new ValidationApiException(message, status, problem, correlationId, operation);
            case 401 -> new SessionExpiredApiException(message, status, problem, correlationId, operation, null);
            case 403 -> new ForbiddenApiException(message, status, problem, correlationId, operation);
            case 404 -> new NotFoundApiException(message, status, problem, correlationId, operation);
            case 409 -> new ConflictApiException(message, status, problem, correlationId, operation);
            case 429 -> new TooManyRequestsApiException(message, status, problem, correlationId, operation, retryAfter, body);
            case 502, 503, 504 -> new ServiceUnavailableApiException(message, status, problem, correlationId, operation, retryAfter);
            default -> new BackofficeApiException(message, status, problem, correlationId, operation, null);
        };
    }

    private static String describe(HttpStatusCode status, ApiProblem problem, String operation) {
        StringBuilder message = new StringBuilder(operation == null ? "API call" : operation)
                .append(" -> ").append(status.value());
        HttpStatus resolved = HttpStatus.resolve(status.value());
        if (resolved != null) {
            message.append(' ').append(resolved.getReasonPhrase());
        }
        if (problem != null) {
            if (ApiProblem.hasText(problem.code())) {
                message.append(" [").append(problem.code()).append(']');
            }
            if (ApiProblem.hasText(problem.detail())) {
                message.append(": ").append(problem.detail());
            } else if (ApiProblem.hasText(problem.title())) {
                message.append(": ").append(problem.title());
            }
        }
        return message.toString();
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public ApiProblem getProblem() {
        return problem;
    }

    /** El id que ponia la cabecera X-Correlation-Id de la respuesta, o el del cuerpo. */
    public String getCorrelationId() {
        return ApiProblem.hasText(correlationId) ? correlationId : problem.correlationId();
    }

    /** Identificador para citar: traceId del servicio si lo hay, si no el de correlacion. */
    public String getReference() {
        String reference = problem.reference();
        return ApiProblem.hasText(reference) ? reference : getCorrelationId();
    }

    public String getOperation() {
        return operation;
    }
}

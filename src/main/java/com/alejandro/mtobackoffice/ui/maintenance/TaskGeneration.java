package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationErrorDto;
import com.alejandro.mtobackoffice.client.error.ApiProblem;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.maintenance.OperationClient;
import com.alejandro.mtobackoffice.client.maintenance.OrderClient;
import com.alejandro.mtobackoffice.ui.support.SharedPolling;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

/**
 * Generar las tareas de una orden de verdad, que con un EP grande puede tardar mas de lo que se quiere
 * esperar: se pide con {@code Prefer: respond-async, wait=5}. Si el servicio termina en esos segundos
 * contesta lo de siempre (200 con el resultado, o su error); si no, 202 con la operacion, que sigue alli y
 * se pregunta cada {@link #FOLLOW_PERIOD} desde el dialogo ({@code GET /operations/{id}}), como los
 * trabajos de mto-configuration. La previsualizacion ({@code dryRun}) sigue siendo sincrona: no escribe
 * ni se pone en fila. El tope de operaciones del despliegue es un 429 {@code OPS-429} con su
 * {@code Retry-After}, que se dice como siempre.
 */
@Component
public class TaskGeneration {

    /** Lo que se le pide al servicio: la respuesta de siempre si termina en 5 s, la operacion si no. */
    public static final String PREFER = "respond-async, wait=5";
    public static final Duration FOLLOW_PERIOD = Duration.ofSeconds(2);

    private static final TypeReference<OperationDto<GenerateTasksResultDto>> OPERATION = new TypeReference<>() {
    };

    private final OrderClient orders;
    private final OperationClient operations;
    private final ObjectMapper mapper;
    private final SharedPolling polling;

    public TaskGeneration(OrderClient orders, OperationClient operations, ObjectMapper mapper, SharedPolling polling) {
        this.orders = orders;
        this.operations = operations;
        this.mapper = mapper;
        this.polling = polling;
    }

    /** Lo que contesto el servicio: el resultado, o la operacion que sigue alli. */
    public sealed interface Started permits Done, Following {
    }

    public record Done(GenerateTasksResultDto result) implements Started {
    }

    public record Following(OperationDto<GenerateTasksResultDto> operation) implements Started {
    }

    public Started start(UUID orderId, GenerateTasksRequest request) {
        ResponseEntity<JsonNode> response = orders.generateTasksAsync(orderId, request, PREFER);
        if (response.getStatusCode().isSameCodeAs(HttpStatus.ACCEPTED)) {
            return new Following(mapper.convertValue(response.getBody(), OPERATION));
        }
        return new Done(mapper.convertValue(response.getBody(), GenerateTasksResultDto.class));
    }

    public OperationDto<GenerateTasksResultDto> status(UUID operationId) {
        return operations.generation(operationId);
    }

    /** Pregunta cada {@link #FOLLOW_PERIOD} desde el hilo compartido; quien sigue la operacion lo cancela. */
    public ScheduledFuture<?> follow(Runnable pass) {
        return polling.every(FOLLOW_PERIOD, pass);
    }

    /**
     * El error de una operacion fallida como el que habria dado la llamada sincrona, para decirlo con la
     * tabla de siempre ({@code UiErrors}); una abandonada (su instancia dejo de latir) es {@code OPS-410}.
     */
    public static BackofficeApiException failureOf(OperationDto<?> operation) {
        OperationErrorDto error = operation.error();
        int status = error == null || error.status() == null ? 500 : error.status();
        ApiProblem problem = new ApiProblem(null, HttpStatus.valueOf(status).name(), status, error == null ? null : error.message(), null,
                error == null ? null : error.errorCode(), null, null, null, false, null, null);
        return BackofficeApiException.of(HttpStatusCode.valueOf(status), problem, null, null, "GET /api/maintenance/operations/" + operation.id());
    }
}

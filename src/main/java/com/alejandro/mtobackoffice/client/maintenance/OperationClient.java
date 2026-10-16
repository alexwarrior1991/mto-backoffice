package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OperationDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

/**
 * Las operaciones de fondo de mto-maintenance. El {@code Location} del 202 es la ruta interna del
 * servicio y no se sigue: se pregunta por el id, a traves del gateway.
 */
@HttpExchange("/api/maintenance/operations")
public interface OperationClient {

    /** Una generacion de tareas: al terminar con exito, su resultado es el de la llamada sincrona. */
    @GetExchange("/{id}")
    OperationDto<GenerateTasksResultDto> generation(@PathVariable("id") UUID id);
}

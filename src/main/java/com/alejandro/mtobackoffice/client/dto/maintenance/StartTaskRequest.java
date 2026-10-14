package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/** Iniciar una tarea pendiente en un turno en curso de su via (o de la conectada, si su activo une dos), con la orden en curso. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StartTaskRequest(UUID shiftId, String assignedUser) {
}

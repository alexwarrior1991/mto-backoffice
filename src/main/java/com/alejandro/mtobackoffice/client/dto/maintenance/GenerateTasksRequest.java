package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Generar una tarea por perfil del tramo; sin tipos, los del servicio (grupos 1, 2 y 4). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateTasksRequest(List<String> taskTypeCodes, Boolean withChecklist) {
}

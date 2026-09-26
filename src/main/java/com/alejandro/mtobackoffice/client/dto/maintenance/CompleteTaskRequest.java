package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Completar una tarea en un turno en curso de su via (la fila del parte). {@code workComplete=false}
 * deja los defectos abiertos con {@code repairPlannedDate}; si no, nacen ya resueltos en este turno.
 * {@code taskTypeCodes}, si viaja, sustituye a los tipos de la tarea antes de comprobar la posesion.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompleteTaskRequest(UUID shiftId, List<String> taskTypeCodes, String notes, String defectsFound, Boolean workComplete,
                                  LocalDate repairPlannedDate, List<InlineDefectRequest> inlineDefects,
                                  List<TaskMaterialRequest> materials, List<String> photoRefs) {
}

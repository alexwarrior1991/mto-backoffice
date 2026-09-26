package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Un defecto encontrado al completar una tarea (y quiza corregido en el mismo turno). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InlineDefectRequest(DefectSeverity severity, String description, String technicalNotes, String correctionType,
                                  String partsReplaced) {
}

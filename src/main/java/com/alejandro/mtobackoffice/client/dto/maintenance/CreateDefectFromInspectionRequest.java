package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Defecto desde una inspeccion; sin gravedad, la deriva el servicio del resultado. {@code force} para uno leve. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateDefectFromInspectionRequest(DefectSeverity severity, String description, String technicalNotes, Boolean force) {
}

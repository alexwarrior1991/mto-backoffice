package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

/** Alta de una tarea en una orden no terminada. Con {@code withChecklist} (o un tipo de diagnostico) copia la plantilla del activo. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskRequest(String description, UUID assetId, String assignedUser, List<String> taskTypeCodes, Boolean withChecklist) {
}

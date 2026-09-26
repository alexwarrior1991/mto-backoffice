package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

/** Alta de una orden sobre un activo activo; nace en borrador (una urgente, ademas, critica). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderRequest(String title, String description, MaintenanceOrderType type, MaintenancePriority priority, UUID assetId,
                           LocalDate plannedDate, UUID teamId, String assignedUser, UUID stockProjectId) {
}

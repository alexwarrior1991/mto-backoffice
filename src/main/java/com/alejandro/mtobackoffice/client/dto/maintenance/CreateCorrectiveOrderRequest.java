package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

/** Orden correctiva desde una inspeccion; lo que no viaja lo pone el servicio (una insegura es urgente y critica). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateCorrectiveOrderRequest(String title, String description, MaintenancePriority priority, LocalDate plannedDate, UUID teamId) {
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

/** Registrar una linea; fuera de borrador se reserva al momento en mto-stock. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MaterialUsageRequest(UUID materialId, String materialCode, UUID warehouseId, BigDecimal plannedQuantity, String unit, UUID taskId,
                                   Boolean allowOverConsumption) {
}

package com.alejandro.mtobackoffice.client.dto.stock;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Un ajuste de inventario; pide {@code stock-adjust} ademas de {@code stock-write}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AdjustmentRequest(UUID materialId, UUID warehouseId, AdjustmentDirection direction, BigDecimal quantity,
                                Instant occurredAt, String externalReference, String notes) {
}

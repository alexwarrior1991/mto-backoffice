package com.alejandro.mtobackoffice.client.dto.stock;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Una transferencia entre dos almacenes distintos; el servicio responde con los dos apuntes. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransferRequest(UUID materialId, UUID sourceWarehouseId, UUID targetWarehouseId, BigDecimal quantity,
                              Instant occurredAt, String externalReference, String notes) {
}

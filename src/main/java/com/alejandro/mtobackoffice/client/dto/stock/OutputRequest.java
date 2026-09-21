package com.alejandro.mtobackoffice.client.dto.stock;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Una salida de material. Con {@code reservationId} consume esa reserva: tiene que ser la del mismo
 * material y almacen, y la cantidad, exactamente la reservada.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OutputRequest(UUID materialId, UUID warehouseId, UUID projectId, UUID reservationId, BigDecimal quantity,
                            Instant occurredAt, String externalReference, String notes) {
}

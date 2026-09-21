package com.alejandro.mtobackoffice.client.dto.stock;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Alta de una reserva, que nace activa; sin fecha el servicio pone ahora. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReservationRequest(UUID materialId, UUID warehouseId, UUID projectId, BigDecimal quantity, Instant reservedAt) {
}

package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.util.UUID;

/** Modificacion de una reserva activa: almacen, proyecto y cantidad; el material no cambia. */
public record ReservationUpdateRequest(UUID warehouseId, UUID projectId, BigDecimal quantity) {
}

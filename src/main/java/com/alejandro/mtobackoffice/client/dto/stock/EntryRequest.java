package com.alejandro.mtobackoffice.client.dto.stock;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Una entrada de material en un almacen, con proveedor opcional; sin fecha el servicio pone ahora. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EntryRequest(UUID materialId, UUID warehouseId, UUID supplierId, BigDecimal quantity, Instant occurredAt,
                           String externalReference, String notes) {
}

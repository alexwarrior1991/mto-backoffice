package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

/** Un material usado en la tarea: el servicio lo registra como linea de la orden, ya consumida, y lo reserva en mto-stock. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskMaterialRequest(UUID materialId, String materialCode, UUID warehouseId, BigDecimal quantity, String unit) {
}

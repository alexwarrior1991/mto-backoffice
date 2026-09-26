package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.UUID;

/** Un material consumido en el mes, sumado por el servicio. */
public record MonthlyMaterialLineDto(UUID materialId, String materialCode, String unit, BigDecimal consumedQuantity) {
}

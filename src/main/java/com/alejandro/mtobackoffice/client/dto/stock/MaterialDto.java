package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.util.UUID;

/** Un material del catalogo; la unidad es texto libre en el servicio. */
public record MaterialDto(UUID id, String code, String name, String unitOfMeasure, BigDecimal minimumStockLevel,
                          Boolean active, AuditDto audit) {

    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    public MaterialSummaryDto summary() {
        return new MaterialSummaryDto(id, code, name, unitOfMeasure, active);
    }
}

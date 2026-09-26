package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Modificacion parcial de una linea. Lo previsto de una linea reservada no cambia (409
 * {@code MAT-001}): se quita y se registra otra vez.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MaterialUsageUpdateRequest(BigDecimal plannedQuantity, BigDecimal consumedQuantity, Boolean allowOverConsumption) {

    /** Nada que mandar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return plannedQuantity == null && consumedQuantity == null && allowOverConsumption == null;
    }
}

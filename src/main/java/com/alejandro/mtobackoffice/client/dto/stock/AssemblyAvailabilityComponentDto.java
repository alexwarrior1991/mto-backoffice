package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;

/** Un componente en el calculo de disponibilidad; {@code limitingComponent} puede ser verdadero en varios a la vez. */
public record AssemblyAvailabilityComponentDto(MaterialSummaryDto material, BigDecimal requiredQuantityPerAssembly,
                                               BigDecimal onHandQuantity, BigDecimal activeReservedQuantity, BigDecimal availableQuantity,
                                               BigDecimal producibleAssemblyQuantity, Boolean limitingComponent) {

    public boolean isLimiting() {
        return Boolean.TRUE.equals(limitingComponent);
    }
}

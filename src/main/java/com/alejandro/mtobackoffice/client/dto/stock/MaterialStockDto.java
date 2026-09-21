package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Las existencias de un material, en un almacen o en todos ({@code warehouse} nulo entonces):
 * fisico, reservado (solo las reservas activas), disponible y si esta por debajo del minimo.
 */
public record MaterialStockDto(MaterialSummaryDto material, WarehouseSummaryDto warehouse, BigDecimal onHandQuantity,
                               BigDecimal activeReservedQuantity, BigDecimal availableQuantity, BigDecimal minimumStockLevel,
                               Boolean lowStock, Instant calculatedAt) {

    public boolean isLowStock() {
        return Boolean.TRUE.equals(lowStock);
    }
}

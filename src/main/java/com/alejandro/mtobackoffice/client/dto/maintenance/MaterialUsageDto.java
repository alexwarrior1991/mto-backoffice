package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Una linea de material de una orden: el material y el almacen son ids de mto-stock (con el codigo y
 * la descripcion copiados al registrarla), lo previsto y lo consumido, y como va con el almacen.
 * Se reserva al planificar la orden, se consume al completarla y se libera al cancelarla.
 */
public record MaterialUsageDto(UUID id, UUID orderId, UUID taskId, UUID materialId, String materialCode, String materialDescriptionSnapshot,
                               UUID warehouseId, BigDecimal plannedQuantity, BigDecimal consumedQuantity, String unit,
                               Boolean allowOverConsumption, UUID stockReservationId, StockSyncStatus stockSyncStatus, String stockSyncError,
                               AuditDto audit, Long version) {

    public boolean isReserved() {
        return stockReservationId != null;
    }

    public String materialLabel() {
        return materialDescriptionSnapshot == null || materialDescriptionSnapshot.isBlank()
                ? materialCode : materialCode + " - " + materialDescriptionSnapshot;
    }
}

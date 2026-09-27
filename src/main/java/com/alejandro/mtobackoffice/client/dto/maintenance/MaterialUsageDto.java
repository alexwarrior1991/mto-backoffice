package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Una linea de material de una orden: el material y el almacen son ids de mto-stock (con el codigo y
 * la descripcion copiados al registrarla), lo previsto y lo consumido, y como va con el almacen.
 * Se reserva al planificar la orden, se consume al completarla y se libera al cancelarla.
 * {@code stockRequestInDoubt} es la peticion que mando al almacen y se quedo sin respuesta, si hay una.
 */
public record MaterialUsageDto(UUID id, UUID orderId, UUID taskId, UUID materialId, String materialCode, String materialDescriptionSnapshot,
                               UUID warehouseId, BigDecimal plannedQuantity, BigDecimal consumedQuantity, String unit,
                               Boolean allowOverConsumption, UUID stockReservationId, StockSyncStatus stockSyncStatus, String stockSyncError,
                               StockRequestType stockRequestInDoubt, AuditDto audit, Long version) {

    public boolean isReserved() {
        return stockReservationId != null;
    }

    /**
     * Hay una peticion al almacen sin respuesta: hasta que conteste, el servicio rechaza (409
     * {@code MAT-001}) cambiar lo previsto o lo consumido de la linea y el proyecto de almacen de la orden.
     */
    public boolean isInDoubt() {
        return stockRequestInDoubt != null;
    }

    /**
     * Quitarla no se ofrece con una salida en duda, porque el material quiza ya salio y el servicio
     * la rechaza; tampoco con una peticion que no se conoce. Con una reserva en duda si: el servicio la
     * confirma para liberarla.
     */
    public boolean isRemovable() {
        return stockRequestInDoubt == null || stockRequestInDoubt == StockRequestType.RESERVATION;
    }

    public String materialLabel() {
        return materialDescriptionSnapshot == null || materialDescriptionSnapshot.isBlank()
                ? materialCode : materialCode + " - " + materialDescriptionSnapshot;
    }
}

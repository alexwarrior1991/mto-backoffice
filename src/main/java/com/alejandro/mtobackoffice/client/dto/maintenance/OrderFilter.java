package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Los filtros de la lista de ordenes. Lo que va a {@code null} no viaja y no filtra; un texto en
 * blanco se trata como {@code null}, porque {@code code=} filtraria por el codigo vacio.
 * {@code plannedFrom} y {@code plannedTo} son inclusivos.
 */
public record OrderFilter(MaintenanceOrderStatus status, MaintenanceOrderType type, MaintenancePriority priority,
                          UUID assetId, CatenaryAssetType assetType, Long trackId, Long stationId, Long executionPackageId,
                          LocalDate plannedFrom, LocalDate plannedTo, String assignedUser, UUID teamId, String code) {

    public static final OrderFilter NONE = new OrderFilter(null, null, null, null, null, null, null, null, null, null, null, null, null);

    public OrderFilter {
        assignedUser = blankToNull(assignedUser);
        code = blankToNull(code);
    }

    /** Solo las de un activo: la pestana de ordenes de su fila. */
    public static OrderFilter ofAsset(UUID assetId) {
        return new OrderFilter(null, null, null, assetId, null, null, null, null, null, null, null, null, null);
    }

    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text.trim();
    }
}

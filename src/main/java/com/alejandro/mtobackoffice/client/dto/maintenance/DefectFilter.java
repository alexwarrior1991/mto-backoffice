package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.Instant;
import java.util.UUID;

/** Los filtros de la lista de defectos; lo que va a {@code null} no viaja. */
public record DefectFilter(DefectSeverity severity, DefectStatus status, UUID assetId, UUID orderId, Long trackId, Long executionPackageId,
                           Instant detectedFrom, Instant detectedTo) {

    public static final DefectFilter NONE = new DefectFilter(null, null, null, null, null, null, null, null);

    /** Los defectos vinculados a una orden. */
    public static DefectFilter ofOrder(UUID orderId) {
        return new DefectFilter(null, null, null, orderId, null, null, null, null);
    }
}

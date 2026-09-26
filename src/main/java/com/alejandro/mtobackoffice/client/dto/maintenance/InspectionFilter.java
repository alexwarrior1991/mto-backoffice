package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.LocalDate;
import java.util.UUID;

/** Los filtros de la lista de inspecciones; lo que va a {@code null} no viaja. Las fechas son inclusivas. */
public record InspectionFilter(InspectionResult result, CatenaryAssetType assetType, Long trackId, Long executionPackageId,
                               LocalDate inspectionFrom, LocalDate inspectionTo, String inspector, UUID originOrderId) {

    public static final InspectionFilter NONE = new InspectionFilter(null, null, null, null, null, null, null, null);

    public InspectionFilter {
        inspector = inspector == null || inspector.isBlank() ? null : inspector.trim();
    }

    /** Las inspecciones hechas desde una orden de inspeccion. */
    public static InspectionFilter ofOrder(UUID orderId) {
        return new InspectionFilter(null, null, null, null, null, null, null, orderId);
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Una inspeccion registrada en el turno, tal como sale en su parte: lo que se miro, donde, que salio y
 * el defecto o la orden correctiva que abrio.
 */
public record ShiftReportInspectionDto(UUID inspectionId, String code, LocalDate inspectionDate, String assetCode, String assetName,
                                       CatenaryAssetType assetType, Long trackId, BigDecimal kp, InspectionKind inspectionKind,
                                       InspectionResult result, String inspector, String detectedDefects, String generatedDefectCode,
                                       String generatedOrderCode) {

    /** El codigo del activo y, si lo hay, su nombre. */
    public String assetLabel() {
        if (assetName == null || assetName.isBlank() || assetName.equals(assetCode)) {
            return assetCode == null ? "" : assetCode;
        }
        return assetCode == null ? assetName : assetCode + " - " + assetName;
    }
}

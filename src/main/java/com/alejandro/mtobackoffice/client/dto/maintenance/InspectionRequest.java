package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Alta de una inspeccion: activo, fecha y resultado obligatorios; {@code originOrderId} si viene de una orden de inspeccion. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InspectionRequest(UUID assetId, LocalDate inspectionDate, String inspector, InspectionKind inspectionKind,
                                InspectionResult result, String description, String detectedDefects, String recommendedActions,
                                BigDecimal kp, UUID originOrderId, UUID shiftId) {
}

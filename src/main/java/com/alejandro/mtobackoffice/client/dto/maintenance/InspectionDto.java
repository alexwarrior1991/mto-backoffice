package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Una inspeccion de un activo, con los puntos copiados de la plantilla activa de su tipo y lo que
 * genero (defecto y orden correctiva, una vez cada uno: repetir devuelve lo mismo).
 */
public record InspectionDto(UUID id, String code, AssetSummaryDto asset, Long executionPackageId, Long trackId, Long stationId,
                            BigDecimal kp, LocalDate inspectionDate, String inspector, InspectionKind inspectionKind, UUID templateId,
                            InspectionResult result, String description, String detectedDefects, String recommendedActions,
                            UUID generatedDefectId, UUID generatedOrderId, UUID originOrderId, UUID shiftId, List<CheckItemDto> items,
                            AuditDto audit, Long version) {

    public InspectionDto {
        items = items == null ? List.of() : List.copyOf(items);
    }
}

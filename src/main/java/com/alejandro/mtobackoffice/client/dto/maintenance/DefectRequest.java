package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Alta de un defecto: activo, gravedad y descripcion obligatorios. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DefectRequest(UUID assetId, DefectSeverity severity, String description, String technicalNotes, Instant detectedAt,
                            UUID inspectionId, UUID orderId, BigDecimal startKp, BigDecimal endKp, String correctionType,
                            String partsReplaced, LocalDate repairPlannedDate, List<String> photoRefs) {
}

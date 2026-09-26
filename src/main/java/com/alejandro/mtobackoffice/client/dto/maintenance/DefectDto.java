package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Un defecto de catenaria: donde esta, como de grave es, de que inspeccion o tarea salio y la orden que lo corrige. */
public record DefectDto(UUID id, String code, AssetSummaryDto asset, UUID inspectionId, UUID orderId, DefectSeverity severity,
                        DefectStatus status, String description, String technicalNotes, Instant detectedAt, Instant resolvedAt,
                        String resolutionNotes, String discardReason, Long executionPackageId, Long trackId, Long stationId,
                        BigDecimal startKp, BigDecimal endKp, String correctionType, String partsReplaced, UUID resolvedInShiftId,
                        LocalDate repairPlannedDate, UUID foundInTaskId, List<String> photoRefs, AuditDto audit) {

    public DefectDto {
        photoRefs = photoRefs == null ? List.of() : List.copyOf(photoRefs);
    }
}

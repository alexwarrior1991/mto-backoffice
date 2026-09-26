package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Una fila del parte del turno: la tarea trabajada, su perfil, lo hecho, lo encontrado y lo usado. */
public record ShiftReportRowDto(int number, UUID taskId, String orderCode, Long executionPackageId, Long trackId, String profileCode,
                                String profileName, BigDecimal kp, String sectioning, List<String> switches, List<String> taskTypeCodes,
                                String worksPerformed, String defectsFound, List<String> materials, Instant startedAt, Instant completedAt,
                                MaintenanceTaskStatus status, boolean workComplete, LocalDate repairPlannedDate, List<String> photoRefs) {

    public ShiftReportRowDto {
        switches = switches == null ? List.of() : List.copyOf(switches);
        taskTypeCodes = taskTypeCodes == null ? List.of() : List.copyOf(taskTypeCodes);
        materials = materials == null ? List.of() : List.copyOf(materials);
        photoRefs = photoRefs == null ? List.of() : List.copyOf(photoRefs);
    }
}

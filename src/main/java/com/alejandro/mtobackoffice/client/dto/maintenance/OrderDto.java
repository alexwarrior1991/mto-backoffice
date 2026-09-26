package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Una orden de mantenimiento. Los ids de paquete, via y estacion son de mto-configuration y el del
 * proyecto, de mto-stock: el servicio no guarda sus nombres, y la pantalla los pide a su servicio.
 * {@code taskCount}, {@code completedTaskCount}, {@code estimatedMinutes} y {@code estimatedShifts}
 * los calcula el servicio y no se pueden ordenar.
 */
public record OrderDto(UUID id, String code, String title, String description, MaintenanceOrderType type,
                       MaintenanceOrderStatus status, MaintenancePriority priority, AssetSummaryDto asset,
                       Long executionPackageId, Long trackId, Long stationId, BigDecimal startKp, BigDecimal endKp,
                       LocalDate plannedDate, Instant actualStartDate, Instant actualEndDate, TeamSummaryDto team,
                       String assignedUser, String closingNotes, String cancellationReason, UUID originInspectionId,
                       UUID originDefectId, UUID stockProjectId, int taskCount, int completedTaskCount,
                       BigDecimal estimatedMinutes, int estimatedShifts, AuditDto audit) {
}

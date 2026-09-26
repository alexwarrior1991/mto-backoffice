package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Un turno nocturno: equipo, ventana, posesion y las vias que recorre (ids de mto-configuration),
 * con los seccionadores que se abren para la posesion. {@code netWorkMinutes} lo calcula el servicio
 * al cerrar si no se le da.
 */
public record ShiftDto(UUID id, String code, LocalDate shiftDate, TeamSummaryDto team, String baseName, String vehicle,
                       PossessionType possessionType, Instant plannedStart, Instant plannedEnd, Instant actualStart, Instant actualEnd,
                       Instant voltageCutoffAt, Integer netWorkMinutes, List<AssetSummaryDto> blockingDisconnectors, String earthingPoints,
                       String parkingPlace, Long executionPackageId, List<Long> trackIds, BigDecimal startKp, BigDecimal endKp,
                       String personnel, String measurementEquipment, ShiftStatus status, String observations, AuditDto audit, Long version) {

    public ShiftDto {
        blockingDisconnectors = blockingDisconnectors == null ? List.of() : List.copyOf(blockingDisconnectors);
        trackIds = trackIds == null ? List.of() : List.copyOf(trackIds);
    }
}

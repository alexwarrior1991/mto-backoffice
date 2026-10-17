package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Un turno nocturno: equipo, ventana, posesion y las vias que recorre (ids de mto-configuration),
 * con los seccionadores que se abren para la posesion. {@code netWorkMinutes} lo calcula el servicio
 * al cerrar si no se le da. Su dotacion (jefe, supervisor y linemen) y sus vehiculos son de
 * mto-resources, con donde quedo cada vehiculo al cerrarlo.
 */
public record ShiftDto(UUID id, String code, LocalDate shiftDate, TeamSummaryDto team, String baseName, String vehicle,
                       PossessionType possessionType, Instant plannedStart, Instant plannedEnd, Instant actualStart, Instant actualEnd,
                       Instant voltageCutoffAt, Integer netWorkMinutes, List<AssetSummaryDto> blockingDisconnectors, String earthingPoints,
                       String parkingPlace, Long executionPackageId, List<Long> trackIds, BigDecimal startKp, BigDecimal endKp,
                       String personnel, String measurementEquipment, ShiftStatus status, String observations, AuditDto audit, Long version,
                       PersonSummaryDto teamLeader, PersonSummaryDto supervisor, List<PersonSummaryDto> linemen, List<ShiftVehicleDto> vehicles) {

    public ShiftDto {
        blockingDisconnectors = blockingDisconnectors == null ? List.of() : List.copyOf(blockingDisconnectors);
        trackIds = trackIds == null ? List.of() : List.copyOf(trackIds);
        linemen = linemen == null ? List.of() : List.copyOf(linemen);
        vehicles = vehicles == null ? List.of() : List.copyOf(vehicles);
    }

    /** Un turno sin dotacion ni vehiculos de mto-resources, como los de antes. */
    public ShiftDto(UUID id, String code, LocalDate shiftDate, TeamSummaryDto team, String baseName, String vehicle,
                    PossessionType possessionType, Instant plannedStart, Instant plannedEnd, Instant actualStart, Instant actualEnd,
                    Instant voltageCutoffAt, Integer netWorkMinutes, List<AssetSummaryDto> blockingDisconnectors, String earthingPoints,
                    String parkingPlace, Long executionPackageId, List<Long> trackIds, BigDecimal startKp, BigDecimal endKp,
                    String personnel, String measurementEquipment, ShiftStatus status, String observations, AuditDto audit, Long version) {
        this(id, code, shiftDate, team, baseName, vehicle, possessionType, plannedStart, plannedEnd, actualStart, actualEnd, voltageCutoffAt,
                netWorkMinutes, blockingDisconnectors, earthingPoints, parkingPlace, executionPackageId, trackIds, startKp, endKp, personnel,
                measurementEquipment, status, observations, audit, version, null, null, List.of(), List.of());
    }
}

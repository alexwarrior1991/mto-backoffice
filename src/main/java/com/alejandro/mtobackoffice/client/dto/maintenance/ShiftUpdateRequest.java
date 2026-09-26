package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Modificacion parcial de un turno planificado o en curso ({@code null} es «no tocar»). Vias y
 * seccionadores, si viajan, sustituyen enteros a los que habia; las vias no pueden quedar vacias.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShiftUpdateRequest(LocalDate shiftDate, UUID teamId, String baseName, String vehicle, PossessionType possessionType,
                                 Instant plannedStart, Instant plannedEnd, Set<UUID> blockingDisconnectorIds, String earthingPoints,
                                 String parkingPlace, Long executionPackageId, Set<Long> trackIds, BigDecimal startKp, BigDecimal endKp,
                                 String personnel, String measurementEquipment, String observations) {

    /** Nada que mandar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return equals(new ShiftUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
    }
}

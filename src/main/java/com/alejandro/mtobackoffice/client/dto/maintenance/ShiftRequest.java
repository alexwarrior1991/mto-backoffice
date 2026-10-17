package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Alta de un turno: fecha, posesion y al menos una via son obligatorias. Sin base ni vehiculo, los del
 * equipo; con un equipo de mto-resources, lo que no se diga de la dotacion y los vehiculos lo copia el
 * servicio del equipo (jefe, supervisor, linemen y vehiculo por defecto).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ShiftRequest(LocalDate shiftDate, UUID teamId, String baseName, String vehicle, PossessionType possessionType,
                           Instant plannedStart, Instant plannedEnd, Set<UUID> blockingDisconnectorIds, String earthingPoints,
                           String parkingPlace, Long executionPackageId, Set<Long> trackIds, BigDecimal startKp, BigDecimal endKp,
                           String personnel, String measurementEquipment, String observations,
                           Set<UUID> vehicleIds, UUID teamLeaderPersonId, UUID supervisorPersonId, Set<UUID> linemanPersonIds) {

    /** Sin la dotacion ni los vehiculos de mto-resources, como antes. */
    public ShiftRequest(LocalDate shiftDate, UUID teamId, String baseName, String vehicle, PossessionType possessionType,
                        Instant plannedStart, Instant plannedEnd, Set<UUID> blockingDisconnectorIds, String earthingPoints,
                        String parkingPlace, Long executionPackageId, Set<Long> trackIds, BigDecimal startKp, BigDecimal endKp,
                        String personnel, String measurementEquipment, String observations) {
        this(shiftDate, teamId, baseName, vehicle, possessionType, plannedStart, plannedEnd, blockingDisconnectorIds, earthingPoints,
                parkingPlace, executionPackageId, trackIds, startKp, endKp, personnel, measurementEquipment, observations, null, null, null, null);
    }
}

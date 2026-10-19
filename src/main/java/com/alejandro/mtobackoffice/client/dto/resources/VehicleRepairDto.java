package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Una reparacion de un vehiculo en el taller. Una que inmoviliza manda en el estado del vehiculo: abierta,
 * lo pone «En reparacion»; al cerrar o cancelar la ultima abierta vuelve a «Operativo» si seguia en
 * reparacion. Planificada o abierta, el vehiculo no entra en los turnos de esas fechas.
 */
public record VehicleRepairDto(UUID id, String code, UUID vehicleId, RepairKind kind, RepairStatus status, Boolean immobilises,
                               String description, String workshop, LocalDate plannedFrom, LocalDate plannedTo, Instant openedAt,
                               Instant closedAt, Integer odometerKm, BigDecimal engineHours, BigDecimal cost, String notes, Long version) {

    public boolean isImmobilising() {
        return Boolean.TRUE.equals(immobilises);
    }

    /** El estado, con uno que no llega como desconocido. */
    public RepairStatus state() {
        return status == null ? RepairStatus.UNKNOWN : status;
    }
}

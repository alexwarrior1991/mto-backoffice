package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Lo que se cambia de una reparacion, para un {@code MergePatch}: el estado no, que tiene sus
 * transiciones. Se vacian el taller, el fin previsto, los contadores, el coste y las notas; las fechas
 * previstas cambian mientras esta planificada o abierta, y si inmoviliza, solo planificada (409
 * {@code REP-001} si no).
 */
public record VehicleRepairUpdateRequest(RepairKind kind, Boolean immobilises, String description, String workshop, LocalDate plannedFrom,
                                         LocalDate plannedTo, Integer odometerKm, BigDecimal engineHours, BigDecimal cost, String notes) {
}

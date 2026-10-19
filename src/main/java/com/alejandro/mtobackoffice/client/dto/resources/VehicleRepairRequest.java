package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

/** El alta de una reparacion: nace {@code PLANNED} (con {@code plannedFrom}) u {@code OPEN}; lo vacio no viaja. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleRepairRequest(RepairKind kind, RepairStatus status, Boolean immobilises, String description, String workshop,
                                   LocalDate plannedFrom, LocalDate plannedTo, Integer odometerKm, BigDecimal engineHours, BigDecimal cost,
                                   String notes) {
}

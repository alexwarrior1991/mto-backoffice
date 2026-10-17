package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Un vehiculo de la flota de mto-resources, con su modelo y donde esta ahora ({@code position}, nula si
 * nunca se registro). {@code available} es activo y operativo o limitado.
 */
public record VehicleDto(UUID id, String code, String name, VehicleTypeSummaryDto type, String registrationPlate, String railVehicleNumber,
                         String serialNumber, Integer manufactureYear, VehicleOwner owner, String ownerCompany,
                         VehicleOperationalStatus operationalStatus, String statusReason, Boolean available, Integer odometerKm,
                         BigDecimal engineHours, String notes, VehicleLocationDto position, Boolean active, Long version) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public VehicleMobility mobility() {
        return type == null ? null : type.mobility();
    }

    public VehicleSummaryDto summary() {
        return new VehicleSummaryDto(id, code, name, mobility(), type == null ? null : type.category());
    }
}

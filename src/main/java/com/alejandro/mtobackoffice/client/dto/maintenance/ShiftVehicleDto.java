package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleSummaryDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Un vehiculo de mto-resources en un turno, con lo que mantenimiento guarda de el y donde quedo
 * aparcado al cerrarlo ({@code null} hasta entonces, o si se cerro sin decirlo): un sitio de
 * mto-resources, una via de mto-configuration y su kp, o un texto.
 */
public record ShiftVehicleDto(UUID vehicleId, String code, String name, VehicleMobility mobility, VehicleCategory category, Boolean available,
                              Parking parking) {

    public record Parking(UUID siteId, Long trackId, BigDecimal kp, String description) {
    }

    /** El vehiculo como lo eligen los desplegables, que lo reconocen por su id. */
    public VehicleSummaryDto summary() {
        return new VehicleSummaryDto(vehicleId, code, name, mobility, category);
    }

    public String label() {
        return summary().label();
    }
}

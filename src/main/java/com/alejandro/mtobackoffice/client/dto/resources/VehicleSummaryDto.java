package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Objects;
import java.util.UUID;

/**
 * Un vehiculo dentro de otra respuesta: el vehiculo por defecto de un equipo, el portador de otro, una
 * fila del tablero. Es el mismo si es el mismo id, como {@link PersonSummaryDto}.
 */
public record VehicleSummaryDto(UUID id, String code, String name, VehicleMobility mobility, VehicleCategory category) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof VehicleSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.UUID;

/** Un vehiculo dentro de otra respuesta: el vehiculo por defecto de un equipo, el portador de otro. */
public record VehicleSummaryDto(UUID id, String code, String name) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }
}

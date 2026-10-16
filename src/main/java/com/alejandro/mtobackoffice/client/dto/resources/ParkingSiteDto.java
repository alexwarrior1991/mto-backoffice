package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Un sitio del catalogo de aparcamientos de mto-resources: donde ya se sabe que se puede dejar un
 * vehiculo. EP, estacion y via son ids de mto-configuration. No tiene historial: es un catalogo.
 */
public record ParkingSiteDto(UUID id, String code, String name, ParkingSiteKind kind, Long executionPackageId, Long stationId, Long trackId,
                             BigDecimal kp, Boolean railAccess, Boolean roadAccess, String address, BigDecimal latitude, BigDecimal longitude,
                             String notes, Boolean active, Long version) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}

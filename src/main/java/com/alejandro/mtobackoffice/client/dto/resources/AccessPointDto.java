package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Un punto de acceso a la via como lo tiene mto-resources ({@code GET /track-access-points}), la copia
 * del maestro de mto-configuration que llega por evento: lo que ofrece «Registrar ubicacion». Solo las
 * claves que se usan; el id es el de mto-configuration y el kp va en metros. Igual por id, para que un
 * desplegable reconozca lo elegido.
 */
public record AccessPointDto(Long id, String name, Long trackId, BigDecimal kp, AccessPointKind kind, Boolean roadRailAccess,
                             BigDecimal maxAxleLoadT, BigDecimal maxGrossWeightT, Boolean enabled) {

    /**
     * «Acceso km 110+176 · Acceso preparado · kp 110176.000», y «· sin biviales» si no sirve para
     * encarrilarlos (si eso le impide al vehiculo usarlo lo dice el servicio).
     */
    public String label() {
        StringBuilder label = new StringBuilder(name == null || name.isBlank() ? "#" + id : name);
        if (kind != null) {
            label.append(" · ").append(kind.label());
        }
        if (kp != null) {
            label.append(" · kp ").append(kp.toPlainString());
        }
        if (Boolean.FALSE.equals(roadRailAccess)) {
            label.append(" · sin biviales");
        }
        return label.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AccessPointDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

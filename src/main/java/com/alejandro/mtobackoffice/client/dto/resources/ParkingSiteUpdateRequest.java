package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;

/**
 * Lo que se cambia de un sitio, para un {@code MergePatch}: todo menos el codigo. El servicio deja
 * vaciar el EP, la estacion, la via, el kp, la direccion, las coordenadas y las notas;
 * {@code active=false} lo retira y pide {@code resources-delete}.
 */
public record ParkingSiteUpdateRequest(String name, ParkingSiteKind kind, Long executionPackageId, Long stationId, Long trackId, BigDecimal kp,
                                       Boolean railAccess, Boolean roadAccess, String address, BigDecimal latitude, BigDecimal longitude,
                                       String notes, Boolean active) {

    public static ParkingSiteUpdateRequest active(boolean active) {
        return new ParkingSiteUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, active);
    }
}

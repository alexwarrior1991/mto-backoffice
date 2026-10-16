package com.alejandro.mtobackoffice.client.dto.resources;

/**
 * Una fila del tablero de la flota: el vehiculo, como esta y donde. Uno que va con otro trae en
 * {@code effective} la posicion de su portador, que es donde esta de verdad.
 */
public record FleetPositionDto(VehicleSummaryDto vehicle, VehicleOperationalStatus operationalStatus, Boolean available,
                               VehicleLocationDto position, VehicleLocationDto effective) {

    /** Donde esta de verdad: la del portador si va con otro, si no la suya. */
    public VehicleLocationDto where() {
        return effective != null ? effective : position;
    }
}

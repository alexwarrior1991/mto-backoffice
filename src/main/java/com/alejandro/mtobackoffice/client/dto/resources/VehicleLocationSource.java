package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** De donde salio una ubicacion: a mano, de un turno de mto-maintenance, del dispositivo de campo o del GPS del vehiculo. */
public enum VehicleLocationSource {
    MANUAL("A mano"),
    SHIFT("Turno"),
    FIELD("Campo"),
    TELEMATICS("GPS"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehicleLocationSource(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleLocationSource of(String value) {
        return ClientEnums.parse(VehicleLocationSource.class, value, UNKNOWN);
    }

    public static List<VehicleLocationSource> selectable() {
        return ClientEnums.selectable(VehicleLocationSource.class, UNKNOWN);
    }
}

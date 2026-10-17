package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** De quien es un vehiculo; uno que no es propio dice de que empresa. */
public enum VehicleOwner {
    OWN("Propio"),
    LEASED("Alquilado"),
    SUBCONTRACTOR("Subcontrata"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehicleOwner(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleOwner of(String value) {
        return ClientEnums.parse(VehicleOwner.class, value, UNKNOWN);
    }

    public static List<VehicleOwner> selectable() {
        return ClientEnums.selectable(VehicleOwner.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Lo que mueve un vehiculo. */
public enum VehiclePower {
    DIESEL("Diesel"),
    ELECTRIC("Electrico"),
    BATTERY("Bateria"),
    HYBRID("Hibrido"),
    MANUAL("Manual"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehiclePower(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehiclePower of(String value) {
        return ClientEnums.parse(VehiclePower.class, value, UNKNOWN);
    }

    public static List<VehiclePower> selectable() {
        return ClientEnums.selectable(VehiclePower.class, UNKNOWN);
    }
}

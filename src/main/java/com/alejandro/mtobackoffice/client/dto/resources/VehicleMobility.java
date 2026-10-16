package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Por donde se mueve un modelo de vehiculo: es lo que deciden las reglas de la flota (que cifras lleva, donde puede estar). */
public enum VehicleMobility {
    RAIL_ONLY("Solo via"),
    ROAD_RAIL("Bivial"),
    ROAD_ONLY("Solo carretera"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehicleMobility(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleMobility of(String value) {
        return ClientEnums.parse(VehicleMobility.class, value, UNKNOWN);
    }

    public static List<VehicleMobility> selectable() {
        return ClientEnums.selectable(VehicleMobility.class, UNKNOWN);
    }

    /** Si va por la via: uno de solo via o una bivial. */
    public boolean runsOnTrack() {
        return this == RAIL_ONLY || this == ROAD_RAIL;
    }

    /** Si va por carretera: uno de solo carretera o una bivial. */
    public boolean runsOnRoad() {
        return this == ROAD_ONLY || this == ROAD_RAIL;
    }
}

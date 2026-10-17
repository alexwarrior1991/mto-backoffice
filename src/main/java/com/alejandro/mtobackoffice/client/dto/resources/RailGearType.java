package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** El equipo ferroviario de una bivial (EN 15746): de quien es la traccion y el freno en el carril. Solo lo lleva una bivial, y siempre. */
public enum RailGearType {
    RAIL_WHEEL_DRIVE("9A · traccion en la rueda ferroviaria"),
    FRICTION_DRIVE("9B · traccion por friccion del neumatico"),
    ROAD_WHEEL_DRIVE("9C · neumatico sobre el carril"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    RailGearType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RailGearType of(String value) {
        return ClientEnums.parse(RailGearType.class, value, UNKNOWN);
    }

    public static List<RailGearType> selectable() {
        return ClientEnums.selectable(RailGearType.class, UNKNOWN);
    }
}

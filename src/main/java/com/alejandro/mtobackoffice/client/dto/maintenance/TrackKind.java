package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Si un tramo de via esta en la via principal o en una desviada; una desviada solo admite turnos con posesion total. */
public enum TrackKind {
    MAIN("Principal"),
    DIVERTED("Desviada"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    TrackKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TrackKind of(String value) {
        return MaintenanceEnums.parse(TrackKind.class, value, UNKNOWN);
    }

    public static List<TrackKind> selectable() {
        return MaintenanceEnums.selectable(TrackKind.class, UNKNOWN);
    }
}

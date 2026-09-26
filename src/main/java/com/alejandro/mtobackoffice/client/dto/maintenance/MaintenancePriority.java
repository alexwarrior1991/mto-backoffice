package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Prioridad de una orden. */
public enum MaintenancePriority {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Critica"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    MaintenancePriority(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MaintenancePriority of(String value) {
        return MaintenanceEnums.parse(MaintenancePriority.class, value, UNKNOWN);
    }

    public static List<MaintenancePriority> selectable() {
        return MaintenanceEnums.selectable(MaintenancePriority.class, UNKNOWN);
    }
}

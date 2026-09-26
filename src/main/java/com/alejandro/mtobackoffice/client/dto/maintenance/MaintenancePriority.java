package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Prioridad de una orden. */
public enum MaintenancePriority {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Critica"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    MaintenancePriority(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MaintenancePriority of(String value) {
        return ClientEnums.parse(MaintenancePriority.class, value, UNKNOWN);
    }

    public static List<MaintenancePriority> selectable() {
        return ClientEnums.selectable(MaintenancePriority.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Gravedad de un defecto de catenaria. */
public enum DefectSeverity {
    LOW("Leve"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Critica"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    DefectSeverity(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DefectSeverity of(String value) {
        return MaintenanceEnums.parse(DefectSeverity.class, value, UNKNOWN);
    }

    public static List<DefectSeverity> selectable() {
        return MaintenanceEnums.selectable(DefectSeverity.class, UNKNOWN);
    }
}

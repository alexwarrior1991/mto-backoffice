package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Gravedad de un defecto de catenaria. */
public enum DefectSeverity {
    LOW("Leve"),
    MEDIUM("Media"),
    HIGH("Alta"),
    CRITICAL("Critica"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    DefectSeverity(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DefectSeverity of(String value) {
        return ClientEnums.parse(DefectSeverity.class, value, UNKNOWN);
    }

    public static List<DefectSeverity> selectable() {
        return ClientEnums.selectable(DefectSeverity.class, UNKNOWN);
    }
}

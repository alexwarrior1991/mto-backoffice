package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Resultado de un punto de checklist. */
public enum CheckItemResult {
    OK("Correcto"),
    DEFECT("Defecto"),
    NOT_APPLICABLE("No aplica"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    CheckItemResult(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CheckItemResult of(String value) {
        return MaintenanceEnums.parse(CheckItemResult.class, value, UNKNOWN);
    }

    public static List<CheckItemResult> selectable() {
        return MaintenanceEnums.selectable(CheckItemResult.class, UNKNOWN);
    }
}

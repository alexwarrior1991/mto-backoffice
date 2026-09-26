package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Tipo de inspeccion. */
public enum InspectionKind {
    VISUAL("Visual"),
    TECHNICAL("Tecnica"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    InspectionKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InspectionKind of(String value) {
        return ClientEnums.parse(InspectionKind.class, value, UNKNOWN);
    }

    public static List<InspectionKind> selectable() {
        return ClientEnums.selectable(InspectionKind.class, UNKNOWN);
    }
}

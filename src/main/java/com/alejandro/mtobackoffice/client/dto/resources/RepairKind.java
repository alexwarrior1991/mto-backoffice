package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que se le hace a un vehiculo en el taller. */
public enum RepairKind {
    BREAKDOWN("Averia"),
    SERVICE("Revision"),
    INSPECTION("Inspeccion"),
    MODIFICATION("Modificacion"),
    OTHER("Otra"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    RepairKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RepairKind of(String value) {
        return ClientEnums.parse(RepairKind.class, value, UNKNOWN);
    }

    public static List<RepairKind> selectable() {
        return ClientEnums.selectable(RepairKind.class, UNKNOWN);
    }
}

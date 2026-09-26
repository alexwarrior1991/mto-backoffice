package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Grupo funcional de un tipo de tarea del plan de mantenimiento de catenaria. */
public enum FunctionalGroup {
    STRUCTURAL_SUPPORTS("Soportes estructurales"),
    OVERHEAD_CONDUCTORS("Conductores aereos"),
    DEVICES_AND_SWITCHES("Aparatos y seccionadores"),
    ANCHORAGE_COMPONENTS("Anclajes"),
    TURNOUTS_AND_SWITCHES("Desvios y agujas"),
    DIAGNOSTICS("Diagnostico"),
    NONE("Sin grupo"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    FunctionalGroup(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FunctionalGroup of(String value) {
        return ClientEnums.parse(FunctionalGroup.class, value, UNKNOWN);
    }

    public static List<FunctionalGroup> selectable() {
        return ClientEnums.selectable(FunctionalGroup.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** La disciplina de una unidad: un proyecto no tiene, un departamento la dice y lo de debajo la hereda. */
public enum WorkDiscipline {
    MAINTENANCE("Mantenimiento"),
    CONSTRUCTION("Construccion"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    WorkDiscipline(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WorkDiscipline of(String value) {
        return ClientEnums.parse(WorkDiscipline.class, value, UNKNOWN);
    }

    public static List<WorkDiscipline> selectable() {
        return ClientEnums.selectable(WorkDiscipline.class, UNKNOWN);
    }
}

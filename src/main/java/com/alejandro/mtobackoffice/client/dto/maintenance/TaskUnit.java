package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** La unidad en que se miden los minutos estandar de un tipo de tarea. */
public enum TaskUnit {
    UNIT("Unidad"),
    SPAN("Vano"),
    KM("Kilometro"),
    DEFECT("Defecto"),
    PROFILE("Perfil"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    TaskUnit(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TaskUnit of(String value) {
        return MaintenanceEnums.parse(TaskUnit.class, value, UNKNOWN);
    }

    public static List<TaskUnit> selectable() {
        return MaintenanceEnums.selectable(TaskUnit.class, UNKNOWN);
    }
}

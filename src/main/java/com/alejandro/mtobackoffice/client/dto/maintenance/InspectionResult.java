package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Resultado de una inspeccion. Uno correcto no genera ni defecto ni orden; un defecto leve solo
 * genera defecto si se fuerza ({@code force}); uno inseguro genera una orden urgente.
 */
public enum InspectionResult {
    OK("Correcta"),
    MINOR_DEFECT("Defecto leve"),
    MAJOR_DEFECT("Defecto grave"),
    UNSAFE("Insegura"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    InspectionResult(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Hay algo que corregir: se ofrecen crear el defecto y la orden correctiva. */
    public boolean foundSomething() {
        return this == MINOR_DEFECT || this == MAJOR_DEFECT || this == UNSAFE;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InspectionResult of(String value) {
        return MaintenanceEnums.parse(InspectionResult.class, value, UNKNOWN);
    }

    public static List<InspectionResult> selectable() {
        return MaintenanceEnums.selectable(InspectionResult.class, UNKNOWN);
    }
}

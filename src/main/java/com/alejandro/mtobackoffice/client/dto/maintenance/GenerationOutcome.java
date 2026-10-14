package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Lo que pasa con cada activo al generar las tareas de un preventivo (la previsualizacion). */
public enum GenerationOutcome {
    CREATE("Se crea"),
    ALREADY_IN_ORDER("Ya en la orden"),
    OTHER_PACKAGE("De otro EP: va en otra orden"),
    IN_OTHER_ORDER("En otra orden abierta"),
    NO_TASK_TYPES("Sin tipo de tarea: elige tipos"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    GenerationOutcome(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GenerationOutcome of(String value) {
        return ClientEnums.parse(GenerationOutcome.class, value, UNKNOWN);
    }

    public static List<GenerationOutcome> selectable() {
        return ClientEnums.selectable(GenerationOutcome.class, UNKNOWN);
    }
}

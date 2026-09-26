package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Estado de un turno: {@code PLANNED → IN_PROGRESS → CLOSED}, o {@code CANCELLED} mientras no este
 * cerrado. Lo que se ofrece en cada estado esta copiado de {@code ShiftStateMachine} del servicio,
 * solo para no ofrecer lo que va a fallar.
 */
public enum ShiftStatus {
    PLANNED("Planificado"),
    IN_PROGRESS("En curso"),
    CLOSED("Cerrado"),
    CANCELLED("Cancelado"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ShiftStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Planificado o en curso: se modifica, admite tareas y se cancela. */
    public boolean isOpen() {
        return this == PLANNED || this == IN_PROGRESS;
    }

    public boolean canStart() {
        return this == PLANNED;
    }

    public boolean canClose() {
        return this == IN_PROGRESS;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ShiftStatus of(String value) {
        return ClientEnums.parse(ShiftStatus.class, value, UNKNOWN);
    }

    public static List<ShiftStatus> selectable() {
        return ClientEnums.selectable(ShiftStatus.class, UNKNOWN);
    }
}

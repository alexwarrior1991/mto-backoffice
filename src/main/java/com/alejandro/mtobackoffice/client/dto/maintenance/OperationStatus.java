package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * El estado de una operacion de fondo de mto-maintenance (el 202 de un {@code Prefer: respond-async}).
 * Tolerante: uno nuevo se lee como {@code UNKNOWN}, que se da por terminado para no preguntar sin fin,
 * como un trabajo de estado desconocido.
 */
public enum OperationStatus {
    QUEUED("En cola"),
    RUNNING("En marcha"),
    SUCCEEDED("Terminada"),
    FAILED("Fallida"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    OperationStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Ya no cambia: se deja de preguntar. */
    public boolean isFinished() {
        return this != QUEUED && this != RUNNING;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OperationStatus of(String value) {
        return ClientEnums.parse(OperationStatus.class, value, UNKNOWN);
    }
}

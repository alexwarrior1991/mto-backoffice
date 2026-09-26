package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Estado de un defecto: {@code OPEN → IN_PROGRESS} (vinculado a una orden) {@code → RESOLVED → CLOSED},
 * o {@code DISCARDED} mientras este abierto. Lo que se ofrece esta copiado de {@code DefectStateMachine}
 * del servicio, solo para no ofrecer lo que va a fallar.
 */
public enum DefectStatus {
    OPEN("Abierto"),
    IN_PROGRESS("En curso"),
    RESOLVED("Resuelto"),
    CLOSED("Cerrado"),
    DISCARDED("Descartado"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    DefectStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Abierto o en curso: se vincula a una orden y se resuelve. */
    public boolean isPending() {
        return this == OPEN || this == IN_PROGRESS;
    }

    /** Ni cerrado ni descartado: se modifica. */
    public boolean isEditable() {
        return this == OPEN || this == IN_PROGRESS || this == RESOLVED;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DefectStatus of(String value) {
        return ClientEnums.parse(DefectStatus.class, value, UNKNOWN);
    }

    public static List<DefectStatus> selectable() {
        return ClientEnums.selectable(DefectStatus.class, UNKNOWN);
    }
}

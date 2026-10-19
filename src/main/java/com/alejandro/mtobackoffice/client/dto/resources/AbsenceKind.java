package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Por que no esta una persona. Solo se ve aqui, en su ficha: los eventos de mto-resources llevan el
 * periodo, nunca la clase.
 */
public enum AbsenceKind {
    VACATION("Vacaciones"),
    SICK_LEAVE("Baja"),
    TRAINING("Formacion"),
    PERSONAL("Asuntos propios"),
    OTHER("Otra"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    AbsenceKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AbsenceKind of(String value) {
        return ClientEnums.parse(AbsenceKind.class, value, UNKNOWN);
    }

    public static List<AbsenceKind> selectable() {
        return ClientEnums.selectable(AbsenceKind.class, UNKNOWN);
    }
}

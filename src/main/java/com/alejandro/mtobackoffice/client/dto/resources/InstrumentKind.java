package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que es un instrumento de mto-resources (los que se llevan a un turno y se calibran). */
public enum InstrumentKind {
    EARTHING_POLE("Pertiga de puesta a tierra"),
    VOLTAGE_DETECTOR("Detector de tension"),
    MEASURING("Equipo de medida"),
    OTHER("Otro"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    InstrumentKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InstrumentKind of(String value) {
        return ClientEnums.parse(InstrumentKind.class, value, UNKNOWN);
    }

    public static List<InstrumentKind> selectable() {
        return ClientEnums.selectable(InstrumentKind.class, UNKNOWN);
    }
}

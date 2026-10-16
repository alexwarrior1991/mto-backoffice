package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que es un sitio de aparcamiento del catalogo. */
public enum ParkingSiteKind {
    SIDING("Apartadero"),
    STATION_TRACK("Via de estacion"),
    DEPOT("Cochera"),
    WORKSHOP("Taller"),
    ROAD_PARKING("Aparcamiento de carretera"),
    ACCESS_AREA("Zona de acceso"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ParkingSiteKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ParkingSiteKind of(String value) {
        return ClientEnums.parse(ParkingSiteKind.class, value, UNKNOWN);
    }

    public static List<ParkingSiteKind> selectable() {
        return ClientEnums.selectable(ParkingSiteKind.class, UNKNOWN);
    }
}

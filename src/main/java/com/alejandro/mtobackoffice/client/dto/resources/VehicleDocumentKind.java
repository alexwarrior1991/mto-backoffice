package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que documento de un vehiculo caduca: un vehiculo tiene como mucho uno de cada tipo. */
public enum VehicleDocumentKind {
    ROAD_INSPECTION("Inspeccion de carretera (ITV)"),
    RAIL_INSPECTION("Inspeccion ferroviaria"),
    INSURANCE("Seguro"),
    CRANE_CERTIFICATE("Certificado de la grua"),
    PLATFORM_CERTIFICATE("Certificado de la plataforma"),
    OTHER("Otro"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehicleDocumentKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleDocumentKind of(String value) {
        return ClientEnums.parse(VehicleDocumentKind.class, value, UNKNOWN);
    }

    public static List<VehicleDocumentKind> selectable() {
        return ClientEnums.selectable(VehicleDocumentKind.class, UNKNOWN);
    }
}

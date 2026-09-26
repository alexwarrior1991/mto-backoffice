package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Tipo de una orden. {@code URGENT} es el unico que salta de borrador a en curso, y nace critica. */
public enum MaintenanceOrderType {
    PREVENTIVE("Preventiva"),
    CORRECTIVE("Correctiva"),
    INSPECTION("Inspeccion"),
    URGENT("Urgente"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    MaintenanceOrderType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MaintenanceOrderType of(String value) {
        return ClientEnums.parse(MaintenanceOrderType.class, value, UNKNOWN);
    }

    public static List<MaintenanceOrderType> selectable() {
        return ClientEnums.selectable(MaintenanceOrderType.class, UNKNOWN);
    }
}

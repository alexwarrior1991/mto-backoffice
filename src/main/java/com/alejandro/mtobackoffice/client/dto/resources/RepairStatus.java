package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * El estado de una reparacion: {@code PLANNED -> OPEN -> CLOSED}, o {@code CANCELLED} desde las dos
 * primeras. Copiado de {@code RepairRules} de mto-resources solo para no ofrecer lo que va a fallar: lo
 * decide el servicio (409 {@code REP-001}). Un estado desconocido no admite nada.
 */
public enum RepairStatus {
    PLANNED("Planificada"),
    OPEN("Abierta"),
    CLOSED("Cerrada"),
    CANCELLED("Cancelada"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    RepairStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public boolean canOpen() {
        return this == PLANNED;
    }

    public boolean canClose() {
        return this == OPEN;
    }

    public boolean canCancel() {
        return this == PLANNED || this == OPEN;
    }

    /** Si se puede modificar: cualquiera que se conozca (cerrada o cancelada, su coste y sus notas). */
    public boolean isChangeable() {
        return this != UNKNOWN;
    }

    /** Si sus fechas previstas aun cambian: planificada o abierta. */
    public boolean datesChange() {
        return this == PLANNED || this == OPEN;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RepairStatus of(String value) {
        return ClientEnums.parse(RepairStatus.class, value, UNKNOWN);
    }

    public static List<RepairStatus> selectable() {
        return ClientEnums.selectable(RepairStatus.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Como se acciona un seccionador, leido de mantenimiento (tolerante; el del maestro de mto-configuration
 * es {@code master.DisconnectorDriveType}): decide si RP-06 le va por defecto al generar un preventivo.
 */
public enum DisconnectorDrive {
    MOTOR("Con motor"),
    MANUAL("Manual"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    DisconnectorDrive(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DisconnectorDrive of(String value) {
        return ClientEnums.parse(DisconnectorDrive.class, value, UNKNOWN);
    }

    public static List<DisconnectorDrive> selectable() {
        return ClientEnums.selectable(DisconnectorDrive.class, UNKNOWN);
    }
}

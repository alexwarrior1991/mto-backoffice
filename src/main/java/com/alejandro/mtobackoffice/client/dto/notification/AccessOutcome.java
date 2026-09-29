package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Como acabo un acceso: {@code FAILURE} son los logins fallidos, las rachas, los bloqueos y los
 * logouts fallidos; {@code SUCCESS}, el resto. Viaja como filtro y vuelve en cada linea.
 */
public enum AccessOutcome {
    SUCCESS("Correcto"),
    FAILURE("Fallido"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    AccessOutcome(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccessOutcome of(String value) {
        return ClientEnums.parse(AccessOutcome.class, value, UNKNOWN);
    }

    public static List<AccessOutcome> selectable() {
        return ClientEnums.selectable(AccessOutcome.class, UNKNOWN);
    }
}

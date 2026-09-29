package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Gravedad de una linea del registro o de una notificacion. */
public enum ActivitySeverity {
    INFO("Informacion"),
    WARNING("Aviso"),
    CRITICAL("Critica"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ActivitySeverity(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ActivitySeverity of(String value) {
        return ClientEnums.parse(ActivitySeverity.class, value, UNKNOWN);
    }

    public static List<ActivitySeverity> selectable() {
        return ClientEnums.selectable(ActivitySeverity.class, UNKNOWN);
    }
}

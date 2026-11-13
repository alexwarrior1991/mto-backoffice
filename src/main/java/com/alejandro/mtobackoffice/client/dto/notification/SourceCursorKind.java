package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que lee una marca del lector de Keycloak: los accesos o los eventos de administracion. */
public enum SourceCursorKind {
    KEYCLOAK_LOGIN("Accesos de Keycloak"),
    KEYCLOAK_ADMIN("Administracion de Keycloak"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    SourceCursorKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SourceCursorKind of(String value) {
        return ClientEnums.parse(SourceCursorKind.class, value, UNKNOWN);
    }

    public static List<SourceCursorKind> selectable() {
        return ClientEnums.selectable(SourceCursorKind.class, UNKNOWN);
    }
}

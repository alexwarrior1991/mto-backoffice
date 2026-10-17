package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * La clase de un punto de acceso a la via tal como la cuenta mto-resources, que la copia del maestro
 * de mto-configuration. Se lee de un servicio y nunca vuelve a el, asi que tolera lo desconocido
 * ({@code UNKNOWN}, «Desconocido»), al contrario que el {@code TrackAccessPointKind} del maestro, que
 * se devuelve entero.
 */
public enum AccessPointKind {
    PREPARED_ACCESS("Acceso preparado"),
    LEVEL_CROSSING("Paso a nivel"),
    DEPOT("Base o cochera"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    AccessPointKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccessPointKind of(String value) {
        return ClientEnums.parse(AccessPointKind.class, value, UNKNOWN);
    }

    public static List<AccessPointKind> selectable() {
        return ClientEnums.selectable(AccessPointKind.class, UNKNOWN);
    }
}

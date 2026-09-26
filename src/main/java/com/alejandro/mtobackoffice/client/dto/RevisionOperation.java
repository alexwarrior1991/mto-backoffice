package com.alejandro.mtobackoffice.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que hizo una revision del historial (Envers en mto-stock y en mto-maintenance). */
public enum RevisionOperation {
    CREATED("Alta"),
    UPDATED("Modificacion"),
    DELETED("Baja"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    RevisionOperation(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RevisionOperation of(String value) {
        return ClientEnums.parse(RevisionOperation.class, value, UNKNOWN);
    }

    public static List<RevisionOperation> selectable() {
        return ClientEnums.selectable(RevisionOperation.class, UNKNOWN);
    }
}

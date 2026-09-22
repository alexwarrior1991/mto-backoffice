package com.alejandro.mtobackoffice.client.dto.stock;

public enum RevisionOperation {
    CREATED("Alta"),
    UPDATED("Modificacion"),
    DELETED("Baja");

    private final String label;

    RevisionOperation(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

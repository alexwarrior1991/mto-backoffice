package com.alejandro.mtobackoffice.client.dto.stock;

/** Solo una reserva activa reduce el disponible y solo una activa se puede cambiar. */
public enum ReservationStatus {
    ACTIVE("Activa"),
    RELEASED("Liberada"),
    CONSUMED("Consumida"),
    CANCELLED("Cancelada");

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

package com.alejandro.mtobackoffice.client.dto.stock;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Solo una reserva activa reduce el disponible y solo una activa se puede cambiar. Un estado que
 * mto-stock estrene se lee como {@code UNKNOWN}, que no es activa: su fila no ofrece acciones.
 */
public enum ReservationStatus {
    ACTIVE("Activa"),
    RELEASED("Liberada"),
    CONSUMED("Consumida"),
    CANCELLED("Cancelada"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ReservationStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReservationStatus of(String value) {
        return ClientEnums.parse(ReservationStatus.class, value, UNKNOWN);
    }

    public static List<ReservationStatus> selectable() {
        return ClientEnums.selectable(ReservationStatus.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Como va una linea de material con mto-stock: sin pedir (borrador), reservada, consumida, liberada,
 * fallida (el almacen no respondio) o rechazada (dijo que no; el motivo viene en
 * {@code stockSyncError}). Las dos ultimas se reintentan con sincronizar.
 */
public enum StockSyncStatus {
    NOT_REQUESTED("Sin pedir"),
    RESERVED("Reservada"),
    CONSUMED("Consumida"),
    RELEASED("Liberada"),
    FAILED("Fallida"),
    REJECTED("Rechazada"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    StockSyncStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Lo que stock no llego a hacer, porque no respondio o porque dijo que no: bloquea completar la orden salvo con {@code force}. */
    public boolean isSyncFailed() {
        return this == FAILED || this == REJECTED;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static StockSyncStatus of(String value) {
        return ClientEnums.parse(StockSyncStatus.class, value, UNKNOWN);
    }

    public static List<StockSyncStatus> selectable() {
        return ClientEnums.selectable(StockSyncStatus.class, UNKNOWN);
    }
}

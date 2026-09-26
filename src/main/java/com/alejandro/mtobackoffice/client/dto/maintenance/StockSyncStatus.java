package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Como va una linea de material con mto-stock: sin pedir (borrador), reservada, consumida, liberada o fallida (se reintenta con sincronizar). */
public enum StockSyncStatus {
    NOT_REQUESTED("Sin pedir"),
    RESERVED("Reservada"),
    CONSUMED("Consumida"),
    RELEASED("Liberada"),
    FAILED("Fallida"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    StockSyncStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static StockSyncStatus of(String value) {
        return ClientEnums.parse(StockSyncStatus.class, value, UNKNOWN);
    }

    public static List<StockSyncStatus> selectable() {
        return ClientEnums.selectable(StockSyncStatus.class, UNKNOWN);
    }
}

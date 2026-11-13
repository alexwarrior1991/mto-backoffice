package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Por que termino una pasada de reproceso de una DLQ. */
public enum ReprocessStop {
    EMPTY("la cola se quedo sin mensajes"),
    MAX("se alcanzo el maximo pedido"),
    TIME_BUDGET("se agoto el tiempo de la pasada (10 s)"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ReprocessStop(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReprocessStop of(String value) {
        return ClientEnums.parse(ReprocessStop.class, value, UNKNOWN);
    }

    public static List<ReprocessStop> selectable() {
        return ClientEnums.selectable(ReprocessStop.class, UNKNOWN);
    }
}

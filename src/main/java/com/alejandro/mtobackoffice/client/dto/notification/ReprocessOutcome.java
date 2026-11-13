package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que fue de un mensaje de una DLQ al reprocesarlo: aplicado o ya aplicado sale de la cola; fallido se queda. */
public enum ReprocessOutcome {
    PROCESSED("Procesado"),
    DUPLICATE("Duplicado"),
    FAILED("Fallido"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ReprocessOutcome(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReprocessOutcome of(String value) {
        return ClientEnums.parse(ReprocessOutcome.class, value, UNKNOWN);
    }

    public static List<ReprocessOutcome> selectable() {
        return ClientEnums.selectable(ReprocessOutcome.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** A quien va una entrega: a una audiencia entera, que el despachador expande, o a un destinatario ya resuelto. */
public enum DeliveryScope {
    AUDIENCE("Audiencia"),
    RECIPIENT("Destinatario"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    DeliveryScope(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DeliveryScope of(String value) {
        return ClientEnums.parse(DeliveryScope.class, value, UNKNOWN);
    }

    public static List<DeliveryScope> selectable() {
        return ClientEnums.selectable(DeliveryScope.class, UNKNOWN);
    }
}

package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Como esta un mensaje de una fuente en el inbox de mto-notification. */
public enum InboxMessageStatus {
    RECEIVED("Recibido"),
    PROCESSING("En proceso"),
    PROCESSED("Procesado"),
    FAILED("Fallido"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    InboxMessageStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static InboxMessageStatus of(String value) {
        return ClientEnums.parse(InboxMessageStatus.class, value, UNKNOWN);
    }

    public static List<InboxMessageStatus> selectable() {
        return ClientEnums.selectable(InboxMessageStatus.class, UNKNOWN);
    }
}

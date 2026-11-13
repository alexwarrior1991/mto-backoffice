package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Como va una entrega de mto-notification (el correo de un aviso). Tolerante: un estado nuevo se lee como desconocido y no se reintenta. */
public enum DeliveryStatus {
    PENDING("Pendiente"),
    IN_PROGRESS("En curso"),
    SENT("Enviada"),
    FAILED("Fallida"),
    SKIPPED("Omitida"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    DeliveryStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /**
     * Si el servicio deja reintentarla: solo una fallida (agoto sus intentos) o una omitida (sin
     * direccion, canal apagado, tope por hora). Copiado de {@code AdminServiceImpl.retry} solo para no
     * ofrecer lo que responderia 409 {@code DLV-409}; una desconocida no se reintenta.
     */
    public boolean canRetry() {
        return this == FAILED || this == SKIPPED;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DeliveryStatus of(String value) {
        return ClientEnums.parse(DeliveryStatus.class, value, UNKNOWN);
    }

    public static List<DeliveryStatus> selectable() {
        return ClientEnums.selectable(DeliveryStatus.class, UNKNOWN);
    }
}

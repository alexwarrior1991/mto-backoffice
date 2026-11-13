package com.alejandro.mtobackoffice.client.dto.notification;

/**
 * Los canales por los que puede salir un aviso manual, con el nombre con el que viajan. Solo viaja en
 * peticiones (sin {@code UNKNOWN}): son los que el servicio conoce hoy ({@code DeliveryChannels.KNOWN}
 * alli), y otro seria su 422 {@code NTF-422}.
 */
public enum BroadcastChannel {

    /** La bandeja de la aplicacion: se lee, no se entrega. */
    INBOX("inbox", "Bandeja"),
    /** El correo: deja una entrega por destinatario. */
    EMAIL("email", "Correo");

    private final String wire;
    private final String label;

    BroadcastChannel(String wire, String label) {
        this.wire = wire;
        this.label = label;
    }

    /** Como se llama en el servicio. */
    public String wire() {
        return wire;
    }

    public String label() {
        return label;
    }
}

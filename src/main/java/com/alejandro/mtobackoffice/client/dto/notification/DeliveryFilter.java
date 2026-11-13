package com.alejandro.mtobackoffice.client.dto.notification;

import java.util.UUID;

/**
 * Los filtros de {@code GET /admin/deliveries}; lo que va a {@code null} no viaja. El canal se
 * compara entero en el servicio.
 */
public record DeliveryFilter(DeliveryStatus status, String channel, UUID notificationId) {

    public static final DeliveryFilter NONE = new DeliveryFilter(null, null, null);
}

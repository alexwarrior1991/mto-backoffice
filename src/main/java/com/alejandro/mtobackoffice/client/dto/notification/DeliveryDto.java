package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.UUID;

/**
 * Una entrega por un canal que empuja (el correo), para la administracion. {@code channel} es la
 * cadena del servicio ({@code email}); {@code recipient} es la direccion de una entrega a un
 * destinatario, y {@code audienceKey} la audiencia de una que todavia se expande.
 */
public record DeliveryDto(UUID id, UUID notificationId, String channel, DeliveryScope scope, String audienceKey, String recipient,
                          String recipientUsername, DeliveryStatus status, int attempts, int maxAttempts, Instant nextAttemptAt,
                          Instant sentAt, String lastError, Instant createdAt) {
}

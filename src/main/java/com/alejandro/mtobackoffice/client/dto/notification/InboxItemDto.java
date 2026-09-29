package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.UUID;

/**
 * Una notificacion de mi bandeja, con su estado de lectura para esta persona. {@code link} es una
 * ruta de esta aplicacion ({@code /mantenimiento/ordenes/{id}}, {@code /actividad?category=SYSTEM}...)
 * que ponen las reglas del servicio; {@code activityEventId} es la linea del registro que la causo.
 */
public record InboxItemDto(UUID id, String ruleKey, ActivityCategory category, ActivitySeverity severity, String title, String body,
                           String link, String subjectType, String subjectId, UUID activityEventId, Instant createdAt, boolean read,
                           Instant readAt) {

    public boolean hasLink() {
        return link != null && !link.isBlank();
    }
}

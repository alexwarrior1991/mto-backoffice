package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * La notificacion que crea un correo de prueba o un aviso manual, con las audiencias a las que
 * quedo dirigida.
 */
public record AdminNotificationDto(UUID id, String ruleKey, ActivityCategory category, ActivitySeverity severity, String title,
                                   String body, String link, List<String> audiences, Instant createdAt, String createdBy) {

    public AdminNotificationDto {
        audiences = audiences == null ? List.of() : List.copyOf(audiences);
    }
}

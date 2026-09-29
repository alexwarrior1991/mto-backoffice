package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Un acceso: quien, desde que IP y como acabo. Solo esta categoria lleva la IP. */
public record AccessEventDto(UUID id, Long seq, String type, ActivitySeverity severity, AccessOutcome outcome, Instant occurredAt,
                             Instant recordedAt, String username, String userId, String ipAddress, String correlationId, int eventCount,
                             Map<String, Object> payload) {

    public AccessEventDto {
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }
}

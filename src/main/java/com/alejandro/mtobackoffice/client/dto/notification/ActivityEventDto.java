package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Una linea del registro: que fue ({@code type}, {@code category}), quien, cuando, desde que
 * servicio y sobre que. {@code eventCount} es mayor que uno en una rafaga (una importacion es una
 * linea con su recuento); {@code supersededBy} apunta a la linea que ya cuenta el mismo cambio (el
 * evento de administracion de Keycloak fundido con el de mto-users). {@code payload} es lo que la
 * fuente publico, ya pasado por la lista blanca del servicio: aqui se pinta tal cual.
 */
public record ActivityEventDto(UUID id, Long seq, String sourceService, String sourceEventId, ActivityCategory category, String type,
                               ActivitySeverity severity, Instant occurredAt, Instant recordedAt, ActorDto actor, SubjectDto subject,
                               String correlationId, int eventCount, Map<String, Object> payload, UUID supersededBy) {

    public ActivityEventDto {
        payload = payload == null ? Map.of() : Map.copyOf(payload);
    }
}

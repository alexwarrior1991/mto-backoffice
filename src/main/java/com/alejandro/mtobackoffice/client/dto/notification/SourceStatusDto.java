package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * {@code GET /admin/sources}: las marcas y los arrendamientos del lector de Keycloak, el inbox por
 * fuente y estado, las rafagas abiertas y las entregas por estado. Las claves de {@code deliveries}
 * son los estados tal como los manda el servicio: uno nuevo no rompe la lectura.
 */
public record SourceStatusDto(List<CursorDto> cursors, List<InboxCountDto> inbox, Long openBursts, Map<String, Long> deliveries) {

    public SourceStatusDto {
        cursors = cursors == null ? List.of() : List.copyOf(cursors);
        inbox = inbox == null ? List.of() : List.copyOf(inbox);
        deliveries = deliveries == null ? Map.of() : deliveries;
    }

    /** Hasta donde ha leido el lector de Keycloak una de sus dos fuentes, y quien la tiene arrendada. */
    public record CursorDto(SourceCursorKind kind, Instant lastEventTime, String leaseOwner, Instant leaseUntil, Instant lastPollAt,
                            Instant lastSuccessAt, String lastError) {
    }

    /** Cuantos mensajes de una fuente hay en el inbox en un estado. */
    public record InboxCountDto(String sourceService, InboxMessageStatus status, long total) {
    }
}

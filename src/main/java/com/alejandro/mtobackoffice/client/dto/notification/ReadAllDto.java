package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;

/** Hasta donde quedo leido todo: la mas reciente visible al marcar, no «ahora». */
public record ReadAllDto(Instant allReadUntil) {
}

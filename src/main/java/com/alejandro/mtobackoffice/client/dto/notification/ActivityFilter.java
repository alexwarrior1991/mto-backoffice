package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;

/**
 * Los filtros del registro; lo que va a {@code null} no viaja, y {@code includeSuperseded} solo
 * viaja cuando es verdadero (el servicio esconde lo fundido por defecto). {@code category} nunca
 * es {@link ActivityCategory#ACCESS}: el servicio lo rechaza con 400.
 */
public record ActivityFilter(ActivityCategory category, String type, String actorUsername, String subjectType, String subjectId,
                             ActivitySeverity severity, String sourceService, Instant from, Instant to, boolean includeSuperseded) {

    public static final ActivityFilter NONE = new ActivityFilter(null, null, null, null, null, null, null, null, null, false);
}

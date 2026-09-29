package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;

/** Los filtros de los accesos; lo que va a {@code null} no viaja. La IP es literal, no un rango. */
public record AccessFilter(String username, String ipAddress, String type, AccessOutcome outcome, Instant from, Instant to) {

    public static final AccessFilter NONE = new AccessFilter(null, null, null, null, null, null);
}

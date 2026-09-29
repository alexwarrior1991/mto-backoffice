package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;

/** Los filtros de mi bandeja; lo que va a {@code null} no viaja. {@code unread=true} deja solo las no leidas. */
public record InboxFilter(Boolean unread, ActivityCategory category, ActivitySeverity severity, Instant from, Instant to) {

    public static final InboxFilter NONE = new InboxFilter(null, null, null, null, null);
}

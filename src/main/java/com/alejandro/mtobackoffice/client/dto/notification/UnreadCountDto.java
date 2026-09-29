package com.alejandro.mtobackoffice.client.dto.notification;

/** El contador de la campana. Con {@code capped}, {@code count} es «ese numero o mas», no un total. */
public record UnreadCountDto(long count, boolean capped) {
}

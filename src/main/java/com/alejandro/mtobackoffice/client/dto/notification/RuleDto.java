package com.alejandro.mtobackoffice.client.dto.notification;

import java.util.List;

/**
 * Una regla cargada en mto-notification, tal como esta en su YAML: que eventos la disparan, su
 * condicion, a quien avisa ({@code KIND:clave}, con la clave como plantilla) y por que canales.
 * {@code throttleWindow} es una duracion ISO-8601 ({@code PT30M}) o {@code null} sin freno.
 */
public record RuleDto(String key, List<String> events, String when, ActivitySeverity severity, List<String> audiences,
                      List<String> channels, String title, String body, String link, String throttleWindow, String throttleKey) {

    public RuleDto {
        events = events == null ? List.of() : List.copyOf(events);
        audiences = audiences == null ? List.of() : List.copyOf(audiences);
        channels = channels == null ? List.of() : List.copyOf(channels);
    }
}

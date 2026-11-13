package com.alejandro.mtobackoffice.client.dto.notification;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * {@code POST /admin/broadcasts}: un aviso manual. Las audiencias van como {@code KIND:clave}
 * ({@link AudienceKind}) y los canales por su nombre en el servicio ({@link BroadcastChannel}); lo
 * que va a {@code null} no viaja (sin gravedad el servicio pone {@code INFO}; sin canales, la
 * bandeja).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BroadcastRequest(String title, String body, String link, ActivitySeverity severity, List<String> audiences,
                               List<String> channels) {
}

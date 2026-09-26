package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.Instant;
import java.util.UUID;

/**
 * Un cambio de estado del historial de una orden o de un defecto. Los estados llegan como texto
 * porque el historial es comun a los dos; el primero no tiene estado anterior.
 */
public record StatusHistoryDto(UUID id, String previousStatus, String newStatus, Instant changedAt, String changedBy, String comment) {
}

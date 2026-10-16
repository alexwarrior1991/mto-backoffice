package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.Instant;
import java.util.UUID;

/**
 * Una operacion de fondo de mto-maintenance ({@code GET /operations/{id}}): lo que siguio en el servicio
 * cuando una llamada con {@code Prefer: respond-async} no termino en su espera. Al terminar,
 * {@code result} es el mismo cuerpo que habria dado la llamada sincrona, o {@code error} el error que
 * habria dado. Solo la ve quien la pidio (o un supervisor); a otro, 404.
 */
public record OperationDto<T>(UUID id, String kind, String scope, OperationStatus status, String requestedBy, Instant createdAt,
                              Instant startedAt, Instant finishedAt, Integer resultStatus, T result, OperationErrorDto error) {
}

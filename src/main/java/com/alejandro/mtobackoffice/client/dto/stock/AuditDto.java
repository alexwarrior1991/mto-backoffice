package com.alejandro.mtobackoffice.client.dto.stock;

import java.time.Instant;

/** Quien toco la fila por ultima vez; en una revision de historial viene vacio a proposito. */
public record AuditDto(Instant createdAt, Instant updatedAt, String createdBy, String updatedBy) {
}

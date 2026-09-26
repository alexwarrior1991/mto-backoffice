package com.alejandro.mtobackoffice.client.dto;

import java.time.Instant;

/**
 * Quien, cuando y por que camino ({@code HTTP}, {@code MESSAGING}, {@code SYSTEM} o {@code BASELINE},
 * la foto inicial de lo que ya existia antes de auditar).
 */
public record RevisionMetadataDto(long revision, Instant revisionAt, RevisionOperation operation, String author,
                                  String source, String correlationId) {
}

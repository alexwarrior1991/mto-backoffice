package com.alejandro.mtobackoffice.client.dto.notification;

import java.time.Instant;
import java.util.List;

/**
 * {@code GET /admin/dead-letters/{source}}: los primeros mensajes de una DLQ, que siguen en ella.
 * Nunca traen el cuerpo: el JSON crudo solo vive en el inbox del servicio.
 */
public record DeadLetterPeekDto(String source, String deadLetterQueue, Long messages, List<PeekedDto> peeked) {

    public DeadLetterPeekDto {
        peeked = peeked == null ? List.of() : List.copyOf(peeked);
    }

    /** Un mensaje y lo que el inbox sabe de el; {@code inbox} es {@code null} si nunca llego a registrarse o ya se purgo. */
    public record PeekedDto(MessageDto message, InboxRecordDto inbox) {
    }

    /**
     * De donde venia el mensaje, por que lo descarto el broker ({@code deathReason}, {@code deathCount})
     * y, si no llegaria al inbox, por que ({@code problem}: la firma, un cuerpo que no es el sobre...).
     */
    public record MessageDto(String messageId, String sourceService, String eventType, String aggregateType, String aggregateId,
                             String exchange, String routingKey, String deathReason, Long deathCount, Instant deadLetteredAt,
                             Integer payloadBytes, String problem) {
    }

    public record InboxRecordDto(InboxMessageStatus status, Integer attempts, Instant failedAt, String failureReason) {
    }
}

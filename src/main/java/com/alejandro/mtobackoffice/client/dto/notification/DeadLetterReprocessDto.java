package com.alejandro.mtobackoffice.client.dto.notification;

import java.util.List;

/**
 * {@code POST /admin/dead-letters/{source}/reprocess}: una pasada de reproceso. {@code remaining}
 * es lo que guarda la DLQ al terminar: lo que volvio a fallar y lo que la pasada no llego a sacar.
 */
public record DeadLetterReprocessDto(String source, String deadLetterQueue, Integer requested, Integer fetched, Long processed,
                                     Long duplicates, Long failed, Long remaining, ReprocessStop stoppedBy, List<ResultDto> results) {

    public DeadLetterReprocessDto {
        results = results == null ? List.of() : List.copyOf(results);
    }

    /** Que fue de un mensaje; {@code reason} solo en los que volvieron a fallar. */
    public record ResultDto(String messageId, String eventType, String routingKey, ReprocessOutcome outcome, String reason) {
    }
}

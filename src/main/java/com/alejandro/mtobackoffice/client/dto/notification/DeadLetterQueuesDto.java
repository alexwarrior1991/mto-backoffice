package com.alejandro.mtobackoffice.client.dto.notification;

import java.util.List;

/**
 * {@code GET /admin/dead-letters}: la cola de mensajes muertos de cada fuente de RabbitMQ. Con el
 * broker apagado en el despliegue, {@code enabled} es {@code false} y no hay colas.
 */
public record DeadLetterQueuesDto(boolean enabled, List<QueueDto> queues) {

    public DeadLetterQueuesDto {
        queues = queues == null ? List.of() : List.copyOf(queues);
    }

    /** {@code messages} es {@code null} si el broker no tiene esa cola (un despliegue que aun no la ha declarado). */
    public record QueueDto(String source, String queue, String deadLetterQueue, Long messages) {

        /**
         * Si hay algo que mirar o reprocesar: la cola existe y guarda mensajes. Sin cola el servicio
         * responde 409 {@code SRC-409}; vacia, no hay nada que hacer.
         */
        public boolean hasMessages() {
            return messages != null && messages > 0;
        }
    }
}

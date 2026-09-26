package com.alejandro.mtobackoffice.client.dto.jobs;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Estado de un trabajo en segundo plano, tal como lo devuelven el 202 del arranque, el 429 de un
 * rechazo y el {@code GET} de consulta de las tres familias. Es la union de sus tres respuestas:
 * lo que no aplica llega omitido y aqui queda a {@code null}.
 *
 * <p>{@code downloadUrl} es la ruta interna del servicio ({@code /api/v1/...}); no se usa. La
 * descarga se pide a traves del gateway por la familia y el id ({@link JobFamily#path()}), que es
 * lo unico estable.</p>
 */
public record JobDto(
        UUID id,
        JobType type,
        JobStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant finishedAt,
        Long trackId,
        String mapperType,
        Integer totalItems,
        int processedItems,
        int successfulItems,
        int failedItems,
        String downloadUrl,
        String error,
        List<JobItemError> itemErrors
) {

    public JobDto {
        itemErrors = itemErrors == null ? List.of() : List.copyOf(itemErrors);
    }

    /** La familia a la que se pregunta por el trabajo; vacia si su tipo es desconocido. */
    public Optional<JobFamily> family() {
        return type == null ? Optional.empty() : type.family();
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    /**
     * Una exportacion solo se descarga completa; una importacion tambien cuando termino con
     * errores por fila, porque su fichero es el informe de esos errores. El republicado no
     * produce fichero, ni un trabajo de tipo desconocido, que no tiene familia a la que pedirlo.
     */
    public boolean isDownloadable() {
        if (status == null || !family().map(JobFamily::producesFile).orElse(false)) {
            return false;
        }
        return switch (type) {
            case PROFILE_EXPORT -> status == JobStatus.COMPLETED;
            case PROFILE_IMPORT, LOV_IMPORT -> status == JobStatus.COMPLETED || status == JobStatus.COMPLETED_WITH_ERRORS;
            default -> false;
        };
    }

    /** El nombre con el que se ofrece el fichero; el servicio manda el suyo en {@code Content-Disposition}. */
    public String suggestedFileName() {
        return switch (type) {
            case PROFILE_EXPORT -> "perfiles-via-" + trackId + ".csv";
            case PROFILE_IMPORT -> "informe-maestro-perfiles-" + id + ".json";
            case LOV_IMPORT -> "informe-catalogo-lov-" + id + ".json";
            default -> "trabajo-" + id;
        };
    }
}

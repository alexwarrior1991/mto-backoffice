package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobStatus;
import com.alejandro.mtobackoffice.client.dto.jobs.JobType;
import com.alejandro.mtobackoffice.client.dto.jobs.JobFamily;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

/**
 * Los trabajos en segundo plano de mto-configuration (README_ASYNC_JOBS.md): el arranque responde
 * 202 con el trabajo y un 429 cuando no hay cupo —tambien con el trabajo, ya {@code REJECTED}, que
 * llega como {@code TooManyRequestsApiException} con ese cuerpo—; despues se consulta por id y, si
 * produce fichero, se descarga por id. Cada familia vive bajo su prefijo ({@link JobFamily}).
 *
 * <p>Permisos del servicio: exportar y consultar, {@code config-read}; importar y republicar,
 * {@code config-import}; importar el catalogo de LOV, ademas {@code lov-manage}.</p>
 */
@HttpExchange("/api/configuration")
public interface JobsClient {

    @PostExchange(value = "/profiles/jobs/import", contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    JobDto importProfiles(@RequestPart("file") Resource file, @RequestParam("dryRun") boolean dryRun);

    @PostExchange(value = "/lovs/jobs/import", contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    JobDto importLovs(@RequestPart("file") Resource file, @RequestParam("dryRun") boolean dryRun);

    @PostExchange("/profiles/jobs/export")
    JobDto exportProfiles(@RequestParam("trackId") Long trackId, @RequestParam("mapperType") String mapperType);

    @PostExchange("/master-data/republish")
    JobDto republish(@RequestParam("entity") String entity,
                     @RequestParam(value = "trackId", required = false) Long trackId,
                     @RequestParam(value = "stationId", required = false) Long stationId);

    /**
     * Los trabajos de todas las familias, del mas reciente al mas antiguo ({@code GET /jobs}). Un
     * filtro ausente no filtra. Las filas no traen los errores por elemento ni la ruta de descarga:
     * las dos cosas se piden al detalle de la familia.
     */
    @GetExchange("/jobs")
    PageResponse<JobDto> list(@RequestParam("page") int page, @RequestParam("size") int size,
                              @RequestParam(value = "type", required = false) JobType type,
                              @RequestParam(value = "status", required = false) JobStatus status);

    @GetExchange("/profiles/jobs/{jobId}")
    JobDto profileJob(@PathVariable("jobId") UUID jobId);

    @GetExchange("/lovs/jobs/{jobId}")
    JobDto lovJob(@PathVariable("jobId") UUID jobId);

    @GetExchange("/master-data/republish/{jobId}")
    JobDto republishJob(@PathVariable("jobId") UUID jobId);

    @GetExchange("/profiles/jobs/{jobId}/file")
    ResponseEntity<byte[]> profileJobFile(@PathVariable("jobId") UUID jobId);

    @GetExchange("/lovs/jobs/{jobId}/file")
    ResponseEntity<byte[]> lovJobFile(@PathVariable("jobId") UUID jobId);

    default JobDto status(JobFamily family, UUID jobId) {
        return switch (family) {
            case PROFILE_JOBS -> profileJob(jobId);
            case LOV_JOBS -> lovJob(jobId);
            case REPUBLISH -> republishJob(jobId);
        };
    }

    /** El fichero de un trabajo que lo produce; el republicado no tiene. */
    default ResponseEntity<byte[]> file(JobFamily family, UUID jobId) {
        return switch (family) {
            case PROFILE_JOBS -> profileJobFile(jobId);
            case LOV_JOBS -> lovJobFile(jobId);
            case REPUBLISH -> throw new IllegalArgumentException("Un republicado no produce fichero");
        };
    }
}

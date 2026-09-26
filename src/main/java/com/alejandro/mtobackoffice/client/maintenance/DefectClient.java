package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ResolveDefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StatusHistoryDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Los defectos de catenaria. Resolver, cerrar y descartar piden {@code maintenance-supervise} ademas
 * de {@code maintenance-write}; vincular a una orden, solo {@code write}.
 */
@HttpExchange("/api/maintenance/defects")
public interface DefectClient {

    @PostExchange
    DefectDto create(@RequestBody DefectRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    DefectDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<DefectUpdateRequest> patch);

    @GetExchange("/{id}")
    DefectDto findById(@PathVariable("id") UUID id);

    /**
     * El historial de Envers, la revision mas reciente primero ({@code page} desde 0). Sin ninguna
     * revision el servicio responde 404: {@code RevisionsDialog} lo dice como «sin historial».
     */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<DefectDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);

    @GetExchange
    PageResponse<DefectDto> search(@RequestParam(value = "severity", required = false) DefectSeverity severity,
                                   @RequestParam(value = "status", required = false) DefectStatus status,
                                   @RequestParam(value = "assetId", required = false) UUID assetId,
                                   @RequestParam(value = "orderId", required = false) UUID orderId,
                                   @RequestParam(value = "trackId", required = false) Long trackId,
                                   @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                   @RequestParam(value = "detectedFrom", required = false) Instant detectedFrom,
                                   @RequestParam(value = "detectedTo", required = false) Instant detectedTo,
                                   @RequestParam("page") int page, @RequestParam("size") int size,
                                   @RequestParam("sort") List<String> sort);

    default PageResponse<DefectDto> search(DefectFilter filter, int page, int size, List<String> sort) {
        return search(filter.severity(), filter.status(), filter.assetId(), filter.orderId(), filter.trackId(), filter.executionPackageId(),
                filter.detectedFrom(), filter.detectedTo(), page, size, sort);
    }

    @PostExchange("/{id}/resolve")
    DefectDto resolve(@PathVariable("id") UUID id, @RequestBody ResolveDefectRequest request);

    @PostExchange("/{id}/close")
    DefectDto close(@PathVariable("id") UUID id, @RequestBody ReasonRequest request);

    @PostExchange("/{id}/discard")
    DefectDto discard(@PathVariable("id") UUID id, @RequestBody ReasonRequest request);

    /** Desde abierto (pasa a en curso) o en curso (se re-vincula); la orden no puede estar terminada. */
    @PostExchange("/{id}/link-order/{orderId}")
    DefectDto linkOrder(@PathVariable("id") UUID id, @PathVariable("orderId") UUID orderId);

    @GetExchange("/{id}/history")
    List<StatusHistoryDto> history(@PathVariable("id") UUID id);
}

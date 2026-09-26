package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateCorrectiveOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CreateDefectFromInspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Las inspecciones. Crear el defecto o la orden correctiva es idempotente: repetir devuelve lo ya
 * creado. Una inspeccion correcta no genera nada (422 {@code INS-001}).
 */
@HttpExchange("/api/maintenance/inspections")
public interface InspectionClient {

    @PostExchange
    InspectionDto create(@RequestBody InspectionRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    InspectionDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<InspectionUpdateRequest> patch);

    @PatchExchange(value = "/{id}/items/{itemId}", contentType = MergePatch.MEDIA_TYPE)
    InspectionDto updateItem(@PathVariable("id") UUID id, @PathVariable("itemId") UUID itemId, @RequestBody MergePatch<CheckItemUpdateRequest> patch);

    @GetExchange("/{id}")
    InspectionDto findById(@PathVariable("id") UUID id);

    /**
     * El historial de Envers, la revision mas reciente primero ({@code page} desde 0). Sin ninguna
     * revision el servicio responde 404: {@code RevisionsDialog} lo dice como «sin historial».
     */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<InspectionDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);

    @GetExchange
    PageResponse<InspectionDto> search(@RequestParam(value = "result", required = false) InspectionResult result,
                                       @RequestParam(value = "assetType", required = false) CatenaryAssetType assetType,
                                       @RequestParam(value = "trackId", required = false) Long trackId,
                                       @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                       @RequestParam(value = "inspectionFrom", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inspectionFrom,
                                       @RequestParam(value = "inspectionTo", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inspectionTo,
                                       @RequestParam(value = "inspector", required = false) String inspector,
                                       @RequestParam(value = "originOrderId", required = false) UUID originOrderId,
                                       @RequestParam("page") int page, @RequestParam("size") int size,
                                       @RequestParam("sort") List<String> sort);

    default PageResponse<InspectionDto> search(InspectionFilter filter, int page, int size, List<String> sort) {
        return search(filter.result(), filter.assetType(), filter.trackId(), filter.executionPackageId(), filter.inspectionFrom(),
                filter.inspectionTo(), filter.inspector(), filter.originOrderId(), page, size, sort);
    }

    @PostExchange("/{id}/create-defect")
    DefectDto createDefect(@PathVariable("id") UUID id, @RequestBody CreateDefectFromInspectionRequest request);

    @PostExchange("/{id}/create-corrective-order")
    OrderDto createCorrectiveOrder(@PathVariable("id") UUID id, @RequestBody CreateCorrectiveOrderRequest request);
}

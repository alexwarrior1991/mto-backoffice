package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Los activos de catenaria. Solo se da de alta un tramo de via; se desactiva con {@code DELETE}
 * ({@code maintenance-delete}) y se reactiva modificandolo con {@code enabled=true}. Nunca se
 * borra: las ordenes lo referencian.
 */
@HttpExchange("/api/maintenance/assets")
public interface AssetClient {

    /** 201, activo y de tipo {@code TRACK_SECTION}; 409 {@code AST-409} si el codigo ya existe. */
    @PostExchange
    AssetDto create(@RequestBody AssetRequest request);

    @PutExchange("/{id}")
    AssetDto update(@PathVariable("id") UUID id, @RequestBody AssetUpdateRequest request);

    @GetExchange("/{id}")
    AssetDto findById(@PathVariable("id") UUID id);

    /**
     * El historial de Envers, la revision mas reciente primero ({@code page} desde 0). Sin ninguna
     * revision el servicio responde 404: {@code RevisionsDialog} lo dice como «sin historial».
     */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<AssetDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);

    @GetExchange
    PageResponse<AssetDto> search(@RequestParam(value = "type", required = false) CatenaryAssetType type,
                                  @RequestParam(value = "trackId", required = false) Long trackId,
                                  @RequestParam(value = "stationId", required = false) Long stationId,
                                  @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                  @RequestParam(value = "enabled", required = false) Boolean enabled,
                                  @RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "preventiveDueBefore", required = false) Instant preventiveDueBefore,
                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                  @RequestParam("sort") List<String> sort);

    default PageResponse<AssetDto> search(AssetFilter filter, int page, int size, List<String> sort) {
        return search(filter.type(), filter.trackId(), filter.stationId(), filter.executionPackageId(), filter.enabled(), filter.name(),
                filter.preventiveDueBefore(), page, size, sort);
    }

    /** Desactiva: 204, y si ya lo estaba, tambien. */
    @DeleteExchange("/{id}")
    void disable(@PathVariable("id") UUID id);

    /** Las ordenes de un activo, la mas reciente primero salvo otro orden. */
    @GetExchange("/{id}/orders")
    PageResponse<OrderDto> orders(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size,
                                  @RequestParam("sort") List<String> sort);
}

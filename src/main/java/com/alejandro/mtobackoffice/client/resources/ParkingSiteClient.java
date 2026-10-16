package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteFilter;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.UUID;

/**
 * El catalogo de sitios de aparcamiento de mto-resources ({@code resources-write} para escribir). Es
 * opcional: un vehiculo se puede dejar en una via y un kp, o con una descripcion, sin sitio. No se
 * borran: {@code DELETE} retira ({@code resources-delete}) y las ubicaciones registradas alli lo
 * siguen nombrando.
 */
@HttpExchange("/api/resources/parking-sites")
public interface ParkingSiteClient {

    @GetExchange
    PageResponse<ParkingSiteDto> search(@RequestParam(value = "search", required = false) String search,
                                        @RequestParam(value = "kind", required = false) ParkingSiteKind kind,
                                        @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                        @RequestParam(value = "trackId", required = false) Long trackId,
                                        @RequestParam(value = "railAccess", required = false) Boolean railAccess,
                                        @RequestParam(value = "roadAccess", required = false) Boolean roadAccess,
                                        @RequestParam(value = "active", required = false) Boolean active,
                                        @RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sort") List<String> sort);

    /** Con el id para desempatar al final. */
    default PageResponse<ParkingSiteDto> search(ParkingSiteFilter filter, int page, int size, List<String> sort) {
        return search(filter.search(), filter.kind(), filter.executionPackageId(), filter.trackId(), filter.railAccess(), filter.roadAccess(),
                filter.active(), page, size, MasterFilters.withTieBreak(sort, MasterFilters.BY_ID));
    }

    @GetExchange("/{id}")
    ParkingSiteDto findById(@PathVariable("id") UUID id);

    /** 201; 409 {@code PRK-409} si el codigo ya existe. */
    @PostExchange
    ParkingSiteDto create(@RequestBody ParkingSiteRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    ParkingSiteDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<ParkingSiteUpdateRequest> patch);

    /** Retira: 204. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);
}

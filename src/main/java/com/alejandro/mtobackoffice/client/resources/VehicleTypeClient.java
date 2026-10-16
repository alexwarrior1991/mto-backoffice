package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeUpdateRequest;
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
 * Los modelos de vehiculo de mto-resources ({@code resources-fleet-write} para escribir). No se borran:
 * {@code DELETE} retira ({@code resources-delete}) y ningun vehiculo nuevo es de el; los que ya lo son
 * se quedan. Un modelo incoherente con su movilidad (una categoria de otra, cifras de via en uno de
 * carretera, cifras de grua sin grua) es un 400 con el motivo.
 */
@HttpExchange("/api/resources/vehicle-types")
public interface VehicleTypeClient {

    @GetExchange
    PageResponse<VehicleTypeDto> search(@RequestParam(value = "search", required = false) String search,
                                        @RequestParam(value = "mobility", required = false) VehicleMobility mobility,
                                        @RequestParam(value = "category", required = false) VehicleCategory category,
                                        @RequestParam(value = "capability", required = false) String capability,
                                        @RequestParam(value = "active", required = false) Boolean active,
                                        @RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sort") List<String> sort);

    /** Con el id para desempatar al final. */
    default PageResponse<VehicleTypeDto> search(VehicleTypeFilter filter, int page, int size, List<String> sort) {
        return search(filter.search(), filter.mobility(), filter.category(), filter.capability(), filter.active(), page, size,
                MasterFilters.withTieBreak(sort, MasterFilters.BY_ID));
    }

    @GetExchange("/{id}")
    VehicleTypeDto findById(@PathVariable("id") UUID id);

    /** 201; 409 {@code VTY-409} si el codigo ya existe. */
    @PostExchange
    VehicleTypeDto create(@RequestBody VehicleTypeRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    VehicleTypeDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<VehicleTypeUpdateRequest> patch);

    /** Retira: 204. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);
}

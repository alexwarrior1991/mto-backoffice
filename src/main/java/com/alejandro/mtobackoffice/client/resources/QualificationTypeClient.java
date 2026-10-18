package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationKind;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeRequest;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeUpdateRequest;
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
 * El catalogo de tipos de cualificacion de mto-resources ({@code resources-write} para escribir): los
 * permisos de conducir, las habilitaciones para manejar un vehiculo, las de seguridad. No se borran:
 * {@code DELETE} retira ({@code resources-delete}) y las cualificaciones que ya lo son se quedan.
 */
@HttpExchange("/api/resources/qualification-types")
public interface QualificationTypeClient {

    @GetExchange
    PageResponse<QualificationTypeDto> search(@RequestParam(value = "search", required = false) String search,
                                              @RequestParam(value = "kind", required = false) QualificationKind kind,
                                              @RequestParam(value = "active", required = false) Boolean active,
                                              @RequestParam("page") int page, @RequestParam("size") int size,
                                              @RequestParam("sort") List<String> sort);

    /** Con el id para desempatar al final. */
    default PageResponse<QualificationTypeDto> search(QualificationTypeFilter filter, int page, int size, List<String> sort) {
        return search(filter.search(), filter.kind(), filter.active(), page, size, MasterFilters.withTieBreak(sort, MasterFilters.BY_ID));
    }

    @GetExchange("/{id}")
    QualificationTypeDto findById(@PathVariable("id") UUID id);

    /** 201; 409 {@code QTY-409} si el codigo ya existe. */
    @PostExchange
    QualificationTypeDto create(@RequestBody QualificationTypeRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    QualificationTypeDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<QualificationTypeUpdateRequest> patch);

    /** Retira: 204. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);
}

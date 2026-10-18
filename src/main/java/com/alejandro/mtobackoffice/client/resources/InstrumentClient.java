package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentFilter;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentRequest;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentUpdateRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Los instrumentos de mto-resources ({@code resources-fleet-write} para escribirlos), con su
 * calibracion; se eligen en los turnos de mantenimiento. No se borran: {@code DELETE} retira
 * ({@code resources-delete}). Tienen historial, el de Envers.
 */
@HttpExchange("/api/resources/instruments")
public interface InstrumentClient {

    /** {@code calibrationExpiresBefore} lleva {@code @DateTimeFormat}: sin el saldria con el formato corto de la maquina. */
    @GetExchange
    PageResponse<InstrumentDto> search(@RequestParam(value = "search", required = false) String search,
                                       @RequestParam(value = "kind", required = false) InstrumentKind kind,
                                       @RequestParam(value = "active", required = false) Boolean active,
                                       @RequestParam(value = "calibrationExpiresBefore", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate calibrationExpiresBefore,
                                       @RequestParam("page") int page, @RequestParam("size") int size,
                                       @RequestParam("sort") List<String> sort);

    /** Con el id para desempatar al final. */
    default PageResponse<InstrumentDto> search(InstrumentFilter filter, int page, int size, List<String> sort) {
        return search(filter.search(), filter.kind(), filter.active(), filter.calibrationExpiresBefore(), page, size,
                MasterFilters.withTieBreak(sort, MasterFilters.BY_ID));
    }

    @GetExchange("/{id}")
    InstrumentDto findById(@PathVariable("id") UUID id);

    /** 201; 409 {@code INT-409} si el codigo ya existe. */
    @PostExchange
    InstrumentDto create(@RequestBody InstrumentRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    InstrumentDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<InstrumentUpdateRequest> patch);

    /** Retira: 204. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);

    /** El historial de Envers, la mas reciente primero; sin revisiones, 404 {@code INT-404}. */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<InstrumentDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);
}

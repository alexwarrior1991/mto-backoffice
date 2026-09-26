package com.alejandro.mtobackoffice.client.stock;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationStatus;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.UUID;

/** Las reservas; solo una activa cambia de estado (422 {@code RES-001} si no). */
@HttpExchange("/api/stock/reservations")
public interface ReservationClient {

    /** 201, activa; 409 {@code STK-001} si no hay disponible. */
    @PostExchange
    ReservationDto create(@RequestBody ReservationRequest request);

    @PutExchange("/{id}")
    ReservationDto update(@PathVariable("id") UUID id, @RequestBody ReservationUpdateRequest request);

    /** Cancela: el {@code DELETE} responde 200 con la reserva ya cancelada. Pide {@code stock-delete}. */
    @DeleteExchange("/{id}")
    ReservationDto cancel(@PathVariable("id") UUID id);

    /** Libera el reservado sin movimiento. */
    @PostExchange("/{id}/release")
    ReservationDto release(@PathVariable("id") UUID id);

    /** Consume: baja el fisico ademas del reservado. */
    @PostExchange("/{id}/consume")
    ReservationDto consume(@PathVariable("id") UUID id);

    @GetExchange("/{id}")
    ReservationDto findById(@PathVariable("id") UUID id);

    @GetExchange
    PageResponse<ReservationDto> search(@RequestParam(value = "warehouseId", required = false) UUID warehouseId,
                                        @RequestParam(value = "status", required = false) ReservationStatus status,
                                        @RequestParam(value = "projectId", required = false) UUID projectId,
                                        @RequestParam(value = "materialId", required = false) UUID materialId,
                                        @RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sort") List<String> sort);

    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<ReservationDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);
}

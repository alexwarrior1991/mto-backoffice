package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CloseShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceTaskStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.PossessionType;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Los turnos nocturnos: {@code PLANNED → IN_PROGRESS → CLOSED}, o {@code CANCELLED}. Una tarea solo
 * se trabaja en un turno en curso que recorra su via, con una posesion compatible (409
 * {@code SHF-001} si no).
 */
@HttpExchange("/api/maintenance/shifts")
public interface ShiftClient {

    @PostExchange
    ShiftDto create(@RequestBody ShiftRequest request);

    @PutExchange("/{id}")
    ShiftDto update(@PathVariable("id") UUID id, @RequestBody ShiftUpdateRequest request);

    @GetExchange("/{id}")
    ShiftDto findById(@PathVariable("id") UUID id);

    @GetExchange
    PageResponse<ShiftDto> search(@RequestParam(value = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                  @RequestParam(value = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                  @RequestParam(value = "teamId", required = false) UUID teamId,
                                  @RequestParam(value = "trackId", required = false) Long trackId,
                                  @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                  @RequestParam(value = "status", required = false) ShiftStatus status,
                                  @RequestParam(value = "possessionType", required = false) PossessionType possessionType,
                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                  @RequestParam("sort") List<String> sort);

    default PageResponse<ShiftDto> search(ShiftFilter filter, int page, int size, List<String> sort) {
        return search(filter.dateFrom(), filter.dateTo(), filter.teamId(), filter.trackId(), filter.executionPackageId(), filter.status(),
                filter.possessionType(), page, size, sort);
    }

    @PostExchange("/{id}/start")
    ShiftDto start(@PathVariable("id") UUID id, @RequestBody StartShiftRequest request);

    /** Las tareas sin terminar vuelven a la cola de su orden, sin cancelarse. */
    @PostExchange("/{id}/close")
    ShiftDto close(@PathVariable("id") UUID id, @RequestBody CloseShiftRequest request);

    @PostExchange("/{id}/cancel")
    ShiftDto cancel(@PathVariable("id") UUID id, @RequestBody ReasonRequest request);

    @GetExchange("/{id}/tasks")
    List<TaskDto> tasks(@PathVariable("id") UUID id, @RequestParam(value = "status", required = false) MaintenanceTaskStatus status);

    /** Los perfiles de las tareas del turno, en el orden fisico de la via. */
    @GetExchange("/{id}/profiles")
    List<AssetSummaryDto> profiles(@PathVariable("id") UUID id, @RequestParam(value = "status", required = false) MaintenanceTaskStatus status);

    /** Asigna una tarea pendiente al turno; comprueba via y posesion (409 {@code SHF-001}). */
    @PostExchange("/{id}/tasks/{taskId}")
    TaskDto assignTask(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId);
}

package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Las ordenes de mantenimiento. El orden ({@code sort=campo,asc}) solo admite atributos de la
 * entidad: los calculados ({@code taskCount}, {@code estimatedMinutes}...) no, y uno desconocido es
 * un 400 {@code REQ-400}.
 */
@HttpExchange("/api/maintenance/orders")
public interface OrderClient {

    @GetExchange("/{id}")
    OrderDto findById(@PathVariable("id") UUID id);

    /**
     * Las fechas van con {@code @DateTimeFormat}: sin el, el conversor por defecto escribiria un
     * {@code LocalDate} con el formato corto de la maquina y no en ISO.
     */
    @GetExchange
    PageResponse<OrderDto> search(@RequestParam(value = "status", required = false) MaintenanceOrderStatus status,
                                  @RequestParam(value = "type", required = false) MaintenanceOrderType type,
                                  @RequestParam(value = "priority", required = false) MaintenancePriority priority,
                                  @RequestParam(value = "assetId", required = false) UUID assetId,
                                  @RequestParam(value = "assetType", required = false) CatenaryAssetType assetType,
                                  @RequestParam(value = "trackId", required = false) Long trackId,
                                  @RequestParam(value = "stationId", required = false) Long stationId,
                                  @RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                  @RequestParam(value = "plannedFrom", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedFrom,
                                  @RequestParam(value = "plannedTo", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedTo,
                                  @RequestParam(value = "assignedUser", required = false) String assignedUser,
                                  @RequestParam(value = "teamId", required = false) UUID teamId,
                                  @RequestParam(value = "code", required = false) String code,
                                  @RequestParam("page") int page, @RequestParam("size") int size,
                                  @RequestParam("sort") List<String> sort);

    default PageResponse<OrderDto> search(OrderFilter filter, int page, int size, List<String> sort) {
        return search(filter.status(), filter.type(), filter.priority(), filter.assetId(), filter.assetType(),
                filter.trackId(), filter.stationId(), filter.executionPackageId(), filter.plannedFrom(),
                filter.plannedTo(), filter.assignedUser(), filter.teamId(), filter.code(), page, size, sort);
    }
}

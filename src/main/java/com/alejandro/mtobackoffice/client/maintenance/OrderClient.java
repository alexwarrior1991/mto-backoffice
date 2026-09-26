package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssignOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CommentRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.CompleteTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.GenerateTasksResultDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.PlanOrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.StatusHistoryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskUpdateRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

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

    /** 201, en borrador; el activo tiene que estar activo (409 {@code AST-001} si no). */
    @PostExchange
    OrderDto create(@RequestBody OrderRequest request);

    @PutExchange("/{id}")
    OrderDto update(@PathVariable("id") UUID id, @RequestBody OrderUpdateRequest request);

    @GetExchange("/{id}")
    OrderDto findById(@PathVariable("id") UUID id);

    /**
     * El historial de Envers, la revision mas reciente primero ({@code page} desde 0). Sin ninguna
     * revision el servicio responde 404: {@code RevisionsDialog} lo dice como «sin historial».
     */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<OrderDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);

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

    // --- Transiciones: 409 TRN-001 desde un estado que no la admite ---------------------------------

    @PostExchange("/{id}/plan")
    OrderDto plan(@PathVariable("id") UUID id, @RequestBody PlanOrderRequest request);

    @PostExchange("/{id}/assign")
    OrderDto assign(@PathVariable("id") UUID id, @RequestBody AssignOrderRequest request);

    @PostExchange("/{id}/start")
    OrderDto start(@PathVariable("id") UUID id, @RequestBody CommentRequest request);

    /** {@code force} pide {@code maintenance-supervise} ademas de {@code maintenance-write}. */
    @PostExchange("/{id}/complete")
    OrderDto complete(@PathVariable("id") UUID id, @RequestBody CompleteOrderRequest request);

    /** Pide {@code maintenance-supervise} ademas de {@code maintenance-write}; libera las reservas de sus materiales. */
    @PostExchange("/{id}/cancel")
    OrderDto cancel(@PathVariable("id") UUID id, @RequestBody ReasonRequest request);

    /** Los cambios de estado, el primero el alta. */
    @GetExchange("/{id}/history")
    List<StatusHistoryDto> history(@PathVariable("id") UUID id);

    // --- Tareas -------------------------------------------------------------------------------------

    @GetExchange("/{id}/tasks")
    List<TaskDto> tasks(@PathVariable("id") UUID id);

    @PostExchange("/{id}/tasks")
    TaskDto createTask(@PathVariable("id") UUID id, @RequestBody TaskRequest request);

    /** Una tarea por perfil habilitado del tramo; los que ya tienen tarea se saltan. Solo preventivas en borrador o planificadas. */
    @PostExchange("/{id}/tasks/generate")
    GenerateTasksResultDto generateTasks(@PathVariable("id") UUID id, @RequestBody GenerateTasksRequest request);

    @PutExchange("/{id}/tasks/{taskId}")
    TaskDto updateTask(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId, @RequestBody TaskUpdateRequest request);

    @PostExchange("/{id}/tasks/{taskId}/cancel")
    TaskDto cancelTask(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId, @RequestBody ReasonRequest request);

    /** Con la orden en curso y un turno en curso de su via, con posesion compatible. */
    @PostExchange("/{id}/tasks/{taskId}/start")
    TaskDto startTask(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId, @RequestBody StartTaskRequest request);

    /** La fila del parte: defectos en linea, materiales usados, notas; el checklist tiene que estar contestado. */
    @PostExchange("/{id}/tasks/{taskId}/complete")
    TaskDto completeTask(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId, @RequestBody CompleteTaskRequest request);

    @PutExchange("/{id}/tasks/{taskId}/check-items/{itemId}")
    TaskDto updateCheckItem(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId, @PathVariable("itemId") UUID itemId,
                            @RequestBody CheckItemUpdateRequest request);

    // --- Lineas de material: se reservan al planificar, se consumen al completar, se liberan al cancelar ---

    @GetExchange("/{id}/materials")
    List<MaterialUsageDto> materials(@PathVariable("id") UUID id);

    /** Con la orden sin terminar; fuera de borrador se reserva al momento. */
    @PostExchange("/{id}/materials")
    MaterialUsageDto registerMaterial(@PathVariable("id") UUID id, @RequestBody MaterialUsageRequest request);

    @PutExchange("/{id}/materials/{usageId}")
    MaterialUsageDto updateMaterial(@PathVariable("id") UUID id, @PathVariable("usageId") UUID usageId, @RequestBody MaterialUsageUpdateRequest request);

    /** Reintenta con mto-stock lo que toque por el estado de la orden; 503 {@code STK-503} si el almacen sigue sin responder. */
    @PostExchange("/{id}/materials/{usageId}/sync")
    MaterialUsageDto syncMaterial(@PathVariable("id") UUID id, @PathVariable("usageId") UUID usageId);

    /**
     * Quita la linea (204; pide {@code maintenance-delete}), liberando antes su reserva. Consumida o
     * con la orden terminada, 409 {@code MAT-001}; con el almacen caido, 503 {@code STK-503} y la
     * linea sigue.
     */
    @DeleteExchange("/{id}/materials/{usageId}")
    void removeMaterial(@PathVariable("id") UUID id, @PathVariable("usageId") UUID usageId);
}

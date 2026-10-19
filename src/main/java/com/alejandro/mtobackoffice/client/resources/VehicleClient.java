package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.resources.FleetPositionDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleCategory;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleFilter;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOperationalStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Los vehiculos de la flota de mto-resources ({@code resources-fleet-write} para escribirlos) y donde
 * esta cada uno: su historial de ubicaciones, solo de anadir ({@code resources-location-write} para
 * registrar una a mano), y el tablero de la flota. Un vehiculo no se borra: {@code DELETE} lo retira
 * ({@code resources-delete}; 409 {@code VEH-001} mientras sea el vehiculo por defecto de un equipo).
 * Sus documentos que caducan (la ITV, el seguro...) cuelgan de el, uno de cada tipo, y su taller: las
 * reparaciones, que no se borran (se cancelan) y, si lo inmovilizan, mandan en su estado.
 */
@HttpExchange("/api/resources/vehicles")
public interface VehicleClient {

    /** El orden del tablero: por via y kp, con el vehiculo para desempatar; con una via elegida se lee como la via. */
    List<String> BY_TRACK_AND_KP = List.of("trackId,asc", "kp,asc", "vehicleId,asc");

    @GetExchange
    PageResponse<VehicleDto> search(@RequestParam(value = "search", required = false) String search,
                                    @RequestParam(value = "typeId", required = false) UUID typeId,
                                    @RequestParam(value = "mobility", required = false) VehicleMobility mobility,
                                    @RequestParam(value = "category", required = false) VehicleCategory category,
                                    @RequestParam(value = "status", required = false) VehicleOperationalStatus status,
                                    @RequestParam(value = "available", required = false) Boolean available,
                                    @RequestParam(value = "active", required = false) Boolean active,
                                    @RequestParam("page") int page, @RequestParam("size") int size,
                                    @RequestParam("sort") List<String> sort);

    /** Con el id para desempatar al final. */
    default PageResponse<VehicleDto> search(VehicleFilter filter, int page, int size, List<String> sort) {
        return search(filter.search(), filter.typeId(), filter.mobility(), filter.category(), filter.status(), filter.available(), filter.active(),
                page, size, MasterFilters.withTieBreak(sort, MasterFilters.BY_ID));
    }

    @GetExchange("/{id}")
    VehicleDto findById(@PathVariable("id") UUID id);

    /** 201; 409 {@code VEH-409} si el codigo, la matricula o el numero ferroviario ya los tiene otro. */
    @PostExchange
    VehicleDto create(@RequestBody VehicleRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    VehicleDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<VehicleUpdateRequest> patch);

    /** Retira: 204. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);

    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<VehicleDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);

    /** 201 con la ubicacion; 409 {@code VEH-001} si su movilidad no la admite. */
    @PostExchange("/{id}/locations")
    VehicleLocationDto recordLocation(@PathVariable("id") UUID id, @RequestBody VehicleLocationRequest request);

    /** El historial de un vehiculo, la mas reciente primero (sin {@code sort}: ordena el servicio); {@code to} no se incluye. */
    @GetExchange("/{id}/locations")
    PageResponse<VehicleLocationDto> locations(@PathVariable("id") UUID id, @RequestParam(value = "from", required = false) Instant from,
                                               @RequestParam(value = "to", required = false) Instant to,
                                               @RequestParam("page") int page, @RequestParam("size") int size);

    /** El tablero: los vehiculos activos con posicion conocida; un remolcado se encuentra por donde esta su portador. */
    @GetExchange("/positions")
    PageResponse<FleetPositionDto> positions(@RequestParam(value = "executionPackageId", required = false) Long executionPackageId,
                                             @RequestParam(value = "trackId", required = false) Long trackId,
                                             @RequestParam(value = "kind", required = false) VehicleLocationKind kind,
                                             @RequestParam("page") int page, @RequestParam("size") int size,
                                             @RequestParam("sort") List<String> sort);

    /** Sus documentos, enteros. */
    @GetExchange("/{id}/documents")
    List<VehicleDocumentDto> documents(@PathVariable("id") UUID id);

    /** 201 ({@code resources-fleet-write}); 409 {@code VDO-409} si ya tiene uno de ese tipo. */
    @PostExchange("/{id}/documents")
    VehicleDocumentDto addDocument(@PathVariable("id") UUID id, @RequestBody VehicleDocumentRequest request);

    @PatchExchange(value = "/{id}/documents/{documentId}", contentType = MergePatch.MEDIA_TYPE)
    VehicleDocumentDto updateDocument(@PathVariable("id") UUID id, @PathVariable("documentId") UUID documentId,
                                      @RequestBody MergePatch<VehicleDocumentUpdateRequest> patch);

    /** Un borrado de verdad ({@code resources-delete}): 204. */
    @DeleteExchange("/{id}/documents/{documentId}")
    void removeDocument(@PathVariable("id") UUID id, @PathVariable("documentId") UUID documentId);

    /** Su taller, la mas reciente primero. */
    @GetExchange("/{id}/repairs")
    List<VehicleRepairDto> repairs(@PathVariable("id") UUID id);

    /** 201 ({@code resources-fleet-write}): planificada, o abierta ya. */
    @PostExchange("/{id}/repairs")
    VehicleRepairDto addRepair(@PathVariable("id") UUID id, @RequestBody VehicleRepairRequest request);

    @PatchExchange(value = "/{id}/repairs/{repairId}", contentType = MergePatch.MEDIA_TYPE)
    VehicleRepairDto updateRepair(@PathVariable("id") UUID id, @PathVariable("repairId") UUID repairId,
                                  @RequestBody MergePatch<VehicleRepairUpdateRequest> patch);

    /** {@code PLANNED -> OPEN}, sin cuerpo; 409 {@code REP-001} si no esta planificada. */
    @PostExchange("/{id}/repairs/{repairId}/open")
    VehicleRepairDto openRepair(@PathVariable("id") UUID id, @PathVariable("repairId") UUID repairId);

    /** {@code OPEN -> CLOSED}, sin cuerpo; 409 {@code REP-001} si no esta abierta. */
    @PostExchange("/{id}/repairs/{repairId}/close")
    VehicleRepairDto closeRepair(@PathVariable("id") UUID id, @PathVariable("repairId") UUID repairId);

    /** Planificada o abierta {@code -> CANCELLED}, sin cuerpo. */
    @PostExchange("/{id}/repairs/{repairId}/cancel")
    VehicleRepairDto cancelRepair(@PathVariable("id") UUID id, @PathVariable("repairId") UUID repairId);
}

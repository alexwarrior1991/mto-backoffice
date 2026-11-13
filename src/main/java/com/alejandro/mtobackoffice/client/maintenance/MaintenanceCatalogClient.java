package com.alejandro.mtobackoffice.client.maintenance;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.FunctionalGroup;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionTemplateUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.UUID;

/**
 * Los catalogos de mto-maintenance, sin paginar porque son pocos: los equipos (que se dan de alta y
 * se modifican aqui), los tipos de tarea del plan (de los que solo cambia el equipo que exigen) y las
 * plantillas de inspeccion (versionadas por tipo de activo: se crea la version siguiente, y de una que
 * existe solo cambian el nombre y si es la activa).
 */
@HttpExchange("/api/maintenance")
public interface MaintenanceCatalogClient {

    @GetExchange("/teams")
    List<TeamDto> teams();

    /** 201; 409 {@code TEA-409} si el codigo ya existe. */
    @PostExchange("/teams")
    TeamDto createTeam(@RequestBody TeamRequest request);

    /** Completo: lo que no viaja (base, vehiculo) se borra. */
    @PutExchange("/teams/{id}")
    TeamDto updateTeam(@PathVariable("id") UUID id, @RequestBody TeamRequest request);

    @GetExchange("/task-types")
    List<TaskTypeDto> taskTypes(@RequestParam(value = "functionalGroup", required = false) FunctionalGroup functionalGroup,
                                @RequestParam(value = "requiresFullPossession", required = false) Boolean requiresFullPossession,
                                @RequestParam(value = "diagnostic", required = false) Boolean diagnostic);

    /**
     * El equipo que exige un tipo de tarea, con merge-patch y la version leida; pide
     * {@code maintenance-supervise} y {@code maintenance-write}. 409 {@code CON-001} con una version vieja.
     */
    @PatchExchange(value = "/task-types/{code}", contentType = MergePatch.MEDIA_TYPE)
    TaskTypeDto updateTaskType(@PathVariable("code") String code, @RequestBody MergePatch<TaskTypeUpdateRequest> patch);

    /** Todas las versiones de todos los tipos de activo, activas o no, enteras y sin paginar. */
    @GetExchange("/inspection-templates")
    List<InspectionTemplateDto> inspectionTemplates();

    /**
     * La version siguiente de la plantilla de un tipo de activo, que nace activa y sustituye a la activa;
     * pide {@code maintenance-supervise} y {@code maintenance-write}. 201; 409 {@code CON-001} si
     * {@code replacesVersion} ya no es la activa del tipo.
     */
    @PostExchange("/inspection-templates")
    InspectionTemplateDto createInspectionTemplate(@RequestBody InspectionTemplateRequest request);

    /**
     * Renombrar, activar o desactivar una version, con merge-patch y su {@code lockVersion} leido (nunca
     * {@code version}); pide {@code maintenance-supervise} y {@code maintenance-write}. 409
     * {@code CON-001} con un bloqueo viejo; 404 {@code TPL-404} si ya no existe.
     */
    @PatchExchange(value = "/inspection-templates/{id}", contentType = MergePatch.MEDIA_TYPE)
    InspectionTemplateDto updateInspectionTemplate(@PathVariable("id") UUID id, @RequestBody MergePatch<InspectionTemplateUpdateRequest> patch);
}

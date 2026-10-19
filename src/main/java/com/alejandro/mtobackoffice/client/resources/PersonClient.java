package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonFilter;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonUpdateRequest;
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
 * Las personas de mto-resources. No se borran nunca: {@code DELETE} desactiva
 * ({@code resources-delete}; 409 {@code UNI-001} si dirige una unidad o un equipo se quedaria con
 * menos de 2 linemen), y se reactiva con un {@code PATCH active=true} ({@code resources-write}).
 * Sus cualificaciones cuelgan de ella: una de cada tipo, que se renueva cambiando su caducidad. Y sus
 * ausencias, que no se solapan (409 {@code ABS-001}).
 */
@HttpExchange("/api/resources/people")
public interface PersonClient {

    /** 201; 409 {@code PER-409} si el usuario o el numero de empleado ya los tiene otra. */
    @PostExchange
    PersonDto create(@RequestBody PersonRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    PersonDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<PersonUpdateRequest> patch);

    @GetExchange("/{id}")
    PersonDto findById(@PathVariable("id") UUID id);

    /** El historial de Envers, la mas reciente primero; sin revisiones, 404 {@code PER-404}. */
    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<PersonDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page, @RequestParam("size") int size);

    @GetExchange
    PageResponse<PersonDto> search(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "category", required = false) List<PersonCategory> categories,
                                   @RequestParam(value = "unitId", required = false) UUID unitId,
                                   @RequestParam(value = "active", required = false) Boolean active,
                                   @RequestParam("page") int page, @RequestParam("size") int size,
                                   @RequestParam("sort") List<String> sort);

    /** Con el id para desempatar al final: dos personas con los mismos apellidos no saltan de pagina. */
    default PageResponse<PersonDto> search(PersonFilter filter, int page, int size, List<String> sort) {
        return search(filter.search(), filter.categories().isEmpty() ? null : List.copyOf(filter.categories()), filter.unitId(),
                filter.active(), page, size, MasterFilters.withTieBreak(sort, MasterFilters.BY_ID));
    }

    /** Desactiva: 204. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);

    /** Sus cualificaciones, enteras y por el codigo del tipo. */
    @GetExchange("/{id}/qualifications")
    List<PersonQualificationDto> qualifications(@PathVariable("id") UUID id);

    /** 201; 409 {@code QUA-001} si ya tiene ese tipo o el tipo esta retirado. */
    @PostExchange("/{id}/qualifications")
    PersonQualificationDto addQualification(@PathVariable("id") UUID id, @RequestBody PersonQualificationRequest request);

    @PatchExchange(value = "/{id}/qualifications/{qualificationId}", contentType = MergePatch.MEDIA_TYPE)
    PersonQualificationDto updateQualification(@PathVariable("id") UUID id, @PathVariable("qualificationId") UUID qualificationId,
                                               @RequestBody MergePatch<PersonQualificationUpdateRequest> patch);

    /** Un borrado de verdad ({@code resources-delete}): 204; su historial lo conserva el servicio. */
    @DeleteExchange("/{id}/qualifications/{qualificationId}")
    void removeQualification(@PathVariable("id") UUID id, @PathVariable("qualificationId") UUID qualificationId);

    /** Sus ausencias, la mas reciente primero. */
    @GetExchange("/{id}/absences")
    List<PersonAbsenceDto> absences(@PathVariable("id") UUID id);

    /** 201 ({@code resources-write}); 409 {@code ABS-001} si se solapa con otra suya. */
    @PostExchange("/{id}/absences")
    PersonAbsenceDto addAbsence(@PathVariable("id") UUID id, @RequestBody PersonAbsenceRequest request);

    @PatchExchange(value = "/{id}/absences/{absenceId}", contentType = MergePatch.MEDIA_TYPE)
    PersonAbsenceDto updateAbsence(@PathVariable("id") UUID id, @PathVariable("absenceId") UUID absenceId,
                                   @RequestBody MergePatch<PersonAbsenceUpdateRequest> patch);

    /** Un borrado de verdad ({@code resources-delete}): 204; su historial lo conserva el servicio. */
    @DeleteExchange("/{id}/absences/{absenceId}")
    void removeAbsence(@PathVariable("id") UUID id, @PathVariable("absenceId") UUID absenceId);
}

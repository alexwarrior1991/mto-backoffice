package com.alejandro.mtobackoffice.client.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.RevisionDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationTreeNodeDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitRequest;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberDto;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberLeaveRequest;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMemberRequest;
import com.alejandro.mtobackoffice.client.dto.resources.TeamMembersRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.util.List;
import java.util.UUID;

/**
 * El organigrama de mto-resources: proyecto, departamento, jefatura de supervisores, supervision y
 * equipo. Un equipo nace completo (su jefe y de 2 a 6 linemen) y nunca sale de esa regla: sus linemen
 * entran de uno en uno ({@code POST /members}, 409 {@code UNI-001} con 6), salen de uno en uno
 * ({@code leave}, 409 con 2) o se cambian todos de una vez ({@code PUT /members}). No se borra nada:
 * {@code DELETE} desactiva ({@code resources-delete}).
 */
@HttpExchange("/api/resources/units")
public interface OrganizationClient {

    /** El arbol entero, desde cada proyecto; el organigrama es pequeno. */
    @GetExchange("/tree")
    List<OrganizationTreeNodeDto> tree(@RequestParam("includeInactive") boolean includeInactive);

    @GetExchange("/{id}")
    OrganizationUnitDto findById(@PathVariable("id") UUID id);

    /** Las unidades que cuelgan de otra (sus hijas), por codigo y con el id para desempatar. */
    @GetExchange
    PageResponse<OrganizationUnitDto> search(@RequestParam(value = "parentId", required = false) UUID parentId,
                                             @RequestParam(value = "kind", required = false) OrganizationUnitKind kind,
                                             @RequestParam(value = "active", required = false) Boolean active,
                                             @RequestParam("page") int page, @RequestParam("size") int size,
                                             @RequestParam("sort") List<String> sort);

    default PageResponse<OrganizationUnitDto> children(UUID parentId, int page, int size) {
        return search(parentId, null, null, page, size, MasterFilters.withTieBreak(List.of("code,asc"), MasterFilters.BY_ID));
    }

    /** 201; 409 {@code UNI-409} si el codigo ya existe, {@code UNI-001} si rompe una regla del organigrama. */
    @PostExchange
    OrganizationUnitDto create(@RequestBody OrganizationUnitRequest request);

    @PatchExchange(value = "/{id}", contentType = MergePatch.MEDIA_TYPE)
    OrganizationUnitDto update(@PathVariable("id") UUID id, @RequestBody MergePatch<OrganizationUnitUpdateRequest> patch);

    /** Desactiva: 204; 409 {@code UNI-001} si tiene unidades activas debajo. */
    @DeleteExchange("/{id}")
    void deactivate(@PathVariable("id") UUID id);

    @GetExchange("/{id}/revisions")
    PageResponse<RevisionDto<OrganizationUnitDto>> revisions(@PathVariable("id") UUID id, @RequestParam("page") int page,
                                                             @RequestParam("size") int size);

    /** Los linemen de un equipo; con {@code includeFormer}, tambien los que salieron. */
    @GetExchange("/{id}/members")
    List<TeamMemberDto> members(@PathVariable("id") UUID id, @RequestParam("includeFormer") boolean includeFormer);

    @PutExchange("/{id}/members")
    OrganizationUnitDto replaceMembers(@PathVariable("id") UUID id, @RequestBody TeamMembersRequest request);

    @PostExchange("/{id}/members")
    OrganizationUnitDto addMember(@PathVariable("id") UUID id, @RequestBody TeamMemberRequest request);

    @PostExchange("/{id}/members/{personId}/leave")
    OrganizationUnitDto leave(@PathVariable("id") UUID id, @PathVariable("personId") UUID personId, @RequestBody TeamMemberLeaveRequest request);
}

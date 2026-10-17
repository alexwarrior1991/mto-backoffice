package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Un nodo del arbol del organigrama ({@code GET /units/tree}), con sus hijos; en un equipo, cuantos linemen tiene. */
public record OrganizationTreeNodeDto(UUID id, String code, String name, OrganizationUnitKind kind, WorkDiscipline discipline,
                                      PersonSummaryDto head, Set<Long> executionPackageIds, Integer linemen, Boolean active,
                                      List<OrganizationTreeNodeDto> children) {

    public OrganizationTreeNodeDto {
        executionPackageIds = executionPackageIds == null ? Set.of() : Set.copyOf(executionPackageIds);
        children = children == null ? List.of() : List.copyOf(children);
    }

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public UnitSummaryDto summary() {
        return new UnitSummaryDto(id, code, name, kind);
    }
}

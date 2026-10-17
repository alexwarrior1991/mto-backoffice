package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Una unidad del organigrama de mto-resources. Un equipo trae ademas sus linemen, si esta completo y
 * su supervisor y su jefe de supervisores (los jefes de las unidades de arriba mas cercanas).
 * {@code version} vuelve como se leyo en la modificacion: si otra persona guardo antes, 409
 * {@code CON-001}.
 */
public record OrganizationUnitDto(UUID id, String code, String name, OrganizationUnitKind kind, WorkDiscipline discipline,
                                  UnitSummaryDto parent, PersonSummaryDto head, Set<Long> executionPackageIds,
                                  VehicleSummaryDto defaultVehicle, List<TeamMemberDto> members, Integer linemen, Boolean complete,
                                  PersonSummaryDto supervisor, PersonSummaryDto chiefSupervisor, Boolean active, Long version) {

    public OrganizationUnitDto {
        executionPackageIds = executionPackageIds == null ? Set.of() : Set.copyOf(executionPackageIds);
        members = members == null ? List.of() : List.copyOf(members);
    }

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public boolean isTeam() {
        return kind == OrganizationUnitKind.TEAM;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    /** Los linemen de ahora, sin los que ya salieron. */
    public List<TeamMemberDto> currentMembers() {
        return members.stream().filter(TeamMemberDto::isCurrent).toList();
    }

    public UnitSummaryDto summary() {
        return new UnitSummaryDto(id, code, name, kind);
    }
}

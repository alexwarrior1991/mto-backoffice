package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * El alta de una unidad; lo vacio no viaja. Un equipo nace completo: con su jefe y de 2 a 6 linemen
 * en la misma peticion. La disciplina la dice un departamento; lo demas la hereda.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OrganizationUnitRequest(String code, String name, OrganizationUnitKind kind, WorkDiscipline discipline, UUID parentId,
                                      UUID headPersonId, Set<Long> executionPackageIds, UUID defaultVehicleId, List<UUID> memberPersonIds,
                                      LocalDate joinedOn) {
}

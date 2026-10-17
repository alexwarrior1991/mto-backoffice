package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Un equipo de mantenimiento, con los paquetes de ejecucion (ids de mto-configuration) en los que
 * trabaja. Uno sincronizado desde mto-resources ({@code synchronizedFromResources}) trae su unidad alli
 * ({@code sourceEntityId}), su jefe, su supervisor, sus linemen y su vehiculo por defecto, y no se edita
 * aqui: su {@code PUT} es 409 {@code TEA-001}. Las personas son las de mto-resources, con su forma.
 */
public record TeamDto(UUID id, String code, String name, String baseName, String vehicle, Boolean active, Set<Long> executionPackageIds,
                      Boolean synchronizedFromResources, UUID sourceEntityId, PersonSummaryDto leader, PersonSummaryDto supervisor,
                      List<PersonSummaryDto> members, UUID defaultVehicleId, AuditDto audit) {

    public TeamDto {
        executionPackageIds = executionPackageIds == null ? Set.of() : Set.copyOf(executionPackageIds);
        members = members == null ? List.of() : List.copyOf(members);
    }

    /** Un equipo local, de antes de mto-resources. */
    public TeamDto(UUID id, String code, String name, String baseName, String vehicle, Boolean active, Set<Long> executionPackageIds,
                   AuditDto audit) {
        this(id, code, name, baseName, vehicle, active, executionPackageIds, false, null, null, null, List.of(), null, audit);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    /** Es de mto-resources: aqui solo se lee. No es {@code isX()}: no es una propiedad que viaje. */
    public boolean fromResources() {
        return Boolean.TRUE.equals(synchronizedFromResources);
    }

    /** Lo que llevan una orden o un turno. */
    public TeamSummaryDto summary() {
        return new TeamSummaryDto(id, code, name, baseName);
    }

    public String label() {
        return name == null || name.isBlank() ? code : code + " - " + name;
    }
}

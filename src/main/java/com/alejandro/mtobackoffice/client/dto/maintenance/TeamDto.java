package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.util.Set;
import java.util.UUID;

/** Un equipo de mantenimiento, con los paquetes de ejecucion (ids de mto-configuration) en los que trabaja. */
public record TeamDto(UUID id, String code, String name, String baseName, String vehicle, Boolean active, Set<Long> executionPackageIds,
                      AuditDto audit) {

    public TeamDto {
        executionPackageIds = executionPackageIds == null ? Set.of() : Set.copyOf(executionPackageIds);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public String label() {
        return name == null || name.isBlank() ? code : code + " - " + name;
    }
}

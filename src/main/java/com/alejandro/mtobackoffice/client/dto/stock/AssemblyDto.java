package com.alejandro.mtobackoffice.client.dto.stock;

import com.alejandro.mtobackoffice.client.dto.AuditDto;
import java.util.List;
import java.util.UUID;

/** Un conjunto: un producto virtual definido por su lista de materiales; no tiene stock propio. */
public record AssemblyDto(UUID id, String code, String name, Boolean active, List<AssemblyComponentDto> components, AuditDto audit) {

    public AssemblyDto {
        components = components == null ? List.of() : List.copyOf(components);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    public AssemblySummaryDto summary() {
        return new AssemblySummaryDto(id, code, name, active);
    }
}

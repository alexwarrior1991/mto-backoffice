package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Objects;
import java.util.UUID;

/** Una unidad dentro de otra respuesta: el padre de una unidad. Es la misma si es el mismo id, como {@link PersonSummaryDto}. */
public record UnitSummaryDto(UUID id, String code, String name, OrganizationUnitKind kind) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnitSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

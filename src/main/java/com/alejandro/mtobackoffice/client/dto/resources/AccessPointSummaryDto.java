package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Objects;

/**
 * El punto de acceso a la via de una ubicacion ({@code trackAccessPoint}, V9 de mto-resources): por donde
 * encarrilo o desencarrilo, del maestro de mto-configuration. Igual por id.
 */
public record AccessPointSummaryDto(Long id, String name, AccessPointKind kind) {

    /** Su nombre, o {@code #id} si llega sin el. */
    public String label() {
        return name == null || name.isBlank() ? "#" + id : name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof AccessPointSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

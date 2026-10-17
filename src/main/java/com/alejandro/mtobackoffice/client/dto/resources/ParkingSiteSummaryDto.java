package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Objects;
import java.util.UUID;

/** Un sitio de aparcamiento dentro de una ubicacion. */
public record ParkingSiteSummaryDto(UUID id, String code, String name, ParkingSiteKind kind) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public static ParkingSiteSummaryDto of(ParkingSiteDto site) {
        return new ParkingSiteSummaryDto(site.id(), site.code(), site.name(), site.kind());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ParkingSiteSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.UUID;

/** El equipo tal y como viene dentro de una orden o un turno. */
public record TeamSummaryDto(UUID id, String code, String name, String baseName) {

    public String label() {
        return name == null || name.isBlank() ? code : code + " - " + name;
    }
}

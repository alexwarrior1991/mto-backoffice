package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.List;
import java.util.UUID;

/**
 * Una persona (o un usuario de Keycloak sin ficha, con {@code personId} nulo) cuyo perfil de puesto no
 * casa con su categoria: {@code expectedProfile} es el de su categoria y {@code profiles}, los que tiene.
 */
public record PositionDiscrepancyDto(UUID personId, String personCode, String fullName, String username, PersonCategory category,
                                     Boolean personActive, String expectedProfile, List<String> profiles) {

    public List<String> profilesOrEmpty() {
        return profiles == null ? List.of() : profiles;
    }

    /** Como se nombra la persona: su codigo y su nombre, o nada sin ficha. */
    public String personLabel() {
        if (personCode == null) {
            return fullName == null ? "" : fullName;
        }
        return fullName == null || fullName.isBlank() ? personCode : personCode + " · " + fullName;
    }
}

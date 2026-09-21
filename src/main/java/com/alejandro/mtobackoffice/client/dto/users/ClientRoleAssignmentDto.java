package com.alejandro.mtobackoffice.client.dto.users;

import java.util.List;

/** Los roles que alguien (o un perfil) tiene en un cliente. */
public record ClientRoleAssignmentDto(String clientId, List<String> roles) {

    public ClientRoleAssignmentDto {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}

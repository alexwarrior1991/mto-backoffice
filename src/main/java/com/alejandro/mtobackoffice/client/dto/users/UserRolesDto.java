package com.alejandro.mtobackoffice.client.dto.users;

import java.util.List;

/** Las asignaciones directas de una persona: roles de realm y roles por cliente. */
public record UserRolesDto(List<String> realmRoles, List<ClientRoleAssignmentDto> clientRoles) {

    public UserRolesDto {
        realmRoles = realmRoles == null ? List.of() : List.copyOf(realmRoles);
        clientRoles = clientRoles == null ? List.of() : List.copyOf(clientRoles);
    }
}

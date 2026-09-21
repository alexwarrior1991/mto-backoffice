package com.alejandro.mtobackoffice.client.dto.users;

import java.util.List;

/** Un perfil con lo que concede: roles por cliente y roles de realm. */
public record RealmProfileDto(String name, String description, List<ClientRoleAssignmentDto> clientRoles, List<String> realmRoles) {

    public RealmProfileDto {
        clientRoles = clientRoles == null ? List.of() : List.copyOf(clientRoles);
        realmRoles = realmRoles == null ? List.of() : List.copyOf(realmRoles);
    }
}

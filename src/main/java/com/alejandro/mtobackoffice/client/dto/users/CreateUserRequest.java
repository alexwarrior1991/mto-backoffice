package com.alejandro.mtobackoffice.client.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * El alta. Lo que va a {@code null} no viaja; {@code enabled} nulo es activo para el servicio, y la
 * contrasena temporal, si viene, obliga a cambiarla al entrar. {@code toString()} no la ensena:
 * una contrasena nunca llega a un log.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateUserRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean emailVerified,
        Boolean enabled,
        Map<String, List<String>> attributes,
        List<RequiredAction> requiredActions,
        String temporaryPassword
) {

    @Override
    public String toString() {
        return "CreateUserRequest[username=" + username + ", email=" + email + ", enabled=" + enabled
                + ", requiredActions=" + requiredActions + ", temporaryPassword=" + (temporaryPassword == null ? "null" : "***") + "]";
    }
}

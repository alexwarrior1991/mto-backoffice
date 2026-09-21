package com.alejandro.mtobackoffice.client.dto.users;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Un usuario del realm tal como lo devuelve mto-users; los atributos son los no gestionados del realm. */
public record UserDto(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        Boolean emailVerified,
        Boolean enabled,
        Instant createdAt,
        Map<String, List<String>> attributes,
        List<String> requiredActions
) {

    public UserDto {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        requiredActions = requiredActions == null ? List.of() : List.copyOf(requiredActions);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /** Nombre y apellidos, o el nombre de usuario si no hay nada mas. */
    public String fullName() {
        String name = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        return name.isEmpty() ? (username == null ? "" : username) : name;
    }
}

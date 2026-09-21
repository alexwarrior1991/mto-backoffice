package com.alejandro.mtobackoffice.client.dto.users;

/** Un cliente del realm cuyos roles se pueden asignar; {@code clientId} es el nombre, no el UUID. */
public record ClientDto(String clientId, String name, String description) {

    public String label() {
        return name == null || name.isBlank() ? clientId : name;
    }
}

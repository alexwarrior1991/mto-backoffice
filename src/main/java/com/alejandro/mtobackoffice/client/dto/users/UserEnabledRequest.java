package com.alejandro.mtobackoffice.client.dto.users;

/** Activar o desactivar. Desactivar no cierra sesiones ni revoca tokens offline. */
public record UserEnabledRequest(Boolean enabled) {
}

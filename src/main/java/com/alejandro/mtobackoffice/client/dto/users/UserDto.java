package com.alejandro.mtobackoffice.client.dto.users;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Un usuario del realm tal como lo devuelve mto-users; los atributos son los no gestionados del realm.
 *
 * <p>{@code serviceAccount} dice si es la cuenta de servicio de un cliente, que mto-users solo deja
 * leer (403 {@code USR-SVC-403}). Es un {@code Boolean} porque un servicio anterior no lo manda: sin
 * la clave no es una cuenta de servicio, y aqui no se adivina por el nombre.</p>
 */
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
        List<String> requiredActions,
        Boolean serviceAccount
) {

    /** El prefijo que Keycloak pone a la cuenta de servicio de un cliente; mto-users lo reserva en el alta. */
    public static final String SERVICE_ACCOUNT_PREFIX = "service-account-";

    public UserDto {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        requiredActions = requiredActions == null ? List.of() : List.copyOf(requiredActions);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /** La cuenta de servicio de un cliente: se lee, y ninguna escritura sobre ella se ofrece. */
    public boolean isServiceAccount() {
        return Boolean.TRUE.equals(serviceAccount);
    }

    /** Un nombre de usuario que mto-users rechaza en el alta, sin distinguir mayusculas. */
    public static boolean isReservedUsername(String username) {
        return username != null && username.trim().toLowerCase(Locale.ROOT).startsWith(SERVICE_ACCOUNT_PREFIX);
    }

    /** Nombre y apellidos, o el nombre de usuario si no hay nada mas. */
    public String fullName() {
        String name = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        return name.isEmpty() ? (username == null ? "" : username) : name;
    }
}

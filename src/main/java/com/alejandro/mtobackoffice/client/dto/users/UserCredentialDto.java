package com.alejandro.mtobackoffice.client.dto.users;

import java.time.Instant;

/** Una credencial sin su secreto ni su forma de almacenamiento: mto-users no los manda. */
public record UserCredentialDto(String id, String type, String userLabel, Instant createdAt) {

    public boolean isPassword() {
        return "password".equals(type);
    }

    public String typeLabel() {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case "password" -> "Contrasena";
            case "otp" -> "Segundo factor (OTP)";
            case "webauthn" -> "Llave de seguridad";
            case "webauthn-passwordless" -> "Llave sin contrasena";
            default -> type;
        };
    }
}

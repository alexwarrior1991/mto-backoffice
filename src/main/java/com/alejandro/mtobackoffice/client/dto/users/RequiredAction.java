package com.alejandro.mtobackoffice.client.dto.users;

import java.util.Arrays;
import java.util.List;

/** Las acciones requeridas de Keycloak que mto-users admite. */
public enum RequiredAction {
    UPDATE_PASSWORD("Cambiar la contrasena"),
    VERIFY_EMAIL("Verificar el email"),
    UPDATE_PROFILE("Completar el perfil"),
    CONFIGURE_TOTP("Configurar el segundo factor (OTP)"),
    TERMS_AND_CONDITIONS("Aceptar los terminos y condiciones");

    private final String label;

    RequiredAction(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /**
     * Si completar la accion pone o cambia una credencial (la contrasena o el segundo factor), como
     * {@code RequiredAction.touchesCredentials()} de mto-users: un correo de acciones con una de ellas
     * pide alli, ademas de {@code users-write}, {@code users-password-reset} (403 {@code AUTH-403}).
     * Un {@code switch} sin {@code default}, como alli: una accion nueva elige su lado.
     */
    public boolean touchesCredentials() {
        return switch (this) {
            case UPDATE_PASSWORD, CONFIGURE_TOTP -> true;
            case VERIFY_EMAIL, UPDATE_PROFILE, TERMS_AND_CONDITIONS -> false;
        };
    }

    /**
     * Lo que ofrece el correo de acciones: todo a quien puede ademas restablecer contrasenas, y a quien
     * no, solo lo que no toca una credencial, que es lo que el servicio le aceptaria.
     */
    public static List<RequiredAction> forActionsEmail(boolean canSetCredentials) {
        return Arrays.stream(values()).filter(action -> canSetCredentials || !action.touchesCredentials()).toList();
    }
}

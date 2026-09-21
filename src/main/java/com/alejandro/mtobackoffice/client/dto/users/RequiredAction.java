package com.alejandro.mtobackoffice.client.dto.users;

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
}

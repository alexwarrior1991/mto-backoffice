package com.alejandro.mtobackoffice.configuration.security;

/**
 * Los permisos del modulo de notificaciones: los roles del cliente {@code mto-notification-api}
 * tal como los emite {@link KeycloakRoleMapper} ({@code notification-inbox} llega como
 * {@code ROLE_NOTIFICATION_INBOX}). Ninguno implica otro, y en ese servicio el permiso lo decide el
 * recurso, no el verbo: la bandeja es de quien la lee, el registro pide el suyo y los accesos
 * —usuario e IP— piden uno aparte, que no viene con el registro. {@code notification-admin} abre
 * la administracion del servicio (reglas, entregas, fuentes), que todavia no tiene pantalla aqui.
 */
public final class NotificationRoles {

    /** Mi bandeja: leer, el contador de la campana y marcar como leidas. */
    public static final String NOTIFICATION_INBOX = "NOTIFICATION_INBOX";

    /** El registro de actividad, todo menos los accesos. */
    public static final String NOTIFICATION_ACTIVITY_READ = "NOTIFICATION_ACTIVITY_READ";

    /** Los accesos: logins, fallos, logouts y bloqueos, con usuario e IP. */
    public static final String NOTIFICATION_ACCESS_READ = "NOTIFICATION_ACCESS_READ";

    /** Administracion del servicio; sin pantalla todavia. */
    public static final String NOTIFICATION_ADMIN = "NOTIFICATION_ADMIN";

    private NotificationRoles() {
    }
}

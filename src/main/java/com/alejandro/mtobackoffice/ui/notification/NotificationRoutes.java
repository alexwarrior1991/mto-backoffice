package com.alejandro.mtobackoffice.ui.notification;

/**
 * Las rutas del modulo de notificaciones: la bandeja, y el registro de actividad con los accesos
 * colgando de el (el grupo «Actividad» del menu, cuyo nodo es el propio registro). Son tambien las
 * rutas que las reglas de mto-notification ponen como enlace de cada aviso.
 */
public final class NotificationRoutes {

    public static final String INBOX = "notificaciones";
    public static final String ACTIVITY = "actividad";
    public static final String ACCESS = ACTIVITY + "/accesos";

    private NotificationRoutes() {
    }
}

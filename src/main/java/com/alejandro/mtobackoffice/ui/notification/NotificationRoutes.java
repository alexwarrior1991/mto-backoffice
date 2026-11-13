package com.alejandro.mtobackoffice.ui.notification;

/**
 * Las rutas del modulo de notificaciones: la bandeja con la administracion del servicio colgando de
 * ella (el grupo «Notificaciones», cuyo nodo es la propia bandeja), y el registro de actividad con los accesos
 * colgando de el (el grupo «Actividad» del menu, cuyo nodo es el propio registro). Son tambien las
 * rutas que las reglas de mto-notification ponen como enlace de cada aviso.
 */
public final class NotificationRoutes {

    public static final String INBOX = "notificaciones";
    /** La administracion del servicio: cuelga de la bandeja, que es el nodo del grupo «Notificaciones». */
    public static final String ADMIN = INBOX + "/administracion";
    public static final String ACTIVITY = "actividad";
    public static final String ACCESS = ACTIVITY + "/accesos";

    private NotificationRoutes() {
    }
}

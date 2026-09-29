package com.alejandro.mtobackoffice.ui.notification;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.InvalidLocationException;
import com.vaadin.flow.router.QueryParameters;

/**
 * Abre el enlace de una notificacion. Las reglas de mto-notification ponen rutas de esta
 * aplicacion, con barra inicial y a veces con parametros ({@code /mantenimiento/ordenes/{id}},
 * {@code /actividad?category=SYSTEM}, {@code /actividad/accesos?username=...}): se navega a la
 * ruta con sus parametros, sin pasar por el navegador. Un enlace absoluto ({@code http(s)://}) se
 * abre en una pestana nueva; cualquier otra cosa se trata como ruta y, si Vaadin no la admite, se
 * dice y no se abre.
 */
public final class NotificationLinks {

    private NotificationLinks() {
    }

    public static void open(UI ui, String link) {
        String target = link == null ? "" : link.trim();
        if (target.isEmpty()) {
            return;
        }
        if (target.startsWith("http://") || target.startsWith("https://")) {
            ui.getPage().open(target);
            return;
        }
        String path = target.startsWith("/") ? target.substring(1) : target;
        int query = path.indexOf('?');
        String route = query < 0 ? path : path.substring(0, query);
        QueryParameters parameters = query < 0 ? QueryParameters.empty() : QueryParameters.fromString(path.substring(query + 1));
        try {
            ui.navigate(route, parameters);
        } catch (InvalidLocationException | IllegalArgumentException invalid) {
            Notification.show("El enlace de la notificacion no es valido: " + target, 8000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}

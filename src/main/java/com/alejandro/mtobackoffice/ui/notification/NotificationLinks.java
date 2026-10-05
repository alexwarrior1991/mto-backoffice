package com.alejandro.mtobackoffice.ui.notification;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.InvalidLocationException;
import com.vaadin.flow.router.QueryParameters;

import java.util.Optional;

/**
 * Abre el enlace de una notificacion. Las reglas de mto-notification ponen rutas de esta
 * aplicacion, con barra inicial y a veces con parametros ({@code /mantenimiento/ordenes/{id}},
 * {@code /actividad?category=SYSTEM}, {@code /actividad/accesos?username=...}): se navega a la
 * ruta con sus parametros, sin pasar por el navegador. Como en mto-frontend ({@link #target}), una
 * ruta empieza por una sola barra, un enlace {@code http(s)://} se abre en otra pestana con
 * {@code noopener}, y cualquier otra cosa no es un destino: no se ofrece.
 */
public final class NotificationLinks {

    private NotificationLinks() {
    }

    /** A donde lleva un enlace: una ruta de esta aplicacion, una direccion absoluta, o ningun sitio. */
    public record Target(String link, boolean external) {
    }

    public static Optional<Target> target(String link) {
        String value = link == null ? "" : link.trim();
        if (value.startsWith("http://") || value.startsWith("https://")) {
            return Optional.of(new Target(value, true));
        }
        if (value.startsWith("/") && !value.startsWith("//")) {
            return Optional.of(new Target(value, false));
        }
        return Optional.empty();
    }

    public static void open(UI ui, String link) {
        target(link).ifPresent(target -> open(ui, target));
    }

    private static void open(UI ui, Target destination) {
        String target = destination.link();
        if (destination.external()) {
            // Sin noopener la pestana nueva podria tocar esta con window.opener.
            ui.getPage().executeJs("window.open($0, '_blank', 'noopener')", target);
            return;
        }
        String path = target.substring(1);
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

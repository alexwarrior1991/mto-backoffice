package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.component.UI;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Objects;

/**
 * La zona horaria en la que se pintan y se escriben las fechas: la del navegador de la persona, como
 * en mto-frontend, y no la del servidor (el contenedor corre en UTC). Una hora escrita aqui es la
 * misma hora en la SPA.
 *
 * <p>Vaadin 25 lee los datos del navegador de la peticion de arranque, antes de navegar, asi que
 * {@code ExtendedClientDetails.getTimeZoneId()} ya esta cuando una vista se pinta. Fuera de una UI
 * (un hilo de fondo sin {@code UI.access()}) o si el navegador no la dice, vale la zona por defecto
 * ({@code app.display.time-zone}, {@code DisplayTimeZoneConfiguration}).</p>
 */
public final class ClientTimeZone {

    /** La de mto-maintenance para sus informes ({@code app.reports.time-zone}); la configuracion la cambia. */
    public static final ZoneId DEFAULT = ZoneId.of("Asia/Jerusalem");

    private static volatile ZoneId fallback = DEFAULT;

    private ClientTimeZone() {
    }

    /** La de la UI en curso, o la de por defecto. */
    public static ZoneId current() {
        UI ui = UI.getCurrent();
        return ui == null ? fallback : of(ui);
    }

    /** La del navegador de esa UI, o la de por defecto si no la ha dicho o no es una zona valida. */
    public static ZoneId of(UI ui) {
        var details = ui.getPage().getExtendedClientDetails();
        String id = details == null ? null : details.getTimeZoneId();
        if (id == null || id.isBlank()) {
            return fallback;
        }
        try {
            return ZoneId.of(id.trim());
        } catch (DateTimeException unknown) {
            return fallback;
        }
    }

    /** La zona de por defecto. */
    public static ZoneId fallback() {
        return fallback;
    }

    /** La fija la configuracion al arrancar. */
    public static void useAsFallback(ZoneId zone) {
        fallback = Objects.requireNonNull(zone);
    }
}

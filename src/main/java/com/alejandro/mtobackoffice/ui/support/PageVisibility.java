package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;

/**
 * Si la pestana del navegador de una UI se ve. Lo que se vuelve a preguntar al servicio desde
 * {@link SharedPolling} (los trabajos en curso y el contador de la campana) no pregunta con la
 * pestana oculta, igual que mto-frontend: nadie lo esta mirando, y en cuanto se vuelve a ver, la
 * siguiente pasada lo pone al dia.
 *
 * <p>El navegador lo avisa con {@code visibilitychange} en {@code document}, que no llega a ningun
 * elemento; el script de {@link #of(UI)} lo reenvia al {@code <body>}, que es el elemento de la UI.
 * El estado lo lee el hilo de la consulta sin el bloqueo de la sesion, por eso es {@code volatile}.
 * Hasta el primer aviso, la pestana se da por vista.</p>
 */
public final class PageVisibility {

    static final String EVENT = "mto-visibility";
    private static final String HIDDEN = "event.detail.hidden";

    private volatile boolean shown = true;

    private PageVisibility() {
    }

    /** La de esta UI; la primera vez se apunta a lo que avisa el navegador. En el hilo de la UI. */
    public static PageVisibility of(UI ui) {
        PageVisibility visibility = ComponentUtil.getData(ui, PageVisibility.class);
        if (visibility == null) {
            PageVisibility created = new PageVisibility();
            ComponentUtil.setData(ui, PageVisibility.class, created);
            ui.getElement().addEventListener(EVENT, event -> created.changed(!event.getEventData().path(HIDDEN).asBoolean(false)))
                    .addEventData(HIDDEN);
            ui.getElement().executeJs("""
                    const body = this;
                    document.addEventListener('visibilitychange',
                        () => body.dispatchEvent(new CustomEvent($0, {detail: {hidden: document.hidden}})));
                    """, EVENT);
            visibility = created;
        }
        return visibility;
    }

    /** Si la pestana se ve; se puede leer desde cualquier hilo. */
    public boolean isShown() {
        return shown;
    }

    /** Lo que avisa el navegador; los tests lo llaman directamente. */
    public void changed(boolean shown) {
        this.shown = shown;
    }
}

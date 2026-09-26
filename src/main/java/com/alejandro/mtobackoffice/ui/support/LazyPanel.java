package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Una pestana de una ficha que solo pide sus datos la primera vez que alguien la abre: abrir una
 * ficha con cuatro pestanas no tiene por que costar cinco llamadas. La usan la ficha de un usuario
 * y las de mantenimiento (orden, turno, inspeccion, defecto).
 */
public abstract class LazyPanel extends VerticalLayout {

    private boolean loaded;

    protected LazyPanel() {
        setPadding(false);
    }

    public final void ensureLoaded() {
        if (!loaded) {
            reload();
        }
    }

    public final void reload() {
        loaded = true;
        load();
    }

    /** Vuelve a pedir los datos solo si ya se habian pedido: si no, los pedira al abrirse. */
    public final void reloadIfLoaded() {
        if (loaded) {
            reload();
        }
    }

    protected abstract void load();
}

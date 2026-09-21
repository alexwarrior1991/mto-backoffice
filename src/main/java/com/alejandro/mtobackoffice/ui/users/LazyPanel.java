package com.alejandro.mtobackoffice.ui.users;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Una pestana de la ficha que solo pide sus datos la primera vez que alguien la abre: la ficha
 * tiene cuatro y abrirla no tiene por que costar cinco llamadas.
 */
abstract class LazyPanel extends VerticalLayout {

    private boolean loaded;

    protected LazyPanel() {
        setPadding(false);
    }

    final void ensureLoaded() {
        if (!loaded) {
            reload();
        }
    }

    final void reload() {
        loaded = true;
        load();
    }

    protected abstract void load();
}

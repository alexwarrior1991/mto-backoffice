package com.alejandro.mtobackoffice.configuration.security;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Deja el nombre del principal en la sesion de Vaadin en cuanto nace (la sesion y cada UI se
 * crean dentro de una peticion ya autenticada), para que {@link CurrentPrincipal} lo encuentre
 * desde un hilo sin peticion ni contexto de seguridad.
 */
@Component
public class PrincipalSessionRecorder implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(init -> CurrentPrincipal.remember(init.getSession()));
        event.getSource().addUIInitListener(init -> CurrentPrincipal.remember(init.getUI().getSession()));
    }
}

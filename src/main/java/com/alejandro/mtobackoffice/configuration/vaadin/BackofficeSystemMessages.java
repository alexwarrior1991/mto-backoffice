package com.alejandro.mtobackoffice.configuration.vaadin;

import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Los mensajes de sistema de Vaadin, en castellano y sin el dialogo de sesion caducada.
 *
 * <p>La sesion de esta aplicacion vive en memoria: un reinicio la pierde, y con ella el cliente
 * autorizado con el token de la persona. Con el aviso por defecto, la pantalla abierta se quedaba
 * con un dialogo de "sesion caducada" que solo servia para pulsarlo. Sin el aviso, Vaadin recarga
 * la pagina en cuanto detecta que la sesion ya no esta; la cadena de seguridad manda al login de
 * Keycloak y, con la sesion de Keycloak aun viva, la persona vuelve a la misma pantalla sin
 * escribir nada. Es la reentrada que cabe sin persistir sesiones: serializar la sesion de Vaadin
 * es fragil y el almacen de tokens se perderia igual.</p>
 */
@Component
public class BackofficeSystemMessages implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().setSystemMessagesProvider(info -> messages());
    }

    static CustomizedSystemMessages messages() {
        CustomizedSystemMessages messages = new CustomizedSystemMessages();
        // Sin dialogo: recarga, y la cadena de seguridad hace el resto (SSO de Keycloak).
        messages.setSessionExpiredNotificationEnabled(false);
        messages.setSessionExpiredURL(null);
        messages.setSessionExpiredCaption("Sesion caducada");
        messages.setSessionExpiredMessage("Vuelve a entrar para continuar.");
        messages.setInternalErrorCaption("Error interno");
        messages.setInternalErrorMessage("Algo ha fallado en el servidor. Pulsa aqui o recarga la pagina para continuar.");
        messages.setCookiesDisabledCaption("Cookies deshabilitadas");
        messages.setCookiesDisabledMessage("La aplicacion necesita cookies para mantener la sesion. Habilitalas y recarga la pagina.");
        return messages;
    }
}

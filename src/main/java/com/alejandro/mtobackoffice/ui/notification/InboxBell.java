package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.UnreadCountDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.security.CurrentPrincipal;
import com.alejandro.mtobackoffice.ui.support.SharedPolling;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

/**
 * La campana de la barra: cuantas notificaciones tiene la persona sin leer, y el camino a su
 * bandeja. Solo la tiene quien puede leer la bandeja ({@code notification-inbox}).
 *
 * <p>El contador se pide al entrar en cada pantalla y, mientras hay una abierta, cada
 * {@link #REFRESH_PERIOD} desde el hilo compartido ({@link SharedPolling}), con el principal fijado
 * y llevando el numero a la pantalla con {@code UI.access()}: es {@code @Push}, no un sondeo del
 * navegador. La bandeja lo refresca ademas al marcar algo como leido. El servicio acota el numero
 * ({@code capped}: «ese o mas»), y aqui se pinta con un {@code +}. Un fallo al pedirlo no molesta:
 * el numero se queda como estaba y la bandeja dira lo que pasa al abrirse.</p>
 */
public class InboxBell extends Div {

    public static final String ID = "inbox-bell";
    public static final String COUNT_ID = "inbox-bell-count";
    /** Cada cuanto se vuelve a pedir el contador mientras hay una pantalla abierta. */
    static final Duration REFRESH_PERIOD = Duration.ofSeconds(30);
    private static final Logger LOGGER = LoggerFactory.getLogger(InboxBell.class);

    private final NotificationClient client;
    private final SharedPolling polling;
    private final String principal;
    private final Button button;
    private final Span badge = new Span();
    private ScheduledFuture<?> ticker;

    public InboxBell(NotificationClient client, SharedPolling polling, String principal) {
        this.client = client;
        this.polling = polling;
        this.principal = principal;
        button = new Button(VaadinIcon.BELL.create(), click -> UI.getCurrent().navigate(NotificationsView.class));
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        button.setTooltipText("Notificaciones");
        button.setAriaLabel("Notificaciones");
        button.setId(ID);
        badge.setId(COUNT_ID);
        badge.getElement().getThemeList().add("badge error pill small");
        badge.setVisible(false);
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER);
        add(button, badge);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refresh();
        UI ui = attachEvent.getUI();
        ticker = polling.every(REFRESH_PERIOD, () -> poll(ui));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (ticker != null) {
            ticker.cancel(false);
            ticker = null;
        }
        super.onDetach(detachEvent);
    }

    /** Vuelve a pedir el contador y lo pinta; en el hilo de una peticion o dentro de {@code UI.access()}. */
    public void refresh() {
        show(fetch());
    }

    /** Una pasada en el hilo compartido: pide fuera del bloqueo de la sesion y pinta con {@code UI.access()}. */
    void poll(UI ui) {
        UnreadCountDto count = fetch();
        if (count != null) {
            ui.access(() -> show(count));
        }
    }

    /** Para los tests: una pasada con la UI de esta campana. */
    public void pollOnce() {
        getUI().ifPresent(this::poll);
    }

    private UnreadCountDto fetch() {
        if (principal == null) {
            return null;
        }
        try {
            return CurrentPrincipal.callAs(principal, client::unreadCount);
        } catch (BackofficeApiException failure) {
            // Cada 30 s y por cada pantalla abierta: con el servicio caido, un WARN por pasada llenaria
            // el log sin decir nada nuevo. La bandeja lo dira, con su referencia, al abrirse.
            LOGGER.debug("No se ha podido consultar el contador de notificaciones: {}", failure.getMessage());
            return null;
        }
    }

    private void show(UnreadCountDto count) {
        if (count == null) {
            return;
        }
        String text = count.capped() ? count.count() + "+" : String.valueOf(count.count());
        badge.setText(text);
        badge.setVisible(count.count() > 0);
        button.setTooltipText(count.count() == 0 ? "Notificaciones: nada sin leer" : "Notificaciones: " + text + " sin leer");
    }

    /** La campana de la UI en curso, si la persona la tiene: la bandeja la refresca al marcar leidas. */
    public static Optional<InboxBell> find(UI ui) {
        if (ui == null) {
            return Optional.empty();
        }
        return descendants(ui).filter(InboxBell.class::isInstance).map(InboxBell.class::cast).findFirst();
    }

    private static Stream<Component> descendants(Component component) {
        return component.getChildren().flatMap(child -> Stream.concat(Stream.of(child), descendants(child)));
    }
}

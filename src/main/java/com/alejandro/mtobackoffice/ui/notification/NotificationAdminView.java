package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.security.NotificationRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/**
 * La administracion de mto-notification ({@code /api/notifications/admin/**}, toda con
 * {@code notification-admin}): como estan sus fuentes, que guardan sus colas de mensajes muertos (y
 * reprocesarlas), las entregas de correo (y reintentar las que no salieron), las reglas cargadas y
 * los avisos manuales. Cada pestana es un {@link LazyPanel}: pide sus datos la primera vez que se
 * abre, y la pantalla, al entrar, solo los de la primera.
 *
 * <p>Aqui no se decide nada: lo que una cola o una entrega admite lo dice el servicio, y lo unico
 * que se copia de el es lo justo para no ofrecer lo que va a fallar.</p>
 */
@Route(value = NotificationRoutes.ADMIN, layout = MainLayout.class)
@PageTitle(NotificationAdminView.TITLE)
@Menu(title = "Administracion", order = 81, icon = "vaadin:cogs")
@RolesAllowed(NotificationRoles.NOTIFICATION_ADMIN)
public class NotificationAdminView extends VerticalLayout {

    public static final String TITLE = "Administracion de notificaciones";
    public static final String SOURCES_TAB = "Fuentes";
    public static final String DEAD_LETTERS_TAB = "Mensajes muertos (DLQ)";
    public static final String DELIVERIES_TAB = "Entregas";
    public static final String RULES_TAB = "Reglas";
    public static final String BROADCASTS_TAB = "Avisos manuales";

    public NotificationAdminView(NotificationClient client) {
        setSizeFull();
        TabSheet tabs = new TabSheet();
        tabs.setId("notification-admin-tabs");
        tabs.setSizeFull();
        AdminDeliveriesPanel deliveries = new AdminDeliveriesPanel(client);
        tabs.add(SOURCES_TAB, new AdminSourcesPanel(client));
        tabs.add(DEAD_LETTERS_TAB, new AdminDeadLettersPanel(client));
        tabs.add(DELIVERIES_TAB, deliveries);
        tabs.add(RULES_TAB, new AdminRulesPanel(client));
        // Un correo de prueba o un aviso por correo dejan entregas: si esa pestana ya se abrio, se relee.
        tabs.add(BROADCASTS_TAB, new AdminBroadcastsPanel(client, deliveries::reloadIfLoaded));
        tabs.addSelectedChangeListener(change -> loadTab(tabs, change.getSelectedTab()));
        add(new H2(TITLE), tabs);
        expand(tabs);
        loadTab(tabs, tabs.getSelectedTab());
    }

    private static void loadTab(TabSheet tabs, Tab tab) {
        if (tab != null && tabs.getComponent(tab) instanceof LazyPanel panel) {
            panel.ensureLoaded();
        }
    }
}

package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.AdminNotificationDto;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 * Los avisos que manda una persona y no una regla: el correo de prueba ({@code POST /admin/test-email}),
 * para comprobar que el canal funciona, y el aviso general ({@code POST /admin/broadcasts}), que llega
 * a personas de verdad. Esta pestana no pide nada al abrirse; dice lo ultimo que se mando desde ella.
 */
class AdminBroadcastsPanel extends LazyPanel {

    private final Span last = new Span();

    /** @param onSent que hacer tras mandar algo: releer las entregas, si esa pestana ya se abrio */
    AdminBroadcastsPanel(NotificationClient client, Runnable onSent) {
        Button testEmail = new Button("Correo de prueba", VaadinIcon.ENVELOPE_O.create(),
                click -> new TestEmailDialog(client, sent -> sent(sent, onSent)).open());
        testEmail.setId("admin-test-email");
        Button broadcast = new Button("Aviso general", VaadinIcon.MEGAPHONE.create(),
                click -> new BroadcastDialog(client, sent -> sent(sent, onSent)).open());
        broadcast.setId("admin-broadcast");
        last.setId("admin-broadcasts-last");
        last.setVisible(false);
        add(new Paragraph("El correo de prueba manda un mensaje a la direccion que digas y te deja la notificacion en tu bandeja: sirve"
                        + " para comprobar el canal de correo. El aviso general lo reciben las audiencias que elijas, por la bandeja, por"
                        + " correo o por los dos."),
                new HorizontalLayout(testEmail, broadcast), last);
    }

    private void sent(AdminNotificationDto notification, Runnable onSent) {
        last.setText("Ultimo enviado: «" + notification.title() + "»" + (notification.createdAt() == null ? "" : ", el " + Formats.dateTime(notification.createdAt()))
                + (notification.audiences().isEmpty() ? "" : ", a " + String.join(", ", notification.audiences())));
        last.setVisible(true);
        onSent.run();
    }

    @Override
    protected void load() {
        // Nada que pedir: aqui solo se manda.
    }
}

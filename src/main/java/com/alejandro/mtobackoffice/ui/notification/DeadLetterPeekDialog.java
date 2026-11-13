package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterPeekDto;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Los primeros mensajes de una cola de mensajes muertos, que siguen en ella: de donde venia cada uno,
 * por que lo aparto el broker, por que no llegaria al inbox ({@code problem}) y lo que el inbox guarda
 * de el. Nunca el cuerpo: el servicio no lo da (un mensaje descartado por su firma puede traer
 * cualquier cosa).
 */
public class DeadLetterPeekDialog extends Dialog {

    static final String NO_INBOX = "Sin registro en el inbox";

    public DeadLetterPeekDialog(DeadLetterPeekDto peek) {
        setHeaderTitle("Mensajes muertos de " + peek.source());
        setWidth("min(90rem, 96vw)");
        setHeight("min(40rem, 90vh)");

        Span summary = new Span(summary(peek));
        summary.setId("peek-summary");

        Grid<DeadLetterPeekDto.PeekedDto> grid = new Grid<>();
        grid.setId("peek-grid");
        grid.addColumn(row -> text(row.message().messageId())).setHeader("Mensaje").setKey("messageId").setAutoWidth(true);
        grid.addColumn(row -> text(row.message().eventType())).setHeader("Tipo").setKey("eventType").setAutoWidth(true);
        grid.addColumn(row -> text(row.message().routingKey())).setHeader("Clave de enrutado").setKey("routingKey").setAutoWidth(true);
        grid.addColumn(row -> text(row.message().exchange())).setHeader("Exchange").setKey("exchange").setAutoWidth(true);
        grid.addColumn(row -> text(row.message().sourceService())).setHeader("Origen").setKey("sourceService").setAutoWidth(true);
        grid.addColumn(row -> aggregate(row.message())).setHeader("Agregado").setKey("aggregate").setAutoWidth(true);
        grid.addColumn(row -> death(row.message())).setHeader("Motivo del broker").setKey("death").setAutoWidth(true);
        grid.addColumn(row -> Formats.dateTime(row.message().deadLetteredAt())).setHeader("Apartado").setKey("deadLetteredAt").setAutoWidth(true);
        grid.addColumn(row -> row.message().payloadBytes() == null ? "" : row.message().payloadBytes() + " B").setHeader("Tamano").setKey("payloadBytes")
                .setAutoWidth(true);
        grid.addColumn(row -> text(row.message().problem())).setHeader("Problema").setKey("problem").setFlexGrow(1);
        grid.addColumn(row -> inbox(row.inbox())).setHeader("Inbox").setKey("inbox").setFlexGrow(1);
        grid.setItems(peek.peeked());
        grid.setSizeFull();

        VerticalLayout layout = new VerticalLayout(summary, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.expand(grid);
        add(layout);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    static String summary(DeadLetterPeekDto peek) {
        String held = peek.messages() == null ? "" : peek.messages() + " en la cola; ";
        return peek.deadLetterQueue() + ": " + held + "se ensenan los primeros " + peek.peeked().size()
                + ", que siguen en ella. El cuerpo de un mensaje no se ensena nunca.";
    }

    private static String aggregate(DeadLetterPeekDto.MessageDto message) {
        if (message.aggregateType() == null) {
            return text(message.aggregateId());
        }
        return message.aggregateId() == null ? message.aggregateType() : message.aggregateType() + " " + message.aggregateId();
    }

    /** {@code rejected x3}: por que lo aparto el broker y cuantas veces. */
    private static String death(DeadLetterPeekDto.MessageDto message) {
        String reason = text(message.deathReason());
        return message.deathCount() == null || message.deathCount() <= 1 ? reason : (reason + " x" + message.deathCount()).trim();
    }

    /** Lo que el inbox guarda del mensaje: su estado, sus intentos, cuando fallo y por que. */
    static String inbox(DeadLetterPeekDto.InboxRecordDto inbox) {
        if (inbox == null) {
            return NO_INBOX;
        }
        List<String> parts = new ArrayList<>();
        if (inbox.status() != null) {
            parts.add(inbox.status().label());
        }
        if (inbox.attempts() != null) {
            parts.add(inbox.attempts() + (inbox.attempts() == 1 ? " intento" : " intentos"));
        }
        if (inbox.failedAt() != null) {
            parts.add("fallo el " + Formats.dateTime(inbox.failedAt()));
        }
        if (inbox.failureReason() != null && !inbox.failureReason().isBlank()) {
            parts.add(inbox.failureReason());
        }
        return String.join(" · ", parts);
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}

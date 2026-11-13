package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterQueuesDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.RowActions;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;

/**
 * Las colas de mensajes muertos (DLQ) de las fuentes de RabbitMQ ({@code GET /admin/dead-letters}):
 * lo que no se pudo aplicar y el broker aparto. Por fuente, «Mirar» ensena los primeros mensajes sin
 * sacarlos y «Reprocesar» los pasa otra vez por su consumidor; las dos solo donde hay algo (una cola
 * que el broker no tiene seria el 409 {@code SRC-409} del servicio, y una vacia no tiene nada). Con
 * el broker apagado en el despliegue ({@code enabled: false}) se dice y no hay acciones.
 */
class AdminDeadLettersPanel extends LazyPanel {

    static final String ACTIONS_COLUMN = "actions";
    static final String DISABLED = "RabbitMQ esta apagado en este despliegue: no hay colas de mensajes muertos que mirar ni reprocesar.";
    static final String NO_QUEUE = "Sin cola en el broker";
    /** Cuantos mensajes ensena «Mirar»: los primeros, el lote por defecto del servicio. */
    static final int PEEK = 20;

    private final NotificationClient client;
    private final Span disabled = new Span(DISABLED);
    private final Grid<DeadLetterQueuesDto.QueueDto> grid = new Grid<>();

    AdminDeadLettersPanel(NotificationClient client) {
        this.client = client;
        setSizeFull();
        Button refresh = new Button("Actualizar", VaadinIcon.REFRESH.create(), click -> reload());
        refresh.setId("admin-dlq-refresh");
        disabled.setId("admin-dlq-disabled");
        disabled.setVisible(false);
        Span hint = new Span("Lo que una fuente publico y no se pudo aplicar. Mirar no saca nada de la cola; reprocesar si.");
        HorizontalLayout toolbar = new HorizontalLayout(hint, refresh);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(hint);

        grid.setId("admin-dlq-grid");
        grid.addColumn(DeadLetterQueuesDto.QueueDto::source).setHeader("Fuente").setKey("source").setAutoWidth(true);
        grid.addColumn(DeadLetterQueuesDto.QueueDto::queue).setHeader("Cola").setKey("queue").setFlexGrow(1);
        grid.addColumn(DeadLetterQueuesDto.QueueDto::deadLetterQueue).setHeader("Cola de mensajes muertos").setKey("deadLetterQueue").setFlexGrow(1);
        grid.addColumn(row -> row.messages() == null ? NO_QUEUE : String.valueOf(row.messages())).setHeader("Mensajes").setKey("messages")
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();

        add(toolbar, disabled, grid);
        expand(grid);
    }

    private Component rowActions(DeadLetterQueuesDto.QueueDto queue) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (queue.hasMessages()) {
            actions.add(RowActions.button("peek-" + queue.source(), VaadinIcon.EYE, "Mirar los mensajes de " + queue.source(), click -> peek(queue)));
            actions.add(RowActions.button("reprocess-" + queue.source(), VaadinIcon.ROTATE_RIGHT, "Reprocesar los mensajes de " + queue.source(),
                    click -> new DeadLetterReprocessDialog(client, queue, this::reload).open()));
        }
        return actions;
    }

    /** Una llamada; si falla (la fuente ya no existe, el broker no responde) se avisa y no se abre nada. */
    private void peek(DeadLetterQueuesDto.QueueDto queue) {
        try {
            new DeadLetterPeekDialog(client.peekDeadLetters(queue.source(), PEEK)).open();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    @Override
    protected void load() {
        try {
            DeadLetterQueuesDto queues = client.deadLetterQueues();
            disabled.setVisible(!queues.enabled());
            grid.setVisible(queues.enabled());
            grid.setItems(queues.queues());
        } catch (BackofficeApiException failure) {
            disabled.setVisible(false);
            grid.setVisible(true);
            grid.setItems(List.of());
            UiErrors.show(failure);
        }
    }
}

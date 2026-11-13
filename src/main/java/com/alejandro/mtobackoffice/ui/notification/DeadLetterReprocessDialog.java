package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterQueuesDto;
import com.alejandro.mtobackoffice.client.dto.notification.DeadLetterReprocessDto;
import com.alejandro.mtobackoffice.client.dto.notification.ReprocessStop;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Validator;

/**
 * Reprocesar la cola de mensajes muertos de una fuente
 * ({@code POST /admin/dead-letters/{source}/reprocess?max=}): el dialogo dice que va a pasar, pide
 * cuantos mensajes como mucho (de 1 a 100, 20 de partida) y, al terminar, ensena en el mismo sitio
 * que fue de cada uno. Un fallo ({@code SRC-404}, {@code SRC-409}, {@code SRC-503}) se avisa con el
 * dialogo abierto. Nada lo repite solo: cada pasada es un clic.
 */
public class DeadLetterReprocessDialog extends Dialog {

    public static final int MIN = 1;
    public static final int MAX = 100;
    public static final int DEFAULT = 20;
    static final String RANGE = "Entre " + MIN + " y " + MAX + " mensajes";
    static final String RUN_ID = "reprocess-run";

    private final NotificationClient client;
    private final DeadLetterQueuesDto.QueueDto queue;
    private final Runnable onDone;
    private final IntegerField max = Numbers.atLeast(new IntegerField("Maximo de mensajes"), MIN, RANGE);
    private final Span summary = new Span();
    private final Grid<DeadLetterReprocessDto.ResultDto> results = new Grid<>();

    /** @param onDone que hacer tras una pasada: releer las colas, que ya no guardan lo mismo */
    public DeadLetterReprocessDialog(NotificationClient client, DeadLetterQueuesDto.QueueDto queue, Runnable onDone) {
        this.client = client;
        this.queue = queue;
        this.onDone = onDone;
        setHeaderTitle("Reprocesar los mensajes muertos de " + queue.source());
        setWidth("min(70rem, 96vw)");
        setCloseOnOutsideClick(false);

        Paragraph explanation = new Paragraph(explanation(queue));
        explanation.setId("reprocess-explanation");
        max.setId("reprocess-max");
        max.setMax(MAX);
        max.getI18n().setMaxErrorMessage(RANGE);
        max.setValue(DEFAULT);
        max.setStepButtonsVisible(true);
        max.setHelperText(RANGE);
        summary.setId("reprocess-summary");
        summary.setVisible(false);

        results.setId("reprocess-results");
        results.addColumn(row -> text(row.messageId())).setHeader("Mensaje").setKey("messageId").setAutoWidth(true);
        results.addColumn(row -> text(row.eventType())).setHeader("Tipo").setKey("eventType").setAutoWidth(true);
        results.addColumn(row -> text(row.routingKey())).setHeader("Clave de enrutado").setKey("routingKey").setAutoWidth(true);
        results.addColumn(row -> row.outcome() == null ? "" : row.outcome().label()).setHeader("Resultado").setKey("outcome").setAutoWidth(true);
        results.addColumn(row -> text(row.reason())).setHeader("Motivo").setKey("reason").setFlexGrow(1);
        results.setAllRowsVisible(true);
        results.setVisible(false);

        VerticalLayout layout = new VerticalLayout(explanation, max, summary, results);
        layout.setPadding(false);
        add(layout);

        Button run = new Button("Reprocesar", click -> run());
        run.setId(RUN_ID);
        run.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cerrar", click -> close()), run);
    }

    static String explanation(DeadLetterQueuesDto.QueueDto queue) {
        return "Pasa los primeros mensajes de " + queue.deadLetterQueue() + " otra vez por el consumidor de " + queue.source()
                + ", cada uno una vez. Los que se aplican, y los que ya estaban aplicados, salen de la cola; los que vuelven a fallar se"
                + " quedan en ella, con su motivo. No se republica nada, y lo que se aplique puede generar avisos, como cualquier evento.";
    }

    private void run() {
        // Fuera de rango el servicio responde 400 VAL-001; vacio mandaria su 20 sin que nadie lo haya pedido.
        if (!Numbers.check(max, Validator.from(value -> value != null && value >= MIN && value <= MAX, RANGE))) {
            return;
        }
        try {
            DeadLetterReprocessDto result = client.reprocessDeadLetters(queue.source(), max.getValue());
            summary.setText(summary(result));
            summary.setVisible(true);
            results.setItems(result.results());
            results.setVisible(true);
            onDone.run();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** «Pedidos 20 · sacados 3 · procesados 1 · duplicados 1 · fallidos 1 · quedan 1 en la cola · paro porque …». */
    static String summary(DeadLetterReprocessDto result) {
        StringBuilder text = new StringBuilder("Pedidos ").append(number(result.requested()))
                .append(" · sacados ").append(number(result.fetched()))
                .append(" · procesados ").append(number(result.processed()))
                .append(" · duplicados ").append(number(result.duplicates()))
                .append(" · fallidos ").append(number(result.failed()));
        if (result.remaining() != null) {
            text.append(" · quedan ").append(result.remaining()).append(" en la cola");
        }
        if (result.stoppedBy() != null) {
            text.append(result.stoppedBy() == ReprocessStop.UNKNOWN ? " · paro por un motivo que esta version no conoce"
                    : " · paro porque " + result.stoppedBy().label());
        }
        return text.toString();
    }

    private static String number(Number value) {
        return value == null ? "0" : value.toString();
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}

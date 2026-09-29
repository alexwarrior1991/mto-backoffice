package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.AccessEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityEventDto;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Una linea del registro, o un acceso, entera: la cabecera y el {@code payload} clave a clave, tal
 * como lo publico la fuente y lo dejo la lista blanca del servicio. Aqui no se interpreta nada.
 */
public class EventDetailDialog extends Dialog {

    public static final String ID = "event-detail";
    public static final String PAYLOAD_ID = "event-payload";

    /** Una clave del payload con su valor como texto (un valor anidado se pinta tal cual). */
    public record PayloadEntry(String key, String value) {
    }

    private EventDetailDialog(String title, List<String[]> fields, Map<String, Object> payload) {
        setId(ID);
        setHeaderTitle(title);
        setWidth("min(60rem, 96vw)");
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);
        for (String[] field : fields) {
            if (field[1] == null || field[1].isBlank()) {
                continue;
            }
            Span label = new Span(field[0] + ": ");
            label.addClassNames(LumoUtility.TextColor.SECONDARY);
            Span value = new Span(field[1]);
            value.getElement().setAttribute("data-field", field[0]);
            layout.add(new Div(label, value));
        }
        Grid<PayloadEntry> grid = new Grid<>();
        grid.setId(PAYLOAD_ID);
        grid.addColumn(PayloadEntry::key).setHeader("Clave").setKey("key").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(PayloadEntry::value).setHeader("Valor").setKey("value").setFlexGrow(1);
        grid.setAllRowsVisible(true);
        grid.setItems(entries(payload));
        Span payloadTitle = new Span(payload.isEmpty() ? "Sin datos publicados" : "Datos publicados");
        payloadTitle.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.Margin.Top.MEDIUM);
        layout.add(payloadTitle);
        if (!payload.isEmpty()) {
            layout.add(grid);
        }
        add(layout);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    public static EventDetailDialog of(ActivityEventDto event) {
        List<String[]> fields = new ArrayList<>();
        fields.add(new String[]{"Tipo", event.type()});
        fields.add(new String[]{"Categoria", event.category() == null ? null : event.category().label()});
        fields.add(new String[]{"Gravedad", event.severity() == null ? null : event.severity().label()});
        fields.add(new String[]{"Cuando", Formats.dateTime(event.occurredAt())});
        fields.add(new String[]{"Registrado", Formats.dateTime(event.recordedAt())});
        fields.add(new String[]{"Quien", event.actor() == null ? null : actor(event)});
        fields.add(new String[]{"Sobre que", event.subject() == null ? null : event.subject().describe()});
        fields.add(new String[]{"Origen", event.sourceService()});
        fields.add(new String[]{"Evento de origen", event.sourceEventId()});
        fields.add(new String[]{"Eventos agrupados", event.eventCount() > 1 ? String.valueOf(event.eventCount()) : null});
        fields.add(new String[]{"Correlacion", event.correlationId()});
        fields.add(new String[]{"Fundida en", event.supersededBy() == null ? null : event.supersededBy().toString()});
        fields.add(new String[]{"Id", event.id() == null ? null : event.id().toString()});
        return new EventDetailDialog(event.type() == null ? "Evento" : event.type(), fields, event.payload());
    }

    public static EventDetailDialog of(AccessEventDto event) {
        List<String[]> fields = new ArrayList<>();
        fields.add(new String[]{"Tipo", event.type()});
        fields.add(new String[]{"Resultado", event.outcome() == null ? null : event.outcome().label()});
        fields.add(new String[]{"Gravedad", event.severity() == null ? null : event.severity().label()});
        fields.add(new String[]{"Cuando", Formats.dateTime(event.occurredAt())});
        fields.add(new String[]{"Registrado", Formats.dateTime(event.recordedAt())});
        fields.add(new String[]{"Usuario", event.username()});
        fields.add(new String[]{"Id de usuario", event.userId()});
        fields.add(new String[]{"IP", event.ipAddress()});
        fields.add(new String[]{"Eventos agrupados", event.eventCount() > 1 ? String.valueOf(event.eventCount()) : null});
        fields.add(new String[]{"Correlacion", event.correlationId()});
        fields.add(new String[]{"Id", event.id() == null ? null : event.id().toString()});
        return new EventDetailDialog(event.type() == null ? "Acceso" : event.type(), fields, event.payload());
    }

    private static String actor(ActivityEventDto event) {
        String who = event.actor().describe();
        return event.actor().kind() == null || who.equals(event.actor().kind().label()) ? who : who + " (" + event.actor().kind().label() + ")";
    }

    static List<PayloadEntry> entries(Map<String, Object> payload) {
        Map<String, Object> sorted = new TreeMap<>(payload);
        List<PayloadEntry> entries = new ArrayList<>();
        sorted.forEach((key, value) -> entries.add(new PayloadEntry(key, Objects.toString(value, ""))));
        return entries;
    }
}

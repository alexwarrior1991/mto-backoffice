package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.RuleDto;
import com.alejandro.mtobackoffice.client.dto.notification.RulesDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.TextMatching;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Las reglas que mto-notification tiene cargadas ({@code GET /admin/rules}), de solo lectura: se
 * cambian en su YAML y se validan al arrancar el servicio, no aqui. Llegan enteras, asi que el filtro
 * es local (sin mayusculas ni tildes) sobre lo que se ve de cada una.
 */
class AdminRulesPanel extends LazyPanel {

    private final NotificationClient client;
    private final TextField filter = new TextField();
    private final Span source = new Span();
    private final Span variables = new Span();
    private final Span count = new Span();
    private final Grid<RuleDto> grid = new Grid<>();
    private List<RuleDto> rules = List.of();

    AdminRulesPanel(NotificationClient client) {
        this.client = client;
        setSizeFull();
        filter.setId("admin-rules-filter");
        filter.setPlaceholder("Filtrar por clave, evento, audiencia, canal o texto");
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        filter.setClearButtonVisible(true);
        filter.setWidth("28rem");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(change -> applyFilter());
        Button refresh = new Button("Actualizar", VaadinIcon.REFRESH.create(), click -> reload());
        refresh.setId("admin-rules-refresh");
        source.setId("admin-rules-source");
        variables.setId("admin-rules-variables");
        count.setId("admin-rules-count");
        HorizontalLayout toolbar = new HorizontalLayout(filter, count, refresh);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(count);

        grid.setId("admin-rules-grid");
        grid.addColumn(RuleDto::key).setHeader("Clave").setKey("key").setAutoWidth(true);
        grid.addColumn(rule -> String.join(", ", rule.events())).setHeader("Eventos").setKey("events").setFlexGrow(1);
        grid.addColumn(rule -> text(rule.when())).setHeader("Condicion").setKey("when").setFlexGrow(1);
        grid.addColumn(rule -> rule.severity() == null ? "La del evento" : rule.severity().label()).setHeader("Gravedad").setKey("severity")
                .setAutoWidth(true);
        grid.addColumn(rule -> String.join(", ", rule.audiences())).setHeader("Audiencias").setKey("audiences").setFlexGrow(1);
        grid.addColumn(rule -> String.join(", ", rule.channels())).setHeader("Canales").setKey("channels").setAutoWidth(true);
        grid.addColumn(rule -> text(rule.title())).setHeader("Titulo").setKey("title").setFlexGrow(1);
        grid.addColumn(rule -> text(rule.link())).setHeader("Enlace").setKey("link").setAutoWidth(true);
        grid.addColumn(AdminRulesPanel::throttle).setHeader("Freno").setKey("throttle").setAutoWidth(true);
        grid.setSizeFull();

        add(source, variables, toolbar, grid);
        expand(grid);
    }

    @Override
    protected void load() {
        try {
            RulesDto loaded = client.adminRules();
            rules = loaded.rules();
            source.setText("Cargadas de " + text(loaded.source()));
            variables.setText(variables(loaded.variables()));
            variables.setVisible(!loaded.variables().isEmpty());
        } catch (BackofficeApiException failure) {
            rules = List.of();
            source.setText("");
            variables.setVisible(false);
            UiErrors.show(failure);
        }
        applyFilter();
    }

    private void applyFilter() {
        String text = filter.getValue() == null ? "" : filter.getValue();
        List<RuleDto> shown = rules.stream().filter(rule -> searchable(rule).anyMatch(value -> TextMatching.contains(value, text))).toList();
        grid.setItems(shown);
        count.setText(shown.size() == rules.size() ? rules.size() + (rules.size() == 1 ? " regla" : " reglas")
                : shown.size() + " de " + rules.size() + " reglas");
    }

    /** Lo que se ve de una regla, que es donde busca el filtro. */
    private static Stream<String> searchable(RuleDto rule) {
        return Stream.of(Stream.of(rule.key(), rule.when(), rule.title(), rule.link()), rule.events().stream(), rule.audiences().stream(),
                rule.channels().stream()).flatMap(part -> part).filter(value -> value != null);
    }

    /** «Variables: a = 1 · b = x», por orden de nombre. */
    static String variables(Map<String, Object> variables) {
        return "Variables: " + new TreeMap<>(variables).entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
                .collect(Collectors.joining(" · "));
    }

    /** «Uno cada 30 min por #{…}»: la ventana del freno y su clave; nada sin freno. */
    static String throttle(RuleDto rule) {
        if (rule.throttleWindow() == null || rule.throttleWindow().isBlank()) {
            return "";
        }
        String window = "Uno cada " + window(rule.throttleWindow());
        return rule.throttleKey() == null || rule.throttleKey().isBlank() ? window : window + " por " + rule.throttleKey();
    }

    /** Una duracion ISO-8601 ({@code PT30M}) en dias, horas y minutos; si no se sabe leer, tal cual. */
    static String window(String iso) {
        try {
            Duration duration = Duration.parse(iso);
            if (duration.isZero() || duration.isNegative() || duration.toSecondsPart() != 0) {
                return iso;
            }
            StringBuilder text = new StringBuilder();
            if (duration.toDaysPart() > 0) {
                text.append(duration.toDaysPart()).append(" d ");
            }
            if (duration.toHoursPart() > 0) {
                text.append(duration.toHoursPart()).append(" h ");
            }
            if (duration.toMinutesPart() > 0) {
                text.append(duration.toMinutesPart()).append(" min ");
            }
            return text.toString().trim();
        } catch (DateTimeParseException unreadable) {
            return iso;
        }
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}

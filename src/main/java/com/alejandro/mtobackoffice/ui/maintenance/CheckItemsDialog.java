package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.CheckItemUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Comparator;
import java.util.UUID;

/**
 * El checklist de una tarea abierta, punto a punto: medida, ajuste, valor tras el ajuste, resultado
 * y notas. Cada punto se guarda por separado y se repinta con lo que devuelve el servicio, que es
 * quien dice si quedo fuera de rango y quien rechaza un OK fuera de rango sin ajustar (422
 * {@code INS-001}). Para completar la tarea, los puntos con medida tienen que tener resultado.
 */
public class CheckItemsDialog extends Dialog {

    private final UUID orderId;
    private final MaintenanceClients clients;
    private final Runnable changed;
    private final VerticalLayout rows = new VerticalLayout();

    public CheckItemsDialog(UUID orderId, TaskDto task, MaintenanceClients clients, Runnable changed) {
        this.orderId = orderId;
        this.clients = clients;
        this.changed = changed;
        setHeaderTitle("Checklist de la tarea " + task.sequence());
        setWidth("min(95vw, 980px)");
        rows.setPadding(false);
        add(rows);
        paint(task);
        getFooter().add(new Button("Cerrar", click -> close()));
    }

    private void paint(TaskDto task) {
        rows.removeAll();
        task.checkItems().stream()
                .sorted(Comparator.comparing((CheckItemDto item) -> item.orderIndex() == null ? Integer.MAX_VALUE : item.orderIndex()))
                .forEach(item -> rows.add(row(task, item)));
    }

    private Div row(TaskDto task, CheckItemDto item) {
        String range = item.minValue() == null && item.maxValue() == null ? ""
                : " (" + Formats.quantity(item.minValue()) + " - " + Formats.quantity(item.maxValue())
                + (item.unit() == null ? "" : " " + item.unit()) + ")";
        Span label = new Span(item.code() + " " + item.label() + range);
        Span outOfRange = new Span("Fuera de rango");
        outOfRange.getElement().getThemeList().add("badge error");
        outOfRange.setVisible(Boolean.TRUE.equals(item.outOfRange()));

        BigDecimalField measured = new BigDecimalField("Medida");
        measured.setId("check-measured-" + item.id());
        measured.setValue(item.measuredValue());
        Checkbox adjusted = new Checkbox("Ajustado");
        adjusted.setId("check-adjusted-" + item.id());
        adjusted.setValue(Boolean.TRUE.equals(item.adjusted()));
        BigDecimalField after = new BigDecimalField("Tras el ajuste");
        after.setId("check-after-" + item.id());
        after.setValue(item.valueAfterAdjustment());
        ComboBox<CheckItemResult> result = new ComboBox<>("Resultado", CheckItemResult.selectable());
        result.setId("check-result-" + item.id());
        result.setItemLabelGenerator(CheckItemResult::label);
        result.setValue(item.itemResult() == CheckItemResult.UNKNOWN ? null : item.itemResult());
        TextField notes = new TextField("Notas");
        notes.setId("check-notes-" + item.id());
        notes.setValue(item.notes() == null ? "" : item.notes());
        Button save = new Button("Guardar", click -> save(task, item, new CheckItemUpdateRequest(measured.getValue(),
                adjusted.getValue() ? Boolean.TRUE : null, after.getValue(), result.getValue(), TransitionForm.nullIfBlank(notes.getValue()))));
        save.setId("check-save-" + item.id());
        save.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout heading = new HorizontalLayout(label, outOfRange);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        HorizontalLayout fields = new HorizontalLayout(measured, adjusted, after, result, notes, save);
        fields.setAlignItems(FlexComponent.Alignment.BASELINE);
        fields.setWrap(true);
        Div row = new Div(heading, fields);
        row.setWidthFull();
        return row;
    }

    private void save(TaskDto task, CheckItemDto item, CheckItemUpdateRequest request) {
        try {
            TaskDto updated = clients.orders().updateCheckItem(orderId, task.id(), item.id(), request);
            MaintenanceUi.success("Guardado " + item.code());
            paint(updated);
            changed.run();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

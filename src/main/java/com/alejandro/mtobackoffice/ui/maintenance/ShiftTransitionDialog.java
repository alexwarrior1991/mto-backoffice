package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CloseShiftRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartShiftRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.function.Consumer;

/**
 * Iniciar o cerrar un turno. Lo que se deja vacio lo pone el servicio: el inicio o el fin reales,
 * ahora; los minutos netos, desde el corte de tension (o el inicio) hasta el fin. Al cerrar, las
 * tareas que no se terminaron vuelven a su orden sin cancelarse.
 */
public class ShiftTransitionDialog extends Dialog {

    public static final String CONFIRM_ID = "shift-transition-confirm";

    public enum Kind {
        START("Iniciar"),
        CLOSE("Cerrar");

        private final String label;

        Kind(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public ShiftTransitionDialog(Kind kind, ShiftDto shift, MaintenanceClients clients, Consumer<ShiftDto> done) {
        setHeaderTitle(kind.label() + " " + shift.code());
        setCloseOnOutsideClick(false);
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        DateTimePicker when = new DateTimePicker(kind == Kind.START ? "Inicio real" : "Fin real");
        when.setId("shift-transition-when");
        when.setHelperText("Vacio: ahora");
        DateTimePicker cutoff = new DateTimePicker("Corte de tension");
        cutoff.setId("shift-transition-cutoff");
        cutoff.setValue(Formats.toLocalDateTime(shift.voltageCutoffAt()));
        IntegerField netMinutes = new IntegerField("Minutos netos de trabajo");
        netMinutes.setId("shift-transition-net-minutes");
        netMinutes.setMin(0);
        netMinutes.setHelperText("Vacio: los calcula el servicio desde el corte de tension (o el inicio)");
        TextArea observations = new TextArea("Observaciones");
        observations.setId("shift-transition-observations");
        observations.setValue(shift.observations() == null ? "" : shift.observations());
        layout.add(when, cutoff);
        if (kind == Kind.CLOSE) {
            add(new Paragraph("Las tareas que no se terminaron vuelven a su orden, sin cancelarse."));
            layout.add(netMinutes, observations);
        }
        add(layout);

        Button confirm = new Button(kind.label(), click -> {
            try {
                ShiftDto result = kind == Kind.START
                        ? clients.shifts().start(shift.id(), new StartShiftRequest(Formats.toInstant(when.getValue()), Formats.toInstant(cutoff.getValue())))
                        : clients.shifts().close(shift.id(), new CloseShiftRequest(Formats.toInstant(when.getValue()),
                        Formats.toInstant(cutoff.getValue()), netMinutes.getValue(), TransitionForm.nullIfBlank(observations.getValue())));
                close();
                MaintenanceUi.success(result.code() + ": " + result.status().label().toLowerCase());
                done.accept(result);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
            }
        });
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Volver", click -> close()), confirm);
    }
}

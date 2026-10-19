package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.AbsenceKind;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;

/**
 * Alta y modificacion de una ausencia de una persona, con las dos fechas incluidas. Lo evidente se
 * comprueba antes de llamar: la clase y las fechas, y que el fin no sea anterior al inicio. Que no se solape
 * con otra suya lo dice el servicio (409 {@code ABS-001}), con el dialogo abierto. La modificacion manda lo
 * que cambio y la version leida; solo las notas se vacian.
 */
public class PersonAbsenceDialog extends Dialog {

    public static final String SAVE_ID = "absence-save";
    static final int NOTES_LENGTH = 500;
    static final String END_BEFORE_START = "El fin no puede ser anterior al inicio";

    private final Binder<PersonAbsenceForm> binder = new Binder<>(PersonAbsenceForm.class);
    private final PersonAbsenceForm form;

    /**
     * @param existing la ausencia a modificar, o {@code null} para dar una de alta
     * @param saved    que hacer despues de guardar
     */
    public PersonAbsenceDialog(PersonDto person, PersonAbsenceDto existing, ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = PersonAbsenceForm.of(existing);
        setHeaderTitle(creating ? "Nueva ausencia de " + person.label() : "Modificar la ausencia de " + person.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 560px)");

        Select<AbsenceKind> kind = new Select<>();
        kind.setLabel("Clase");
        kind.setId("absence-kind");
        kind.setItems(AbsenceKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        kind.setHelperText("Solo se ve aqui: a mantenimiento le llegan las fechas");
        DatePicker startsOn = new DatePicker("Desde");
        startsOn.setId("absence-starts-on");
        DatePicker endsOn = new DatePicker("Hasta (incluido)");
        endsOn.setId("absence-ends-on");
        TextArea notes = new TextArea("Notas");
        notes.setId("absence-notes");
        notes.setMaxLength(NOTES_LENGTH);

        binder.forField(kind).asRequired("La clase es obligatoria").bind("kind");
        binder.forField(startsOn).asRequired("El inicio es obligatorio").bind("startsOn");
        Binder.Binding<PersonAbsenceForm, LocalDate> end = binder.forField(endsOn).asRequired("El fin es obligatorio")
                .withValidator(date -> date == null || startsOn.getValue() == null || !date.isBefore(startsOn.getValue()), END_BEFORE_START)
                .bind("endsOn");
        startsOn.addValueChangeListener(change -> {
            if (endsOn.getValue() != null) {
                end.validate();
            }
        });
        binder.forField(notes).bind("notes");

        FormLayout layout = new FormLayout(kind, startsOn, endsOn, notes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("400px", 2));
        layout.setColspan(kind, 2);
        layout.setColspan(notes, 2);
        binder.readBean(form);
        add(layout);

        Button save = new Button("Guardar", click -> save(person, existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(PersonDto person, PersonAbsenceDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            PersonAbsenceDto result;
            if (existing == null) {
                result = clients.people().addAbsence(person.id(), form.toRequest());
            } else {
                MergePatch<PersonAbsenceUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.people().updateAbsence(person.id(), existing.id(), patch);
            }
            close();
            Screens.success("Guardada la ausencia (" + PersonAbsencesPanel.period(result) + ")");
            saved.run();
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

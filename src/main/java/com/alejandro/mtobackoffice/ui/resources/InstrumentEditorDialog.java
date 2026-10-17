package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;

/**
 * Alta y modificacion de un instrumento. El codigo no cambia despues del alta; la calibracion no
 * caduca antes de hacerse (lo evidente, antes de llamar). Renovarla es cambiar sus fechas; sin fecha de
 * caducidad no tiene calibracion. Que el codigo no se repita lo dice el servicio (409 {@code INT-409})
 * con el dialogo abierto. La modificacion manda lo que cambio y la version leida.
 */
public class InstrumentEditorDialog extends Dialog {

    public static final String SAVE_ID = "instrument-save";
    static final int CODE_LENGTH = 32;
    static final int NAME_LENGTH = 120;
    static final int SERIAL_LENGTH = 60;
    static final int NOTES_LENGTH = 500;

    private final Binder<InstrumentForm> binder = new Binder<>(InstrumentForm.class);
    private final InstrumentForm form;

    public InstrumentEditorDialog(InstrumentDto existing, ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = InstrumentForm.of(existing);
        setHeaderTitle(creating ? "Nuevo instrumento" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");

        TextField code = new TextField("Codigo");
        code.setId("instrument-code");
        code.setMaxLength(CODE_LENGTH);
        code.setReadOnly(!creating);
        code.setHelperText(creating ? "En mayusculas; no cambia despues" : "No cambia");
        TextField name = new TextField("Nombre");
        name.setId("instrument-name");
        name.setMaxLength(NAME_LENGTH);
        Select<InstrumentKind> kind = new Select<>();
        kind.setLabel("Tipo");
        kind.setId("instrument-kind");
        kind.setItems(InstrumentKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        TextField serialNumber = new TextField("Numero de serie");
        serialNumber.setId("instrument-serial");
        serialNumber.setMaxLength(SERIAL_LENGTH);
        DatePicker calibratedOn = new DatePicker("Calibrado el");
        calibratedOn.setId("instrument-calibrated-on");
        calibratedOn.setClearButtonVisible(true);
        DatePicker calibrationExpiresOn = new DatePicker("Calibracion hasta");
        calibrationExpiresOn.setId("instrument-calibration-expires-on");
        calibrationExpiresOn.setClearButtonVisible(true);
        calibrationExpiresOn.setHelperText("Vacia: sin calibracion");
        TextArea notes = new TextArea("Notas");
        notes.setId("instrument-notes");
        notes.setMaxLength(NOTES_LENGTH);

        binder.forField(code).asRequired(Required.text("El codigo es obligatorio")).bind("code");
        binder.forField(name).asRequired(Required.text("El nombre es obligatorio")).bind("name");
        binder.forField(kind).asRequired("El tipo es obligatorio").bind("kind");
        binder.forField(serialNumber).bind("serialNumber");
        binder.forField(calibratedOn).bind("calibratedOn");
        Binder.Binding<InstrumentForm, LocalDate> expiry = binder.forField(calibrationExpiresOn)
                .withValidator(date -> date == null || calibratedOn.getValue() == null || !date.isBefore(calibratedOn.getValue()),
                        "La calibracion no caduca antes de hacerse")
                .bind("calibrationExpiresOn");
        calibratedOn.addValueChangeListener(change -> expiry.validate());
        binder.forField(notes).bind("notes");

        FormLayout layout = new FormLayout(code, name, kind, serialNumber, calibratedOn, calibrationExpiresOn, notes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(notes, 2);
        add(layout);
        binder.readBean(form);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(InstrumentDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            InstrumentDto result;
            if (existing == null) {
                result = clients.instruments().create(form.toRequest());
            } else {
                MergePatch<InstrumentUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.instruments().update(existing.id(), patch);
            }
            close();
            Screens.success("Guardado " + result.label());
            saved.run();
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

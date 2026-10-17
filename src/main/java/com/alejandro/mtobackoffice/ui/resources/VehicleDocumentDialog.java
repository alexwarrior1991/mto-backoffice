package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Alta y modificacion de un documento de un vehiculo. El alta ofrece los tipos que el vehiculo no tiene
 * todavia, porque tiene uno de cada tipo (renovarlo es cambiar su caducidad, y otro del mismo tipo seria
 * 409 {@code VDO-409}); el tipo no cambia despues. La caducidad es obligatoria y no es anterior a la
 * expedicion, lo evidente antes de llamar. La modificacion manda lo que cambio y la version leida.
 */
public class VehicleDocumentDialog extends Dialog {

    public static final String SAVE_ID = "document-save";
    static final int REFERENCE_LENGTH = 60;
    static final int NOTES_LENGTH = 500;

    private final Binder<VehicleDocumentForm> binder = new Binder<>(VehicleDocumentForm.class);
    private final VehicleDocumentForm form;

    /**
     * @param existing el documento a modificar, o {@code null} para dar uno de alta
     * @param current  los que ya tiene el vehiculo, para no ofrecer su tipo otra vez
     */
    public VehicleDocumentDialog(VehicleDto vehicle, VehicleDocumentDto existing, Collection<VehicleDocumentDto> current,
                                 ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = VehicleDocumentForm.of(existing);
        setHeaderTitle(creating ? "Nuevo documento de " + vehicle.label() : "Modificar " + existing.label() + " de " + vehicle.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");

        Select<VehicleDocumentKind> kind = new Select<>();
        kind.setLabel("Tipo");
        kind.setId("document-kind");
        if (creating) {
            Set<VehicleDocumentKind> held = current.stream().map(VehicleDocumentDto::kind).collect(Collectors.toSet());
            kind.setItems(VehicleDocumentKind.selectable().stream().filter(value -> !held.contains(value)).toList());
        } else {
            kind.setItems(List.of(existing.kind()));
            kind.setReadOnly(true);
            kind.setHelperText("No cambia: un vehiculo tiene uno de cada tipo");
        }
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        TextField reference = new TextField("Referencia");
        reference.setId("document-reference");
        reference.setMaxLength(REFERENCE_LENGTH);
        DatePicker issuedOn = new DatePicker("Expedido");
        issuedOn.setId("document-issued-on");
        issuedOn.setClearButtonVisible(true);
        DatePicker expiresOn = new DatePicker("Caduca");
        expiresOn.setId("document-expires-on");
        TextArea notes = new TextArea("Notas");
        notes.setId("document-notes");
        notes.setMaxLength(NOTES_LENGTH);

        binder.forField(kind).asRequired("El tipo es obligatorio").bind("kind");
        binder.forField(reference).bind("reference");
        binder.forField(issuedOn).bind("issuedOn");
        Binder.Binding<VehicleDocumentForm, LocalDate> expiry = binder.forField(expiresOn)
                .asRequired("La caducidad es obligatoria")
                .withValidator(date -> issuedOn.getValue() == null || !date.isBefore(issuedOn.getValue()), "No puede caducar antes de expedirse")
                .bind("expiresOn");
        issuedOn.addValueChangeListener(change -> {
            if (expiresOn.getValue() != null) {
                expiry.validate();
            }
        });
        binder.forField(notes).bind("notes");

        FormLayout layout = new FormLayout(kind, reference, issuedOn, expiresOn, notes);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(notes, 2);
        add(layout);
        binder.readBean(form);

        Button save = new Button("Guardar", click -> save(vehicle, existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(VehicleDto vehicle, VehicleDocumentDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            VehicleDocumentDto result;
            if (existing == null) {
                result = clients.vehicles().addDocument(vehicle.id(), form.toRequest());
            } else {
                MergePatch<VehicleDocumentUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.vehicles().updateDocument(vehicle.id(), existing.id(), patch);
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

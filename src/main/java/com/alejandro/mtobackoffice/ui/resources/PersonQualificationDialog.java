package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeFilter;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.resources.QualificationTypeClient;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Alta y modificacion de una cualificacion de una persona. El alta ofrece los tipos activos que la
 * persona no tiene todavia, porque una persona tiene una de cada tipo (renovarla es cambiar su
 * caducidad) y el servicio rechaza un tipo retirado (409 {@code QUA-001}); el tipo no cambia despues.
 * Lo evidente se comprueba antes de llamar: el tipo y que no caduque antes de expedirse. La vigencia
 * habitual del tipo se dice junto a la caducidad, sin calcular nada. La modificacion manda lo que cambio
 * y la version leida; cualquier dato se puede vaciar (sin caducidad, no caduca).
 */
public class PersonQualificationDialog extends Dialog {

    public static final String SAVE_ID = "qualification-save";
    static final int REFERENCE_LENGTH = 60;
    static final int NOTES_LENGTH = 500;
    static final String NO_EXPIRY = "Vacia: no caduca";
    /** Los tipos del catalogo, que caben en una pagina. */
    static final int ALL_TYPES = 200;

    private final Binder<PersonQualificationForm> binder = new Binder<>(PersonQualificationForm.class);
    private final PersonQualificationForm form;
    private final DatePicker expiresOn = new DatePicker("Caduca");

    /**
     * @param existing la cualificacion a modificar, o {@code null} para dar una de alta
     * @param current  las que ya tiene la persona, para no ofrecer su tipo otra vez
     * @param saved    que hacer despues de guardar
     */
    public PersonQualificationDialog(PersonDto person, PersonQualificationDto existing, Collection<PersonQualificationDto> current,
                                     ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = PersonQualificationForm.of(existing);
        setHeaderTitle(creating ? "Nueva cualificacion de " + person.label() : "Modificar " + existing.label() + " de " + person.code());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");

        TextField reference = new TextField("Referencia");
        reference.setId("qualification-reference");
        reference.setMaxLength(REFERENCE_LENGTH);
        reference.setHelperText("El numero del permiso o del certificado");
        DatePicker issuedOn = new DatePicker("Expedida");
        issuedOn.setId("qualification-issued-on");
        issuedOn.setClearButtonVisible(true);
        expiresOn.setId("qualification-expires-on");
        expiresOn.setClearButtonVisible(true);
        expiresOn.setHelperText(NO_EXPIRY);
        TextArea notes = new TextArea("Notas");
        notes.setId("qualification-notes");
        notes.setMaxLength(NOTES_LENGTH);

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        if (creating) {
            ComboBox<QualificationTypeDto> type = new ComboBox<>("Tipo");
            type.setId("qualification-type");
            type.setItems(offered(clients.qualificationTypes(), current));
            type.setItemLabelGenerator(QualificationTypeDto::label);
            type.setRequiredIndicatorVisible(true);
            type.addValueChangeListener(change -> expiresOn.setHelperText(helper(change.getValue())));
            binder.forField(type).asRequired("El tipo es obligatorio").bind("qualificationTypeId");
            layout.add(type);
            layout.setColspan(type, 2);
        } else {
            TextField type = new TextField("Tipo");
            type.setId("qualification-type-readonly");
            type.setValue(existing.label());
            type.setReadOnly(true);
            type.setHelperText("No cambia: una persona tiene una de cada tipo");
            layout.add(type);
            layout.setColspan(type, 2);
        }
        binder.forField(reference).bind("reference");
        binder.forField(issuedOn).bind("issuedOn");
        Binder.Binding<PersonQualificationForm, LocalDate> expiry = binder.forField(expiresOn)
                .withValidator(date -> date == null || issuedOn.getValue() == null || !date.isBefore(issuedOn.getValue()),
                        "No puede caducar antes de expedirse")
                .bind("expiresOn");
        issuedOn.addValueChangeListener(change -> expiry.validate());
        binder.forField(notes).bind("notes");
        layout.add(reference, issuedOn, expiresOn, notes);
        layout.setColspan(notes, 2);
        binder.readBean(form);
        add(layout);

        Button save = new Button("Guardar", click -> save(person, existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    /** Los tipos activos que la persona no tiene; si no se pueden leer, se notifica y no se ofrece ninguno. */
    private static List<QualificationTypeDto> offered(QualificationTypeClient types, Collection<PersonQualificationDto> current) {
        Set<UUID> held = current.stream().map(PersonQualificationDto::qualificationType).filter(Objects::nonNull)
                .map(QualificationTypeSummaryDto::id).collect(Collectors.toSet());
        try {
            return types.search(QualificationTypeFilter.ACTIVE, 0, ALL_TYPES, List.of("code,asc")).content().stream()
                    .filter(type -> !held.contains(type.id())).toList();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return List.of();
        }
    }

    /** La vigencia habitual del tipo, si la tiene: solo se dice. */
    static String helper(QualificationTypeDto type) {
        if (type == null || type.validityMonths() == null) {
            return NO_EXPIRY;
        }
        return NO_EXPIRY + ". Vigencia habitual: " + type.validityMonths() + (type.validityMonths() == 1 ? " mes" : " meses");
    }

    private void save(PersonDto person, PersonQualificationDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            PersonQualificationDto result;
            if (existing == null) {
                result = clients.people().addQualification(person.id(), form.toRequest());
            } else {
                MergePatch<PersonQualificationUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.people().updateQualification(person.id(), existing.id(), patch);
            }
            close();
            Screens.success("Guardada " + result.label());
            saved.run();
        } catch (ValidationApiException validation) {
            Screens.showValidation(binder, validation);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

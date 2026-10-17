package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.DrivingLicence;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationKind;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Numbers;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Alta y modificacion de un tipo de cualificacion. El codigo no cambia despues del alta; el permiso de
 * conducir se pide solo en uno de {@code DRIVING_LICENCE} (y es obligatorio en el), y la vigencia
 * habitual, si se dice, es de un mes o mas. Que el codigo no se repita lo dice el servicio (409
 * {@code QTY-409}) con el dialogo abierto. La modificacion manda lo que cambio y la version leida.
 */
public class QualificationTypeEditorDialog extends Dialog {

    public static final String SAVE_ID = "qualification-type-save";
    static final int CODE_LENGTH = 32;
    static final int NAME_LENGTH = 120;
    static final int DESCRIPTION_LENGTH = 500;

    private final Binder<QualificationTypeForm> binder = new Binder<>(QualificationTypeForm.class);
    private final QualificationTypeForm form;

    public QualificationTypeEditorDialog(QualificationTypeDto existing, ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = QualificationTypeForm.of(existing);
        setHeaderTitle(creating ? "Nuevo tipo de cualificacion" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");

        TextField code = new TextField("Codigo");
        code.setId("qualification-type-code");
        code.setMaxLength(CODE_LENGTH);
        code.setReadOnly(!creating);
        code.setHelperText(creating ? "En mayusculas; no cambia despues" : "No cambia");
        TextField name = new TextField("Nombre");
        name.setId("qualification-type-name");
        name.setMaxLength(NAME_LENGTH);
        Select<QualificationKind> kind = new Select<>();
        kind.setLabel("Clase");
        kind.setId("qualification-type-kind");
        kind.setItems(QualificationKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "" : value.label());
        Select<DrivingLicence> licence = new Select<>();
        licence.setLabel("Permiso de conducir");
        licence.setId("qualification-type-licence");
        licence.setItems(DrivingLicence.selectable());
        licence.setItemLabelGenerator(value -> value == null ? "" : value.label());
        IntegerField validity = Numbers.atLeast(new IntegerField("Vigencia habitual (meses)"), 1, "Como minimo 1 mes");
        validity.setId("qualification-type-validity");
        validity.setHelperText("Vacia: sin vigencia habitual");
        TextArea description = new TextArea("Descripcion");
        description.setId("qualification-type-description");
        description.setMaxLength(DESCRIPTION_LENGTH);

        binder.forField(code).asRequired(Required.text("El codigo es obligatorio")).bind("code");
        binder.forField(name).asRequired(Required.text("El nombre es obligatorio")).bind("name");
        binder.forField(kind).asRequired("La clase es obligatoria").bind("kind");
        Binder.Binding<QualificationTypeForm, DrivingLicence> licenceBinding = binder.forField(licence)
                .withValidator(value -> kind.getValue() != QualificationKind.DRIVING_LICENCE || value != null,
                        "Un permiso de conducir dice cual")
                .bind("drivingLicence");
        binder.forField(validity).bind("validityMonths");
        binder.forField(description).bind("description");
        kind.addValueChangeListener(change -> {
            boolean driving = change.getValue() == QualificationKind.DRIVING_LICENCE;
            licence.setVisible(driving);
            licence.setRequiredIndicatorVisible(driving);
            if (!driving) {
                licenceBinding.validate();
            }
        });

        FormLayout layout = new FormLayout(code, name, kind, licence, validity, description);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        layout.setColspan(description, 2);
        add(layout);
        binder.readBean(form);
        licence.setVisible(form.getKind() == QualificationKind.DRIVING_LICENCE);
        licence.setRequiredIndicatorVisible(form.getKind() == QualificationKind.DRIVING_LICENCE);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(QualificationTypeDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            QualificationTypeDto result;
            if (existing == null) {
                result = clients.qualificationTypes().create(form.toRequest());
            } else {
                MergePatch<QualificationTypeUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.qualificationTypes().update(existing.id(), patch);
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

package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonEmployer;
import com.alejandro.mtobackoffice.client.dto.resources.PersonUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

/**
 * Alta y modificacion de una persona. Solo se exige lo evidente: nombre, apellidos y categoria, la
 * empresa de una subcontrata y la longitud de cada columna. Que un usuario o un numero de empleado no
 * se repitan, o que la categoria nueva siga valiendo para lo que dirige, lo dice el servicio (409
 * {@code PER-409} o {@code UNI-001}) con el dialogo abierto. La modificacion manda solo lo que cambio
 * y la version leida; vaciar lo opcional viaja como {@code null}.
 */
public class PersonEditorDialog extends Dialog {

    public static final String SAVE_ID = "person-save";
    static final int FIRST_NAME_LENGTH = 100;
    static final int LAST_NAME_LENGTH = 150;
    static final int COMPANY_LENGTH = 150;
    static final int EMPLOYEE_NUMBER_LENGTH = 40;
    static final int USERNAME_LENGTH = 100;
    static final int PHONE_LENGTH = 40;

    private final Binder<PersonForm> binder = new Binder<>(PersonForm.class);
    private final PersonForm form;

    /**
     * @param existing la persona a modificar, o {@code null} para dar una de alta
     * @param saved    que hacer despues de guardar
     */
    public PersonEditorDialog(PersonDto existing, ResourcesClients clients, Runnable saved) {
        boolean creating = existing == null;
        this.form = PersonForm.of(existing);
        setHeaderTitle(creating ? "Nueva persona" : "Modificar " + existing.label());
        setCloseOnOutsideClick(false);
        setWidth("min(90vw, 640px)");

        TextField firstName = text("person-first-name", "Nombre", FIRST_NAME_LENGTH);
        firstName.setRequiredIndicatorVisible(true);
        TextField lastName = text("person-last-name", "Apellidos", LAST_NAME_LENGTH);
        lastName.setRequiredIndicatorVisible(true);
        ComboBox<PersonCategory> category = new ComboBox<>("Categoria", PersonCategory.selectable());
        category.setId("person-category");
        category.setItemLabelGenerator(PersonCategory::label);
        category.setRequiredIndicatorVisible(true);
        Select<PersonEmployer> employer = new Select<>();
        employer.setLabel("Empleo");
        employer.setId("person-employer");
        employer.setItems(PersonEmployer.selectable());
        employer.setItemLabelGenerator(PersonEmployer::label);
        TextField company = text("person-company", "Empresa", COMPANY_LENGTH);
        TextField employeeNumber = text("person-employee-number", "Numero de empleado", EMPLOYEE_NUMBER_LENGTH);
        TextField username = text("person-username", "Usuario", USERNAME_LENGTH);
        username.setHelperText("Su usuario de Keycloak, si entra en las aplicaciones");
        TextField phone = text("person-phone", "Telefono", PHONE_LENGTH);

        binder.forField(firstName).asRequired(Required.text("El nombre es obligatorio")).bind("firstName");
        binder.forField(lastName).asRequired(Required.text("Los apellidos son obligatorios")).bind("lastName");
        binder.forField(category).asRequired("La categoria es obligatoria").bind("category");
        binder.forField(employer).asRequired("El empleo es obligatorio").bind("employer");
        Binder.Binding<PersonForm, String> companyBinding = binder.forField(company)
                .withValidator(value -> employer.getValue() != PersonEmployer.SUBCONTRACTOR || (value != null && !value.isBlank()),
                        "Una persona de subcontrata dice su empresa")
                .bind("company");
        binder.forField(employeeNumber).bind("employeeNumber");
        binder.forField(username).bind("username");
        binder.forField(phone).bind("phone");
        employer.addValueChangeListener(change -> {
            boolean subcontractor = change.getValue() == PersonEmployer.SUBCONTRACTOR;
            company.setVisible(subcontractor);
            company.setRequiredIndicatorVisible(subcontractor);
            companyBinding.validate();
        });
        binder.readBean(form);
        company.setVisible(form.getEmployer() == PersonEmployer.SUBCONTRACTOR);
        company.setRequiredIndicatorVisible(form.getEmployer() == PersonEmployer.SUBCONTRACTOR);

        FormLayout layout = new FormLayout(firstName, lastName, category, employer, company, employeeNumber, username, phone);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("480px", 2));
        add(layout);

        Button save = new Button("Guardar", click -> save(existing, clients, saved));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private static TextField text(String id, String label, int maxLength) {
        TextField field = new TextField(label);
        field.setId(id);
        field.setMaxLength(maxLength);
        return field;
    }

    private void save(PersonDto existing, ResourcesClients clients, Runnable saved) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            PersonDto result;
            if (existing == null) {
                result = clients.people().create(form.toRequest());
            } else {
                MergePatch<PersonUpdateRequest> patch = form.toPatch(existing);
                if (patch.changesNothing()) {
                    close();
                    return;
                }
                result = clients.people().update(existing.id(), patch);
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

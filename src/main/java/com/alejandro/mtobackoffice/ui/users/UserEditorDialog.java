package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;

import java.util.List;

/**
 * Alta o modificacion de un usuario. Aqui solo se valida lo evidente (usuario obligatorio y con
 * la forma que exige el servicio, email con forma de email, contrasena de al menos ocho, atributos
 * legibles); la politica de contrasenas y lo demas lo dice el servicio y se ensena campo a campo.
 * En una modificacion el nombre de usuario se ensena pero no se toca, y solo viaja lo que cambio.
 */
public class UserEditorDialog extends Dialog {

    static final int MAX_LENGTH = 255;
    static final String USERNAME_PATTERN = "[a-zA-Z0-9._@-]+";
    static final int MIN_PASSWORD_LENGTH = 8;

    private final Binder<UserForm> binder = new Binder<>(UserForm.class);
    private final UserForm form;
    private final UserDto existing;
    private final UsersClient client;
    private final Runnable onSaved;

    /**
     * @param existing usuario a modificar, o {@code null} para un alta
     * @param onSaved  que hacer despues de guardar (recargar la lista o la ficha)
     */
    public UserEditorDialog(UserDto existing, UsersClient client, Runnable onSaved) {
        this.existing = existing;
        this.client = client;
        this.onSaved = onSaved;
        this.form = UserForm.of(existing);
        boolean creating = existing == null;
        setHeaderTitle(creating ? "Alta de usuario" : "Modificar usuario " + existing.username());
        setCloseOnOutsideClick(false);
        setWidth("min(48rem, 96vw)");

        TextField username = new TextField("Usuario");
        username.setMaxLength(MAX_LENGTH);
        username.setRequiredIndicatorVisible(true);
        username.setHelperText("Letras, cifras y . _ @ -");
        TextField firstName = new TextField("Nombre");
        firstName.setMaxLength(MAX_LENGTH);
        TextField lastName = new TextField("Apellidos");
        lastName.setMaxLength(MAX_LENGTH);
        TextField email = new TextField("Email");
        email.setMaxLength(MAX_LENGTH);
        Checkbox emailVerified = new Checkbox("Email verificado");
        Checkbox enabled = new Checkbox("Activo");
        PasswordField temporaryPassword = new PasswordField("Contrasena temporal");
        temporaryPassword.setHelperText("Al menos " + MIN_PASSWORD_LENGTH + " caracteres; la persona la cambia al entrar");
        MultiSelectComboBox<RequiredAction> requiredActions = new MultiSelectComboBox<>("Acciones requeridas al entrar");
        requiredActions.setItems(RequiredAction.values());
        requiredActions.setItemLabelGenerator(RequiredAction::label);
        TextArea attributes = new TextArea("Atributos (clave=valor por linea)");
        attributes.setHelperText("Una clave repetida acumula valores");

        if (creating) {
            binder.forField(username).asRequired("El usuario es obligatorio")
                    .withValidator(new RegexpValidator("Letras, cifras y . _ @ -", USERNAME_PATTERN))
                    .bind("username");
            binder.forField(enabled).bind("enabled");
            binder.forField(temporaryPassword)
                    .withValidator(value -> value == null || value.isBlank() || value.length() >= MIN_PASSWORD_LENGTH,
                            "Al menos " + MIN_PASSWORD_LENGTH + " caracteres")
                    .bind("temporaryPassword");
            binder.forField(requiredActions).bind("requiredActions");
        } else {
            username.setValue(existing.username() == null ? "" : existing.username());
            username.setReadOnly(true);
        }
        binder.forField(firstName).bind("firstName");
        binder.forField(lastName).bind("lastName");
        binder.forField(email).withValidator(new EmailValidator("No tiene forma de email", true)).bind("email");
        binder.forField(emailVerified).bind("emailVerified");
        binder.forField(attributes).withValidator((value, context) -> {
            try {
                UserAttributes.parse(value);
                return ValidationResult.ok();
            } catch (IllegalArgumentException unreadable) {
                return ValidationResult.error(unreadable.getMessage());
            }
        }).bind("attributes");
        binder.readBean(form);

        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("36em", 2));
        layout.add(username, email, firstName, lastName, emailVerified);
        if (creating) {
            layout.add(enabled, temporaryPassword, requiredActions);
            layout.setColspan(requiredActions, 2);
        }
        layout.add(attributes);
        layout.setColspan(attributes, 2);
        add(layout);

        Button save = new Button("Guardar", click -> save());
        save.setId("user-save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancelar", click -> close());
        getFooter().add(cancel, save);
    }

    private void save() {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            UserDto saved = existing == null
                    ? client.create(form.toCreateRequest())
                    : client.update(existing.id(), form.toUpdateRequest(existing));
            close();
            Notification.show("Guardado " + saved.username(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            onSaved.run();
        } catch (ValidationApiException validation) {
            List<String> unattributed = ServerValidation.apply(binder, validation);
            if (!unattributed.isEmpty()) {
                Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

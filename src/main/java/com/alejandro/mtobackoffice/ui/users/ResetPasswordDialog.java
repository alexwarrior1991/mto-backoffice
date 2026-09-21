package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.ResetPasswordRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/**
 * Una contrasena nueva para la persona. Temporal por defecto: Keycloak le pide cambiarla al
 * entrar, que es lo que se quiere cuando la fija otra persona. La politica de contrasenas del
 * realm la aplica Keycloak y llega como 400 ({@code KC-400}), que cae sobre el campo.
 */
public class ResetPasswordDialog extends Dialog {

    static final String SAVE_ID = "reset-password-save";

    /** Lo que el Binder lee y escribe; {@code password} se llama como en el servicio. */
    public static class Form {
        private String password = "";
        private boolean temporary = true;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isTemporary() {
            return temporary;
        }

        public void setTemporary(boolean temporary) {
            this.temporary = temporary;
        }
    }

    private final Binder<Form> binder = new Binder<>(Form.class);
    private final Form form = new Form();

    public ResetPasswordDialog(UserDto user, UsersClient client) {
        setHeaderTitle("Contrasena para " + user.username());
        setCloseOnOutsideClick(false);

        PasswordField password = new PasswordField("Contrasena nueva");
        password.setHelperText("Al menos " + UserEditorDialog.MIN_PASSWORD_LENGTH + " caracteres; la politica del realm puede pedir mas");
        password.setRequiredIndicatorVisible(true);
        Checkbox temporary = new Checkbox("Temporal: la persona tiene que cambiarla al entrar");

        binder.forField(password).asRequired("La contrasena es obligatoria")
                .withValidator(value -> value.length() >= UserEditorDialog.MIN_PASSWORD_LENGTH,
                        "Al menos " + UserEditorDialog.MIN_PASSWORD_LENGTH + " caracteres")
                .bind("password");
        binder.forField(temporary).bind("temporary");
        binder.readBean(form);

        FormLayout layout = new FormLayout(password, temporary);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(layout);

        Button save = new Button("Fijar contrasena", click -> save(user, client));
        save.setId(SAVE_ID);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), save);
    }

    private void save(UserDto user, UsersClient client) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            client.resetPassword(user.id(), new ResetPasswordRequest(form.getPassword(), form.isTemporary()));
            close();
            Notification.show("Contrasena fijada para " + user.username() + (form.isTemporary() ? " (temporal)" : ""),
                    3000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

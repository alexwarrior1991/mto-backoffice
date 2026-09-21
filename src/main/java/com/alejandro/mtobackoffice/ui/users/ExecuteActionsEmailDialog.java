package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.ExecuteActionsEmailRequest;
import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * El correo con las acciones que la persona tiene que completar al entrar (cambiar la contrasena,
 * verificar el email...). Lo manda Keycloak, asi que hace falta SMTP en el realm: sin el,
 * mto-users responde 502 y la notificacion ensena su detalle. Sin email no hay a quien mandarlo.
 */
public class ExecuteActionsEmailDialog extends Dialog {

    static final String SEND_ID = "actions-email-send";
    static final int MIN_LIFESPAN_SECONDS = 60;

    /** Lo que el Binder lee y escribe; las propiedades se llaman como en el servicio. */
    public static class Form {
        private Set<RequiredAction> actions = new LinkedHashSet<>();
        private Integer lifespanSeconds;

        public Set<RequiredAction> getActions() {
            return actions;
        }

        public void setActions(Set<RequiredAction> actions) {
            this.actions = actions == null ? new LinkedHashSet<>() : new LinkedHashSet<>(actions);
        }

        public Integer getLifespanSeconds() {
            return lifespanSeconds;
        }

        public void setLifespanSeconds(Integer lifespanSeconds) {
            this.lifespanSeconds = lifespanSeconds;
        }
    }

    private final Binder<Form> binder = new Binder<>(Form.class);
    private final Form form = new Form();

    public ExecuteActionsEmailDialog(UserDto user, UsersClient client) {
        setHeaderTitle("Correo de acciones para " + user.username());
        setCloseOnOutsideClick(false);
        boolean hasEmail = user.email() != null && !user.email().isBlank();

        add(new Paragraph(hasEmail
                ? "Keycloak manda a " + user.email() + " un enlace con las acciones elegidas; la persona las completa al abrirlo."
                : "El usuario no tiene email: Keycloak no tiene a quien mandar el enlace."));
        MultiSelectComboBox<RequiredAction> actions = new MultiSelectComboBox<>("Acciones");
        actions.setItems(RequiredAction.values());
        actions.setItemLabelGenerator(RequiredAction::label);
        actions.setRequiredIndicatorVisible(true);
        IntegerField lifespan = new IntegerField("Validez del enlace (segundos)");
        lifespan.setHelperText("Vacio: la que tenga el realm por defecto; al menos " + MIN_LIFESPAN_SECONDS);

        binder.forField(actions).asRequired("Elige al menos una accion").bind("actions");
        binder.forField(lifespan)
                .withValidator(value -> value == null || value >= MIN_LIFESPAN_SECONDS, "Al menos " + MIN_LIFESPAN_SECONDS + " segundos")
                .bind("lifespanSeconds");
        binder.readBean(form);

        FormLayout layout = new FormLayout(actions, lifespan);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(layout);

        Button send = new Button("Enviar", click -> send(user, client));
        send.setId(SEND_ID);
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        send.setEnabled(hasEmail);
        getFooter().add(new Button("Cancelar", click -> close()), send);
    }

    private void send(UserDto user, UsersClient client) {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            client.executeActionsEmail(user.id(), new ExecuteActionsEmailRequest(List.copyOf(form.getActions()), form.getLifespanSeconds(), null, null));
            close();
            Notification.show("Correo enviado a " + user.email(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

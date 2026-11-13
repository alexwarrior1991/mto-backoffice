package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.AdminNotificationDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.function.SerializableConsumer;

/**
 * Un correo de prueba ({@code POST /admin/test-email}): el servicio crea una notificacion para quien
 * llama y una entrega directa a la direccion. Responde 202: el correo sale despues, y como le fue se
 * ve en la pestana Entregas. Aqui solo se exige lo evidente (una direccion con forma de email); lo
 * demas lo dice el servicio, en su campo.
 */
public class TestEmailDialog extends Dialog {

    static final String SEND_ID = "test-email-send";
    private static final EmailValidator EMAIL = new EmailValidator("No tiene forma de email");

    private final Binder<TestEmailForm> binder = new Binder<>(TestEmailForm.class);
    private final TestEmailForm form = new TestEmailForm();
    private final NotificationClient client;
    private final SerializableConsumer<AdminNotificationDto> onSent;

    public TestEmailDialog(NotificationClient client, SerializableConsumer<AdminNotificationDto> onSent) {
        this.client = client;
        this.onSent = onSent;
        setHeaderTitle("Correo de prueba");
        setCloseOnOutsideClick(false);

        TextField to = new TextField("Para");
        to.setId("test-email-to");
        to.setPlaceholder("ops@mto.local");
        to.setHelperText("Recibe un mensaje de prueba; a ti te queda la notificacion en la bandeja");
        to.setWidthFull();
        binder.forField(to).asRequired(Required.text("La direccion es obligatoria"))
                // Sin los blancos de alrededor, que es como viaja.
                .withValidator((value, context) -> EMAIL.apply(value.trim(), context))
                .withValidator(value -> value.trim().length() <= TestEmailForm.MAX_LENGTH, "Como mucho " + TestEmailForm.MAX_LENGTH + " caracteres")
                .bind("to");
        binder.readBean(form);

        FormLayout layout = new FormLayout(to);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        layout.setWidth("28rem");
        add(layout);

        Button send = new Button("Enviar", click -> send());
        send.setId(SEND_ID);
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), send);
    }

    private void send() {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        try {
            AdminNotificationDto sent = client.sendTestEmail(form.toRequest());
            close();
            Screens.success("Correo de prueba en cola para " + form.toRequest().to() + ": como le va se ve en Entregas");
            onSent.accept(sent);
        } catch (ValidationApiException validation) {
            if (validation.getProblem().hasFieldErrors()) {
                Screens.showValidation(binder, validation);
            } else {
                UiErrors.show(validation);
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.dto.notification.AdminNotificationDto;
import com.alejandro.mtobackoffice.client.dto.notification.AudienceKind;
import com.alejandro.mtobackoffice.client.dto.notification.BroadcastChannel;
import com.alejandro.mtobackoffice.client.dto.notification.BroadcastRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.Required;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.function.SerializableConsumer;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Un aviso general ({@code POST /admin/broadcasts}): un titulo, y si se quiere un cuerpo y un enlace,
 * para las audiencias que se escriban ({@code CLASE:clave}, una por linea) y por los canales que se
 * elijan. Solo se ofrece lo que el servicio acepta (sus cuatro clases de audiencia, sus dos canales,
 * sus tres gravedades) y se comprueba lo evidente antes de llamar. Llega a personas de verdad y no se
 * puede retirar, asi que antes de enviarlo se confirma, diciendo a quien va y por donde.
 */
public class BroadcastDialog extends Dialog {

    static final String SEND_ID = "broadcast-send";
    static final String LINK_RULE = "Una ruta de la aplicacion (/notificaciones) o una direccion http(s)";

    private final Binder<BroadcastForm> binder = new Binder<>(BroadcastForm.class);
    private final BroadcastForm form = new BroadcastForm();
    private final NotificationClient client;
    private final SerializableConsumer<AdminNotificationDto> onSent;

    public BroadcastDialog(NotificationClient client, SerializableConsumer<AdminNotificationDto> onSent) {
        this.client = client;
        this.onSent = onSent;
        setHeaderTitle("Aviso general");
        setCloseOnOutsideClick(false);
        setWidth("min(44rem, 96vw)");

        TextField title = new TextField("Titulo");
        title.setId("broadcast-title");
        TextArea body = new TextArea("Cuerpo");
        body.setId("broadcast-body");
        body.setMinHeight("6rem");
        TextField link = new TextField("Enlace");
        link.setId("broadcast-link");
        link.setHelperText(LINK_RULE);
        Select<ActivitySeverity> severity = new Select<>();
        severity.setId("broadcast-severity");
        severity.setLabel("Gravedad");
        severity.setItems(ActivitySeverity.selectable());
        severity.setItemLabelGenerator(ActivitySeverity::label);
        TextArea audiences = new TextArea("Audiencias");
        audiences.setId("broadcast-audiences");
        audiences.setMinHeight("6rem");
        audiences.setHelperText("Una por linea, CLASE:clave: " + Arrays.stream(AudienceKind.values()).map(AudienceKind::example)
                .collect(Collectors.joining(", ")));
        MultiSelectComboBox<BroadcastChannel> channels = new MultiSelectComboBox<>("Canales");
        channels.setId("broadcast-channels");
        channels.setItems(BroadcastChannel.values());
        channels.setItemLabelGenerator(BroadcastChannel::label);
        channels.setHelperText("El correo va a la direccion de cada persona de la audiencia");

        binder.forField(title).asRequired(Required.text("El titulo es obligatorio"))
                .withValidator(value -> value.trim().length() <= BroadcastForm.TITLE_MAX, "Como mucho " + BroadcastForm.TITLE_MAX + " caracteres")
                .bind("title");
        binder.forField(body).withValidator(value -> value.length() <= BroadcastForm.BODY_MAX, "Como mucho " + BroadcastForm.BODY_MAX + " caracteres")
                .bind("body");
        binder.forField(link).withValidator(value -> value.trim().length() <= BroadcastForm.LINK_MAX, "Como mucho " + BroadcastForm.LINK_MAX + " caracteres")
                // Lo que la bandeja no sabria seguir (NotificationLinks): ni una ruta ni un http(s).
                .withValidator(value -> value.isBlank() || NotificationLinks.target(value).isPresent(), LINK_RULE)
                .bind("link");
        binder.forField(severity).asRequired("La gravedad es obligatoria").bind("severity");
        binder.forField(audiences).withValidator((value, context) -> BroadcastForm.audiencesProblem(value)
                .map(ValidationResult::error).orElseGet(ValidationResult::ok)).bind("audiences");
        audiences.setRequiredIndicatorVisible(true);
        binder.forField(channels).withValidator(value -> !value.isEmpty(), "Al menos un canal").bind("channels");
        channels.setRequiredIndicatorVisible(true);
        binder.readBean(form);

        FormLayout layout = new FormLayout(title, body, link, severity, audiences, channels);
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        add(layout);

        Button send = new Button("Enviar", click -> confirm());
        send.setId(SEND_ID);
        send.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", click -> close()), send);
    }

    /** Lo evidente primero; si vale, la confirmacion, que dice a quien va y por donde. */
    private void confirm() {
        if (!binder.writeBeanIfValid(form)) {
            return;
        }
        BroadcastRequest request = form.toRequest();
        ConfirmDialog dialog = new ConfirmDialog("Enviar el aviso general", confirmation(form, request), "Enviar", confirm -> send(request),
                "Volver", cancel -> { });
        dialog.setConfirmButtonTheme("primary");
        dialog.open();
    }

    static String confirmation(BroadcastForm form, BroadcastRequest request) {
        String channels = Arrays.stream(BroadcastChannel.values()).filter(form.getChannels()::contains).map(BroadcastChannel::label)
                .collect(Collectors.joining(" y "));
        int audiences = request.audiences().size();
        return "«" + request.title() + "» se envia a " + audiences + (audiences == 1 ? " audiencia (" : " audiencias (")
                + String.join(", ", request.audiences()) + ") por " + channels
                + ". Llega a personas de verdad y no se puede retirar. ¿Enviar?";
    }

    private void send(BroadcastRequest request) {
        try {
            AdminNotificationDto sent = client.broadcast(request);
            close();
            int audiences = sent.audiences().isEmpty() ? request.audiences().size() : sent.audiences().size();
            Screens.success("Aviso enviado a " + audiences + (audiences == 1 ? " audiencia" : " audiencias"));
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

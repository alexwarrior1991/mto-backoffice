package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

/** Lo que repiten las pantallas de mantenimiento: botones de fila, confirmaciones y avisos. */
final class MaintenanceUi {

    /** Un PUT parcial no puede vaciar un numero ni una referencia: {@code null} es «no tocar». */
    static final String CANNOT_CLEAR = "No se puede vaciar: el servicio conserva el valor si no se manda";

    private MaintenanceUi() {
    }

    static Button rowButton(String id, VaadinIcon icon, String tooltip, ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(icon.create(), listener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        button.setTooltipText(tooltip);
        button.setId(id);
        return button;
    }

    static void confirm(String header, String text, String confirmText, Runnable action) {
        ConfirmDialog dialog = new ConfirmDialog(header, text, confirmText, confirm -> action.run(), "Volver", cancel -> { });
        dialog.setConfirmButtonTheme("primary");
        dialog.open();
    }

    static void success(String message) {
        Notification.show(message, 3000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /** Los errores del servicio campo a campo; lo que no cae en un campo, como notificacion. El dialogo sigue abierto. */
    static void showValidation(Binder<?> binder, ValidationApiException validation) {
        List<String> unattributed = ServerValidation.apply(binder, validation);
        if (!unattributed.isEmpty()) {
            Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    static String yesNo(Boolean value) {
        return value == null ? "" : value ? "Si" : "No";
    }
}

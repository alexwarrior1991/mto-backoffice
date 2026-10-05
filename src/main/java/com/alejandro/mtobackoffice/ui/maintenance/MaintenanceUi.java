package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.alejandro.mtobackoffice.ui.support.ServerValidation;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Lo que repiten las pantallas de mantenimiento: botones de fila, confirmaciones y avisos. */
final class MaintenanceUi {

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

    /**
     * Para un dialogo sin Binder, como hace mto-frontend con sus alias: cada error por campo del
     * servicio cae en el componente que se llama como su campo (varios nombres pueden ir al mismo), lo
     * demas se notifica, y sin errores por campo es la notificacion de siempre. El dialogo sigue abierto.
     */
    static void showValidation(Map<String, ? extends HasValidation> fields, ValidationApiException validation) {
        if (!validation.getProblem().hasFieldErrors()) {
            UiErrors.show(validation);
            return;
        }
        List<String> unattributed = new ArrayList<>();
        for (ApiFieldError error : validation.getProblem().errors()) {
            HasValidation field = error.field() == null ? null : fields.get(error.field());
            if (field == null) {
                unattributed.add(ServerValidation.message(error));
            } else {
                field.setErrorMessage(ServerValidation.message(error));
                field.setInvalid(true);
            }
        }
        if (!unattributed.isEmpty()) {
            Notification.show(String.join(". ", unattributed), 8000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    static final String LOAD_FAILURE_ID = "detail-load-failure";
    static final String LOAD_RETRY_ID = "detail-load-retry";

    /**
     * Una ficha que no se ha podido leer por algo que no es un 404, como en mto-frontend: lo que habia
     * se esconde y se ensena el motivo con «Volver a la lista» y «Reintentar». Un 404 no pasa por aqui:
     * dice que no existe y vuelve a la lista.
     */
    static void showLoadFailure(VerticalLayout view, String what, BackofficeApiException failure, Runnable back, Runnable retry) {
        clearLoadFailure(view);
        List<Component> hidden = view.getChildren().filter(Component::isVisible).toList();
        hidden.forEach(child -> child.setVisible(false));
        ComponentUtil.setData(view, HiddenByFailure.class, new HiddenByFailure(hidden));
        Button backButton = new Button("Volver a la lista", VaadinIcon.ARROW_LEFT.create(), click -> back.run());
        Button retryButton = new Button("Reintentar", VaadinIcon.REFRESH.create(), click -> retry.run());
        retryButton.setId(LOAD_RETRY_ID);
        retryButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        VerticalLayout panel = new VerticalLayout(new Paragraph("No se ha podido leer " + what + ": " + UiErrors.message(failure)),
                new HorizontalLayout(backButton, retryButton));
        panel.setId(LOAD_FAILURE_ID);
        panel.setPadding(false);
        view.addComponentAsFirst(panel);
    }

    /** La ficha se ha leido: fuera el aviso, y lo que se escondio vuelve a verse. */
    static void clearLoadFailure(VerticalLayout view) {
        view.getChildren().filter(child -> LOAD_FAILURE_ID.equals(child.getId().orElse(null))).toList().forEach(view::remove);
        HiddenByFailure hidden = ComponentUtil.getData(view, HiddenByFailure.class);
        if (hidden != null) {
            hidden.components().forEach(child -> child.setVisible(true));
            ComponentUtil.setData(view, HiddenByFailure.class, null);
        }
    }

    private record HiddenByFailure(List<Component> components) {
    }

    static String yesNo(Boolean value) {
        return value == null ? "" : value ? "Si" : "No";
    }
}

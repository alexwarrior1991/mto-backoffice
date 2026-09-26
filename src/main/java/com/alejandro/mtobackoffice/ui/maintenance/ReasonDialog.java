package com.alejandro.mtobackoffice.ui.maintenance;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.function.Predicate;

/**
 * Una cancelacion con su motivo, obligatorio: el de una orden, una tarea o un turno. La accion
 * devuelve si se hizo; si el servicio la rechaza, el dialogo sigue abierto con lo escrito.
 */
class ReasonDialog extends Dialog {

    static final String REASON_ID = "reason-text";
    static final String CONFIRM_ID = "reason-confirm";

    ReasonDialog(String header, String text, String confirmText, Predicate<String> action) {
        setHeaderTitle(header);
        setCloseOnOutsideClick(false);
        TextArea reason = new TextArea("Motivo");
        reason.setId(REASON_ID);
        reason.setRequired(true);
        reason.setWidthFull();
        add(new Paragraph(text), reason);

        Button confirm = new Button(confirmText, click -> {
            String value = reason.getValue() == null ? "" : reason.getValue().trim();
            if (value.isEmpty()) {
                reason.setErrorMessage("El motivo es obligatorio");
                reason.setInvalid(true);
                return;
            }
            if (action.test(value)) {
                close();
            }
        });
        confirm.setId(CONFIRM_ID);
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        getFooter().add(new Button("Volver", click -> close()), confirm);
    }
}

package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.data.binder.Validator;

/**
 * Lo obligatorio de un formulario. Para el Binder un texto de solo espacios no esta vacio, y el
 * servicio lo recibiria recortado y vacio: aqui, como en mto-frontend, cuenta como vacio y no sale.
 */
public final class Required {

    private Required() {
    }

    /** Para {@code asRequired(...)}: un texto con algo que no sea espacio. */
    public static Validator<String> text(String message) {
        return Validator.from(value -> value != null && !value.isBlank(), message);
    }
}

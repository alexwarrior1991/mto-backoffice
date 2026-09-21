package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;

/**
 * Vuelca los errores campo a campo del servicio ({@code errors[{field, code, message}]}) sobre los
 * campos de un formulario. La validacion de negocio vive en el servicio; la UI solo la ensena
 * donde corresponde. Lo que no se pueda atribuir a un campo se devuelve para la notificacion.
 */
public final class ServerValidation {

    private ServerValidation() {
    }

    public static List<String> apply(Binder<?> binder, ValidationApiException exception) {
        List<String> unattributed = new ArrayList<>();
        for (ApiFieldError error : exception.getProblem().errors()) {
            String message = message(error);
            HasValidation field = error.field() == null ? null : binder.getBinding(error.field())
                    .map(Binder.Binding::getField)
                    .filter(HasValidation.class::isInstance)
                    .map(HasValidation.class::cast)
                    .orElse(null);
            if (field == null) {
                unattributed.add(message);
            } else {
                field.setErrorMessage(message);
                field.setInvalid(true);
            }
        }
        if (exception.getProblem().errors().isEmpty()) {
            unattributed.add(UiErrors.message(exception));
        }
        return unattributed;
    }

    private static String message(ApiFieldError error) {
        if (error.message() != null && !error.message().isBlank()) {
            return error.message();
        }
        return error.code() == null ? "Valor no valido" : error.code();
    }
}

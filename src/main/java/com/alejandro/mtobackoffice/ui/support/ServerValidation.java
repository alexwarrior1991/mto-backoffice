package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Vuelca los errores campo a campo del servicio ({@code errors[{field, code, message}]}) sobre los
 * campos de un formulario. La validacion de negocio vive en el servicio; la UI solo la ensena
 * donde corresponde. Lo que no se pueda atribuir a un campo se devuelve para la notificacion.
 */
public final class ServerValidation {

    private ServerValidation() {
    }

    public static List<String> apply(Binder<?> binder, ValidationApiException exception) {
        return apply(binder, exception, Map.of(), Map.of());
    }

    /**
     * Como {@link #apply(Binder, ValidationApiException)}, para los campos del servicio que en el
     * formulario no se llaman igual, como hace mto-frontend con sus alias.
     *
     * @param aliases   campo del servicio → propiedad del formulario ({@code sourceWarehouseId} de una
     *                  transferencia es el {@code warehouseId} del dialogo)
     * @param elsewhere campo del servicio → donde se ensena su mensaje si no es un campo del Binder (la
     *                  lista de materiales de un conjunto); vale tambien para sus elementos
     *                  ({@code components[0].quantity})
     */
    public static List<String> apply(Binder<?> binder, ValidationApiException exception, Map<String, String> aliases,
                                     Map<String, Consumer<String>> elsewhere) {
        List<String> unattributed = new ArrayList<>();
        for (ApiFieldError error : exception.getProblem().errors()) {
            String message = message(error);
            Optional<Consumer<String>> other = error.field() == null ? Optional.empty() : elsewhere.entrySet().stream()
                    .filter(entry -> error.field().equals(entry.getKey()) || error.field().startsWith(entry.getKey() + "[")
                            || error.field().startsWith(entry.getKey() + "."))
                    .map(Map.Entry::getValue)
                    .findFirst();
            if (other.isPresent()) {
                other.get().accept(message);
                continue;
            }
            String property = error.field() == null ? null : aliases.getOrDefault(error.field(), error.field());
            HasValidation field = property == null ? null : binder.getBinding(property)
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

package com.alejandro.mtobackoffice.ui.support;

import com.alejandro.mtobackoffice.client.error.ApiFieldError;
import com.alejandro.mtobackoffice.client.error.ValidationApiException;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Vuelca los errores campo a campo del servicio ({@code errors[{field, code, message}]}) sobre los
 * campos de un formulario. La validacion de negocio vive en el servicio; la UI solo la ensena
 * donde corresponde. Lo que no se pueda atribuir a un campo se devuelve para la notificacion. El
 * elemento de una lista de valores ({@code sectionings[1]}) va al campo de su lista.
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
        return apply(binder, exception, aliases, elsewhere, Map.of(), false);
    }

    /**
     * El error de un hijo que su editor lleva en una tabla y no en el Binder: la posicion del hijo en
     * la coleccion que se mando ({@code null} si el error es de la coleccion entera) y su campo tal
     * como lo nombra el servicio, sin traducir ({@code steadyArm.steadyArmType}).
     */
    public record ChildError(Integer index, String field, String message) {

        /** «Fila 2 · cantileverType: …»: la fila que ocupa en la tabla, que es el orden en que viajo. */
        public String text() {
            String row = index == null ? "" : "Fila " + (index + 1);
            String where = row.isEmpty() || field.isEmpty() ? row + field : row + " · " + field;
            return where.isEmpty() ? message : where + ": " + message;
        }
    }

    /**
     * Para los editores de maestros de mto-configuration, que dice cada error con la ruta de su campo
     * desde la raiz del cuerpo. Ninguno se pierde, y cada uno acaba en el primero de estos sitios que
     * case, como en mto-frontend ({@code applyServerErrors}):
     * <ol>
     * <li>su campo del Binder ({@code poleType});</li>
     * <li>el campo de la lista, si es un elemento de una lista de valores ({@code sectionings[1]});</li>
     * <li>la tabla de hijos que el editor lleva fuera del Binder ({@code children}, por el nombre de la
     * coleccion en el cuerpo: {@code cantilevers[1].cantileverType}). Se llama siempre, tambien con
     * una lista vacia, para que lo de un intento anterior no se quede pintado;</li>
     * <li>lo demas se devuelve con su ruta delante, para la notificacion
     * ({@code disconnectors[0].connectedTrackId: ...}).</li>
     * </ol>
     */
    public static List<String> applyWithPaths(Binder<?> binder, ValidationApiException exception,
                                              Map<String, Consumer<List<ChildError>>> children) {
        return apply(binder, exception, Map.of(), Map.of(), children, true);
    }

    private static List<String> apply(Binder<?> binder, ValidationApiException exception, Map<String, String> aliases,
                                      Map<String, Consumer<String>> elsewhere, Map<String, Consumer<List<ChildError>>> children,
                                      boolean withPaths) {
        List<String> unattributed = new ArrayList<>();
        Map<String, List<ChildError>> byChild = new LinkedHashMap<>();
        children.keySet().forEach(name -> byChild.put(name, new ArrayList<>()));
        Set<HasValidation> marked = new HashSet<>();
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
            HasValidation field = boundField(binder, property);
            if (field == null && property != null) {
                // El elemento de una lista de valores (sectionings[1]) no tiene campo: va al de la lista.
                Matcher element = LIST_ELEMENT.matcher(property);
                field = element.matches() ? boundField(binder, element.group(1)) : null;
            }
            if (field != null) {
                // Dos elementos de la misma lista caen en el mismo campo: se dicen los dos.
                boolean again = !marked.add(field) && field.getErrorMessage() != null && !field.getErrorMessage().isBlank();
                field.setErrorMessage(again ? field.getErrorMessage() + ". " + message : message);
                field.setInvalid(true);
                continue;
            }
            Matcher child = error.field() == null ? null : CHILD.matcher(error.field());
            if (child != null && child.matches() && byChild.containsKey(child.group(1))) {
                byChild.get(child.group(1)).add(new ChildError(child.group(2) == null ? null : Integer.valueOf(child.group(2)),
                        child.group(3) == null ? "" : child.group(3), message));
                continue;
            }
            unattributed.add(withPaths && error.field() != null && !error.field().isBlank() ? error.field() + ": " + message : message);
        }
        children.forEach((name, show) -> show.accept(List.copyOf(byChild.get(name))));
        if (exception.getProblem().errors().isEmpty()) {
            unattributed.add(UiErrors.message(exception));
        }
        return unattributed;
    }

    /** {@code sectionings[1]}: el elemento de una lista. */
    private static final Pattern LIST_ELEMENT = Pattern.compile("(.+)\\[\\d+]");
    /** {@code cantilevers[1].steadyArm.steadyArmType}: la coleccion, la posicion y el campo del hijo. */
    private static final Pattern CHILD = Pattern.compile("([^.\\[\\]]+)(?:\\[(\\d+)])?(?:\\.(.+))?");

    private static HasValidation boundField(Binder<?> binder, String property) {
        return property == null ? null : binder.getBinding(property)
                .map(Binder.Binding::getField)
                .filter(HasValidation.class::isInstance)
                .map(HasValidation.class::cast)
                .orElse(null);
    }

    /** El texto de un error por campo: su mensaje, o su codigo si no trae mensaje. */
    public static String message(ApiFieldError error) {
        if (error.message() != null && !error.message().isBlank()) {
            return error.message();
        }
        return error.code() == null ? "Valor no valido" : error.code();
    }
}

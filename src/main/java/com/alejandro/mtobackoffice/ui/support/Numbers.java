package com.alejandro.mtobackoffice.ui.support;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.HasValidator;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Las comprobaciones de un numero antes de llamar al servicio, las mismas que hace mto-frontend y
 * con los limites de las columnas del servicio: una cantidad de almacen o de mantenimiento es
 * {@code numeric(19,6)} (13 enteros y 6 decimales), un KP de mantenimiento {@code numeric(12,3)}
 * (9 y 3, con signo), y las medidas de infraestructura son enteras o no negativas segun su columna.
 * Lo demas (un rango, una regla de negocio) lo decide el servicio.
 *
 * <p>Los decimales se cuentan como se escribieron (un {@code BigDecimalField} conserva la escala):
 * {@code 1.5000000} son siete, como en la expresion de la SPA. Un valor vacio pasa: lo obligatorio
 * lo dice {@code asRequired}.</p>
 */
public final class Numbers {

    /** Una cantidad de almacen o de mantenimiento: {@code numeric(19,6)}. */
    public static final int QUANTITY_INTEGERS = 13;
    public static final int QUANTITY_DECIMALS = 6;
    /** Un KP de mantenimiento, y un valor medido de un checklist: {@code numeric(12,3)}. */
    public static final int KP_INTEGERS = 9;
    public static final int KP_DECIMALS = 3;
    /** Lo que se dice de un texto que el campo no puede leer como numero. */
    public static final String UNREADABLE = "No es un numero: escribelo con cifras, como 12.5";

    private Numbers() {
    }

    /** Una cantidad mayor que cero, con sus 13 enteros y 6 decimales. */
    public static Validator<BigDecimal> positiveQuantity() {
        return all(Validator.from(value -> value == null || value.signum() > 0, "Tiene que ser mayor que cero"),
                digits(QUANTITY_INTEGERS, QUANTITY_DECIMALS));
    }

    /** Una cantidad que puede ser cero, con sus 13 enteros y 6 decimales. */
    public static Validator<BigDecimal> quantity() {
        return all(nonNegative(), digits(QUANTITY_INTEGERS, QUANTITY_DECIMALS));
    }

    /** Un KP de mantenimiento: con signo, 9 enteros y 3 decimales. */
    public static Validator<BigDecimal> kp() {
        return digits(KP_INTEGERS, KP_DECIMALS);
    }

    /** Un valor medido de un checklist (la medida y lo que quedo tras el ajuste): como un KP. */
    public static Validator<BigDecimal> measure() {
        return digits(KP_INTEGERS, KP_DECIMALS);
    }

    /** Una medida que no puede ser negativa. */
    public static Validator<BigDecimal> nonNegative() {
        return Validator.from(value -> value == null || value.signum() >= 0, "No puede ser negativo");
    }

    /** Una medida entera, sin decimales escritos ({@code 12.0} tampoco); con {@code signed} admite el signo. */
    public static Validator<BigDecimal> whole(boolean signed) {
        Validator<BigDecimal> whole = Validator.from(value -> value == null || value.scale() <= 0, "Un numero entero, sin decimales");
        return signed ? whole : all(whole, nonNegative());
    }

    /** Como mucho {@code integers} cifras enteras y {@code decimals} decimales, como se escribieron. */
    public static Validator<BigDecimal> digits(int integers, int decimals) {
        return (value, context) -> {
            if (value == null) {
                return ValidationResult.ok();
            }
            if (value.scale() > decimals) {
                return ValidationResult.error("Como mucho " + decimals + " decimales");
            }
            if (integerDigits(value) > integers) {
                return ValidationResult.error("Como mucho " + integers + " cifras enteras");
            }
            return ValidationResult.ok();
        };
    }

    /** Varias comprobaciones en orden: dice la primera que no pasa. */
    @SafeVarargs
    public static <V> Validator<V> all(Validator<V>... validators) {
        return (value, context) -> {
            for (Validator<V> validator : validators) {
                ValidationResult result = validator.apply(value, context);
                if (result.isError()) {
                    return result;
                }
            }
            return ValidationResult.ok();
        };
    }

    /** Lo obligatorio de un dialogo sin {@code Binder}, para {@link #check}. */
    public static <V> Validator<V> required(String message) {
        return Validator.from(Objects::nonNull, message);
    }

    /**
     * El minimo de un campo entero, con su mensaje. El {@code Binder} aplica antes que ninguna otra la
     * comprobacion propia del campo (su minimo y lo que no sabe leer), y sin estos textos el campo se
     * queda en rojo sin decir por que.
     */
    public static IntegerField atLeast(IntegerField field, int min, String message) {
        field.setMin(min);
        field.setI18n(new IntegerField.IntegerFieldI18n().setMinErrorMessage(message).setBadInputErrorMessage(UNREADABLE));
        return field;
    }

    /**
     * Para un dialogo sin {@code Binder}, lo mismo que haria uno: primero la comprobacion propia del
     * campo (lo que no pudo leer como numero, que da por vacio y viajaria como «vaciar», y su minimo) y
     * despues la nuestra. Marca el campo con su mensaje si no pasa y dice si paso.
     */
    public static <V, F extends Component & HasValue<?, V> & HasValidation & HasValidator<V>> boolean check(F field, Validator<V> validator) {
        V value = field.getValue();
        ValueContext context = new ValueContext(field, field);
        ValidationResult own = field.getDefaultValidator().apply(value, context);
        ValidationResult result = own.isError()
                ? ValidationResult.error(own.getErrorMessage() == null || own.getErrorMessage().isBlank() ? UNREADABLE : own.getErrorMessage())
                : validator.apply(value, context);
        field.setInvalid(result.isError());
        field.setErrorMessage(result.isError() ? result.getErrorMessage() : null);
        return !result.isError();
    }

    private static int integerDigits(BigDecimal value) {
        BigDecimal integral = value.abs().setScale(0, RoundingMode.DOWN);
        return integral.signum() == 0 ? 0 : integral.precision();
    }
}

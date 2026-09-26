package com.alejandro.mtobackoffice.client.dto;

import java.util.Arrays;
import java.util.List;

/**
 * Lo comun de los enumerados que se leen de un servicio. El servicio puede estrenar un valor (un
 * estado, un tipo) antes que esta aplicacion; con un enumerado estricto, una sola fila con ese valor
 * haria ilegible la pagina entera, y el fallo ni siquiera se notificaria, porque las vistas solo
 * capturan {@code BackofficeApiException}. Por eso cada enumerado lleva {@code UNKNOWN}: el valor
 * que no conoce se lee asi, se pinta «Desconocido», no se ofrece en ningun filtro ni formulario y no
 * abre ninguna accion. El mapper global no se toca: cada enumerado declara su {@code @JsonCreator}.
 *
 * <p>No lo llevan los enumerados que solo viajan en peticiones ni los de los maestros de
 * mto-configuration, que se devuelven enteros: un {@code UNKNOWN} volveria al servicio como un valor
 * que no existe.</p>
 */
public final class ClientEnums {

    public static final String UNKNOWN_LABEL = "Desconocido";

    private ClientEnums() {
    }

    /** El valor por su nombre; {@code null} sigue siendo {@code null} y uno que no se conoce es {@code unknown}. */
    public static <E extends Enum<E>> E parse(Class<E> type, String value, E unknown) {
        if (value == null) {
            return null;
        }
        for (E constant : type.getEnumConstants()) {
            if (constant.name().equals(value)) {
                return constant;
            }
        }
        return unknown;
    }

    /** Lo que se puede elegir: todos menos {@code unknown}, en su orden. */
    public static <E extends Enum<E>> List<E> selectable(Class<E> type, E unknown) {
        return Arrays.stream(type.getEnumConstants()).filter(constant -> constant != unknown).toList();
    }
}

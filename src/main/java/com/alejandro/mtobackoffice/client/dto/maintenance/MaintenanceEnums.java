package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.Arrays;
import java.util.List;

/**
 * Lo comun de los enumerados de mto-maintenance. El servicio puede estrenar un valor (un estado, un
 * tipo) antes que esta aplicacion; con un enumerado estricto, una sola fila con ese valor haria
 * ilegible la pagina entera. Por eso cada enumerado lleva {@code UNKNOWN}: el valor que no conoce se
 * lee asi, se pinta «Desconocido», no se ofrece en ningun filtro ni formulario y no abre ninguna
 * transicion. El mapper global no se toca: cada enumerado declara su {@code @JsonCreator}.
 */
public final class MaintenanceEnums {

    public static final String UNKNOWN_LABEL = "Desconocido";

    private MaintenanceEnums() {
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

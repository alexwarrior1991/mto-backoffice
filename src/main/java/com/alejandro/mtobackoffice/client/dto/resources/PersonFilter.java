package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Los filtros de la lista de personas; lo que va a {@code null} (o vacio) no viaja. {@code search}
 * busca en el codigo, el nombre, los apellidos, el usuario y el numero de empleado; {@code categories}
 * es cualquiera de ellas ({@code category} repetido); {@code unitId}, quien dirige esa unidad o es uno
 * de sus linemen ahora.
 */
public record PersonFilter(String search, Set<PersonCategory> categories, UUID unitId, Boolean active) {

    public static final PersonFilter NONE = new PersonFilter(null, Set.of(), null, null);

    public PersonFilter {
        search = search == null || search.isBlank() ? null : search.trim();
        // En el orden del enumerado: la query sale igual cada vez, sea cual sea el orden en que se eligieron.
        categories = categories == null || categories.isEmpty() ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(categories));
    }

    /** Para un selector: activas de estas categorias, buscando por texto. */
    public static PersonFilter activeOf(String search, Set<PersonCategory> categories) {
        return new PersonFilter(search, categories, null, true);
    }
}

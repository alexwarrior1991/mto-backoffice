package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Objects;
import java.util.UUID;

/**
 * Una persona dentro de otra respuesta: el jefe de una unidad, un lineman, el supervisor de un equipo.
 * Es la misma persona si es el mismo id, aunque el nombre haya cambiado entre dos lecturas: asi un
 * desplegable reconoce como elegida la persona leida de la unidad entre las que trae la busqueda.
 */
public record PersonSummaryDto(UUID id, String code, String fullName, String username, PersonCategory category) {

    public String label() {
        return fullName == null || fullName.isBlank() ? code : code + " · " + fullName;
    }

    public static PersonSummaryDto of(PersonDto person) {
        return new PersonSummaryDto(person.id(), person.code(), person.fullName(), person.username(), person.category());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PersonSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

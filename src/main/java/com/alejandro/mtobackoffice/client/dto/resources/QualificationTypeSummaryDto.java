package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Objects;
import java.util.UUID;

/**
 * Un tipo de cualificacion dentro de otra respuesta: el de una cualificacion de una persona, la
 * habilitacion que exige un modelo a su conductor. Es el mismo si es el mismo id, como
 * {@link PersonSummaryDto}, para que un desplegable reconozca el leido.
 */
public record QualificationTypeSummaryDto(UUID id, String code, String name, QualificationKind kind) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof QualificationTypeSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

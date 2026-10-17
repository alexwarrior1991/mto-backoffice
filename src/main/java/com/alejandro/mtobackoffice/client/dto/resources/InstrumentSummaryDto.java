package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Un instrumento dentro de otra respuesta: los de un turno de mto-maintenance, con la fecha de su
 * calibracion. Es el mismo si es el mismo id, como {@link PersonSummaryDto}, para que el desplegable
 * del turno reconozca los leidos entre los que trae la busqueda.
 */
public record InstrumentSummaryDto(UUID id, String code, String name, InstrumentKind kind, LocalDate calibrationExpiresOn) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof InstrumentSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

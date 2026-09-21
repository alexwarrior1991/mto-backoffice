package com.alejandro.mtobackoffice.client.dto.master;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Referencia a una entrada de catalogo dentro de un maestro ({@code poleType}, {@code sectionings}...).
 * El servicio la resuelve por {@code id} o por {@code code}, asi que se envian los dos; la
 * descripcion solo sirve para ensenarla. La igualdad es por id (o por codigo si no hay id), que
 * es lo que permite a un ComboBox reconocer como suya la referencia que llego del servicio.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LovRef(Long id, String code, String description) {

    public String label() {
        if (description == null || description.isBlank()) {
            return code == null ? "" : code;
        }
        return code == null ? description : code + " - " + description;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LovRef that)) {
            return false;
        }
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        return code != null && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : (code == null ? 0 : code.hashCode());
    }
}

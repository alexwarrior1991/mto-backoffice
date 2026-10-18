package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;

/**
 * Los filtros de la lista de instrumentos; lo que va a {@code null} no viaja.
 * {@code calibrationExpiresBefore}: los que tienen la calibracion caducada o a punto antes de ese dia.
 */
public record InstrumentFilter(String search, InstrumentKind kind, Boolean active, LocalDate calibrationExpiresBefore) {

    public static final InstrumentFilter NONE = new InstrumentFilter(null, null, null, null);

    public InstrumentFilter {
        search = search == null || search.isBlank() ? null : search.trim();
    }

    /** Para el desplegable de un turno: los activos, buscando por texto. */
    public static InstrumentFilter activeMatching(String search) {
        return new InstrumentFilter(search, null, true, null);
    }
}

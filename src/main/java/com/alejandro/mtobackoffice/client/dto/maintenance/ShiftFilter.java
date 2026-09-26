package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.LocalDate;
import java.util.UUID;

/** Los filtros de la lista de turnos; lo que va a {@code null} no viaja. Las fechas son inclusivas. */
public record ShiftFilter(LocalDate dateFrom, LocalDate dateTo, UUID teamId, Long trackId, Long executionPackageId, ShiftStatus status,
                          PossessionType possessionType) {

    public static final ShiftFilter NONE = new ShiftFilter(null, null, null, null, null, null, null);

    /** Los turnos en curso de una via: donde se puede trabajar una tarea de esa via. */
    public static ShiftFilter inProgressOn(Long trackId) {
        return new ShiftFilter(null, null, null, trackId, null, ShiftStatus.IN_PROGRESS, null);
    }
}

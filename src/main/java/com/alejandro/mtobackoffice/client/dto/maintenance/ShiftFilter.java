package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Los filtros de la lista de turnos; lo que va a {@code null} (o vacio) no viaja. Las fechas son
 * inclusivas. {@code trackIds} viaja como {@code trackId} repetido: los turnos que recorren alguna de
 * esas vias, cada uno una vez (las dos de un aislador que las une).
 */
public record ShiftFilter(LocalDate dateFrom, LocalDate dateTo, UUID teamId, List<Long> trackIds, Long executionPackageId, ShiftStatus status,
                          PossessionType possessionType) {

    public static final ShiftFilter NONE = new ShiftFilter(null, null, null, null, null, null, null);

    public ShiftFilter {
        trackIds = trackIds == null ? List.of() : trackIds.stream().filter(Objects::nonNull).distinct().toList();
    }

    /** Los turnos en curso de unas vias: donde se puede trabajar una tarea de ellas. */
    public static ShiftFilter inProgressOn(Long... trackIds) {
        return inProgressOn(Arrays.asList(trackIds));
    }

    public static ShiftFilter inProgressOn(List<Long> trackIds) {
        return new ShiftFilter(null, null, null, trackIds, null, ShiftStatus.IN_PROGRESS, null);
    }

    /** Todos los turnos de unas vias, sin mirar su estado. */
    public static ShiftFilter on(List<Long> trackIds) {
        return new ShiftFilter(null, null, null, trackIds, null, null, null);
    }
}

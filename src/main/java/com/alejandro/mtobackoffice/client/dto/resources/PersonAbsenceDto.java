package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Una ausencia de una persona, con las dos fechas incluidas. Dos de la misma persona no se solapan (409
 * {@code ABS-001}); mto-maintenance deja fuera de sus turnos a quien esta ausente a la fecha del turno.
 */
public record PersonAbsenceDto(UUID id, UUID personId, AbsenceKind kind, LocalDate startsOn, LocalDate endsOn, String notes, Long version) {

    /** Los dias que dura, contando los dos extremos; sin fechas, ninguno. */
    public long days() {
        return startsOn == null || endsOn == null ? 0 : ChronoUnit.DAYS.between(startsOn, endsOn) + 1;
    }

    /** La clase, como se nombra en los titulos y las confirmaciones. */
    public String label() {
        return kind == null ? "" : kind.label();
    }
}

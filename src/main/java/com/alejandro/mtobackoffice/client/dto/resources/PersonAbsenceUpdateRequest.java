package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;

/** Lo que se cambia de una ausencia, para un {@code MergePatch}: la clase y las fechas se cambian; solo las notas se vacian. */
public record PersonAbsenceUpdateRequest(AbsenceKind kind, LocalDate startsOn, LocalDate endsOn, String notes) {
}

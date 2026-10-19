package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/** El alta de una ausencia; las notas vacias no viajan. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonAbsenceRequest(AbsenceKind kind, LocalDate startsOn, LocalDate endsOn, String notes) {
}

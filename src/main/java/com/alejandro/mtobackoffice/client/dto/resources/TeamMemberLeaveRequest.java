package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/** Un lineman que sale de un equipo; sin fecha, hoy. 409 {@code UNI-001} si el equipo se quedaria con menos de 2. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeamMemberLeaveRequest(LocalDate leftOn) {
}

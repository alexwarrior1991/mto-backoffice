package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

/** Un lineman que entra en un equipo; sin fecha, hoy. 409 {@code UNI-001} si el equipo ya tiene 6. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeamMemberRequest(UUID personId, LocalDate joinedOn) {
}

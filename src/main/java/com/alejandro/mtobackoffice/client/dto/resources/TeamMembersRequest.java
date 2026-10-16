package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Los linemen de un equipo, enteros (de 2 a 6): los que no estan salen y los que no estaban entran, de una vez. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeamMembersRequest(List<UUID> memberPersonIds, LocalDate effectiveOn) {
}

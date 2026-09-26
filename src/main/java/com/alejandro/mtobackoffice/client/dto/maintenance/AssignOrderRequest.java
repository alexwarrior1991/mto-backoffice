package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/** Asignar a un equipo, a una persona o a los dos; al menos uno (400 si no). En curso, reasigna sin cambiar de estado. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssignOrderRequest(UUID teamId, String assignedUser, String comment) {
}

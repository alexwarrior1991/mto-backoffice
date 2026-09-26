package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/** Iniciar un turno; sin {@code actualStart}, el servicio toma el instante actual. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StartShiftRequest(Instant actualStart, Instant voltageCutoffAt) {
}

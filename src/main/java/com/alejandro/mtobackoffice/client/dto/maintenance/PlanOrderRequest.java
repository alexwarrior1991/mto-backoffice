package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/** Planificar: la fecha no puede ser pasada. Es el momento en que se reservan los materiales en mto-stock. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanOrderRequest(LocalDate plannedDate, String comment) {
}

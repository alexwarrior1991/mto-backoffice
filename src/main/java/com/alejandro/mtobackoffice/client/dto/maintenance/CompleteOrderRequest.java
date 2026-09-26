package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Completar una orden en curso: sin tareas abiertas, y con alguna completada o notas de cierre.
 * {@code force} (pide {@code maintenance-supervise}) cierra aunque haya lineas de material sin
 * sincronizar con mto-stock, y lo deja escrito en las notas de cierre.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompleteOrderRequest(String closingNotes, Boolean force, String comment) {
}

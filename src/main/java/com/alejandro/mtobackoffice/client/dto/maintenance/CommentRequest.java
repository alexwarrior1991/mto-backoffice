package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

/** El cuerpo opcional de una transicion sin datos: solo un comentario para el historial. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommentRequest(String comment) {
}

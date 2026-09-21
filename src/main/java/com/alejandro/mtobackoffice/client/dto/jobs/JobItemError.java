package com.alejandro.mtobackoffice.client.dto.jobs;

/** Fallo de un elemento concreto: la posicion en lo enviado (o la fila del Excel) y el motivo. */
public record JobItemError(Integer index, String operation, String code, String message) {
}

package com.alejandro.mtobackoffice.client.dto;

/** Metadatos de una pagina: {@code totalElements} es lo que alimenta el recuento de un Grid perezoso. */
public record PageMetadata(int number, int size, long totalElements, int totalPages) {
}

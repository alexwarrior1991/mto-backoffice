package com.alejandro.mtobackoffice.client.dto.maintenance;

/** El error que habria dado la llamada sincrona: su estado, su codigo ({@code TRN-001}, {@code OPS-410}...) y su mensaje. */
public record OperationErrorDto(Integer status, String errorCode, String message) {
}

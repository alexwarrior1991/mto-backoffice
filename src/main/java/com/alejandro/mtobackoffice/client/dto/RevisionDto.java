package com.alejandro.mtobackoffice.client.dto;

/** Una revision del historial: los metadatos y la foto de la entidad tal como quedo. */
public record RevisionDto<T>(RevisionMetadataDto revision, T entity) {
}

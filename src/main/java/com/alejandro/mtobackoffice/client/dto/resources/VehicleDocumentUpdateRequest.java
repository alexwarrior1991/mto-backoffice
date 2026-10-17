package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;

/** Lo que se cambia de un documento, para un {@code MergePatch}: el tipo no cambia y la caducidad no se vacia. */
public record VehicleDocumentUpdateRequest(String reference, LocalDate issuedOn, LocalDate expiresOn, String notes) {
}

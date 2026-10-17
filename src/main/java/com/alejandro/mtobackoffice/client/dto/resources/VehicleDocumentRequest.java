package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/** El alta de un documento de un vehiculo; lo vacio no viaja. El tipo y la caducidad son obligatorios. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleDocumentRequest(VehicleDocumentKind kind, String reference, LocalDate issuedOn, LocalDate expiresOn, String notes) {
}

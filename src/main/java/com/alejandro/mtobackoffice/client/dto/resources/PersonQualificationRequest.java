package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.UUID;

/** El alta de una cualificacion de una persona; lo vacio no viaja ({@code expiresOn} vacio: no caduca). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonQualificationRequest(UUID qualificationTypeId, String reference, LocalDate issuedOn, LocalDate expiresOn, String notes) {
}

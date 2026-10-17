package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Una cualificacion de una persona: su tipo, su referencia (el numero del permiso o del certificado),
 * cuando se expidio y cuando caduca ({@code null}: no caduca). {@code expired} lo dice el servicio, con
 * la fecha de hoy en su zona; renovarla es cambiar {@code expiresOn}, porque una persona tiene una sola
 * de cada tipo.
 */
public record PersonQualificationDto(UUID id, UUID personId, QualificationTypeSummaryDto qualificationType, String reference, LocalDate issuedOn,
                                     LocalDate expiresOn, String notes, Boolean expired, Long version) {

    /** El tipo, como se nombra en los titulos y las confirmaciones. */
    public String label() {
        return qualificationType == null ? "" : qualificationType.label();
    }
}

package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;

/** Lo que se cambia de una cualificacion, para un {@code MergePatch}: el tipo no cambia, y todo lo demas se puede vaciar. */
public record PersonQualificationUpdateRequest(String reference, LocalDate issuedOn, LocalDate expiresOn, String notes) {
}

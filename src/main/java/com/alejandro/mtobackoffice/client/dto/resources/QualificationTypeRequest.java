package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

/** El alta de un tipo de cualificacion; lo vacio no viaja. El permiso solo con {@code DRIVING_LICENCE}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QualificationTypeRequest(String code, String name, QualificationKind kind, DrivingLicence drivingLicence, Integer validityMonths,
                                       String description) {
}

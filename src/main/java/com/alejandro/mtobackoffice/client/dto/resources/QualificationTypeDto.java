package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.UUID;

/**
 * Un tipo de cualificacion del catalogo de mto-resources ({@code RAIL-DRIVER}, {@code LIC-C}...). Un
 * permiso de conducir dice cual ({@code drivingLicence}); {@code validityMonths} es la vigencia
 * habitual, solo informativa. No tiene historial: es un catalogo.
 */
public record QualificationTypeDto(UUID id, String code, String name, QualificationKind kind, DrivingLicence drivingLicence,
                                   Integer validityMonths, String description, Boolean active, Long version) {

    public String label() {
        return summary().label();
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public QualificationTypeSummaryDto summary() {
        return new QualificationTypeSummaryDto(id, code, name, kind);
    }
}

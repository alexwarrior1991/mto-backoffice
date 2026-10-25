package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Un instrumento de mto-resources (una pertiga, un detector de tension, un equipo de medida) con su
 * calibracion: hasta cuando vale ({@code null}, sin calibracion) y si ya caduco
 * ({@code calibrationExpired}, del servicio). Se elige en los turnos de mantenimiento.
 */
public record InstrumentDto(UUID id, String code, String name, InstrumentKind kind, String serialNumber, LocalDate calibratedOn,
                            LocalDate calibrationExpiresOn, Boolean calibrationExpired, String notes, Boolean active, Long version) {

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public InstrumentSummaryDto summary() {
        return new InstrumentSummaryDto(id, code, name, kind, calibrationExpiresOn, calibrationExpired);
    }
}

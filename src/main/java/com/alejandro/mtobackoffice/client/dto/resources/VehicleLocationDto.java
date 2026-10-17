package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Donde estuvo o esta un vehiculo: una linea del historial, que es solo de anadir. Un turno de
 * mto-maintenance deja {@code sourceReference = mto-maintenance:shift:<id>}.
 */
public record VehicleLocationDto(UUID id, VehicleLocationKind kind, ParkingSiteSummaryDto parkingSite, Long executionPackageId, Long trackId,
                                 BigDecimal kp, BigDecimal latitude, BigDecimal longitude, String accessPoint, String description,
                                 VehicleSummaryDto carrier, VehicleAttachment attachment, VehicleLocationSource source, String sourceReference,
                                 Instant observedAt, Instant recordedAt, String recordedBy) {

    /** El prefijo con el que un turno de mto-maintenance firma la ubicacion que deja. */
    public static final String SHIFT_REFERENCE = "mto-maintenance:shift:";

    /** El turno de mto-maintenance que la dejo, si fue uno. */
    public UUID shiftId() {
        if (sourceReference == null || !sourceReference.startsWith(SHIFT_REFERENCE)) {
            return null;
        }
        try {
            return UUID.fromString(sourceReference.substring(SHIFT_REFERENCE.length()));
        } catch (IllegalArgumentException notAnId) {
            return null;
        }
    }
}

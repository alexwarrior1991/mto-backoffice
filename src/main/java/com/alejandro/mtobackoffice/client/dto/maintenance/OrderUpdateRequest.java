package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Modificacion parcial de una orden ({@code null} es «no tocar»), sin bloqueo optimista. En
 * borrador y planificada vale todo; despues solo {@code description}, {@code priority} y
 * {@code closingNotes} (el resto es 409 {@code TRN-001}).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderUpdateRequest(String title, String description, MaintenancePriority priority, LocalDate plannedDate, UUID teamId,
                                 String assignedUser, String closingNotes, Long executionPackageId, Long trackId, Long stationId,
                                 BigDecimal startKp, BigDecimal endKp, UUID stockProjectId) {

    /** Nada que mandar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return equals(new OrderUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null));
    }
}

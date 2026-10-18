package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Un documento de un vehiculo que caduca (la ITV, el seguro, el certificado de la grua...): uno de cada
 * tipo, asi que renovarlo es cambiar {@code expiresOn}, que nunca falta. {@code expired} lo dice el
 * servicio.
 */
public record VehicleDocumentDto(UUID id, UUID vehicleId, VehicleDocumentKind kind, String reference, LocalDate issuedOn, LocalDate expiresOn,
                                 String notes, Boolean expired, Long version) {

    public String label() {
        return kind == null ? "" : kind.label();
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Donde se dejo un vehiculo del turno al cerrarlo: un sitio de mto-resources, una via y su kp, o un
 * texto; lo que no se dice no viaja. Uno de solo via no se deja con solo un texto (409 {@code SHF-001}).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleParkingRequest(UUID vehicleId, UUID parkingSiteId, Long trackId, BigDecimal kp, String description) {
}

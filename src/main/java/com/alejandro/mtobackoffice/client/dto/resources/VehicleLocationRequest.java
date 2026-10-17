package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Donde esta un vehiculo, registrado a mano ({@code resources-location-write}); lo vacio no viaja y sin
 * {@code observedAt} es ahora. Un sitio aporta su EP, su via y su kp si no se dicen. {@code accessPointId}
 * es un punto de acceso del maestro, uno de los de su via (V9 de mto-resources: 404 {@code TAP-404} si no
 * lo conoce, 409 {@code VEH-001} si no le vale al vehiculo); {@code accessPoint}, el texto libre de antes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleLocationRequest(VehicleLocationKind kind, UUID parkingSiteId, Long executionPackageId, Long trackId, BigDecimal kp,
                                     BigDecimal latitude, BigDecimal longitude, Long accessPointId, String accessPoint, String description,
                                     UUID carrierVehicleId, VehicleAttachment attachment, Instant observedAt) {
}

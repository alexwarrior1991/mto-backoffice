package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/** Alta de un tramo de via, lo unico que se crea por la API: el resto de activos llega de mto-configuration. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssetRequest(String code, String name, String description, Long executionPackageId, Long trackId, Long stationId,
                           BigDecimal startKp, BigDecimal endKp, TrackKind trackKind, Integer preventiveIntervalDays) {
}

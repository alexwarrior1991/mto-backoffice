package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/** El alta de un sitio; lo vacio no viaja. Con acceso por via dice su via; las coordenadas van juntas. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParkingSiteRequest(String code, String name, ParkingSiteKind kind, Long executionPackageId, Long stationId, Long trackId,
                                 BigDecimal kp, Boolean railAccess, Boolean roadAccess, String address, BigDecimal latitude,
                                 BigDecimal longitude, String notes) {
}

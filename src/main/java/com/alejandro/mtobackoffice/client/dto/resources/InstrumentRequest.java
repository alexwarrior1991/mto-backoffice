package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/** El alta de un instrumento; lo vacio no viaja. El codigo no cambia despues. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InstrumentRequest(String code, String name, InstrumentKind kind, String serialNumber, LocalDate calibratedOn,
                                LocalDate calibrationExpiresOn, String notes) {
}

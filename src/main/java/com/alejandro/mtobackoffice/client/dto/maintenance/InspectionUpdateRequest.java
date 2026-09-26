package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Modificacion parcial de una inspeccion; el servicio vuelve a comprobar que el resultado casa con los puntos. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InspectionUpdateRequest(LocalDate inspectionDate, String inspector, InspectionKind inspectionKind, InspectionResult result,
                                      String description, String detectedDefects, String recommendedActions, BigDecimal kp) {

    /** Nada que mandar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return equals(new InspectionUpdateRequest(null, null, null, null, null, null, null, null));
    }
}

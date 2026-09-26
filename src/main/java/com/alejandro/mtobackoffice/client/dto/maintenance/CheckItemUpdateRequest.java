package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Lo medido en un punto de checklist. Una medida fuera de rango no puede quedar {@code OK} sin
 * ajustarse (422 {@code INS-001}); eso lo decide el servicio.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CheckItemUpdateRequest(BigDecimal measuredValue, Boolean adjusted, BigDecimal valueAfterAdjustment, CheckItemResult itemResult,
                                     String notes) {
}

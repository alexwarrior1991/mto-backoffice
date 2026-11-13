package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Un punto de una version nueva de una plantilla de inspeccion. Su posicion en la lista es su orden:
 * ni {@code orderIndex} ni {@code id} viajan. Lo vacio (unidad, minimo, maximo) no viaja.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InspectionTemplateItemRequest(String code, String label, String unit, BigDecimal minValue, BigDecimal maxValue,
                                            Boolean requiresMeasure) {
}

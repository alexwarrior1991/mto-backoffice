package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.UUID;

/** Un punto de checklist, de una tarea o de una inspeccion, con lo medido y si quedo fuera de rango. */
public record CheckItemDto(UUID id, String code, String label, String unit, BigDecimal minValue, BigDecimal maxValue,
                           Boolean requiresMeasure, BigDecimal measuredValue, Boolean adjusted, BigDecimal valueAfterAdjustment,
                           CheckItemResult itemResult, String notes, Integer orderIndex, Boolean outOfRange, Long version) {
}

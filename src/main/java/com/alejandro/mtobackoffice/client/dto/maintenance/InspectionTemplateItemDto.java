package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.UUID;

/** Un punto del checklist de una plantilla de inspeccion, con su rango si pide medida. */
public record InspectionTemplateItemDto(UUID id, String code, String label, String unit, BigDecimal minValue, BigDecimal maxValue,
                                        Boolean requiresMeasure, Integer orderIndex) {
}

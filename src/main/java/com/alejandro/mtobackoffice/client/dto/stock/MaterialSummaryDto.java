package com.alejandro.mtobackoffice.client.dto.stock;

import java.util.UUID;

/** Lo que de un material viaja dentro de otra cosa (un movimiento, una reserva, una linea de BOM). */
public record MaterialSummaryDto(UUID id, String code, String name, String unitOfMeasure, Boolean active) {

    public String label() {
        return StockLabels.codeAndName(code, name);
    }
}

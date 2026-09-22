package com.alejandro.mtobackoffice.client.dto.stock;

import java.util.UUID;

public record SupplierSummaryDto(UUID id, String code, String name, Boolean active) {

    public String label() {
        return StockLabels.codeAndName(code, name);
    }
}

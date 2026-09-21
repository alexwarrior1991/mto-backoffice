package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;

public record MaterialUpdateRequest(String code, String name, String unitOfMeasure, BigDecimal minimumStockLevel, boolean active) {
}

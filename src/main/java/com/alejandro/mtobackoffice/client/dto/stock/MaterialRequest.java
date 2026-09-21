package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;

public record MaterialRequest(String code, String name, String unitOfMeasure, BigDecimal minimumStockLevel) {
}

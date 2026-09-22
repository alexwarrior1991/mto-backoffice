package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.util.UUID;

public record AssemblyComponentRequest(UUID materialId, BigDecimal quantity) {
}

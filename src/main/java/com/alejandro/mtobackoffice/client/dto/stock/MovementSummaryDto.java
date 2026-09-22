package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovementSummaryDto(UUID id, MovementType type, BigDecimal quantity, Instant occurredAt, String externalReference) {
}

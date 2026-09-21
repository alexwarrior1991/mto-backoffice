package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReservationSummaryDto(UUID id, ReservationStatus status, BigDecimal quantity, Instant reservedAt) {
}

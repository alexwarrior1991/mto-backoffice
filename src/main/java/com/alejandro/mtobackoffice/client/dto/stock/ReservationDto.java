package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReservationDto(UUID id, MaterialSummaryDto material, WarehouseSummaryDto warehouse, ProjectSummaryDto project,
                             BigDecimal quantity, ReservationStatus status, Instant reservedAt, Instant releasedAt, Boolean active,
                             AuditDto audit) {

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }
}

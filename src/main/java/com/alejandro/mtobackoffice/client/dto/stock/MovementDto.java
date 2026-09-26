package com.alejandro.mtobackoffice.client.dto.stock;

import com.alejandro.mtobackoffice.client.dto.AuditDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Un apunte del libro de movimientos; una transferencia son dos, enlazados por {@code relatedMovement}. */
public record MovementDto(UUID id, MaterialSummaryDto material, WarehouseSummaryDto warehouse, MovementType type,
                          BigDecimal quantity, BigDecimal signedQuantity, Instant occurredAt, SupplierSummaryDto supplier,
                          ProjectSummaryDto project, ReservationSummaryDto reservation, MovementSummaryDto relatedMovement,
                          String externalReference, String notes, AuditDto audit) {
}

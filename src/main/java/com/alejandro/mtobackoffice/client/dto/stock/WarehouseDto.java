package com.alejandro.mtobackoffice.client.dto.stock;

import java.util.UUID;

public record WarehouseDto(UUID id, String code, String name, Boolean active, AuditDto audit) {

    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    public WarehouseSummaryDto summary() {
        return new WarehouseSummaryDto(id, code, name, active);
    }
}

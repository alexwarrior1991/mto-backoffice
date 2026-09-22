package com.alejandro.mtobackoffice.client.dto.stock;

import java.util.UUID;

public record SupplierDto(UUID id, String code, String name, Boolean active, AuditDto audit) {

    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    public SupplierSummaryDto summary() {
        return new SupplierSummaryDto(id, code, name, active);
    }
}

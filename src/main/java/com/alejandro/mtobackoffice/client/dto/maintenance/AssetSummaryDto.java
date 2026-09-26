package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.UUID;

/** El activo tal y como viene dentro de una orden, un defecto o una inspeccion. {@code trackId} es de mto-configuration. */
public record AssetSummaryDto(UUID id, String code, String name, CatenaryAssetType type, Long trackId,
                              BigDecimal startKp, BigDecimal endKp, String sectioning, Boolean enabled) {

    /** El codigo y, si lo hay, el nombre. */
    public String label() {
        if (name == null || name.isBlank() || name.equals(code)) {
            return code == null ? "" : code;
        }
        return code == null ? name : code + " - " + name;
    }
}

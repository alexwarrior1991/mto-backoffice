package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.UUID;

/** Una aguja de un aislador de seccion, como en el plano: {@code W31 1:9}. Una dada de baja sigue apareciendo, marcada. */
public record AssetSwitchDto(UUID id, String code, BigDecimal kp, Integer turnoutDenominator, String turnoutRate, Long trackId,
                             Boolean enabled) {

    public String label() {
        return turnoutRate == null ? code : code + " " + turnoutRate;
    }
}

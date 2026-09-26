package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** El avance del mantenimiento preventivo: activos revisados y km cubiertos, en total y por fila. Lo calcula el servicio. */
public record ProgressReportDto(Instant from, Instant to, long totalAssets, long checkedAssets, BigDecimal completionRatio,
                                BigDecimal coveredKm, BigDecimal totalKm, List<ProgressRowDto> rows) {

    public ProgressReportDto {
        rows = rows == null ? List.of() : List.copyOf(rows);
    }
}

package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Cuantos conjuntos se podrian montar ahora en un almacen, y que componente lo limita. */
public record AssemblyAvailabilityDto(AssemblySummaryDto assembly, WarehouseSummaryDto warehouse, BigDecimal availableQuantity,
                                      List<AssemblyAvailabilityComponentDto> components, Instant calculatedAt) {

    public AssemblyAvailabilityDto {
        components = components == null ? List.of() : List.copyOf(components);
    }
}

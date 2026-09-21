package com.alejandro.mtobackoffice.client.dto.stock;

import java.math.BigDecimal;
import java.util.UUID;

/** Una linea de la lista de materiales: cuanto de que material lleva un conjunto. */
public record AssemblyComponentDto(UUID id, MaterialSummaryDto material, BigDecimal quantity) {
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;

/** Una fila del avance: por paquete, via y tipo de activo (ids de mto-configuration). */
public record ProgressRowDto(Long executionPackageId, Long trackId, CatenaryAssetType assetType, long totalAssets, long checkedAssets,
                             BigDecimal completionRatio, BigDecimal coveredKm, BigDecimal totalKm) {
}

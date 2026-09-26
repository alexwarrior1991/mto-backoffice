package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.time.Instant;

/**
 * Los filtros de la lista de activos; lo que va a {@code null} no viaja. {@code name} es el nombre
 * de campo ({@code 12-2.27} de un perfil, {@code HSA-NS5} de un seccionador): parcial y sin
 * mayusculas. {@code preventiveDueBefore}: los que tienen el preventivo vencido antes de ese
 * instante.
 */
public record AssetFilter(CatenaryAssetType type, Long trackId, Long stationId, Long executionPackageId, Boolean enabled, String name,
                          Instant preventiveDueBefore) {

    public static final AssetFilter NONE = new AssetFilter(null, null, null, null, null, null, null);

    public AssetFilter {
        name = name == null || name.isBlank() ? null : name.trim();
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * El activo tal y como viene dentro de una orden, un defecto o una inspeccion. {@code trackId} es de
 * mto-configuration; {@code connectedTrackId}, la otra via de un aislador o un seccionador que une dos,
 * que se trabaja desde un turno de cualquiera de las dos. Su EP y su estacion, porque una orden es de un
 * solo EP; {@code driveType}, el accionamiento de un seccionador, que decide su tipo de tarea por defecto.
 */
public record AssetSummaryDto(UUID id, String code, String name, CatenaryAssetType type, Long trackId,
                              BigDecimal startKp, BigDecimal endKp, String sectioning, Boolean enabled, Long connectedTrackId,
                              Long executionPackageId, Long stationId, DisconnectorDrive driveType) {

    /** El codigo y, si lo hay, el nombre. */
    public String label() {
        if (name == null || name.isBlank() || name.equals(code)) {
            return code == null ? "" : code;
        }
        return code == null ? name : code + " - " + name;
    }

    /**
     * Las vias en las que se trabaja: la suya (o, sin ella, la de respaldo, la de su orden) y la
     * conectada si une dos. Sin repetir y sin vacios: es lo que se busca en los turnos.
     */
    public List<Long> tracks(Long fallbackTrackId) {
        return Stream.of(trackId != null ? trackId : fallbackTrackId, connectedTrackId).filter(Objects::nonNull).distinct().toList();
    }

    /** Las de un activo que puede no estar: sin activo, solo la de respaldo. */
    public static List<Long> tracksOf(AssetSummaryDto asset, Long fallbackTrackId) {
        return asset == null ? Stream.of(fallbackTrackId).filter(Objects::nonNull).toList() : asset.tracks(fallbackTrackId);
    }
}

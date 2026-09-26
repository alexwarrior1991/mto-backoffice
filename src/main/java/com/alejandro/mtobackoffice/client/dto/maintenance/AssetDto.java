package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Un activo de catenaria. Un {@code TRACK_SECTION} se da de alta aqui; perfiles, seccionadores y
 * aisladores llegan de mto-configuration ({@code sourceService}) y de ellos solo se cambian aqui la
 * descripcion y el intervalo preventivo (el resto es 409 {@code AST-001}). Paquete, via y
 * estacion son ids de mto-configuration. {@code nextPreventiveDueAt} lo calcula el servicio.
 */
public record AssetDto(UUID id, String code, String name, CatenaryAssetType type, String description, Long executionPackageId,
                       Long trackId, Long stationId, BigDecimal startKp, BigDecimal endKp, String profileSourceId, String sectioning,
                       TrackKind trackKind, Long connectedTrackId, SectionInsulatorInstallation installationType,
                       List<AssetSwitchDto> switches, String sourceService, String sourceEntityId, Boolean enabled,
                       Integer preventiveIntervalDays, Instant lastPreventiveCompletedAt, Instant nextPreventiveDueAt, AuditDto audit) {

    public AssetDto {
        switches = switches == null ? List.of() : List.copyOf(switches);
    }

    /** Llega de los datos maestros de mto-configuration: su identidad y su localizacion no se tocan aqui. */
    public boolean isSynchronized() {
        return sourceService != null && !sourceService.isBlank();
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /** El codigo y, si lo hay, el nombre. */
    public String label() {
        if (name == null || name.isBlank() || name.equals(code)) {
            return code == null ? "" : code;
        }
        return code == null ? name : code + " - " + name;
    }
}

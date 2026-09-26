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
 *
 * <p>{@code enabled} es el valor efectivo, y lo deciden dos: {@code enabledAtSource}, lo que dice
 * mto-configuration ({@code null} en un tramo propio), y {@code disabledLocally}, lo que decidio
 * mantenimiento, que ningun evento de datos maestros deshace.</p>
 */
public record AssetDto(UUID id, String code, String name, CatenaryAssetType type, String description, Long executionPackageId,
                       Long trackId, Long stationId, BigDecimal startKp, BigDecimal endKp, String profileSourceId, String sectioning,
                       TrackKind trackKind, Long connectedTrackId, SectionInsulatorInstallation installationType,
                       List<AssetSwitchDto> switches, String sourceService, String sourceEntityId, Boolean enabled,
                       Boolean enabledAtSource, Boolean disabledLocally,
                       Integer preventiveIntervalDays, Instant lastPreventiveCompletedAt, Instant nextPreventiveDueAt, AuditDto audit, Long version) {

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

    /** mto-configuration lo tiene desactivado: vuelve cuando el origen lo reactive, y reactivarlo aqui es 409 {@code AST-001}. */
    public boolean isDisabledAtSource() {
        return Boolean.FALSE.equals(enabledAtSource);
    }

    /** Lo desactivo mantenimiento: sigue asi diga lo que diga mto-configuration, hasta que se reactive aqui. */
    public boolean isDisabledLocally() {
        return Boolean.TRUE.equals(disabledLocally);
    }

    /** Lo que llevan una orden o un desplegable. */
    public AssetSummaryDto summary() {
        return new AssetSummaryDto(id, code, name, type, trackId, startKp, endKp, sectioning, enabled);
    }

    /** El codigo y, si lo hay, el nombre. */
    public String label() {
        if (name == null || name.isBlank() || name.equals(code)) {
            return code == null ? "" : code;
        }
        return code == null ? name : code + " - " + name;
    }
}

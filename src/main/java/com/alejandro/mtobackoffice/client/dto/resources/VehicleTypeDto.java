package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Un modelo de vehiculo de mto-resources: lo que comparten todas sus unidades. {@code mobility} decide
 * el resto (las cifras de via, de bivial y de carretera que lleva). Las capacidades van como texto
 * ({@link VehicleCapability}). {@code requiredDriverQualification} es la habilitacion que exige a quien
 * lo conduce ({@code null}: ninguna); un turno que lo lleva necesita una persona distinta con ella por
 * cada vehiculo autopropulsado. No tiene historial: es un catalogo.
 */
public record VehicleTypeDto(UUID id, String code, String name, String manufacturer, String model, VehicleMobility mobility,
                             VehicleCategory category, Boolean selfPropelled, Boolean portable, BigDecimal lengthM, BigDecimal widthM,
                             BigDecimal heightM, BigDecimal tareT, BigDecimal maxGrossT, BigDecimal maxAxleLoadT, Integer axles, Integer seats,
                             BigDecimal payloadT, VehiclePower power, Integer trackGaugeMm, Integer maxSpeedTrackWorkingKmh,
                             Integer maxSpeedTrackTransportKmh, BigDecimal maxGradientPermille, Integer minCurveRadiusM, Integer maxWorkingCantMm,
                             Boolean transportModeAuthorised, RailGearType railGearType, Integer onTrackingMinutes, Boolean needsPreparedAccess,
                             Integer maxSpeedRoadKmh, DrivingLicence requiredLicence, Set<String> capabilities, BigDecimal craneCapacityT,
                             BigDecimal craneReachM, BigDecimal platformHeightM, Integer platformLoadKg, BigDecimal platformInsulationKv,
                             BigDecimal towingCapacityT, String notes, Boolean active, Long version,
                             QualificationTypeSummaryDto requiredDriverQualification) {

    public VehicleTypeDto {
        capabilities = capabilities == null ? Set.of() : Collections.unmodifiableSet(new TreeSet<>(capabilities));
    }

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}

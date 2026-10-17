package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Lo que se cambia de un modelo, para un {@code MergePatch}: todo menos el codigo y la movilidad. El
 * servicio deja vaciar todo lo opcional; {@code active=false} retira el modelo y pide
 * {@code resources-delete}.
 */
public record VehicleTypeUpdateRequest(String name, String manufacturer, String model, VehicleCategory category, Boolean selfPropelled,
                                       Boolean portable, BigDecimal lengthM, BigDecimal widthM, BigDecimal heightM, BigDecimal tareT,
                                       BigDecimal maxGrossT, BigDecimal maxAxleLoadT, Integer axles, Integer seats, BigDecimal payloadT,
                                       VehiclePower power, Integer trackGaugeMm, Integer maxSpeedTrackWorkingKmh, Integer maxSpeedTrackTransportKmh,
                                       BigDecimal maxGradientPermille, Integer minCurveRadiusM, Integer maxWorkingCantMm,
                                       Boolean transportModeAuthorised, RailGearType railGearType, Integer onTrackingMinutes,
                                       Boolean needsPreparedAccess, Integer maxSpeedRoadKmh, DrivingLicence requiredLicence, Set<String> capabilities,
                                       BigDecimal craneCapacityT, BigDecimal craneReachM, BigDecimal platformHeightM, Integer platformLoadKg,
                                       BigDecimal platformInsulationKv, BigDecimal towingCapacityT, String notes, Boolean active) {

    public static VehicleTypeUpdateRequest active(boolean active) {
        return new VehicleTypeUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, active);
    }
}

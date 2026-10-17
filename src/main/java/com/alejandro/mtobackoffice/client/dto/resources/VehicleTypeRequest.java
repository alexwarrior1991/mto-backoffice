package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * El alta de un modelo; lo vacio no viaja. El codigo y la movilidad no cambian despues. Sin
 * {@code requiredDriverQualificationTypeId} el servicio pone la habilitacion que corresponde a su
 * movilidad y a su permiso.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleTypeRequest(String code, String name, String manufacturer, String model, VehicleMobility mobility,
                                 VehicleCategory category, Boolean selfPropelled, Boolean portable, BigDecimal lengthM, BigDecimal widthM,
                                 BigDecimal heightM, BigDecimal tareT, BigDecimal maxGrossT, BigDecimal maxAxleLoadT, Integer axles,
                                 Integer seats, BigDecimal payloadT, VehiclePower power, Integer trackGaugeMm, Integer maxSpeedTrackWorkingKmh,
                                 Integer maxSpeedTrackTransportKmh, BigDecimal maxGradientPermille, Integer minCurveRadiusM,
                                 Integer maxWorkingCantMm, Boolean transportModeAuthorised, RailGearType railGearType,
                                 Integer onTrackingMinutes, Boolean needsPreparedAccess, Integer maxSpeedRoadKmh, DrivingLicence requiredLicence,
                                 Set<String> capabilities, BigDecimal craneCapacityT, BigDecimal craneReachM, BigDecimal platformHeightM,
                                 Integer platformLoadKg, BigDecimal platformInsulationKv, BigDecimal towingCapacityT, String notes,
                                 UUID requiredDriverQualificationTypeId) {
}

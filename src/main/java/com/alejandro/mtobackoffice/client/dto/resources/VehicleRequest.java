package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

/** El alta de un vehiculo; lo vacio no viaja. Lo que exige (matricula, numero ferroviario) lo decide su modelo. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleRequest(String code, String name, UUID typeId, String registrationPlate, String railVehicleNumber, String serialNumber,
                             Integer manufactureYear, VehicleOwner owner, String ownerCompany, VehicleOperationalStatus operationalStatus,
                             String statusReason, Integer odometerKm, BigDecimal engineHours, String notes, String telematicsDeviceId) {
}

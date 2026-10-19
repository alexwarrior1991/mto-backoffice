package com.alejandro.mtobackoffice.client.dto.resources;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lo que se cambia de un vehiculo, para un {@code MergePatch}: todo menos el codigo. El servicio deja
 * vaciar el nombre, la matricula, el numero ferroviario, el de serie, el ano, la empresa, el motivo, los
 * km, las horas, las notas y el dispositivo GPS; {@code active=false} lo retira y pide {@code resources-delete}.
 */
public record VehicleUpdateRequest(String name, UUID typeId, String registrationPlate, String railVehicleNumber, String serialNumber,
                                   Integer manufactureYear, VehicleOwner owner, String ownerCompany, VehicleOperationalStatus operationalStatus,
                                   String statusReason, Integer odometerKm, BigDecimal engineHours, String notes, Boolean active,
                                   String telematicsDeviceId) {

    public static VehicleUpdateRequest active(boolean active) {
        return new VehicleUpdateRequest(null, null, null, null, null, null, null, null, null, null, null, null, null, active, null);
    }
}

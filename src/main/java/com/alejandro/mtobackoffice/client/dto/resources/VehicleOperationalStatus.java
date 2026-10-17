package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Como esta un vehiculo; uno que no esta operativo dice por que. Disponible es activo y operativo o limitado. */
public enum VehicleOperationalStatus {
    OPERATIONAL("Operativo"),
    LIMITED("Limitado"),
    UNDER_REPAIR("En reparacion"),
    OUT_OF_SERVICE("Fuera de servicio"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehicleOperationalStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleOperationalStatus of(String value) {
        return ClientEnums.parse(VehicleOperationalStatus.class, value, UNKNOWN);
    }

    public static List<VehicleOperationalStatus> selectable() {
        return ClientEnums.selectable(VehicleOperationalStatus.class, UNKNOWN);
    }
}

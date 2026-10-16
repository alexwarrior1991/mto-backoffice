package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Como va un vehiculo con otro: remolcado por uno que remolca, o transportado por uno de carretera. */
public enum VehicleAttachment {
    TOWED("Remolcado"),
    CARRIED("Transportado"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    VehicleAttachment(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleAttachment of(String value) {
        return ClientEnums.parse(VehicleAttachment.class, value, UNKNOWN);
    }

    public static List<VehicleAttachment> selectable() {
        return ClientEnums.selectable(VehicleAttachment.class, UNKNOWN);
    }
}

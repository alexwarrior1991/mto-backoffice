package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** El permiso de conducir que exige un modelo por carretera. */
public enum DrivingLicence {
    B("B"),
    C("C"),
    C_E("C+E"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    DrivingLicence(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DrivingLicence of(String value) {
        return ClientEnums.parse(DrivingLicence.class, value, UNKNOWN);
    }

    public static List<DrivingLicence> selectable() {
        return ClientEnums.selectable(DrivingLicence.class, UNKNOWN);
    }
}

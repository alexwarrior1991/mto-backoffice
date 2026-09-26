package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Como se monta un aislador de seccion: entre dos vias o dentro de una. */
public enum SectionInsulatorInstallation {
    TRACK_CONNECTION("Conexion entre vias"),
    IN_TRACK("En la via"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    SectionInsulatorInstallation(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SectionInsulatorInstallation of(String value) {
        return MaintenanceEnums.parse(SectionInsulatorInstallation.class, value, UNKNOWN);
    }

    public static List<SectionInsulatorInstallation> selectable() {
        return MaintenanceEnums.selectable(SectionInsulatorInstallation.class, UNKNOWN);
    }
}

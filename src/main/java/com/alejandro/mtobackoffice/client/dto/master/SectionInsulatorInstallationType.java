package com.alejandro.mtobackoffice.client.dto.master;

/** Como esta puesto un aislador de seccion: entre dos vias que conectan, o en medio de una sola. */
public enum SectionInsulatorInstallationType {

    TRACK_CONNECTION("Conexion de vias"),
    IN_TRACK("En una via");

    private final String label;

    SectionInsulatorInstallationType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

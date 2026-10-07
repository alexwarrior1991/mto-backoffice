package com.alejandro.mtobackoffice.client.dto.master;

/**
 * Como se acciona un seccionador: con motor o a mano.
 *
 * <p>Sin {@code UNKNOWN}, como {@link SectionInsulatorInstallationType}: el maestro se devuelve
 * entero, y un valor desconocido volveria al servicio como uno que no existe.
 */
public enum DisconnectorDriveType {

    MOTOR("Motor"),
    MANUAL("Manual");

    private final String label;

    DisconnectorDriveType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

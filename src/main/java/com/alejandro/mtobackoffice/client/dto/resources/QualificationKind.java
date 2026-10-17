package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Que es un tipo de cualificacion: un permiso de conducir (lleva su {@link DrivingLicence}), la
 * habilitacion para manejar un vehiculo (dresina, bivial), una de seguridad u otra.
 */
public enum QualificationKind {
    DRIVING_LICENCE("Permiso de conducir"),
    VEHICLE_OPERATION("Manejo de vehiculos"),
    SAFETY("Seguridad"),
    OTHER("Otra"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    QualificationKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static QualificationKind of(String value) {
        return ClientEnums.parse(QualificationKind.class, value, UNKNOWN);
    }

    public static List<QualificationKind> selectable() {
        return ClientEnums.selectable(QualificationKind.class, UNKNOWN);
    }
}

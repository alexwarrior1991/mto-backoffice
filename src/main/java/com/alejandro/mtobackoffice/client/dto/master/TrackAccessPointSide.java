package com.alejandro.mtobackoffice.client.dto.master;

/**
 * Por que lado de la via se entra desde un punto de acceso, en el sentido creciente del KP. Como
 * {@link TrackAccessPointKind}, el DTO guarda la cadena leida, que vuelve tal cual y se ensena cruda si
 * no se conoce.
 */
public enum TrackAccessPointSide {

    LEFT("Izquierda"),
    RIGHT("Derecha"),
    BOTH("Los dos lados");

    private final String label;

    TrackAccessPointSide(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** La etiqueta de un valor leido; uno que no se conoce se ensena tal cual, y ninguno, vacio. */
    public static String labelOf(String value) {
        if (value == null) {
            return "";
        }
        for (TrackAccessPointSide side : values()) {
            if (side.name().equals(value)) {
                return side.label;
            }
        }
        return value;
    }
}

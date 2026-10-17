package com.alejandro.mtobackoffice.client.dto.master;

/**
 * Que clase de punto de acceso a la via es, con su etiqueta. El DTO no lo guarda como enumerado sino
 * como la cadena que se leyo ({@link TrackAccessPointDto#getKind()}): el maestro se devuelve entero, y
 * asi un valor que el servicio estrene vuelve tal cual (un {@code UNKNOWN} volveria como un valor que no
 * existe) y se ensena crudo. Aqui solo estan los que se ofrecen.
 */
public enum TrackAccessPointKind {

    PREPARED_ACCESS("Acceso preparado"),
    LEVEL_CROSSING("Paso a nivel"),
    DEPOT("Base o cochera");

    private final String label;

    TrackAccessPointKind(String label) {
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
        for (TrackAccessPointKind kind : values()) {
            if (kind.name().equals(value)) {
                return kind.label;
            }
        }
        return value;
    }
}

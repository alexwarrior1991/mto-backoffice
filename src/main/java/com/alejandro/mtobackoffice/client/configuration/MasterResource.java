package com.alejandro.mtobackoffice.client.configuration;

/**
 * Los maestros de infraestructura de mto-configuration que el backoffice edita. Sus controladores
 * comparten la misma familia de endpoints ({@code CRUDController}); las mensulas y los brazos de
 * atirantado son hijos del perfil y no tienen pantalla propia.
 */
public enum MasterResource {

    EXECUTION_PACKAGES("execution-packages", "Paquetes de ejecucion", "Paquete de ejecucion"),
    STATIONS("stations", "Estaciones", "Estacion"),
    TRACKS("tracks", "Vias", "Via"),
    PROFILES("profiles", "Perfiles", "Perfil"),
    DISCONNECTORS("disconnectors", "Seccionadores", "Seccionador"),
    SECTION_INSULATORS("section-insulators", "Aisladores de seccion", "Aislador de seccion"),
    TRACK_ACCESS_POINTS("track-access-points", "Accesos a via", "Acceso a via");

    private final String path;
    private final String title;
    private final String singular;

    MasterResource(String path, String title, String singular) {
        this.path = path;
        this.title = title;
        this.singular = singular;
    }

    /** Ultimo segmento de la ruta publica: {@code /api/configuration/<path>}. */
    public String path() {
        return path;
    }

    public String title() {
        return title;
    }

    public String singular() {
        return singular;
    }
}

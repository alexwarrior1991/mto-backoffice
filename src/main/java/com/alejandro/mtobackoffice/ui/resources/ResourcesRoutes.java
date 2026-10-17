package com.alejandro.mtobackoffice.ui.resources;

/**
 * Las rutas del modulo de recursos (personal, organigrama y flota de mto-resources), las mismas que
 * en mto-frontend. El prefijo es el grupo del menu y no tiene pantalla propia, asi que el nodo del
 * grupo es solo una etiqueta, como Infraestructura. Los segmentos literales de la flota
 * ({@code tablero}, {@code modelos}, {@code aparcamientos}) ganan a {@code :vehicleId}.
 */
public final class ResourcesRoutes {

    public static final String PREFIX = "recursos";
    public static final String PEOPLE = PREFIX + "/personal";
    public static final String ORGANIZATION = PREFIX + "/organigrama";
    public static final String FLEET = PREFIX + "/flota";
    public static final String BOARD = FLEET + "/tablero";
    public static final String VEHICLE_TYPES = FLEET + "/modelos";
    public static final String PARKING_SITES = FLEET + "/aparcamientos";

    private ResourcesRoutes() {
    }
}

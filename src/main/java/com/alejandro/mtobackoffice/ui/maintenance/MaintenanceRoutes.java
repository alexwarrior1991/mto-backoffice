package com.alejandro.mtobackoffice.ui.maintenance;

/**
 * Las rutas del modulo de mantenimiento; el prefijo es el grupo del menu, y la lista de ordenes, en
 * el propio prefijo, es a la vez el nodo del grupo.
 */
public final class MaintenanceRoutes {

    public static final String PREFIX = "mantenimiento";
    public static final String ORDERS = PREFIX;
    public static final String ORDER = PREFIX + "/ordenes";
    public static final String ASSETS = PREFIX + "/activos";
    public static final String SHIFTS = PREFIX + "/turnos";
    public static final String INSPECTIONS = PREFIX + "/inspecciones";
    public static final String DEFECTS = PREFIX + "/defectos";
    public static final String TEAMS = PREFIX + "/equipos";
    public static final String TASK_TYPES = PREFIX + "/tipos-de-tarea";
    public static final String TEMPLATES = PREFIX + "/plantillas";

    private MaintenanceRoutes() {
    }
}

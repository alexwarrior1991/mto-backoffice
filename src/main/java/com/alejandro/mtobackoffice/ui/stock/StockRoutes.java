package com.alejandro.mtobackoffice.ui.stock;

/** Las rutas del modulo de almacen; el prefijo es el grupo del menu. */
public final class StockRoutes {

    public static final String PREFIX = "almacen";
    public static final String WAREHOUSES = PREFIX + "/almacenes";
    public static final String SUPPLIERS = PREFIX + "/proveedores";
    public static final String PROJECTS = PREFIX + "/proyectos";
    public static final String MATERIALS = PREFIX + "/materiales";
    public static final String MOVEMENTS = PREFIX + "/movimientos";
    public static final String RESERVATIONS = PREFIX + "/reservas";
    public static final String ASSEMBLIES = PREFIX + "/conjuntos";

    private StockRoutes() {
    }
}

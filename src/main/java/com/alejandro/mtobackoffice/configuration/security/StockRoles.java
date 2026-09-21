package com.alejandro.mtobackoffice.configuration.security;

/**
 * Los permisos del modulo de almacen: los roles del cliente {@code mto-stock-api} tal como los emite
 * {@link KeycloakRoleMapper} ({@code stock-read} llega como {@code ROLE_STOCK_READ}). Ninguno implica
 * otro; un ajuste de inventario pide {@code stock-write} y {@code stock-adjust} a la vez.
 */
public final class StockRoles {

    public static final String STOCK_READ = "STOCK_READ";
    public static final String STOCK_WRITE = "STOCK_WRITE";
    public static final String STOCK_DELETE = "STOCK_DELETE";
    public static final String STOCK_ADJUST = "STOCK_ADJUST";

    private StockRoles() {
    }
}

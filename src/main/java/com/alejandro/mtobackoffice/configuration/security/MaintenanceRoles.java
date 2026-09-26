package com.alejandro.mtobackoffice.configuration.security;

/**
 * Los permisos del modulo de mantenimiento: los roles del cliente {@code mto-maintenance-api} tal
 * como los emite {@link KeycloakRoleMapper} ({@code maintenance-read} llega como
 * {@code ROLE_MAINTENANCE_READ}). Ninguno implica otro. {@code maintenance-supervise} se pide ademas
 * de {@code maintenance-write}, como en el servicio: cancelar una orden, completarla con
 * {@code force} y resolver, cerrar o descartar un defecto.
 */
public final class MaintenanceRoles {

    public static final String MAINTENANCE_READ = "MAINTENANCE_READ";
    public static final String MAINTENANCE_WRITE = "MAINTENANCE_WRITE";
    public static final String MAINTENANCE_DELETE = "MAINTENANCE_DELETE";
    public static final String MAINTENANCE_SUPERVISE = "MAINTENANCE_SUPERVISE";

    private MaintenanceRoles() {
    }
}

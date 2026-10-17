package com.alejandro.mtobackoffice.configuration.security;

/**
 * Los permisos del modulo de recursos (personal, organigrama y flota): los roles del cliente
 * {@code mto-resources-api} tal como los emite {@link KeycloakRoleMapper} ({@code resources-read}
 * llega como {@code ROLE_RESOURCES_READ}). Ninguno implica otro, como en el servicio: escribir
 * personas y unidades no basta para la flota, la flota no basta para retirar, y registrar donde esta
 * un vehiculo ({@code resources-location-write}) es solo eso. Retirar o desactivar algo, tambien con
 * un {@code PATCH active=false}, pide {@code resources-delete}.
 */
public final class ResourcesRoles {

    public static final String RESOURCES_READ = "RESOURCES_READ";
    public static final String RESOURCES_WRITE = "RESOURCES_WRITE";
    public static final String RESOURCES_FLEET_WRITE = "RESOURCES_FLEET_WRITE";
    public static final String RESOURCES_LOCATION_WRITE = "RESOURCES_LOCATION_WRITE";
    public static final String RESOURCES_DELETE = "RESOURCES_DELETE";

    private ResourcesRoles() {
    }
}

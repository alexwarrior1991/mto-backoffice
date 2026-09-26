package com.alejandro.mtobackoffice.configuration.security;

/**
 * Permisos que comprueban las vistas, ya normalizados a autoridad de Spring.
 *
 * <p>Son los roles de cliente de {@code mto-configuration-api} que declara
 * {@code mto-configuration/keycloak/mto-configuration-partial-import.json}, en mayusculas y con
 * guion bajo: {@code config-read} llega como {@code ROLE_CONFIG_READ}. Esta aplicacion no declara
 * roles propios: ensena o esconde lo que el servicio va a permitir o negar. Un rol que se anade
 * aqui sin existir alli no lo tiene nadie y la pantalla desaparece del menu para todos. Los del
 * modulo de usuarios, roles de cliente de {@code mto-users-api}, viven en {@link UserRoles}; los de
 * almacen, en {@link StockRoles}, y los de mantenimiento, en {@link MaintenanceRoles}.</p>
 */
public final class SecurityRoles {

    private SecurityRoles() {
    }

    /** Lectura de infraestructura y listas de valores. */
    public static final String CONFIG_READ = "CONFIG_READ";

    /** Alta y modificacion de infraestructura, registro a registro. */
    public static final String CONFIG_WRITE = "CONFIG_WRITE";

    /** Borrado y cancelacion. */
    public static final String CONFIG_DELETE = "CONFIG_DELETE";

    /** Cargas masivas e importaciones de ficheros. */
    public static final String CONFIG_IMPORT = "CONFIG_IMPORT";

    /** Mantenimiento de las listas de valores (se pide ademas de la escritura). */
    public static final String LOV_MANAGE = "LOV_MANAGE";

    /** Consulta del historico de revisiones. */
    public static final String CONFIG_AUDIT = "CONFIG_AUDIT";
}

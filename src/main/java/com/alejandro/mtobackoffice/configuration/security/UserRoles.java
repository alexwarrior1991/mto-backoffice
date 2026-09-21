package com.alejandro.mtobackoffice.configuration.security;

/**
 * Permisos del modulo de usuarios, ya normalizados a autoridad de Spring.
 *
 * <p>Son los roles de cliente de {@code mto-users-api} que declara
 * {@code mto-users/keycloak/mto-users-partial-import.json}, en mayusculas y con guion bajo:
 * {@code users-read} llega como {@code ROLE_USERS_READ}. Ninguno implica otro (escribir no
 * borra, ni cierra sesiones, ni fija contrasenas) y el servicio los comprueba ruta a ruta: aqui
 * solo deciden que se ensena. Van aparte de {@link SecurityRoles} porque son de otro cliente; el
 * mapeo emite {@code ROLE_} para los dos, y lo que evita que un rol de uno se confunda con uno del
 * otro es que sus nombres no se solapan ({@code config-*} y {@code lov-manage} frente a
 * {@code users-*}), cosa que {@code SecurityLayerTest} comprueba.</p>
 */
public final class UserRoles {

    private UserRoles() {
    }

    /** Consulta de usuarios, de sus roles y perfiles, y de los catalogos de roles y perfiles. */
    public static final String USERS_READ = "USERS_READ";
    /** Alta y modificacion, activar y desactivar, y envio de acciones requeridas por correo. */
    public static final String USERS_WRITE = "USERS_WRITE";
    /** Borrado: la unica operacion irreversible, y por eso aparte de la escritura. */
    public static final String USERS_DELETE = "USERS_DELETE";
    /** Asignar y quitar roles de cliente. */
    public static final String USERS_ROLES_WRITE = "USERS_ROLES_WRITE";
    /** Fijar una contrasena temporal. */
    public static final String USERS_PASSWORD_RESET = "USERS_PASSWORD_RESET";
    /** Asignar y quitar perfiles (roles compuestos de realm). */
    public static final String USERS_PROFILES_WRITE = "USERS_PROFILES_WRITE";
    /** Cerrar sesiones abiertas, normales y offline. */
    public static final String USERS_SESSIONS_WRITE = "USERS_SESSIONS_WRITE";
    /** Quitar credenciales. */
    public static final String USERS_CREDENTIALS_WRITE = "USERS_CREDENTIALS_WRITE";
}

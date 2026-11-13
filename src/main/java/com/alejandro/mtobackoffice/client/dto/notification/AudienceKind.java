package com.alejandro.mtobackoffice.client.dto.notification;

import java.util.Locale;

/**
 * Las clases de audiencia de un aviso manual, {@code KIND:clave}. Solo viaja en peticiones, asi que no
 * lleva {@code UNKNOWN}: son las que el servicio acepta hoy ({@code AudienceKind} alli), y una que no
 * este aqui seria su 422 {@code NTF-422}.
 */
public enum AudienceKind {

    /** Una persona, por su nombre de usuario. */
    USER("USER:nombre.de.usuario"),
    /** Una persona, por su id de Keycloak. */
    USER_ID("USER_ID:id-de-keycloak"),
    /** Un perfil: un rol compuesto del realm. */
    PROFILE("PROFILE:mto-admin"),
    /** Un rol de cliente; su clave es {@code <cliente>:<rol>}. */
    CLIENT_ROLE("CLIENT_ROLE:mto-stock-api:stock-read");

    /** Lo mas largo que admite el servicio para una audiencia entera. */
    public static final int MAX_LENGTH = 300;

    private final String example;

    AudienceKind(String example) {
        this.example = example;
    }

    public String example() {
        return example;
    }

    /**
     * Por que una audiencia escrita no la aceptaria el servicio, o {@code null} si vale. La misma regla
     * que {@code Audience.parse} alli: una clase conocida (sin distinguir mayusculas), dos puntos y una
     * clave que no este en blanco; la de un rol de cliente es {@code <cliente>:<rol>}.
     */
    public static String problemOf(String audience) {
        String text = audience == null ? "" : audience.trim();
        int separator = text.indexOf(':');
        if (separator <= 0 || separator == text.length() - 1 || text.substring(separator + 1).isBlank()) {
            return "«" + text + "» no es CLASE:clave";
        }
        AudienceKind kind = parse(text.substring(0, separator));
        if (kind == null) {
            return "«" + text + "»: la clase es USER, USER_ID, PROFILE o CLIENT_ROLE";
        }
        if (kind == CLIENT_ROLE && !text.substring(separator + 1).trim().contains(":")) {
            return "«" + text + "»: un rol de cliente es CLIENT_ROLE:cliente:rol";
        }
        if (text.length() > MAX_LENGTH) {
            return "Una audiencia tiene como mucho " + MAX_LENGTH + " caracteres";
        }
        return null;
    }

    private static AudienceKind parse(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        for (AudienceKind kind : values()) {
            if (kind.name().equals(normalized)) {
                return kind;
            }
        }
        return null;
    }
}

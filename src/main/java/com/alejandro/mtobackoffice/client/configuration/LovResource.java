package com.alejandro.mtobackoffice.client.configuration;

import java.util.Arrays;
import java.util.Optional;

/**
 * Los 17 catalogos (listas de valores) de mto-configuration. Todos comparten los mismos ocho
 * endpoints y el mismo DTO, asi que una sola vista parametrizada por este enum los sirve todos.
 *
 * <p>Tres dependen de otro catalogo, y el servicio exige ese tipo al dar de alta y al modificar
 * (README_API.md §5 de mto-configuration; sin el, 400): {@link #parent()} dice en que campo viaja,
 * de que catalogo sale y como se llama en pantalla. Es la misma tabla que {@code lovResources.js} de
 * mto-frontend.</p>
 */
public enum LovResource {

    ANCHORAGES("anchorages", "Anclajes"),
    ANCHORAGE_FOUNDATIONS("anchorage-foundations", "Cimentaciones de anclaje",
            new Parent("anchorageFoundationType", "anchorage-foundation-types", "Tipo de cimentacion de anclaje")),
    ANCHORAGE_FOUNDATION_TYPES("anchorage-foundation-types", "Tipos de cimentacion de anclaje"),
    ASSEMBLY_CONFIGURATIONS("assembly-configurations", "Configuraciones de montaje"),
    CANTILEVER_TYPES("cantilever-types", "Tipos de mensula"),
    COMERCIAL_ENTITY_TYPES("comercial-entity-types", "Tipos de entidad comercial"),
    DISCONNECTOR_FUNCTIONS("disconnector-functions", "Funciones de seccionador"),
    FOUNDATIONS("foundations", "Cimentaciones", new Parent("foundationType", "foundation-types", "Tipo de cimentacion")),
    FOUNDATION_TYPES("foundation-types", "Tipos de cimentacion"),
    POLE_TYPES("pole-types", "Tipos de poste"),
    PORTALS("portals", "Porticos", new Parent("portalType", "portal-types", "Tipo de portico")),
    PORTAL_TYPES("portal-types", "Tipos de portico"),
    PROFILE_STATUSES("profile-statuses", "Estados de perfil"),
    RETURN_SUPPORTS("return-supports", "Soportes de retorno"),
    SECTIONINGS("sectionings", "Seccionamientos"),
    STEADY_ARM_TYPES("steady-arm-types", "Tipos de brazo de atirantado"),
    SUPPORT_TYPES("support-types", "Tipos de soporte");

    /**
     * El catalogo del que depende uno de estos.
     *
     * @param field el campo del cuerpo en el que viaja, como {@code {"id": ...}}
     * @param path  el catalogo del que sale, por su ruta
     * @param label su nombre en pantalla
     */
    public record Parent(String field, String path, String label) {
    }

    private final String path;
    private final String title;
    private final Parent parent;

    LovResource(String path, String title) {
        this(path, title, null);
    }

    LovResource(String path, String title, Parent parent) {
        this.path = path;
        this.title = title;
        this.parent = parent;
    }

    /** Ultimo segmento de la ruta publica: {@code /api/configuration/<path>}. */
    public String path() {
        return path;
    }

    public String title() {
        return title;
    }

    /** El tipo que exige el servicio en los tres catalogos que dependen de otro. */
    public Optional<Parent> parent() {
        return Optional.ofNullable(parent);
    }

    /** El catalogo cuyo ultimo segmento de ruta es {@code path}, si existe. */
    public static Optional<LovResource> byPath(String path) {
        return Arrays.stream(values()).filter(resource -> resource.path.equals(path)).findFirst();
    }
}

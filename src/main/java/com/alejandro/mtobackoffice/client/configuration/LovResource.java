package com.alejandro.mtobackoffice.client.configuration;

import java.util.Arrays;
import java.util.Optional;

/**
 * Los 17 catalogos (listas de valores) de mto-configuration. Todos comparten los mismos ocho
 * endpoints y el mismo DTO, asi que una sola vista parametrizada por este enum los sirve todos.
 */
public enum LovResource {

    ANCHORAGES("anchorages", "Anclajes"),
    ANCHORAGE_FOUNDATIONS("anchorage-foundations", "Cimentaciones de anclaje"),
    ANCHORAGE_FOUNDATION_TYPES("anchorage-foundation-types", "Tipos de cimentacion de anclaje"),
    ASSEMBLY_CONFIGURATIONS("assembly-configurations", "Configuraciones de montaje"),
    CANTILEVER_TYPES("cantilever-types", "Tipos de mensula"),
    COMERCIAL_ENTITY_TYPES("comercial-entity-types", "Tipos de entidad comercial"),
    DISCONNECTOR_FUNCTIONS("disconnector-functions", "Funciones de seccionador"),
    FOUNDATIONS("foundations", "Cimentaciones"),
    FOUNDATION_TYPES("foundation-types", "Tipos de cimentacion"),
    POLE_TYPES("pole-types", "Tipos de poste"),
    PORTALS("portals", "Porticos"),
    PORTAL_TYPES("portal-types", "Tipos de portico"),
    PROFILE_STATUSES("profile-statuses", "Estados de perfil"),
    RETURN_SUPPORTS("return-supports", "Soportes de retorno"),
    SECTIONINGS("sectionings", "Seccionamientos"),
    STEADY_ARM_TYPES("steady-arm-types", "Tipos de brazo de atirantado"),
    SUPPORT_TYPES("support-types", "Tipos de soporte");

    private final String path;
    private final String title;

    LovResource(String path, String title) {
        this.path = path;
        this.title = title;
    }

    /** Ultimo segmento de la ruta publica: {@code /api/configuration/<path>}. */
    public String path() {
        return path;
    }

    public String title() {
        return title;
    }

    /** El catalogo cuyo ultimo segmento de ruta es {@code path}, si existe. */
    public static Optional<LovResource> byPath(String path) {
        return Arrays.stream(values()).filter(resource -> resource.path.equals(path)).findFirst();
    }
}

package com.alejandro.mtobackoffice.client.dto.jobs;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.Optional;

/**
 * Tipos de trabajo de mto-configuration. Anadir uno alli exige una migracion, pero la lista de
 * {@code GET /jobs} ensena todos, asi que uno nuevo llega antes que esta aplicacion: se lee como
 * {@code UNKNOWN}, que no tiene familia, y por eso no se consulta por separado ni se descarga.
 */
public enum JobType {

    PROFILE_EXPORT(JobFamily.PROFILE_JOBS, "Exportacion de perfiles"),
    PROFILE_BULK_CREATE(JobFamily.PROFILE_JOBS, "Alta masiva de perfiles"),
    PROFILE_BULK_UPDATE(JobFamily.PROFILE_JOBS, "Modificacion masiva de perfiles"),
    PROFILE_IMPORT(JobFamily.PROFILE_JOBS, "Importacion del maestro de perfiles"),
    LOV_IMPORT(JobFamily.LOV_JOBS, "Importacion del catalogo de LOV"),
    MASTER_DATA_REPUBLISH(JobFamily.REPUBLISH, "Republicado de datos maestros"),
    UNKNOWN(null, ClientEnums.UNKNOWN_LABEL);

    private final JobFamily family;
    private final String label;

    JobType(JobFamily family, String label) {
        this.family = family;
        this.label = label;
    }

    /** La familia bajo la que vive; {@code UNKNOWN} no tiene. */
    public Optional<JobFamily> family() {
        return Optional.ofNullable(family);
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static JobType of(String value) {
        return ClientEnums.parse(JobType.class, value, UNKNOWN);
    }

    public static List<JobType> selectable() {
        return ClientEnums.selectable(JobType.class, UNKNOWN);
    }
}

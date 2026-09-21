package com.alejandro.mtobackoffice.client.dto.jobs;

/** Tipos de trabajo de mto-configuration; anadir uno alli exige una migracion, asi que la lista es cerrada. */
public enum JobType {

    PROFILE_EXPORT(JobFamily.PROFILE_JOBS, "Exportacion de perfiles"),
    PROFILE_BULK_CREATE(JobFamily.PROFILE_JOBS, "Alta masiva de perfiles"),
    PROFILE_BULK_UPDATE(JobFamily.PROFILE_JOBS, "Modificacion masiva de perfiles"),
    PROFILE_IMPORT(JobFamily.PROFILE_JOBS, "Importacion del maestro de perfiles"),
    LOV_IMPORT(JobFamily.LOV_JOBS, "Importacion del catalogo de LOV"),
    MASTER_DATA_REPUBLISH(JobFamily.REPUBLISH, "Republicado de datos maestros");

    private final JobFamily family;
    private final String label;

    JobType(JobFamily family, String label) {
        this.family = family;
        this.label = label;
    }

    public JobFamily family() {
        return family;
    }

    public String label() {
        return label;
    }
}

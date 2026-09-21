package com.alejandro.mtobackoffice.client.dto.jobs;

/**
 * Cada familia de trabajos vive bajo su propio prefijo en mto-configuration, con su endpoint de
 * estado y, si produce fichero, el de descarga. El republicado no produce fichero.
 */
public enum JobFamily {

    PROFILE_JOBS("profiles/jobs", true),
    LOV_JOBS("lovs/jobs", true),
    REPUBLISH("master-data/republish", false);

    private final String path;
    private final boolean producesFile;

    JobFamily(String path, boolean producesFile) {
        this.path = path;
        this.producesFile = producesFile;
    }

    /** Ruta relativa al prefijo publico: {@code /api/configuration/<path>}. */
    public String path() {
        return path;
    }

    public boolean producesFile() {
        return producesFile;
    }
}

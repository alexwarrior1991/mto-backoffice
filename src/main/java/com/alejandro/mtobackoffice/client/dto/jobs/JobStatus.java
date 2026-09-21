package com.alejandro.mtobackoffice.client.dto.jobs;

/** Estados de un trabajo en segundo plano de mto-configuration (README_ASYNC_JOBS.md §3). */
public enum JobStatus {

    PENDING("En cola"),
    RUNNING("En curso"),
    COMPLETED("Terminado"),
    COMPLETED_WITH_ERRORS("Terminado con errores"),
    FAILED("Fallido"),
    REJECTED("Rechazado");

    private final String label;

    JobStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** {@code true} si el trabajo ya no va a cambiar: deja de consultarse. */
    public boolean isTerminal() {
        return this != PENDING && this != RUNNING;
    }
}

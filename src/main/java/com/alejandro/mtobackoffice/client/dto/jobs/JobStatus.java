package com.alejandro.mtobackoffice.client.dto.jobs;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Estados de un trabajo en segundo plano de mto-configuration (README_ASYNC_JOBS.md §3). Uno que el
 * servicio estrene se lee como {@code UNKNOWN} y se da por terminado: mejor dejar de consultar un
 * trabajo que quiza sigue (recargar lo trae) que consultar para siempre uno que ya acabo.
 */
public enum JobStatus {

    PENDING("En cola"),
    RUNNING("En curso"),
    COMPLETED("Terminado"),
    COMPLETED_WITH_ERRORS("Terminado con errores"),
    FAILED("Fallido"),
    REJECTED("Rechazado"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static JobStatus of(String value) {
        return ClientEnums.parse(JobStatus.class, value, UNKNOWN);
    }

    public static List<JobStatus> selectable() {
        return ClientEnums.selectable(JobStatus.class, UNKNOWN);
    }
}

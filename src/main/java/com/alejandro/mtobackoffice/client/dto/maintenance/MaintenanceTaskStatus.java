package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Estado de una tarea: {@code PENDING → IN_PROGRESS → COMPLETED}, o {@code CANCELLED} mientras este abierta. */
public enum MaintenanceTaskStatus {
    PENDING("Pendiente"),
    IN_PROGRESS("En curso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    MaintenanceTaskStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Pendiente o en curso: solo una tarea abierta se modifica o se cancela. */
    public boolean isOpen() {
        return this == PENDING || this == IN_PROGRESS;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MaintenanceTaskStatus of(String value) {
        return ClientEnums.parse(MaintenanceTaskStatus.class, value, UNKNOWN);
    }

    public static List<MaintenanceTaskStatus> selectable() {
        return ClientEnums.selectable(MaintenanceTaskStatus.class, UNKNOWN);
    }
}

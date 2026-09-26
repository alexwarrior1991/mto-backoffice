package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Estado de una orden: {@code DRAFT → PLANNED → ASSIGNED → IN_PROGRESS → COMPLETED}, o
 * {@code CANCELLED} desde cualquiera abierto. Que transicion vale la decide el servicio (409
 * {@code TRN-001}); aqui solo sirve para no ofrecer lo que va a fallar.
 */
public enum MaintenanceOrderStatus {
    DRAFT("Borrador"),
    PLANNED("Planificada"),
    ASSIGNED("Asignada"),
    IN_PROGRESS("En curso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    UNKNOWN(MaintenanceEnums.UNKNOWN_LABEL);

    private final String label;

    MaintenanceOrderStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MaintenanceOrderStatus of(String value) {
        return MaintenanceEnums.parse(MaintenanceOrderStatus.class, value, UNKNOWN);
    }

    public static List<MaintenanceOrderStatus> selectable() {
        return MaintenanceEnums.selectable(MaintenanceOrderStatus.class, UNKNOWN);
    }
}

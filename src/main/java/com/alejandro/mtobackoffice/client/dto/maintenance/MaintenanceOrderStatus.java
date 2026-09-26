package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
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
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    MaintenanceOrderStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /**
     * Lo que la pantalla ofrece en cada estado, copiado de la maquina de estados del servicio
     * ({@code OrderStateMachine}) solo para no ofrecer lo que va a fallar: quien decide sigue siendo
     * el servicio, y un {@code TRN-001} se notifica. {@code UNKNOWN} no ofrece nada.
     */
    public boolean isOpen() {
        return this == DRAFT || this == PLANNED || this == ASSIGNED || this == IN_PROGRESS;
    }

    public boolean canPlan() {
        return this == DRAFT;
    }

    /** {@code ASSIGNED} reasigna; {@code IN_PROGRESS} reasigna sin cambiar de estado. */
    public boolean canAssign() {
        return this == PLANNED || this == ASSIGNED || this == IN_PROGRESS;
    }

    /** Una urgente arranca sin planificar. */
    public boolean canStart(MaintenanceOrderType type) {
        return this == PLANNED || this == ASSIGNED || (this == DRAFT && type == MaintenanceOrderType.URGENT);
    }

    public boolean canComplete() {
        return this == IN_PROGRESS;
    }

    /** En borrador y planificada se modifica todo; despues, solo descripcion, prioridad y notas de cierre. */
    public boolean allowsFullUpdate() {
        return this == DRAFT || this == PLANNED;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MaintenanceOrderStatus of(String value) {
        return ClientEnums.parse(MaintenanceOrderStatus.class, value, UNKNOWN);
    }

    public static List<MaintenanceOrderStatus> selectable() {
        return ClientEnums.selectable(MaintenanceOrderStatus.class, UNKNOWN);
    }
}

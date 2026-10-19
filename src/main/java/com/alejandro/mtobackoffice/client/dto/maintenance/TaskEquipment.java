package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * El equipo que exige un tipo de tarea de mto-maintenance: la tarea solo se trabaja en un turno que lo
 * lleva (un vehiculo con plataforma aislada; para la puesta a tierra, un vehiculo que la tenga o una
 * pertiga de tierra entre sus instrumentos). Lo decide el servicio (409 {@code SHF-001}). El tipo de
 * tarea guarda los nombres tal como llegan ({@code TaskTypeDto.requiredEquipment}), para que uno que esta
 * pantalla no conoce vuelva tal cual al modificarlo; este enumerado los nombra.
 */
public enum TaskEquipment {
    INSULATED_PLATFORM("Plataforma aislada"),
    EARTHING("Puesta a tierra"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    TaskEquipment(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TaskEquipment of(String value) {
        return ClientEnums.parse(TaskEquipment.class, value, UNKNOWN);
    }

    public static List<TaskEquipment> selectable() {
        return ClientEnums.selectable(TaskEquipment.class, UNKNOWN);
    }

    /** El nombre de uno conocido, o el valor tal cual llego si no se conoce. */
    public static String labelOf(String value) {
        TaskEquipment equipment = of(value);
        return equipment == UNKNOWN ? value : equipment.label();
    }
}

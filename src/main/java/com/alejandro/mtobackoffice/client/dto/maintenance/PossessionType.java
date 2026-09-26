package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Posesion de la via en un turno: parcial (entre semana, entre dos seccionadores) o total (fin de semana o festivo). */
public enum PossessionType {
    PARTIAL("Parcial"),
    FULL("Total"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    PossessionType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PossessionType of(String value) {
        return ClientEnums.parse(PossessionType.class, value, UNKNOWN);
    }

    public static List<PossessionType> selectable() {
        return ClientEnums.selectable(PossessionType.class, UNKNOWN);
    }
}

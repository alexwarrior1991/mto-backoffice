package com.alejandro.mtobackoffice.client.dto.stock;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Los seis apuntes del libro de movimientos y su signo. Uno que mto-stock estrene se lee como
 * {@code UNKNOWN}, sin signo: la cantidad con signo la da el servicio ({@code signedQuantity}).
 */
public enum MovementType {
    ENTRY("Entrada", 1),
    OUTPUT("Salida", -1),
    POSITIVE_ADJUSTMENT("Ajuste positivo", 1),
    NEGATIVE_ADJUSTMENT("Ajuste negativo", -1),
    INCOMING_TRANSFER("Transferencia entrante", 1),
    OUTGOING_TRANSFER("Transferencia saliente", -1),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL, 0);

    private final String label;
    private final int sign;

    MovementType(String label, int sign) {
        this.label = label;
        this.sign = sign;
    }

    public String label() {
        return label;
    }

    public int sign() {
        return sign;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MovementType of(String value) {
        return ClientEnums.parse(MovementType.class, value, UNKNOWN);
    }

    public static List<MovementType> selectable() {
        return ClientEnums.selectable(MovementType.class, UNKNOWN);
    }
}

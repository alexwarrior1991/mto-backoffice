package com.alejandro.mtobackoffice.client.dto.stock;

/** Los seis apuntes del libro de movimientos y su signo. */
public enum MovementType {
    ENTRY("Entrada", 1),
    OUTPUT("Salida", -1),
    POSITIVE_ADJUSTMENT("Ajuste positivo", 1),
    NEGATIVE_ADJUSTMENT("Ajuste negativo", -1),
    INCOMING_TRANSFER("Transferencia entrante", 1),
    OUTGOING_TRANSFER("Transferencia saliente", -1);

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
}

package com.alejandro.mtobackoffice.client.dto.stock;

/** Sentido de un ajuste de inventario. */
public enum AdjustmentDirection {
    POSITIVE("Positivo: aparece material"),
    NEGATIVE("Negativo: falta material");

    private final String label;

    AdjustmentDirection(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

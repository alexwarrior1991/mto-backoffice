package com.alejandro.mtobackoffice.client.dto.stock;

/** Como se nombra una referencia del almacen en pantalla: el codigo y, si lo hay, el nombre. */
public final class StockLabels {

    private StockLabels() {
    }

    public static String codeAndName(String code, String name) {
        if (name == null || name.isBlank()) {
            return code == null ? "" : code;
        }
        return code == null || code.isBlank() ? name : code + " - " + name;
    }
}

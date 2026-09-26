package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Lo que cambio en un editor de mantenimiento frente a lo leido, para un {@link MergePatch}. Cada
 * metodo devuelve lo que tiene que viajar como valor ({@code null} si no cambio) y apunta como vaciado
 * el campo que tenia valor y ya no lo tiene. Que se puede vaciar lo decide el servicio; aqui lo
 * obligatorio lleva {@code asRequired} en el editor y nunca llega vacio.
 */
final class Changes {

    private final Set<String> cleared = new TreeSet<>();

    /** Un texto, recortado: igual no viaja, y en blanco vacia lo que habia. */
    String text(String field, String value, String original) {
        String current = value == null ? "" : value.trim();
        String before = original == null ? "" : original.trim();
        if (current.equals(before)) {
            return null;
        }
        if (current.isEmpty()) {
            cleared.add(field);
            return null;
        }
        return current;
    }

    /** Cualquier otro valor: igual no viaja, y {@code null} vacia lo que habia. */
    <T> T value(String field, T value, T original) {
        if (Objects.equals(value, original)) {
            return null;
        }
        if (value == null) {
            cleared.add(field);
        }
        return value;
    }

    /** Un numero: {@code 12.1} y {@code 12.100} son el mismo. */
    BigDecimal number(String field, BigDecimal value, BigDecimal original) {
        boolean same = value == null ? original == null : original != null && value.compareTo(original) == 0;
        return same ? null : value(field, value, original);
    }

    /** El parche con lo cambiado, lo vaciado y la version leida. */
    <T extends Record> MergePatch<T> patch(T values, Long version) {
        return new MergePatch<>(values, cleared, version);
    }
}

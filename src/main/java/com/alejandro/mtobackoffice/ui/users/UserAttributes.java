package com.alejandro.mtobackoffice.ui.users;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Los atributos de un usuario como texto, una linea {@code clave=valor} por valor: es lo que cabe
 * en un formulario sin una tabla editable. Una clave repetida acumula valores, que es como
 * Keycloak los guarda ({@code Map<String, List<String>>}).
 */
public final class UserAttributes {

    private UserAttributes() {
    }

    /**
     * @throws IllegalArgumentException con la linea que no se entiende (sin {@code =} o sin clave)
     */
    public static Map<String, List<String>> parse(String text) {
        Map<String, List<String>> attributes = new LinkedHashMap<>();
        if (text == null || text.isBlank()) {
            return attributes;
        }
        String[] lines = text.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            int separator = line.indexOf('=');
            String key = separator < 0 ? "" : line.substring(0, separator).trim();
            if (key.isEmpty()) {
                throw new IllegalArgumentException("Linea " + (i + 1) + ": se esperaba clave=valor");
            }
            attributes.computeIfAbsent(key, ignored -> new ArrayList<>()).add(line.substring(separator + 1).trim());
        }
        return attributes;
    }

    /** Las claves en orden, para que el texto sea el mismo cada vez que se abre el editor. */
    public static String format(Map<String, List<String>> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : new TreeMap<>(attributes).entrySet()) {
            List<String> values = entry.getValue() == null || entry.getValue().isEmpty() ? List.of("") : entry.getValue();
            for (String value : values) {
                if (!text.isEmpty()) {
                    text.append('\n');
                }
                text.append(entry.getKey()).append('=').append(value == null ? "" : value);
            }
        }
        return text.toString();
    }
}

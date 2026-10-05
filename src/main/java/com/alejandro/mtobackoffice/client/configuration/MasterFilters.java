package com.alejandro.mtobackoffice.client.configuration;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cuerpo y orden de un {@code POST /filter}. Lo que va a {@code null} o en blanco no se manda: para
 * el servicio un filtro ausente no filtra, y mandar {@code ""} o {@code false} no siempre significa
 * lo mismo.
 */
public final class MasterFilters {

    private MasterFilters() {
    }

    /** Pares clave/valor; se descartan los valores nulos y los textos en blanco. */
    public static Map<String, Object> of(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Pares clave/valor");
        }
        Map<String, Object> filter = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            Object value = keyValues[i + 1];
            if (value == null || (value instanceof String text && text.isBlank())) {
                continue;
            }
            filter.put(String.valueOf(keyValues[i]), value instanceof String text ? text.trim() : value);
        }
        return filter;
    }

    /** El desempate de las listas de mto-stock y mto-maintenance: el id, que no se repite nunca. */
    public static final String BY_ID = "id,asc";

    /**
     * El orden de una lista que lo necesita entero: el elegido en el Grid o, sin el, el de la
     * pantalla, y al final su desempate ({@link #withTieBreak}).
     */
    public static List<String> sort(List<QuerySortOrder> orders, List<String> fallback, String tieBreak) {
        List<String> chosen = sort(orders);
        return withTieBreak(chosen.isEmpty() ? fallback : chosen, tieBreak);
    }

    /**
     * El orden con su desempate al final, como {@code sortWithTieBreak} de mto-frontend: sin el, dos
     * filas iguales en la columna elegida (los dos apuntes de una transferencia, dos turnos del mismo
     * dia) podrian salir en dos paginas o en ninguna. Si el orden ya va por ese campo, no se repite.
     */
    public static List<String> withTieBreak(List<String> sort, String tieBreak) {
        String field = fieldOf(tieBreak);
        if (sort.stream().anyMatch(order -> fieldOf(order).equals(field))) {
            return List.copyOf(sort);
        }
        List<String> sorted = new ArrayList<>(sort);
        sorted.add(tieBreak);
        return List.copyOf(sorted);
    }

    private static String fieldOf(String order) {
        int comma = order.indexOf(',');
        return comma < 0 ? order : order.substring(0, comma);
    }

    /** {@code sort=campo,asc} por cada orden del Grid; sin orden, lista vacia y manda el del servicio. */
    public static List<String> sort(List<QuerySortOrder> orders) {
        List<String> sort = new ArrayList<>();
        for (QuerySortOrder order : orders) {
            sort.add(order.getSorted() + "," + (order.getDirection() == SortDirection.DESCENDING ? "desc" : "asc"));
        }
        return sort;
    }
}

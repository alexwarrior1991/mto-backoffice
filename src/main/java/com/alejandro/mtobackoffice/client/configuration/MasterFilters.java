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

    /** {@code sort=campo,asc} por cada orden del Grid; sin orden, lista vacia y manda el del servicio. */
    public static List<String> sort(List<QuerySortOrder> orders) {
        List<String> sort = new ArrayList<>();
        for (QuerySortOrder order : orders) {
            sort.add(order.getSorted() + "," + (order.getDirection() == SortDirection.DESCENDING ? "desc" : "asc"));
        }
        return sort;
    }
}

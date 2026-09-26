package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Una modificacion de mto-maintenance como {@code application/merge-patch+json} (RFC 7396): lo que
 * cambio viaja con su valor ({@code values}, el record de la modificacion con {@code null} en lo que
 * no se toca), lo que se vacio viaja a {@code null} ({@code cleared}) y lo demas no viaja. La
 * {@code version} leida va siempre: si otra persona guardo entre medias, el servicio responde 409
 * {@code CON-001} sin escribir nada. Que se puede vaciar lo decide el servicio (400 {@code VAL-001}
 * si no); los editores solo ofrecen vaciar lo que el admite.
 *
 * @param <T> el record de la modificacion, con los nombres de campo del servicio
 */
public record MergePatch<T extends Record>(T values, Set<String> cleared, Long version) {

    /** El tipo de contenido del RFC 7396; un PATCH con otro responde 415. */
    public static final String MEDIA_TYPE = "application/merge-patch+json";

    public MergePatch {
        cleared = Set.copyOf(cleared);
        Set<String> fields = Arrays.stream(values.getClass().getRecordComponents()).map(RecordComponent::getName)
                .collect(Collectors.toSet());
        for (String field : cleared) {
            if (!fields.contains(field)) {
                throw new IllegalArgumentException(values.getClass().getSimpleName() + " has no field " + field + " to clear");
            }
        }
    }

    /** Lo cambiado, sin vaciar nada. */
    public static <T extends Record> MergePatch<T> of(T values, Long version) {
        return new MergePatch<>(values, Set.of(), version);
    }

    /** El cuerpo que viaja: los valores puestos, los campos vaciados a {@code null} y la version. */
    @JsonValue
    public Map<String, Object> body() {
        Map<String, Object> body = new LinkedHashMap<>();
        for (RecordComponent component : values.getClass().getRecordComponents()) {
            Object value = read(component);
            if (value != null) {
                body.put(component.getName(), value);
            }
        }
        new TreeSet<>(cleared).forEach(field -> body.put(field, null));
        if (version != null) {
            body.put("version", version);
        }
        return body;
    }

    /** Nada que cambiar ni que vaciar: el editor se cierra sin llamar. No es {@code isEmpty()} por la misma razon que en los records. */
    public boolean changesNothing() {
        return cleared.isEmpty() && Arrays.stream(values.getClass().getRecordComponents()).allMatch(component -> read(component) == null);
    }

    private Object read(RecordComponent component) {
        try {
            return component.getAccessor().invoke(values);
        } catch (IllegalAccessException | InvocationTargetException failure) {
            throw new IllegalStateException("Cannot read " + component.getName() + " of " + values.getClass().getSimpleName(), failure);
        }
    }
}

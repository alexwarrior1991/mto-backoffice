package com.alejandro.mtobackoffice.client.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Una entrada de lista de valores de mto-configuration. Solo las claves que usa la UI tienen campo;
 * lo que va a {@code null} no se envia (el servicio ignora los campos de auditoria al escribir, y el
 * id lo manda la ruta).
 *
 * <p>Lo que el servicio manda y aqui no tiene campo ({@code drawingNumber}, el tipo de los tres
 * catalogos que lo tienen, o una clave que anada manana) cae en {@link #extras()} y vuelve tal cual:
 * el {@code PUT} sustituye la entrada entera (README_API.md §5 de mto-configuration), y lo que no
 * viajara se borraria. Es la misma excepcion a «solo las claves que usa la UI» que los maestros.</p>
 *
 * <p>{@code enabled} se envia siempre: en el servicio es un {@code boolean} primitivo y una entrada
 * sin el naceria desactivada.</p>
 *
 * <p>{@code versionNumber} vuelve al servicio tal como se leyo, y nunca se edita: es el bloqueo
 * optimista. Si otra persona ha guardado la entrada desde que esta pantalla la leyo, el servicio
 * responde 409 {@code CON-001} en vez de pisar su cambio. Un alta no lo lleva ({@code null} no
 * viaja), y el servicio no comprueba nada sin el.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LovDto(
        Long id,
        String code,
        String description,
        Boolean enabled,
        Integer versionNumber,
        LocalDateTime versionDate,
        String versionUser,
        Map<String, Object> extras
) {

    public LovDto(Long id, String code, String description, Boolean enabled, Integer versionNumber,
                  LocalDateTime versionDate, String versionUser, @JsonAnySetter Map<String, Object> extras) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.enabled = enabled;
        this.versionNumber = versionNumber;
        this.versionDate = versionDate;
        this.versionUser = versionUser;
        this.extras = extras == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(extras));
    }

    /** Una entrada sin nada mas que lo que tiene campo. */
    public LovDto(Long id, String code, String description, Boolean enabled, Integer versionNumber,
                  LocalDateTime versionDate, String versionUser) {
        this(id, code, description, enabled, versionNumber, versionDate, versionUser, Map.of());
    }

    public static LovDto forCreate(String code, String description, boolean enabled) {
        return new LovDto(null, code, description, enabled, null, null, null);
    }

    /** Lo que llego del servicio y aqui no tiene campo; vuelve tal cual en un {@code PUT}. */
    @Override
    @JsonAnyGetter
    public Map<String, Object> extras() {
        return extras;
    }

    /** La entrada leida con lo cambiado encima: lo que no tiene campo vuelve como se leyo. */
    public LovDto withValues(String code, String description, boolean enabled) {
        return new LovDto(id, code, description, enabled, versionNumber, versionDate, versionUser, extras);
    }

    public LovDto withEnabled(boolean enabled) {
        return new LovDto(id, code, description, enabled, versionNumber, versionDate, versionUser, extras);
    }

    /** Con el tipo padre por su id, que es como lo resuelve el servicio: {@code "foundationType": {"id": 3}}. */
    public LovDto withParent(String field, Long parentId) {
        Map<String, Object> changed = new LinkedHashMap<>(extras);
        changed.put(field, Map.of("id", parentId));
        return new LovDto(id, code, description, enabled, versionNumber, versionDate, versionUser, changed);
    }

    /** El tipo padre que trae la entrada en {@code field} (su id, su codigo y su descripcion). */
    public Optional<LovDto> parent(String field) {
        if (!(extras.get(field) instanceof Map<?, ?> parent) || !(parent.get("id") instanceof Number id)) {
            return Optional.empty();
        }
        return Optional.of(new LovDto(id.longValue(), text(parent.get("code")), text(parent.get("description")),
                null, null, null, null));
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }
}

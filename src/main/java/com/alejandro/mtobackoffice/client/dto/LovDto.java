package com.alejandro.mtobackoffice.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Una entrada de lista de valores de mto-configuration. Solo las claves que usa la UI: un campo
 * nuevo alla no rompe nada aqui, y lo que va a {@code null} no se envia (el servicio ignora los
 * campos de auditoria al escribir, y el id lo manda la ruta).
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
        String versionUser
) {

    public static LovDto forCreate(String code, String description, boolean enabled) {
        return new LovDto(null, code, description, enabled, null, null, null);
    }

    public LovDto withValues(String code, String description, boolean enabled) {
        return new LovDto(id, code, description, enabled, versionNumber, versionDate, versionUser);
    }

    public LovDto withEnabled(boolean enabled) {
        return new LovDto(id, code, description, enabled, versionNumber, versionDate, versionUser);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}

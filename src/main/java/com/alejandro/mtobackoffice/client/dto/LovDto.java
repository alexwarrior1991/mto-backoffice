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
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LovDto(
        Long id,
        String code,
        String description,
        Boolean enabled,
        LocalDateTime versionDate,
        String versionUser
) {

    public static LovDto forCreate(String code, String description, boolean enabled) {
        return new LovDto(null, code, description, enabled, null, null);
    }

    public LovDto withValues(String code, String description, boolean enabled) {
        return new LovDto(id, code, description, enabled, versionDate, versionUser);
    }

    public LovDto withEnabled(boolean enabled) {
        return new LovDto(id, code, description, enabled, versionDate, versionUser);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }
}

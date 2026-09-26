package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

/**
 * Modificacion parcial: {@code null} es «no tocar», asi que solo viaja lo que cambio. Sin bloqueo
 * optimista: lo ultimo que llega gana. En un activo sincronizado solo valen {@code description},
 * {@code enabled} y {@code preventiveIntervalDays}; {@code enabled} de esos no se ofrece, porque el
 * siguiente evento de mto-configuration lo vuelve a escribir.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AssetUpdateRequest(String name, String description, Boolean enabled, Integer preventiveIntervalDays,
                                 Long executionPackageId, Long trackId, Long stationId, BigDecimal startKp, BigDecimal endKp,
                                 TrackKind trackKind) {

    public static AssetUpdateRequest enabled(boolean enabled) {
        return new AssetUpdateRequest(null, null, enabled, null, null, null, null, null, null, null);
    }

    /** Nada que mandar: el editor se cierra sin llamar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return equals(new AssetUpdateRequest(null, null, null, null, null, null, null, null, null, null));
    }
}

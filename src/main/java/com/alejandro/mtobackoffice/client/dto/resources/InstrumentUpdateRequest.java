package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;

/**
 * Lo que se cambia de un instrumento, para un {@code MergePatch}: todo menos el codigo. Renovar su
 * calibracion es cambiar sus fechas; {@code active=false} lo retira y pide {@code resources-delete}.
 */
public record InstrumentUpdateRequest(String name, InstrumentKind kind, String serialNumber, LocalDate calibratedOn,
                                      LocalDate calibrationExpiresOn, String notes, Boolean active) {

    public static InstrumentUpdateRequest active(boolean active) {
        return new InstrumentUpdateRequest(null, null, null, null, null, null, active);
    }
}

package com.alejandro.mtobackoffice.client.dto.resources;

/**
 * Lo que se cambia de un tipo de cualificacion, para un {@code MergePatch}: todo menos el codigo. Se
 * vacian el permiso, la vigencia y la descripcion; {@code active=false} lo retira y pide
 * {@code resources-delete}.
 */
public record QualificationTypeUpdateRequest(String name, QualificationKind kind, DrivingLicence drivingLicence, Integer validityMonths,
                                             String description, Boolean active) {

    public static QualificationTypeUpdateRequest active(boolean active) {
        return new QualificationTypeUpdateRequest(null, null, null, null, null, active);
    }
}

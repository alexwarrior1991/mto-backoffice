package com.alejandro.mtobackoffice.client.dto.resources;

/** Los filtros de la lista de sitios de aparcamiento; lo que va a {@code null} no viaja. */
public record ParkingSiteFilter(String search, ParkingSiteKind kind, Long executionPackageId, Long trackId, Boolean railAccess,
                                Boolean roadAccess, Boolean active) {

    public static final ParkingSiteFilter NONE = new ParkingSiteFilter(null, null, null, null, null, null, null);

    public ParkingSiteFilter {
        search = search == null || search.isBlank() ? null : search.trim();
    }
}

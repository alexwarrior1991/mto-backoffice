package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.UUID;

/** Los filtros de la lista de vehiculos; lo que va a {@code null} no viaja. {@code available}: activo y operativo o limitado. */
public record VehicleFilter(String search, UUID typeId, VehicleMobility mobility, VehicleCategory category, VehicleOperationalStatus status,
                            Boolean available, Boolean active) {

    public static final VehicleFilter NONE = new VehicleFilter(null, null, null, null, null, null, null);

    public VehicleFilter {
        search = search == null || search.isBlank() ? null : search.trim();
    }

    /** Para un selector: los activos y disponibles, buscando por texto. */
    public static VehicleFilter availableMatching(String search) {
        return new VehicleFilter(search, null, null, null, null, true, true);
    }
}

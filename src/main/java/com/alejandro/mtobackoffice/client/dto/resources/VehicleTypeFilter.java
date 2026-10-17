package com.alejandro.mtobackoffice.client.dto.resources;

/** Los filtros de la lista de modelos; lo que va a {@code null} no viaja. */
public record VehicleTypeFilter(String search, VehicleMobility mobility, VehicleCategory category, String capability, Boolean active) {

    public static final VehicleTypeFilter NONE = new VehicleTypeFilter(null, null, null, null, null);

    public VehicleTypeFilter {
        search = search == null || search.isBlank() ? null : search.trim();
    }
}

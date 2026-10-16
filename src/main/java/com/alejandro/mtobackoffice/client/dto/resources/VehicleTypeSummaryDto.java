package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/** El modelo de un vehiculo dentro de su respuesta: lo que decide que exige (matricula, numero ferroviario) y donde puede estar. */
public record VehicleTypeSummaryDto(UUID id, String code, String name, VehicleMobility mobility, VehicleCategory category,
                                    Boolean selfPropelled, Integer seats, RailGearType railGearType, Boolean transportModeAuthorised,
                                    Set<String> capabilities) {

    public VehicleTypeSummaryDto {
        capabilities = capabilities == null ? Set.of() : Collections.unmodifiableSet(new TreeSet<>(capabilities));
    }

    public String label() {
        return name == null || name.isBlank() || name.equals(code) ? code : code + " · " + name;
    }

    public static VehicleTypeSummaryDto of(VehicleTypeDto type) {
        return new VehicleTypeSummaryDto(type.id(), type.code(), type.name(), type.mobility(), type.category(), type.selfPropelled(),
                type.seats(), type.railGearType(), type.transportModeAuthorised(), type.capabilities());
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof VehicleTypeSummaryDto that && id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

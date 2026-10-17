package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Lo que se cambia de una unidad, para un {@code MergePatch}. Los linemen no van aqui sino en
 * {@code /members}, salvo al reactivar un equipo, que vuelve completo ({@code active=true} con sus
 * linemen). El servicio deja vaciar el jefe (salvo en un equipo, que se sustituye), los EP y el
 * vehiculo por defecto. {@code active=false} desactiva, como el {@code DELETE}.
 */
public record OrganizationUnitUpdateRequest(String name, UUID parentId, UUID headPersonId, Set<Long> executionPackageIds,
                                            UUID defaultVehicleId, Boolean active, List<UUID> memberPersonIds, LocalDate joinedOn) {

    /** Reactivar un equipo: vuelve con sus linemen, de 2 a 6, desde esa fecha. */
    public static OrganizationUnitUpdateRequest reactivateTeam(List<UUID> memberPersonIds, LocalDate joinedOn) {
        return new OrganizationUnitUpdateRequest(null, null, null, null, null, true, List.copyOf(memberPersonIds), joinedOn);
    }

    public static OrganizationUnitUpdateRequest reactivate() {
        return new OrganizationUnitUpdateRequest(null, null, null, null, null, true, null, null);
    }
}

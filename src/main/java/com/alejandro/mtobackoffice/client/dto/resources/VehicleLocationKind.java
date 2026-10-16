package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Donde esta un vehiculo. {@code UNKNOWN} es aqui un valor del servicio («no se sabe donde esta», lo que
 * deja un turno sin aparcamiento) y tambien lo que se lee de un valor que esta aplicacion no conoce:
 * los dos se dicen «Desconocida». Una ubicacion no se modifica, asi que lo leido nunca vuelve al servicio.
 *
 * <p>Lo que admite cada movilidad esta copiado de {@code FleetRules} solo para no ofrecer lo que va a
 * fallar (409 {@code VEH-001}): uno de solo via nunca va por carretera, uno de solo carretera nunca esta
 * en la via.</p>
 */
public enum VehicleLocationKind {
    PARKED("Aparcado"),
    ON_TRACK("En via"),
    ON_ROAD("Por carretera"),
    IN_WORKSHOP("En taller"),
    ATTACHED("Con otro vehiculo"),
    UNKNOWN("Desconocida");

    private final String label;

    VehicleLocationKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleLocationKind of(String value) {
        return ClientEnums.parse(VehicleLocationKind.class, value, UNKNOWN);
    }

    /** Para un filtro: todas, tambien «no se sabe». */
    public static List<VehicleLocationKind> filterable() {
        return List.of(values());
    }

    /** Lo que se puede registrar de un vehiculo de esta movilidad. */
    public static List<VehicleLocationKind> recordable(VehicleMobility mobility) {
        return switch (mobility == null ? VehicleMobility.UNKNOWN : mobility) {
            case RAIL_ONLY -> List.of(PARKED, ON_TRACK, IN_WORKSHOP, ATTACHED, UNKNOWN);
            case ROAD_ONLY -> List.of(PARKED, ON_ROAD, IN_WORKSHOP, ATTACHED, UNKNOWN);
            case ROAD_RAIL -> List.of(PARKED, ON_TRACK, ON_ROAD, IN_WORKSHOP, ATTACHED, UNKNOWN);
            case UNKNOWN -> List.of(UNKNOWN);
        };
    }
}

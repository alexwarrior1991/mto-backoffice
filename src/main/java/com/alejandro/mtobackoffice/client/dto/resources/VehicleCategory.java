package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Que es un modelo de vehiculo. Cada categoria es de una sola movilidad, copiada del servicio solo para no ofrecer en el editor lo que va a fallar (400). */
public enum VehicleCategory {
    CATENARY_TOWER_CAR("Dresina de catenaria", VehicleMobility.RAIL_ONLY),
    WIRING_TRAIN("Tren de tendido", VehicleMobility.RAIL_ONLY),
    TRACK_MAINTENANCE_CAR("Dresina de via", VehicleMobility.RAIL_ONLY),
    INSPECTION_CAR("Vehiculo de auscultacion", VehicleMobility.RAIL_ONLY),
    LOCOMOTIVE("Locomotora", VehicleMobility.RAIL_ONLY),
    RAIL_WAGON("Vagon o plataforma", VehicleMobility.RAIL_ONLY),
    TROLLEY("Vagoneta", VehicleMobility.RAIL_ONLY),
    ROAD_RAIL_MEWP("Plataforma elevadora bivial", VehicleMobility.ROAD_RAIL),
    ROAD_RAIL_TRUCK("Camion bivial", VehicleMobility.ROAD_RAIL),
    ROAD_RAIL_EXCAVATOR("Excavadora bivial", VehicleMobility.ROAD_RAIL),
    ROAD_RAIL_TRACTOR("Tractor bivial", VehicleMobility.ROAD_RAIL),
    ROAD_RAIL_TRAILER("Remolque bivial", VehicleMobility.ROAD_RAIL),
    VAN("Furgoneta", VehicleMobility.ROAD_ONLY),
    PICKUP("Pick-up", VehicleMobility.ROAD_ONLY),
    CREW_BUS("Autobus de personal", VehicleMobility.ROAD_ONLY),
    CRANE_TRUCK("Camion grua", VehicleMobility.ROAD_ONLY),
    TRUCK("Camion", VehicleMobility.ROAD_ONLY),
    LOW_LOADER("Gondola", VehicleMobility.ROAD_ONLY),
    CAR("Turismo", VehicleMobility.ROAD_ONLY),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL, VehicleMobility.UNKNOWN);

    private final String label;
    private final VehicleMobility mobility;

    VehicleCategory(String label, VehicleMobility mobility) {
        this.label = label;
        this.mobility = mobility;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VehicleCategory of(String value) {
        return ClientEnums.parse(VehicleCategory.class, value, UNKNOWN);
    }

    public static List<VehicleCategory> selectable() {
        return ClientEnums.selectable(VehicleCategory.class, UNKNOWN);
    }

    public VehicleMobility mobility() {
        return mobility;
    }

    /** Las de una movilidad, para el desplegable del editor. */
    public static List<VehicleCategory> of(VehicleMobility mobility) {
        return selectable().stream().filter(category -> category.mobility == mobility).toList();
    }
}

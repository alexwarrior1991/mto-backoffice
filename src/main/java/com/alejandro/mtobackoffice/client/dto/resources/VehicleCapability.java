package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.Arrays;
import java.util.List;

/**
 * Lo que sabe hacer un modelo de vehiculo. Los DTO guardan las capacidades como texto, no como este
 * enumerado: el conjunto viaja entero al modificarlo, y una capacidad que el servicio estrene antes que
 * esta aplicacion tiene que volver tal cual en vez de perderse. Este enumerado da las que se ofrecen y
 * su nombre; una desconocida se pinta con su codigo.
 */
public enum VehicleCapability {
    CRANE("Grua"),
    ELEVATING_PLATFORM("Plataforma elevadora"),
    INSULATED_PLATFORM("Plataforma aislada"),
    ROTATING_PLATFORM("Plataforma giratoria"),
    PANTOGRAPH("Pantografo"),
    CONTACT_WIRE_HOLDER("Soporte del hilo de contacto"),
    WIRE_DRUM("Bobina de hilo"),
    OCS_MEASURING("Medicion de catenaria"),
    TRACK_MEASURING("Medicion de via"),
    EARTHING("Puesta a tierra"),
    TOWING("Remolca"),
    CREW_CABIN("Cabina de personal"),
    WORKSHOP("Taller");

    private final String label;

    VehicleCapability(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** Los codigos que se ofrecen, en su orden. */
    public static List<String> codes() {
        return Arrays.stream(values()).map(Enum::name).toList();
    }

    /** El nombre de una capacidad por su codigo; una que no se conoce, con su codigo. */
    public static String labelOf(String code) {
        return Arrays.stream(values()).filter(capability -> capability.name().equals(code)).findFirst().map(VehicleCapability::label)
                .orElse(code);
    }
}

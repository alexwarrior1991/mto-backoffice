package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.ui.support.RevisionsDialog;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * El historial de recursos: el de Envers en mto-resources, en el mismo {@link RevisionsDialog} que
 * almacen y mantenimiento, con una linea por recurso que dice como quedo en cada revision. Es lectura,
 * asi que lo ofrece toda fila a quien puede leer. Sin revisiones el servicio responde 404 y el
 * dialogo lo dice como «sin historial todavia».
 */
final class ResourcesHistory {

    private ResourcesHistory() {
    }

    static <D> RevisionsDialog<D> dialog(String code, RevisionsDialog.Source<D> source, Function<D, String> describe) {
        return new RevisionsDialog<>(code, source, describe);
    }

    static String person(PersonDto person) {
        return join(person.fullName(), ResourcesFormats.category(person.category()), ResourcesFormats.employer(person), person.username(),
                ResourcesFormats.state(person.active()).toLowerCase());
    }

    /** La unidad como quedo; sus linemen tienen su propio historial en la pestana del equipo. */
    static String unit(OrganizationUnitDto unit) {
        return join(unit.name(), ResourcesFormats.kind(unit.kind()), ResourcesFormats.discipline(unit.discipline()),
                unit.parent() == null ? null : "en " + unit.parent().code(), "jefe " + ResourcesFormats.head(unit.head()).toLowerCase(),
                unit.executionPackageIds().isEmpty() ? null : "EP " + unit.executionPackageIds().stream().sorted().map(String::valueOf)
                        .collect(Collectors.joining(", ")),
                ResourcesFormats.state(unit.active()).toLowerCase());
    }

    /** El vehiculo como quedo; donde estuvo va en su pestana de ubicaciones, no aqui. */
    static String vehicle(VehicleDto vehicle) {
        return join(vehicle.name(), vehicle.type() == null ? null : vehicle.type().code(), vehicle.registrationPlate(), vehicle.railVehicleNumber(),
                ResourcesFormats.operational(vehicle.operationalStatus(), vehicle.statusReason()), ResourcesFormats.owner(vehicle),
                ResourcesFormats.vehicleState(vehicle.active()).toLowerCase());
    }

    /** Las partes que hay, separadas por {@code ·}; lo vacio no deja separadores sueltos. */
    static String join(String... parts) {
        return Arrays.stream(parts).filter(part -> part != null && !part.isBlank()).collect(Collectors.joining(" · "));
    }
}

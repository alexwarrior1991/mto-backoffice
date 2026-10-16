package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOperationalStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOwner;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonEmployer;
import com.alejandro.mtobackoffice.client.dto.resources.PersonSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.WorkDiscipline;
import com.alejandro.mtobackoffice.ui.support.Formats;

import java.time.LocalDate;

/** Como se dicen en pantalla las cosas de mto-resources. */
final class ResourcesFormats {

    private ResourcesFormats() {
    }

    static String category(PersonCategory category) {
        return category == null ? "" : category.label();
    }

    /** Propio, o la empresa de una subcontrata. */
    static String employer(PersonDto person) {
        if (person.employer() == PersonEmployer.SUBCONTRACTOR) {
            return person.company() == null || person.company().isBlank() ? PersonEmployer.SUBCONTRACTOR.label() : person.company();
        }
        return person.employer() == null ? "" : person.employer().label();
    }

    static String state(Boolean active) {
        return Boolean.TRUE.equals(active) ? "Activa" : "Inactiva";
    }

    static String kind(OrganizationUnitKind kind) {
        return kind == null ? "" : kind.label();
    }

    static String discipline(WorkDiscipline discipline) {
        return discipline == null ? "" : discipline.label();
    }

    /** El jefe de una unidad, o «Vacante»: solo un equipo lo tiene siempre. */
    static String head(PersonSummaryDto head) {
        return head == null ? "Vacante" : head.label();
    }

    /** Los linemen de un equipo frente a la regla: «4 de 2 a 6». Nada en las demas unidades. */
    static String linemen(OrganizationUnitKind kind, Integer linemen) {
        return kind != OrganizationUnitKind.TEAM ? "" : (linemen == null ? 0 : linemen) + " de 2 a 6";
    }

    static String date(LocalDate date) {
        return date == null ? "" : Formats.date(date);
    }

    /** Por donde se llega a un sitio: «Via y carretera», «Via», «Carretera». */
    static String access(ParkingSiteDto site) {
        boolean rail = Boolean.TRUE.equals(site.railAccess());
        boolean road = Boolean.TRUE.equals(site.roadAccess());
        return rail && road ? "Via y carretera" : rail ? "Via" : road ? "Carretera" : "";
    }

    /** Propio, o la empresa de uno alquilado o de subcontrata. */
    static String owner(VehicleDto vehicle) {
        if (vehicle.owner() != null && vehicle.owner() != VehicleOwner.OWN && vehicle.ownerCompany() != null && !vehicle.ownerCompany().isBlank()) {
            return vehicle.owner().label() + " · " + vehicle.ownerCompany();
        }
        return vehicle.owner() == null ? "" : vehicle.owner().label();
    }

    /** Como esta un vehiculo, con el motivo si no esta operativo. */
    static String operational(VehicleOperationalStatus status, String reason) {
        if (status == null) {
            return "";
        }
        return status != VehicleOperationalStatus.OPERATIONAL && reason != null && !reason.isBlank() ? status.label() + " · " + reason : status.label();
    }

    static String vehicleState(Boolean active) {
        return Boolean.TRUE.equals(active) ? "Activo" : "Retirado";
    }

    /**
     * Donde esta (o estuvo) un vehiculo, en una linea: «Aparcado · APT-RSH · VIA 1 · kp 12.300», «En via ·
     * VIA 1 · kp 10.5», «Remolcado por DR-02». Sin ubicacion, «Sin ubicacion».
     */
    static String place(VehicleLocationDto location, ResourcesNames names) {
        if (location == null) {
            return "Sin ubicacion";
        }
        if (location.kind() == VehicleLocationKind.ATTACHED && location.carrier() != null) {
            String how = location.attachment() == null ? location.kind().label() : location.attachment().label();
            return how + " por " + location.carrier().code();
        }
        return ResourcesHistory.join(location.kind() == null ? null : location.kind().label(),
                location.parkingSite() == null ? null : location.parkingSite().code(),
                location.trackId() == null ? null : names.trackName(location.trackId()),
                location.kp() == null ? null : "kp " + location.kp().toPlainString(),
                location.description());
    }

    /** De donde salio una ubicacion y cuando: «Turno · 16/10/2026 05:40 · service-account-mto-maintenance-svc». */
    static String origin(VehicleLocationDto location) {
        return location == null ? "" : ResourcesHistory.join(location.source() == null ? null : location.source().label(),
                location.observedAt() == null ? null : Formats.dateTime(location.observedAt()), location.recordedBy());
    }
}

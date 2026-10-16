package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
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
}

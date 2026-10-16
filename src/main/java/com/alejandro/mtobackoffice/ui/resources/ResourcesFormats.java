package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonEmployer;

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
}

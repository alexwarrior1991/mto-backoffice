package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** De quien es empleada una persona; una de subcontrata dice su empresa. */
public enum PersonEmployer {
    OWN("Propio"),
    SUBCONTRACTOR("Subcontrata"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    PersonEmployer(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PersonEmployer of(String value) {
        return ClientEnums.parse(PersonEmployer.class, value, UNKNOWN);
    }

    public static List<PersonEmployer> selectable() {
        return ClientEnums.selectable(PersonEmployer.class, UNKNOWN);
    }
}

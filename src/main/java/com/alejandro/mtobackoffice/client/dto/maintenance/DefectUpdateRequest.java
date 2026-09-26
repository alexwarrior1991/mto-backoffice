package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;

/** Modificacion parcial de un defecto que no este cerrado ni descartado. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DefectUpdateRequest(DefectSeverity severity, String description, String technicalNotes, String correctionType,
                                  String partsReplaced, LocalDate repairPlannedDate, List<String> photoRefs) {

    /** Nada que mandar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return equals(new DefectUpdateRequest(null, null, null, null, null, null, null));
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Modificacion parcial de una tarea abierta; {@code taskTypeCodes} sustituye a los que tenia. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskUpdateRequest(String description, String assignedUser, List<String> taskTypeCodes, String notes, String defectsFound,
                                List<String> photoRefs) {

    /** Nada que mandar. No es {@code isEmpty()} porque Jackson lo serializaria como propiedad. */
    public boolean changesNothing() {
        return equals(new TaskUpdateRequest(null, null, null, null, null, null));
    }
}

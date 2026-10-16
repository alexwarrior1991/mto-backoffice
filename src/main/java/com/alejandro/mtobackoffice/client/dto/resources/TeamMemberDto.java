package com.alejandro.mtobackoffice.client.dto.resources;

import java.time.LocalDate;
import java.util.UUID;

/** Un lineman de un equipo, con su entrada y, si ya salio, su salida. */
public record TeamMemberDto(UUID id, PersonSummaryDto person, LocalDate joinedOn, LocalDate leftOn) {

    public boolean isCurrent() {
        return leftOn == null;
    }
}

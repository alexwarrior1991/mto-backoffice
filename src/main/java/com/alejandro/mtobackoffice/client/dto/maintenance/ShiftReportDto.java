package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.List;

/** El parte diario de un turno: su cabecera, los recuentos y una fila por tarea trabajada. */
public record ShiftReportDto(ShiftDto shift, int tasksCompleted, int tasksPending, int profilesReviewed, int defectsFound, int defectsResolved,
                             List<ShiftReportRowDto> rows) {

    public ShiftReportDto {
        rows = rows == null ? List.of() : List.copyOf(rows);
    }
}

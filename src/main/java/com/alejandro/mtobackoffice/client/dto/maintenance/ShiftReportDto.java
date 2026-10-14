package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.List;

/**
 * El parte diario de un turno: su cabecera, los recuentos, una fila por tarea trabajada y las
 * inspecciones registradas en el. {@code inspectionsRecorded} lo cuenta el servicio; es un
 * {@code Integer} para que un servicio que todavia no lo manda se lea igual.
 */
public record ShiftReportDto(ShiftDto shift, int tasksCompleted, int tasksPending, int profilesReviewed, int defectsFound, int defectsResolved,
                             List<ShiftReportRowDto> rows, Integer inspectionsRecorded, List<ShiftReportInspectionDto> inspections) {

    public ShiftReportDto {
        rows = rows == null ? List.of() : List.copyOf(rows);
        inspections = inspections == null ? List.of() : List.copyOf(inspections);
    }
}

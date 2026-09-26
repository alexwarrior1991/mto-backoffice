package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/** El resumen de un mes: turnos, minutos netos, ordenes y tareas, perfiles, km, defectos y materiales. Lo calcula el servicio. */
public record MonthlyReportDto(YearMonth month, Long executionPackageId, int shiftsPlanned, int shiftsClosed, int shiftsCancelled,
                               int netWorkMinutes, BigDecimal averageNetMinutesPerShift, int ordersCompleted, int tasksCompleted,
                               long profilesChecked, BigDecimal coveredKm, int defectsDetected, int defectsResolved,
                               int correctiveOrdersCreated, List<MonthlyMaterialLineDto> materials) {

    public MonthlyReportDto {
        materials = materials == null ? List.of() : List.copyOf(materials);
    }
}

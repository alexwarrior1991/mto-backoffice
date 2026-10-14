package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;

/**
 * Lo que genero el servicio: tareas nuevas, perfiles que ya tenian tarea, el total y la estimacion de
 * carga. {@code skippedAssets} cuenta todo lo que ya tenia tarea, perfiles y aisladores; es un
 * {@code Integer} para que un servicio que todavia no lo manda se lea igual.
 */
public record GenerateTasksResultDto(int createdTasks, int skippedProfiles, int totalTasks, BigDecimal estimatedMinutes, int estimatedShifts,
                                     Integer skippedAssets) {

    /** Lo que ya tenia tarea: todos los activos si el servicio lo cuenta, si no los perfiles. */
    public int skipped() {
        return skippedAssets == null ? skippedProfiles : skippedAssets;
    }
}

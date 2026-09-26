package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;

/** Lo que genero el servicio: tareas nuevas, perfiles que ya tenian tarea, el total y la estimacion de carga. */
public record GenerateTasksResultDto(int createdTasks, int skippedProfiles, int totalTasks, BigDecimal estimatedMinutes, int estimatedShifts) {
}

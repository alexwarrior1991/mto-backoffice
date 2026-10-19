package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.Set;

/**
 * Lo que se cambia de un tipo de tarea, para un {@code MergePatch}: solo el equipo que exige, entero (lo
 * que llega sustituye a lo que habia), y {@code null} para que no exija ninguno. Pide
 * {@code maintenance-supervise} ademas de {@code maintenance-write}.
 */
public record TaskTypeUpdateRequest(Set<String> requiredEquipment) {
}

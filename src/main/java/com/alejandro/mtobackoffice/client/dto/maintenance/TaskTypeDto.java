package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Un tipo de tarea del plan de mantenimiento ({@code RG-xx}/{@code RP-xx}), con los minutos
 * estandar que alimentan la estimacion de carga. Es un catalogo del servicio: lo unico que se cambia
 * aqui es el equipo que exige ({@code requiredEquipment}, los nombres tal como llegan; ver
 * {@link TaskEquipment}), con la {@code version} leida.
 */
public record TaskTypeDto(UUID id, String code, String description, FunctionalGroup functionalGroup, BigDecimal standardMinutesPerUnit,
                          TaskUnit unit, BigDecimal fixedMinutes, Boolean requiresFullPossession, Boolean diagnostic, Boolean active,
                          Integer orderIndex, Set<String> requiredEquipment, Long version) {

    public TaskTypeDto {
        requiredEquipment = requiredEquipment == null ? Set.of() : Set.copyOf(requiredEquipment);
    }

    public String label() {
        return description == null || description.isBlank() ? code : code + " - " + description;
    }
}

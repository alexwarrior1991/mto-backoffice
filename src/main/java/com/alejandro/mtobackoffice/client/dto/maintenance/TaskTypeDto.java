package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Un tipo de tarea del plan de mantenimiento ({@code RG-xx}/{@code RP-xx}), con los minutos
 * estandar que alimentan la estimacion de carga. Es un catalogo del servicio: aqui solo se lee.
 */
public record TaskTypeDto(UUID id, String code, String description, FunctionalGroup functionalGroup, BigDecimal standardMinutesPerUnit,
                          TaskUnit unit, BigDecimal fixedMinutes, Boolean requiresFullPossession, Boolean diagnostic, Boolean active,
                          Integer orderIndex) {

    public String label() {
        return description == null || description.isBlank() ? code : code + " - " + description;
    }
}

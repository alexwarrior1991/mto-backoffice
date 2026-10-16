package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.List;

/**
 * La categoria de una persona en mto-resources, con su rango: el jefe de una unidad tiene una
 * categoria de rango igual o mayor que la que pide su nivel. El rango esta copiado del servicio solo
 * para no ofrecer en un desplegable lo que va a fallar; quien decide es el servicio (409
 * {@code UNI-001}). Una categoria nueva alli se lee como {@code UNKNOWN}, de rango 0.
 */
public enum PersonCategory {
    WORKER("Operario", 1),
    TEAM_LEADER("Jefe de equipo", 2),
    SUPERVISOR("Supervisor", 3),
    CHIEF_SUPERVISOR("Jefe de supervisores", 4),
    CHIEF_MAINTENANCE("Jefe de mantenimiento", 5),
    CHIEF_CONSTRUCTION("Jefe de construccion", 5),
    PROJECT_MANAGER("Jefe de proyecto", 6),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL, 0);

    private final String label;
    private final int rank;

    PersonCategory(String label, int rank) {
        this.label = label;
        this.rank = rank;
    }

    public String label() {
        return label;
    }

    public int rank() {
        return rank;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PersonCategory of(String value) {
        return ClientEnums.parse(PersonCategory.class, value, UNKNOWN);
    }

    public static List<PersonCategory> selectable() {
        return ClientEnums.selectable(PersonCategory.class, UNKNOWN);
    }

    /** Las que llegan a un rango: el jefe de un turno es cualquiera menos un operario ({@code atLeast(2)}). */
    public static List<PersonCategory> atLeast(int rank) {
        return Arrays.stream(values()).filter(category -> category != UNKNOWN && category.rank >= rank).toList();
    }
}

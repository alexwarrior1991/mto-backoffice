package com.alejandro.mtobackoffice.client.dto.resources;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * El nivel de una unidad del organigrama, del proyecto al equipo. Las reglas de abajo estan copiadas
 * de {@code OrganizationRules} de mto-resources solo para no ofrecer lo que va a fallar: un padre de
 * un nivel estrictamente superior (un proyecto no cuelga de nada) y un jefe de la categoria del nivel.
 * Quien decide es el servicio (409 {@code UNI-001}). Un nivel nuevo alli se lee como {@code UNKNOWN},
 * que no ofrece nada.
 */
public enum OrganizationUnitKind {
    PROJECT("Proyecto", 5),
    DEPARTMENT("Departamento", 4),
    CHIEF_SUPERVISION("Jefatura de supervisores", 3),
    SUPERVISION("Supervision", 2),
    TEAM("Equipo", 1),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL, 0);

    private final String label;
    private final int level;

    OrganizationUnitKind(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public String label() {
        return label;
    }

    public int level() {
        return level;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OrganizationUnitKind of(String value) {
        return ClientEnums.parse(OrganizationUnitKind.class, value, UNKNOWN);
    }

    public static List<OrganizationUnitKind> selectable() {
        return ClientEnums.selectable(OrganizationUnitKind.class, UNKNOWN);
    }

    /** Si una unidad de este nivel puede colgar de una de {@code parent}: de un nivel estrictamente superior. */
    public boolean canHangFrom(OrganizationUnitKind parent) {
        return this != PROJECT && this != UNKNOWN && parent != null && parent != UNKNOWN && parent.level > level;
    }

    /**
     * Las categorias de quien puede dirigir una unidad de este nivel: un equipo, un jefe de equipo o
     * mas; una supervision, un supervisor o mas. Un departamento lo dirige el jefe de su disciplina o el
     * de proyecto, nunca el de la otra.
     */
    public List<PersonCategory> headCategories(WorkDiscipline discipline) {
        return switch (this) {
            case PROJECT -> List.of(PersonCategory.PROJECT_MANAGER);
            case DEPARTMENT -> discipline == WorkDiscipline.CONSTRUCTION
                    ? List.of(PersonCategory.CHIEF_CONSTRUCTION, PersonCategory.PROJECT_MANAGER)
                    : List.of(PersonCategory.CHIEF_MAINTENANCE, PersonCategory.PROJECT_MANAGER);
            case CHIEF_SUPERVISION -> PersonCategory.atLeast(PersonCategory.CHIEF_SUPERVISOR.rank());
            case SUPERVISION -> PersonCategory.atLeast(PersonCategory.SUPERVISOR.rank());
            case TEAM -> PersonCategory.atLeast(PersonCategory.TEAM_LEADER.rank());
            case UNKNOWN -> List.of();
        };
    }

    /** Un lineman es un operario o un jefe de equipo que va de lineman con otro. */
    public static final List<PersonCategory> LINEMAN_CATEGORIES = List.of(PersonCategory.WORKER, PersonCategory.TEAM_LEADER);
}

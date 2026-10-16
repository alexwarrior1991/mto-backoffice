package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.OrganizationTreeNodeDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.UnitSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.WorkDiscipline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * El arbol del organigrama ya cargado ({@code GET /units/tree}), para lo que el editor de una unidad
 * decide sin llamar: de que unidades puede colgar y que disciplina hereda. Las reglas son copia de las
 * del servicio, solo para no ofrecer lo que va a fallar: un padre activo, de un nivel estrictamente
 * superior y, si ya tiene disciplina, la misma (un proyecto no tiene). Quien decide es el servicio
 * (409 {@code UNI-001}).
 */
final class OrganizationTree {

    private final List<OrganizationTreeNodeDto> roots;
    private final Map<UUID, OrganizationTreeNodeDto> byId = new LinkedHashMap<>();

    OrganizationTree(List<OrganizationTreeNodeDto> roots) {
        this.roots = roots == null ? List.of() : List.copyOf(roots);
        this.roots.forEach(this::index);
    }

    private void index(OrganizationTreeNodeDto node) {
        byId.put(node.id(), node);
        node.children().forEach(this::index);
    }

    List<OrganizationTreeNodeDto> roots() {
        return roots;
    }

    /** Todas las unidades, en el orden del arbol (cada una antes que sus hijas). */
    List<OrganizationTreeNodeDto> all() {
        return Collections.unmodifiableList(new ArrayList<>(byId.values()));
    }

    /**
     * De que unidades puede colgar una de este nivel: activas, de un nivel estrictamente superior y de
     * su disciplina si la tiene. {@code self} (al modificar) no puede colgar de si misma; sus hijas
     * tampoco, pero esas ya son de un nivel inferior.
     */
    List<UnitSummaryDto> parentsFor(OrganizationUnitKind kind, WorkDiscipline discipline, UUID self) {
        if (kind == null) {
            // Sin nivel todavia, cualquier unidad de la que algo pueda colgar: el padre se puede elegir antes que el nivel.
            return byId.values().stream()
                    .filter(node -> node.isActive() && node.kind() != OrganizationUnitKind.TEAM && node.kind() != OrganizationUnitKind.UNKNOWN)
                    .map(OrganizationTreeNodeDto::summary)
                    .toList();
        }
        return byId.values().stream()
                .filter(node -> node.isActive() && !node.id().equals(self) && kind.canHangFrom(node.kind()))
                .filter(node -> discipline == null || node.discipline() == null || node.discipline() == discipline)
                .map(OrganizationTreeNodeDto::summary)
                .toList();
    }

    /** La disciplina que hereda lo que cuelga de {@code parent}; {@code null} bajo un proyecto o sin padre. */
    WorkDiscipline disciplineUnder(UnitSummaryDto parent) {
        OrganizationTreeNodeDto node = parent == null ? null : byId.get(parent.id());
        return node == null ? null : node.discipline();
    }
}

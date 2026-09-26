package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.maintenance.MaintenanceCatalogClient;
import com.alejandro.mtobackoffice.ui.support.UiErrors;

import java.util.Comparator;
import java.util.List;

/**
 * Los catalogos de mantenimiento que usan los desplegables (equipos y tipos de tarea), pedidos una
 * vez por pantalla y solo si alguien los necesita. Un fallo se notifica y deja la lista vacia.
 */
final class MaintenanceCatalogs {

    private final MaintenanceCatalogClient client;
    private List<TeamDto> teams;
    private List<TaskTypeDto> taskTypes;

    MaintenanceCatalogs(MaintenanceCatalogClient client) {
        this.client = client;
    }

    /** Todos, tambien los retirados, para nombrar lo que ya estaba asignado; por codigo. */
    List<TeamDto> teams() {
        if (teams == null) {
            try {
                teams = client.teams().stream().sorted(Comparator.comparing(TeamDto::code)).toList();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                teams = List.of();
            }
        }
        return teams;
    }

    List<TeamDto> activeTeams() {
        return teams().stream().filter(TeamDto::isActive).toList();
    }

    /** Los activos, en el orden del plan. */
    List<TaskTypeDto> taskTypes() {
        if (taskTypes == null) {
            try {
                taskTypes = client.taskTypes(null, null, null).stream()
                        .filter(type -> !Boolean.FALSE.equals(type.active()))
                        .sorted(Comparator.comparing((TaskTypeDto type) -> type.orderIndex() == null ? Integer.MAX_VALUE : type.orderIndex())
                                .thenComparing(TaskTypeDto::code))
                        .toList();
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                taskTypes = List.of();
            }
        }
        return taskTypes;
    }
}

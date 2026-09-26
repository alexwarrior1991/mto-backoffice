package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.FunctionalGroup;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.List;

/**
 * El catalogo de tipos de tarea del plan de mantenimiento, de solo lectura: lo mantiene el servicio.
 * Sus minutos estandar son los que usa la estimacion de carga de una orden.
 */
@Route(value = MaintenanceRoutes.TASK_TYPES, layout = MainLayout.class)
@PageTitle("Tipos de tarea")
@Menu(title = "Tipos de tarea", order = 67, icon = "vaadin:tasks")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class TaskTypesView extends VerticalLayout {

    private final MaintenanceClients clients;
    private final ComboBox<FunctionalGroup> group = new ComboBox<>("Grupo funcional");
    private final Span count = new Span();
    private final Grid<TaskTypeDto> grid = new Grid<>();

    public TaskTypesView(MaintenanceClients clients) {
        this.clients = clients;
        setSizeFull();
        group.setId("task-types-group");
        group.setItems(FunctionalGroup.selectable());
        group.setItemLabelGenerator(FunctionalGroup::label);
        group.setClearButtonVisible(true);
        group.addValueChangeListener(change -> refresh());
        HorizontalLayout toolbar = new HorizontalLayout(group, count);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("task-types-grid");
        grid.addColumn(TaskTypeDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        grid.addColumn(TaskTypeDto::description).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        grid.addColumn(type -> type.functionalGroup() == null ? "" : type.functionalGroup().label()).setHeader("Grupo").setKey("group")
                .setAutoWidth(true);
        grid.addColumn(type -> type.unit() == null ? "" : type.unit().label()).setHeader("Unidad").setKey("unit").setAutoWidth(true);
        grid.addColumn(type -> Formats.quantity(type.standardMinutesPerUnit())).setHeader("Min/unidad").setKey("minutesPerUnit")
                .setAutoWidth(true);
        grid.addColumn(type -> Formats.quantity(type.fixedMinutes())).setHeader("Min fijos").setKey("fixedMinutes").setAutoWidth(true);
        grid.addColumn(type -> MaintenanceUi.yesNo(type.requiresFullPossession())).setHeader("Posesion total").setKey("fullPossession")
                .setAutoWidth(true);
        grid.addColumn(type -> MaintenanceUi.yesNo(type.diagnostic())).setHeader("Diagnostico").setKey("diagnostic").setAutoWidth(true);
        grid.addColumn(type -> MaintenanceUi.yesNo(type.active())).setHeader("Activo").setKey("active").setAutoWidth(true);
        grid.setSizeFull();

        add(new H2("Tipos de tarea"), toolbar, grid);
        expand(grid);
        refresh();
    }

    private void refresh() {
        try {
            List<TaskTypeDto> types = clients.catalog().taskTypes(group.getValue(), null, null).stream()
                    .sorted(Comparator.comparing((TaskTypeDto type) -> type.orderIndex() == null ? Integer.MAX_VALUE : type.orderIndex())
                            .thenComparing(TaskTypeDto::code))
                    .toList();
            grid.setItems(types);
            count.setText(types.size() + " tipos");
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

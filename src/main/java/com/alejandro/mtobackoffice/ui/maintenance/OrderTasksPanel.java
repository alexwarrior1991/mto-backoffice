package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.function.Supplier;

/**
 * Las tareas de una orden, en su orden. Con {@code maintenance-write}: anadir una tarea (orden no
 * terminada), generar las de un preventivo sobre un tramo (borrador o planificada), y modificar,
 * rellenar el checklist o cancelar las abiertas. Con la orden en curso, completar una abierta en
 * uno de los turnos en curso de su via (iniciarlas es cosa del turno). Cada cambio avisa a la
 * ficha, que repinta el avance.
 */
class OrderTasksPanel extends LazyPanel {

    static final String GRID_ID = "order-tasks-grid";
    static final String ACTIONS_COLUMN = "actions";

    private final Supplier<OrderDto> order;
    private final MaintenanceClients clients;
    private final MaintenanceCatalogs catalogs;
    private final boolean canWrite;
    private final boolean canPickMaterials;
    private final Runnable changed;
    private final Button add = new Button("Anadir tarea", VaadinIcon.PLUS.create());
    private final Button generate = new Button("Generar tareas", VaadinIcon.MAGIC.create());
    private final Grid<TaskDto> grid = new Grid<>();

    OrderTasksPanel(Supplier<OrderDto> order, MaintenanceClients clients, MaintenanceCatalogs catalogs, boolean canWrite,
                    boolean canPickMaterials, Runnable changed) {
        this.order = order;
        this.clients = clients;
        this.catalogs = catalogs;
        this.canWrite = canWrite;
        this.canPickMaterials = canPickMaterials;
        this.changed = changed;
        add.setId("task-add");
        add.addClickListener(click -> new TaskEditorDialog(order.get(), null, clients, catalogs, this::changed).open());
        generate.setId("task-generate");
        generate.addClickListener(click -> new GenerateTasksDialog(order.get(), clients, catalogs, this::changed).open());

        grid.setId(GRID_ID);
        grid.addColumn(TaskDto::sequence).setHeader("#").setKey("sequence").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(TaskDto::description).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        grid.addColumn(task -> task.asset() == null ? "" : task.asset().label()).setHeader("Activo").setKey("asset").setAutoWidth(true);
        grid.addColumn(task -> task.asset() == null ? "" : MaintenanceFormats.kpRange(task.asset().startKp(), task.asset().endKp()))
                .setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(task -> String.join(", ", task.taskTypeCodes())).setHeader("Tipos").setKey("types").setAutoWidth(true);
        grid.addColumn(task -> task.status() == null ? "" : task.status().label()).setHeader("Estado").setKey("status").setAutoWidth(true);
        grid.addColumn(TaskDto::assignedUser).setHeader("Asignada a").setKey("assignedUser").setAutoWidth(true);
        grid.addColumn(task -> Formats.dateTime(task.completedAt())).setHeader("Completada").setKey("completedAt").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);

        HorizontalLayout toolbar = new HorizontalLayout(add, generate);
        add(toolbar, grid);
    }

    private Component rowActions(TaskDto task) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite && task.isOpen()) {
            OrderDto current = order.get();
            actions.add(MaintenanceUi.rowButton("task-edit-" + task.id(), VaadinIcon.EDIT, "Modificar",
                    click -> new TaskEditorDialog(current, task, clients, catalogs, this::changed).open()));
            if (!task.checkItems().isEmpty()) {
                actions.add(MaintenanceUi.rowButton("task-checklist-" + task.id(), VaadinIcon.CHECK_SQUARE_O, "Checklist",
                        click -> CheckItemsDialog.ofTask(current.id(), task, clients, this::changed).open()));
            }
            if (current.status() != null && current.status().canComplete()) {
                // Desde la orden no hay turno: se elige entre los en curso de la via de la tarea.
                Long trackId = task.asset() != null && task.asset().trackId() != null ? task.asset().trackId() : current.trackId();
                actions.add(MaintenanceUi.rowButton("task-complete-" + task.id(), VaadinIcon.CHECK, "Completar",
                        click -> new CompleteTaskDialog(current.id(), trackId, task, null, clients, catalogs, canPickMaterials,
                                this::changed).open()));
            }
            Button cancel = MaintenanceUi.rowButton("task-cancel-" + task.id(), VaadinIcon.CLOSE_CIRCLE, "Cancelar",
                    click -> new ReasonDialog("Cancelar la tarea " + task.sequence(),
                            "La tarea queda cancelada con su motivo en las notas; no se puede reabrir.", "Cancelar la tarea",
                            reason -> cancel(task, reason)).open());
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(cancel);
        }
        return actions;
    }

    private boolean cancel(TaskDto task, String reason) {
        try {
            clients.orders().cancelTask(order.get().id(), task.id(), new ReasonRequest(reason));
            MaintenanceUi.success("Tarea " + task.sequence() + " cancelada");
            changed();
            return true;
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return false;
        }
    }

    private void changed() {
        reload();
        changed.run();
    }

    @Override
    protected void load() {
        OrderDto current = order.get();
        MaintenanceOrderStatus status = current.status();
        add.setVisible(canWrite && status.isOpen());
        generate.setVisible(canWrite && current.type() == MaintenanceOrderType.PREVENTIVE
                && (status == MaintenanceOrderStatus.DRAFT || status == MaintenanceOrderStatus.PLANNED)
                && current.asset() != null && current.asset().type() == CatenaryAssetType.TRACK_SECTION);
        try {
            grid.setItems(clients.orders().tasks(current.id()).stream()
                    .sorted(Comparator.comparing((TaskDto task) -> task.sequence() == null ? Integer.MAX_VALUE : task.sequence()))
                    .toList());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

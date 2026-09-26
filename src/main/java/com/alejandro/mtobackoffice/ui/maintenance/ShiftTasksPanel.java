package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceTaskStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.StartTaskRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Las tareas asignadas a un turno, de todas sus ordenes. Con {@code maintenance-write} y el turno en
 * curso se inician y se completan aqui; el checklist y la cancelacion valen mientras la tarea este
 * abierta. Si la orden no esta en curso o el turno no admite el trabajo, lo dice el servicio. Cada
 * fila abre su orden.
 */
class ShiftTasksPanel extends LazyPanel {

    static final String GRID_ID = "shift-tasks-grid";
    static final String ACTIONS_COLUMN = "actions";

    private final Supplier<ShiftDto> shift;
    private final MaintenanceClients clients;
    private final MaintenanceCatalogs catalogs;
    private final boolean canWrite;
    private final boolean canPickMaterials;
    private final Runnable changed;
    private final Grid<TaskDto> grid = new Grid<>();
    private final Map<UUID, String> orderCodes = new HashMap<>();

    ShiftTasksPanel(Supplier<ShiftDto> shift, MaintenanceClients clients, MaintenanceCatalogs catalogs, boolean canWrite, boolean canPickMaterials,
                    Runnable changed) {
        this.shift = shift;
        this.clients = clients;
        this.catalogs = catalogs;
        this.canWrite = canWrite;
        this.canPickMaterials = canPickMaterials;
        this.changed = changed;
        grid.setId(GRID_ID);
        grid.addColumn(task -> orderCodes.getOrDefault(task.orderId(), "")).setHeader("Orden").setKey("order").setAutoWidth(true);
        grid.addColumn(TaskDto::sequence).setHeader("#").setKey("sequence").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(TaskDto::description).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        grid.addColumn(task -> task.asset() == null ? "" : task.asset().label()).setHeader("Activo").setKey("asset").setAutoWidth(true);
        grid.addColumn(task -> task.asset() == null ? "" : MaintenanceFormats.kpRange(task.asset().startKp(), task.asset().endKp()))
                .setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(task -> String.join(", ", task.taskTypeCodes())).setHeader("Tipos").setKey("types").setAutoWidth(true);
        grid.addColumn(task -> task.status() == null ? "" : task.status().label()).setHeader("Estado").setKey("status").setAutoWidth(true);
        grid.addColumn(task -> Formats.dateTime(task.completedAt())).setHeader("Completada").setKey("completedAt").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        add(grid);
    }

    private Component rowActions(TaskDto task) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(MaintenanceUi.rowButton("shift-task-order-" + task.id(), VaadinIcon.EXTERNAL_LINK, "Abrir la orden",
                click -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(task.orderId()))));
        if (!canWrite || !task.isOpen()) {
            return actions;
        }
        ShiftDto current = shift.get();
        boolean working = current.status() != null && current.status().canClose();
        if (working && task.status() == MaintenanceTaskStatus.PENDING) {
            actions.add(MaintenanceUi.rowButton("shift-task-start-" + task.id(), VaadinIcon.PLAY, "Iniciar", click -> start(task)));
        }
        if (!task.checkItems().isEmpty()) {
            actions.add(MaintenanceUi.rowButton("shift-task-checklist-" + task.id(), VaadinIcon.CHECK_SQUARE_O, "Checklist",
                    click -> CheckItemsDialog.ofTask(task.orderId(), task, clients, this::changed).open()));
        }
        if (working) {
            actions.add(MaintenanceUi.rowButton("shift-task-complete-" + task.id(), VaadinIcon.CHECK, "Completar",
                    click -> new CompleteTaskDialog(task.orderId(), null, task, current, clients, catalogs, canPickMaterials, this::changed).open()));
        }
        Button cancel = MaintenanceUi.rowButton("shift-task-cancel-" + task.id(), VaadinIcon.CLOSE_CIRCLE, "Cancelar",
                click -> new ReasonDialog("Cancelar la tarea " + task.sequence(), "La tarea queda cancelada con su motivo; no se puede reabrir.",
                        "Cancelar la tarea", reason -> cancel(task, reason)).open());
        cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
        actions.add(cancel);
        return actions;
    }

    private void start(TaskDto task) {
        try {
            clients.orders().startTask(task.orderId(), task.id(), new StartTaskRequest(shift.get().id(), null));
            MaintenanceUi.success("Tarea " + task.sequence() + " iniciada");
            changed();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private boolean cancel(TaskDto task, String reason) {
        try {
            clients.orders().cancelTask(task.orderId(), task.id(), new ReasonRequest(reason));
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
        try {
            List<TaskDto> tasks = clients.shifts().tasks(shift.get().id(), null).stream()
                    .sorted(Comparator.comparing(TaskDto::orderId).thenComparing(task -> task.sequence() == null ? Integer.MAX_VALUE : task.sequence()))
                    .toList();
            tasks.stream().map(TaskDto::orderId).distinct().filter(id -> !orderCodes.containsKey(id)).forEach(this::loadOrderCode);
            grid.setItems(tasks);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** El codigo de la orden para la columna: las tareas solo traen su id, y un turno tiene pocas ordenes. */
    private void loadOrderCode(UUID orderId) {
        try {
            OrderDto order = clients.orders().findById(orderId);
            orderCodes.put(orderId, order.code());
        } catch (NotFoundApiException missing) {
            orderCodes.put(orderId, "?");
        }
    }
}

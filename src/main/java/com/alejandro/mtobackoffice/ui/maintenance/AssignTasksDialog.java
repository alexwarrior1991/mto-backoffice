package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceTaskStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Asignar tareas pendientes a un turno: una via del turno, una orden abierta de esa via y sus tareas
 * pendientes. Se asignan una a una ({@code POST} por tarea), porque el servicio comprueba via y
 * posesion de cada una: las que rechaza (409 {@code SHF-001}, o una que ya no esta pendiente) se
 * cuentan en el aviso con su motivo, y las demas quedan asignadas.
 */
public class AssignTasksDialog extends Dialog {

    public static final String ASSIGN_ID = "assign-tasks-confirm";
    static final String TASKS_GRID_ID = "assign-tasks-grid";

    private final ShiftDto shift;
    private final MaintenanceClients clients;
    private final ComboBox<OrderDto> order = new ComboBox<>("Orden");
    private final Grid<TaskDto> tasks = new Grid<>();

    public AssignTasksDialog(ShiftDto shift, MaintenanceClients clients, MaintenanceNames names, Runnable done) {
        this.shift = shift;
        this.clients = clients;
        setHeaderTitle("Asignar tareas a " + shift.code());
        setWidth("min(95vw, 960px)");

        ComboBox<RefItem> track = new ComboBox<>("Via");
        track.setId("assign-tasks-track");
        track.setItems(shift.trackIds().stream().map(names::trackRef).toList());
        track.setItemLabelGenerator(RefItem::label);
        order.setId("assign-tasks-order");
        order.setItemLabelGenerator(candidate -> candidate.code() + " · " + candidate.title()
                + (candidate.status() == null ? "" : " (" + candidate.status().label().toLowerCase() + ")"));
        track.addValueChangeListener(change -> loadOrders(change.getValue()));
        order.addValueChangeListener(change -> loadTasks(change.getValue()));

        tasks.setId(TASKS_GRID_ID);
        tasks.setSelectionMode(Grid.SelectionMode.MULTI);
        tasks.addColumn(TaskDto::sequence).setHeader("#").setAutoWidth(true).setFlexGrow(0);
        tasks.addColumn(TaskDto::description).setHeader("Descripcion").setFlexGrow(1);
        tasks.addColumn(task -> task.asset() == null ? "" : task.asset().label()).setHeader("Activo").setAutoWidth(true);
        tasks.addColumn(task -> String.join(", ", task.taskTypeCodes())).setHeader("Tipos").setAutoWidth(true);
        tasks.addColumn(task -> Objects.equals(task.shiftId(), shift.id()) ? "Ya en este turno" : task.shiftId() == null ? "" : "En otro turno")
                .setHeader("Turno").setAutoWidth(true);
        tasks.setHeight("320px");

        HorizontalLayout pickers = new HorizontalLayout(track, order);
        pickers.setWidthFull();
        pickers.expand(order);
        add(pickers, tasks);
        if (!shift.trackIds().isEmpty()) {
            track.setValue(names.trackRef(shift.trackIds().getFirst()));
        }

        Button assign = new Button("Asignar", click -> assign(done));
        assign.setId(ASSIGN_ID);
        assign.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cerrar", click -> close()), assign);
    }

    private void loadOrders(RefItem track) {
        order.clear();
        if (track == null) {
            order.setItems(List.of());
            return;
        }
        try {
            order.setItems(clients.orders().search(OrderFilter.onTrack(track.id()), 0, 100, List.of("plannedDate,asc")).content().stream()
                    .filter(candidate -> candidate.status() != null && candidate.status().isOpen())
                    .toList());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void loadTasks(OrderDto selected) {
        if (selected == null) {
            tasks.setItems(List.of());
            return;
        }
        try {
            tasks.setItems(clients.orders().tasks(selected.id()).stream()
                    .filter(task -> task.status() == MaintenanceTaskStatus.PENDING)
                    .sorted(Comparator.comparing((TaskDto task) -> task.sequence() == null ? Integer.MAX_VALUE : task.sequence()))
                    .toList());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void assign(Runnable done) {
        List<TaskDto> selected = tasks.getSelectedItems().stream()
                .sorted(Comparator.comparing((TaskDto task) -> task.sequence() == null ? Integer.MAX_VALUE : task.sequence()))
                .toList();
        if (selected.isEmpty()) {
            return;
        }
        int assigned = 0;
        List<String> refused = new ArrayList<>();
        for (TaskDto task : selected) {
            try {
                clients.shifts().assignTask(shift.id(), task.id());
                assigned++;
            } catch (BackofficeApiException failure) {
                refused.add("tarea " + task.sequence() + " (" + UiErrors.message(failure) + ")");
            }
        }
        String text = "Asignadas: " + assigned + "." + (refused.isEmpty() ? "" : " Rechazadas: " + String.join("; ", refused));
        Notification notification = Notification.show(text, refused.isEmpty() ? 3000 : 10000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(refused.isEmpty() ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_WARNING);
        loadTasks(order.getValue());
        done.run();
    }
}

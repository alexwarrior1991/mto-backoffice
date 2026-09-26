package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaterialUsageDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.StockSyncStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Las lineas de material de una orden y como van con mto-stock (el error, si fallo, en el tooltip
 * del estado). Con la orden sin terminar: anadir (write + {@code stock-read}), modificar (write) y
 * quitar (delete; ni consumidas), que libera antes la reserva. Sincronizar (write) reintenta con el
 * almacen una linea fallida, o una sin pedir fuera de borrador; si el almacen sigue caido, el
 * servicio responde 503 {@code STK-503} y la linea se queda como estaba.
 */
class OrderMaterialsPanel extends LazyPanel {

    static final String GRID_ID = "order-materials-grid";
    static final String ACTIONS_COLUMN = "actions";

    private final Supplier<OrderDto> order;
    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final boolean canWrite;
    private final boolean canDelete;
    private final Button add = new Button("Anadir material", VaadinIcon.PLUS.create());
    private final Paragraph withoutStock = new Paragraph("Anadir materiales pide leer el almacen (stock-read).");
    private final Grid<MaterialUsageDto> grid = new Grid<>();
    private final Map<UUID, TaskDto> tasks = new HashMap<>();

    OrderMaterialsPanel(Supplier<OrderDto> order, MaintenanceClients clients, MaintenanceNames names, boolean canWrite, boolean canDelete) {
        this.order = order;
        this.clients = clients;
        this.names = names;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
        add.setId("material-add");
        add.addClickListener(click -> new MaterialUsageDialog(order.get(), null, openTasks(), clients, this::reload).open());
        grid.setId(GRID_ID);
        grid.addColumn(MaterialUsageDto::materialLabel).setHeader("Material").setKey("material").setFlexGrow(1);
        grid.addColumn(line -> names.warehouseName(line.warehouseId())).setHeader("Almacen").setKey("warehouse").setAutoWidth(true);
        grid.addColumn(line -> quantity(line.plannedQuantity(), line.unit())).setHeader("Previsto").setKey("planned").setAutoWidth(true);
        grid.addColumn(line -> quantity(line.consumedQuantity(), line.unit())).setHeader("Consumido").setKey("consumed").setAutoWidth(true);
        grid.addColumn(line -> line.taskId() == null || !tasks.containsKey(line.taskId()) ? "" : "Tarea " + tasks.get(line.taskId()).sequence())
                .setHeader("Tarea").setKey("task").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::status)).setHeader("Stock").setKey("status").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setAllRowsVisible(true);
        add(new HorizontalLayout(add, withoutStock), grid);
    }

    private static String quantity(BigDecimal value, String unit) {
        return value == null ? "" : Formats.quantity(value) + (unit == null ? "" : " " + unit);
    }

    private Span status(MaterialUsageDto line) {
        Span status = new Span(line.stockSyncStatus() == null ? "" : line.stockSyncStatus().label());
        status.setId("material-status-" + line.id());
        if (line.stockSyncError() != null && !line.stockSyncError().isBlank()) {
            status.setTitle(line.stockSyncError());
            status.getElement().getThemeList().add("badge error");
        }
        return status;
    }

    private Component rowActions(MaterialUsageDto line) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        OrderDto current = order.get();
        MaintenanceOrderStatus orderStatus = current.status();
        boolean orderOpen = orderStatus != null && orderStatus.isOpen();
        StockSyncStatus status = line.stockSyncStatus();
        if (canWrite && orderOpen && status != StockSyncStatus.CONSUMED) {
            actions.add(MaintenanceUi.rowButton("material-edit-" + line.id(), VaadinIcon.EDIT, "Modificar",
                    click -> new MaterialUsageDialog(current, line, openTasks(), clients, this::reload).open()));
        }
        boolean retryable = status == StockSyncStatus.FAILED || (status == StockSyncStatus.NOT_REQUESTED && orderStatus != MaintenanceOrderStatus.DRAFT);
        if (canWrite && retryable) {
            actions.add(MaintenanceUi.rowButton("material-sync-" + line.id(), VaadinIcon.REFRESH, "Sincronizar con el almacen", click -> sync(line)));
        }
        if (canDelete && orderOpen && status != StockSyncStatus.CONSUMED) {
            Button remove = MaintenanceUi.rowButton("material-remove-" + line.id(), VaadinIcon.TRASH, "Quitar", click -> MaintenanceUi.confirm(
                    "Quitar " + line.materialLabel(),
                    line.isReserved() ? "Se libera antes su reserva en el almacen, y la linea desaparece (queda en su historial)."
                            : "La linea desaparece (queda en su historial).",
                    "Quitar", () -> remove(line)));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(remove);
        }
        return actions;
    }

    private void sync(MaterialUsageDto line) {
        try {
            MaterialUsageDto synced = clients.orders().syncMaterial(order.get().id(), line.id());
            MaintenanceUi.success(line.materialCode() + ": " + (synced.stockSyncStatus() == null ? "" : synced.stockSyncStatus().label().toLowerCase()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    private void remove(MaterialUsageDto line) {
        try {
            clients.orders().removeMaterial(order.get().id(), line.id());
            MaintenanceUi.success("Quitado " + line.materialCode());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    private List<TaskDto> openTasks() {
        return tasks.values().stream().filter(TaskDto::isOpen)
                .sorted(Comparator.comparing((TaskDto task) -> task.sequence() == null ? Integer.MAX_VALUE : task.sequence()))
                .toList();
    }

    @Override
    protected void load() {
        OrderDto current = order.get();
        boolean orderOpen = current.status() != null && current.status().isOpen();
        add.setVisible(canWrite && orderOpen && names.readsStock());
        withoutStock.setVisible(canWrite && orderOpen && !names.readsStock());
        try {
            tasks.clear();
            clients.orders().tasks(current.id()).forEach(task -> tasks.put(task.id(), task));
            grid.setItems(clients.orders().materials(current.id()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

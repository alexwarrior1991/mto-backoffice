package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;
import java.util.function.Supplier;

/**
 * Las inspecciones hechas desde una orden de inspeccion; con {@code maintenance-write} se da de alta
 * una sobre su activo (el servicio no completa una orden de inspeccion sin ninguna). Una fila abre
 * su ficha.
 */
class OrderInspectionsPanel extends LazyPanel {

    static final String GRID_ID = "order-inspections-grid";

    private final Supplier<OrderDto> order;
    private final MaintenanceClients clients;
    private final boolean canWrite;
    private final Runnable changed;
    private final Button create = new Button("Nueva inspeccion", VaadinIcon.PLUS.create());
    private final Grid<InspectionDto> grid = new Grid<>();

    OrderInspectionsPanel(Supplier<OrderDto> order, MaintenanceClients clients, boolean canWrite, Runnable changed) {
        this.order = order;
        this.clients = clients;
        this.canWrite = canWrite;
        this.changed = changed;
        create.setId("order-inspection-create");
        create.addClickListener(click -> new InspectionEditorDialog(null, order.get(), clients, created -> {
            reload();
            changed.run();
        }).open());
        grid.setId(GRID_ID);
        grid.addColumn(InspectionDto::code).setHeader("Codigo").setAutoWidth(true);
        grid.addColumn(inspection -> MaintenanceFormats.date(inspection.inspectionDate())).setHeader("Fecha").setAutoWidth(true);
        grid.addColumn(inspection -> inspection.asset() == null ? "" : inspection.asset().label()).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(inspection -> inspection.result() == null ? "" : inspection.result().label()).setHeader("Resultado").setAutoWidth(true);
        grid.addColumn(InspectionDto::inspector).setHeader("Inspector").setFlexGrow(1);
        grid.setAllRowsVisible(true);
        grid.addItemClickListener(click -> UI.getCurrent().navigate(InspectionDetailView.class,
                InspectionDetailView.parametersOf(click.getItem().id())));
        add(create, grid);
    }

    @Override
    protected void load() {
        OrderDto current = order.get();
        create.setVisible(canWrite && current.type() == MaintenanceOrderType.INSPECTION && current.status() != null && current.status().isOpen());
        try {
            grid.setItems(clients.inspections().search(InspectionFilter.ofOrder(current.id()), 0, 100, List.of("inspectionDate,desc")).content());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

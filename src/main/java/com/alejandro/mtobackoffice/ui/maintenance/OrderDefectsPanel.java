package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;
import java.util.function.Supplier;

/** Los defectos vinculados a una orden; con {@code maintenance-write} se da de alta uno ya vinculado. Una fila abre su ficha. */
class OrderDefectsPanel extends LazyPanel {

    static final String GRID_ID = "order-defects-grid";

    private final Supplier<OrderDto> order;
    private final MaintenanceClients clients;
    private final boolean canWrite;
    private final Button create = new Button("Nuevo defecto", VaadinIcon.PLUS.create());
    private final Grid<DefectDto> grid = new Grid<>();

    OrderDefectsPanel(Supplier<OrderDto> order, MaintenanceClients clients, boolean canWrite) {
        this.order = order;
        this.clients = clients;
        this.canWrite = canWrite;
        create.setId("order-defect-create");
        create.addClickListener(click -> new DefectEditorDialog(null, order.get(), clients, created -> reload()).open());
        grid.setId(GRID_ID);
        grid.addColumn(DefectDto::code).setHeader("Codigo").setAutoWidth(true);
        grid.addColumn(defect -> Formats.dateTime(defect.detectedAt())).setHeader("Detectado").setAutoWidth(true);
        grid.addColumn(defect -> defect.asset() == null ? "" : defect.asset().label()).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(defect -> defect.severity() == null ? "" : defect.severity().label()).setHeader("Gravedad").setAutoWidth(true);
        grid.addColumn(defect -> defect.status() == null ? "" : defect.status().label()).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(DefectDto::description).setHeader("Descripcion").setFlexGrow(1);
        grid.setAllRowsVisible(true);
        grid.addItemClickListener(click -> UI.getCurrent().navigate(DefectDetailView.class, DefectDetailView.parametersOf(click.getItem().id())));
        add(create, grid);
    }

    @Override
    protected void load() {
        OrderDto current = order.get();
        create.setVisible(canWrite && current.status() != null && current.status().isOpen());
        try {
            grid.setItems(clients.defects().search(DefectFilter.ofOrder(current.id()), 0, 100, List.of("detectedAt,desc")).content());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

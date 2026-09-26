package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceTaskStatus;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;

import java.util.UUID;
import java.util.function.Supplier;

/** Los perfiles de las tareas del turno, en el orden fisico de la via; por defecto, los ya revisados (tarea completada). */
class ShiftProfilesPanel extends LazyPanel {

    static final String GRID_ID = "shift-profiles-grid";

    private final Supplier<UUID> shiftId;
    private final MaintenanceClients clients;
    private final ComboBox<MaintenanceTaskStatus> status = new ComboBox<>("Tareas");
    private final Grid<AssetSummaryDto> grid = new Grid<>();

    ShiftProfilesPanel(Supplier<UUID> shiftId, MaintenanceClients clients) {
        this.shiftId = shiftId;
        this.clients = clients;
        status.setId("shift-profiles-status");
        status.setItems(MaintenanceTaskStatus.selectable());
        status.setItemLabelGenerator(MaintenanceTaskStatus::label);
        status.setClearButtonVisible(true);
        status.setValue(MaintenanceTaskStatus.COMPLETED);
        status.addValueChangeListener(change -> reload());
        grid.setId(GRID_ID);
        grid.addColumn(AssetSummaryDto::name).setHeader("Perfil").setKey("name").setAutoWidth(true);
        grid.addColumn(AssetSummaryDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        grid.addColumn(profile -> MaintenanceFormats.kp(profile.startKp())).setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(AssetSummaryDto::sectioning).setHeader("Seccionamiento").setKey("sectioning").setFlexGrow(1);
        grid.setAllRowsVisible(true);
        add(status, grid);
    }

    @Override
    protected void load() {
        try {
            grid.setItems(clients.shifts().profiles(shiftId.get(), status.getValue()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.ReportFormat;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportRowDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Downloads;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * El parte diario del turno, como lo compone el servicio: sus recuentos y una fila por tarea
 * trabajada, con los enlaces Excel y PDF del mismo parte.
 */
class ShiftReportPanel extends LazyPanel {

    private final Supplier<UUID> shiftId;
    private final MaintenanceClients clients;
    private final Div summary = new Div();
    private final HorizontalLayout downloads = new HorizontalLayout();
    private final Grid<ShiftReportRowDto> grid = new Grid<>();

    ShiftReportPanel(Supplier<UUID> shiftId, MaintenanceClients clients) {
        this.shiftId = shiftId;
        this.clients = clients;
        summary.setId("shift-report-summary");
        downloads.setId("shift-report-downloads");
        grid.setId("shift-report-grid");
        grid.addColumn(ShiftReportRowDto::number).setHeader("#").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(ShiftReportRowDto::orderCode).setHeader("Orden").setAutoWidth(true);
        grid.addColumn(row -> row.profileName() == null ? row.profileCode() : row.profileName()).setHeader("Perfil").setAutoWidth(true);
        grid.addColumn(row -> MaintenanceFormats.kp(row.kp())).setHeader("KP").setAutoWidth(true);
        grid.addColumn(row -> String.join(", ", row.taskTypeCodes())).setHeader("Tipos").setAutoWidth(true);
        grid.addColumn(ShiftReportRowDto::worksPerformed).setHeader("Trabajos").setFlexGrow(1);
        grid.addColumn(ShiftReportRowDto::defectsFound).setHeader("Defectos").setAutoWidth(true);
        grid.addColumn(row -> String.join(", ", row.materials())).setHeader("Materiales").setAutoWidth(true);
        grid.addColumn(row -> row.status() == null ? "" : row.status().label()).setHeader("Estado").setAutoWidth(true);
        grid.setAllRowsVisible(true);
        add(summary, downloads, grid);
    }

    @Override
    protected void load() {
        UUID id = shiftId.get();
        try {
            ShiftReportDto report = clients.reports().shiftReport(id);
            summary.setText("Tareas: " + report.tasksCompleted() + " completadas, " + report.tasksPending() + " pendientes · Perfiles revisados: "
                    + report.profilesReviewed() + " · Defectos: " + report.defectsFound() + " encontrados, " + report.defectsResolved()
                    + " resueltos");
            grid.setItems(report.rows());
            downloads.removeAll();
            String code = report.shift() == null ? "turno" : report.shift().code();
            for (ReportFormat format : ReportFormat.values()) {
                downloads.add(Downloads.link("shift-report-" + format.parameter(), format.label(), "parte-" + code + "." + format.parameter(),
                        () -> clients.reports().shiftReportFile(id, format.parameter())));
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

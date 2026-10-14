package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.ReportFormat;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportInspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftReportRowDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Downloads;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.RowActions;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * El parte diario del turno, como lo compone el servicio: sus recuentos, una fila por tarea trabajada
 * (un perfil o un aislador), las inspecciones registradas en el turno, cada una con su boton para
 * abrirla, y los enlaces Excel y PDF del mismo parte. Aqui no se cuenta nada: el numero de
 * inspecciones tambien es del servicio.
 */
class ShiftReportPanel extends LazyPanel {

    static final String INSPECTIONS_GRID_ID = "shift-report-inspections";

    private final Supplier<UUID> shiftId;
    private final MaintenanceClients clients;
    private final Div summary = new Div();
    private final HorizontalLayout downloads = new HorizontalLayout();
    private final Grid<ShiftReportRowDto> grid = new Grid<>();
    private final Grid<ShiftReportInspectionDto> inspections = new Grid<>();

    ShiftReportPanel(Supplier<UUID> shiftId, MaintenanceClients clients) {
        this.shiftId = shiftId;
        this.clients = clients;
        summary.setId("shift-report-summary");
        downloads.setId("shift-report-downloads");
        grid.setId("shift-report-grid");
        grid.addColumn(ShiftReportRowDto::number).setHeader("#").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(ShiftReportRowDto::orderCode).setHeader("Orden").setAutoWidth(true);
        grid.addColumn(row -> row.profileName() == null ? row.profileCode() : row.profileName()).setHeader("Activo").setAutoWidth(true);
        grid.addColumn(row -> MaintenanceFormats.kp(row.kp())).setHeader("KP").setAutoWidth(true);
        grid.addColumn(row -> String.join(", ", row.taskTypeCodes())).setHeader("Tipos").setAutoWidth(true);
        grid.addColumn(ShiftReportRowDto::worksPerformed).setHeader("Trabajos").setFlexGrow(1);
        grid.addColumn(ShiftReportRowDto::defectsFound).setHeader("Defectos").setAutoWidth(true);
        grid.addColumn(row -> String.join(", ", row.materials())).setHeader("Materiales").setAutoWidth(true);
        grid.addColumn(row -> row.status() == null ? "" : row.status().label()).setHeader("Estado").setAutoWidth(true);
        grid.setAllRowsVisible(true);

        inspections.setId(INSPECTIONS_GRID_ID);
        inspections.addColumn(ShiftReportInspectionDto::code).setHeader("Codigo").setAutoWidth(true);
        inspections.addColumn(row -> MaintenanceFormats.date(row.inspectionDate())).setHeader("Fecha").setAutoWidth(true);
        inspections.addColumn(ShiftReportInspectionDto::assetLabel).setHeader("Activo").setAutoWidth(true);
        inspections.addColumn(row -> MaintenanceFormats.kp(row.kp())).setHeader("KP").setAutoWidth(true);
        inspections.addColumn(row -> row.inspectionKind() == null ? "" : row.inspectionKind().label()).setHeader("Tipo").setAutoWidth(true);
        inspections.addColumn(row -> row.result() == null ? "" : row.result().label()).setHeader("Resultado").setAutoWidth(true);
        inspections.addColumn(ShiftReportInspectionDto::inspector).setHeader("Inspector").setAutoWidth(true);
        inspections.addColumn(ShiftReportInspectionDto::detectedDefects).setHeader("Observado").setFlexGrow(1);
        inspections.addColumn(row -> Stream.of(row.generatedDefectCode(), row.generatedOrderCode()).filter(code -> code != null)
                .collect(Collectors.joining(", "))).setHeader("Genero").setAutoWidth(true);
        inspections.setAllRowsVisible(true);
        inspections.setEmptyStateText("No se registro ninguna inspeccion en el turno.");
        RowActions.openButton(inspections, ShiftReportInspectionDto::inspectionId, row -> "Abrir " + row.code(),
                row -> UI.getCurrent().navigate(InspectionDetailView.class, InspectionDetailView.parametersOf(row.inspectionId())));
        add(summary, downloads, grid, new H4("Inspecciones"), inspections);
    }

    @Override
    protected void load() {
        UUID id = shiftId.get();
        try {
            ShiftReportDto report = clients.reports().shiftReport(id);
            summary.setText("Tareas: " + report.tasksCompleted() + " completadas, " + report.tasksPending() + " pendientes · Perfiles revisados: "
                    + report.profilesReviewed() + " · Defectos: " + report.defectsFound() + " encontrados, " + report.defectsResolved()
                    + " resueltos" + (report.inspectionsRecorded() == null ? "" : " · Inspecciones: " + report.inspectionsRecorded()));
            grid.setItems(report.rows());
            inspections.setItems(report.inspections());
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

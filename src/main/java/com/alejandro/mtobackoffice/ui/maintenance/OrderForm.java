package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderType;
import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenancePriority;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;

import java.time.LocalDate;

/**
 * Modelo mutable del editor de una orden, con las propiedades llamadas como los campos de la
 * peticion ({@code assetId} y {@code teamId} guardan la opcion elegida) para que los errores del
 * servicio caigan en su campo.
 */
public class OrderForm {

    private String title = "";
    private String description = "";
    private MaintenanceOrderType type = MaintenanceOrderType.PREVENTIVE;
    private MaintenancePriority priority = MaintenancePriority.MEDIUM;
    private AssetSummaryDto assetId;
    private LocalDate plannedDate;
    private TeamSummaryDto teamId;
    private String assignedUser = "";
    private String closingNotes = "";
    private ProjectSummaryDto stockProjectId;

    /** @param names para el proyecto de almacen, que la orden guarda como id */
    public static OrderForm of(OrderDto dto, MaintenanceNames names) {
        OrderForm form = new OrderForm();
        if (dto != null) {
            form.setTitle(orEmpty(dto.title()));
            form.setDescription(orEmpty(dto.description()));
            form.setType(dto.type());
            form.setPriority(dto.priority());
            form.setAssetId(dto.asset());
            form.setPlannedDate(dto.plannedDate());
            form.setTeamId(dto.team());
            form.setAssignedUser(orEmpty(dto.assignedUser()));
            form.setClosingNotes(orEmpty(dto.closingNotes()));
            form.setStockProjectId(names.projectRef(dto.stockProjectId()));
        }
        return form;
    }

    public OrderRequest toRequest() {
        return new OrderRequest(title.trim(), nullIfBlank(description), type, priority, assetId == null ? null : assetId.id(), plannedDate,
                teamId == null ? null : teamId.id(), nullIfBlank(assignedUser), stockProjectId == null ? null : stockProjectId.id());
    }

    /**
     * Lo que cambio, lo vaciado y la version leida. Con {@code full} (borrador o planificada) titulo,
     * prevista, equipo, persona y proyecto de almacen; si no, solo descripcion, prioridad y notas de
     * cierre, que es lo unico que el servicio admite (y vacia) despues.
     */
    public MergePatch<OrderUpdateRequest> toPatch(OrderDto original, boolean full) {
        Changes changes = new Changes();
        OrderUpdateRequest values = new OrderUpdateRequest(
                full ? changes.text("title", title, original.title()) : null,
                changes.text("description", description, original.description()),
                priority == original.priority() ? null : priority,
                full ? changes.value("plannedDate", plannedDate, original.plannedDate()) : null,
                full ? changes.value("teamId", teamId == null ? null : teamId.id(), original.team() == null ? null : original.team().id()) : null,
                full ? changes.text("assignedUser", assignedUser, original.assignedUser()) : null,
                full ? null : changes.text("closingNotes", closingNotes, original.closingNotes()),
                null, null, null, null, null,
                full ? changes.value("stockProjectId", stockProjectId == null ? null : stockProjectId.id(), original.stockProjectId()) : null);
        return changes.patch(values, original.version());
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MaintenanceOrderType getType() {
        return type;
    }

    public void setType(MaintenanceOrderType type) {
        this.type = type;
    }

    public MaintenancePriority getPriority() {
        return priority;
    }

    public void setPriority(MaintenancePriority priority) {
        this.priority = priority;
    }

    public AssetSummaryDto getAssetId() {
        return assetId;
    }

    public void setAssetId(AssetSummaryDto assetId) {
        this.assetId = assetId;
    }

    public LocalDate getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(LocalDate plannedDate) {
        this.plannedDate = plannedDate;
    }

    public TeamSummaryDto getTeamId() {
        return teamId;
    }

    public void setTeamId(TeamSummaryDto teamId) {
        this.teamId = teamId;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public ProjectSummaryDto getStockProjectId() {
        return stockProjectId;
    }

    public void setStockProjectId(ProjectSummaryDto stockProjectId) {
        this.stockProjectId = stockProjectId;
    }

    public String getClosingNotes() {
        return closingNotes;
    }

    public void setClosingNotes(String closingNotes) {
        this.closingNotes = closingNotes;
    }
}

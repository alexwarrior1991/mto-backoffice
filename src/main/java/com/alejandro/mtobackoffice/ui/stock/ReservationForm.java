package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo mutable del editor de reservas; las propiedades se llaman como los campos de la peticion
 * aunque guarden el resumen elegido, para que {@code validationErrors[].field} caiga en su campo.
 */
public class ReservationForm {

    private MaterialSummaryDto materialId;
    private WarehouseSummaryDto warehouseId;
    private ProjectSummaryDto projectId;
    private BigDecimal quantity;
    private LocalDateTime reservedAt;

    public static ReservationForm of(ReservationDto existing) {
        ReservationForm form = new ReservationForm();
        if (existing != null) {
            form.setMaterialId(existing.material());
            form.setWarehouseId(existing.warehouse());
            form.setProjectId(existing.project());
            form.setQuantity(existing.quantity());
        }
        return form;
    }

    public ReservationRequest toCreateRequest() {
        return new ReservationRequest(materialId.id(), warehouseId.id(), projectId.id(), quantity, StockFormats.toInstant(reservedAt));
    }

    /** La modificacion no lleva material: el servicio no lo cambia. */
    public ReservationUpdateRequest toUpdateRequest() {
        return new ReservationUpdateRequest(warehouseId.id(), projectId.id(), quantity);
    }

    public MaterialSummaryDto getMaterialId() {
        return materialId;
    }

    public void setMaterialId(MaterialSummaryDto materialId) {
        this.materialId = materialId;
    }

    public WarehouseSummaryDto getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(WarehouseSummaryDto warehouseId) {
        this.warehouseId = warehouseId;
    }

    public ProjectSummaryDto getProjectId() {
        return projectId;
    }

    public void setProjectId(ProjectSummaryDto projectId) {
        this.projectId = projectId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }
}

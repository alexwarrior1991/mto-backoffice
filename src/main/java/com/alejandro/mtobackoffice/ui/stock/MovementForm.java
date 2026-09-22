package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentDirection;
import com.alejandro.mtobackoffice.client.dto.stock.AdjustmentRequest;
import com.alejandro.mtobackoffice.client.dto.stock.EntryRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.OutputRequest;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.SupplierSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.TransferRequest;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo mutable de un movimiento en el editor. Las propiedades se llaman como los campos de las
 * peticiones del servicio ({@code materialId}, {@code warehouseId}...) aunque guarden el resumen
 * elegido y no el id: asi un {@code validationErrors[].field} cae en su desplegable.
 */
public class MovementForm {

    private MaterialSummaryDto materialId;
    private WarehouseSummaryDto warehouseId;
    private WarehouseSummaryDto targetWarehouseId;
    private SupplierSummaryDto supplierId;
    private ProjectSummaryDto projectId;
    private UUID reservationId;
    private AdjustmentDirection direction = AdjustmentDirection.POSITIVE;
    private BigDecimal quantity;
    private LocalDateTime occurredAt;
    private String externalReference = "";
    private String notes = "";

    public EntryRequest toEntry() {
        return new EntryRequest(materialId.id(), warehouseId.id(), supplierId == null ? null : supplierId.id(), quantity,
                StockFormats.toInstant(occurredAt), blankToNull(externalReference), blankToNull(notes));
    }

    public OutputRequest toOutput() {
        return new OutputRequest(materialId.id(), warehouseId.id(), projectId == null ? null : projectId.id(), reservationId, quantity,
                StockFormats.toInstant(occurredAt), blankToNull(externalReference), blankToNull(notes));
    }

    public TransferRequest toTransfer() {
        return new TransferRequest(materialId.id(), warehouseId.id(), targetWarehouseId.id(), quantity,
                StockFormats.toInstant(occurredAt), blankToNull(externalReference), blankToNull(notes));
    }

    public AdjustmentRequest toAdjustment() {
        return new AdjustmentRequest(materialId.id(), warehouseId.id(), direction, quantity,
                StockFormats.toInstant(occurredAt), blankToNull(externalReference), blankToNull(notes));
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
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

    public WarehouseSummaryDto getTargetWarehouseId() {
        return targetWarehouseId;
    }

    public void setTargetWarehouseId(WarehouseSummaryDto targetWarehouseId) {
        this.targetWarehouseId = targetWarehouseId;
    }

    public SupplierSummaryDto getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(SupplierSummaryDto supplierId) {
        this.supplierId = supplierId;
    }

    public ProjectSummaryDto getProjectId() {
        return projectId;
    }

    public void setProjectId(ProjectSummaryDto projectId) {
        this.projectId = projectId;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public AdjustmentDirection getDirection() {
        return direction;
    }

    public void setDirection(AdjustmentDirection direction) {
        this.direction = direction;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

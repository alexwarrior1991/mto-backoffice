package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectSeverity;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.ui.support.Formats;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** Modelo mutable del editor de un defecto, con las propiedades llamadas como los campos de la peticion. */
public class DefectForm {

    private AssetSummaryDto assetId;
    private DefectSeverity severity = DefectSeverity.MEDIUM;
    private String description = "";
    private String technicalNotes = "";
    private LocalDateTime detectedAt;
    private BigDecimal startKp;
    private BigDecimal endKp;
    private String correctionType = "";
    private String partsReplaced = "";
    private LocalDate repairPlannedDate;

    public static DefectForm of(DefectDto dto) {
        DefectForm form = new DefectForm();
        if (dto != null) {
            form.setAssetId(dto.asset());
            form.setSeverity(dto.severity());
            form.setDescription(orEmpty(dto.description()));
            form.setTechnicalNotes(orEmpty(dto.technicalNotes()));
            form.setDetectedAt(Formats.toLocalDateTime(dto.detectedAt()));
            form.setStartKp(dto.startKp());
            form.setEndKp(dto.endKp());
            form.setCorrectionType(orEmpty(dto.correctionType()));
            form.setPartsReplaced(orEmpty(dto.partsReplaced()));
            form.setRepairPlannedDate(dto.repairPlannedDate());
        }
        return form;
    }

    /** @param orderId la orden en cuya ficha se da de alta, o {@code null} */
    public DefectRequest toRequest(UUID orderId) {
        return new DefectRequest(assetId == null ? null : assetId.id(), severity, description.trim(), nullIfBlank(technicalNotes),
                Formats.toInstant(detectedAt), null, orderId, startKp, endKp, nullIfBlank(correctionType), nullIfBlank(partsReplaced),
                repairPlannedDate, null);
    }

    /** Lo que cambio, lo vaciado y la version leida. */
    public MergePatch<DefectUpdateRequest> toPatch(DefectDto original) {
        Changes changes = new Changes();
        DefectUpdateRequest values = new DefectUpdateRequest(
                severity == original.severity() ? null : severity,
                changes.text("description", description, original.description()),
                changes.text("technicalNotes", technicalNotes, original.technicalNotes()),
                changes.text("correctionType", correctionType, original.correctionType()),
                changes.text("partsReplaced", partsReplaced, original.partsReplaced()),
                changes.value("repairPlannedDate", repairPlannedDate, original.repairPlannedDate()),
                null);
        return changes.patch(values, original.version());
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public AssetSummaryDto getAssetId() {
        return assetId;
    }

    public void setAssetId(AssetSummaryDto assetId) {
        this.assetId = assetId;
    }

    public DefectSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DefectSeverity severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnicalNotes() {
        return technicalNotes;
    }

    public void setTechnicalNotes(String technicalNotes) {
        this.technicalNotes = technicalNotes;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }

    public BigDecimal getStartKp() {
        return startKp;
    }

    public void setStartKp(BigDecimal startKp) {
        this.startKp = startKp;
    }

    public BigDecimal getEndKp() {
        return endKp;
    }

    public void setEndKp(BigDecimal endKp) {
        this.endKp = endKp;
    }

    public String getCorrectionType() {
        return correctionType;
    }

    public void setCorrectionType(String correctionType) {
        this.correctionType = correctionType;
    }

    public String getPartsReplaced() {
        return partsReplaced;
    }

    public void setPartsReplaced(String partsReplaced) {
        this.partsReplaced = partsReplaced;
    }

    public LocalDate getRepairPlannedDate() {
        return repairPlannedDate;
    }

    public void setRepairPlannedDate(LocalDate repairPlannedDate) {
        this.repairPlannedDate = repairPlannedDate;
    }
}

package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionKind;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionResult;
import com.alejandro.mtobackoffice.client.dto.maintenance.InspectionUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/** Modelo mutable del editor de una inspeccion, con las propiedades llamadas como los campos de la peticion. */
public class InspectionForm {

    private AssetSummaryDto assetId;
    private LocalDate inspectionDate = LocalDate.now();
    private String inspector = "";
    private InspectionKind inspectionKind = InspectionKind.VISUAL;
    private InspectionResult result = InspectionResult.OK;
    private String description = "";
    private String detectedDefects = "";
    private String recommendedActions = "";
    private BigDecimal kp;

    public static InspectionForm of(InspectionDto dto) {
        InspectionForm form = new InspectionForm();
        if (dto != null) {
            form.setAssetId(dto.asset());
            form.setInspectionDate(dto.inspectionDate());
            form.setInspector(orEmpty(dto.inspector()));
            form.setInspectionKind(dto.inspectionKind());
            form.setResult(dto.result());
            form.setDescription(orEmpty(dto.description()));
            form.setDetectedDefects(orEmpty(dto.detectedDefects()));
            form.setRecommendedActions(orEmpty(dto.recommendedActions()));
            form.setKp(dto.kp());
        }
        return form;
    }

    /** @param originOrderId la orden de inspeccion desde la que se da de alta, o {@code null} */
    public InspectionRequest toRequest(UUID originOrderId) {
        return new InspectionRequest(assetId == null ? null : assetId.id(), inspectionDate, nullIfBlank(inspector), inspectionKind, result,
                nullIfBlank(description), nullIfBlank(detectedDefects), nullIfBlank(recommendedActions), kp, originOrderId, null);
    }

    public InspectionUpdateRequest toUpdateRequest(InspectionDto original) {
        return new InspectionUpdateRequest(
                Objects.equals(inspectionDate, original.inspectionDate()) ? null : inspectionDate,
                changed(inspector, original.inspector()),
                inspectionKind == original.inspectionKind() ? null : inspectionKind,
                result == original.result() ? null : result,
                changed(description, original.description()),
                changed(detectedDefects, original.detectedDefects()),
                changed(recommendedActions, original.recommendedActions()),
                kp == null ? null : original.kp() != null && kp.compareTo(original.kp()) == 0 ? null : kp);
    }

    private static String changed(String value, String original) {
        String current = value == null ? "" : value.trim();
        return current.equals(orEmpty(original)) ? null : current;
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

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public InspectionKind getInspectionKind() {
        return inspectionKind;
    }

    public void setInspectionKind(InspectionKind inspectionKind) {
        this.inspectionKind = inspectionKind;
    }

    public InspectionResult getResult() {
        return result;
    }

    public void setResult(InspectionResult result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetectedDefects() {
        return detectedDefects;
    }

    public void setDetectedDefects(String detectedDefects) {
        this.detectedDefects = detectedDefects;
    }

    public String getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(String recommendedActions) {
        this.recommendedActions = recommendedActions;
    }

    public BigDecimal getKp() {
        return kp;
    }

    public void setKp(BigDecimal kp) {
        this.kp = kp;
    }
}

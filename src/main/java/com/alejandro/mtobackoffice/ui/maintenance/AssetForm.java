package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.MergePatch;
import com.alejandro.mtobackoffice.client.dto.maintenance.TrackKind;
import com.alejandro.mtobackoffice.ui.master.RefItem;

import java.math.BigDecimal;

/**
 * Modelo mutable del editor de un activo. Sus propiedades se llaman como los campos de la peticion
 * (aunque {@code trackId} guarde la opcion elegida, con su nombre) para que un
 * {@code validationErrors[].field} del servicio caiga en su campo.
 */
public class AssetForm {

    private String code = "";
    private String name = "";
    private String description = "";
    private RefItem executionPackageId;
    private RefItem trackId;
    private RefItem stationId;
    private BigDecimal startKp;
    private BigDecimal endKp;
    private TrackKind trackKind = TrackKind.MAIN;
    private Integer preventiveIntervalDays;

    public static AssetForm of(AssetDto dto, MaintenanceNames names) {
        AssetForm form = new AssetForm();
        if (dto != null) {
            form.setCode(orEmpty(dto.code()));
            form.setName(orEmpty(dto.name()));
            form.setDescription(orEmpty(dto.description()));
            form.setExecutionPackageId(names.packageRef(dto.executionPackageId()));
            form.setTrackId(names.trackRef(dto.trackId()));
            form.setStationId(names.stationRef(dto.stationId()));
            form.setStartKp(dto.startKp());
            form.setEndKp(dto.endKp());
            form.setTrackKind(dto.trackKind());
            form.setPreventiveIntervalDays(dto.preventiveIntervalDays());
        }
        return form;
    }

    /** El alta de un tramo de via; lo vacio no viaja. */
    public AssetRequest toRequest() {
        return new AssetRequest(code.trim(), name.trim(), nullIfBlank(description), idOf(executionPackageId), idOf(trackId),
                idOf(stationId), startKp, endKp, trackKind, preventiveIntervalDays);
    }

    /**
     * Lo que cambio respecto a lo leido, lo vaciado y la version. De un activo sincronizado el editor
     * solo toca la descripcion y el intervalo preventivo: lo demas sale igual y no viaja.
     */
    public MergePatch<AssetUpdateRequest> toPatch(AssetDto original) {
        Changes changes = new Changes();
        AssetUpdateRequest values = new AssetUpdateRequest(
                changes.text("name", name, original.name()),
                changes.text("description", description, original.description()),
                null,
                changes.value("preventiveIntervalDays", preventiveIntervalDays, original.preventiveIntervalDays()),
                changes.value("executionPackageId", idOf(executionPackageId), original.executionPackageId()),
                changes.value("trackId", idOf(trackId), original.trackId()),
                changes.value("stationId", idOf(stationId), original.stationId()),
                changes.number("startKp", startKp, original.startKp()),
                changes.number("endKp", endKp, original.endKp()),
                trackKind == original.trackKind() ? null : trackKind);
        return changes.patch(values, original.version());
    }

    private static Long idOf(RefItem item) {
        return item == null ? null : item.id();
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RefItem getExecutionPackageId() {
        return executionPackageId;
    }

    public void setExecutionPackageId(RefItem executionPackageId) {
        this.executionPackageId = executionPackageId;
    }

    public RefItem getTrackId() {
        return trackId;
    }

    public void setTrackId(RefItem trackId) {
        this.trackId = trackId;
    }

    public RefItem getStationId() {
        return stationId;
    }

    public void setStationId(RefItem stationId) {
        this.stationId = stationId;
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

    public TrackKind getTrackKind() {
        return trackKind;
    }

    public void setTrackKind(TrackKind trackKind) {
        this.trackKind = trackKind;
    }

    public Integer getPreventiveIntervalDays() {
        return preventiveIntervalDays;
    }

    public void setPreventiveIntervalDays(Integer preventiveIntervalDays) {
        this.preventiveIntervalDays = preventiveIntervalDays;
    }
}

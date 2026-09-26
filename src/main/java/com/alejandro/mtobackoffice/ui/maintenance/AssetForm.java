package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.TrackKind;
import com.alejandro.mtobackoffice.ui.master.RefItem;

import java.math.BigDecimal;
import java.util.Objects;

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
     * Solo lo que cambio respecto a lo leido: para el servicio {@code null} es «no tocar». Una
     * descripcion vaciada viaja como cadena vacia; un numero o una referencia no se pueden vaciar
     * (el editor no lo deja).
     */
    public AssetUpdateRequest toUpdateRequest(AssetDto original) {
        return new AssetUpdateRequest(
                changed(name, original.name()),
                changed(description, original.description()),
                null,
                Objects.equals(preventiveIntervalDays, original.preventiveIntervalDays()) ? null : preventiveIntervalDays,
                changedId(executionPackageId, original.executionPackageId()),
                changedId(trackId, original.trackId()),
                changedId(stationId, original.stationId()),
                sameNumber(startKp, original.startKp()) ? null : startKp,
                sameNumber(endKp, original.endKp()) ? null : endKp,
                trackKind == original.trackKind() ? null : trackKind);
    }

    private static Long idOf(RefItem item) {
        return item == null ? null : item.id();
    }

    private static Long changedId(RefItem item, Long original) {
        Long id = idOf(item);
        return Objects.equals(id, original) ? null : id;
    }

    /** {@code 12.1} y {@code 12.100} son el mismo KP. */
    private static boolean sameNumber(BigDecimal value, BigDecimal original) {
        return value == null ? original == null : original != null && value.compareTo(original) == 0;
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

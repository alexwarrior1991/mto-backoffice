package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteDto;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteKind;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteRequest;
import com.alejandro.mtobackoffice.client.dto.resources.ParkingSiteUpdateRequest;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.math.BigDecimal;

/**
 * Modelo mutable del editor de un sitio de aparcamiento. Sus propiedades se llaman como los campos de
 * la peticion, aunque el EP, la estacion y la via guarden lo elegido ({@link RefItem}) y no solo su id.
 * Sin {@code config-read} no se eligen y no viajan: los leidos se quedan como estan.
 */
public class ParkingSiteForm {

    private String code = "";
    private String name = "";
    private ParkingSiteKind kind;
    private RefItem executionPackageId;
    private RefItem stationId;
    private RefItem trackId;
    private BigDecimal kp;
    private boolean railAccess;
    private boolean roadAccess = true;
    private String address = "";
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String notes = "";

    public static ParkingSiteForm of(ParkingSiteDto dto, ResourcesNames names) {
        ParkingSiteForm form = new ParkingSiteForm();
        if (dto == null) {
            return form;
        }
        form.code = dto.code();
        form.name = orEmpty(dto.name());
        form.kind = dto.kind();
        form.executionPackageId = names.packageRef(dto.executionPackageId());
        form.stationId = names.stationRef(dto.stationId());
        form.trackId = names.trackRef(dto.trackId());
        form.kp = dto.kp();
        form.railAccess = Boolean.TRUE.equals(dto.railAccess());
        form.roadAccess = Boolean.TRUE.equals(dto.roadAccess());
        form.address = orEmpty(dto.address());
        form.latitude = dto.latitude();
        form.longitude = dto.longitude();
        form.notes = orEmpty(dto.notes());
        return form;
    }

    /** El alta; lo vacio no viaja. */
    public ParkingSiteRequest toRequest(boolean readsConfiguration) {
        return new ParkingSiteRequest(code.trim(), name.trim(), kind, readsConfiguration ? idOf(executionPackageId) : null,
                readsConfiguration ? idOf(stationId) : null, readsConfiguration ? idOf(trackId) : null, kp, railAccess, roadAccess,
                blankToNull(address), latitude, longitude, blankToNull(notes));
    }

    /** Lo que cambio respecto a lo leido, lo vaciado y la version. */
    public MergePatch<ParkingSiteUpdateRequest> toPatch(ParkingSiteDto original, boolean readsConfiguration) {
        Changes changes = new Changes();
        ParkingSiteUpdateRequest values = new ParkingSiteUpdateRequest(
                changes.text("name", name, original.name()),
                changes.value("kind", kind, original.kind()),
                readsConfiguration ? changes.value("executionPackageId", idOf(executionPackageId), original.executionPackageId()) : null,
                readsConfiguration ? changes.value("stationId", idOf(stationId), original.stationId()) : null,
                readsConfiguration ? changes.value("trackId", idOf(trackId), original.trackId()) : null,
                changes.number("kp", kp, original.kp()),
                changes.value("railAccess", railAccess, Boolean.TRUE.equals(original.railAccess())),
                changes.value("roadAccess", roadAccess, Boolean.TRUE.equals(original.roadAccess())),
                changes.text("address", address, original.address()),
                changes.number("latitude", latitude, original.latitude()),
                changes.number("longitude", longitude, original.longitude()),
                changes.text("notes", notes, original.notes()),
                null);
        return changes.patch(values, original.version());
    }

    private static Long idOf(RefItem item) {
        return item == null ? null : item.id();
    }

    private static String blankToNull(String value) {
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

    public ParkingSiteKind getKind() {
        return kind;
    }

    public void setKind(ParkingSiteKind kind) {
        this.kind = kind;
    }

    public RefItem getExecutionPackageId() {
        return executionPackageId;
    }

    public void setExecutionPackageId(RefItem executionPackageId) {
        this.executionPackageId = executionPackageId;
    }

    public RefItem getStationId() {
        return stationId;
    }

    public void setStationId(RefItem stationId) {
        this.stationId = stationId;
    }

    public RefItem getTrackId() {
        return trackId;
    }

    public void setTrackId(RefItem trackId) {
        this.trackId = trackId;
    }

    public BigDecimal getKp() {
        return kp;
    }

    public void setKp(BigDecimal kp) {
        this.kp = kp;
    }

    public boolean isRailAccess() {
        return railAccess;
    }

    public void setRailAccess(boolean railAccess) {
        this.railAccess = railAccess;
    }

    public boolean isRoadAccess() {
        return roadAccess;
    }

    public void setRoadAccess(boolean roadAccess) {
        this.roadAccess = roadAccess;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

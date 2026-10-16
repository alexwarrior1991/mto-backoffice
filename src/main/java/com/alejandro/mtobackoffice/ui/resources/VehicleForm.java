package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleMobility;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOperationalStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleOwner;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleTypeSummaryDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.math.BigDecimal;

/**
 * Modelo mutable del editor de un vehiculo. Sus propiedades se llaman como los campos de la peticion,
 * aunque el modelo guarde lo elegido y no solo su id. Lo que su modelo no admite no viaja: la
 * matricula de uno de solo via ni el numero ferroviario de uno de solo carretera; ni la empresa de uno
 * propio, ni el motivo de uno operativo.
 */
public class VehicleForm {

    private String code = "";
    private String name = "";
    private VehicleTypeSummaryDto typeId;
    private String registrationPlate = "";
    private String railVehicleNumber = "";
    private String serialNumber = "";
    private Integer manufactureYear;
    private VehicleOwner owner = VehicleOwner.OWN;
    private String ownerCompany = "";
    private VehicleOperationalStatus operationalStatus = VehicleOperationalStatus.OPERATIONAL;
    private String statusReason = "";
    private Integer odometerKm;
    private BigDecimal engineHours;
    private String notes = "";

    public static VehicleForm of(VehicleDto dto) {
        VehicleForm form = new VehicleForm();
        if (dto == null) {
            return form;
        }
        form.code = dto.code();
        form.name = orEmpty(dto.name());
        form.typeId = dto.type();
        form.registrationPlate = orEmpty(dto.registrationPlate());
        form.railVehicleNumber = orEmpty(dto.railVehicleNumber());
        form.serialNumber = orEmpty(dto.serialNumber());
        form.manufactureYear = dto.manufactureYear();
        form.owner = dto.owner() == null ? VehicleOwner.OWN : dto.owner();
        form.ownerCompany = orEmpty(dto.ownerCompany());
        form.operationalStatus = dto.operationalStatus() == null ? VehicleOperationalStatus.OPERATIONAL : dto.operationalStatus();
        form.statusReason = orEmpty(dto.statusReason());
        form.odometerKm = dto.odometerKm();
        form.engineHours = dto.engineHours();
        form.notes = orEmpty(dto.notes());
        return form;
    }

    /** Si el modelo elegido va por carretera: lleva matricula. */
    static boolean hasPlate(VehicleTypeSummaryDto type) {
        return type == null || type.mobility() != VehicleMobility.RAIL_ONLY;
    }

    /** Si el modelo elegido va por via: puede llevar numero de vehiculo ferroviario. */
    static boolean hasRailNumber(VehicleTypeSummaryDto type) {
        return type == null || type.mobility() != VehicleMobility.ROAD_ONLY;
    }

    private String plate() {
        return hasPlate(typeId) ? registrationPlate : "";
    }

    private String railNumber() {
        return hasRailNumber(typeId) ? railVehicleNumber : "";
    }

    private String company() {
        return owner == VehicleOwner.OWN ? "" : ownerCompany;
    }

    private String reason() {
        return operationalStatus == VehicleOperationalStatus.OPERATIONAL ? "" : statusReason;
    }

    /** El alta; lo vacio no viaja. */
    public VehicleRequest toRequest() {
        return new VehicleRequest(code.trim(), blankToNull(name), typeId == null ? null : typeId.id(), blankToNull(plate()),
                blankToNull(railNumber()), blankToNull(serialNumber), manufactureYear, owner, blankToNull(company()), operationalStatus,
                blankToNull(reason()), odometerKm, engineHours, blankToNull(notes));
    }

    /** Lo que cambio respecto a lo leido, lo vaciado y la version. */
    public MergePatch<VehicleUpdateRequest> toPatch(VehicleDto original) {
        Changes changes = new Changes();
        VehicleUpdateRequest values = new VehicleUpdateRequest(
                changes.text("name", name, original.name()),
                changes.value("typeId", typeId == null ? null : typeId.id(), original.type() == null ? null : original.type().id()),
                changes.text("registrationPlate", plate(), original.registrationPlate()),
                changes.text("railVehicleNumber", railNumber(), original.railVehicleNumber()),
                changes.text("serialNumber", serialNumber, original.serialNumber()),
                changes.value("manufactureYear", manufactureYear, original.manufactureYear()),
                changes.value("owner", owner, original.owner()),
                changes.text("ownerCompany", company(), original.ownerCompany()),
                changes.value("operationalStatus", operationalStatus, original.operationalStatus()),
                changes.text("statusReason", reason(), original.statusReason()),
                changes.value("odometerKm", odometerKm, original.odometerKm()),
                changes.number("engineHours", engineHours, original.engineHours()),
                changes.text("notes", notes, original.notes()),
                null);
        return changes.patch(values, original.version());
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

    public VehicleTypeSummaryDto getTypeId() {
        return typeId;
    }

    public void setTypeId(VehicleTypeSummaryDto typeId) {
        this.typeId = typeId;
    }

    public String getRegistrationPlate() {
        return registrationPlate;
    }

    public void setRegistrationPlate(String registrationPlate) {
        this.registrationPlate = registrationPlate;
    }

    public String getRailVehicleNumber() {
        return railVehicleNumber;
    }

    public void setRailVehicleNumber(String railVehicleNumber) {
        this.railVehicleNumber = railVehicleNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public VehicleOwner getOwner() {
        return owner;
    }

    public void setOwner(VehicleOwner owner) {
        this.owner = owner;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public VehicleOperationalStatus getOperationalStatus() {
        return operationalStatus;
    }

    public void setOperationalStatus(VehicleOperationalStatus operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public Integer getOdometerKm() {
        return odometerKm;
    }

    public void setOdometerKm(Integer odometerKm) {
        this.odometerKm = odometerKm;
    }

    public BigDecimal getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(BigDecimal engineHours) {
        this.engineHours = engineHours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

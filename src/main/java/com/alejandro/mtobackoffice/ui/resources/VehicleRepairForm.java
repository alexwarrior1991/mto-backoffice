package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.RepairKind;
import com.alejandro.mtobackoffice.client.dto.resources.RepairStatus;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleRepairUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Modelo mutable del dialogo de una reparacion. Sus propiedades se llaman como los campos de la peticion
 * para que un {@code validationErrors[].field} del servicio caiga en su campo. El estado solo se elige en
 * el alta (planificada o abierta ahora); despues cambia con sus transiciones.
 */
public class VehicleRepairForm {

    private RepairStatus status = RepairStatus.OPEN;
    private RepairKind kind;
    private boolean immobilises = true;
    private String description = "";
    private String workshop = "";
    private LocalDate plannedFrom;
    private LocalDate plannedTo;
    private Integer odometerKm;
    private BigDecimal engineHours;
    private BigDecimal cost;
    private String notes = "";

    public static VehicleRepairForm of(VehicleRepairDto dto) {
        VehicleRepairForm form = new VehicleRepairForm();
        if (dto != null) {
            form.status = dto.state();
            form.kind = dto.kind();
            form.immobilises = dto.isImmobilising();
            form.description = blankIfNull(dto.description());
            form.workshop = blankIfNull(dto.workshop());
            form.plannedFrom = dto.plannedFrom();
            form.plannedTo = dto.plannedTo();
            form.odometerKm = dto.odometerKm();
            form.engineHours = dto.engineHours();
            form.cost = dto.cost();
            form.notes = blankIfNull(dto.notes());
        }
        return form;
    }

    /** El alta, con el estado elegido; lo vacio no viaja. */
    public VehicleRepairRequest toRequest() {
        return new VehicleRepairRequest(kind, status, immobilises, description.trim(), textOrNull(workshop), plannedFrom, plannedTo,
                odometerKm, engineHours, cost, textOrNull(notes));
    }

    /**
     * Lo que cambio, lo vaciado y la version leida. La clase, la descripcion y el inicio previsto se
     * cambian, no se vacian (el editor los pide); el estado no viaja.
     */
    public MergePatch<VehicleRepairUpdateRequest> toPatch(VehicleRepairDto original) {
        Changes changes = new Changes();
        VehicleRepairUpdateRequest values = new VehicleRepairUpdateRequest(
                changes.value("kind", kind, original.kind()),
                changes.value("immobilises", immobilises, original.isImmobilising()),
                changes.text("description", description, original.description()),
                changes.text("workshop", workshop, original.workshop()),
                changes.value("plannedFrom", plannedFrom, original.plannedFrom()),
                changes.value("plannedTo", plannedTo, original.plannedTo()),
                changes.value("odometerKm", odometerKm, original.odometerKm()),
                changes.number("engineHours", engineHours, original.engineHours()),
                changes.number("cost", cost, original.cost()),
                changes.text("notes", notes, original.notes()));
        return changes.patch(values, original.version());
    }

    private static String blankIfNull(String value) {
        return value == null ? "" : value;
    }

    private static String textOrNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public RepairStatus getStatus() {
        return status;
    }

    public void setStatus(RepairStatus status) {
        this.status = status;
    }

    public RepairKind getKind() {
        return kind;
    }

    public void setKind(RepairKind kind) {
        this.kind = kind;
    }

    public boolean isImmobilises() {
        return immobilises;
    }

    public void setImmobilises(boolean immobilises) {
        this.immobilises = immobilises;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public LocalDate getPlannedFrom() {
        return plannedFrom;
    }

    public void setPlannedFrom(LocalDate plannedFrom) {
        this.plannedFrom = plannedFrom;
    }

    public LocalDate getPlannedTo() {
        return plannedTo;
    }

    public void setPlannedTo(LocalDate plannedTo) {
        this.plannedTo = plannedTo;
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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

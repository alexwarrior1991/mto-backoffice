package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentRequest;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Modelo mutable del editor de un instrumento. Sus propiedades se llaman como los campos de la peticion
 * para que un error del servicio caiga en su campo.
 */
public class InstrumentForm {

    private String code = "";
    private String name = "";
    private InstrumentKind kind;
    private String serialNumber = "";
    private LocalDate calibratedOn;
    private LocalDate calibrationExpiresOn;
    private String notes = "";

    public static InstrumentForm of(InstrumentDto dto) {
        InstrumentForm form = new InstrumentForm();
        if (dto != null) {
            form.code = dto.code();
            form.name = orEmpty(dto.name());
            form.kind = dto.kind();
            form.serialNumber = orEmpty(dto.serialNumber());
            form.calibratedOn = dto.calibratedOn();
            form.calibrationExpiresOn = dto.calibrationExpiresOn();
            form.notes = orEmpty(dto.notes());
        }
        return form;
    }

    /** El alta; lo vacio no viaja. El codigo va en mayusculas, como lo guarda el servicio. */
    public InstrumentRequest toRequest() {
        return new InstrumentRequest(code.trim().toUpperCase(Locale.ROOT), name.trim(), kind, blankToNull(serialNumber), calibratedOn,
                calibrationExpiresOn, blankToNull(notes));
    }

    /** Lo que cambio respecto a lo leido, lo vaciado y la version; renovar la calibracion es cambiar sus fechas. */
    public MergePatch<InstrumentUpdateRequest> toPatch(InstrumentDto original) {
        Changes changes = new Changes();
        InstrumentUpdateRequest values = new InstrumentUpdateRequest(
                changes.text("name", name, original.name()),
                changes.value("kind", kind, original.kind()),
                changes.text("serialNumber", serialNumber, original.serialNumber()),
                changes.value("calibratedOn", calibratedOn, original.calibratedOn()),
                changes.value("calibrationExpiresOn", calibrationExpiresOn, original.calibrationExpiresOn()),
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

    public InstrumentKind getKind() {
        return kind;
    }

    public void setKind(InstrumentKind kind) {
        this.kind = kind;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public LocalDate getCalibratedOn() {
        return calibratedOn;
    }

    public void setCalibratedOn(LocalDate calibratedOn) {
        this.calibratedOn = calibratedOn;
    }

    public LocalDate getCalibrationExpiresOn() {
        return calibrationExpiresOn;
    }

    public void setCalibrationExpiresOn(LocalDate calibrationExpiresOn) {
        this.calibrationExpiresOn = calibrationExpiresOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

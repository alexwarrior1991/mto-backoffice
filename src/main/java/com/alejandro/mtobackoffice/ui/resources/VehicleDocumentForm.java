package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentRequest;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleDocumentUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.time.LocalDate;

/**
 * Modelo mutable del dialogo de un documento de un vehiculo. Sus propiedades se llaman como los campos
 * de la peticion para que un error del servicio caiga en su campo. La caducidad es obligatoria en el
 * editor, asi que nunca viaja vaciada.
 */
public class VehicleDocumentForm {

    private VehicleDocumentKind kind;
    private String reference = "";
    private LocalDate issuedOn;
    private LocalDate expiresOn;
    private String notes = "";

    public static VehicleDocumentForm of(VehicleDocumentDto dto) {
        VehicleDocumentForm form = new VehicleDocumentForm();
        if (dto != null) {
            form.kind = dto.kind();
            form.reference = orEmpty(dto.reference());
            form.issuedOn = dto.issuedOn();
            form.expiresOn = dto.expiresOn();
            form.notes = orEmpty(dto.notes());
        }
        return form;
    }

    /** El alta; lo vacio no viaja. */
    public VehicleDocumentRequest toRequest() {
        return new VehicleDocumentRequest(kind, blankToNull(reference), issuedOn, expiresOn, blankToNull(notes));
    }

    /** Lo que cambio respecto a lo leido, lo vaciado y la version; el tipo no cambia. */
    public MergePatch<VehicleDocumentUpdateRequest> toPatch(VehicleDocumentDto original) {
        Changes changes = new Changes();
        VehicleDocumentUpdateRequest values = new VehicleDocumentUpdateRequest(
                changes.text("reference", reference, original.reference()),
                changes.value("issuedOn", issuedOn, original.issuedOn()),
                changes.value("expiresOn", expiresOn, original.expiresOn()),
                changes.text("notes", notes, original.notes()));
        return changes.patch(values, original.version());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public VehicleDocumentKind getKind() {
        return kind;
    }

    public void setKind(VehicleDocumentKind kind) {
        this.kind = kind;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(LocalDate issuedOn) {
        this.issuedOn = issuedOn;
    }

    public LocalDate getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(LocalDate expiresOn) {
        this.expiresOn = expiresOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

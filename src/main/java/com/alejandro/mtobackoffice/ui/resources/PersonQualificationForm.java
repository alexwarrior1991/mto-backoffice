package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonQualificationUpdateRequest;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.time.LocalDate;

/**
 * Modelo mutable del dialogo de una cualificacion de una persona. Sus propiedades se llaman como los
 * campos de la peticion para que un {@code validationErrors[].field} del servicio caiga en su campo,
 * aunque {@code qualificationTypeId} guarde el tipo elegido y no solo su id.
 */
public class PersonQualificationForm {

    private QualificationTypeDto qualificationTypeId;
    private String reference = "";
    private LocalDate issuedOn;
    private LocalDate expiresOn;
    private String notes = "";

    public static PersonQualificationForm of(PersonQualificationDto dto) {
        PersonQualificationForm form = new PersonQualificationForm();
        if (dto != null) {
            form.reference = orEmpty(dto.reference());
            form.issuedOn = dto.issuedOn();
            form.expiresOn = dto.expiresOn();
            form.notes = orEmpty(dto.notes());
        }
        return form;
    }

    /** El alta; lo vacio no viaja, y sin caducidad no caduca. */
    public PersonQualificationRequest toRequest() {
        return new PersonQualificationRequest(qualificationTypeId == null ? null : qualificationTypeId.id(), blankToNull(reference), issuedOn,
                expiresOn, blankToNull(notes));
    }

    /** Lo que cambio, lo vaciado y la version leida: el servicio deja vaciar todo (una caducidad vacia es que no caduca). */
    public MergePatch<PersonQualificationUpdateRequest> toPatch(PersonQualificationDto original) {
        Changes changes = new Changes();
        PersonQualificationUpdateRequest values = new PersonQualificationUpdateRequest(
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

    public QualificationTypeDto getQualificationTypeId() {
        return qualificationTypeId;
    }

    public void setQualificationTypeId(QualificationTypeDto qualificationTypeId) {
        this.qualificationTypeId = qualificationTypeId;
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

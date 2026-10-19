package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.AbsenceKind;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonAbsenceUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.time.LocalDate;

/**
 * Modelo mutable del dialogo de una ausencia. Sus propiedades se llaman como los campos de la peticion
 * para que un {@code validationErrors[].field} del servicio caiga en su campo.
 */
public class PersonAbsenceForm {

    private AbsenceKind kind;
    private LocalDate startsOn;
    private LocalDate endsOn;
    private String notes = "";

    public static PersonAbsenceForm of(PersonAbsenceDto dto) {
        PersonAbsenceForm form = new PersonAbsenceForm();
        if (dto != null) {
            form.kind = dto.kind();
            form.startsOn = dto.startsOn();
            form.endsOn = dto.endsOn();
            form.notes = dto.notes() == null ? "" : dto.notes();
        }
        return form;
    }

    /** El alta; las notas vacias no viajan. */
    public PersonAbsenceRequest toRequest() {
        return new PersonAbsenceRequest(kind, startsOn, endsOn, notes == null || notes.isBlank() ? null : notes.trim());
    }

    /** Lo que cambio, las notas vaciadas y la version leida: la clase y las fechas se cambian, no se vacian. */
    public MergePatch<PersonAbsenceUpdateRequest> toPatch(PersonAbsenceDto original) {
        Changes changes = new Changes();
        PersonAbsenceUpdateRequest values = new PersonAbsenceUpdateRequest(
                changes.value("kind", kind, original.kind()),
                changes.value("startsOn", startsOn, original.startsOn()),
                changes.value("endsOn", endsOn, original.endsOn()),
                changes.text("notes", notes, original.notes()));
        return changes.patch(values, original.version());
    }

    public AbsenceKind getKind() {
        return kind;
    }

    public void setKind(AbsenceKind kind) {
        this.kind = kind;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }

    public void setStartsOn(LocalDate startsOn) {
        this.startsOn = startsOn;
    }

    public LocalDate getEndsOn() {
        return endsOn;
    }

    public void setEndsOn(LocalDate endsOn) {
        this.endsOn = endsOn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

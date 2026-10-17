package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.DrivingLicence;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationKind;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeDto;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeRequest;
import com.alejandro.mtobackoffice.client.dto.resources.QualificationTypeUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

import java.util.Locale;

/**
 * Modelo mutable del editor de un tipo de cualificacion. Sus propiedades se llaman como los campos de
 * la peticion para que un error del servicio caiga en su campo. El permiso de conducir solo existe en
 * uno de {@code DRIVING_LICENCE}: en otro no viaja, y al modificar se vacia si lo tenia.
 */
public class QualificationTypeForm {

    private String code = "";
    private String name = "";
    private QualificationKind kind;
    private DrivingLicence drivingLicence;
    private Integer validityMonths;
    private String description = "";

    public static QualificationTypeForm of(QualificationTypeDto dto) {
        QualificationTypeForm form = new QualificationTypeForm();
        if (dto != null) {
            form.code = dto.code();
            form.name = orEmpty(dto.name());
            form.kind = dto.kind();
            form.drivingLicence = dto.drivingLicence();
            form.validityMonths = dto.validityMonths();
            form.description = orEmpty(dto.description());
        }
        return form;
    }

    /** El permiso solo cuenta en un permiso de conducir. */
    private DrivingLicence licence() {
        return kind == QualificationKind.DRIVING_LICENCE ? drivingLicence : null;
    }

    /** El alta; lo vacio no viaja. El codigo va en mayusculas, como lo guarda el servicio. */
    public QualificationTypeRequest toRequest() {
        return new QualificationTypeRequest(code.trim().toUpperCase(Locale.ROOT), name.trim(), kind, licence(), validityMonths,
                description == null || description.isBlank() ? null : description.trim());
    }

    /** Lo que cambio respecto a lo leido, lo vaciado (permiso, vigencia, descripcion) y la version. */
    public MergePatch<QualificationTypeUpdateRequest> toPatch(QualificationTypeDto original) {
        Changes changes = new Changes();
        QualificationTypeUpdateRequest values = new QualificationTypeUpdateRequest(
                changes.text("name", name, original.name()),
                changes.value("kind", kind, original.kind()),
                changes.value("drivingLicence", licence(), original.drivingLicence()),
                changes.value("validityMonths", validityMonths, original.validityMonths()),
                changes.text("description", description, original.description()),
                null);
        return changes.patch(values, original.version());
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

    public QualificationKind getKind() {
        return kind;
    }

    public void setKind(QualificationKind kind) {
        this.kind = kind;
    }

    public DrivingLicence getDrivingLicence() {
        return drivingLicence;
    }

    public void setDrivingLicence(DrivingLicence drivingLicence) {
        this.drivingLicence = drivingLicence;
    }

    public Integer getValidityMonths() {
        return validityMonths;
    }

    public void setValidityMonths(Integer validityMonths) {
        this.validityMonths = validityMonths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

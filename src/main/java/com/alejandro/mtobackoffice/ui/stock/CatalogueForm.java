package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.CatalogueRequest;
import com.alejandro.mtobackoffice.client.dto.stock.CatalogueUpdateRequest;

/**
 * Modelo mutable del editor de almacen, proveedor o proyecto. Sus propiedades se llaman como los
 * campos del servicio para que un {@code validationErrors[].field} caiga en su campo.
 */
public class CatalogueForm {

    private String code = "";
    private String name = "";
    private boolean active = true;

    public static CatalogueForm of(CatalogueEditorDialog.Snapshot existing) {
        CatalogueForm form = new CatalogueForm();
        if (existing != null) {
            form.setCode(existing.code() == null ? "" : existing.code());
            form.setName(existing.name() == null ? "" : existing.name());
            form.setActive(existing.active());
        }
        return form;
    }

    public CatalogueRequest toCreateRequest() {
        return new CatalogueRequest(code.trim(), name.trim());
    }

    public CatalogueUpdateRequest toUpdateRequest() {
        return new CatalogueUpdateRequest(code.trim(), name.trim(), active);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

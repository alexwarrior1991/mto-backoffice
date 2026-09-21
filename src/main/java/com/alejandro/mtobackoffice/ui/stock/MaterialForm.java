package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialRequest;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialUpdateRequest;

import java.math.BigDecimal;

/** Modelo mutable del editor de materiales; propiedades con los nombres del servicio. */
public class MaterialForm {

    private String code = "";
    private String name = "";
    private String unitOfMeasure = "";
    private BigDecimal minimumStockLevel = BigDecimal.ZERO;
    private boolean active = true;

    public static MaterialForm of(MaterialDto dto) {
        MaterialForm form = new MaterialForm();
        if (dto != null) {
            form.setCode(dto.code() == null ? "" : dto.code());
            form.setName(dto.name() == null ? "" : dto.name());
            form.setUnitOfMeasure(dto.unitOfMeasure() == null ? "" : dto.unitOfMeasure());
            form.setMinimumStockLevel(dto.minimumStockLevel() == null ? BigDecimal.ZERO : dto.minimumStockLevel());
            form.setActive(dto.isEnabled());
        }
        return form;
    }

    public MaterialRequest toCreateRequest() {
        return new MaterialRequest(code.trim(), name.trim(), unitOfMeasure.trim(), minimumStockLevel);
    }

    public MaterialUpdateRequest toUpdateRequest() {
        return new MaterialUpdateRequest(code.trim(), name.trim(), unitOfMeasure.trim(), minimumStockLevel, active);
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

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public BigDecimal getMinimumStockLevel() {
        return minimumStockLevel;
    }

    public void setMinimumStockLevel(BigDecimal minimumStockLevel) {
        this.minimumStockLevel = minimumStockLevel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

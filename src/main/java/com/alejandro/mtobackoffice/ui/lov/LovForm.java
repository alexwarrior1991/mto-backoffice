package com.alejandro.mtobackoffice.ui.lov;

import com.alejandro.mtobackoffice.client.dto.LovDto;

/** Modelo mutable del editor de una entrada de catalogo: lo que el Binder lee y escribe. */
public class LovForm {

    private String code = "";
    private String description = "";
    private boolean enabled = true;

    public static LovForm of(LovDto dto) {
        LovForm form = new LovForm();
        if (dto != null) {
            form.setCode(dto.code() == null ? "" : dto.code());
            form.setDescription(dto.description() == null ? "" : dto.description());
            form.setEnabled(dto.isEnabled());
        }
        return form;
    }

    /** El DTO a enviar: el existente con los valores del formulario, o uno nuevo. */
    public LovDto toDto(LovDto existing) {
        String trimmedCode = code == null ? "" : code.trim();
        String trimmedDescription = description == null ? "" : description.trim();
        return existing == null
                ? LovDto.forCreate(trimmedCode, trimmedDescription, enabled)
                : existing.withValues(trimmedCode, trimmedDescription, enabled);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

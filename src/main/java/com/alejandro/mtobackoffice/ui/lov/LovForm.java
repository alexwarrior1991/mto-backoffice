package com.alejandro.mtobackoffice.ui.lov;

import com.alejandro.mtobackoffice.client.configuration.LovResource;
import com.alejandro.mtobackoffice.client.dto.LovDto;

import java.util.Optional;

/** Modelo mutable del editor de una entrada de catalogo: lo que el Binder lee y escribe. */
public class LovForm {

    private String code = "";
    private String description = "";
    private boolean enabled = true;
    private LovDto parent;

    public static LovForm of(LovDto dto) {
        return of(dto, Optional.empty());
    }

    /** Con el tipo padre que trae la entrada, en los tres catalogos que lo tienen. */
    public static LovForm of(LovDto dto, Optional<LovResource.Parent> parentType) {
        LovForm form = new LovForm();
        if (dto != null) {
            form.setCode(dto.code() == null ? "" : dto.code());
            form.setDescription(dto.description() == null ? "" : dto.description());
            form.setEnabled(dto.isEnabled());
            parentType.flatMap(type -> dto.parent(type.field())).ifPresent(form::setParent);
        }
        return form;
    }

    /** El DTO a enviar: el existente con los valores del formulario, o uno nuevo. */
    public LovDto toDto(LovDto existing) {
        return toDto(existing, Optional.empty());
    }

    /**
     * El DTO a enviar: la entrada leida entera con lo cambiado encima (lo que no tiene campo vuelve
     * tal cual), o una nueva; y, si el catalogo tiene tipo padre, ese tipo por su id.
     */
    public LovDto toDto(LovDto existing, Optional<LovResource.Parent> parentType) {
        String trimmedCode = code == null ? "" : code.trim();
        String trimmedDescription = description == null ? "" : description.trim();
        LovDto dto = existing == null
                ? LovDto.forCreate(trimmedCode, trimmedDescription, enabled)
                : existing.withValues(trimmedCode, trimmedDescription, enabled);
        return parentType.isPresent() && parent != null && parent.id() != null
                ? dto.withParent(parentType.get().field(), parent.id())
                : dto;
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

    public LovDto getParent() {
        return parent;
    }

    public void setParent(LovDto parent) {
        this.parent = parent;
    }
}

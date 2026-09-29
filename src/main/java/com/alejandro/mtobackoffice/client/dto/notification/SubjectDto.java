package com.alejandro.mtobackoffice.client.dto.notification;

/** Sobre que fue: el tipo de entidad, su id y una etiqueta legible cuando la fuente la da. */
public record SubjectDto(String type, String id, String label) {

    public String describe() {
        String name = label != null && !label.isBlank() ? label : id == null ? "" : id;
        if (type == null || type.isBlank()) {
            return name;
        }
        return name.isEmpty() ? type : type + " " + name;
    }
}

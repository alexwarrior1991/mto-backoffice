package com.alejandro.mtobackoffice.client.dto.notification;

/** Quien hizo algo: su clase, su nombre de usuario y su id en el realm (los tres pueden faltar). */
public record ActorDto(ActorKind kind, String username, String id) {

    /** El nombre de usuario, o la clase si no lo hay (un planificador, una rafaga cerrada). */
    public String describe() {
        if (username != null && !username.isBlank()) {
            return username;
        }
        return kind == null ? "" : kind.label();
    }
}

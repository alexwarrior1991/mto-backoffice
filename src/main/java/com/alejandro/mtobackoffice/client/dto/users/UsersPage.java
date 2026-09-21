package com.alejandro.mtobackoffice.client.dto.users;

import java.util.List;

/**
 * La pagina de la busqueda de usuarios, al estilo de Keycloak: desplazamiento {@code first},
 * tamano {@code max} y el {@code total} de la consulta. No es la forma {@code {content, page}} de
 * mto-configuration, y solo la busqueda la devuelve: las listas de miembros van planas.
 */
public record UsersPage<T>(List<T> content, int first, int max, long total) {

    public UsersPage {
        content = content == null ? List.of() : List.copyOf(content);
    }
}

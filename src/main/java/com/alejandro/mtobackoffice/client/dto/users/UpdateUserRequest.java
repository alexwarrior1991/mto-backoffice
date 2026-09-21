package com.alejandro.mtobackoffice.client.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * La modificacion, parcial: para el servicio {@code null} es "no tocar", asi que solo viaja lo que
 * cambia y vaciar un campo se manda como cadena vacia. El nombre de usuario no se puede cambiar.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        Boolean emailVerified,
        Map<String, List<String>> attributes
) {
}

package com.alejandro.mtobackoffice.client.dto.users;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * El correo con las acciones que la persona tiene que completar al entrar (cambiar la contrasena,
 * verificar el email...). Necesita SMTP en el realm; sin el, mto-users responde 502.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExecuteActionsEmailRequest(
        List<RequiredAction> actions,
        Integer lifespanSeconds,
        String clientId,
        String redirectUri
) {
}

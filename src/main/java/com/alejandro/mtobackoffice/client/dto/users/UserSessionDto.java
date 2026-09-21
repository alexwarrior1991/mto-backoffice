package com.alejandro.mtobackoffice.client.dto.users;

import java.time.Instant;
import java.util.List;

/** Una sesion de la persona, normal u offline, con los clientes por los que entro. */
public record UserSessionDto(
        String id,
        String username,
        String ipAddress,
        Instant startedAt,
        Instant lastAccessAt,
        List<String> clients
) {

    public UserSessionDto {
        clients = clients == null ? List.of() : List.copyOf(clients);
    }
}

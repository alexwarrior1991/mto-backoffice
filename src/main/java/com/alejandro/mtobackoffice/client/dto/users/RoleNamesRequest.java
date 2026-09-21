package com.alejandro.mtobackoffice.client.dto.users;

import java.util.List;

/** Nombres de rol de un cliente, para anadir ({@code PUT}) o quitar ({@code DELETE} con cuerpo). */
public record RoleNamesRequest(List<String> roles) {
}

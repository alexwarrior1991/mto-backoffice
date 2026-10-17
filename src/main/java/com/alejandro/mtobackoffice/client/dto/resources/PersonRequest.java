package com.alejandro.mtobackoffice.client.dto.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * El alta de una persona; el codigo ({@code PER-000001}) lo pone el servicio y lo vacio no viaja.
 * {@code username} es su {@code preferred_username} de Keycloak, si entra en las aplicaciones.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PersonRequest(String firstName, String lastName, PersonCategory category, String employeeNumber, String username,
                            PersonEmployer employer, String company, String phone) {
}

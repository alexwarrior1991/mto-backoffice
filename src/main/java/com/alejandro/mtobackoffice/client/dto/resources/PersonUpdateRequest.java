package com.alejandro.mtobackoffice.client.dto.resources;

/**
 * Lo que se cambia de una persona, para un {@code MergePatch}: lo que va a {@code null} no viaja, y lo
 * vaciado lo dice el parche. {@code active=false} desactiva como el {@code DELETE} y pide
 * {@code resources-delete}; {@code active=true} reactiva y basta con {@code resources-write}.
 */
public record PersonUpdateRequest(String firstName, String lastName, PersonCategory category, String employeeNumber, String username,
                                  PersonEmployer employer, String company, String phone, Boolean active) {

    public static PersonUpdateRequest active(boolean active) {
        return new PersonUpdateRequest(null, null, null, null, null, null, null, null, active);
    }
}

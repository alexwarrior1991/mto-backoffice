package com.alejandro.mtobackoffice.client.dto.master;

/** Una empresa (entidad de negocio), solo lectura: es a lo que apunta {@code companyId} de un paquete. */
public record BusinessEntityDto(Long id, String identificationNumber, String name, String code) {

    public String label() {
        return identificationNumber == null || identificationNumber.isBlank() ? name : name + " (" + identificationNumber + ")";
    }
}

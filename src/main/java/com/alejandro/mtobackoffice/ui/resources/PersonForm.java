package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonEmployer;
import com.alejandro.mtobackoffice.client.dto.resources.PersonRequest;
import com.alejandro.mtobackoffice.client.dto.resources.PersonUpdateRequest;
import com.alejandro.mtobackoffice.ui.support.Changes;

/**
 * Modelo mutable del editor de una persona. Sus propiedades se llaman como los campos de la peticion
 * para que un {@code validationErrors[].field} del servicio caiga en su campo.
 */
public class PersonForm {

    private String firstName = "";
    private String lastName = "";
    private PersonCategory category;
    private PersonEmployer employer = PersonEmployer.OWN;
    private String company = "";
    private String employeeNumber = "";
    private String username = "";
    private String phone = "";

    public static PersonForm of(PersonDto dto) {
        PersonForm form = new PersonForm();
        if (dto != null) {
            form.setFirstName(orEmpty(dto.firstName()));
            form.setLastName(orEmpty(dto.lastName()));
            form.setCategory(dto.category());
            form.setEmployer(dto.employer() == null ? PersonEmployer.OWN : dto.employer());
            form.setCompany(orEmpty(dto.company()));
            form.setEmployeeNumber(orEmpty(dto.employeeNumber()));
            form.setUsername(orEmpty(dto.username()));
            form.setPhone(orEmpty(dto.phone()));
        }
        return form;
    }

    /** El alta; lo vacio no viaja. Una persona propia no lleva empresa. */
    public PersonRequest toRequest() {
        return new PersonRequest(firstName.trim(), lastName.trim(), category, nullIfBlank(employeeNumber), nullIfBlank(username),
                employer, employer == PersonEmployer.SUBCONTRACTOR ? nullIfBlank(company) : null, nullIfBlank(phone));
    }

    /**
     * Lo que cambio respecto a lo leido, lo vaciado y la version. Lo que el servicio deja vaciar es lo
     * opcional (numero de empleado, usuario, empresa, telefono); nombre, apellidos y categoria son
     * obligatorios en el editor y nunca salen vacios. Pasar a propio vacia la empresa.
     */
    public MergePatch<PersonUpdateRequest> toPatch(PersonDto original) {
        Changes changes = new Changes();
        String chosenCompany = employer == PersonEmployer.SUBCONTRACTOR ? company : "";
        PersonUpdateRequest values = new PersonUpdateRequest(
                changes.text("firstName", firstName, original.firstName()),
                changes.text("lastName", lastName, original.lastName()),
                changes.value("category", category, original.category()),
                changes.text("employeeNumber", employeeNumber, original.employeeNumber()),
                changes.text("username", username, original.username()),
                changes.value("employer", employer, original.employer()),
                changes.text("company", chosenCompany, original.company()),
                changes.text("phone", phone, original.phone()),
                null);
        return changes.patch(values, original.version());
    }

    private static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PersonCategory getCategory() {
        return category;
    }

    public void setCategory(PersonCategory category) {
        this.category = category;
    }

    public PersonEmployer getEmployer() {
        return employer;
    }

    public void setEmployer(PersonEmployer employer) {
        this.employer = employer;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

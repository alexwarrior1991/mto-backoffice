package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.CreateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.UpdateUserRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Modelo mutable del editor de un usuario: lo que el Binder lee y escribe. Sus propiedades se
 * llaman como los campos del servicio para que un {@code validationErrors[].field} caiga en su
 * campo.
 */
public class UserForm {

    private String username = "";
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private boolean emailVerified;
    private boolean enabled = true;
    private String temporaryPassword = "";
    private Set<RequiredAction> requiredActions = new LinkedHashSet<>();
    private String attributes = "";

    public static UserForm of(UserDto dto) {
        UserForm form = new UserForm();
        if (dto != null) {
            form.setUsername(orEmpty(dto.username()));
            form.setFirstName(orEmpty(dto.firstName()));
            form.setLastName(orEmpty(dto.lastName()));
            form.setEmail(orEmpty(dto.email()));
            form.setEmailVerified(Boolean.TRUE.equals(dto.emailVerified()));
            form.setEnabled(dto.isEnabled());
            form.setAttributes(UserAttributes.format(dto.attributes()));
            Set<RequiredAction> actions = new LinkedHashSet<>();
            for (String action : dto.requiredActions()) {
                try {
                    actions.add(RequiredAction.valueOf(action));
                } catch (IllegalArgumentException unknown) {
                    // una accion que esta aplicacion no conoce no se puede elegir; el servicio la conserva
                }
            }
            form.setRequiredActions(actions);
        }
        return form;
    }

    /** El alta: lo vacio no viaja, y con NON_NULL el servicio aplica sus valores por defecto. */
    public CreateUserRequest toCreateRequest() {
        return new CreateUserRequest(
                trimmed(username),
                nullIfBlank(firstName),
                nullIfBlank(lastName),
                nullIfBlank(email),
                emailVerified,
                enabled,
                attributesOrNull(),
                requiredActions.isEmpty() ? null : List.copyOf(requiredActions),
                nullIfBlank(temporaryPassword));
    }

    /**
     * La modificacion, solo con lo que cambio respecto a lo leido: para el servicio {@code null}
     * es "no tocar", asi que un campo igual va a {@code null} y uno vaciado viaja como cadena
     * vacia. El nombre de usuario no cambia nunca.
     */
    public UpdateUserRequest toUpdateRequest(UserDto original) {
        Map<String, List<String>> parsed = UserAttributes.parse(attributes);
        return new UpdateUserRequest(
                changed(firstName, original.firstName()),
                changed(lastName, original.lastName()),
                changed(email, original.email()),
                emailVerified == Boolean.TRUE.equals(original.emailVerified()) ? null : emailVerified,
                Objects.equals(parsed, original.attributes()) ? null : parsed);
    }

    private Map<String, List<String>> attributesOrNull() {
        Map<String, List<String>> parsed = UserAttributes.parse(attributes);
        return parsed.isEmpty() ? null : parsed;
    }

    private static String changed(String value, String original) {
        String current = trimmed(value);
        return current.equals(orEmpty(original)) ? null : current;
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }

    private static String nullIfBlank(String value) {
        String current = trimmed(value);
        return current.isEmpty() ? null : current;
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public Set<RequiredAction> getRequiredActions() {
        return requiredActions;
    }

    public void setRequiredActions(Set<RequiredAction> requiredActions) {
        this.requiredActions = requiredActions == null ? new LinkedHashSet<>() : new LinkedHashSet<>(requiredActions);
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}

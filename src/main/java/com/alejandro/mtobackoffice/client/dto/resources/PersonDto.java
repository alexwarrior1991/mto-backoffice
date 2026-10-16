package com.alejandro.mtobackoffice.client.dto.resources;

import java.util.UUID;

/**
 * Una persona de mto-resources, solo con lo que usan las pantallas. {@code version} vuelve como se
 * leyo en la modificacion: si otra persona guardo antes, el servicio responde 409 {@code CON-001}.
 */
public record PersonDto(UUID id, String code, String employeeNumber, String firstName, String lastName, String fullName,
                        String username, PersonCategory category, PersonEmployer employer, String company, String phone,
                        Boolean active, Long version) {

    /** {@code PER-000003 · Yossi Cohen}, como se nombra en los titulos y las confirmaciones. */
    public String label() {
        return fullName == null || fullName.isBlank() ? code : code + " · " + fullName;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}

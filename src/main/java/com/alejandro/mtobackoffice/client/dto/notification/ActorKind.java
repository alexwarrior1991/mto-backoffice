package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

/** Quien esta detras de un evento: una persona, la cuenta de servicio de otro servicio, o nadie en concreto. */
public enum ActorKind {
    PERSON("Persona"),
    SERVICE("Servicio"),
    SYSTEM("Sistema"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ActorKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ActorKind of(String value) {
        return ClientEnums.parse(ActorKind.class, value, UNKNOWN);
    }
}

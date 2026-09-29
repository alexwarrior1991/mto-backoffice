package com.alejandro.mtobackoffice.client.dto.notification;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * Las categorias del registro de mto-notification, y de las notificaciones que salen de el.
 * {@link #ACCESS} es distinta de las demas: tiene su pantalla y su permiso aparte, y el registro
 * general la rechaza como filtro (400).
 */
public enum ActivityCategory {
    ACCESS("Accesos"),
    USERS("Usuarios"),
    CONFIGURATION("Configuracion"),
    MAINTENANCE("Mantenimiento"),
    STOCK("Almacen"),
    SYSTEM("Sistema"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    ActivityCategory(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ActivityCategory of(String value) {
        return ClientEnums.parse(ActivityCategory.class, value, UNKNOWN);
    }

    public static List<ActivityCategory> selectable() {
        return ClientEnums.selectable(ActivityCategory.class, UNKNOWN);
    }

    /** Lo que admite el filtro del registro general: todas menos los accesos, que van por su pantalla. */
    public static List<ActivityCategory> selectableForActivity() {
        return selectable().stream().filter(category -> category != ACCESS).toList();
    }
}

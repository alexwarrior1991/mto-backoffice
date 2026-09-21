package com.alejandro.mtobackoffice.ui.master;

import com.vaadin.flow.component.select.Select;

/**
 * El filtro de tres estados de una lista: todo, solo lo activo o solo lo inactivo. El servicio
 * filtra por un booleano solo si viene ({@code enabled} u {@code onLoad}), asi que "todo" es no
 * mandarlo.
 */
public enum EnabledFilter {

    ALL(null),
    ENABLED(Boolean.TRUE),
    DISABLED(Boolean.FALSE);

    private final Boolean value;

    EnabledFilter(Boolean value) {
        this.value = value;
    }

    /** {@code null} para no filtrar; es lo que se mete en el cuerpo del {@code /filter}. */
    public Boolean value() {
        return value;
    }

    public static Select<EnabledFilter> select(String label, String all, String enabled, String disabled) {
        Select<EnabledFilter> select = new Select<>();
        select.setLabel(label);
        select.setItems(values());
        select.setItemLabelGenerator(filter -> switch (filter) {
            case ALL -> all;
            case ENABLED -> enabled;
            case DISABLED -> disabled;
        });
        select.setValue(ALL);
        return select;
    }
}

package com.alejandro.mtobackoffice.client.dto.resources;

/** Los filtros del catalogo de tipos de cualificacion; lo que va a {@code null} no viaja. */
public record QualificationTypeFilter(String search, QualificationKind kind, Boolean active) {

    public static final QualificationTypeFilter NONE = new QualificationTypeFilter(null, null, null);
    /** Los que se pueden elegir: una persona o un modelo no se apuntan a uno retirado. */
    public static final QualificationTypeFilter ACTIVE = new QualificationTypeFilter(null, null, true);

    public QualificationTypeFilter {
        search = search == null || search.isBlank() ? null : search.trim();
    }
}

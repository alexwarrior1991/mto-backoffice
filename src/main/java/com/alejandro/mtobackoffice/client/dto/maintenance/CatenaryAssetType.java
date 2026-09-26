package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/** Sobre que se hace el mantenimiento: un tramo de via, creado aqui, o un elemento de mto-configuration. */
public enum CatenaryAssetType {
    TRACK_SECTION("Tramo de via"),
    PROFILE("Perfil"),
    DISCONNECTOR("Seccionador"),
    SECTION_INSULATOR("Aislador de seccion"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    CatenaryAssetType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CatenaryAssetType of(String value) {
        return ClientEnums.parse(CatenaryAssetType.class, value, UNKNOWN);
    }

    public static List<CatenaryAssetType> selectable() {
        return ClientEnums.selectable(CatenaryAssetType.class, UNKNOWN);
    }
}

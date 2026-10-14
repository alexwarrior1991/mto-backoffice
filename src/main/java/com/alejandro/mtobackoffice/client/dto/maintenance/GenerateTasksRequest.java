package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Generar una tarea por perfil del tramo y, con {@code assetTypes}, tambien por aislador de seccion
 * que lo toca; sin tipos, los del servicio para cada activo (grupos 1, 2 y 4 en un perfil, RG-03 en un
 * aislador), y los elegidos van a todo lo generado. Sin {@code assetTypes}, solo perfiles.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateTasksRequest(List<String> taskTypeCodes, Boolean withChecklist, List<CatenaryAssetType> assetTypes) {

    /** Lo que se genera con los aisladores: los perfiles y ellos. */
    public static final List<CatenaryAssetType> WITH_INSULATORS = List.of(CatenaryAssetType.PROFILE, CatenaryAssetType.SECTION_INSULATOR);
}

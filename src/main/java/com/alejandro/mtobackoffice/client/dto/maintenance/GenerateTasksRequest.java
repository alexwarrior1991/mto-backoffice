package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Generar las tareas de un preventivo. Sin {@code selections}, la peticion de siempre: una tarea por
 * perfil del tramo de la orden y, con {@code assetTypes}, tambien por aislador o seccionador que lo
 * toca. Con ellas, lo que dice cada una, en cualquier via del EP de la orden (una orden es de un solo
 * EP). Sin tipos, los del servicio para cada activo (grupos 1, 2 y 4 en un perfil, RG-03 en un aislador,
 * RP-06 en un seccionador con motor); los elegidos van a todo lo generado. {@code dryRun} no crea nada:
 * dice que pasaria. Lo vacio no viaja.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateTasksRequest(List<String> taskTypeCodes, Boolean withChecklist, List<CatenaryAssetType> assetTypes,
                                   List<GenerationSelection> selections, Boolean dryRun) {

    /** Lo que se genera con los aisladores: los perfiles y ellos. */
    public static final List<CatenaryAssetType> WITH_INSULATORS = List.of(CatenaryAssetType.PROFILE, CatenaryAssetType.SECTION_INSULATOR);

    /** La peticion de siempre, sobre el tramo de la orden. */
    public static GenerateTasksRequest ofSection(List<String> taskTypeCodes, Boolean withChecklist, List<CatenaryAssetType> assetTypes,
                                                 Boolean dryRun) {
        return new GenerateTasksRequest(taskTypeCodes, withChecklist, assetTypes, null, dryRun);
    }
}

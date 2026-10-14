package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Una parte de lo que genera un preventivo: los activos de un tipo en un sitio del EP de la orden.
 * El sitio es uno de cuatro: {@code sectionId} (otro tramo del EP), {@code trackId} con KP opcionales
 * (lo de esa via y lo que conecta con ella), {@code assetIds} (los elegidos) o ninguno (todo el EP);
 * {@code stationId} filtra cualquiera salvo los elegidos. Sus tipos, si los lleva, son solo para lo
 * suyo. Lo vacio no viaja.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerationSelection(CatenaryAssetType assetType, UUID sectionId, Long trackId, BigDecimal fromKp, BigDecimal toKp,
                                  Long stationId, List<UUID> assetIds, List<String> taskTypeCodes) {
}

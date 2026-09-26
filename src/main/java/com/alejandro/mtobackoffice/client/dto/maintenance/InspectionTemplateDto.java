package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.util.List;
import java.util.UUID;

/**
 * Una version de la plantilla de inspeccion de un tipo de activo. Una inspeccion copia los puntos
 * de la plantilla activa al crearse, asi que cambiar la plantilla no cambia las inspecciones hechas.
 */
public record InspectionTemplateDto(UUID id, CatenaryAssetType assetType, Integer version, String name, Boolean active,
                                    List<InspectionTemplateItemDto> items) {

    public InspectionTemplateDto {
        items = items == null ? List.of() : List.copyOf(items);
    }
}

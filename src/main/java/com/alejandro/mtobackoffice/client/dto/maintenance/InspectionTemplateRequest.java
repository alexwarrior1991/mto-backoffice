package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * La version siguiente de la plantilla de un tipo de activo: su nombre y la lista ENTERA de puntos, en
 * orden. Nace activa y sustituye a la activa. {@code replacesVersion} es el numero de la version activa
 * del tipo tal como se leyo (si ya es otra, 409 {@code CON-001}); no viaja si el tipo no tenia activa.
 * El numero de la version nueva lo pone el servicio: {@code version} no se manda.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InspectionTemplateRequest(CatenaryAssetType assetType, String name, List<InspectionTemplateItemRequest> items,
                                        Integer replacesVersion) {

    public InspectionTemplateRequest {
        items = items == null ? List.of() : List.copyOf(items);
    }
}

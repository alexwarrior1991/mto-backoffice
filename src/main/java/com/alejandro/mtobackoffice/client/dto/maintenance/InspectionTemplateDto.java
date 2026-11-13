package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.util.List;
import java.util.UUID;

/**
 * Una version de la plantilla de inspeccion de un tipo de activo. Una inspeccion copia los puntos
 * de la plantilla activa al crearse, asi que cambiar la plantilla no cambia las inspecciones hechas.
 *
 * <p>{@code version} es el <b>numero de la version</b> dentro de su tipo de activo, no el bloqueo
 * optimista: ese es {@code lockVersion}, que vuelve en un {@code PATCH} como se leyo. {@code version}
 * no se manda nunca. {@code audit} dice quien creo la version y cuando. Un servicio anterior, que no
 * deja escribir las plantillas, no manda ninguna de las dos claves: {@link #writable()} lo dice.</p>
 */
public record InspectionTemplateDto(UUID id, CatenaryAssetType assetType, Integer version, String name, Boolean active,
                                    List<InspectionTemplateItemDto> items, Long lockVersion, AuditDto audit) {

    public InspectionTemplateDto {
        items = items == null ? List.of() : List.copyOf(items);
    }

    /** Como la mandaba un servicio anterior: sin bloqueo ni auditoria. */
    public InspectionTemplateDto(UUID id, CatenaryAssetType assetType, Integer version, String name, Boolean active,
                                 List<InspectionTemplateItemDto> items) {
        this(id, assetType, version, name, active, items, null, null);
    }

    /** La activa de su tipo de activo; una sin la clave no lo es. */
    public boolean activeNow() {
        return Boolean.TRUE.equals(active);
    }

    /**
     * Se puede renombrar, activar o desactivar: trae su bloqueo (el servicio deja escribir plantillas) y
     * es de un tipo de activo que esta aplicacion conoce. Lo desconocido no abre nada.
     */
    public boolean writable() {
        return lockVersion != null && assetType != null && assetType != CatenaryAssetType.UNKNOWN;
    }
}

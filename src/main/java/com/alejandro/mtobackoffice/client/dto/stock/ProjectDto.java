package com.alejandro.mtobackoffice.client.dto.stock;

import com.alejandro.mtobackoffice.client.dto.AuditDto;
import java.util.UUID;

/**
 * Un proyecto del almacen. {@code sourceService} y {@code synchronizedFromMasterData} dicen si vino
 * de mto-configuration como paquete de ejecucion: entonces es de su origen, el servicio rechaza el
 * {@code PUT} (422 {@code PRJ-001}) y aqui se ensena en solo lectura.
 */
public record ProjectDto(UUID id, String code, String name, Boolean active, String sourceService,
                         Boolean synchronizedFromMasterData, AuditDto audit) {

    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    public boolean isSynchronized() {
        return Boolean.TRUE.equals(synchronizedFromMasterData);
    }

    public ProjectSummaryDto summary() {
        return new ProjectSummaryDto(id, code, name, active);
    }
}

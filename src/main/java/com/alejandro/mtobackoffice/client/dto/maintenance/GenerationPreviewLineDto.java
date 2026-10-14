package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Un activo de la previsualizacion de generar, en el orden en que se generaria: lo que llevaria su
 * tarea y lo que pasaria con el ({@code outcome}, tolerante), y con {@code IN_OTHER_ORDER} la orden donde
 * ya tiene trabajo pendiente.
 */
public record GenerationPreviewLineDto(UUID assetId, String code, String name, CatenaryAssetType type, Long trackId, Long connectedTrackId,
                                       BigDecimal kp, List<String> taskTypeCodes, Boolean withChecklist, GenerationOutcome outcome,
                                       String otherOrderCode) {

    public GenerationPreviewLineDto {
        taskTypeCodes = taskTypeCodes == null ? List.of() : List.copyOf(taskTypeCodes);
    }

    /** El codigo y, si lo hay, el nombre, como un activo. */
    public String label() {
        if (name == null || name.isBlank() || name.equals(code)) {
            return code == null ? "" : code;
        }
        return code == null ? name : code + " - " + name;
    }

    /** Lo que pasa con el, con la orden donde ya esta si es en otra. */
    public String outcomeLabel() {
        if (outcome == GenerationOutcome.IN_OTHER_ORDER && otherOrderCode != null) {
            return "En " + otherOrderCode;
        }
        return outcome == null ? "" : outcome.label();
    }
}

package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.AuditDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Una tarea de una orden: su numero en la orden, el activo (un perfil en un preventivo), los tipos de tarea y su checklist. */
public record TaskDto(UUID id, UUID orderId, Integer sequence, String description, MaintenanceTaskStatus status, String assignedUser,
                      AssetSummaryDto asset, UUID shiftId, Instant startedAt, Instant completedAt, String defectsFound, String notes,
                      List<String> photoRefs, List<String> taskTypeCodes, List<CheckItemDto> checkItems, AuditDto audit) {

    public TaskDto {
        photoRefs = photoRefs == null ? List.of() : List.copyOf(photoRefs);
        taskTypeCodes = taskTypeCodes == null ? List.of() : List.copyOf(taskTypeCodes);
        checkItems = checkItems == null ? List.of() : List.copyOf(checkItems);
    }

    public boolean isOpen() {
        return status != null && status.isOpen();
    }
}

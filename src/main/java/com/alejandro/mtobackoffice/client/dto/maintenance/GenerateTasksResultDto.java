package com.alejandro.mtobackoffice.client.dto.maintenance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Lo que genero el servicio, o con {@code dryRun} lo que generaria: tareas nuevas, lo que ya tenia
 * tarea en la orden ({@code skippedAssets}, y de ello los perfiles), el total y la estimacion de carga;
 * y lo creado por tipo de activo, lo saltado por estar en otra orden abierta, por ser de otro EP o por
 * no tener tipo de tarea, y con la previsualizacion cada activo. Lo que un servicio anterior no manda
 * es un {@code Integer} o una lista que se leen igual.
 */
public record GenerateTasksResultDto(int createdTasks, int skippedProfiles, int totalTasks, BigDecimal estimatedMinutes, int estimatedShifts,
                                     Integer skippedAssets, Map<CatenaryAssetType, Integer> createdByAssetType, Integer skippedInOtherOrders,
                                     List<String> otherOrderCodes, Integer skippedOtherPackage, Integer skippedWithoutTaskTypes,
                                     Boolean dryRun, List<GenerationPreviewLineDto> assets) {

    public GenerateTasksResultDto {
        createdByAssetType = createdByAssetType == null ? Map.of() : Map.copyOf(createdByAssetType);
        otherOrderCodes = otherOrderCodes == null ? List.of() : List.copyOf(otherOrderCodes);
        assets = assets == null ? List.of() : List.copyOf(assets);
    }

    /** Lo que ya tenia tarea: todos los activos si el servicio lo cuenta, si no los perfiles. */
    public int skipped() {
        return skippedAssets == null ? skippedProfiles : skippedAssets;
    }

    public int inOtherOrders() {
        return skippedInOtherOrders == null ? 0 : skippedInOtherOrders;
    }

    public int otherPackage() {
        return skippedOtherPackage == null ? 0 : skippedOtherPackage;
    }

    public int withoutTaskTypes() {
        return skippedWithoutTaskTypes == null ? 0 : skippedWithoutTaskTypes;
    }
}

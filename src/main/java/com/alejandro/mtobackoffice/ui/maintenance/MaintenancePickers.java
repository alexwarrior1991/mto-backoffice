package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.CatenaryAssetType;
import com.alejandro.mtobackoffice.client.dto.maintenance.TaskTypeDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.maintenance.AssetClient;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Desplegables de mantenimiento. Los activos son miles y se buscan en el servidor mientras se
 * escribe (por su nombre de campo, solo los activos); equipos y tipos de tarea son pocos y vienen de
 * {@link MaintenanceCatalogs}.
 */
final class MaintenancePickers {

    private static final List<String> BY_TRACK_AND_KP = List.of("trackId,asc", "startKp,asc");

    private MaintenancePickers() {
    }

    /** @param type solo los de ese tipo; {@code null}, cualquiera */
    static ComboBox<AssetSummaryDto> asset(String label, AssetClient assets, CatenaryAssetType type) {
        ComboBox<AssetSummaryDto> combo = new ComboBox<>(label);
        combo.setItemLabelGenerator(AssetSummaryDto::label);
        combo.setClearButtonVisible(true);
        combo.setPlaceholder("Escribe su nombre: 12-2.27, HSA-NS5...");
        combo.setItems(query -> {
            try {
                String text = query.getFilter().orElse("");
                return assets.search(new AssetFilter(type, null, null, null, true, text, null), query.getPage(), query.getPageSize(),
                        BY_TRACK_AND_KP).content().stream().map(AssetDto::summary);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        return combo;
    }

    /** Varios activos de un tipo, buscados en el servidor igual que {@link #asset}: los seccionadores de un turno. */
    static MultiSelectComboBox<AssetSummaryDto> assets(String label, AssetClient assets, CatenaryAssetType type) {
        MultiSelectComboBox<AssetSummaryDto> combo = new MultiSelectComboBox<>(label);
        combo.setItemLabelGenerator(AssetSummaryDto::label);
        combo.setClearButtonVisible(true);
        combo.setPlaceholder("Escribe su nombre");
        combo.setItems(query -> {
            try {
                String text = query.getFilter().orElse("");
                return assets.search(new AssetFilter(type, null, null, null, true, text, null), query.getPage(), query.getPageSize(),
                        BY_TRACK_AND_KP).content().stream().map(AssetDto::summary);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return Stream.empty();
            }
        });
        return combo;
    }

    /** Los equipos activos; el valor leido de una orden se ve aunque el equipo ya este retirado. */
    static ComboBox<TeamSummaryDto> team(String label, Collection<TeamDto> activeTeams) {
        ComboBox<TeamSummaryDto> combo = new ComboBox<>(label);
        combo.setItems(activeTeams.stream().map(TeamDto::summary).toList());
        combo.setItemLabelGenerator(TeamSummaryDto::label);
        combo.setClearButtonVisible(true);
        return combo;
    }

    static MultiSelectComboBox<TaskTypeDto> taskTypes(String label, Collection<TaskTypeDto> types) {
        MultiSelectComboBox<TaskTypeDto> combo = new MultiSelectComboBox<>(label);
        combo.setItems(List.copyOf(types));
        combo.setItemLabelGenerator(TaskTypeDto::label);
        combo.setClearButtonVisible(true);
        return combo;
    }
}

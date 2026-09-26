package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.StatusHistoryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.grid.Grid;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Los cambios de estado de una orden o de un defecto, como los guarda el servicio (el primero es
 * el alta). Es append-only y no se audita: es la otra mitad de la historia, junto a las revisiones.
 */
class StatusHistoryPanel extends LazyPanel {

    private final Supplier<List<StatusHistoryDto>> history;
    private final Grid<StatusHistoryDto> grid = new Grid<>();

    /** @param statusLabel como se nombra un estado que llega como texto */
    StatusHistoryPanel(String id, Supplier<List<StatusHistoryDto>> history, Function<String, String> statusLabel) {
        this.history = history;
        grid.setId(id);
        grid.addColumn(change -> Formats.dateTime(change.changedAt())).setHeader("Fecha").setKey("changedAt").setAutoWidth(true);
        grid.addColumn(change -> change.previousStatus() == null ? "" : statusLabel.apply(change.previousStatus())).setHeader("De")
                .setKey("from").setAutoWidth(true);
        grid.addColumn(change -> statusLabel.apply(change.newStatus())).setHeader("A").setKey("to").setAutoWidth(true);
        grid.addColumn(StatusHistoryDto::changedBy).setHeader("Por").setKey("changedBy").setAutoWidth(true);
        grid.addColumn(StatusHistoryDto::comment).setHeader("Comentario").setKey("comment").setFlexGrow(1);
        grid.setAllRowsVisible(true);
        add(grid);
    }

    @Override
    protected void load() {
        try {
            grid.setItems(history.get());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

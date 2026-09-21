package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.vaadin.flow.component.grid.Grid;

/** Las columnas del libro de movimientos, las mismas en la pantalla del libro y en la ficha de un material. */
public class MovementGrid extends Grid<MovementDto> {

    public MovementGrid(boolean withMaterial) {
        addColumn(dto -> StockFormats.dateTime(dto.occurredAt())).setHeader("Fecha").setKey("occurredAt").setSortProperty("occurredAt").setSortable(true).setAutoWidth(true);
        addColumn(dto -> dto.type() == null ? "" : dto.type().label()).setHeader("Tipo").setKey("type").setAutoWidth(true);
        if (withMaterial) {
            addColumn(dto -> dto.material() == null ? "" : dto.material().label()).setHeader("Material").setKey("material").setFlexGrow(1);
        }
        addColumn(dto -> dto.warehouse() == null ? "" : dto.warehouse().code()).setHeader("Almacen").setKey("warehouse").setAutoWidth(true);
        addColumn(dto -> StockFormats.quantity(dto.signedQuantity())).setHeader("Cantidad").setKey("quantity").setSortProperty("quantity").setSortable(true).setAutoWidth(true);
        addColumn(dto -> dto.supplier() != null ? dto.supplier().code() : dto.project() != null ? dto.project().code() : "")
                .setHeader("Proveedor / proyecto").setKey("counterpart").setAutoWidth(true);
        addColumn(dto -> dto.reservation() == null ? "" : StockFormats.quantity(dto.reservation().quantity()) + " reservados")
                .setHeader("Reserva").setKey("reservation").setAutoWidth(true);
        addColumn(dto -> dto.externalReference() == null ? "" : dto.externalReference()).setHeader("Referencia").setKey("externalReference").setAutoWidth(true);
        addColumn(dto -> dto.audit() == null || dto.audit().createdBy() == null ? "" : dto.audit().createdBy()).setHeader("Por").setKey("createdBy").setAutoWidth(true);
        setMultiSort(false);
    }
}

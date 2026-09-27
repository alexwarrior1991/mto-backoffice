package com.alejandro.mtobackoffice.client.dto.maintenance;

import com.alejandro.mtobackoffice.client.dto.ClientEnums;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

/**
 * La peticion que una linea de material mando al almacen y se quedo sin respuesta
 * ({@code stockRequestInDoubt}): una reserva o una salida que mto-stock quiza aplico. Hasta que
 * conteste, el servicio la repite antes de hacer nada mas con la linea, la reintenta solo cada 5
 * minutos y no deja cambiar lo que viaja en ella. Un valor que no se conoce cuenta como en duda, y no
 * abre nada.
 */
public enum StockRequestType {
    RESERVATION("Reserva sin respuesta"),
    OUTPUT("Salida sin respuesta"),
    UNKNOWN(ClientEnums.UNKNOWN_LABEL);

    private final String label;

    StockRequestType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static StockRequestType of(String value) {
        return ClientEnums.parse(StockRequestType.class, value, UNKNOWN);
    }

    public static List<StockRequestType> selectable() {
        return ClientEnums.selectable(StockRequestType.class, UNKNOWN);
    }
}

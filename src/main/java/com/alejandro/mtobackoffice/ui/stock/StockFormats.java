package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.ui.support.Formats;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Como se pintan cantidades y fechas del almacen: lo comun a todos los modulos, en {@link Formats}. */
public final class StockFormats {

    private StockFormats() {
    }

    /** Sin ceros de relleno: el servicio guarda seis decimales y devuelve {@code 10.000000}. */
    public static String quantity(BigDecimal value) {
        return Formats.quantity(value);
    }

    public static String dateTime(Instant instant) {
        return Formats.dateTime(instant);
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        return Formats.toInstant(dateTime);
    }

    /** El principio del dia, para un {@code dateFrom}. */
    public static Instant startOfDay(LocalDate date) {
        return Formats.startOfDay(date);
    }

    /** El ultimo instante del dia, para un {@code dateTo} inclusivo. */
    public static Instant endOfDay(LocalDate date) {
        return Formats.endOfDay(date);
    }
}

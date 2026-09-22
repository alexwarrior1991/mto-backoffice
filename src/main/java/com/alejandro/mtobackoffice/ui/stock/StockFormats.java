package com.alejandro.mtobackoffice.ui.stock;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Como se pintan cantidades y fechas del almacen; las fechas, en la zona del servidor. */
public final class StockFormats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private StockFormats() {
    }

    /** Sin ceros de relleno: el servicio guarda seis decimales y devuelve {@code 10.000000}. */
    public static String quantity(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    public static String dateTime(Instant instant) {
        return instant == null ? "" : DATE_TIME.format(instant);
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /** El principio del dia, para un {@code dateFrom}. */
    public static Instant startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    /** El ultimo instante del dia, para un {@code dateTo} inclusivo. */
    public static Instant endOfDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().minusMillis(1);
    }
}

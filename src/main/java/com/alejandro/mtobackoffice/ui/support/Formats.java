package com.alejandro.mtobackoffice.ui.support;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Como se pintan cantidades y fechas en cualquier modulo; las fechas, en la zona del servidor. Los
 * modulos que tienen formatos propios ({@code StockFormats}, {@code MaintenanceFormats}) delegan
 * aqui lo comun.
 */
public final class Formats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formats() {
    }

    /** Sin ceros de relleno: los servicios guardan varios decimales y devuelven {@code 10.000000}. */
    public static String quantity(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    public static String dateTime(Instant instant) {
        return instant == null ? "" : DATE_TIME.format(instant);
    }

    public static String date(LocalDate date) {
        return date == null ? "" : DATE.format(date);
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

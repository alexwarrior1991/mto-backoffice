package com.alejandro.mtobackoffice.ui.support;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Como se pintan cantidades y fechas en cualquier modulo; las fechas, en la zona del navegador de la
 * persona ({@link ClientTimeZone}), igual que en mto-frontend. Las conversiones entre lo que se
 * escribe en un {@code DatePicker} o un {@code DateTimePicker} y el {@code Instant} que viaja usan la
 * misma zona. Los modulos que tienen formatos propios ({@code StockFormats},
 * {@code MaintenanceFormats}) delegan aqui lo comun.
 */
public final class Formats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formats() {
    }

    /** Sin ceros de relleno: los servicios guardan varios decimales y devuelven {@code 10.000000}. */
    public static String quantity(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    public static String dateTime(Instant instant) {
        return dateTime(instant, DATE_TIME);
    }

    /** Un instante con otro patron, en la zona de la persona. */
    public static String dateTime(Instant instant, DateTimeFormatter pattern) {
        return instant == null ? "" : pattern.withZone(ClientTimeZone.current()).format(instant);
    }

    public static String date(LocalDate date) {
        return date == null ? "" : DATE.format(date);
    }

    /** Hoy, en la zona de la persona: lo que un {@code DatePicker} propone o no deja pasar. */
    public static LocalDate today() {
        return LocalDate.now(ClientTimeZone.current());
    }

    /** Ahora, en la zona de la persona, para un {@code DateTimePicker}. */
    public static LocalDateTime now() {
        return LocalDateTime.now(ClientTimeZone.current());
    }

    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ClientTimeZone.current()).toInstant();
    }

    /** Para un {@code DateTimePicker}: el instante en la zona de la persona. */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ClientTimeZone.current());
    }

    /** El principio del dia, para un {@code dateFrom}. */
    public static Instant startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay(ClientTimeZone.current()).toInstant();
    }

    /** El ultimo instante del dia, para un {@code dateTo} inclusivo. */
    public static Instant endOfDay(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay(ClientTimeZone.current()).toInstant().minusMillis(1);
    }
}

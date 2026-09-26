package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetDto;
import com.alejandro.mtobackoffice.ui.support.Formats;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Como se pintan KP, avances y fechas de mantenimiento: lo comun a todos los modulos, en {@link Formats}. */
public final class MaintenanceFormats {

    private MaintenanceFormats() {
    }

    /** Un KP sin ceros de relleno: el servicio lo guarda con tres decimales. */
    public static String kp(BigDecimal kp) {
        return Formats.quantity(kp);
    }

    /** {@code 12.1 - 13.45}, o un solo KP si el tramo es un punto (un perfil, un seccionador). */
    public static String kpRange(BigDecimal start, BigDecimal end) {
        if (start == null || end == null || start.compareTo(end) == 0) {
            return kp(start == null ? end : start);
        }
        return kp(start) + " - " + kp(end);
    }

    /**
     * Si un activo admite trabajo y, si no, quien lo desactivo: mantenimiento, mto-configuration o
     * los dos. Un servicio sin esas dos columnas solo dice «Desactivado».
     */
    public static String assetState(AssetDto asset) {
        if (asset.isEnabled()) {
            return "Activo";
        }
        if (asset.isDisabledLocally() && asset.isDisabledAtSource()) {
            return "Desactivado aqui y en configuracion";
        }
        if (asset.isDisabledLocally()) {
            return "Desactivado aqui";
        }
        return asset.isDisabledAtSource() ? "Desactivado en configuracion" : "Desactivado";
    }

    /** Tareas completadas sobre el total: {@code 3/10}; sin tareas, vacio. */
    public static String progress(int completed, int total) {
        return total == 0 ? "" : completed + "/" + total;
    }

    /** Una fraccion del servicio ({@code 0.4500}) como porcentaje: {@code 45 %}. */
    public static String percent(BigDecimal ratio) {
        return ratio == null ? "" : ratio.movePointRight(2).stripTrailingZeros().toPlainString() + " %";
    }

    public static String date(LocalDate date) {
        return Formats.date(date);
    }

    public static String dateTime(Instant instant) {
        return Formats.dateTime(instant);
    }
}

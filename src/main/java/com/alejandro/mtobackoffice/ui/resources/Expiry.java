package com.alejandro.mtobackoffice.ui.resources;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Como se dice una caducidad (una cualificacion, un documento de un vehiculo, la calibracion de un
 * instrumento), igual que en mto-frontend: «Caducada» cuando el servicio lo dice, «Caduca en N dias»
 * mientras falten 30 o menos, y «Vigente» mas alla; sin fecha, lo que diga quien llama («No caduca»,
 * «Sin calibracion»). Si ha caducado lo decide el servicio, con su fecha y su zona ({@code expired}, o
 * {@code calibrationExpired}); los dias que faltan son solo presentacion. Lo que no entra en un turno lo
 * dice mto-maintenance a la fecha del turno (409 {@code SHF-001}), no esta pantalla.
 */
public final class Expiry {

    /** Desde cuantos dias antes se avisa, los mismos que mto-resources ({@code app.resources.expiry.warning-days}). */
    public static final int WARNING_DAYS = 30;
    public static final String VALID = "Vigente";

    private Expiry() {
    }

    /** El estado de una caducidad a dia de hoy, en la zona del servidor. */
    public static String state(LocalDate expiresOn, Boolean expired, String expiredLabel, String withoutDate) {
        return state(expiresOn, expired, LocalDate.now(), expiredLabel, withoutDate);
    }

    /**
     * El estado frente a {@code today}. Un servicio que todavia no dice {@code expired} se lee por la
     * fecha; uno que dice que no ha caducado con la fecha ya pasada (otra zona) caduca hoy.
     */
    public static String state(LocalDate expiresOn, Boolean expired, LocalDate today, String expiredLabel, String withoutDate) {
        if (expiresOn == null) {
            return withoutDate;
        }
        if (expired == null ? expiresOn.isBefore(today) : expired) {
            return expiredLabel;
        }
        long days = Math.max(0, ChronoUnit.DAYS.between(today, expiresOn));
        if (days == 0) {
            return "Caduca hoy";
        }
        if (days > WARNING_DAYS) {
            return VALID;
        }
        return days == 1 ? "Caduca en 1 dia" : "Caduca en " + days + " dias";
    }
}

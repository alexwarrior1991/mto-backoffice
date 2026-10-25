package com.alejandro.mtobackoffice.configuration.vaadin;

import com.alejandro.mtobackoffice.ui.support.ClientTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;

/**
 * La zona por defecto de {@link ClientTimeZone} ({@code app.display.time-zone}): la que vale cuando
 * el navegador no dice la suya. Una zona que no existe impide arrancar.
 */
@Configuration
public class DisplayTimeZoneConfiguration {

    public DisplayTimeZoneConfiguration(@Value("${app.display.time-zone:Asia/Jerusalem}") String timeZone) {
        ClientTimeZone.useAsFallback(ZoneId.of(timeZone.trim()));
    }
}

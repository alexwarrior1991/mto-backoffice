package com.alejandro.mtobackoffice.configuration.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Cabecera de correlacion ({@code app.correlation.*}). Por defecto la misma que acepta y propaga el
 * gateway; cada llamada saliente lleva un id nuevo y ese id es el que aparece en el log del gateway
 * y en el del servicio.
 */
@Validated
@ConfigurationProperties(prefix = "app.correlation")
public record CorrelationProperties(@DefaultValue("X-Correlation-Id") @NotBlank String headerName) {
}

package com.alejandro.mtobackoffice.configuration.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * El gateway ({@code app.gateway.*}): toda llamada sale por el, nunca directamente a un servicio.
 *
 * @param baseUrl raiz del gateway (en local el 8090; en compose, el nombre del servicio)
 */
@Validated
@ConfigurationProperties(prefix = "app.gateway")
public record GatewayProperties(@NotBlank String baseUrl) {
}

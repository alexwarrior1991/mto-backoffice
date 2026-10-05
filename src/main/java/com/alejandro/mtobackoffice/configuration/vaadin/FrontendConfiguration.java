package com.alejandro.mtobackoffice.configuration.vaadin;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Registra {@link FrontendProperties}, la direccion de la SPA para el enlace de la barra. */
@Configuration
@EnableConfigurationProperties(FrontendProperties.class)
public class FrontendConfiguration {
}

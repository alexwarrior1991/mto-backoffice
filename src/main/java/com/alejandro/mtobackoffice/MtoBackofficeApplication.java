package com.alejandro.mtobackoffice;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Backoffice web del dominio MTO: un cliente Vaadin de las APIs publicadas por mto-gateway. Sin
 * base de datos, sin broker y sin logica de negocio: lo que la API no da bien se arregla en el
 * servicio, no aqui.
 *
 * <p>{@code @Push} (core, Apache 2.0) permite refrescar la UI desde un hilo de fondo —el progreso
 * de una importacion— sin sondear desde el navegador. Va aqui porque el shell de la aplicacion es
 * el unico sitio donde Vaadin lo admite.</p>
 *
 * <p>El tema tambien se declara aqui. En Vaadin 25 cargarlo es explicito: un shell sin
 * {@code @StyleSheet} (ni {@code @Theme}) deja la aplicacion con la letra y los colores del
 * navegador. {@link Lumo#STYLESHEET} es Lumo, y {@link Lumo#UTILITY_STYLESHEET} las clases de
 * {@code LumoUtility} que usan las vistas, que sin esa hoja no pintan nada.</p>
 */
@SpringBootApplication
@Push
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
public class MtoBackofficeApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MtoBackofficeApplication.class, args);
    }
}

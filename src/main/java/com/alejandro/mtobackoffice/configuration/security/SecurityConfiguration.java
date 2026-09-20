package com.alejandro.mtobackoffice.configuration.security;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad de la aplicacion web: todo requiere haber entrado, y se entra con Authorization Code
 * en Keycloak.
 *
 * <p>Las reglas de las vistas van en las propias vistas ({@code @RolesAllowed}, {@code @PermitAll})
 * y las aplica el control de navegacion de Vaadin, que {@link VaadinSecurityConfigurer} activa. Lo
 * que se decide aqui es lo que va por HTTP: el login OIDC, el logout federado (lo crea el
 * configurador porque hay un {@code ClientRegistrationRepository}) y las sondas de salud.</p>
 *
 * <p>{@link VaadinAwareSecurityContextHolderStrategyConfiguration} hace que el contexto de seguridad
 * este disponible en los hilos de Vaadin ({@code UI.access()}, push); la autoconfiguracion de
 * vaadin-spring ya la registra, el import la deja explicita.</p>
 */
@Configuration
@EnableWebSecurity
@Import(VaadinAwareSecurityContextHolderStrategyConfiguration.class)
public class SecurityConfiguration {

    /** Ruta que inicia el flujo de codigo; es la "pagina de login" para Vaadin. */
    public static final String LOGIN_URL = "/oauth2/authorization/" + KeycloakClientRegistrations.REGISTRATION_ID;

    @Bean
    public KeycloakRoleMapper keycloakRoleMapper(KeycloakProperties properties) {
        return new KeycloakRoleMapper(properties.rolesClientId());
    }

    @Bean
    public BackofficeOidcUserService backofficeOidcUserService(KeycloakRoleMapper roleMapper) {
        return new BackofficeOidcUserService(roleMapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, BackofficeOidcUserService oidcUserService)
            throws Exception {
        // Las sondas las consulta compose y el CI sin token. Van antes del configurador de Vaadin,
        // que cierra el resto con anyRequest().authenticated().
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll());
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService)));
        http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> vaadin.oauth2LoginPage(LOGIN_URL));
        return http.build();
    }
}

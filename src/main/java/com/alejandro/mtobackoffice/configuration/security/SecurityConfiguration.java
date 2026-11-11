package com.alejandro.mtobackoffice.configuration.security;

import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

/**
 * Seguridad de la aplicacion web: todo requiere haber entrado, y se entra con Authorization Code
 * en Keycloak.
 *
 * <p>Las reglas de las vistas van en las propias vistas ({@code @RolesAllowed}, {@code @PermitAll})
 * y las aplica el control de navegacion de Vaadin, que {@link VaadinSecurityConfigurer} activa. Lo
 * que se decide aqui es lo que va por HTTP: el login OIDC, el logout federado (lo crea el
 * configurador porque hay un {@code ClientRegistrationRepository}), las sondas de salud y que las
 * autoridades de la sesion sigan al ultimo access token ({@link LiveAuthorities}).</p>
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
        return new KeycloakRoleMapper(properties.rolesClientIds());
    }

    @Bean
    public AccessTokenReader accessTokenReader(KeycloakRoleMapper roleMapper) {
        return new AccessTokenReader(roleMapper);
    }

    @Bean
    public LiveAuthorities liveAuthorities(AccessTokenReader accessTokens) {
        return new LiveAuthorities(accessTokens);
    }

    @Bean
    public BackofficeOidcUserService backofficeOidcUserService(AccessTokenReader accessTokens, LiveAuthorities liveAuthorities) {
        return new BackofficeOidcUserService(accessTokens, liveAuthorities);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, BackofficeOidcUserService oidcUserService,
                                                   LiveAuthorities liveAuthorities) throws Exception {
        // Las sondas las consulta compose y el CI sin token. Van antes del configurador de Vaadin,
        // que cierra el resto con anyRequest().authenticated().
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll());
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService)));
        // Los permisos siguen al ultimo access token de la persona: cada peticion empieza poniendo al dia
        // las autoridades de su sesion, ya cargada por SecurityContextHolderFilter.
        http.addFilterAfter(new LiveAuthoritiesFilter(liveAuthorities), SecurityContextHolderFilter.class);
        http.with(VaadinSecurityConfigurer.vaadin(), vaadin -> vaadin.oauth2LoginPage(LOGIN_URL));
        return http.build();
    }
}

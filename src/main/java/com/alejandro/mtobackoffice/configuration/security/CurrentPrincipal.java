package com.alejandro.mtobackoffice.configuration.security;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Nombre del principal en cuyo nombre sale una llamada, resuelto desde cualquier hilo.
 *
 * <p>El token se pide por nombre de principal ({@code AuthorizedClientServiceOAuth2AuthorizedClientManager}),
 * asi que lo unico que hace falta llevar a un hilo es ese nombre. Se resuelve, por este orden:</p>
 * <ol>
 *   <li>el fijado explicitamente con {@link #runAs} / {@link #callAs} —hilos de fondo: sondeo de
 *       trabajos, tareas que luego actualizan la UI con {@code UI.access()}—;</li>
 *   <li>el {@code SecurityContextHolder}, que con la estrategia Vaadin-aware vale tanto en una
 *       peticion como dentro de {@code UI.access()};</li>
 *   <li>el guardado en la {@code VaadinSession} al entrar, como respaldo.</li>
 * </ol>
 */
public final class CurrentPrincipal {

    private static final ScopedValue<String> EXPLICIT = ScopedValue.newInstance();

    static final String SESSION_ATTRIBUTE = CurrentPrincipal.class.getName() + ".name";

    private CurrentPrincipal() {
    }

    public static Optional<String> name() {
        if (EXPLICIT.isBound()) {
            return Optional.of(EXPLICIT.get());
        }
        Optional<String> authenticated = fromSecurityContext();
        if (authenticated.isPresent()) {
            return authenticated;
        }
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null && session.getAttribute(SESSION_ATTRIBUTE) instanceof String name) {
            return Optional.of(name);
        }
        return Optional.empty();
    }

    /** Guarda en la sesion de Vaadin el principal del contexto de seguridad actual, si lo hay. */
    public static void remember(VaadinSession session) {
        fromSecurityContext().ifPresent(name -> session.setAttribute(SESSION_ATTRIBUTE, name));
    }

    public static void runAs(String principalName, Runnable task) {
        ScopedValue.where(EXPLICIT, principalName).run(task);
    }

    public static <T> T callAs(String principalName, Supplier<T> task) {
        return ScopedValue.where(EXPLICIT, principalName).call(task::get);
    }

    private static Optional<String> fromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return Optional.ofNullable(authentication.getName()).filter(name -> !name.isBlank());
    }
}

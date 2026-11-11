package com.alejandro.mtobackoffice.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Al empezar cada peticion, pone la sesion al dia con el ultimo access token de su persona
 * ({@link LiveAuthorities#sync()}): asi la navegacion, {@code @RolesAllowed} y los botones de esta
 * ida y vuelta ven los permisos de ahora, tambien si el token lo renovo otro hilo u otra sesion.
 * Va detras del filtro que carga el contexto de seguridad de la sesion, y cambia ese mismo contexto.
 */
public class LiveAuthoritiesFilter extends OncePerRequestFilter {

    private final LiveAuthorities liveAuthorities;

    public LiveAuthoritiesFilter(LiveAuthorities liveAuthorities) {
        this.liveAuthorities = liveAuthorities;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        liveAuthorities.sync();
        chain.doFilter(request, response);
    }
}

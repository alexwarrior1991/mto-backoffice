package com.alejandro.mtobackoffice.ui.support;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Un hilo compartido para lo que se vuelve a preguntar al servicio mientras una pantalla esta
 * abierta: el estado de los trabajos en curso ({@code JobsView}) y el contador de la campana
 * ({@code InboxBell}). Es lo que hace util {@code @Push}: el servicio se pregunta desde aqui, fuera
 * de cualquier peticion del navegador, y la respuesta se lleva a la pantalla con
 * {@code UI.access()}. Cada pantalla se apunta al entrar y se borra al salir; sin pantallas
 * abiertas no se consulta nada. El principal se fija en la tarea con {@code CurrentPrincipal.callAs}.
 */
@Component
public class SharedPolling {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedPolling.class);

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "shared-polling");
        thread.setDaemon(true);
        return thread;
    });

    /** Ejecuta la tarea cada {@code period}; un fallo se registra y no para el ciclo. */
    public ScheduledFuture<?> every(Duration period, Runnable task) {
        return executor.scheduleWithFixedDelay(() -> {
            try {
                task.run();
            } catch (RuntimeException failure) {
                LOGGER.warn("La consulta en segundo plano ha fallado: {}", failure.getMessage());
            }
        }, period.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    void shutdown() {
        executor.shutdownNow();
    }
}

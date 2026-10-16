package com.alejandro.mtobackoffice.ui.support;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.thread.Threading;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Lo que se vuelve a preguntar al servicio mientras una pantalla esta abierta: el estado de los
 * trabajos en curso ({@code JobsView}) y el contador de la campana ({@code InboxBell}). Es lo que hace
 * util {@code @Push}: el servicio se pregunta desde aqui, fuera de cualquier peticion del navegador, y
 * la respuesta se lleva a la pantalla con {@code UI.access()}. Cada pantalla se apunta al entrar y se
 * borra al salir; sin pantallas abiertas no se consulta nada. El principal se fija en la tarea con
 * {@code CurrentPrincipal.callAs}.
 *
 * <p>Un solo hilo, {@code shared-polling}, lleva el reloj de todas. Con los hilos virtuales activos
 * ({@code spring.threads.virtual.enabled}) cada pasada corre ademas en un hilo virtual propio
 * ({@code shared-polling-N}): una llamada lenta al gateway (hasta 15 s) solo retrasa su pantalla, y no
 * la de todas las demas, como cuando las pasadas se hacian una detras de otra en el hilo del reloj. La
 * pasada de una pantalla nunca se pisa consigo misma: si la anterior sigue esperando, ese tic se
 * salta. Sin los hilos virtuales, las pasadas vuelven a correr en el hilo del reloj, como antes.</p>
 */
@Component
public class SharedPolling {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedPolling.class);

    private final ScheduledExecutorService clock = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "shared-polling");
        thread.setDaemon(true);
        return thread;
    });

    /** Un hilo virtual por pasada; {@code null} sin hilos virtuales, y entonces la pasada corre en el reloj. */
    private final ExecutorService passes;

    public SharedPolling(Environment environment) {
        this.passes = Threading.VIRTUAL.isActive(environment)
                ? Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("shared-polling-", 0).factory())
                : null;
    }

    /** Ejecuta la tarea cada {@code period}; un fallo se registra y no para el ciclo. */
    public ScheduledFuture<?> every(Duration period, Runnable task) {
        Runnable pass = () -> {
            try {
                task.run();
            } catch (RuntimeException failure) {
                LOGGER.warn("La consulta en segundo plano ha fallado: {}", failure.getMessage());
            }
        };
        Runnable tick = passes == null ? pass : onItsOwnThread(pass);
        return clock.scheduleWithFixedDelay(tick, period.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
    }

    /** La pasada en un hilo virtual, saltando el tic mientras la anterior de la misma pantalla no ha terminado. */
    private Runnable onItsOwnThread(Runnable pass) {
        AtomicBoolean running = new AtomicBoolean();
        return () -> {
            if (!running.compareAndSet(false, true)) {
                return;
            }
            try {
                passes.execute(() -> {
                    try {
                        pass.run();
                    } finally {
                        running.set(false);
                    }
                });
            } catch (RejectedExecutionException shuttingDown) {
                running.set(false);
            }
        };
    }

    /** Para el reloj y las pasadas en curso; lo llama Spring al cerrar, y los tests con la suya. */
    @PreDestroy
    public void shutdown() {
        clock.shutdownNow();
        if (passes != null) {
            passes.shutdownNow();
        }
    }
}

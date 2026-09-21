package com.alejandro.mtobackoffice.ui.jobs;

import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.vaadin.flow.server.VaadinSession;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Los trabajos lanzados desde esta sesion, del mas reciente al mas antiguo. Vive en la
 * {@link VaadinSession}, no en la vista: navegar a otra pantalla y volver no pierde la lista, y
 * un trabajo sigue corriendo en el servicio aunque nadie lo mire.
 *
 * <p>El servicio no tiene endpoint de listado de trabajos: solo se puede consultar uno por id. Por
 * eso el historial es el de la sesion; al cerrarla se pierde la lista, no los trabajos.</p>
 */
public final class JobLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Un trabajo de la lista: como lo describio quien lo lanzo y su ultimo estado conocido. */
    public record Entry(String label, JobDto job, Instant launchedAt) implements Serializable {

        Entry with(JobDto updated) {
            return new Entry(label, updated, launchedAt);
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    /** La lista de la sesion actual, creandola la primera vez. Se llama con la sesion bloqueada. */
    public static JobLog of(VaadinSession session) {
        JobLog log = session.getAttribute(JobLog.class);
        if (log == null) {
            log = new JobLog();
            session.setAttribute(JobLog.class, log);
        }
        return log;
    }

    public synchronized void track(String label, JobDto job) {
        entries.removeIf(entry -> entry.job().id().equals(job.id()));
        entries.addFirst(new Entry(label, job, Instant.now()));
    }

    /** Reemplaza el estado del trabajo con ese id; uno que no este en la lista se ignora. */
    public synchronized void update(JobDto job) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).job().id().equals(job.id())) {
                entries.set(i, entries.get(i).with(job));
            }
        }
    }

    public synchronized List<Entry> entries() {
        return List.copyOf(entries);
    }

    /** Los que todavia pueden cambiar de estado: los unicos que merece la pena consultar. */
    public synchronized List<Entry> active() {
        return entries.stream().filter(entry -> !entry.job().isTerminal()).toList();
    }
}

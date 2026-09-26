package com.alejandro.mtobackoffice.ui.jobs;

import com.alejandro.mtobackoffice.client.configuration.BusinessEntityClient;
import com.alejandro.mtobackoffice.client.configuration.ExecutionPackageClient;
import com.alejandro.mtobackoffice.client.configuration.JobsClient;
import com.alejandro.mtobackoffice.client.configuration.StationClient;
import com.alejandro.mtobackoffice.client.configuration.TrackClient;
import com.alejandro.mtobackoffice.client.dto.PageMetadata;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.jobs.JobDto;
import com.alejandro.mtobackoffice.client.dto.jobs.JobStatus;
import com.alejandro.mtobackoffice.client.dto.jobs.JobType;
import com.alejandro.mtobackoffice.client.dto.jobs.UploadedFile;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.TooManyRequestsApiException;
import com.alejandro.mtobackoffice.configuration.security.CurrentPrincipal;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.master.ReferenceCatalog;
import com.alejandro.mtobackoffice.ui.support.Downloads;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Los trabajos en segundo plano de mto-configuration: lanzarlos y seguirlos.
 *
 * <p>Lanzar es una llamada que responde 202 con el trabajo (o 429 con el trabajo ya rechazado y un
 * {@code Retry-After}); a partir de ahi el servicio trabaja solo. La lista es la del servicio
 * ({@code GET /jobs}, todas las familias, del mas reciente al mas antiguo, paginada y filtrable por
 * tipo y estado), asi que se ven tambien los trabajos lanzados desde otra sesion o antes de un
 * reinicio; lo que solo sabe esta sesion —con que etiqueta lanzo cada uno— lo guarda {@link JobLog}
 * y se pinta encima. Seguirlos es lo que hace {@code @Push}: mientras esta pantalla esta abierta y
 * hay algo en curso, un hilo compartido ({@link JobPolling}) vuelve a pedir la pagina cada pocos
 * segundos y lleva lo que cambio a la pantalla con {@code UI.access()}, sin que el navegador
 * pregunte; un trabajo de esta sesion que no este en la pagina se consulta por su familia.</p>
 *
 * <p>El fichero de un trabajo se descarga a traves de esta aplicacion ({@link DownloadHandler}): el
 * token nunca llega al navegador, asi que el navegador no puede pedirlo al gateway. Los permisos
 * son los del servicio: exportar y consultar, {@code config-read}; importar y republicar,
 * {@code config-import}; el catalogo de LOV, ademas {@code lov-manage}.</p>
 */
@Route(value = JobsView.ROUTE, layout = MainLayout.class)
@PageTitle("Trabajos")
@Menu(title = "Trabajos", order = 30, icon = "vaadin:cogs")
@RolesAllowed(SecurityRoles.CONFIG_READ)
public class JobsView extends VerticalLayout {

    public static final String ROUTE = "trabajos";
    static final Duration POLL_PERIOD = Duration.ofSeconds(2);
    static final int PAGE_SIZE = 20;
    static final int MAX_UPLOAD_BYTES = 20 * 1024 * 1024;
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static final List<String> MAPPER_TYPES = List.of("basic", "default", "technical");
    static final Map<String, String> REPUBLISH_TARGETS = Map.of(
            "profile", "Perfiles", "disconnector", "Seccionadores", "section-insulator", "Aisladores de seccion", "all", "Todo");

    private static final Logger LOGGER = LoggerFactory.getLogger(JobsView.class);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss").withZone(ZoneId.systemDefault());

    /** Lo ultimo que se pidio al servicio: pagina y filtros. Lo lee tambien el hilo de consulta. */
    private record Query(int page, JobType type, JobStatus status) {
    }

    private final JobsClient client;
    private final ObjectMapper objectMapper;
    private final JobPolling polling;
    private final ReferenceCatalog catalog;
    private final AuthenticationContext authentication;
    private final boolean canImport;
    private final boolean canManageLovs;

    private final Grid<JobDto> grid = new Grid<>();
    private final Span count = new Span();
    private final Span pageInfo = new Span();
    private final Button previous = new Button("Anteriores", VaadinIcon.ANGLE_LEFT.create());
    private final Button next = new Button("Siguientes", VaadinIcon.ANGLE_RIGHT.create());
    private final ComboBox<JobType> typeFilter = new ComboBox<>("Tipo");
    private final ComboBox<JobStatus> statusFilter = new ComboBox<>("Estado");
    private JobLog log;
    private String principal;
    private ScheduledFuture<?> ticker;
    private int currentPage;
    private volatile Query lastQuery = new Query(0, null, null);
    private volatile List<JobDto> rows = List.of();

    private UploadedFile profileMaster;
    private UploadedFile lovMaster;

    public JobsView(JobsClient client, ExecutionPackageClient packages, StationClient stations, TrackClient tracks,
                    BusinessEntityClient companies, JobPolling polling, AuthenticationContext authentication,
                    ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.polling = polling;
        this.authentication = authentication;
        this.catalog = new ReferenceCatalog(packages, stations, tracks, companies);
        this.canImport = authentication.hasRole(SecurityRoles.CONFIG_IMPORT);
        this.canManageLovs = authentication.hasRole(SecurityRoles.LOV_MANAGE);
        setSizeFull();

        FlexLayout launchers = new FlexLayout(exportCard());
        if (canImport) {
            launchers.add(importCard("Importar el maestro de perfiles", "profile-master.xlsx: paquetes, estaciones, vias, perfiles y mensulas",
                    "import-profiles", file -> profileMaster = file, () -> profileMaster,
                    (file, dryRun) -> client.importProfiles(file.asResource(), dryRun)));
            if (canManageLovs) {
                launchers.add(importCard("Importar el catalogo de LOV", "lov-master.xlsx: las 17 listas de valores por codigo",
                        "import-lovs", file -> lovMaster = file, () -> lovMaster,
                        (file, dryRun) -> client.importLovs(file.asResource(), dryRun)));
            }
            launchers.add(republishCard());
        }
        launchers.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        launchers.addClassNames(LumoUtility.Gap.MEDIUM);

        add(new H2("Trabajos"), launchers, listHeader(), buildGrid());
        expand(grid);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        log = JobLog.of(attachEvent.getSession());
        principal = authentication.getPrincipalName().orElse(null);
        UI ui = attachEvent.getUI();
        load();
        ticker = polling.every(POLL_PERIOD, () -> poll(ui));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (ticker != null) {
            ticker.cancel(false);
            ticker = null;
        }
        super.onDetach(detachEvent);
    }

    // --- Lanzadores -----------------------------------------------------------------------------

    private Component exportCard() {
        ComboBox<RefItem> track = Pickers.reference("Via", catalog.tracks());
        track.setId("export-track");
        Select<String> mapper = new Select<>();
        mapper.setLabel("Formato");
        mapper.setItems(MAPPER_TYPES);
        mapper.setValue(MAPPER_TYPES.getFirst());
        Button export = new Button("Exportar", VaadinIcon.DOWNLOAD.create());
        export.setId("export-profiles");
        export.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        export.addClickListener(click -> {
            RefItem selected = track.getValue();
            if (selected == null) {
                track.setErrorMessage("Elige una via");
                track.setInvalid(true);
                return;
            }
            launch("Exportacion de " + selected.label(), () -> client.exportProfiles(selected.id(), mapper.getValue()));
        });
        return card("Exportar los perfiles de una via", "Un CSV con todos los perfiles de la via, en el formato elegido",
                track, mapper, export);
    }

    private interface ImportCall {
        JobDto submit(UploadedFile file, boolean dryRun);
    }

    private Component importCard(String title, String hint, String buttonId,
                                 Consumer<UploadedFile> keep, Supplier<UploadedFile> loaded, ImportCall call) {
        Button start = new Button("Importar", VaadinIcon.UPLOAD.create());
        start.setId(buttonId);
        start.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        start.setEnabled(false);
        Upload upload = new Upload(UploadHandler.inMemory((metadata, data) ->
                whenAttached(ui -> ui.access(() -> {
                    keep.accept(new UploadedFile(metadata.fileName(), metadata.contentType(), data));
                    start.setEnabled(true);
                }))));
        upload.setId(buttonId + "-upload");
        upload.setAcceptedFileTypes(".xlsx", XLSX);
        upload.setMaxFiles(1);
        upload.setMaxFileSize(MAX_UPLOAD_BYTES);
        Checkbox dryRun = new Checkbox("Simulacion: no escribe nada, solo el informe");
        start.addClickListener(click -> {
            UploadedFile file = loaded.get();
            if (file == null) {
                return;
            }
            boolean simulated = Boolean.TRUE.equals(dryRun.getValue());
            String label = title.replace("Importar el ", "Importacion del ") + " (" + file.fileName() + (simulated ? ", simulacion" : "") + ")";
            if (launch(label, () -> call.submit(file, simulated))) {
                upload.clearFileList();
                keep.accept(null);
                start.setEnabled(false);
            }
        });
        return card(title, hint, upload, dryRun, start);
    }

    private Component republishCard() {
        Select<String> target = new Select<>();
        target.setLabel("Que republicar");
        target.setItems(List.of("profile", "disconnector", "section-insulator", "all"));
        target.setItemLabelGenerator(REPUBLISH_TARGETS::get);
        target.setValue("profile");
        ComboBox<RefItem> track = Pickers.reference("Solo la via", catalog.tracks());
        ComboBox<RefItem> station = Pickers.reference("Solo la estacion", catalog.stations());
        station.setEnabled(false);
        target.addValueChangeListener(change -> {
            boolean profiles = "profile".equals(change.getValue());
            boolean stationScoped = "disconnector".equals(change.getValue()) || "section-insulator".equals(change.getValue());
            track.setEnabled(profiles);
            station.setEnabled(stationScoped);
            if (!profiles) {
                track.clear();
            }
            if (!stationScoped) {
                station.clear();
            }
        });
        Button republish = new Button("Republicar", VaadinIcon.REFRESH.create());
        republish.setId("republish");
        republish.addClickListener(click -> {
            String entity = target.getValue();
            Long trackId = track.getValue() == null ? null : track.getValue().id();
            Long stationId = station.getValue() == null ? null : station.getValue().id();
            launch("Republicado de " + REPUBLISH_TARGETS.get(entity).toLowerCase(Locale.ROOT),
                    () -> client.republish(entity, trackId, stationId));
        });
        return card("Republicar datos maestros", "Vuelve a emitir los eventos de lo que ya existia antes de que hubiera consumidores",
                target, track, station, republish);
    }

    private static Component card(String title, String hint, Component... content) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_20, LumoUtility.BorderRadius.MEDIUM);
        card.setWidth("22rem");
        card.setSpacing(false);
        H3 heading = new H3(title);
        heading.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.NONE);
        Span help = new Span(hint);
        help.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        card.add(heading, help);
        card.add(content);
        return card;
    }

    /**
     * Lanza el trabajo, lo apunta con su etiqueta y vuelve a la primera pagina, donde ya esta. Un
     * 429 tambien se apunta: el servicio persiste el trabajo como rechazado y lo devuelve en el
     * cuerpo, y la persona tiene que saber que no va a correr y cuando volver a intentarlo.
     *
     * @return {@code true} si el servicio acepto el trabajo
     */
    boolean launch(String label, Supplier<JobDto> call) {
        try {
            JobDto job = call.get();
            log.track(label, job);
            firstPage();
            Notification.show("Trabajo encolado: " + label, 4000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            return true;
        } catch (TooManyRequestsApiException tooMany) {
            rejectedJob(tooMany).ifPresent(job -> log.track(label, job));
            firstPage();
            String when = tooMany.getRetryAfter().map(retry -> " Intentalo en " + retry.toSeconds() + " s.").orElse(" Intentalo mas tarde.");
            Notification.show("Sin hueco para " + label + ": el servicio lo ha rechazado." + when, 8000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return false;
        }
    }

    private Optional<JobDto> rejectedJob(TooManyRequestsApiException tooMany) {
        try {
            JobDto job = objectMapper.readValue(tooMany.getBody(), JobDto.class);
            return Optional.ofNullable(job).filter(candidate -> candidate.id() != null);
        } catch (RuntimeException notAJob) {
            LOGGER.debug("El cuerpo del 429 no es un trabajo: {}", notAJob.getMessage());
            return Optional.empty();
        }
    }

    // --- La lista -------------------------------------------------------------------------------

    private Component listHeader() {
        typeFilter.setId("jobs-type");
        typeFilter.setItems(JobType.selectable());
        typeFilter.setItemLabelGenerator(JobType::label);
        typeFilter.setClearButtonVisible(true);
        typeFilter.addValueChangeListener(change -> firstPage());
        statusFilter.setId("jobs-status");
        statusFilter.setItems(JobStatus.selectable());
        statusFilter.setItemLabelGenerator(JobStatus::label);
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(change -> firstPage());
        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> load());
        previous.setId("jobs-previous");
        previous.addClickListener(click -> {
            if (currentPage > 0) {
                currentPage--;
                load();
            }
        });
        next.setId("jobs-next");
        next.addClickListener(click -> {
            currentPage++;
            load();
        });
        pageInfo.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);
        HorizontalLayout header = new HorizontalLayout(new H3("Historial"), count, typeFilter, statusFilter, reload, previous, pageInfo, next);
        header.setAlignItems(FlexComponent.Alignment.BASELINE);
        header.setWidthFull();
        header.expand(count);
        return header;
    }

    private void firstPage() {
        currentPage = 0;
        load();
    }

    /** Pide al servicio la pagina actual con los filtros de la pantalla. En el hilo de la UI. */
    void load() {
        Query query = new Query(currentPage, typeFilter.getValue(), statusFilter.getValue());
        lastQuery = query;
        try {
            show(query, client.list(query.page(), PAGE_SIZE, query.type(), query.status()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            show(query, new PageResponse<>(List.of(), new PageMetadata(query.page(), PAGE_SIZE, 0, 0)));
        }
    }

    private void show(Query query, PageResponse<JobDto> page) {
        rows = page.content();
        grid.setItems(rows);
        long total = page.page() == null ? rows.size() : page.page().totalElements();
        int pages = page.page() == null ? 1 : Math.max(1, page.page().totalPages());
        long running = rows.stream().filter(job -> !job.isTerminal()).count();
        count.setText(total == 0 ? "Ninguno todavia" : total + " en el servicio, " + running + " en curso");
        pageInfo.setText("Pagina " + (query.page() + 1) + " de " + pages);
        previous.setEnabled(query.page() > 0);
        next.setEnabled(query.page() + 1 < pages);
    }

    // --- Seguimiento ------------------------------------------------------------------------------

    /**
     * Una pasada de consulta, en el hilo de {@link JobPolling} (los tests la llaman directamente):
     * si hay algo en curso —en la pagina o lanzado desde aqui— vuelve a pedir la pagina fuera del
     * bloqueo de la sesion, pregunta por su familia a los trabajos de esta sesion que no esten en
     * ella, y lleva lo que cambio a la pantalla con {@code UI.access()}.
     */
    void poll(UI ui) {
        if (log == null) {
            return;
        }
        List<JobLog.Entry> own = log.active();
        if (own.isEmpty() && rows.stream().allMatch(JobDto::isTerminal)) {
            return;
        }
        Query query = lastQuery;
        PageResponse<JobDto> page;
        try {
            page = CurrentPrincipal.callAs(principal, () -> client.list(query.page(), PAGE_SIZE, query.type(), query.status()));
        } catch (BackofficeApiException failure) {
            LOGGER.warn("No se ha podido consultar la lista de trabajos: {}", failure.getMessage());
            return;
        }
        Map<UUID, JobDto> onPage = new HashMap<>();
        page.content().forEach(job -> onPage.put(job.id(), job));
        List<JobDto> updates = new ArrayList<>();
        for (JobLog.Entry entry : own) {
            JobDto update = onPage.get(entry.job().id());
            if (update == null) {
                try {
                    update = CurrentPrincipal.callAs(principal, () -> statusOf(entry.job()));
                } catch (BackofficeApiException failure) {
                    LOGGER.warn("No se ha podido consultar el trabajo {}: {}", entry.job().id(), failure.getMessage());
                    continue;
                }
            }
            updates.add(update);
        }
        ui.access(() -> {
            for (JobDto update : updates) {
                boolean finishedNow = update.isTerminal()
                        && log.find(update.id()).map(entry -> !entry.job().isTerminal()).orElse(false);
                log.update(update);
                if (finishedNow) {
                    announce(update);
                }
            }
            if (query.equals(lastQuery)) {
                show(query, page);
            }
        });
    }

    /**
     * Los metodos concretos del cliente y no sus {@code default} ({@code status}, {@code file}):
     * un doble de la interfaz no ejecuta los {@code default}, y esta vista se prueba con uno. Un
     * trabajo de tipo desconocido no tiene familia a la que preguntar: se queda como se leyo, y
     * solo cambia cuando cambia en la lista.
     */
    private JobDto statusOf(JobDto job) {
        return job.family().map(family -> switch (family) {
            case PROFILE_JOBS -> client.profileJob(job.id());
            case LOV_JOBS -> client.lovJob(job.id());
            case REPUBLISH -> client.republishJob(job.id());
        }).orElse(job);
    }

    /** Solo se llama con un trabajo descargable ({@link JobDto#isDownloadable()}), que tiene familia. */
    private ResponseEntity<byte[]> fileOf(JobDto job) {
        return switch (job.family().orElseThrow(() -> new IllegalArgumentException("Un trabajo de tipo desconocido no se descarga"))) {
            case PROFILE_JOBS -> client.profileJobFile(job.id());
            case LOV_JOBS -> client.lovJobFile(job.id());
            case REPUBLISH -> throw new IllegalArgumentException("Un republicado no produce fichero");
        };
    }

    /** Para los tests: una pasada con la UI de esta vista. */
    public void pollOnce() {
        getUI().ifPresent(this::poll);
    }

    private void announce(JobDto job) {
        String label = labelOf(job);
        boolean ok = job.status() == JobStatus.COMPLETED;
        Notification notification = Notification.show(label + ": " + job.status().label(), ok ? 5000 : 10000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(ok ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_ERROR);
    }

    private void whenAttached(Consumer<UI> action) {
        getUI().ifPresent(action);
    }

    /** La etiqueta con la que se lanzo desde esta sesion; si vino de otra parte, se describe. */
    String labelOf(JobDto job) {
        Optional<String> own = log == null ? Optional.empty() : log.labelOf(job.id());
        return own.orElseGet(() -> describe(job));
    }

    private String describe(JobDto job) {
        if (job.type() == null) {
            return "Trabajo";
        }
        if (job.type() == JobType.PROFILE_EXPORT && job.trackId() != null) {
            String format = job.mapperType() == null ? "" : ", " + job.mapperType();
            return "Exportacion de " + catalog.trackName(job.trackId()) + " (" + format.replaceFirst("^, ", "") + ")";
        }
        return job.type().label();
    }

    private Component buildGrid() {
        grid.addColumn(this::labelOf).setHeader("Trabajo").setKey("label").setFlexGrow(1);
        grid.addColumn(job -> job.type() == null ? "" : job.type().label()).setHeader("Tipo").setKey("type").setAutoWidth(true);
        grid.addColumn(job -> job.status() == null ? "" : job.status().label()).setHeader("Estado").setKey("status").setAutoWidth(true);
        grid.addColumn(job -> job.createdAt() == null ? "" : TIME.format(job.createdAt())).setHeader("Lanzado").setKey("createdAt").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(JobsView::progress)).setHeader("Progreso").setKey("progress").setAutoWidth(true);
        grid.addColumn(job -> String.valueOf(job.successfulItems())).setHeader("Correctos").setKey("successful").setAutoWidth(true);
        grid.addColumn(job -> String.valueOf(job.failedItems())).setHeader("Fallidos").setKey("failed").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::actions)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        grid.setSizeFull();
        return grid;
    }

    private static Component progress(JobDto job) {
        Integer total = job.totalItems();
        if (total == null || total <= 0) {
            return new Span(job.status() == JobStatus.RUNNING || job.status() == JobStatus.PENDING ? "..." : String.valueOf(job.processedItems()));
        }
        ProgressBar bar = new ProgressBar(0, total, Math.min(job.processedItems(), total));
        bar.setWidth("8rem");
        Span text = new Span(job.processedItems() + " / " + total);
        text.addClassNames(LumoUtility.FontSize.SMALL);
        HorizontalLayout layout = new HorizontalLayout(bar, text);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(false);
        return layout;
    }

    private Component actions(JobDto job) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (job.isDownloadable()) {
            actions.add(downloadLink(job));
        }
        if (job.failedItems() > 0 || (job.error() != null && !job.error().isBlank())) {
            Button errors = new Button("Errores", click -> showErrors(job));
            errors.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            errors.setId("errors-" + job.id());
            actions.add(errors);
        }
        return actions;
    }

    /** La fila de la lista no trae los errores por elemento: se piden al detalle de la familia. */
    private void showErrors(JobDto job) {
        JobDto detail = job;
        if (job.itemErrors().isEmpty() && job.failedItems() > 0) {
            try {
                detail = statusOf(job);
            } catch (BackofficeApiException failure) {
                UiErrors.show(failure);
                return;
            }
        }
        new JobErrorsDialog(labelOf(job), detail).open();
    }

    /**
     * El fichero se pide al servicio desde aqui, con el token de la persona, y se sirve al
     * navegador en la misma respuesta: el navegador nunca habla con el gateway.
     */
    private Anchor downloadLink(JobDto job) {
        return Downloads.link("download-" + job.id(), "Descargar", job.suggestedFileName(), () -> fileOf(job));
    }

    DownloadResponse download(JobDto job) {
        return Downloads.response(() -> fileOf(job), job.suggestedFileName());
    }
}

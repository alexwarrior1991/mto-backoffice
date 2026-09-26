package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.AssetSummaryDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftStatus;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * La ficha de un turno: su cabecera (equipo, posesion, vias, ventana prevista y real, corte de
 * tension, minutos netos, seccionadores abiertos), los botones que su estado admite (modificar,
 * iniciar, asignar tareas, cerrar y cancelar, todos con {@code maintenance-write}) y las pestanas
 * de tareas, perfiles y el parte del turno (con su Excel y su PDF).
 */
@Route(value = MaintenanceRoutes.SHIFTS + "/:" + ShiftDetailView.SHIFT_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class ShiftDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String SHIFT_ID_PARAMETER = "shiftId";
    static final String TASKS_TAB = "Tareas";
    static final String PROFILES_TAB = "Perfiles";
    static final String REPORT_TAB = "Parte";

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final MaintenanceCatalogs catalogs;
    private final boolean canWrite;
    private final boolean canPickMaterials;

    private final H2 title = new H2();
    private final Span statusBadge = new Span();
    private final Span possessionBadge = new Span();
    private final Div summary = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Div tabsHolder = new Div();
    private final List<LazyPanel> panels = new ArrayList<>();

    private ShiftDto shift;

    public static RouteParameters parametersOf(UUID shiftId) {
        return new RouteParameters(SHIFT_ID_PARAMETER, shiftId.toString());
    }

    public ShiftDetailView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.catalogs = new MaintenanceCatalogs(clients.catalog());
        this.canWrite = authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE);
        this.canPickMaterials = authentication.hasRole(StockRoles.STOCK_READ);
        setSizeFull();
        statusBadge.getElement().getThemeList().add("badge");
        statusBadge.setId("shift-status");
        possessionBadge.getElement().getThemeList().add("badge");
        HorizontalLayout heading = new HorizontalLayout(title, statusBadge, possessionBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("shift-summary");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        tabsHolder.setWidthFull();
        add(heading, summary, buttons, tabsHolder);
        expand(tabsHolder);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(SHIFT_ID_PARAMETER).orElse("");
        try {
            show(clients.shifts().findById(UUID.fromString(id)));
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe el turno " + id, 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo(ShiftsView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            event.forwardTo(ShiftsView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return shift == null ? "Turno" : "Turno " + shift.code();
    }

    ShiftDto shift() {
        return shift;
    }

    private void show(ShiftDto loaded) {
        paint(loaded);
        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        panels.clear();
        addPanel(tabs, TASKS_TAB, new ShiftTasksPanel(this::shift, clients, catalogs, canWrite, canPickMaterials, this::reload));
        addPanel(tabs, PROFILES_TAB, new ShiftProfilesPanel(() -> shift.id(), clients));
        addPanel(tabs, REPORT_TAB, new ShiftReportPanel(() -> shift.id(), clients));
        tabs.addSelectedChangeListener(change -> loadTab(tabs, change.getSelectedTab()));
        tabsHolder.removeAll();
        tabsHolder.add(tabs);
        loadTab(tabs, tabs.getSelectedTab());
    }

    private void addPanel(TabSheet tabs, String label, LazyPanel panel) {
        panels.add(panel);
        tabs.add(label, panel);
    }

    private static void loadTab(TabSheet tabs, Tab tab) {
        if (tab != null && tabs.getComponent(tab) instanceof LazyPanel panel) {
            panel.ensureLoaded();
        }
    }

    void reload() {
        try {
            changed(clients.shifts().findById(shift.id()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void changed(ShiftDto updated) {
        paint(updated);
        panels.forEach(LazyPanel::reloadIfLoaded);
    }

    private void paint(ShiftDto loaded) {
        shift = loaded;
        title.setText(loaded.code() + " · " + MaintenanceFormats.date(loaded.shiftDate()));
        ShiftStatus status = loaded.status();
        statusBadge.setText(status == null ? "" : status.label());
        statusBadge.getElement().getThemeList().set("success", status == ShiftStatus.CLOSED);
        statusBadge.getElement().getThemeList().set("contrast", status == ShiftStatus.CANCELLED);
        possessionBadge.setText(loaded.possessionType() == null ? "" : "Posesion " + loaded.possessionType().label().toLowerCase());

        summary.removeAll();
        summary.add(line("Equipo: " + (loaded.team() == null ? "sin equipo" : loaded.team().label()),
                "Base: " + orDash(loaded.baseName()), "Vehiculo: " + orDash(loaded.vehicle())));
        summary.add(line("Vias: " + loaded.trackIds().stream().map(names::trackName).collect(Collectors.joining(", ")),
                "KP " + orDash(MaintenanceFormats.kpRange(loaded.startKp(), loaded.endKp())),
                "Paquete: " + orDash(names.packageName(loaded.executionPackageId()))));
        summary.add(line("Previsto: " + orDash(Formats.dateTime(loaded.plannedStart())) + " - " + orDash(Formats.dateTime(loaded.plannedEnd())),
                "Real: " + orDash(Formats.dateTime(loaded.actualStart())) + " - " + orDash(Formats.dateTime(loaded.actualEnd())),
                "Corte de tension: " + orDash(Formats.dateTime(loaded.voltageCutoffAt())),
                "Neto: " + (loaded.netWorkMinutes() == null ? "-" : loaded.netWorkMinutes() + " min")));
        summary.add(line("Seccionadores abiertos: " + orDash(loaded.blockingDisconnectors().stream().map(AssetSummaryDto::label)
                        .collect(Collectors.joining(", "))),
                "Puesta a tierra: " + orDash(loaded.earthingPoints()), "Estacionamiento: " + orDash(loaded.parkingPlace())));
        if (loaded.personnel() != null && !loaded.personnel().isBlank()) {
            summary.add(line("Personal: " + loaded.personnel()));
        }
        if (loaded.observations() != null && !loaded.observations().isBlank()) {
            summary.add(line("Observaciones: " + loaded.observations()));
        }
        paintButtons(loaded);
    }

    private void paintButtons(ShiftDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver a la lista", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(ShiftsView.class)));
        ShiftStatus status = loaded.status();
        if (!canWrite || status == null) {
            return;
        }
        if (status.isOpen()) {
            buttons.add(button("shift-edit", "Modificar", VaadinIcon.EDIT,
                    () -> new ShiftEditorDialog(loaded, clients, names, catalogs, this::changed).open()));
            buttons.add(button("shift-assign-tasks", "Asignar tareas", VaadinIcon.TASKS,
                    () -> new AssignTasksDialog(loaded, clients, names, this::reload).open()));
        }
        if (status.canStart()) {
            buttons.add(button("shift-start", "Iniciar", VaadinIcon.PLAY,
                    () -> new ShiftTransitionDialog(ShiftTransitionDialog.Kind.START, loaded, clients, this::changed).open()));
        }
        if (status.canClose()) {
            buttons.add(button("shift-close", "Cerrar", VaadinIcon.FLAG_CHECKERED,
                    () -> new ShiftTransitionDialog(ShiftTransitionDialog.Kind.CLOSE, loaded, clients, this::changed).open()));
        }
        if (status.isOpen()) {
            Button cancel = button("shift-cancel", "Cancelar", VaadinIcon.CLOSE_CIRCLE, () -> new ReasonDialog("Cancelar " + loaded.code(),
                    "El turno queda cancelado con su motivo. No se puede deshacer.", "Cancelar el turno", this::cancel).open());
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(cancel);
        }
    }

    private boolean cancel(String reason) {
        try {
            ShiftDto cancelled = clients.shifts().cancel(shift.id(), new ReasonRequest(reason));
            MaintenanceUi.success(cancelled.code() + " cancelado");
            changed(cancelled);
            return true;
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return false;
        }
    }

    private static Button button(String id, String text, VaadinIcon icon, Runnable action) {
        Button button = new Button(text, icon.create(), click -> action.run());
        button.setId(id);
        return button;
    }

    private static Div line(String... parts) {
        return new Div(String.join(" · ", parts));
    }

    private static String orDash(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }
}

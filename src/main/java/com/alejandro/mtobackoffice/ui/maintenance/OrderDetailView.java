package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.MaintenanceOrderStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.OrderDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
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

/**
 * La ficha de una orden: su cabecera (activo, via, KP, equipo, avance y estimacion, que calcula el
 * servicio), los botones que su estado admite y pestanas que piden sus datos al abrirse (tareas e
 * historial de estados). Solo se ofrece lo que el estado admite (copiado de la maquina de estados
 * del servicio), pero quien decide es el servicio: un {@code TRN-001} se notifica. Cancelar pide
 * {@code maintenance-supervise} ademas de {@code maintenance-write}.
 */
@Route(value = MaintenanceRoutes.ORDER + "/:" + OrderDetailView.ORDER_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class OrderDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String ORDER_ID_PARAMETER = "orderId";
    static final String TASKS_TAB = "Tareas";
    static final String HISTORY_TAB = "Estados";

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final MaintenanceCatalogs catalogs;
    private final boolean canWrite;
    private final boolean canSupervise;

    private final H2 title = new H2();
    private final Span statusBadge = new Span();
    private final Span typeBadge = new Span();
    private final Span priorityBadge = new Span();
    private final Div summary = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Div tabsHolder = new Div();
    private final List<LazyPanel> panels = new ArrayList<>();

    private OrderDto order;

    public static RouteParameters parametersOf(UUID orderId) {
        return new RouteParameters(ORDER_ID_PARAMETER, orderId.toString());
    }

    public OrderDetailView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.catalogs = new MaintenanceCatalogs(clients.catalog());
        this.canWrite = authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE);
        this.canSupervise = authentication.hasAllRoles(MaintenanceRoles.MAINTENANCE_WRITE, MaintenanceRoles.MAINTENANCE_SUPERVISE);
        setSizeFull();
        for (Span badge : List.of(statusBadge, typeBadge, priorityBadge)) {
            badge.getElement().getThemeList().add("badge");
        }
        statusBadge.setId("order-status");
        HorizontalLayout heading = new HorizontalLayout(title, statusBadge, typeBadge, priorityBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("order-summary");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        tabsHolder.setWidthFull();
        add(heading, summary, buttons, tabsHolder);
        expand(tabsHolder);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(ORDER_ID_PARAMETER).orElse("");
        try {
            show(clients.orders().findById(UUID.fromString(id)));
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe la orden " + id, 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo(OrdersView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            event.forwardTo(OrdersView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return order == null ? "Orden" : "Orden " + order.code();
    }

    OrderDto order() {
        return order;
    }

    private void show(OrderDto loaded) {
        paint(loaded);
        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        panels.clear();
        addPanel(tabs, TASKS_TAB, new OrderTasksPanel(this::order, clients, catalogs, canWrite, this::reload));
        addPanel(tabs, HISTORY_TAB, new StatusHistoryPanel("order-history-grid", () -> clients.orders().history(order.id()),
                status -> MaintenanceOrderStatus.of(status).label()));
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

    /** Relee la orden tras un cambio: cabecera, botones y las pestanas que ya se habian abierto. */
    void reload() {
        try {
            paint(clients.orders().findById(order.id()));
            panels.forEach(LazyPanel::reloadIfLoaded);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** Tras una transicion ya se tiene la orden: se repinta con ella y se releen las pestanas abiertas. */
    private void changed(OrderDto updated) {
        paint(updated);
        panels.forEach(LazyPanel::reloadIfLoaded);
    }

    private void paint(OrderDto loaded) {
        order = loaded;
        title.setText(loaded.code() + " · " + loaded.title());
        MaintenanceOrderStatus status = loaded.status();
        statusBadge.setText(status == null ? "" : status.label());
        statusBadge.getElement().getThemeList().set("success", status == MaintenanceOrderStatus.COMPLETED);
        statusBadge.getElement().getThemeList().set("contrast", status == MaintenanceOrderStatus.CANCELLED);
        typeBadge.setText(loaded.type() == null ? "" : loaded.type().label());
        priorityBadge.setText(loaded.priority() == null ? "" : "Prioridad " + loaded.priority().label().toLowerCase());

        summary.removeAll();
        summary.add(line("Activo: " + (loaded.asset() == null ? "" : loaded.asset().label()),
                "Via: " + names.trackName(loaded.trackId()),
                "KP " + MaintenanceFormats.kpRange(loaded.startKp(), loaded.endKp()),
                "Paquete: " + names.packageName(loaded.executionPackageId())));
        summary.add(line("Equipo: " + (loaded.team() == null ? "sin equipo" : loaded.team().label()),
                "Asignada a: " + (loaded.assignedUser() == null ? "nadie" : loaded.assignedUser()),
                "Prevista: " + orDash(MaintenanceFormats.date(loaded.plannedDate())),
                "Inicio: " + orDash(Formats.dateTime(loaded.actualStartDate())),
                "Fin: " + orDash(Formats.dateTime(loaded.actualEndDate()))));
        summary.add(line("Tareas: " + loaded.completedTaskCount() + " de " + loaded.taskCount() + " completadas",
                "Estimacion: " + Formats.quantity(loaded.estimatedMinutes()) + " min en " + loaded.estimatedShifts() + " turnos"));
        if (loaded.closingNotes() != null && !loaded.closingNotes().isBlank()) {
            summary.add(line("Notas de cierre: " + loaded.closingNotes()));
        }
        if (loaded.cancellationReason() != null && !loaded.cancellationReason().isBlank()) {
            summary.add(line("Motivo de la cancelacion: " + loaded.cancellationReason()));
        }
        paintButtons(loaded);
    }

    private void paintButtons(OrderDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver a la lista", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(OrdersView.class)));
        MaintenanceOrderStatus status = loaded.status();
        if (canWrite && status.isOpen()) {
            buttons.add(button("order-edit", "Modificar", VaadinIcon.EDIT,
                    () -> new OrderEditorDialog(loaded, clients, catalogs, updated -> changed(updated)).open()));
        }
        if (canWrite && status.canPlan()) {
            buttons.add(transition(OrderTransitionDialog.Kind.PLAN, "order-plan", VaadinIcon.CALENDAR, loaded));
        }
        if (canWrite && status.canAssign()) {
            buttons.add(transition(OrderTransitionDialog.Kind.ASSIGN, "order-assign", VaadinIcon.USER_CHECK, loaded));
        }
        if (canWrite && status.canStart(loaded.type())) {
            buttons.add(transition(OrderTransitionDialog.Kind.START, "order-start", VaadinIcon.PLAY, loaded));
        }
        if (canWrite && status.canComplete()) {
            buttons.add(transition(OrderTransitionDialog.Kind.COMPLETE, "order-complete", VaadinIcon.CHECK, loaded));
        }
        if (canSupervise && status.isOpen()) {
            Button cancel = button("order-cancel", "Cancelar", VaadinIcon.CLOSE_CIRCLE, () -> new ReasonDialog("Cancelar " + loaded.code(),
                    "La orden queda cancelada con su motivo y se liberan en el almacen las reservas de sus materiales. No se puede deshacer.",
                    "Cancelar la orden", this::cancel).open());
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(cancel);
        }
    }

    private Button transition(OrderTransitionDialog.Kind kind, String id, VaadinIcon icon, OrderDto loaded) {
        return button(id, kind.label(), icon,
                () -> new OrderTransitionDialog(kind, loaded, clients, catalogs, canSupervise, this::changed).open());
    }

    private boolean cancel(String reason) {
        try {
            OrderDto cancelled = clients.orders().cancel(order.id(), new ReasonRequest(reason));
            MaintenanceUi.success(cancelled.code() + " cancelada");
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

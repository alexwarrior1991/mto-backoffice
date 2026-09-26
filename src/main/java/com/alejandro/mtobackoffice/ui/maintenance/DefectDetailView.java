package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.dto.maintenance.DefectDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.DefectStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.ReasonRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.UUID;
import java.util.function.Function;

/**
 * La ficha de un defecto: su cabecera, de donde salio (la inspeccion o la tarea), la orden que lo
 * corrige, los botones que su estado admite y el historial de estados. Vincular a una orden pide
 * {@code maintenance-write}; resolver, cerrar y descartar, ademas {@code maintenance-supervise}.
 */
@Route(value = MaintenanceRoutes.DEFECTS + "/:" + DefectDetailView.DEFECT_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class DefectDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String DEFECT_ID_PARAMETER = "defectId";

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final boolean canWrite;
    private final boolean canSupervise;

    private final H2 title = new H2();
    private final Span severityBadge = new Span();
    private final Span statusBadge = new Span();
    private final Div summary = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final StatusHistoryPanel history;

    private DefectDto defect;

    public static RouteParameters parametersOf(UUID defectId) {
        return new RouteParameters(DEFECT_ID_PARAMETER, defectId.toString());
    }

    public DefectDetailView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE);
        this.canSupervise = authentication.hasAllRoles(MaintenanceRoles.MAINTENANCE_WRITE, MaintenanceRoles.MAINTENANCE_SUPERVISE);
        this.history = new StatusHistoryPanel("defect-history-grid", () -> clients.defects().history(defect.id()),
                status -> DefectStatus.of(status).label());
        setSizeFull();
        severityBadge.getElement().getThemeList().add("badge");
        statusBadge.getElement().getThemeList().add("badge");
        statusBadge.setId("defect-status");
        HorizontalLayout heading = new HorizontalLayout(title, severityBadge, statusBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("defect-summary");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        add(heading, summary, buttons, new H3("Estados"), history);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(DEFECT_ID_PARAMETER).orElse("");
        try {
            paint(clients.defects().findById(UUID.fromString(id)));
            history.reload();
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe el defecto " + id, 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo(DefectsView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            event.forwardTo(DefectsView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return defect == null ? "Defecto" : "Defecto " + defect.code();
    }

    private void changed(DefectDto updated) {
        paint(updated);
        history.reloadIfLoaded();
    }

    private void paint(DefectDto loaded) {
        defect = loaded;
        title.setText(loaded.code());
        severityBadge.setText(loaded.severity() == null ? "" : "Gravedad " + loaded.severity().label().toLowerCase());
        DefectStatus status = loaded.status();
        statusBadge.setText(status == null ? "" : status.label());
        statusBadge.getElement().getThemeList().set("success", status == DefectStatus.RESOLVED || status == DefectStatus.CLOSED);
        statusBadge.getElement().getThemeList().set("contrast", status == DefectStatus.DISCARDED);
        summary.removeAll();
        summary.add(new Div(String.join(" · ", "Activo: " + (loaded.asset() == null ? "" : loaded.asset().label()),
                "Via: " + names.trackName(loaded.trackId()), "KP " + MaintenanceFormats.kpRange(loaded.startKp(), loaded.endKp()),
                "Detectado: " + Formats.dateTime(loaded.detectedAt()),
                "Reparacion prevista: " + (loaded.repairPlannedDate() == null ? "-" : MaintenanceFormats.date(loaded.repairPlannedDate())))));
        for (String[] text : new String[][]{{"Descripcion", loaded.description()}, {"Notas tecnicas", loaded.technicalNotes()},
                {"Correccion", loaded.correctionType()}, {"Piezas cambiadas", loaded.partsReplaced()},
                {"Resolucion", loaded.resolutionNotes()}, {"Motivo del descarte", loaded.discardReason()}}) {
            if (text[1] != null && !text[1].isBlank()) {
                summary.add(new Div(text[0] + ": " + text[1]));
            }
        }
        if (loaded.resolvedAt() != null) {
            summary.add(new Div("Resuelto: " + Formats.dateTime(loaded.resolvedAt())));
        }
        paintButtons(loaded);
    }

    private void paintButtons(DefectDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver a la lista", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(DefectsView.class)));
        buttons.add(MaintenanceHistory.button("defect-history", loaded.code(), (page, size) -> clients.defects().revisions(loaded.id(), page, size),
                MaintenanceHistory::defect));
        if (loaded.inspectionId() != null) {
            buttons.add(button("defect-inspection-link", "Ver la inspeccion", VaadinIcon.CLIPBOARD_CHECK,
                    () -> UI.getCurrent().navigate(InspectionDetailView.class, InspectionDetailView.parametersOf(loaded.inspectionId()))));
        }
        if (loaded.orderId() != null) {
            buttons.add(button("defect-order-link", "Ver la orden", VaadinIcon.WRENCH,
                    () -> UI.getCurrent().navigate(OrderDetailView.class, OrderDetailView.parametersOf(loaded.orderId()))));
        }
        DefectStatus status = loaded.status();
        if (status == null) {
            return;
        }
        if (canWrite && status.isEditable()) {
            buttons.add(button("defect-edit", "Modificar", VaadinIcon.EDIT, () -> new DefectEditorDialog(loaded, null, clients, this::changed).open()));
        }
        if (canWrite && status.isPending()) {
            buttons.add(button("defect-link-order", "Vincular a orden", VaadinIcon.LINK,
                    () -> DefectTransitionDialogs.linkOrder(loaded, clients, this::changed).open()));
        }
        if (canSupervise && status.isPending()) {
            buttons.add(button("defect-resolve", "Resolver", VaadinIcon.CHECK,
                    () -> DefectTransitionDialogs.resolve(loaded, clients, this::changed).open()));
        }
        if (canSupervise && status == DefectStatus.RESOLVED) {
            buttons.add(button("defect-close", "Cerrar", VaadinIcon.LOCK, () -> new ReasonDialog("Cerrar " + loaded.code(),
                    "El defecto queda cerrado; el motivo va al historial.", "Cerrar el defecto",
                    reason -> call(reason, text -> clients.defects().close(loaded.id(), new ReasonRequest(text)), "cerrado")).open()));
        }
        if (canSupervise && status == DefectStatus.OPEN) {
            Button discard = button("defect-discard", "Descartar", VaadinIcon.TRASH, () -> new ReasonDialog("Descartar " + loaded.code(),
                    "El defecto queda descartado con su motivo. No se puede deshacer.", "Descartar el defecto",
                    reason -> call(reason, text -> clients.defects().discard(loaded.id(), new ReasonRequest(text)), "descartado")).open());
            discard.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(discard);
        }
    }

    private boolean call(String reason, Function<String, DefectDto> operation, String done) {
        try {
            DefectDto result = operation.apply(reason);
            MaintenanceUi.success(result.code() + " " + done);
            changed(result);
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
}

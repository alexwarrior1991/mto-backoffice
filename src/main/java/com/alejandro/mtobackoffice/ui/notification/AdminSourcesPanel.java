package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.dto.notification.DeliveryStatus;
import com.alejandro.mtobackoffice.client.dto.notification.SourceStatusDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Como estan las fuentes ({@code GET /admin/sources}): cuantos mensajes de cada fuente de RabbitMQ
 * hay en el inbox y en que estado, hasta donde ha leido el lector de Keycloak y quien lo tiene
 * arrendado, cuantas rafagas siguen abiertas y cuantas entregas hay en cada estado. Son las cifras
 * del servicio: aqui no se suma nada. Se vuelve a pedir con «Actualizar», sin sondeo.
 */
class AdminSourcesPanel extends LazyPanel {

    private final NotificationClient client;
    private final Span summary = new Span();
    private final Grid<SourceStatusDto.InboxCountDto> inbox = new Grid<>();
    private final Grid<SourceStatusDto.CursorDto> cursors = new Grid<>();

    AdminSourcesPanel(NotificationClient client) {
        this.client = client;
        setSizeFull();
        Button refresh = new Button("Actualizar", VaadinIcon.REFRESH.create(), click -> reload());
        refresh.setId("admin-sources-refresh");
        summary.setId("admin-sources-summary");
        HorizontalLayout toolbar = new HorizontalLayout(summary, refresh);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(summary);

        inbox.setId("admin-inbox-grid");
        inbox.addColumn(SourceStatusDto.InboxCountDto::sourceService).setHeader("Fuente").setKey("sourceService").setFlexGrow(1);
        inbox.addColumn(row -> row.status() == null ? "" : row.status().label()).setHeader("Estado").setKey("status").setAutoWidth(true);
        inbox.addColumn(SourceStatusDto.InboxCountDto::total).setHeader("Mensajes").setKey("total").setAutoWidth(true);
        inbox.setAllRowsVisible(true);

        cursors.setId("admin-cursors-grid");
        cursors.addColumn(row -> row.kind() == null ? "" : row.kind().label()).setHeader("Lector").setKey("kind").setAutoWidth(true);
        cursors.addColumn(row -> Formats.dateTime(row.lastEventTime())).setHeader("Leido hasta").setKey("lastEventTime").setAutoWidth(true);
        cursors.addColumn(row -> Formats.dateTime(row.lastPollAt())).setHeader("Ultimo sondeo").setKey("lastPollAt").setAutoWidth(true);
        cursors.addColumn(row -> Formats.dateTime(row.lastSuccessAt())).setHeader("Ultimo exito").setKey("lastSuccessAt").setAutoWidth(true);
        cursors.addColumn(AdminSourcesPanel::lease).setHeader("Arrendado por").setKey("lease").setAutoWidth(true);
        cursors.addColumn(row -> row.lastError() == null ? "" : row.lastError()).setHeader("Ultimo error").setKey("lastError").setFlexGrow(1);
        cursors.setAllRowsVisible(true);

        add(toolbar, new H4("Inbox por fuente y estado"), inbox, new H4("Lector de Keycloak"), cursors);
    }

    @Override
    protected void load() {
        try {
            SourceStatusDto status = client.adminSources();
            inbox.setItems(status.inbox());
            cursors.setItems(status.cursors());
            summary.setText(summary(status));
        } catch (BackofficeApiException failure) {
            inbox.setItems(List.of());
            cursors.setItems(List.of());
            summary.setText("");
            UiErrors.show(failure);
        }
    }

    /** «Rafagas abiertas: 2 · Entregas: Pendiente 0 · … »: los estados conocidos en su orden y, detras, los que no. */
    static String summary(SourceStatusDto status) {
        List<String> deliveries = new ArrayList<>();
        for (DeliveryStatus known : DeliveryStatus.selectable()) {
            Long total = status.deliveries().get(known.name());
            if (total != null) {
                deliveries.add(known.label() + " " + total);
            }
        }
        for (Map.Entry<String, Long> entry : status.deliveries().entrySet()) {
            if (DeliveryStatus.of(entry.getKey()) == DeliveryStatus.UNKNOWN) {
                deliveries.add(entry.getKey() + " " + entry.getValue());
            }
        }
        String bursts = "Rafagas abiertas: " + (status.openBursts() == null ? 0 : status.openBursts());
        return deliveries.isEmpty() ? bursts : bursts + " · Entregas: " + String.join(" · ", deliveries);
    }

    private static String lease(SourceStatusDto.CursorDto cursor) {
        if (cursor.leaseOwner() == null || cursor.leaseOwner().isBlank()) {
            return "";
        }
        return cursor.leaseUntil() == null ? cursor.leaseOwner() : cursor.leaseOwner() + " hasta " + Formats.dateTime(cursor.leaseUntil());
    }
}

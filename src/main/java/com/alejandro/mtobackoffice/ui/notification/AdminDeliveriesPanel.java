package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.notification.DeliveryDto;
import com.alejandro.mtobackoffice.client.dto.notification.DeliveryFilter;
import com.alejandro.mtobackoffice.client.dto.notification.DeliveryStatus;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.RowActions;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;
import java.util.UUID;

/**
 * Las entregas por los canales que empujan, hoy el correo ({@code GET /admin/deliveries}): paginadas,
 * filtradas y ordenadas en el servicio, la mas reciente primero. Los filtros son los del servicio:
 * el estado, el canal (escrito entero: se compara tal cual) y la notificacion, por su id. «Reintentar»
 * devuelve a la cola una fallida o una omitida, que es lo unico que el servicio reintenta
 * ({@link DeliveryStatus#canRetry()}); en otro estado seria su 409 {@code DLV-409}.
 */
class AdminDeliveriesPanel extends LazyPanel {

    static final String ACTIONS_COLUMN = "actions";
    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("createdAt,desc");

    private final NotificationClient client;
    private final ComboBox<DeliveryStatus> status = new ComboBox<>("Estado");
    private final TextField channel = new TextField("Canal");
    private final TextField notificationId = new TextField("Notificacion (id)");
    private final Span count = new Span();
    private final Grid<DeliveryDto> grid = new Grid<>();
    private LazyPages<DeliveryDto> pages;
    /** Lo que pide la lista: el ultimo filtro bien formado. */
    private DeliveryFilter applied = DeliveryFilter.NONE;

    AdminDeliveriesPanel(NotificationClient client) {
        this.client = client;
        setSizeFull();
        status.setId("admin-deliveries-status");
        status.setItems(DeliveryStatus.selectable());
        status.setItemLabelGenerator(DeliveryStatus::label);
        status.setClearButtonVisible(true);
        channel.setId("admin-deliveries-channel");
        channel.setPlaceholder("email");
        notificationId.setId("admin-deliveries-notification");
        notificationId.setErrorMessage("El id entero de una notificacion (un UUID)");
        for (TextField text : List.of(channel, notificationId)) {
            text.setClearButtonVisible(true);
            text.setValueChangeMode(ValueChangeMode.LAZY);
            text.addValueChangeListener(change -> applyFilters());
        }
        status.addValueChangeListener(change -> applyFilters());
        Button reload = new Button("Actualizar", VaadinIcon.REFRESH.create(), click -> reload());
        reload.setId("admin-deliveries-refresh");
        count.setId("admin-deliveries-count");
        HorizontalLayout toolbar = new HorizontalLayout(status, channel, notificationId, count, reload);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(count);

        grid.setId("admin-deliveries-grid");
        grid.addColumn(row -> Formats.dateTime(row.createdAt())).setHeader("Creada").setKey("createdAt").setSortProperty("createdAt")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(row -> text(row.channel())).setHeader("Canal").setKey("channel").setSortProperty("channel").setSortable(true).setAutoWidth(true);
        grid.addColumn(row -> row.scope() == null ? "" : row.scope().label()).setHeader("Ambito").setKey("scope").setAutoWidth(true);
        grid.addColumn(AdminDeliveriesPanel::recipient).setHeader("Para").setKey("recipient").setFlexGrow(1);
        grid.addColumn(row -> row.status() == null ? "" : row.status().label()).setHeader("Estado").setKey("status").setSortProperty("status")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(row -> row.attempts() + "/" + row.maxAttempts()).setHeader("Intentos").setKey("attempts").setAutoWidth(true);
        grid.addColumn(row -> Formats.dateTime(row.nextAttemptAt())).setHeader("Proximo intento").setKey("nextAttemptAt")
                .setSortProperty("nextAttemptAt").setSortable(true).setAutoWidth(true);
        grid.addColumn(row -> Formats.dateTime(row.sentAt())).setHeader("Enviada").setKey("sentAt").setSortProperty("sentAt").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(row -> text(row.lastError())).setHeader("Ultimo error").setKey("lastError").setFlexGrow(1);
        grid.addColumn(row -> row.notificationId() == null ? "" : row.notificationId().toString()).setHeader("Notificacion").setKey("notificationId")
                .setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();

        add(toolbar, grid);
        expand(grid);
    }

    private Component rowActions(DeliveryDto delivery) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (delivery.status() != null && delivery.status().canRetry()) {
            actions.add(RowActions.button("retry-" + delivery.id(), VaadinIcon.ROTATE_RIGHT, "Reintentar la entrega a " + recipient(delivery),
                    click -> retry(delivery)));
        }
        return actions;
    }

    /** Vuelve a la cola y sale en el siguiente despacho. Salga bien o no, la lista se relee: la entrega ya no esta como se pinto. */
    private void retry(DeliveryDto delivery) {
        try {
            client.retryDelivery(delivery.id());
            Screens.success("Entrega en cola otra vez");
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    /** A quien va: la direccion (y su usuario) de una entrega a un destinatario, o la audiencia de una que aun se expande. */
    static String recipient(DeliveryDto delivery) {
        if (delivery.recipient() != null && !delivery.recipient().isBlank()) {
            return delivery.recipientUsername() == null || delivery.recipientUsername().isBlank()
                    ? delivery.recipient() : delivery.recipient() + " (" + delivery.recipientUsername() + ")";
        }
        return text(delivery.audienceKey());
    }

    /**
     * Un filtro cambio. El id de la notificacion se busca entero: mientras no sea un UUID no se pide
     * nada (el servicio responderia 400) y la lista sigue con lo ultimo que pidio.
     */
    private void applyFilters() {
        String id = ActivityView.blankToNull(notificationId.getValue());
        UUID notification = null;
        boolean valid = true;
        if (id != null) {
            try {
                notification = UUID.fromString(id);
                valid = notification.toString().equalsIgnoreCase(id);
            } catch (IllegalArgumentException notAnId) {
                valid = false;
            }
        }
        notificationId.setInvalid(!valid);
        if (!valid) {
            return;
        }
        applied = new DeliveryFilter(status.getValue(), ActivityView.blankToNull(channel.getValue()), notification);
        reloadIfLoaded();
    }

    /** La lista se monta la primera vez que se abre la pestana; despues, se vuelve a pedir. */
    @Override
    protected void load() {
        if (pages == null) {
            pages = LazyPages.of(grid, this::page, total -> count.setText(total + (total == 1 ? " entrega" : " entregas")));
        } else {
            pages.refresh();
        }
    }

    private LazyPages.Page<DeliveryDto> page(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(client.adminDeliveries(applied, offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }

    private static String text(String value) {
        return value == null ? "" : value;
    }
}

package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityCategory;
import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.dto.notification.InboxFilter;
import com.alejandro.mtobackoffice.client.dto.notification.InboxItemDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.security.NotificationRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.UUID;

/**
 * Mi bandeja: las notificaciones dirigidas a mi (por usuario, perfil o rol de cliente, y eso lo
 * resuelve el servicio con el token), paginadas y filtradas en el servidor, la mas reciente primero.
 *
 * <p>Abre con las <b>no leidas</b>; «Solo no leidas» desmarcado ensena tambien las leidas. El
 * estado de lectura es de cada persona y el servicio no ordena por el, asi que no hay columna que
 * lo ordene: lo dice la marca «Nueva». Abrir una notificacion (la fila, o su flecha) la marca como
 * leida y sigue su enlace, que es una ruta de esta aplicacion puesta por la regla que la creo;
 * «Marcar todas como leidas» va hasta la mas reciente visible, como hace el servicio. La linea
 * del registro que la causo se abre desde la lupa, solo con {@code notification-activity-read}.</p>
 */
@Route(value = NotificationRoutes.INBOX, layout = MainLayout.class)
@PageTitle("Notificaciones")
@Menu(title = "Notificaciones", order = 80, icon = "vaadin:bell")
@RolesAllowed(NotificationRoles.NOTIFICATION_INBOX)
public class NotificationsView extends VerticalLayout {

    /** El tamano por defecto del servicio; su tope es 100. */
    static final int PAGE_SIZE = 20;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("createdAt,desc");

    private final NotificationClient client;
    private final boolean canReadActivity;
    private final Checkbox unreadOnly = new Checkbox("Solo no leidas", true);
    private final ComboBox<ActivityCategory> category = new ComboBox<>("Categoria");
    private final ComboBox<ActivitySeverity> severity = new ComboBox<>("Gravedad");
    private final DatePicker from = new DatePicker("Desde");
    private final DatePicker to = new DatePicker("Hasta");
    private final Span count = new Span();
    private final Grid<InboxItemDto> grid = new Grid<>();
    private LazyPages<InboxItemDto> pages;

    public NotificationsView(NotificationClient client, AuthenticationContext authentication) {
        this.client = client;
        this.canReadActivity = authentication.hasRole(NotificationRoles.NOTIFICATION_ACTIVITY_READ);
        setSizeFull();
        unreadOnly.setId("inbox-unread-only");
        category.setId("inbox-category");
        category.setItems(ActivityCategory.selectable());
        category.setItemLabelGenerator(ActivityCategory::label);
        category.setClearButtonVisible(true);
        severity.setId("inbox-severity");
        severity.setItems(ActivitySeverity.selectable());
        severity.setItemLabelGenerator(ActivitySeverity::label);
        severity.setClearButtonVisible(true);
        from.setId("inbox-from");
        to.setId("inbox-to");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(unreadOnly, category, severity, from, to)) {
            filter.addValueChangeListener(change -> refresh());
        }
        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> refresh());
        Button readAll = new Button("Marcar todas como leidas", VaadinIcon.CHECK_SQUARE_O.create(), click -> markAllRead());
        readAll.setId("inbox-read-all");

        FlexLayout filters = new FlexLayout(unreadOnly, category, severity, from, to);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, reload, readAll);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("inbox-grid");
        grid.addColumn(new ComponentRenderer<>(NotificationsView::state)).setHeader("").setKey("state").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(item -> Formats.dateTime(item.createdAt())).setHeader("Cuando").setKey("createdAt").setSortProperty("createdAt")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(item -> item.severity() == null ? "" : item.severity().label()).setHeader("Gravedad").setKey("severity")
                .setSortProperty("severity").setSortable(true).setAutoWidth(true);
        grid.addColumn(item -> item.category() == null ? "" : item.category().label()).setHeader("Categoria").setKey("category")
                .setSortProperty("category").setSortable(true).setAutoWidth(true);
        grid.addColumn(InboxItemDto::title).setHeader("Titulo").setKey("title").setSortProperty("title").setSortable(true).setFlexGrow(1);
        grid.addColumn(InboxItemDto::body).setHeader("Detalle").setKey("body").setFlexGrow(2);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(countText(total)));
        grid.addItemDoubleClickListener(click -> open(click.getItem()));

        add(new H2("Notificaciones"), filters, toolbar, grid);
        expand(grid);
    }

    private static Component state(InboxItemDto item) {
        Span state = new Span(item.read() ? "" : "Nueva");
        if (!item.read()) {
            state.getElement().getThemeList().add("badge primary pill small");
        }
        return state;
    }

    private Component rowActions(InboxItemDto item) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (NotificationLinks.target(item.link()).isPresent()) {
            Button open = new Button(VaadinIcon.ARROW_RIGHT.create(), click -> open(item));
            open.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            open.setTooltipText("Abrir " + item.link());
            open.setId("open-" + item.id());
            actions.add(open);
        }
        if (!item.read()) {
            Button read = new Button(VaadinIcon.CHECK.create(), click -> {
                if (markRead(item) != null) {
                    refresh();
                }
            });
            read.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            read.setTooltipText("Marcar como leida");
            read.setId("read-" + item.id());
            actions.add(read);
        }
        // Un acceso nunca sale por el registro: su linea seria un 404 ACT-404.
        if (canReadActivity && item.activityEventId() != null && item.category() != ActivityCategory.ACCESS) {
            Button event = new Button(VaadinIcon.SEARCH.create(), click -> showEvent(item.activityEventId()));
            event.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            event.setTooltipText("Ver la linea del registro que la causo");
            event.setId("event-" + item.id());
            actions.add(event);
        }
        return actions;
    }

    void refresh() {
        pages.refresh();
    }

    /** Abrir: marcarla como leida si no lo estaba y seguir su enlace; sin enlace, solo la marca. */
    private void open(InboxItemDto item) {
        InboxItemDto current = item.read() ? item : markRead(item);
        if (current == null) {
            return;
        }
        if (NotificationLinks.target(current.link()).isPresent()) {
            NotificationLinks.open(UI.getCurrent(), current.link());
        } else {
            refresh();
        }
    }

    /** Marca en el servicio y refresca la campana; con un fallo (404 si ya no es mia) lo dice y devuelve {@code null}. */
    private InboxItemDto markRead(InboxItemDto item) {
        try {
            InboxItemDto updated = client.markRead(item.id());
            InboxBell.find(UI.getCurrent()).ifPresent(InboxBell::refresh);
            return updated == null ? item : updated;
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return null;
        }
    }

    private void markAllRead() {
        try {
            client.markAllRead();
            Notification.show("Todas las notificaciones quedan como leidas", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            InboxBell.find(UI.getCurrent()).ifPresent(InboxBell::refresh);
            refresh();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void showEvent(UUID eventId) {
        try {
            EventDetailDialog.of(client.activityEvent(eventId)).open();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private InboxFilter filter() {
        return new InboxFilter(Boolean.TRUE.equals(unreadOnly.getValue()) ? Boolean.TRUE : null, category.getValue(), severity.getValue(),
                Formats.startOfDay(from.getValue()), Formats.endOfDay(to.getValue()));
    }

    private String countText(long total) {
        return total + (Boolean.TRUE.equals(unreadOnly.getValue()) ? " sin leer" : " notificaciones");
    }

    /** Una pagina con su total: una peticion ({@link LazyPages}). */
    private LazyPages.Page<InboxItemDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(client.inbox(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

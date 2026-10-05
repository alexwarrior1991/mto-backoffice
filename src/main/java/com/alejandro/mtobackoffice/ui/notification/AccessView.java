package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.notification.AccessEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.AccessFilter;
import com.alejandro.mtobackoffice.client.dto.notification.AccessOutcome;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.security.NotificationRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Los accesos: logins, fallos, rachas, logouts, bloqueos y cambios de credenciales, con usuario e
 * IP. Es la unica categoria que lleva la IP, y por eso tiene su permiso aparte
 * ({@code notification-access-read}), que no viene con el registro: quien lee la actividad del
 * dominio no ve por ello desde donde entra cada persona. Las reglas de mto-notification enlazan
 * aqui con el usuario o la IP en la URL ({@code /actividad/accesos?username=...}).
 */
@Route(value = NotificationRoutes.ACCESS, layout = MainLayout.class)
@PageTitle("Accesos")
@Menu(title = "Accesos", order = 83, icon = "vaadin:sign-in")
@RolesAllowed(NotificationRoles.NOTIFICATION_ACCESS_READ)
public class AccessView extends VerticalLayout implements BeforeEnterObserver {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("occurredAt,desc");

    private final NotificationClient client;
    private final TextField username = new TextField("Usuario");
    private final TextField ipAddress = new TextField("IP");
    private final TextField type = new TextField("Tipo");
    private final ComboBox<AccessOutcome> outcome = new ComboBox<>("Resultado");
    private final DatePicker from = new DatePicker("Desde");
    private final DatePicker to = new DatePicker("Hasta");
    private final Span count = new Span();
    private final Grid<AccessEventDto> grid = new Grid<>();
    private LazyPages<AccessEventDto> pages;
    private static final Pattern IPV4 = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");
    private static final Pattern IPV6 = Pattern.compile("^[0-9a-fA-F:.]{2,45}$");
    /** Lo que pide la lista: el ultimo filtro con la IP entera (o sin IP). */
    private AccessFilter applied = AccessFilter.NONE;

    public AccessView(NotificationClient client) {
        this.client = client;
        setSizeFull();
        username.setId("access-username");
        ipAddress.setId("access-ip");
        ipAddress.setPlaceholder("10.0.0.7");
        type.setId("access-type");
        type.setPlaceholder("access.login.failed");
        for (TextField text : List.of(username, ipAddress, type)) {
            text.setClearButtonVisible(true);
            text.setValueChangeMode(ValueChangeMode.LAZY);
        }
        outcome.setId("access-outcome");
        outcome.setItems(AccessOutcome.selectable());
        outcome.setItemLabelGenerator(AccessOutcome::label);
        outcome.setClearButtonVisible(true);
        from.setId("access-from");
        to.setId("access-to");
        ipAddress.setErrorMessage("Una IP entera: 10.0.0.7 o 2001:db8::1");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(username, ipAddress, type, outcome, from, to)) {
            filter.addValueChangeListener(change -> applyFilters());
        }
        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> refresh());

        FlexLayout filters = new FlexLayout(username, ipAddress, type, outcome, from, to);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, reload);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("access-grid");
        grid.addColumn(event -> Formats.dateTime(event.occurredAt())).setHeader("Cuando").setKey("occurredAt").setSortProperty("occurredAt")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(AccessEventDto::type).setHeader("Tipo").setKey("type").setSortProperty("type").setSortable(true).setAutoWidth(true);
        grid.addColumn(event -> event.outcome() == null ? "" : event.outcome().label()).setHeader("Resultado").setKey("outcome").setAutoWidth(true);
        grid.addColumn(AccessEventDto::username).setHeader("Usuario").setKey("username").setAutoWidth(true);
        grid.addColumn(AccessEventDto::ipAddress).setHeader("IP").setKey("ip").setAutoWidth(true);
        grid.addColumn(event -> event.severity() == null ? "" : event.severity().label()).setHeader("Gravedad").setKey("severity")
                .setSortProperty("severity").setSortable(true).setAutoWidth(true);
        grid.addColumn(event -> event.eventCount() > 1 ? "x" + event.eventCount() : "").setHeader("Eventos").setKey("count").setAutoWidth(true);
        grid.addColumn(event -> event.correlationId() == null ? "" : event.correlationId()).setHeader("Correlacion").setKey("correlationId")
                .setFlexGrow(1);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total + " accesos"));
        grid.addItemClickListener(click -> EventDetailDialog.of(click.getItem()).open());

        add(new H2("Accesos"), filters, toolbar, grid);
        expand(grid);
    }

    /** Los filtros de la URL; lo que no viene se queda vacio, aunque la pantalla ya estuviera abierta. */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(username, ipAddress, type, outcome, from, to)) {
            filter.clear();
        }
        QueryParameters parameters = event.getLocation().getQueryParameters();
        parameters.getSingleParameter("username").ifPresent(username::setValue);
        parameters.getSingleParameter("ipAddress").ifPresent(ipAddress::setValue);
        parameters.getSingleParameter("type").ifPresent(type::setValue);
        parameters.getSingleParameter("outcome").map(AccessOutcome::of).filter(AccessOutcome.selectable()::contains).ifPresent(outcome::setValue);
    }

    /** Vuelve a pedir la lista con el filtro que ya pidio. */
    void refresh() {
        pages.refresh();
    }

    /**
     * Un filtro cambio. Una IP se busca entera: mientras no lo sea no se pide nada y la lista sigue con
     * lo ultimo que pidio, como en mto-frontend (el servicio la rechazaria con un 400).
     */
    private void applyFilters() {
        String ip = ActivityView.blankToNull(ipAddress.getValue());
        boolean validIp = ip == null || isIpLiteral(ip);
        ipAddress.setInvalid(!validIp);
        if (!validIp) {
            return;
        }
        applied = new AccessFilter(ActivityView.blankToNull(username.getValue()), ip, ActivityView.blankToNull(type.getValue()),
                outcome.getValue(), Formats.startOfDay(from.getValue()), Formats.endOfDay(to.getValue()));
        refresh();
    }

    /**
     * Si un texto es una IP que el servicio lee como literal, con la misma regla que mto-frontend: una
     * IPv4 entera (10.0.0.7) o algo con forma de IPv6 (::1, fe80::1, ::ffff:10.0.0.7). Sin DNS.
     */
    public static boolean isIpLiteral(String value) {
        String text = value == null ? "" : value.trim();
        Matcher ipv4 = IPV4.matcher(text);
        if (ipv4.matches()) {
            for (int part = 1; part <= 4; part++) {
                if (Integer.parseInt(ipv4.group(part)) > 255) {
                    return false;
                }
            }
            return true;
        }
        return text.contains(":") && IPV6.matcher(text).matches();
    }

    private AccessFilter filter() {
        return applied;
    }

    /** Una pagina con su total: una peticion ({@link LazyPages}). */
    private LazyPages.Page<AccessEventDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(client.access(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

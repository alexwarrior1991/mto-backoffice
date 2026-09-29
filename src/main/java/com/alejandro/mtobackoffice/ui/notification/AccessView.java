package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.notification.AccessEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.AccessFilter;
import com.alejandro.mtobackoffice.client.dto.notification.AccessOutcome;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.security.NotificationRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
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
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Stream;

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
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(username, ipAddress, type, outcome, from, to)) {
            filter.addValueChangeListener(change -> refresh());
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
        grid.setItems(this::fetch, this::count);
        grid.addItemClickListener(click -> EventDetailDialog.of(click.getItem()).open());

        add(new H2("Accesos"), filters, toolbar, grid);
        expand(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters parameters = event.getLocation().getQueryParameters();
        parameters.getSingleParameter("username").ifPresent(username::setValue);
        parameters.getSingleParameter("ipAddress").ifPresent(ipAddress::setValue);
        parameters.getSingleParameter("type").ifPresent(type::setValue);
        parameters.getSingleParameter("outcome").map(AccessOutcome::of).filter(AccessOutcome.selectable()::contains).ifPresent(outcome::setValue);
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private AccessFilter filter() {
        return new AccessFilter(ActivityView.blankToNull(username.getValue()), ActivityView.blankToNull(ipAddress.getValue()),
                ActivityView.blankToNull(type.getValue()), outcome.getValue(), Formats.startOfDay(from.getValue()), Formats.endOfDay(to.getValue()));
    }

    private Stream<AccessEventDto> fetch(Query<AccessEventDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<AccessEventDto> page = client.access(filter(), query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " accesos");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<AccessEventDto, Void> query) {
        try {
            long total = client.access(filter(), 0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " accesos");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

package com.alejandro.mtobackoffice.ui.notification;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityCategory;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityEventDto;
import com.alejandro.mtobackoffice.client.dto.notification.ActivityFilter;
import com.alejandro.mtobackoffice.client.dto.notification.ActivitySeverity;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.notification.NotificationClient;
import com.alejandro.mtobackoffice.configuration.security.NotificationRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
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

/**
 * El registro de actividad: todo lo que pasa en el dominio salvo los accesos, que tienen su
 * pantalla y su permiso ({@link AccessView}). Paginado y filtrado en el servidor, lo mas reciente
 * primero; una fila abre el evento entero con lo que la fuente publico.
 *
 * <p>Los filtros son los del servicio y se comparan alli (tipo, actor, origen y tipo de sujeto sin
 * distinguir mayusculas): un tipo se escribe entero ({@code maintenance.order.created}), porque el
 * catalogo de tipos es del servicio y aqui no se copia. Lo fundido (el evento de administracion de
 * Keycloak que ya cuenta el de mto-users del mismo cambio) solo se ensena si se pide. Las reglas
 * de mto-notification enlazan aqui con los filtros en la URL ({@code /actividad?category=SYSTEM}),
 * que se aplican al entrar.</p>
 */
@Route(value = NotificationRoutes.ACTIVITY, layout = MainLayout.class)
@PageTitle("Actividad")
@Menu(title = "Actividad", order = 82, icon = "vaadin:records")
@RolesAllowed(NotificationRoles.NOTIFICATION_ACTIVITY_READ)
public class ActivityView extends VerticalLayout implements BeforeEnterObserver {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("occurredAt,desc");

    private final NotificationClient client;
    private final ComboBox<ActivityCategory> category = new ComboBox<>("Categoria");
    private final TextField type = new TextField("Tipo");
    private final TextField actor = new TextField("Quien");
    private final TextField subjectType = new TextField("Tipo de sujeto");
    private final TextField subjectId = new TextField("Id de sujeto");
    private final ComboBox<ActivitySeverity> severity = new ComboBox<>("Gravedad");
    private final TextField sourceService = new TextField("Origen");
    private final DatePicker from = new DatePicker("Desde");
    private final DatePicker to = new DatePicker("Hasta");
    private final Checkbox includeSuperseded = new Checkbox("Incluir los fundidos");
    private final Span count = new Span();
    private final Grid<ActivityEventDto> grid = new Grid<>();
    private LazyPages<ActivityEventDto> pages;

    public ActivityView(NotificationClient client) {
        this.client = client;
        setSizeFull();
        category.setId("activity-category");
        category.setItems(ActivityCategory.selectableForActivity());
        category.setItemLabelGenerator(ActivityCategory::label);
        category.setClearButtonVisible(true);
        severity.setId("activity-severity");
        severity.setItems(ActivitySeverity.selectable());
        severity.setItemLabelGenerator(ActivitySeverity::label);
        severity.setClearButtonVisible(true);
        type.setId("activity-type");
        type.setPlaceholder("maintenance.order.created");
        actor.setId("activity-actor");
        subjectType.setId("activity-subject-type");
        subjectId.setId("activity-subject-id");
        sourceService.setId("activity-source");
        sourceService.setPlaceholder("mto-maintenance");
        for (TextField text : List.of(type, actor, subjectType, subjectId, sourceService)) {
            text.setClearButtonVisible(true);
            text.setValueChangeMode(ValueChangeMode.LAZY);
        }
        from.setId("activity-from");
        to.setId("activity-to");
        includeSuperseded.setId("activity-include-superseded");
        includeSuperseded.setTooltipText("Los eventos de administracion de Keycloak que ya cuenta el de mto-users del mismo cambio");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(category, type, actor, subjectType, subjectId, severity, sourceService, from, to,
                includeSuperseded)) {
            filter.addValueChangeListener(change -> refresh());
        }
        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> refresh());

        FlexLayout filters = new FlexLayout(category, type, actor, subjectType, subjectId, severity, sourceService, from, to, includeSuperseded);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, reload);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);

        grid.setId("activity-grid");
        grid.addColumn(event -> Formats.dateTime(event.occurredAt())).setHeader("Cuando").setKey("occurredAt").setSortProperty("occurredAt")
                .setSortable(true).setAutoWidth(true);
        grid.addColumn(event -> event.category() == null ? "" : event.category().label()).setHeader("Categoria").setKey("category").setAutoWidth(true);
        grid.addColumn(ActivityEventDto::type).setHeader("Tipo").setKey("type").setSortProperty("type").setSortable(true).setAutoWidth(true);
        grid.addColumn(event -> event.severity() == null ? "" : event.severity().label()).setHeader("Gravedad").setKey("severity")
                .setSortProperty("severity").setSortable(true).setAutoWidth(true);
        grid.addColumn(event -> event.actor() == null ? "" : event.actor().describe()).setHeader("Quien").setKey("actor").setAutoWidth(true);
        grid.addColumn(event -> event.subject() == null ? "" : event.subject().describe()).setHeader("Sobre que").setKey("subject").setFlexGrow(1);
        grid.addColumn(ActivityEventDto::sourceService).setHeader("Origen").setKey("source").setAutoWidth(true);
        grid.addColumn(event -> event.eventCount() > 1 ? "x" + event.eventCount() : "").setHeader("Eventos").setKey("count").setAutoWidth(true);
        grid.addColumn(event -> event.supersededBy() == null ? "" : "Fundida").setHeader("").setKey("superseded").setAutoWidth(true);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total + " eventos"));
        grid.addItemClickListener(click -> showEvent(click.getItem()));

        add(new H2("Actividad"), filters, toolbar, grid);
        expand(grid);
    }

    /**
     * Los filtros que vienen en la URL (los enlaces de las notificaciones): lo que no se conoce se
     * ignora, y lo que no viene se queda vacio, aunque la pantalla ya estuviera abierta con otros.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(category, type, actor, subjectType, subjectId, severity, sourceService, from, to)) {
            filter.clear();
        }
        includeSuperseded.setValue(false);
        QueryParameters parameters = event.getLocation().getQueryParameters();
        parameters.getSingleParameter("category").map(ActivityCategory::of)
                .filter(ActivityCategory.selectableForActivity()::contains).ifPresent(category::setValue);
        parameters.getSingleParameter("type").ifPresent(type::setValue);
        parameters.getSingleParameter("actorUsername").ifPresent(actor::setValue);
        parameters.getSingleParameter("subjectType").ifPresent(subjectType::setValue);
        parameters.getSingleParameter("subjectId").ifPresent(subjectId::setValue);
        parameters.getSingleParameter("severity").map(ActivitySeverity::of)
                .filter(ActivitySeverity.selectable()::contains).ifPresent(severity::setValue);
        parameters.getSingleParameter("sourceService").ifPresent(sourceService::setValue);
        parameters.getSingleParameter("includeSuperseded").map(Boolean::parseBoolean).ifPresent(includeSuperseded::setValue);
    }

    void refresh() {
        pages.refresh();
    }

    /** La fila no trae el payload: la linea entera se pide a su id, como en mto-frontend. */
    private void showEvent(ActivityEventDto row) {
        try {
            EventDetailDialog.of(client.activityEvent(row.id())).open();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private ActivityFilter filter() {
        return new ActivityFilter(category.getValue(), blankToNull(type.getValue()), blankToNull(actor.getValue()),
                blankToNull(subjectType.getValue()), blankToNull(subjectId.getValue()), severity.getValue(), blankToNull(sourceService.getValue()),
                Formats.startOfDay(from.getValue()), Formats.endOfDay(to.getValue()), Boolean.TRUE.equals(includeSuperseded.getValue()));
    }

    static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /** Una pagina con su total: una peticion ({@link LazyPages}). */
    private LazyPages.Page<ActivityEventDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(client.activity(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

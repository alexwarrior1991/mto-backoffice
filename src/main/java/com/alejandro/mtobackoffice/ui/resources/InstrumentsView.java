package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentDto;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentFilter;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentKind;
import com.alejandro.mtobackoffice.client.dto.resources.InstrumentUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * Los instrumentos de mto-resources (pertigas, detectores de tension, equipos de medida), paginados,
 * buscados y filtrados en el servidor como en mto-frontend, tambien por «calibracion caduca antes de»,
 * con el estado de su calibracion como el de las cualificaciones ({@link Expiry}). Darlos de alta y
 * modificarlos pide {@code resources-fleet-write}; retirarlos, {@code resources-delete}; volver a
 * ofrecerlo es un {@code PATCH active=true}. El historial es lectura y lo ofrece toda fila. Es el enlace
 * de los avisos de calibracion de mto-notification ({@code /recursos/instrumentos}).
 */
@Route(value = ResourcesRoutes.INSTRUMENTS, layout = MainLayout.class)
@PageTitle("Instrumentos")
@Menu(title = "Instrumentos", order = 77, icon = "vaadin:tools")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class InstrumentsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    static final String EXPIRED = "Caducada";
    static final String NO_CALIBRATION = "Sin calibracion";
    private static final List<String> DEFAULT_SORT = List.of("code,asc");

    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;

    private final TextField search = new TextField("Buscar");
    private final Select<InstrumentKind> kind = new Select<>();
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Retirados");
    private final DatePicker expiresBefore = new DatePicker("Calibracion caduca antes de");
    private final Span count = new Span();
    private final Grid<InstrumentDto> grid = new Grid<>();
    private LazyPages<InstrumentDto> pages;

    public InstrumentsView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_FLEET_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();

        search.setId("instruments-search");
        search.setPlaceholder("Codigo, nombre, numero de serie");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        kind.setLabel("Tipo");
        kind.setId("instruments-kind");
        kind.setItems(InstrumentKind.selectable());
        kind.setItemLabelGenerator(value -> value == null ? "Todos" : value.label());
        kind.setEmptySelectionAllowed(true);
        kind.setEmptySelectionCaption("Todos");
        state.setId("instruments-state");
        expiresBefore.setId("instruments-expires-before");
        expiresBefore.setClearButtonVisible(true);
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(search, kind, state, expiresBefore)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo instrumento", VaadinIcon.PLUS.create(), click -> new InstrumentEditorDialog(null, clients, this::refresh).open());
        create.setId("instrument-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        FlexLayout filters = new FlexLayout(search, kind, state, expiresBefore);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Instrumentos"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<InstrumentDto> buildGrid() {
        grid.setId("instruments-grid");
        grid.addColumn(InstrumentDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(InstrumentDto::name).setHeader("Nombre").setKey("name").setSortProperty("name").setSortable(true).setAutoWidth(true);
        grid.addColumn(instrument -> instrument.kind() == null ? "" : instrument.kind().label()).setHeader("Tipo").setKey("kind")
                .setSortProperty("kind").setSortable(true).setAutoWidth(true);
        grid.addColumn(InstrumentDto::serialNumber).setHeader("N. serie").setKey("serialNumber").setAutoWidth(true);
        grid.addColumn(instrument -> ResourcesFormats.date(instrument.calibratedOn())).setHeader("Calibrado").setKey("calibratedOn")
                .setAutoWidth(true);
        grid.addColumn(instrument -> ResourcesFormats.date(instrument.calibrationExpiresOn())).setHeader("Calibracion hasta")
                .setKey("calibrationExpiresOn").setSortProperty("calibrationExpiresOn").setSortable(true).setAutoWidth(true);
        grid.addColumn(InstrumentsView::calibration).setHeader("Calibracion").setKey("calibration").setAutoWidth(true);
        grid.addColumn(instrument -> ResourcesFormats.vehicleState(instrument.active())).setHeader("Estado").setKey("active")
                .setSortProperty("active").setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> new InstrumentEditorDialog(event.getItem(), clients, this::refresh).open());
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total == 1 ? "1 instrumento" : total + " instrumentos"));
        return grid;
    }

    /** El estado de la calibracion; si ha caducado lo dice el servicio. */
    static String calibration(InstrumentDto instrument) {
        return Expiry.state(instrument.calibrationExpiresOn(), instrument.calibrationExpired(), EXPIRED, NO_CALIBRATION);
    }

    /** La columna existe siempre: el historial es lectura. */
    private Component rowActions(InstrumentDto instrument) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(Screens.rowButton("instrument-history-" + instrument.id(), VaadinIcon.CLOCK, "Historial de " + instrument.code(),
                click -> ResourcesHistory.dialog(instrument.code(), (page, size) -> clients.instruments().revisions(instrument.id(), page, size),
                        ResourcesHistory::instrument).open()));
        if (canWrite) {
            actions.add(Screens.rowButton("instrument-edit-" + instrument.id(), VaadinIcon.EDIT, "Modificar " + instrument.code(),
                    click -> new InstrumentEditorDialog(instrument, clients, this::refresh).open()));
        }
        if (instrument.isActive() && canDelete) {
            Button retire = Screens.rowButton("instrument-retire-" + instrument.id(), VaadinIcon.BAN, "Retirar " + instrument.code(),
                    click -> Screens.confirm("Retirar " + instrument.label(),
                            "No se borra: deja de ofrecerse en los turnos nuevos, y los turnos que lo llevan lo siguen nombrando. "
                                    + "Se puede volver a ofrecer.", "Retirar", () -> retire(instrument)));
            retire.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(retire);
        } else if (!instrument.isActive() && canWrite) {
            actions.add(Screens.rowButton("instrument-activate-" + instrument.id(), VaadinIcon.CHECK_CIRCLE, "Volver a ofrecer " + instrument.code(),
                    click -> activate(instrument)));
        }
        return actions;
    }

    private void retire(InstrumentDto instrument) {
        try {
            clients.instruments().deactivate(instrument.id());
            Screens.success("Retirado " + instrument.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void activate(InstrumentDto instrument) {
        try {
            clients.instruments().update(instrument.id(), MergePatch.of(InstrumentUpdateRequest.active(true), instrument.version()));
            Screens.success("Se vuelve a ofrecer " + instrument.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        pages.refresh();
    }

    private InstrumentFilter filter() {
        return new InstrumentFilter(search.getValue(), kind.getValue(), state.getValue() == null ? null : state.getValue().value(),
                expiresBefore.getValue());
    }

    private LazyPages.Page<InstrumentDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.instruments().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

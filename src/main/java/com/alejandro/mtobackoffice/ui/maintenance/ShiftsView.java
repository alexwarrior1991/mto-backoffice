package com.alejandro.mtobackoffice.ui.maintenance;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.maintenance.PossessionType;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftDto;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftFilter;
import com.alejandro.mtobackoffice.client.dto.maintenance.ShiftStatus;
import com.alejandro.mtobackoffice.client.dto.maintenance.TeamDto;
import com.alejandro.mtobackoffice.configuration.security.MaintenanceRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.Pickers;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Los turnos nocturnos, paginados y filtrados en el servidor (fechas, equipo, via, paquete, estado
 * y posesion), el mas reciente primero. Una fila abre la ficha del turno, donde se inicia, se
 * asignan tareas, se trabajan y se cierra.
 */
@Route(value = MaintenanceRoutes.SHIFTS, layout = MainLayout.class)
@PageTitle("Turnos")
@Menu(title = "Turnos", order = 62, icon = "vaadin:moon")
@RolesAllowed(MaintenanceRoles.MAINTENANCE_READ)
public class ShiftsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("shiftDate,desc");

    private final MaintenanceClients clients;
    private final MaintenanceNames names;
    private final DatePicker dateFrom = new DatePicker("Desde");
    private final DatePicker dateTo = new DatePicker("Hasta");
    private final ComboBox<TeamDto> team = new ComboBox<>("Equipo");
    private final ComboBox<RefItem> track;
    private final ComboBox<RefItem> executionPackage;
    private final ComboBox<ShiftStatus> status = new ComboBox<>("Estado");
    private final ComboBox<PossessionType> possession = new ComboBox<>("Posesion");
    private final Span count = new Span();
    private final Grid<ShiftDto> grid = new Grid<>();
    private LazyPages<ShiftDto> pages;

    public ShiftsView(MaintenanceClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = MaintenanceNames.of(clients, authentication);
        MaintenanceCatalogs catalogs = new MaintenanceCatalogs(clients.catalog());
        setSizeFull();

        dateFrom.setId("shifts-from");
        dateTo.setId("shifts-to");
        team.setId("shifts-team");
        team.setItems(catalogs.teams());
        team.setItemLabelGenerator(TeamDto::label);
        track = Pickers.reference("Via", names.tracks());
        track.setId("shifts-track");
        executionPackage = Pickers.reference("Paquete", names.packages());
        executionPackage.setId("shifts-package");
        status.setId("shifts-status");
        status.setItems(ShiftStatus.selectable());
        status.setItemLabelGenerator(ShiftStatus::label);
        possession.setId("shifts-possession");
        possession.setItems(PossessionType.selectable());
        possession.setItemLabelGenerator(PossessionType::label);
        for (ComboBox<?> combo : List.of(team, track, executionPackage, status, possession)) {
            combo.setClearButtonVisible(true);
        }
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(dateFrom, dateTo, team, track, executionPackage, status, possession)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nuevo turno", VaadinIcon.PLUS.create(), click -> new ShiftEditorDialog(null, clients, names, catalogs,
                created -> UI.getCurrent().navigate(ShiftDetailView.class, ShiftDetailView.parametersOf(created.id()))).open());
        create.setId("shift-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // Sin config-read no hay vias entre las que elegir, y un turno necesita al menos una.
        create.setVisible(authentication.hasRole(MaintenanceRoles.MAINTENANCE_WRITE) && names.readsConfiguration());

        FlexLayout filters = new FlexLayout(dateFrom, dateTo, team, track, executionPackage, status, possession);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Turnos"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<ShiftDto> buildGrid() {
        grid.setId("shifts-grid");
        grid.addColumn(ShiftDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(shift -> MaintenanceFormats.date(shift.shiftDate())).setHeader("Fecha").setKey("shiftDate")
                .setSortProperty("shiftDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(shift -> shift.team() == null ? "" : shift.team().label()).setHeader("Equipo").setKey("team").setAutoWidth(true);
        grid.addColumn(shift -> shift.possessionType() == null ? "" : shift.possessionType().label()).setHeader("Posesion")
                .setKey("possession").setSortProperty("possessionType").setSortable(true).setAutoWidth(true);
        grid.addColumn(shift -> shift.trackIds().stream().map(names::trackName).collect(Collectors.joining(", "))).setHeader("Vias")
                .setKey("tracks").setFlexGrow(1);
        grid.addColumn(shift -> MaintenanceFormats.kpRange(shift.startKp(), shift.endKp())).setHeader("KP").setKey("kp").setAutoWidth(true);
        grid.addColumn(shift -> shift.status() == null ? "" : shift.status().label()).setHeader("Estado").setKey("status")
                .setSortProperty("status").setSortable(true).setAutoWidth(true);
        grid.addColumn(shift -> Formats.dateTime(shift.actualStart())).setHeader("Inicio").setKey("actualStart").setAutoWidth(true);
        grid.addColumn(shift -> Formats.dateTime(shift.actualEnd())).setHeader("Fin").setKey("actualEnd").setAutoWidth(true);
        grid.addColumn(shift -> shift.netWorkMinutes() == null ? "" : shift.netWorkMinutes() + " min").setHeader("Neto")
                .setKey("netMinutes").setAutoWidth(true);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total + " turnos"));
        grid.addItemClickListener(click -> UI.getCurrent().navigate(ShiftDetailView.class, ShiftDetailView.parametersOf(click.getItem().id())));
        return grid;
    }

    void refresh() {
        pages.refresh();
    }

    private ShiftFilter filter() {
        return new ShiftFilter(dateFrom.getValue(), dateTo.getValue(), team.getValue() == null ? null : team.getValue().id(),
                track.getValue() == null ? null : track.getValue().id(), executionPackage.getValue() == null ? null : executionPackage.getValue().id(),
                status.getValue(), possession.getValue());
    }

    /** Una pagina con su total: una peticion ({@link LazyPages}). */
    private LazyPages.Page<ShiftDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.shifts().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}

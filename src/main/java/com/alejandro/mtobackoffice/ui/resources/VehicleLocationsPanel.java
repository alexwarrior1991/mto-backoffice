package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.ui.maintenance.ShiftDetailView;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * El historial de ubicaciones de un vehiculo, la mas reciente primero (sin {@code sort}: ordena el
 * servicio), paginado y filtrado por dias: «Desde» y «Hasta» incluyen los dos dias enteros, asi que
 * {@code to} viaja como el principio del dia siguiente, porque el servicio no lo incluye. Una ubicacion
 * que dejo un turno enlaza con el turno, si se puede leer mantenimiento.
 */
class VehicleLocationsPanel extends LazyPanel {

    static final int PAGE_SIZE = 20;

    private final Supplier<UUID> vehicleId;
    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean readsMaintenance;
    private final DatePicker from = new DatePicker("Desde");
    private final DatePicker to = new DatePicker("Hasta");
    private final Grid<VehicleLocationDto> grid = new Grid<>();
    private LazyPages<VehicleLocationDto> pages;

    VehicleLocationsPanel(Supplier<UUID> vehicleId, ResourcesClients clients, ResourcesNames names, boolean readsMaintenance) {
        this.vehicleId = vehicleId;
        this.clients = clients;
        this.names = names;
        this.readsMaintenance = readsMaintenance;
        from.setId("locations-from");
        to.setId("locations-to");
        from.setClearButtonVisible(true);
        to.setClearButtonVisible(true);
        from.addValueChangeListener(change -> reloadIfLoaded());
        to.addValueChangeListener(change -> reloadIfLoaded());
        HorizontalLayout filters = new HorizontalLayout(from, to);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);

        grid.setId("locations-grid");
        grid.addColumn(location -> location.observedAt() == null ? "" : Formats.dateTime(location.observedAt())).setHeader("Cuando").setKey("when")
                .setAutoWidth(true);
        grid.addColumn(location -> ResourcesFormats.place(location, names)).setHeader("Donde").setKey("where").setAutoWidth(true);
        grid.addColumn(VehicleLocationDto::accessPointLabel).setHeader("Acceso").setKey("access").setAutoWidth(true);
        grid.addColumn(location -> location.source() == null ? "" : location.source().label()).setHeader("Origen").setKey("source").setAutoWidth(true);
        grid.addColumn(VehicleLocationDto::recordedBy).setHeader("Registrada por").setKey("recordedBy").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::shiftLink)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        grid.setPageSize(PAGE_SIZE);
        grid.setHeight("420px");
        add(filters, grid);
    }

    /** El turno que dejo la ubicacion, si fue uno y se puede leer mantenimiento. */
    private Component shiftLink(VehicleLocationDto location) {
        UUID shift = location.shiftId();
        if (shift == null || !readsMaintenance) {
            return new HorizontalLayout();
        }
        // Con el nombre de la fila, como en mto-frontend: todas las filas tienen este boton.
        return Screens.rowButton("location-shift-" + location.id(), VaadinIcon.CALENDAR_CLOCK, "Ver el turno de la ubicacion del "
                        + Formats.dateTime(location.observedAt()),
                click -> UI.getCurrent().navigate(ShiftDetailView.class, ShiftDetailView.parametersOf(shift)));
    }

    @Override
    protected void load() {
        if (pages == null) {
            pages = LazyPages.of(grid, this::page, total -> { });
        } else {
            pages.refresh();
        }
    }

    private LazyPages.Page<VehicleLocationDto> page(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        return LazyPages.Page.of(clients.vehicles().locations(vehicleId.get(), Formats.startOfDay(from.getValue()),
                to.getValue() == null ? null : Formats.startOfDay(to.getValue().plusDays(1)), offset / size, size));
    }
}

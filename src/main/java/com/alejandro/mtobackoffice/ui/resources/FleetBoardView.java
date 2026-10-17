package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.resources.FleetPositionDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationDto;
import com.alejandro.mtobackoffice.client.dto.resources.VehicleLocationKind;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.resources.VehicleClient;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.RefItem;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * El tablero de la flota, como en mto-frontend: los vehiculos activos con posicion conocida, paginados
 * en el servidor por via y kp (con el vehiculo para desempatar), asi que con una via elegida se lee como
 * la via en orden de kp. La via y el kp son los de donde esta de verdad: un vehiculo remolcado esta
 * donde su portador ({@code effective}) y lo dice. Filtra por EP, via y tipo de ubicacion; «Actualizar»
 * vuelve a pedirlo (no se sondea). Registrar donde esta pide {@code resources-location-write}.
 */
@Route(value = ResourcesRoutes.BOARD, layout = MainLayout.class)
@PageTitle("Tablero de la flota")
@Menu(title = "Tablero de la flota", order = 73, icon = "vaadin:table")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class FleetBoardView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";

    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean canLocate;
    private final ComboBox<RefItem> executionPackage = new ComboBox<>("EP");
    private final ComboBox<RefItem> track = new ComboBox<>("Via");
    private final Select<VehicleLocationKind> kind = new Select<>();
    private final Span count = new Span();
    private final Grid<FleetPositionDto> grid = new Grid<>();
    private LazyPages<FleetPositionDto> pages;

    public FleetBoardView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = ResourcesNames.of(clients, authentication);
        this.canLocate = authentication.hasRole(ResourcesRoles.RESOURCES_LOCATION_WRITE);
        setSizeFull();

        executionPackage.setId("board-package");
        executionPackage.setItems(names.packages());
        executionPackage.setItemLabelGenerator(RefItem::label);
        executionPackage.setClearButtonVisible(true);
        track.setId("board-track");
        track.setItems(names.tracks(null));
        track.setItemLabelGenerator(RefItem::label);
        track.setClearButtonVisible(true);
        executionPackage.setVisible(names.readsConfiguration());
        track.setVisible(names.readsConfiguration());
        // Las vias que se ofrecen son las del EP elegido; una que ya no es de el se quita.
        executionPackage.addValueChangeListener(change -> {
            RefItem chosen = track.getValue();
            List<RefItem> tracks = names.tracks(change.getValue() == null ? null : change.getValue().id());
            track.setItems(tracks);
            track.setValue(chosen != null && tracks.contains(chosen) ? chosen : null);
        });
        kind.setLabel("Donde esta");
        kind.setId("board-kind");
        kind.setItems(VehicleLocationKind.filterable());
        kind.setItemLabelGenerator(value -> value == null ? "Todos" : value.label());
        kind.setEmptySelectionAllowed(true);
        kind.setEmptySelectionCaption("Todos");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(executionPackage, track, kind)) {
            filter.addValueChangeListener(change -> refresh());
        }
        Button refresh = new Button("Actualizar", VaadinIcon.REFRESH.create(), click -> refresh());
        refresh.setId("board-refresh");

        FlexLayout filters = new FlexLayout(executionPackage, track, kind);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, refresh);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Tablero de la flota"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<FleetPositionDto> buildGrid() {
        grid.setId("board-grid");
        grid.addColumn(row -> row.where() == null || row.where().trackId() == null ? "" : names.trackName(row.where().trackId())).setHeader("Via")
                .setKey("track").setAutoWidth(true);
        grid.addColumn(row -> row.where() == null || row.where().kp() == null ? "" : row.where().kp().toPlainString()).setHeader("KP").setKey("kp")
                .setAutoWidth(true);
        grid.addColumn(row -> row.vehicle() == null ? "" : row.vehicle().label()).setHeader("Vehiculo").setKey("vehicle").setAutoWidth(true);
        grid.addColumn(row -> row.vehicle() == null || row.vehicle().mobility() == null ? "" : row.vehicle().mobility().label()).setHeader("Movilidad")
                .setKey("mobility").setAutoWidth(true);
        grid.addColumn(FleetBoardView::kindOf).setHeader("Donde esta").setKey("kind").setAutoWidth(true);
        grid.addColumn(row -> row.where() == null || row.where().parkingSite() == null ? "" : row.where().parkingSite().code()).setHeader("Sitio")
                .setKey("site").setAutoWidth(true);
        grid.addColumn(row -> ResourcesFormats.operational(row.operationalStatus(), null)).setHeader("Estado operativo").setKey("status")
                .setAutoWidth(true);
        grid.addColumn(row -> row.position() == null || row.position().observedAt() == null ? "" : Formats.dateTime(row.position().observedAt()))
                .setHeader("Desde").setKey("since").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setPageSize(PAGE_SIZE);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total == 1 ? "1 vehiculo" : total + " vehiculos"));
        return grid;
    }

    /** El tipo de su propia ubicacion; uno remolcado o transportado dice con quien va. */
    private static String kindOf(FleetPositionDto row) {
        VehicleLocationDto own = row.position();
        if (own == null) {
            return "";
        }
        if (own.kind() == VehicleLocationKind.ATTACHED && own.carrier() != null) {
            String how = own.attachment() == null ? own.kind().label() : own.attachment().label();
            return how + " por " + own.carrier().code();
        }
        return own.kind() == null ? "" : own.kind().label();
    }

    private Component rowActions(FleetPositionDto row) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (row.vehicle() == null) {
            return actions;
        }
        actions.add(Screens.rowButton("board-open-" + row.vehicle().id(), VaadinIcon.ARROW_RIGHT, "Abrir " + row.vehicle().code(),
                click -> UI.getCurrent().navigate(VehicleDetailView.class, VehicleDetailView.parametersOf(row.vehicle().id()))));
        if (canLocate) {
            actions.add(Screens.rowButton("board-locate-" + row.vehicle().id(), VaadinIcon.MAP_MARKER, "Registrar donde esta " + row.vehicle().code(),
                    click -> locate(row)));
        }
        return actions;
    }

    /** El dialogo necesita el vehiculo entero (su modelo decide que se ofrece): se lee al pedirlo. */
    private void locate(FleetPositionDto row) {
        try {
            new VehicleLocationDialog(clients.vehicles().findById(row.vehicle().id()), clients, names, recorded -> refresh()).open();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    void refresh() {
        pages.refresh();
    }

    private LazyPages.Page<FleetPositionDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        return LazyPages.Page.of(clients.vehicles().positions(executionPackage.getValue() == null ? null : executionPackage.getValue().id(),
                track.getValue() == null ? null : track.getValue().id(), kind.getValue(), offset / size, size, VehicleClient.BY_TRACK_AND_KP));
    }
}

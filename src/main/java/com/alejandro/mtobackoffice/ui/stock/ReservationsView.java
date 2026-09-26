package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationDto;
import com.alejandro.mtobackoffice.client.dto.stock.ReservationStatus;
import com.alejandro.mtobackoffice.client.dto.stock.WarehouseSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.stock.AssemblyClient;
import com.alejandro.mtobackoffice.client.stock.MaterialClient;
import com.alejandro.mtobackoffice.client.stock.MovementClient;
import com.alejandro.mtobackoffice.client.stock.ProjectClient;
import com.alejandro.mtobackoffice.client.stock.ReservationClient;
import com.alejandro.mtobackoffice.client.stock.SupplierClient;
import com.alejandro.mtobackoffice.client.stock.WarehouseClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.RevisionsDialog;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Las reservas de material para un proyecto, paginadas en el servidor con sus filtros. Solo una
 * reserva activa cambia: se modifica, se libera (vuelve al disponible sin movimiento), se consume
 * (baja el fisico) o se cancela (pide {@code stock-delete}); tambien se consume con una salida,
 * que ademas lleva referencia y notas. Nada de eso lo decide la pantalla: si la reserva ya no esta
 * activa, el servicio responde 422 {@code RES-001}. Cualquier fila tiene su historial.
 */
@Route(value = StockRoutes.RESERVATIONS, layout = MainLayout.class)
@PageTitle("Reservas")
@Menu(title = "Reservas", order = 56, icon = "vaadin:bookmark")
@RolesAllowed(StockRoles.STOCK_READ)
public class ReservationsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("reservedAt,desc");

    private final StockClients clients;
    private final boolean canWrite;
    private final boolean canCancel;

    private final ComboBox<ReservationStatus> status = new ComboBox<>("Estado");
    private final ComboBox<WarehouseSummaryDto> warehouse;
    private final ComboBox<MaterialSummaryDto> material;
    private final ComboBox<ProjectSummaryDto> project;
    private final Span count = new Span();
    private final Grid<ReservationDto> grid = new Grid<>();

    public ReservationsView(MaterialClient materials, WarehouseClient warehouses, SupplierClient suppliers, ProjectClient projects,
                            MovementClient movements, ReservationClient reservations, AssemblyClient assemblies, AuthenticationContext authentication) {
        this.clients = new StockClients(materials, warehouses, suppliers, projects, movements, reservations, assemblies);
        this.canWrite = authentication.hasRole(StockRoles.STOCK_WRITE);
        this.canCancel = authentication.hasRole(StockRoles.STOCK_DELETE);
        setSizeFull();
        status.setId("reservations-status");
        status.setItems(ReservationStatus.values());
        status.setItemLabelGenerator(ReservationStatus::label);
        status.setClearButtonVisible(true);
        status.setValue(ReservationStatus.ACTIVE);
        warehouse = StockPickers.warehouse("Almacen", warehouses);
        warehouse.setId("reservations-warehouse");
        material = StockPickers.material("Material", materials);
        material.setId("reservations-material");
        project = StockPickers.project("Proyecto", projects);
        project.setId("reservations-project");
        for (Component filter : List.of(status, warehouse, material, project)) {
            ((com.vaadin.flow.component.HasValue<?, ?>) filter).addValueChangeListener(change -> refresh());
        }
        Button create = new Button("Nueva reserva", VaadinIcon.PLUS.create(), click -> new ReservationDialog(null, clients, this::refresh).open());
        create.setId("reservation-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);
        HorizontalLayout toolbar = new HorizontalLayout(status, warehouse, material, project, count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Reservas"), toolbar, buildGrid());
        expand(grid);
    }

    private Component buildGrid() {
        grid.setId("reservations-grid");
        grid.addColumn(dto -> StockFormats.dateTime(dto.reservedAt())).setHeader("Reservada").setKey("reservedAt").setSortProperty("reservedAt").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> dto.material() == null ? "" : dto.material().label()).setHeader("Material").setKey("material").setFlexGrow(1);
        grid.addColumn(dto -> dto.warehouse() == null ? "" : dto.warehouse().code()).setHeader("Almacen").setKey("warehouse").setAutoWidth(true);
        grid.addColumn(dto -> dto.project() == null ? "" : dto.project().code()).setHeader("Proyecto").setKey("project").setAutoWidth(true);
        grid.addColumn(dto -> StockFormats.quantity(dto.quantity())).setHeader("Cantidad").setKey("quantity").setSortProperty("quantity").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> dto.status() == null ? "" : dto.status().label()).setHeader("Estado").setKey("status").setSortProperty("status").setSortable(true).setAutoWidth(true);
        grid.addColumn(dto -> StockFormats.dateTime(dto.releasedAt())).setHeader("Cerrada").setKey("releasedAt").setAutoWidth(true);
        grid.addColumn(dto -> dto.audit() == null || dto.audit().createdBy() == null ? "" : dto.audit().createdBy()).setHeader("Por").setKey("createdBy").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        return grid;
    }

    /** Solo una reserva activa cambia; el historial lo tiene cualquiera, y es lectura. */
    private Component rowActions(ReservationDto reservation) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (!reservation.isActive()) {
            actions.add(historyButton(reservation));
            return actions;
        }
        if (canWrite) {
            actions.add(action("edit-", reservation, VaadinIcon.EDIT, "Modificar", click -> new ReservationDialog(reservation, clients, this::refresh).open()));
            actions.add(action("output-", reservation, VaadinIcon.ARROW_UP, "Salida con esta reserva", click -> outputFrom(reservation)));
            actions.add(action("consume-", reservation, VaadinIcon.CHECK, "Consumir", click -> confirm("Consumir la reserva",
                    "Baja el fisico ademas del reservado: el material sale del almacen. Sin referencia ni notas; para eso, la salida.",
                    "Consumir", () -> call(clients.reservations()::consume, reservation, "Reserva consumida"))));
            actions.add(action("release-", reservation, VaadinIcon.UNLOCK, "Liberar", click -> confirm("Liberar la reserva",
                    "El material vuelve al disponible sin ningun movimiento.", "Liberar",
                    () -> call(clients.reservations()::release, reservation, "Reserva liberada"))));
        }
        if (canCancel) {
            Button cancel = action("cancel-", reservation, VaadinIcon.TRASH, "Cancelar", click -> confirm("Cancelar la reserva",
                    "Queda cancelada y el material vuelve al disponible. No se puede deshacer.", "Cancelar la reserva",
                    () -> call(clients.reservations()::cancel, reservation, "Reserva cancelada")));
            cancel.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(cancel);
        }
        actions.add(historyButton(reservation));
        return actions;
    }

    private Button historyButton(ReservationDto reservation) {
        return action("history-", reservation, VaadinIcon.CLOCK, "Historial", click -> new RevisionsDialog<>(
                "la reserva de " + reservation.material().code() + " para " + reservation.project().code(),
                (page, size) -> clients.reservations().revisions(reservation.id(), page, size), ReservationsView::describe).open());
    }

    /** Una linea con la reserva tal como esta, o como quedo en una revision. */
    static String describe(ReservationDto reservation) {
        String unit = reservation.material() == null || reservation.material().unitOfMeasure() == null ? "" : " " + reservation.material().unitOfMeasure();
        return StockFormats.quantity(reservation.quantity()) + unit + " de " + code(reservation.material() == null ? null : reservation.material().code())
                + " en " + code(reservation.warehouse() == null ? null : reservation.warehouse().code())
                + " para " + code(reservation.project() == null ? null : reservation.project().code())
                + " · " + (reservation.status() == null ? "" : reservation.status().label());
    }

    private static String code(String value) {
        return value == null ? "?" : value;
    }

    private static Button action(String prefix, ReservationDto reservation, VaadinIcon icon, String tooltip,
                                 com.vaadin.flow.component.ComponentEventListener<com.vaadin.flow.component.ClickEvent<Button>> listener) {
        Button button = new Button(icon.create(), listener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        button.setTooltipText(tooltip);
        button.setId(prefix + reservation.id());
        return button;
    }

    private static void confirm(String header, String text, String confirmText, Runnable action) {
        ConfirmDialog dialog = new ConfirmDialog(header, text, confirmText, confirm -> action.run(), "Volver", cancel -> { });
        dialog.setConfirmButtonTheme("primary");
        dialog.open();
    }

    private void call(Function<UUID, ReservationDto> operation, ReservationDto reservation, String done) {
        try {
            ReservationDto result = operation.apply(reservation.id());
            Notification.show(done + ": " + StockFormats.quantity(result.quantity()) + " de " + result.material().code(), 3000,
                    Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    /** Una salida que consume la reserva: material, almacen y cantidad fijos, referencia y notas libres. */
    private void outputFrom(ReservationDto reservation) {
        MovementForm form = new MovementForm();
        form.setMaterialId(reservation.material());
        form.setWarehouseId(reservation.warehouse());
        form.setProjectId(reservation.project());
        form.setQuantity(reservation.quantity());
        form.setReservationId(reservation.id());
        new MovementDialog(MovementDialog.Kind.OUTPUT, form, clients, movement -> refresh()).open();
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private PageResponse<ReservationDto> search(int page, int size, List<String> sort) {
        return clients.reservations().search(warehouse.getValue() == null ? null : warehouse.getValue().id(), status.getValue(),
                project.getValue() == null ? null : project.getValue().id(), material.getValue() == null ? null : material.getValue().id(),
                page, size, sort);
    }

    private Stream<ReservationDto> fetch(Query<ReservationDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<ReservationDto> page = search(query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " reservas");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<ReservationDto, Void> query) {
        try {
            long total = search(0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " reservas");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

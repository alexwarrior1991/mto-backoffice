package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementType;
import com.alejandro.mtobackoffice.client.dto.stock.ProjectSummaryDto;
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
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * El libro de movimientos entero, paginado en el servidor con sus filtros (tipo, almacen,
 * material, proyecto, fechas inclusivas y quien lo registro), y los botones de operar. Solo se
 * anade y se lee: un apunte equivocado se corrige con un ajuste.
 */
@Route(value = StockRoutes.MOVEMENTS, layout = MainLayout.class)
@PageTitle("Movimientos")
@Menu(title = "Movimientos", order = 55, icon = "vaadin:exchange")
@RolesAllowed(StockRoles.STOCK_READ)
public class MovementsView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_SORT = List.of("occurredAt,desc");

    private final StockClients clients;
    private final ComboBox<MovementType> type = new ComboBox<>("Tipo");
    private final ComboBox<WarehouseSummaryDto> warehouse;
    private final ComboBox<MaterialSummaryDto> material;
    private final ComboBox<ProjectSummaryDto> project;
    private final DatePicker from = new DatePicker("Desde");
    private final DatePicker to = new DatePicker("Hasta");
    private final TextField user = new TextField("Registrado por");
    private final Span count = new Span();
    private final MovementGrid grid = new MovementGrid(true);

    public MovementsView(MaterialClient materials, WarehouseClient warehouses, SupplierClient suppliers, ProjectClient projects,
                         MovementClient movements, ReservationClient reservations, AssemblyClient assemblies, AuthenticationContext authentication) {
        this.clients = new StockClients(materials, warehouses, suppliers, projects, movements, reservations, assemblies);
        setSizeFull();
        type.setId("movements-type");
        type.setItems(MovementType.selectable());
        type.setItemLabelGenerator(MovementType::label);
        type.setClearButtonVisible(true);
        warehouse = StockPickers.warehouse("Almacen", warehouses);
        warehouse.setId("movements-warehouse");
        material = StockPickers.material("Material", materials);
        material.setId("movements-material");
        project = StockPickers.project("Proyecto", projects);
        project.setId("movements-project");
        from.setId("movements-from");
        to.setId("movements-to");
        user.setId("movements-user");
        user.setClearButtonVisible(true);
        user.setValueChangeMode(ValueChangeMode.LAZY);
        for (Component filter : List.of(type, warehouse, material, project, from, to, user)) {
            ((com.vaadin.flow.component.HasValue<?, ?>) filter).addValueChangeListener(change -> refresh());
        }
        HorizontalLayout filters = new HorizontalLayout(type, warehouse, material, project, from, to, user);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.setWidthFull();
        HorizontalLayout actions = new HorizontalLayout(count, StockOperations.buttons(authentication, clients, this::prefill, movement -> refresh()));
        actions.setAlignItems(FlexComponent.Alignment.BASELINE);
        actions.setWidthFull();
        actions.expand(count);
        grid.setId("movements-grid");
        grid.setPageSize(PAGE_SIZE);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        add(new H2("Movimientos"), filters, actions, grid);
        expand(grid);
    }

    private MovementForm prefill() {
        MovementForm form = new MovementForm();
        form.setMaterialId(material.getValue());
        form.setWarehouseId(warehouse.getValue());
        form.setProjectId(project.getValue());
        return form;
    }

    void refresh() {
        grid.getDataProvider().refreshAll();
    }

    private PageResponse<MovementDto> search(int page, int size, List<String> sort) {
        String who = user.getValue() == null || user.getValue().isBlank() ? null : user.getValue().trim();
        return clients.movements().search(type.getValue(), id(warehouse.getValue()), project.getValue() == null ? null : project.getValue().id(),
                material.getValue() == null ? null : material.getValue().id(), StockFormats.startOfDay(from.getValue()),
                StockFormats.endOfDay(to.getValue()), who, page, size, sort);
    }

    private static UUID id(WarehouseSummaryDto warehouse) {
        return warehouse == null ? null : warehouse.id();
    }

    private Stream<MovementDto> fetch(Query<MovementDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<MovementDto> page = search(query.getOffset() / size, size, sort.isEmpty() ? DEFAULT_SORT : sort);
            count.setText(page.page().totalElements() + " movimientos");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<MovementDto, Void> query) {
        try {
            long total = search(0, 1, DEFAULT_SORT).page().totalElements();
            count.setText(total + " movimientos");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

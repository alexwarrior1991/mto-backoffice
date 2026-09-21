package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialStockDto;
import com.alejandro.mtobackoffice.client.dto.stock.MaterialSummaryDto;
import com.alejandro.mtobackoffice.client.dto.stock.MovementDto;
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
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Las existencias: la entrada «Almacen» del menu, que es a la vez el nodo del grupo. Se elige un material (buscado en el servidor) y,
 * opcionalmente, un almacen, y se ven las cifras que calcula mto-stock (fisico, reservado por las
 * reservas activas, disponible y si esta bajo minimo), el libro de ese material y los botones de
 * operar. Debajo, los materiales activos bajo minimo del almacen elegido (o de todos). Nada se
 * calcula aqui: el disponible es el del servicio.
 */
@Route(value = StockRoutes.PREFIX, layout = MainLayout.class)
@PageTitle("Existencias")
@Menu(title = "Almacen", order = 50, icon = "vaadin:storage")
@RolesAllowed(StockRoles.STOCK_READ)
public class StockView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    private static final List<String> LEDGER_SORT = List.of("occurredAt,desc");
    private static final List<String> BY_CODE = List.of("code,asc");

    private final StockClients clients;

    private final ComboBox<WarehouseSummaryDto> warehouse;
    private final ComboBox<MaterialSummaryDto> material;
    private final Div card = new Div();
    private final Span onHand = new Span();
    private final Span reserved = new Span();
    private final Span available = new Span();
    private final Span minimum = new Span();
    private final Span lowStockBadge = new Span("Bajo minimo");
    private final H4 ledgerTitle = new H4("Movimientos");
    private final MovementGrid ledger = new MovementGrid(false);
    private final Span lowStockCount = new Span();
    private final Grid<MaterialDto> lowStock = new Grid<>();

    public StockView(MaterialClient materials, WarehouseClient warehouses, SupplierClient suppliers, ProjectClient projects,
                     MovementClient movements, ReservationClient reservations, AssemblyClient assemblies, AuthenticationContext authentication) {
        this.clients = new StockClients(materials, warehouses, suppliers, projects, movements, reservations, assemblies);
        setSizeFull();
        warehouse = StockPickers.warehouse("Almacen", warehouses);
        warehouse.setId("stock-warehouse");
        warehouse.setPlaceholder("Todos los almacenes");
        warehouse.addValueChangeListener(change -> refresh());
        material = StockPickers.material("Material", materials);
        material.setId("stock-material");
        material.setWidth("28rem");
        material.addValueChangeListener(change -> refresh());
        HorizontalLayout toolbar = new HorizontalLayout(material, warehouse,
                StockOperations.buttons(authentication, clients, this::prefill, movement -> refresh()));
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        add(new H2("Existencias"), toolbar, buildCard(), ledgerTitle, buildLedger(), buildLowStock());
        expand(ledger);
        card.setVisible(false);
        ledgerTitle.setVisible(false);
        ledger.setVisible(false);
    }

    private Component buildCard() {
        card.setId("stock-card");
        card.getStyle().set("display", "flex").set("gap", "var(--lumo-space-l)").set("flex-wrap", "wrap")
                .set("padding", "var(--lumo-space-m)").set("border", "1px solid var(--lumo-contrast-20pct)")
                .set("border-radius", "var(--lumo-border-radius-m)");
        lowStockBadge.getElement().getThemeList().add("badge error");
        card.add(figure("Fisico", onHand), figure("Reservado", reserved), figure("Disponible", available), figure("Minimo", minimum), lowStockBadge);
        return card;
    }

    private static Component figure(String label, Span value) {
        Span caption = new Span(label);
        caption.getStyle().set("font-size", "var(--lumo-font-size-xs)").set("color", "var(--lumo-secondary-text-color)");
        value.getStyle().set("font-size", "var(--lumo-font-size-xl)").set("font-weight", "600");
        VerticalLayout figure = new VerticalLayout(caption, value);
        figure.setPadding(false);
        figure.setSpacing(false);
        figure.setWidth("auto");
        return figure;
    }

    private Component buildLedger() {
        ledger.setId("stock-ledger");
        ledger.setPageSize(PAGE_SIZE);
        ledger.setSizeFull();
        ledger.setItems(this::fetchLedger, this::countLedger);
        return ledger;
    }

    private Component buildLowStock() {
        lowStock.setId("low-stock-grid");
        lowStock.addColumn(MaterialDto::code).setHeader("Codigo").setKey("code").setAutoWidth(true);
        lowStock.addColumn(MaterialDto::name).setHeader("Nombre").setKey("name").setFlexGrow(1);
        lowStock.addColumn(MaterialDto::unitOfMeasure).setHeader("Unidad").setKey("unitOfMeasure").setAutoWidth(true);
        lowStock.addColumn(dto -> StockFormats.quantity(dto.minimumStockLevel())).setHeader("Minimo").setKey("minimumStockLevel").setAutoWidth(true);
        lowStock.setPageSize(PAGE_SIZE);
        lowStock.setAllRowsVisible(false);
        lowStock.setHeight("16rem");
        lowStock.setItems(this::fetchLowStock, this::countLowStock);
        lowStock.addItemClickListener(event -> material.setValue(event.getItem().summary()));
        HorizontalLayout heading = new HorizontalLayout(new H4("Bajo minimo"), lowStockCount);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        VerticalLayout section = new VerticalLayout(heading, lowStock);
        section.setPadding(false);
        return section;
    }

    private MovementForm prefill() {
        MovementForm form = new MovementForm();
        form.setMaterialId(material.getValue());
        form.setWarehouseId(warehouse.getValue());
        return form;
    }

    private UUID warehouseId() {
        return warehouse.getValue() == null ? null : warehouse.getValue().id();
    }

    void refresh() {
        MaterialSummaryDto chosen = material.getValue();
        card.setVisible(chosen != null);
        ledgerTitle.setVisible(chosen != null);
        ledger.setVisible(chosen != null);
        if (chosen != null) {
            ledgerTitle.setText("Movimientos de " + chosen.label() + (warehouse.getValue() == null ? "" : " en " + warehouse.getValue().code()));
            try {
                paint(clients.materials().stock(chosen.id(), warehouseId()));
            } catch (BackofficeApiException failure) {
                card.setVisible(false);
                UiErrors.show(failure);
            }
            ledger.getDataProvider().refreshAll();
        }
        lowStock.getDataProvider().refreshAll();
    }

    private void paint(MaterialStockDto stock) {
        onHand.setText(StockFormats.quantity(stock.onHandQuantity()));
        reserved.setText(StockFormats.quantity(stock.activeReservedQuantity()));
        available.setText(StockFormats.quantity(stock.availableQuantity()));
        minimum.setText(StockFormats.quantity(stock.minimumStockLevel()));
        lowStockBadge.setVisible(stock.isLowStock());
    }

    private Stream<MovementDto> fetchLedger(Query<MovementDto, Void> query) {
        MaterialSummaryDto chosen = material.getValue();
        if (chosen == null) {
            return Stream.empty();
        }
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            return clients.materials().movements(chosen.id(), warehouseId(), null, null, null, query.getOffset() / size, size,
                    sort.isEmpty() ? LEDGER_SORT : sort).content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int countLedger(Query<MovementDto, Void> query) {
        MaterialSummaryDto chosen = material.getValue();
        if (chosen == null) {
            return 0;
        }
        try {
            return (int) Math.min(Integer.MAX_VALUE, clients.materials().movements(chosen.id(), warehouseId(), null, null, null, 0, 1, LEDGER_SORT)
                    .page().totalElements());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }

    private Stream<MaterialDto> fetchLowStock(Query<MaterialDto, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            PageResponse<MaterialDto> page = clients.materials().lowStock(warehouseId(), query.getOffset() / size, size, BY_CODE);
            lowStockCount.setText(page.page().totalElements() + " materiales");
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int countLowStock(Query<MaterialDto, Void> query) {
        try {
            long total = clients.materials().lowStock(warehouseId(), 0, 1, BY_CODE).page().totalElements();
            lowStockCount.setText(total + " materiales");
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return 0;
        }
    }
}

package com.alejandro.mtobackoffice.ui.stock;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.stock.StockCatalogueClient;
import com.alejandro.mtobackoffice.configuration.security.StockRoles;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.security.AuthenticationContext;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Un catalogo de mto-stock (almacenes, proveedores, proyectos, materiales): la lista paginada <b>en
 * el servidor</b> con la busqueda por codigo o nombre y el estado, y el editor de cada uno. El grid
 * pide cada pagina a {@code GET /{catalogo}} con {@code search}, {@code active}, {@code page},
 * {@code size} y {@code sort=campo,asc} (solo atributos de la entidad del servicio), y el recuento
 * es el {@code totalElements} de la pagina. No hay borrado: un catalogo se retira desde el editor
 * con {@code active=false}. «Nuevo» y modificar piden {@code stock-write}; esconderlos es
 * cortesia, la guarda es el servicio.
 *
 * @param <D> la fila
 */
public abstract class StockCatalogueView<D> extends VerticalLayout {

    public static final String ACTIONS_COLUMN = "actions";
    static final int PAGE_SIZE = 50;
    static final List<String> DEFAULT_SORT = List.of("code,asc");

    private final StockCatalogueClient<D, ?, ?> client;
    private final String plural;
    protected final boolean canWrite;

    private final TextField search = new TextField();
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Retirados");
    private final Span count = new Span();
    protected final Grid<D> grid = new Grid<>();

    /**
     * @param title  el de la pantalla
     * @param plural como se cuentan las filas («almacenes», «materiales»)
     */
    protected StockCatalogueView(StockCatalogueClient<D, ?, ?> client, AuthenticationContext authentication, String title, String plural) {
        this.client = client;
        this.plural = plural;
        this.canWrite = authentication.hasRole(StockRoles.STOCK_WRITE);
        setSizeFull();
        add(new H2(title), toolbar(), buildGrid());
        expand(grid);
    }

    /** Las columnas de la fila; las ordenables llevan {@code setSortProperty} con el atributo del servicio. */
    protected abstract void configureColumns(Grid<D> grid);

    protected abstract UUID idOf(D row);

    /** Abre el editor de alta ({@code null}) o de modificacion. */
    protected abstract void openEditor(D existing);

    /** Si esta fila se puede modificar; los proyectos sincronizados, por ejemplo, no. */
    protected boolean isEditable(D row) {
        return true;
    }

    private Component toolbar() {
        search.setId("stock-search");
        search.setPlaceholder("Buscar por codigo o nombre");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(change -> refresh());
        state.addValueChangeListener(change -> refresh());
        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> refresh());
        Button create = new Button("Nuevo", VaadinIcon.PLUS.create(), click -> openEditor(null));
        create.setId("stock-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);
        HorizontalLayout toolbar = new HorizontalLayout(search, state, reload, count, create);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(count);
        return toolbar;
    }

    private Component buildGrid() {
        configureColumns(grid);
        if (canWrite) {
            grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
            grid.addItemDoubleClickListener(event -> {
                if (isEditable(event.getItem())) {
                    openEditor(event.getItem());
                }
            });
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        return grid;
    }

    private Component rowActions(D row) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (isEditable(row)) {
            Button edit = new Button(VaadinIcon.EDIT.create(), click -> openEditor(row));
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            edit.setTooltipText("Modificar");
            edit.setId("edit-" + idOf(row));
            actions.add(edit);
        }
        return actions;
    }

    private Stream<D> fetch(Query<D, Void> query) {
        try {
            int size = Math.max(1, query.getLimit());
            List<String> sort = MasterFilters.sort(query.getSortOrders());
            PageResponse<D> page = client.search(searchText(), state.getValue().value(), query.getOffset() / size, size,
                    sort.isEmpty() ? DEFAULT_SORT : sort);
            showCount(page.page() == null ? page.content().size() : page.page().totalElements());
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<D, Void> query) {
        try {
            PageResponse<D> page = client.search(searchText(), state.getValue().value(), 0, 1, DEFAULT_SORT);
            long total = page.page() == null ? page.content().size() : page.page().totalElements();
            showCount(total);
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            showCount(0);
            return 0;
        }
    }

    private String searchText() {
        String text = search.getValue() == null ? "" : search.getValue().trim();
        return text.isEmpty() ? null : text;
    }

    private void showCount(long total) {
        count.setText(total + " " + plural);
    }

    public void refresh() {
        grid.getDataProvider().refreshAll();
    }

    protected static String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Si" : "No";
    }
}

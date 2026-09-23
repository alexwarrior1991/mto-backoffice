package com.alejandro.mtobackoffice.ui.master;

import com.alejandro.mtobackoffice.client.configuration.MasterClient;
import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.configuration.MasterResource;
import com.alejandro.mtobackoffice.client.dto.PageResponse;
import com.alejandro.mtobackoffice.client.dto.master.MasterDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.SecurityRoles;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.security.AuthenticationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;

/**
 * Una pantalla de maestro de infraestructura: lista paginada <b>en el servidor</b> y un editor.
 *
 * <p>El Grid pide cada pagina a {@code POST /filter} con la pagina, el tamano, el orden de sus
 * columnas y el texto de busqueda ({@code searchText}, que el servicio aplica a varias columnas), y
 * el recuento sale de {@code totalElements}: nunca se trae el maestro entero, que en perfiles son
 * miles de filas. Cada fila es lo que el servicio devuelve en la lista, y es sobre esa fila sobre la
 * que se edita (README_API.md §4: lee, modifica sobre lo leido y devuelvelo entero; el editor pone
 * a {@code null} los hijos que no toca).</p>
 *
 * <p>Los botones siguen los permisos del servicio: crear y modificar piden {@code config-write};
 * borrar (logico), {@code config-delete}. Esconderlos es cortesia: la guarda real esta en el
 * servicio. La columna de acciones existe siempre, tambien para quien solo lee, porque un maestro
 * puede ofrecer acciones de lectura ({@link #addRowActions}: el esquema de una via).</p>
 */
public abstract class MasterView<D extends MasterDto> extends VerticalLayout {

    public static final String ROUTE_PREFIX = "infraestructura";
    static final String ACTIONS_COLUMN = "actions";
    static final int PAGE_SIZE = 50;
    protected static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.ROOT);
    protected static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    private final MasterResource resource;
    private final Class<D> type;
    private final MasterClient<D> client;
    private final ObjectMapper objectMapper;
    protected final boolean canWrite;
    protected final boolean canDelete;

    private final TextField search = new TextField();
    private final HorizontalLayout filters = new HorizontalLayout();
    private final Span count = new Span();
    private final Grid<D> grid = new Grid<>();

    protected MasterView(MasterResource resource, Class<D> type, MasterClient<D> client,
                         AuthenticationContext authentication, ObjectMapper objectMapper) {
        this.resource = resource;
        this.type = type;
        this.client = client;
        this.objectMapper = objectMapper;
        this.canWrite = authentication.hasRole(SecurityRoles.CONFIG_WRITE);
        this.canDelete = authentication.hasRole(SecurityRoles.CONFIG_DELETE);
        setSizeFull();
        add(new H2(resource.title()), toolbar(), grid);
        expand(grid);
    }

    /**
     * La vista concreta lo llama al final de su constructor, cuando ya existen sus filtros y sabe
     * sus columnas; antes no hay nada que configurar.
     */
    protected final void init() {
        extraFilters().forEach(filters::add);
        configureColumns(grid);
        grid.addColumn(dto -> dto.getVersionDate() == null ? "" : DATE_TIME.format(dto.getVersionDate()))
                .setHeader("Modificado").setKey("versionDate").setSortProperty("versionDate").setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> edit(event.getItem()));
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
    }

    /** Las columnas propias del maestro; el orden de cada una es un campo del servicio. */
    protected abstract void configureColumns(Grid<D> grid);

    /** Filtros propios ademas del texto; se anaden al cuerpo en {@link #addFilters}. */
    protected List<Component> extraFilters() {
        return List.of();
    }

    /** Lo que va al cuerpo del {@code /filter} ademas de {@code searchText}; nada si es {@code null}. */
    protected void addFilters(Map<String, Object> filter) {
    }

    protected abstract D newDto();

    protected abstract MasterEditorDialog<D> editor(D dto);

    protected MasterResource resource() {
        return resource;
    }

    protected Grid<D> grid() {
        return grid;
    }

    private Component toolbar() {
        search.setPlaceholder("Buscar");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(change -> refresh());
        filters.add(search);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);

        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> refresh());
        Button create = new Button("Nuevo", VaadinIcon.PLUS.create(), click -> edit(null));
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        HorizontalLayout toolbar = new HorizontalLayout(filters, reload, count, create);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(count);
        return toolbar;
    }

    private Component rowActions(D dto) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        if (canWrite) {
            actions.add(rowButton("edit-" + dto.getId(), VaadinIcon.EDIT, "Modificar", click -> edit(dto)));
        }
        addRowActions(dto, actions);
        if (canDelete) {
            Button delete = rowButton("delete-" + dto.getId(), VaadinIcon.TRASH, "Borrar", click -> confirmDelete(dto));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(delete);
        }
        return actions;
    }

    /**
     * Las acciones propias de un maestro, entre modificar y borrar; por defecto ninguna. Se montan
     * con {@link #rowButton} y llevan un id {@code <accion>-<id>} para que los tests las encuentren.
     * Una accion de lectura se ofrece a todo el mundo: quien no puede escribir tambien ve la fila.
     */
    protected void addRowActions(D row, HorizontalLayout actions) {
    }

    protected static Button rowButton(String id, VaadinIcon icon, String tooltip,
                                      ComponentEventListener<ClickEvent<Button>> listener) {
        Button button = new Button(icon.create(), listener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        button.setTooltipText(tooltip);
        button.setId(id);
        return button;
    }

    private Map<String, Object> filterBody() {
        Map<String, Object> filter = MasterFilters.of("searchText", search.getValue());
        addFilters(filter);
        return filter;
    }

    private Stream<D> fetch(Query<D, Void> query) {
        try {
            PageResponse<D> page = client.filter(query.getPage(), query.getPageSize(),
                    MasterFilters.sort(query.getSortOrders()), filterBody());
            showCount(page.page().totalElements());
            return page.content().stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<D, Void> query) {
        try {
            long total = client.filter(0, 1, List.of(), filterBody()).page().totalElements();
            showCount(total);
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            showCount(0);
            return 0;
        }
    }

    private void showCount(long total) {
        String what = total == 1 ? resource.singular() : resource.title();
        count.setText(total + " " + what.toLowerCase(Locale.ROOT));
    }

    public void refresh() {
        grid.deselectAll();
        grid.getDataProvider().refreshAll();
    }

    /**
     * Se edita una <b>copia</b> de la fila: el Binder escribe sobre el objeto al guardar, y si el
     * servicio rechaza el cambio o la persona cancela, la fila del Grid tiene que seguir siendo lo
     * que llego del servicio. La copia pasa por Jackson para que lleve tambien lo que la UI no
     * conoce ({@code extras}).
     */
    void edit(D row) {
        D dto = row == null ? newDto() : objectMapper.convertValue(row, type);
        editor(dto).open();
    }

    /** Alta con {@code POST}, modificacion con {@code PUT /{id}}; lo llama el editor. */
    protected D save(D dto) {
        return dto.isNew() ? client.create(dto) : client.update(dto.getId(), dto);
    }

    protected void onSaved(D saved) {
        refresh();
    }

    private void confirmDelete(D dto) {
        ConfirmDialog dialog = new ConfirmDialog("Borrar " + resource.singular().toLowerCase(Locale.ROOT) + " " + labelOf(dto),
                "Desaparece de las listas (borrado logico en el servicio). ¿Seguro?",
                "Borrar", confirm -> delete(dto), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    /** Como se nombra una fila en mensajes: por defecto su id. */
    protected String labelOf(D dto) {
        return "#" + dto.getId();
    }

    private void delete(D dto) {
        try {
            client.delete(dto.getId());
            Notification.show("Borrado " + labelOf(dto), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refresh();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    protected static String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Si" : "No";
    }
}

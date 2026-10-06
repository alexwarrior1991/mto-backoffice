package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.dto.users.UsersPage;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.UserRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Los usuarios del realm: la lista paginada <b>en el servidor</b> y el editor.
 *
 * <p>La paginacion es la de Keycloak: el Grid pide cada tramo a {@code GET /api/users} con
 * {@code first} y {@code max} (como mucho 200 por peticion, el tope del servicio), y el recuento es
 * el {@code total} de la misma consulta. La busqueda por texto y el filtro por atributo son
 * <b>excluyentes</b> porque el servicio los rechaza juntos (400 {@code SEARCH-400}): escribir en
 * uno deshabilita el otro, y lo deshabilitado no viaja. La API no ordena, asi que las columnas
 * tampoco.</p>
 *
 * <p>Los botones siguen los permisos del servicio: nuevo, modificar y activar/desactivar piden
 * {@code users-write}; borrar, {@code users-delete}; abrir la ficha, solo leer. Esconderlos es
 * cortesia: la guarda real esta en el servicio.</p>
 */
@Route(value = UsersView.ROUTE, layout = MainLayout.class)
@PageTitle("Usuarios")
@Menu(title = "Usuarios", order = 40, icon = "vaadin:users")
@RolesAllowed(UserRoles.USERS_READ)
public class UsersView extends VerticalLayout {

    /** Primer segmento de todas las rutas del modulo; la lista vive en el propio prefijo. */
    public static final String ROUTE_PREFIX = "usuarios";
    public static final String ROUTE = ROUTE_PREFIX;
    static final String ACTIONS_COLUMN = "actions";
    public static final int PAGE_SIZE = 50;
    /** El tope de {@code max} en mto-users; por encima responde 400. */
    static final int MAX_PAGE = 200;
    /** La forma {@code clave:valor} que exige el servicio para un atributo. */
    static final String ATTRIBUTE_PATTERN = "^[^:\\s]{1,255}:[^\\s]{0,255}$";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final UsersClient client;
    private final boolean canWrite;
    private final boolean canDelete;

    private final TextField search = new TextField();
    private final TextField attribute = new TextField();
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todos", "Activos", "Desactivados");
    private final Span count = new Span();
    private final Grid<UserDto> grid = new Grid<>();
    private LazyPages<UserDto> pages;

    /** Lo que pide la lista: el ultimo filtro bien formado de la pantalla. */
    private record ListFilter(String search, Boolean enabled, List<String> attributes) {
    }

    private ListFilter applied = new ListFilter(null, null, null);

    public UsersView(UsersClient client, AuthenticationContext authentication) {
        this.client = client;
        this.canWrite = authentication.hasRole(UserRoles.USERS_WRITE);
        this.canDelete = authentication.hasRole(UserRoles.USERS_DELETE);
        setSizeFull();
        add(new H2("Usuarios"), toolbar(), buildGrid());
        expand(grid);
    }

    private Component toolbar() {
        search.setId("users-search");
        search.setPlaceholder("Buscar por usuario, email o nombre");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(change -> {
            attribute.setEnabled(isBlank(change.getValue()));
            applyFilters();
        });
        attribute.setId("users-attribute");
        attribute.setPlaceholder("Atributo clave:valor");
        attribute.setTooltipText("Un atributo exacto del usuario. No se combina con la busqueda: el servicio rechaza las dos a la vez");
        attribute.setClearButtonVisible(true);
        attribute.setValueChangeMode(ValueChangeMode.LAZY);
        attribute.setErrorMessage("clave:valor, sin espacios");
        attribute.addValueChangeListener(change -> {
            String value = trimmed(change.getValue());
            boolean valid = value.isEmpty() || value.matches(ATTRIBUTE_PATTERN);
            attribute.setInvalid(!valid);
            search.setEnabled(value.isEmpty());
            applyFilters();
        });
        state.addValueChangeListener(change -> applyFilters());

        Button reload = new Button("Recargar", VaadinIcon.REFRESH.create(), click -> refresh());
        Button create = new Button("Nuevo", VaadinIcon.PLUS.create(), click -> openEditor(null));
        create.setId("user-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        HorizontalLayout toolbar = new HorizontalLayout(search, attribute, state, reload, count, create);
        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.expand(count);
        return toolbar;
    }

    private Component buildGrid() {
        grid.addColumn(UserDto::username).setHeader("Usuario").setKey("username").setAutoWidth(true);
        grid.addColumn(UserDto::fullName).setHeader("Nombre").setKey("name").setFlexGrow(1);
        grid.addColumn(dto -> dto.email() == null ? "" : dto.email()).setHeader("Email").setKey("email").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.emailVerified())).setHeader("Verificado").setKey("emailVerified").setAutoWidth(true);
        grid.addColumn(dto -> yesNo(dto.enabled())).setHeader("Activo").setKey("enabled").setAutoWidth(true);
        grid.addColumn(dto -> dto.createdAt() == null ? "" : DATE_TIME.format(dto.createdAt())).setHeader("Creado").setKey("createdAt").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        grid.addItemDoubleClickListener(event -> open(event.getItem()));
        grid.setPageSize(PAGE_SIZE);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, this::showCount);
        return grid;
    }

    private Component rowActions(UserDto user) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        Button open = new Button(VaadinIcon.USER_CARD.create(), click -> open(user));
        open.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
        open.setTooltipText("Abrir la ficha");
        open.setId("open-" + user.id());
        actions.add(open);
        if (canWrite) {
            Button edit = new Button(VaadinIcon.EDIT.create(), click -> openEditor(user));
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            edit.setTooltipText("Modificar");
            edit.setId("edit-" + user.id());
            Button toggle = new Button((user.isEnabled() ? VaadinIcon.BAN : VaadinIcon.CHECK).create(), click -> toggle(user));
            toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            toggle.setTooltipText(user.isEnabled() ? "Desactivar" : "Activar");
            toggle.setId("toggle-" + user.id());
            actions.add(edit, toggle);
        }
        if (canDelete) {
            Button delete = new Button(VaadinIcon.TRASH.create(), click -> confirmDelete(user));
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            delete.setTooltipText("Borrar");
            delete.setId("delete-" + user.id());
            actions.add(delete);
        }
        return actions;
    }

    /**
     * Un tramo del Grid con su total, en una peticion ({@link LazyPages}): {@code first} es el
     * desplazamiento que pide y {@code max} lo que quepa hasta su limite, en trozos de como mucho
     * {@link #MAX_PAGE}. La API no ordena.
     */
    private LazyPages.Page<UserDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        List<UserDto> rows = new ArrayList<>();
        int wanted = Math.max(1, limit);
        long total = 0;
        while (rows.size() < wanted) {
            int max = Math.min(MAX_PAGE, wanted - rows.size());
            UsersPage<UserDto> page = search(offset + rows.size(), max);
            rows.addAll(page.content());
            total = page.total();
            if (page.content().size() < max) {
                break;
            }
        }
        return new LazyPages.Page<>(rows, total);
    }

    private UsersPage<UserDto> search(int first, int max) {
        return client.search(applied.search(), null, null, applied.enabled(), null, applied.attributes(), first, max);
    }

    /**
     * El filtro de la pantalla, o nada si el atributo esta mal formado. Lo deshabilitado no viaja:
     * la busqueda y el atributo se excluyen porque el servicio los rechaza juntos.
     */
    private Optional<ListFilter> currentFilter() {
        String pair = attribute.isEnabled() ? trimmed(attribute.getValue()) : "";
        if (!pair.isEmpty() && !pair.matches(ATTRIBUTE_PATTERN)) {
            return Optional.empty();
        }
        String text = search.isEnabled() && !isBlank(search.getValue()) ? search.getValue().trim() : null;
        return Optional.of(new ListFilter(text, state.getValue().value(), pair.isEmpty() ? null : List.of(pair)));
    }

    /**
     * Un filtro cambio: la lista se pide con el nuevo. Con un atributo mal formado no se pide nada y
     * la lista sigue con lo ultimo que pidio, como en mto-frontend.
     */
    private void applyFilters() {
        currentFilter().ifPresent(filter -> {
            applied = filter;
            refresh();
        });
    }

    private void showCount(long total) {
        count.setText(total == 1 ? "1 usuario" : total + " usuarios");
    }

    /** Vuelve a pedir la lista con el filtro que ya pidio (tras guardar, borrar o «Recargar»). */
    public void refresh() {
        grid.deselectAll();
        pages.refresh();
    }

    private void open(UserDto user) {
        UI.getCurrent().navigate(UserDetailView.class, UserDetailView.parametersOf(user.id()));
    }

    private void openEditor(UserDto existing) {
        new UserEditorDialog(existing, client, saved -> refresh()).open();
    }

    /** Reversible, asi que sin confirmacion. No cierra sesiones: para eso esta la ficha. */
    private void toggle(UserDto user) {
        try {
            UserDto updated = client.setEnabled(user.id(), new UserEnabledRequest(!user.isEnabled()));
            Notification.show((updated.isEnabled() ? "Activado " : "Desactivado ") + updated.username(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refresh();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void confirmDelete(UserDto user) {
        ConfirmDialog dialog = new ConfirmDialog("Borrar usuario " + user.username(),
                "Se borra en Keycloak con sus roles, perfiles, sesiones y credenciales. No se puede deshacer.",
                "Borrar", confirm -> delete(user), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void delete(UserDto user) {
        try {
            client.delete(user.id());
            Notification.show("Borrado " + user.username(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refresh();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private static String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Si" : "No";
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }
}

package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.dto.users.UsersPage;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.UserRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
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
import java.util.stream.Stream;

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
 * {@code users-write}; borrar, {@code users-delete}. Esconderlos es cortesia: la guarda real esta
 * en el servicio.</p>
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
            refresh();
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
            if (valid) {
                refresh();
            }
        });
        state.addValueChangeListener(change -> refresh());

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
        if (canWrite || canDelete) {
            grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        }
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> openEditor(event.getItem()));
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setSizeFull();
        grid.setItems(this::fetch, this::count);
        return grid;
    }

    private Component rowActions(UserDto user) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
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
     * Un tramo del Grid: {@code first} es el desplazamiento que pide y {@code max} lo que quepa
     * hasta su limite, en trozos de como mucho {@link #MAX_PAGE}.
     */
    private Stream<UserDto> fetch(Query<UserDto, Void> query) {
        try {
            List<UserDto> rows = new ArrayList<>();
            int wanted = query.getLimit();
            while (rows.size() < wanted) {
                int max = Math.min(MAX_PAGE, wanted - rows.size());
                UsersPage<UserDto> page = search(query.getOffset() + rows.size(), max);
                rows.addAll(page.content());
                showCount(page.total());
                if (page.content().size() < max) {
                    break;
                }
            }
            return rows.stream();
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return Stream.empty();
        }
    }

    private int count(Query<UserDto, Void> query) {
        try {
            long total = search(0, 1).total();
            showCount(total);
            return (int) Math.min(Integer.MAX_VALUE, total);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            showCount(0);
            return 0;
        }
    }

    private UsersPage<UserDto> search(int first, int max) {
        return client.search(searchText(), null, null, state.getValue().value(), null, attributes(), first, max);
    }

    /** El texto de busqueda, o nada si esta deshabilitado por el atributo o vacio. */
    private String searchText() {
        return search.isEnabled() && !isBlank(search.getValue()) ? search.getValue().trim() : null;
    }

    /** El atributo, o nada si esta deshabilitado por la busqueda, vacio o mal formado. */
    private List<String> attributes() {
        String value = attribute.isEnabled() ? trimmed(attribute.getValue()) : "";
        return value.isEmpty() || !value.matches(ATTRIBUTE_PATTERN) ? null : List.of(value);
    }

    private void showCount(long total) {
        count.setText(total == 1 ? "1 usuario" : total + " usuarios");
    }

    public void refresh() {
        grid.deselectAll();
        grid.getDataProvider().refreshAll();
    }

    private void openEditor(UserDto existing) {
        new UserEditorDialog(existing, client, this::refresh).open();
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

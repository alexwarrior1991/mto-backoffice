package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.UserRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * La ficha de un usuario: cabecera con lo que es, botonera con lo que se le puede hacer y una
 * pestana por cada cosa que Keycloak guarda aparte (perfiles, roles de cliente, sesiones y
 * credenciales). Cada pestana pide sus datos la primera vez que se abre. Cada boton sigue su
 * permiso de {@code mto-users-api}; esconderlo es cortesia, la guarda es el servicio. «Sacar a la
 * persona» pide {@code users-write} y {@code users-sessions-write} a la vez: son sus tres llamadas.
 *
 * <p>Las rutas estaticas del modulo ({@code usuarios/perfiles}, {@code usuarios/roles}) ganan a
 * {@code :userId}: Vaadin resuelve antes los segmentos literales.</p>
 */
@Route(value = UsersView.ROUTE_PREFIX + "/:" + UserDetailView.USER_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(UserRoles.USERS_READ)
public class UserDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String USER_ID_PARAMETER = "userId";
    static final String EDIT_ID = "user-edit";
    static final String TOGGLE_ID = "user-toggle";
    static final String RESET_PASSWORD_ID = "user-reset-password";
    static final String ACTIONS_EMAIL_ID = "user-actions-email";
    static final String DELETE_ID = "user-delete";
    static final String TAKE_OUT_ID = "user-take-out";
    static final String PROFILES_TAB = "Perfiles";
    static final String ROLES_TAB = "Roles de cliente";
    static final String SESSIONS_TAB = "Sesiones";
    static final String CREDENTIALS_TAB = "Credenciales";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final UsersClient client;
    private final AuthenticationContext authentication;

    private final H2 title = new H2();
    private final Span enabledBadge = new Span();
    private final Span verifiedBadge = new Span();
    private final Span summary = new Span();
    private final Span pendingActions = new Span();
    private final Span attributes = new Span();
    private final Button toggle = new Button();
    private final Div tabsHolder = new Div();

    private UserDto user;
    private UserSessionsPanel sessions;

    public static RouteParameters parametersOf(String userId) {
        return new RouteParameters(USER_ID_PARAMETER, userId);
    }

    public UserDetailView(UsersClient client, AuthenticationContext authentication) {
        this.client = client;
        this.authentication = authentication;
        setSizeFull();
        enabledBadge.getElement().getThemeList().add("badge");
        verifiedBadge.getElement().getThemeList().add("badge");
        HorizontalLayout heading = new HorizontalLayout(title, enabledBadge, verifiedBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        pendingActions.getStyle().set("color", "var(--lumo-error-text-color)");
        attributes.getStyle().set("color", "var(--lumo-secondary-text-color)");
        tabsHolder.setWidthFull();
        add(heading, summary, pendingActions, attributes, buttons(), tabsHolder);
        expand(tabsHolder);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String userId = event.getRouteParameters().get(USER_ID_PARAMETER).orElse("");
        try {
            show(client.get(userId));
        } catch (NotFoundApiException missing) {
            Notification.show("No existe el usuario " + userId, 5000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo(UsersView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            event.forwardTo(UsersView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return user == null ? "Usuario" : "Usuario " + user.username();
    }

    private Component buttons() {
        boolean canWrite = authentication.hasRole(UserRoles.USERS_WRITE);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        Button back = new Button("Volver a la lista", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(UsersView.class));
        buttons.add(back);
        if (canWrite) {
            Button edit = new Button("Modificar", VaadinIcon.EDIT.create(), click -> new UserEditorDialog(user, client, this::reload).open());
            edit.setId(EDIT_ID);
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            toggle.setId(TOGGLE_ID);
            toggle.addClickListener(click -> toggle());
            buttons.add(edit, toggle);
        }
        if (authentication.hasRole(UserRoles.USERS_PASSWORD_RESET)) {
            Button reset = new Button("Contrasena temporal", VaadinIcon.KEY.create(), click -> new ResetPasswordDialog(user, client).open());
            reset.setId(RESET_PASSWORD_ID);
            buttons.add(reset);
        }
        if (canWrite) {
            Button email = new Button("Acciones por correo", VaadinIcon.ENVELOPE.create(), click -> new ExecuteActionsEmailDialog(user, client).open());
            email.setId(ACTIONS_EMAIL_ID);
            buttons.add(email);
        }
        if (authentication.hasAllRoles(UserRoles.USERS_WRITE, UserRoles.USERS_SESSIONS_WRITE)) {
            Button takeOut = new Button("Sacar a la persona", VaadinIcon.EXIT.create(), click -> confirmTakeOut());
            takeOut.setId(TAKE_OUT_ID);
            takeOut.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(takeOut);
        }
        if (authentication.hasRole(UserRoles.USERS_DELETE)) {
            Button delete = new Button("Borrar", VaadinIcon.TRASH.create(), click -> confirmDelete());
            delete.setId(DELETE_ID);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(delete);
        }
        return buttons;
    }

    private void show(UserDto loaded) {
        paint(loaded);
        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        tabs.add(PROFILES_TAB, new UserProfilesPanel(loaded.id(), client, authentication.hasRole(UserRoles.USERS_PROFILES_WRITE)));
        tabs.add(ROLES_TAB, new UserRolesPanel(loaded.id(), client, authentication.hasRole(UserRoles.USERS_ROLES_WRITE)));
        sessions = new UserSessionsPanel(loaded.id(), client, authentication.hasRole(UserRoles.USERS_SESSIONS_WRITE));
        tabs.add(SESSIONS_TAB, sessions);
        tabs.add(CREDENTIALS_TAB, new UserCredentialsPanel(loaded.id(), client, authentication.hasRole(UserRoles.USERS_CREDENTIALS_WRITE)));
        tabs.addSelectedChangeListener(change -> loadTab(tabs, change.getSelectedTab()));
        tabsHolder.removeAll();
        tabsHolder.add(tabs);
        loadTab(tabs, tabs.getSelectedTab());
    }

    private static void loadTab(TabSheet tabs, Tab tab) {
        if (tab != null && tabs.getComponent(tab) instanceof LazyPanel panel) {
            panel.ensureLoaded();
        }
    }

    /** La cabecera, a partir de la fila leida; se repinta tras cada cambio. */
    private void paint(UserDto loaded) {
        user = loaded;
        title.setText(loaded.username());
        enabledBadge.setText(loaded.isEnabled() ? "Activo" : "Desactivado");
        enabledBadge.getElement().getThemeList().set("success", loaded.isEnabled());
        enabledBadge.getElement().getThemeList().set("error", !loaded.isEnabled());
        boolean verified = Boolean.TRUE.equals(loaded.emailVerified());
        verifiedBadge.setText(verified ? "Email verificado" : "Email sin verificar");
        verifiedBadge.getElement().getThemeList().set("contrast", !verified);
        List<String> parts = new ArrayList<>();
        parts.add(loaded.fullName());
        if (loaded.email() != null && !loaded.email().isBlank()) {
            parts.add(loaded.email());
        }
        if (loaded.createdAt() != null) {
            parts.add("creado el " + DATE_TIME.format(loaded.createdAt()));
        }
        summary.setText(String.join(" · ", parts));
        pendingActions.setVisible(!loaded.requiredActions().isEmpty());
        pendingActions.setText("Acciones pendientes al entrar: " + String.join(", ", loaded.requiredActions().stream().map(UserDetailView::actionLabel).toList()));
        attributes.setVisible(!loaded.attributes().isEmpty());
        attributes.setText("Atributos: " + String.join(", ", loaded.attributes().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join("|", entry.getValue())).sorted().toList()));
        toggle.setText(loaded.isEnabled() ? "Desactivar" : "Activar");
        toggle.setIcon((loaded.isEnabled() ? VaadinIcon.BAN : VaadinIcon.CHECK).create());
    }

    private static String actionLabel(String action) {
        try {
            return RequiredAction.valueOf(action).label();
        } catch (IllegalArgumentException unknown) {
            return action;
        }
    }

    void reload() {
        try {
            paint(client.get(user.id()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** No cierra sesiones ni revoca tokens offline: desactivar solo bloquea el siguiente login. */
    private void toggle() {
        try {
            UserDto updated = client.setEnabled(user.id(), new UserEnabledRequest(!user.isEnabled()));
            paint(updated);
            Notification.show((updated.isEnabled() ? "Activado " : "Desactivado ") + updated.username(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /**
     * Lo que el README de mto-users deja en manos del cliente: desactivar, cerrar las sesiones y
     * revocar las offline, en ese orden ({@link TakeOut}). No borra nada.
     */
    private void confirmTakeOut() {
        ConfirmDialog dialog = new ConfirmDialog("Sacar a " + user.username(),
                "Se desactiva, se cierran sus sesiones y se revocan sus sesiones offline, en ese orden. "
                        + "No se borra nada: podra volver cuando alguien vuelva a activarle.",
                "Sacar", confirm -> takeOut(), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void takeOut() {
        TakeOut.Result result = TakeOut.run(client, user.id());
        if (result.isComplete()) {
            Notification.show(user.username() + " fuera: desactivado, sesiones cerradas y sesiones offline revocadas",
                    5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            String done = result.done().isEmpty() ? "nada" : String.join(" y ", result.done().stream().map(TakeOut.Step::label).toList());
            Notification.show("No se ha podido sacar a " + user.username() + ": fallo al " + result.failed().label()
                            + " (hecho: " + done + "). " + UiErrors.message(result.failure()),
                    10000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
        reload();
        sessions.reloadIfLoaded();
    }

    private void confirmDelete() {
        ConfirmDialog dialog = new ConfirmDialog("Borrar usuario " + user.username(),
                "Se borra en Keycloak con sus roles, perfiles, sesiones y credenciales. No se puede deshacer.",
                "Borrar", confirm -> delete(), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void delete() {
        try {
            client.delete(user.id());
            Notification.show("Borrado " + user.username(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(UsersView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    /** Para las pruebas y para quien quiera saber que atributos se pintan. */
    Map<String, List<String>> attributesShown() {
        return user == null ? Map.of() : user.attributes();
    }
}

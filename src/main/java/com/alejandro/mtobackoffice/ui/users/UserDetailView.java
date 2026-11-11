package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.RequiredAction;
import com.alejandro.mtobackoffice.client.dto.users.UserDto;
import com.alejandro.mtobackoffice.client.dto.users.UserEnabledRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.CurrentPrincipal;
import com.alejandro.mtobackoffice.configuration.security.UserRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.Formats;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
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

import java.util.ArrayList;
import java.util.List;

/**
 * La ficha de un usuario: cabecera con lo que es, botonera con lo que se le puede hacer y una
 * pestana por cada cosa que Keycloak guarda aparte (perfiles, roles de cliente, sesiones y
 * credenciales). Cada pestana pide sus datos la primera vez que se abre. Cada boton sigue su
 * permiso de {@code mto-users-api}; esconderlo es cortesia, la guarda es el servicio. «Sacar a la
 * persona» pide {@code users-write} y {@code users-sessions-write} a la vez: son sus tres llamadas.
 *
 * <p>Lo que mto-users rechaza por el objetivo tampoco se ofrece ({@link #paint}): una cuenta de
 * servicio solo se lee (403 {@code USR-SVC-403}), asi que la cabecera lo dice y no hay ni botones
 * ni controles en sus pestanas; y en la ficha de quien ha entrado no hay desactivar, borrar ni
 * «sacar a la persona», y sus perfiles y roles se leen (403 {@code USR-SELF-403}).</p>
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
    static final String SERVICE_ACCOUNT_NOTICE_ID = "user-service-account";
    static final String SERVICE_ACCOUNT_NOTICE = "Cuenta de servicio: solo lectura. Se administra con el realm, no desde aqui.";
    static final String OWN_PERMISSIONS_HINT = "Nadie cambia sus propios permisos: tus perfiles y tus roles los asigna o los quita otra persona.";
    static final String PROFILES_TAB = "Perfiles";
    static final String ROLES_TAB = "Roles de cliente";
    static final String SESSIONS_TAB = "Sesiones";
    static final String CREDENTIALS_TAB = "Credenciales";

    private final UsersClient client;
    private final AuthenticationContext authentication;

    private final H2 title = new H2();
    private final Span enabledBadge = new Span();
    private final Span verifiedBadge = new Span();
    private final Span summary = new Span();
    private final Span pendingActions = new Span();
    private final Span attributes = new Span();
    private final Span serviceAccountNotice = new Span(SERVICE_ACCOUNT_NOTICE);
    private final Button toggle = new Button();
    /** Los botones que escriben, cada uno solo si su permiso esta: {@link #paint} decide cuales se ven. */
    private Button edit;
    private Button reset;
    private Button email;
    private Button takeOut;
    private Button delete;
    private final Div tabsHolder = new Div();

    private UserDto user;
    private UserRolesPanel roles;
    private UserSessionsPanel sessions;
    private UserCredentialsPanel credentials;

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
        serviceAccountNotice.setId(SERVICE_ACCOUNT_NOTICE_ID);
        serviceAccountNotice.getElement().getThemeList().add("badge contrast");
        serviceAccountNotice.setVisible(false);
        add(heading, serviceAccountNotice, summary, pendingActions, attributes, buttons(), tabsHolder);
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
            edit = new Button("Modificar", VaadinIcon.EDIT.create(), click -> new UserEditorDialog(user, client, this::paint).open());
            edit.setId(EDIT_ID);
            edit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            toggle.setId(TOGGLE_ID);
            toggle.addClickListener(click -> toggle());
            buttons.add(edit, toggle);
        }
        if (authentication.hasRole(UserRoles.USERS_PASSWORD_RESET)) {
            reset = new Button("Contrasena temporal", VaadinIcon.KEY.create(), click -> new ResetPasswordDialog(user, client, () -> {
                // Keycloak anade «cambiar la contrasena» a sus acciones y una credencial de tipo password.
                reload();
                credentials.reloadIfLoaded();
            }).open());
            reset.setId(RESET_PASSWORD_ID);
            buttons.add(reset);
        }
        if (canWrite) {
            email = new Button("Acciones por correo", VaadinIcon.ENVELOPE.create(), click -> new ExecuteActionsEmailDialog(user, client,
                    authentication.hasAllRoles(UserRoles.USERS_WRITE, UserRoles.USERS_PASSWORD_RESET)).open());
            email.setId(ACTIONS_EMAIL_ID);
            buttons.add(email);
        }
        if (authentication.hasAllRoles(UserRoles.USERS_WRITE, UserRoles.USERS_SESSIONS_WRITE)) {
            takeOut = new Button("Sacar a la persona", VaadinIcon.EXIT.create(), click -> confirmTakeOut());
            takeOut.setId(TAKE_OUT_ID);
            takeOut.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(takeOut);
        }
        if (authentication.hasRole(UserRoles.USERS_DELETE)) {
            delete = new Button("Borrar", VaadinIcon.TRASH.create(), click -> confirmDelete());
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
        // Una cuenta de servicio solo se lee, y nadie cambia sus propios permisos: sus pestanas se leen.
        boolean writable = !loaded.isServiceAccount();
        boolean self = writable && CurrentPrincipal.isSubject(loaded.id());
        boolean permissionsWritable = writable && !self;
        String hint = self ? OWN_PERMISSIONS_HINT : null;
        roles = new UserRolesPanel(loaded.id(), client,
                permissionsWritable && authentication.hasRole(UserRoles.USERS_ROLES_WRITE), hint);
        // Un perfil es un rol de realm que concede roles de cliente: cambiarlo cambia la pestana de roles.
        tabs.add(PROFILES_TAB, new UserProfilesPanel(loaded.id(), client,
                permissionsWritable && authentication.hasRole(UserRoles.USERS_PROFILES_WRITE), hint, () -> roles.reloadIfLoaded()));
        tabs.add(ROLES_TAB, roles);
        sessions = new UserSessionsPanel(loaded.id(), client, writable && authentication.hasRole(UserRoles.USERS_SESSIONS_WRITE));
        tabs.add(SESSIONS_TAB, sessions);
        credentials = new UserCredentialsPanel(loaded.id(), client, writable && authentication.hasRole(UserRoles.USERS_CREDENTIALS_WRITE));
        tabs.add(CREDENTIALS_TAB, credentials);
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
            parts.add("creado el " + Formats.dateTime(loaded.createdAt()));
        }
        summary.setText(String.join(" · ", parts));
        pendingActions.setVisible(!loaded.requiredActions().isEmpty());
        pendingActions.setText("Acciones pendientes al entrar: " + String.join(", ", loaded.requiredActions().stream().map(UserDetailView::actionLabel).toList()));
        attributes.setVisible(!loaded.attributes().isEmpty());
        attributes.setText("Atributos: " + String.join(", ", loaded.attributes().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join("|", entry.getValue())).sorted().toList()));
        toggle.setText(loaded.isEnabled() ? "Desactivar" : "Activar");
        toggle.setIcon((loaded.isEnabled() ? VaadinIcon.BAN : VaadinIcon.CHECK).create());
        offer(loaded);
    }

    /**
     * Lo que se ofrece segun de quien es la ficha, ademas del permiso de cada boton. Sobre una cuenta
     * de servicio mto-users lo rechaza todo (403 {@code USR-SVC-403}); sobre uno mismo, desactivar,
     * borrar y «sacar a la persona», cuyo primer paso es desactivar (403 {@code USR-SELF-403}).
     * Activarse, modificar los datos propios, la contrasena y el correo de acciones siguen.
     */
    private void offer(UserDto loaded) {
        boolean writable = !loaded.isServiceAccount();
        boolean self = CurrentPrincipal.isSubject(loaded.id());
        serviceAccountNotice.setVisible(!writable);
        show(edit, writable);
        show(reset, writable);
        show(email, writable);
        toggle.setVisible(writable && !(self && loaded.isEnabled()));
        show(takeOut, writable && !self);
        show(delete, writable && !self);
    }

    private static void show(Button button, boolean visible) {
        if (button != null) {
            button.setVisible(visible);
        }
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
            UiErrors.show(result.failure(), "No se ha podido sacar a " + user.username() + ": fallo al " + result.failed().label()
                    + " (hecho: " + done + "). " + UiErrors.message(result.failure()));
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
}

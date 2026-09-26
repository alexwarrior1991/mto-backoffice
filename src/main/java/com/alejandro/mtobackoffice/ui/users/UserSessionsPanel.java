package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.UserSessionDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Las sesiones de la persona, normales y offline, cada una con su lista y su «cerrar todas».
 * Son dos recursos a proposito: un token con {@code offline_access} no abre sesion normal,
 * sobrevive a cerrar las sesiones y a desactivar al usuario, y solo muere revocandolo aqui. Los
 * botones piden {@code users-sessions-write}. Cerrar una sesion que no es de este usuario es un
 * 404 del servicio ({@code SES-404}), que se avisa y se recarga.
 */
class UserSessionsPanel extends LazyPanel {

    static final String SESSIONS_GRID_ID = "sessions-grid";
    static final String OFFLINE_GRID_ID = "offline-sessions-grid";
    static final String REVOKE_ALL_ID = "sessions-revoke-all";
    static final String REVOKE_ALL_OFFLINE_ID = "offline-sessions-revoke-all";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final String userId;
    private final UsersClient client;
    private final boolean canRevoke;

    private final Span sessionsCount = new Span();
    private final Span offlineCount = new Span();
    private final Grid<UserSessionDto> sessions = new Grid<>();
    private final Grid<UserSessionDto> offline = new Grid<>();

    UserSessionsPanel(String userId, UsersClient client, boolean canRevoke) {
        this.userId = userId;
        this.client = client;
        this.canRevoke = canRevoke;
        add(section("Sesiones", sessionsCount, sessions, SESSIONS_GRID_ID, "revoke-", REVOKE_ALL_ID,
                "Cerrar todas las sesiones", this::revoke, this::confirmRevokeAll));
        add(new Paragraph("Desactivar al usuario no cierra sus sesiones ni revoca sus tokens offline: "
                + "para sacar a alguien hay que desactivar, cerrar las sesiones y revocar las offline, en ese orden."));
        add(section("Sesiones offline", offlineCount, offline, OFFLINE_GRID_ID, "revoke-offline-", REVOKE_ALL_OFFLINE_ID,
                "Revocar todas las sesiones offline", this::revokeOffline, this::confirmRevokeAllOffline));
        add(new Paragraph("Una sesion offline la abre un token con offline_access. Sobrevive a cerrar las sesiones "
                + "normales y a desactivar al usuario (volver a activarlo la recupera); solo revocandola aqui deja de valer."));
    }

    private Component section(String title, Span count, Grid<UserSessionDto> grid, String gridId, String revokePrefix,
                              String revokeAllId, String revokeAllText, Consumer<UserSessionDto> revoke, Runnable revokeAll) {
        grid.setId(gridId);
        grid.addColumn(dto -> format(dto.startedAt())).setHeader("Inicio").setKey("startedAt").setAutoWidth(true);
        grid.addColumn(dto -> format(dto.lastAccessAt())).setHeader("Ultimo acceso").setKey("lastAccessAt").setAutoWidth(true);
        grid.addColumn(dto -> dto.ipAddress() == null ? "" : dto.ipAddress()).setHeader("IP").setKey("ipAddress").setAutoWidth(true);
        grid.addColumn(dto -> String.join(", ", dto.clients())).setHeader("Clientes").setKey("clients").setFlexGrow(1);
        if (canRevoke) {
            grid.addColumn(new ComponentRenderer<>(dto -> {
                Button button = new Button(VaadinIcon.CLOSE_CIRCLE.create(), click -> revoke.accept(dto));
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                button.setTooltipText("Cerrar esta sesion");
                button.setId(revokePrefix + dto.id());
                return button;
            })).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        }
        grid.setAllRowsVisible(true);
        HorizontalLayout heading = new HorizontalLayout(new H4(title), count);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        if (canRevoke) {
            Button all = new Button(revokeAllText, VaadinIcon.CLOSE_CIRCLE_O.create(), click -> revokeAll.run());
            all.setId(revokeAllId);
            all.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            heading.add(all);
        }
        add(heading);
        return grid;
    }

    @Override
    protected void load() {
        try {
            List<UserSessionDto> normal = client.sessions(userId);
            sessions.setItems(normal);
            sessionsCount.setText(normal.size() == 1 ? "1 sesion" : normal.size() + " sesiones");
            List<UserSessionDto> off = client.offlineSessions(userId);
            offline.setItems(off);
            offlineCount.setText(off.size() == 1 ? "1 sesion offline" : off.size() + " sesiones offline");
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void revoke(UserSessionDto session) {
        call(() -> client.revokeSession(userId, session.id()), "Sesion cerrada");
    }

    private void revokeOffline(UserSessionDto session) {
        call(() -> client.revokeOfflineSession(userId, session.id()), "Sesion offline revocada");
    }

    private void confirmRevokeAll() {
        confirm("Cerrar todas las sesiones", "La persona tendra que volver a entrar en todos sus clientes. "
                + "Las sesiones offline no se tocan.", () -> call(() -> client.revokeAllSessions(userId), "Sesiones cerradas"));
    }

    private void confirmRevokeAllOffline() {
        confirm("Revocar todas las sesiones offline", "Los tokens offline dejan de valer; un refresco con ellos fallara.",
                () -> call(() -> client.revokeAllOfflineSessions(userId), "Sesiones offline revocadas"));
    }

    private static void confirm(String header, String text, Runnable action) {
        ConfirmDialog dialog = new ConfirmDialog(header, text, "Cerrar", confirm -> action.run(), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    /** Cualquier cierre recarga las dos listas: son la foto del servicio, no un estado propio. */
    private void call(Runnable action, String done) {
        try {
            action.run();
            Notification.show(done, 3000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (NotFoundApiException gone) {
            Notification.show("Esa sesion ya no existe o no es de este usuario.", 5000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    private static String format(Instant instant) {
        return instant == null ? "" : DATE_TIME.format(instant);
    }
}

package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.UserCredentialDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Las credenciales de la persona (contrasena, OTP, llaves), sin secretos ni forma de
 * almacenamiento porque mto-users no los manda. Quitar una confirma, y si es la contrasena
 * avisa: la persona no podra entrar hasta que alguien le fije una temporal. Pide
 * {@code users-credentials-write}.
 */
class UserCredentialsPanel extends LazyPanel {

    static final String GRID_ID = "credentials-grid";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

    private final String userId;
    private final UsersClient client;

    private final Grid<UserCredentialDto> grid = new Grid<>();

    UserCredentialsPanel(String userId, UsersClient client, boolean canRemove) {
        this.userId = userId;
        this.client = client;
        add(new Paragraph("Lo que Keycloak guarda para autenticar a la persona. Ni el secreto ni como se almacena salen del servicio."));
        grid.setId(GRID_ID);
        grid.addColumn(UserCredentialDto::typeLabel).setHeader("Tipo").setKey("type").setAutoWidth(true);
        grid.addColumn(dto -> dto.userLabel() == null ? "" : dto.userLabel()).setHeader("Etiqueta").setKey("userLabel").setFlexGrow(1);
        grid.addColumn(dto -> dto.createdAt() == null ? "" : DATE_TIME.format(dto.createdAt())).setHeader("Creada").setKey("createdAt").setAutoWidth(true);
        if (canRemove) {
            grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        }
        grid.setAllRowsVisible(true);
        add(grid);
    }

    private Component rowActions(UserCredentialDto credential) {
        Button remove = new Button(VaadinIcon.TRASH.create(), click -> confirmRemove(credential));
        remove.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        remove.setTooltipText("Quitar la credencial");
        remove.setId("remove-credential-" + credential.id());
        return remove;
    }

    @Override
    protected void load() {
        try {
            grid.setItems(client.credentials(userId));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void confirmRemove(UserCredentialDto credential) {
        String text = credential.isPassword()
                ? "Sin contrasena la persona no podra entrar hasta que alguien le fije una temporal."
                : "Se quita " + credential.typeLabel() + (credential.userLabel() == null ? "" : " (" + credential.userLabel() + ")")
                        + "; la persona sigue entrando con lo demas.";
        ConfirmDialog dialog = new ConfirmDialog("Quitar " + credential.typeLabel(), text, "Quitar", confirm -> remove(credential), "Cancelar", cancel -> { });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void remove(UserCredentialDto credential) {
        try {
            client.deleteCredential(userId, credential.id());
            Notification.show("Credencial quitada: " + credential.typeLabel(), 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }
}

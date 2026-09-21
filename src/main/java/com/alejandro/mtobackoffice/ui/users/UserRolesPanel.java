package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.ClientDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleAssignmentDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleDto;
import com.alejandro.mtobackoffice.client.dto.users.RoleNamesRequest;
import com.alejandro.mtobackoffice.client.dto.users.UserRolesDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Los roles de cliente asignados directamente a una persona, y el alta de mas: se elige el
 * cliente, luego los roles que aun no tiene. Quitar uno es el {@code DELETE} con cuerpo de
 * mto-users. Los roles de realm se ensenan a titulo informativo (los perfiles estan entre ellos).
 * Los controles piden {@code users-roles-write}.
 */
class UserRolesPanel extends LazyPanel {

    static final String GRID_ID = "roles-grid";
    static final String CLIENT_PICKER_ID = "role-client";
    static final String ROLES_PICKER_ID = "role-names";
    static final String ASSIGN_BUTTON_ID = "role-assign";

    /** Una fila: un rol de un cliente. */
    record RoleRow(String clientId, String role) {
    }

    private final String userId;
    private final UsersClient client;
    private final boolean canWrite;

    private final Span realmRoles = new Span();
    private final Grid<RoleRow> grid = new Grid<>();
    private final ComboBox<ClientDto> clientPicker = new ComboBox<>("Cliente");
    private final MultiSelectComboBox<String> rolePicker = new MultiSelectComboBox<>("Roles");
    private final Button assign = new Button("Asignar", VaadinIcon.PLUS.create(), click -> assign());

    private UserRolesDto current = new UserRolesDto(List.of(), List.of());

    UserRolesPanel(String userId, UsersClient client, boolean canWrite) {
        this.userId = userId;
        this.client = client;
        this.canWrite = canWrite;
        add(new Paragraph("Asignaciones directas de roles de cliente. Los clientes protegidos del realm no se listan ni se asignan."));
        grid.setId(GRID_ID);
        grid.addColumn(RoleRow::clientId).setHeader("Cliente").setKey("clientId").setAutoWidth(true);
        grid.addColumn(RoleRow::role).setHeader("Rol").setKey("role").setFlexGrow(1);
        if (canWrite) {
            grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        }
        grid.setAllRowsVisible(true);
        add(grid, realmRoles);
        if (canWrite) {
            clientPicker.setId(CLIENT_PICKER_ID);
            clientPicker.setItemLabelGenerator(ClientDto::label);
            clientPicker.setWidth("20rem");
            clientPicker.addValueChangeListener(change -> refreshRolePicker());
            rolePicker.setId(ROLES_PICKER_ID);
            rolePicker.setWidth("24rem");
            rolePicker.setEnabled(false);
            rolePicker.addValueChangeListener(change -> assign.setEnabled(!change.getValue().isEmpty()));
            assign.setId(ASSIGN_BUTTON_ID);
            assign.setEnabled(false);
            HorizontalLayout toolbar = new HorizontalLayout(clientPicker, rolePicker, assign);
            toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
            add(toolbar);
        }
    }

    private Component rowActions(RoleRow row) {
        Button remove = new Button(VaadinIcon.MINUS_CIRCLE.create(), click -> remove(row));
        remove.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        remove.setTooltipText("Quitar el rol");
        remove.setId("remove-role-" + row.clientId() + "-" + row.role());
        return remove;
    }

    @Override
    protected void load() {
        try {
            paint(client.userRoles(userId));
            if (canWrite) {
                clientPicker.setItems(client.clients());
                clientPicker.clear();
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void paint(UserRolesDto roles) {
        current = roles;
        List<RoleRow> rows = new ArrayList<>();
        for (ClientRoleAssignmentDto assignment : roles.clientRoles()) {
            for (String role : assignment.roles()) {
                rows.add(new RoleRow(assignment.clientId(), role));
            }
        }
        grid.setItems(rows);
        realmRoles.setText(roles.realmRoles().isEmpty()
                ? "Sin roles de realm."
                : "Roles de realm (los perfiles estan entre ellos): " + String.join(", ", roles.realmRoles()));
        refreshRolePicker();
    }

    /** Los roles del cliente elegido que la persona aun no tiene. */
    private void refreshRolePicker() {
        if (!canWrite) {
            return;
        }
        ClientDto chosen = clientPicker.getValue();
        rolePicker.clear();
        if (chosen == null) {
            rolePicker.setItems(List.of());
            rolePicker.setEnabled(false);
            return;
        }
        try {
            Set<String> assigned = current.clientRoles().stream()
                    .filter(assignment -> chosen.clientId().equals(assignment.clientId()))
                    .flatMap(assignment -> assignment.roles().stream())
                    .collect(java.util.stream.Collectors.toSet());
            rolePicker.setItems(client.clientRoles(chosen.clientId()).stream()
                    .map(ClientRoleDto::name)
                    .filter(name -> !assigned.contains(name))
                    .toList());
            rolePicker.setEnabled(true);
        } catch (BackofficeApiException failure) {
            rolePicker.setItems(List.of());
            rolePicker.setEnabled(false);
            UiErrors.show(failure);
        }
    }

    private void assign() {
        ClientDto chosen = clientPicker.getValue();
        List<String> names = rolePicker.getSelectedItems().stream().sorted().toList();
        if (chosen == null || names.isEmpty()) {
            return;
        }
        try {
            paint(client.addClientRoles(userId, chosen.clientId(), new RoleNamesRequest(names)));
            Notification.show(names.size() == 1 ? "Rol " + names.getFirst() + " asignado" : names.size() + " roles asignados",
                    3000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void remove(RoleRow row) {
        try {
            paint(client.removeClientRoles(userId, row.clientId(), new RoleNamesRequest(List.of(row.role()))));
            Notification.show("Rol " + row.role() + " quitado", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

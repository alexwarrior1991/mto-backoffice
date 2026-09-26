package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.RealmProfileSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Los perfiles de una persona (roles compuestos de realm con prefijo {@code mto-}) y el alta de
 * uno mas. Asignar y quitar devuelven la lista del usuario, que es lo que se pinta: sin un GET
 * de mas. Los botones piden {@code users-profiles-write}.
 */
class UserProfilesPanel extends LazyPanel {

    static final String GRID_ID = "profiles-grid";
    static final String ASSIGN_PICKER_ID = "profile-assign";
    static final String ASSIGN_BUTTON_ID = "profile-assign-button";

    private final String userId;
    private final UsersClient client;
    private final boolean canWrite;

    private final Grid<RealmProfileSummaryDto> grid = new Grid<>();
    private final ComboBox<RealmProfileSummaryDto> assignable = new ComboBox<>("Perfil");
    private final Button assign = new Button("Asignar", VaadinIcon.PLUS.create(), click -> assign());

    private List<RealmProfileSummaryDto> catalogue = List.of();
    private List<RealmProfileSummaryDto> assigned = List.of();

    UserProfilesPanel(String userId, UsersClient client, boolean canWrite) {
        this.userId = userId;
        this.client = client;
        this.canWrite = canWrite;
        add(new Paragraph("Un perfil es un rol compuesto de realm (mto-...) que concede roles de cliente. "
                + "Aqui se ven las asignaciones directas: quien tiene un rol por un perfil aparece en el perfil, no en el rol."));
        grid.setId(GRID_ID);
        grid.addColumn(RealmProfileSummaryDto::name).setHeader("Perfil").setKey("name").setAutoWidth(true);
        grid.addColumn(dto -> dto.description() == null ? "" : dto.description()).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        if (canWrite) {
            grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey("actions").setAutoWidth(true).setFlexGrow(0);
        }
        grid.setAllRowsVisible(true);
        add(grid);
        if (canWrite) {
            assignable.setId(ASSIGN_PICKER_ID);
            assignable.setItemLabelGenerator(dto -> dto.description() == null || dto.description().isBlank()
                    ? dto.name() : dto.name() + " (" + dto.description() + ")");
            assignable.setWidth("24rem");
            assignable.addValueChangeListener(change -> assign.setEnabled(change.getValue() != null));
            assign.setId(ASSIGN_BUTTON_ID);
            assign.setEnabled(false);
            HorizontalLayout toolbar = new HorizontalLayout(assignable, assign);
            toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
            add(toolbar);
        }
    }

    private Component rowActions(RealmProfileSummaryDto profile) {
        Button remove = new Button(VaadinIcon.MINUS_CIRCLE.create(), click -> remove(profile));
        remove.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        remove.setTooltipText("Quitar el perfil");
        remove.setId("remove-profile-" + profile.name());
        return remove;
    }

    @Override
    protected void load() {
        try {
            paint(client.userProfiles(userId));
            if (canWrite) {
                catalogue = client.profiles();
                refreshAssignable();
            }
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void paint(List<RealmProfileSummaryDto> profiles) {
        assigned = profiles;
        grid.setItems(profiles);
        refreshAssignable();
    }

    /** Solo lo que falta por asignar; volver a asignar lo asignado es un no-op para el servicio, pero confunde. */
    private void refreshAssignable() {
        if (!canWrite) {
            return;
        }
        Set<String> names = assigned.stream().map(RealmProfileSummaryDto::name).collect(Collectors.toSet());
        assignable.setItems(catalogue.stream().filter(dto -> !names.contains(dto.name())).toList());
        assignable.clear();
    }

    private void assign() {
        RealmProfileSummaryDto chosen = assignable.getValue();
        if (chosen == null) {
            return;
        }
        try {
            paint(client.assignProfile(userId, chosen.name()));
            Notification.show("Perfil " + chosen.name() + " asignado", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void remove(RealmProfileSummaryDto profile) {
        try {
            paint(client.removeProfile(userId, profile.name()));
            Notification.show("Perfil " + profile.name() + " quitado", 3000, Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

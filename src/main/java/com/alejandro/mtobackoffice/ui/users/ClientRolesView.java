package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.ClientDto;
import com.alejandro.mtobackoffice.client.dto.users.ClientRoleDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.UserRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Locale;

/**
 * Los roles de cada cliente del realm (menos los protegidos, que mto-users no lista) y quien
 * tiene cada uno asignado directamente. Solo lectura: los roles los define cada servicio en su
 * partial import. Quien tiene un rol por un perfil aparece en el perfil, no aqui.
 */
@Route(value = ClientRolesView.ROUTE, layout = MainLayout.class)
@PageTitle("Roles de cliente")
@Menu(title = "Roles de cliente", order = 42, icon = "vaadin:key")
@RolesAllowed(UserRoles.USERS_READ)
public class ClientRolesView extends VerticalLayout {

    public static final String ROUTE = UsersView.ROUTE_PREFIX + "/roles";
    static final String CLIENT_PICKER_ID = "roles-client";
    static final String CATALOGUE_ID = "roles-catalogue";
    static final String FILTER_ID = "roles-filter";
    static final String MEMBERS_PREFIX = "role-members";

    private final UsersClient client;

    private final ComboBox<ClientDto> clientPicker = new ComboBox<>("Cliente");
    private final TextField filter = new TextField();
    private final Span count = new Span();
    private final Grid<ClientRoleDto> catalogue = new Grid<>();
    private final MembersPanel members = new MembersPanel(MEMBERS_PREFIX,
            "Solo asignaciones directas del rol. Quien lo tiene por un perfil aparece en el perfil, no aqui.");

    private List<ClientRoleDto> roles = List.of();

    public ClientRolesView(UsersClient client) {
        this.client = client;
        setSizeFull();
        add(new H2("Roles de cliente"));
        SplitLayout split = new SplitLayout(catalogueSide(), membersSide());
        split.setSplitterPosition(40);
        split.setSizeFull();
        add(split);
        expand(split);
        loadClients();
    }

    private Component catalogueSide() {
        clientPicker.setId(CLIENT_PICKER_ID);
        clientPicker.setItemLabelGenerator(ClientDto::label);
        clientPicker.setPlaceholder("Elige un cliente");
        clientPicker.addValueChangeListener(change -> loadRoles(change.getValue()));
        filter.setId(FILTER_ID);
        filter.setPlaceholder("Filtrar por nombre o descripcion");
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(change -> applyFilter());
        HorizontalLayout toolbar = new HorizontalLayout(clientPicker, filter, count);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(filter);
        catalogue.setId(CATALOGUE_ID);
        catalogue.addColumn(ClientRoleDto::name).setHeader("Rol").setKey("name").setAutoWidth(true);
        catalogue.addColumn(dto -> dto.description() == null ? "" : dto.description()).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        catalogue.addColumn(dto -> Boolean.TRUE.equals(dto.composite()) ? "Si" : "No").setHeader("Compuesto").setKey("composite").setAutoWidth(true);
        catalogue.setSelectionMode(Grid.SelectionMode.SINGLE);
        catalogue.addSelectionListener(selection -> selection.getFirstSelectedItem().ifPresentOrElse(this::showMembers, members::clear));
        catalogue.setSizeFull();
        VerticalLayout side = new VerticalLayout(toolbar, catalogue);
        side.setPadding(false);
        side.setSizeFull();
        side.expand(catalogue);
        return side;
    }

    private Component membersSide() {
        VerticalLayout side = new VerticalLayout(new Paragraph("Elige un rol para ver quien lo tiene."), members);
        side.setPadding(false);
        return side;
    }

    private void loadClients() {
        try {
            clientPicker.setItems(client.clients());
        } catch (BackofficeApiException failure) {
            clientPicker.setItems(List.of());
            UiErrors.show(failure);
        }
    }

    private void loadRoles(ClientDto chosen) {
        members.clear();
        if (chosen == null) {
            roles = List.of();
        } else {
            try {
                roles = client.clientRoles(chosen.clientId());
            } catch (BackofficeApiException failure) {
                roles = List.of();
                UiErrors.show(failure);
            }
        }
        applyFilter();
    }

    private void applyFilter() {
        String text = filter.getValue() == null ? "" : filter.getValue().trim().toLowerCase(Locale.ROOT);
        List<ClientRoleDto> shown = roles.stream()
                .filter(dto -> text.isEmpty() || contains(dto.name(), text) || contains(dto.description(), text))
                .toList();
        catalogue.deselectAll();
        catalogue.setItems(shown);
        count.setText(clientPicker.getValue() == null ? "" : shown.size() == roles.size()
                ? roles.size() + " roles" : shown.size() + " de " + roles.size() + " roles");
    }

    private static boolean contains(String value, String text) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(text);
    }

    private void showMembers(ClientRoleDto role) {
        ClientDto chosen = clientPicker.getValue();
        if (chosen == null) {
            return;
        }
        members.show(chosen.clientId() + " / " + role.name(),
                first -> client.clientRoleMembers(chosen.clientId(), role.name(), first, MembersPanel.PAGE_SIZE));
    }
}

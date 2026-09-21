package com.alejandro.mtobackoffice.ui.users;

import com.alejandro.mtobackoffice.client.dto.users.ClientRoleAssignmentDto;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileDto;
import com.alejandro.mtobackoffice.client.dto.users.RealmProfileSummaryDto;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.users.UsersClient;
import com.alejandro.mtobackoffice.configuration.security.UserRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * El catalogo de perfiles del realm (roles compuestos con prefijo {@code mto-}): la lista, con
 * filtro local porque el servicio la devuelve entera, y para el elegido lo que concede (roles por
 * cliente y de realm) y sus miembros, paginados sin total. Solo lectura: un perfil se crea en
 * Keycloak (o en el partial import de cada servicio), no aqui.
 */
@Route(value = UserProfilesView.ROUTE, layout = MainLayout.class)
@PageTitle("Perfiles de usuario")
@Menu(title = "Perfiles de usuario", order = 41, icon = "vaadin:group")
@RolesAllowed(UserRoles.USERS_READ)
public class UserProfilesView extends VerticalLayout {

    public static final String ROUTE = UsersView.ROUTE_PREFIX + "/perfiles";
    static final String CATALOGUE_ID = "profiles-catalogue";
    static final String FILTER_ID = "profile-filter";
    static final String GRANTS_ID = "grants-grid";
    static final String MEMBERS_PREFIX = "members";

    private final UsersClient client;

    private final TextField filter = new TextField();
    private final Span count = new Span();
    private final Grid<RealmProfileSummaryDto> catalogue = new Grid<>();
    private final VerticalLayout detail = new VerticalLayout();
    private final H3 name = new H3();
    private final Paragraph description = new Paragraph();
    private final Grid<UserRolesPanel.RoleRow> grants = new Grid<>();
    private final Span realmRoles = new Span();
    private final MembersPanel members = new MembersPanel(MEMBERS_PREFIX,
            "Solo asignaciones directas del perfil. Quien tiene sus roles por otro camino no aparece.");

    private List<RealmProfileSummaryDto> profiles = List.of();

    public UserProfilesView(UsersClient client) {
        this.client = client;
        setSizeFull();
        add(new H2("Perfiles de usuario"));
        SplitLayout split = new SplitLayout(catalogueSide(), detailSide());
        split.setSplitterPosition(32);
        split.setSizeFull();
        add(split);
        expand(split);
        load();
    }

    private Component catalogueSide() {
        filter.setId(FILTER_ID);
        filter.setPlaceholder("Filtrar por nombre o descripcion");
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(change -> applyFilter());
        filter.setWidthFull();
        catalogue.setId(CATALOGUE_ID);
        catalogue.addColumn(RealmProfileSummaryDto::name).setHeader("Perfil").setKey("name").setAutoWidth(true);
        catalogue.addColumn(dto -> dto.description() == null ? "" : dto.description()).setHeader("Descripcion").setKey("description").setFlexGrow(1);
        catalogue.setSelectionMode(Grid.SelectionMode.SINGLE);
        catalogue.addSelectionListener(selection -> selection.getFirstSelectedItem().ifPresentOrElse(this::show, this::clearDetail));
        catalogue.setSizeFull();
        VerticalLayout side = new VerticalLayout(filter, count, catalogue);
        side.setPadding(false);
        side.setSizeFull();
        side.expand(catalogue);
        return side;
    }

    private Component detailSide() {
        grants.setId(GRANTS_ID);
        grants.addColumn(UserRolesPanel.RoleRow::clientId).setHeader("Cliente").setKey("clientId").setAutoWidth(true);
        grants.addColumn(UserRolesPanel.RoleRow::role).setHeader("Rol").setKey("role").setFlexGrow(1);
        grants.setAllRowsVisible(true);
        detail.add(name, description, new H4("Lo que concede"), grants, realmRoles, members);
        detail.setVisible(false);
        VerticalLayout side = new VerticalLayout(new Paragraph("Elige un perfil para ver lo que concede y quien lo tiene."), detail);
        side.setPadding(false);
        return side;
    }

    private void load() {
        try {
            profiles = client.profiles();
        } catch (BackofficeApiException failure) {
            profiles = List.of();
            UiErrors.show(failure);
        }
        applyFilter();
    }

    private void applyFilter() {
        String text = filter.getValue() == null ? "" : filter.getValue().trim().toLowerCase(Locale.ROOT);
        List<RealmProfileSummaryDto> shown = profiles.stream()
                .filter(dto -> text.isEmpty() || contains(dto.name(), text) || contains(dto.description(), text))
                .toList();
        catalogue.deselectAll();
        catalogue.setItems(shown);
        count.setText(shown.size() == profiles.size() ? profiles.size() + " perfiles" : shown.size() + " de " + profiles.size() + " perfiles");
    }

    private static boolean contains(String value, String text) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(text);
    }

    private void show(RealmProfileSummaryDto summary) {
        try {
            RealmProfileDto profile = client.profile(summary.name());
            name.setText(profile.name());
            description.setText(profile.description() == null ? "" : profile.description());
            List<UserRolesPanel.RoleRow> rows = new ArrayList<>();
            for (ClientRoleAssignmentDto assignment : profile.clientRoles()) {
                for (String role : assignment.roles()) {
                    rows.add(new UserRolesPanel.RoleRow(assignment.clientId(), role));
                }
            }
            grants.setItems(rows);
            realmRoles.setText(profile.realmRoles().isEmpty() ? "Sin roles de realm." : "Roles de realm: " + String.join(", ", profile.realmRoles()));
            detail.setVisible(true);
            members.show(profile.name(), first -> client.profileMembers(profile.name(), first, MembersPanel.PAGE_SIZE));
        } catch (BackofficeApiException failure) {
            clearDetail();
            UiErrors.show(failure);
        }
    }

    private void clearDetail() {
        detail.setVisible(false);
        members.clear();
    }
}

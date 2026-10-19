package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.client.error.NotFoundApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.support.LazyPanel;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import java.util.UUID;
import java.util.function.Consumer;

/**
 * La ficha de una persona de mto-resources, como en mto-frontend: su cabecera (codigo, nombre,
 * categoria, empresa, usuario y si esta activa), los botones de la lista (modificar y reactivar con
 * {@code resources-write}, desactivar con {@code resources-delete}, el historial siempre) y la pestana
 * «Cualificaciones», que pide sus datos al abrirse. Es el enlace de los avisos de caducidad de
 * mto-notification ({@code /recursos/personal/{id}}).
 */
@Route(value = ResourcesRoutes.PEOPLE + "/:" + PersonDetailView.PERSON_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class PersonDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String PERSON_ID_PARAMETER = "personId";
    public static final String QUALIFICATIONS_TAB = "Cualificaciones";
    public static final String ABSENCES_TAB = "Ausencias";

    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;

    private final H2 title = new H2();
    private final Span categoryBadge = new Span();
    private final Span stateBadge = new Span();
    private final Div summary = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final VerticalLayout tabsHolder = new VerticalLayout();
    private final List<LazyPanel> panels = new ArrayList<>();
    private PersonDto person;

    public static RouteParameters parametersOf(UUID personId) {
        return new RouteParameters(PERSON_ID_PARAMETER, personId.toString());
    }

    public PersonDetailView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();
        categoryBadge.getElement().getThemeList().add("badge");
        categoryBadge.setId("person-category-badge");
        stateBadge.getElement().getThemeList().add("badge");
        stateBadge.setId("person-state");
        HorizontalLayout heading = new HorizontalLayout(title, categoryBadge, stateBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("person-summary");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        buttons.getStyle().set("flex-wrap", "wrap");
        tabsHolder.setPadding(false);
        tabsHolder.setSizeFull();
        add(heading, summary, buttons, tabsHolder);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(PERSON_ID_PARAMETER).orElse("");
        load(id, target -> event.forwardTo(target));
    }

    /** Lee la ficha y la pinta. Si no existe, se dice y de vuelta a la lista; otro fallo deja el motivo y «Reintentar». */
    private void load(String id, Consumer<Class<? extends Component>> toList) {
        try {
            show(clients.people().findById(UUID.fromString(id)));
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe la persona " + id, 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
            toList.accept(PeopleView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            Screens.showLoadFailure(this, "la persona", failure, () -> UI.getCurrent().navigate(PeopleView.class),
                    () -> load(id, target -> UI.getCurrent().navigate(target)));
        }
    }

    @Override
    public String getPageTitle() {
        return person == null ? "Persona" : person.label();
    }

    private void show(PersonDto loaded) {
        Screens.clearLoadFailure(this);
        paint(loaded);
        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        panels.clear();
        PersonQualificationsPanel qualifications = new PersonQualificationsPanel(() -> person, clients, canWrite, canDelete);
        panels.add(qualifications);
        tabs.add(QUALIFICATIONS_TAB, qualifications);
        PersonAbsencesPanel absences = new PersonAbsencesPanel(() -> person, clients, canWrite, canDelete);
        panels.add(absences);
        tabs.add(ABSENCES_TAB, absences);
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

    /** Lo que devolvio el servicio tras un cambio: se pinta, y las pestanas ya abiertas se releen. */
    private void changed(PersonDto updated) {
        paint(updated);
        panels.forEach(LazyPanel::reloadIfLoaded);
    }

    private void reload() {
        try {
            changed(clients.people().findById(person.id()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void paint(PersonDto loaded) {
        person = loaded;
        title.setText(loaded.label());
        categoryBadge.setText(ResourcesFormats.category(loaded.category()));
        stateBadge.setText(ResourcesFormats.state(loaded.active()));
        stateBadge.getElement().getThemeList().set("success", loaded.isActive());
        stateBadge.getElement().getThemeList().set("contrast", !loaded.isActive());
        summary.removeAll();
        summary.add(new Div(ResourcesHistory.join("Codigo: " + loaded.code(), "Empresa: " + orDash(ResourcesFormats.employer(loaded)),
                "Usuario: " + orDash(loaded.username()))));
        summary.add(new Div(ResourcesHistory.join("N. empleado: " + orDash(loaded.employeeNumber()), "Telefono: " + orDash(loaded.phone()))));
        paintButtons(loaded);
    }

    private void paintButtons(PersonDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver a Personal", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(PeopleView.class)));
        Button history = new Button("Historial", VaadinIcon.CLOCK.create(),
                click -> ResourcesHistory.dialog(loaded.code(), (page, size) -> clients.people().revisions(loaded.id(), page, size),
                        ResourcesHistory::person).open());
        history.setId("person-history");
        buttons.add(history);
        if (canWrite) {
            Button edit = new Button("Modificar", VaadinIcon.EDIT.create(), click -> new PersonEditorDialog(loaded, clients, this::reload).open());
            edit.setId("person-edit");
            buttons.add(edit);
        }
        if (canDelete && loaded.isActive()) {
            Button deactivate = new Button("Desactivar", VaadinIcon.BAN.create(),
                    click -> Screens.confirm("Desactivar " + loaded.label(), PeopleView.DEACTIVATE_TEXT, "Desactivar", this::deactivate));
            deactivate.setId("person-deactivate");
            deactivate.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(deactivate);
        }
        if (canWrite && !loaded.isActive()) {
            Button reactivate = new Button("Reactivar", VaadinIcon.CHECK_CIRCLE.create(), click -> reactivate());
            reactivate.setId("person-reactivate");
            buttons.add(reactivate);
        }
    }

    private void deactivate() {
        try {
            clients.people().deactivate(person.id());
            Screens.success("Desactivada " + person.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    private void reactivate() {
        try {
            changed(clients.people().update(person.id(), MergePatch.of(PersonUpdateRequest.active(true), person.version())));
            Screens.success("Reactivada " + person.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private static String orDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}

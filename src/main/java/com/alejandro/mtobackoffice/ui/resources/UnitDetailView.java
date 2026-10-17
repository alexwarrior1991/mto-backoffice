package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitDto;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitKind;
import com.alejandro.mtobackoffice.client.dto.resources.OrganizationUnitUpdateRequest;
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
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * La ficha de una unidad del organigrama: su cabecera, de quien depende, quien la dirige y sus EP;
 * en un equipo, ademas, sus linemen frente a la regla de 2 a 6, su supervisor y su jefe de
 * supervisores. Modificar y reactivar piden {@code resources-write}; desactivar,
 * {@code resources-delete}. Un equipo vuelve completo: reactivarlo pide sus linemen. La pestana
 * «Linemen» de un equipo y «Unidades» de las demas piden sus datos al abrirse.
 */
@Route(value = ResourcesRoutes.ORGANIZATION + "/:" + UnitDetailView.UNIT_ID_PARAMETER, layout = MainLayout.class)
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class UnitDetailView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    public static final String UNIT_ID_PARAMETER = "unitId";
    public static final String MEMBERS_TAB = "Linemen";
    public static final String CHILDREN_TAB = "Unidades";

    private final ResourcesClients clients;
    private final ResourcesNames names;
    private final boolean canWrite;
    private final boolean canDelete;

    private final H2 title = new H2();
    private final Span kindBadge = new Span();
    private final Span stateBadge = new Span();
    private final Div summary = new Div();
    private final HorizontalLayout buttons = new HorizontalLayout();
    private final VerticalLayout tabsHolder = new VerticalLayout();
    private final List<LazyPanel> panels = new ArrayList<>();

    private OrganizationUnitDto unit;

    public static RouteParameters parametersOf(UUID unitId) {
        return new RouteParameters(UNIT_ID_PARAMETER, unitId.toString());
    }

    public UnitDetailView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.names = ResourcesNames.of(clients, authentication);
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();
        kindBadge.getElement().getThemeList().add("badge");
        stateBadge.getElement().getThemeList().add("badge");
        stateBadge.setId("unit-state");
        HorizontalLayout heading = new HorizontalLayout(title, kindBadge, stateBadge);
        heading.setAlignItems(FlexComponent.Alignment.BASELINE);
        summary.setId("unit-summary");
        buttons.setAlignItems(FlexComponent.Alignment.BASELINE);
        buttons.getStyle().set("flex-wrap", "wrap");
        tabsHolder.setPadding(false);
        tabsHolder.setSizeFull();
        add(heading, summary, buttons, tabsHolder);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String id = event.getRouteParameters().get(UNIT_ID_PARAMETER).orElse("");
        load(id, target -> event.forwardTo(target));
    }

    /** Lee la ficha y la pinta. Si no existe, se dice y de vuelta al organigrama; otro fallo deja el motivo y «Reintentar». */
    private void load(String id, Consumer<Class<? extends Component>> toList) {
        try {
            show(clients.organization().findById(UUID.fromString(id)));
        } catch (IllegalArgumentException | NotFoundApiException missing) {
            Notification.show("No existe la unidad " + id, 5000, Notification.Position.BOTTOM_START).addThemeVariants(NotificationVariant.LUMO_ERROR);
            toList.accept(OrganizationView.class);
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            Screens.showLoadFailure(this, "la unidad", failure, () -> UI.getCurrent().navigate(OrganizationView.class),
                    () -> load(id, target -> UI.getCurrent().navigate(target)));
        }
    }

    @Override
    public String getPageTitle() {
        return unit == null ? "Unidad" : unit.label();
    }

    OrganizationUnitDto unit() {
        return unit;
    }

    private void show(OrganizationUnitDto loaded) {
        Screens.clearLoadFailure(this);
        paint(loaded);
        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();
        panels.clear();
        if (loaded.isTeam()) {
            addPanel(tabs, MEMBERS_TAB, new TeamMembersPanel(this::unit, clients, canWrite, this::changed));
        } else {
            addPanel(tabs, CHILDREN_TAB, new ChildUnitsPanel(this::unit, clients, names));
        }
        tabs.addSelectedChangeListener(change -> loadTab(tabs, change.getSelectedTab()));
        tabsHolder.removeAll();
        tabsHolder.add(tabs);
        loadTab(tabs, tabs.getSelectedTab());
    }

    private void addPanel(TabSheet tabs, String label, LazyPanel panel) {
        panels.add(panel);
        tabs.add(label, panel);
    }

    private static void loadTab(TabSheet tabs, Tab tab) {
        if (tab != null && tabs.getComponent(tab) instanceof LazyPanel panel) {
            panel.ensureLoaded();
        }
    }

    /** Lo que devolvio el servicio tras un cambio: se pinta, y las pestanas ya abiertas se releen. */
    void changed(OrganizationUnitDto updated) {
        paint(updated);
        panels.forEach(LazyPanel::reloadIfLoaded);
    }

    private void reload() {
        try {
            changed(clients.organization().findById(unit.id()));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }

    private void paint(OrganizationUnitDto loaded) {
        unit = loaded;
        title.setText(loaded.label());
        kindBadge.setText(ResourcesFormats.kind(loaded.kind()));
        stateBadge.setText(ResourcesFormats.state(loaded.active()));
        stateBadge.getElement().getThemeList().set("success", loaded.isActive());
        stateBadge.getElement().getThemeList().set("contrast", !loaded.isActive());
        summary.removeAll();
        List<String> line = new ArrayList<>();
        if (loaded.discipline() != null) {
            line.add("Disciplina: " + ResourcesFormats.discipline(loaded.discipline()));
        }
        line.add("Depende de: " + (loaded.parent() == null ? "nada (raiz)" : loaded.parent().label()));
        line.add("Jefe: " + ResourcesFormats.head(loaded.head()));
        line.add("EP: " + (loaded.executionPackageIds().isEmpty() ? "-" : names.packageNames(loaded.executionPackageIds())));
        summary.add(new Div(String.join(" · ", line)));
        if (loaded.isTeam()) {
            Div team = new Div(String.join(" · ", "Linemen: " + ResourcesFormats.linemen(loaded.kind(), loaded.linemen()),
                    "Completo: " + Screens.yesNo(loaded.complete()),
                    "Supervisor: " + (loaded.supervisor() == null ? "-" : loaded.supervisor().label()),
                    "Jefe de supervisores: " + (loaded.chiefSupervisor() == null ? "-" : loaded.chiefSupervisor().label()),
                    "Vehiculo: " + (loaded.defaultVehicle() == null ? "-" : loaded.defaultVehicle().label())));
            team.setId("unit-team-summary");
            summary.add(team);
        }
        paintButtons(loaded);
    }

    private void paintButtons(OrganizationUnitDto loaded) {
        buttons.removeAll();
        buttons.add(new Button("Volver al organigrama", VaadinIcon.ARROW_LEFT.create(), click -> UI.getCurrent().navigate(OrganizationView.class)));
        Button parent = new Button("Ir a " + (loaded.parent() == null ? "" : loaded.parent().code()), VaadinIcon.LEVEL_UP.create(),
                click -> UI.getCurrent().navigate(UnitDetailView.class, parametersOf(loaded.parent().id())));
        parent.setId("unit-go-parent");
        parent.setVisible(loaded.parent() != null);
        Button people = new Button("Ver su personal", VaadinIcon.USERS.create(),
                click -> UI.getCurrent().navigate(PeopleView.class, QueryParameters.of(PeopleView.UNIT_PARAMETER, loaded.id().toString())));
        people.setId("unit-people");
        Button history = new Button("Historial", VaadinIcon.CLOCK.create(),
                click -> ResourcesHistory.dialog(loaded.code(), (page, size) -> clients.organization().revisions(loaded.id(), page, size),
                        ResourcesHistory::unit).open());
        history.setId("unit-history");
        buttons.add(parent, people, history);
        if (canWrite && loaded.isActive()) {
            Button edit = new Button("Modificar", VaadinIcon.EDIT.create(), click -> openEditor());
            edit.setId("unit-edit");
            buttons.add(edit);
            if (!loaded.isTeam() && loaded.kind() != OrganizationUnitKind.UNKNOWN) {
                Button under = new Button("Nueva unidad debajo", VaadinIcon.PLUS.create(), click -> openCreateUnder());
                under.setId("unit-create-under");
                buttons.add(under);
            }
        }
        if (canDelete && loaded.isActive()) {
            Button deactivate = new Button("Desactivar", VaadinIcon.BAN.create(), click -> Screens.confirm("Desactivar " + loaded.label(),
                    loaded.isTeam()
                            ? "No se borra: sus linemen salen de el hoy y queda fuera de los turnos nuevos. Para reactivarlo hara falta darle linemen otra vez."
                            : "No se borra. Si tiene unidades activas debajo, el servicio no lo deja: desactivalas o muevelas antes.",
                    "Desactivar", this::deactivate));
            deactivate.setId("unit-deactivate");
            deactivate.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttons.add(deactivate);
        }
        if (canWrite && !loaded.isActive()) {
            Button reactivate = new Button("Reactivar", VaadinIcon.CHECK_CIRCLE.create(), click -> reactivate());
            reactivate.setId("unit-reactivate");
            buttons.add(reactivate);
        }
    }

    private OrganizationTree loadTree() {
        try {
            return new OrganizationTree(clients.organization().tree(false));
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
            return null;
        }
    }

    private void openEditor() {
        OrganizationTree tree = loadTree();
        if (tree != null) {
            new UnitEditorDialog(unit, null, tree, clients, names, this::changed).open();
        }
    }

    private void openCreateUnder() {
        OrganizationTree tree = loadTree();
        if (tree != null) {
            new UnitEditorDialog(null, unit.summary(), tree, clients, names, created -> {
                UI.getCurrent().navigate(UnitDetailView.class, parametersOf(created.id()));
            }).open();
        }
    }

    private void deactivate() {
        try {
            clients.organization().deactivate(unit.id());
            Screens.success("Desactivada " + unit.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        reload();
    }

    /** Un equipo vuelve con sus linemen, que se eligen en su dialogo; las demas unidades, con un {@code PATCH active=true}. */
    private void reactivate() {
        if (unit.isTeam()) {
            TeamMembersDialog.reactivate(unit, clients, this::changed).open();
            return;
        }
        try {
            changed(clients.organization().update(unit.id(), MergePatch.of(OrganizationUnitUpdateRequest.reactivate(), unit.version())));
            Screens.success("Reactivada " + unit.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
    }
}

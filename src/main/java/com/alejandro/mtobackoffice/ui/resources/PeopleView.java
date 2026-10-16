package com.alejandro.mtobackoffice.ui.resources;

import com.alejandro.mtobackoffice.client.configuration.MasterFilters;
import com.alejandro.mtobackoffice.client.dto.MergePatch;
import com.alejandro.mtobackoffice.client.dto.resources.PersonCategory;
import com.alejandro.mtobackoffice.client.dto.resources.PersonDto;
import com.alejandro.mtobackoffice.client.dto.resources.PersonFilter;
import com.alejandro.mtobackoffice.client.dto.resources.PersonUpdateRequest;
import com.alejandro.mtobackoffice.client.error.BackofficeApiException;
import com.alejandro.mtobackoffice.configuration.security.ResourcesRoles;
import com.alejandro.mtobackoffice.ui.MainLayout;
import com.alejandro.mtobackoffice.ui.master.EnabledFilter;
import com.alejandro.mtobackoffice.ui.support.LazyPages;
import com.alejandro.mtobackoffice.ui.support.Screens;
import com.alejandro.mtobackoffice.ui.support.UiErrors;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

/**
 * El personal de mto-resources, paginado, buscado y filtrado en el servidor, como en mto-frontend.
 * Toda persona tiene ficha, con o sin usuario de Keycloak. No se borra nunca: desactivar es un
 * {@code DELETE} ({@code resources-delete}) y reactivar un {@code PATCH active=true}
 * ({@code resources-write}). Quien dirige una unidad no se desactiva hasta que tenga otro jefe, y un
 * lineman sale de su equipo hoy salvo que lo dejara con menos de 2: lo dice el servicio (409
 * {@code UNI-001}). El historial es lectura y lo ofrece toda fila.
 */
@Route(value = ResourcesRoutes.PEOPLE, layout = MainLayout.class)
@PageTitle("Personal")
@Menu(title = "Personal", order = 70, icon = "vaadin:user-card")
@RolesAllowed(ResourcesRoles.RESOURCES_READ)
public class PeopleView extends VerticalLayout {

    static final int PAGE_SIZE = 50;
    public static final String ACTIONS_COLUMN = "actions";
    private static final List<String> DEFAULT_SORT = List.of("lastName,asc", "firstName,asc");

    private final ResourcesClients clients;
    private final boolean canWrite;
    private final boolean canDelete;

    private final TextField search = new TextField("Buscar");
    private final MultiSelectComboBox<PersonCategory> categories = new MultiSelectComboBox<>("Categoria");
    private final Select<EnabledFilter> state = EnabledFilter.select("Estado", "Todas", "Activas", "Inactivas");
    private final Span count = new Span();
    private final Grid<PersonDto> grid = new Grid<>();
    private LazyPages<PersonDto> pages;

    public PeopleView(ResourcesClients clients, AuthenticationContext authentication) {
        this.clients = clients;
        this.canWrite = authentication.hasRole(ResourcesRoles.RESOURCES_WRITE);
        this.canDelete = authentication.hasRole(ResourcesRoles.RESOURCES_DELETE);
        setSizeFull();

        search.setId("people-search");
        search.setPlaceholder("Codigo, nombre, usuario, numero de empleado");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        categories.setId("people-categories");
        categories.setItems(PersonCategory.selectable());
        categories.setItemLabelGenerator(PersonCategory::label);
        categories.setClearButtonVisible(true);
        state.setId("people-state");
        for (HasValue<?, ?> filter : List.<HasValue<?, ?>>of(search, categories, state)) {
            filter.addValueChangeListener(change -> refresh());
        }

        Button create = new Button("Nueva persona", VaadinIcon.PLUS.create(), click -> new PersonEditorDialog(null, clients, this::refresh).open());
        create.setId("person-create");
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setVisible(canWrite);

        FlexLayout filters = new FlexLayout(search, categories, state);
        filters.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        filters.setAlignItems(FlexComponent.Alignment.BASELINE);
        filters.getStyle().set("gap", "var(--lumo-space-s)");
        HorizontalLayout toolbar = new HorizontalLayout(count, create);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.expand(count);
        add(new H2("Personal"), filters, toolbar, buildGrid());
        expand(grid);
    }

    private Grid<PersonDto> buildGrid() {
        grid.setId("people-grid");
        grid.addColumn(PersonDto::code).setHeader("Codigo").setKey("code").setSortProperty("code").setSortable(true).setAutoWidth(true);
        grid.addColumn(PersonDto::fullName).setHeader("Nombre").setKey("name").setSortProperty("lastName").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(person -> ResourcesFormats.category(person.category())).setHeader("Categoria").setKey("category")
                .setSortProperty("category").setSortable(true).setAutoWidth(true);
        grid.addColumn(ResourcesFormats::employer).setHeader("Empresa").setKey("employer").setAutoWidth(true);
        grid.addColumn(PersonDto::username).setHeader("Usuario").setKey("username").setSortProperty("username").setSortable(true)
                .setAutoWidth(true);
        grid.addColumn(PersonDto::employeeNumber).setHeader("N. empleado").setKey("employeeNumber").setAutoWidth(true);
        grid.addColumn(PersonDto::phone).setHeader("Telefono").setKey("phone").setAutoWidth(true);
        grid.addColumn(person -> ResourcesFormats.state(person.active())).setHeader("Estado").setKey("active")
                .setSortProperty("active").setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(this::rowActions)).setHeader("").setKey(ACTIONS_COLUMN).setAutoWidth(true).setFlexGrow(0);
        // Como en mto-frontend: el doble clic abre el editor si se puede modificar; un clic no abre nada.
        if (canWrite) {
            grid.addItemDoubleClickListener(event -> new PersonEditorDialog(event.getItem(), clients, this::refresh).open());
        }
        grid.setPageSize(PAGE_SIZE);
        grid.setMultiSort(false);
        grid.setSizeFull();
        pages = LazyPages.of(grid, this::load, total -> count.setText(total + " personas"));
        return grid;
    }

    /** La columna existe siempre: el historial es lectura. */
    private Component rowActions(PersonDto person) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(false);
        actions.add(Screens.rowButton("person-history-" + person.id(), VaadinIcon.CLOCK, "Historial de " + person.code(),
                click -> ResourcesHistory.dialog(person.code(), (page, size) -> clients.people().revisions(person.id(), page, size),
                        ResourcesHistory::person).open()));
        if (canWrite) {
            actions.add(Screens.rowButton("person-edit-" + person.id(), VaadinIcon.EDIT, "Modificar " + person.code(),
                    click -> new PersonEditorDialog(person, clients, this::refresh).open()));
        }
        if (person.isActive() && canDelete) {
            Button deactivate = Screens.rowButton("person-deactivate-" + person.id(), VaadinIcon.BAN, "Desactivar " + person.code(),
                    click -> Screens.confirm("Desactivar " + person.label(),
                            "No se borra: deja de poder entrar en equipos y turnos nuevos. Si es lineman de un equipo, sale de el hoy, "
                                    + "salvo que lo dejara con menos de 2. Quien dirige una unidad necesita antes otro jefe. Se puede reactivar.",
                            "Desactivar", () -> deactivate(person)));
            deactivate.addThemeVariants(ButtonVariant.LUMO_ERROR);
            actions.add(deactivate);
        } else if (!person.isActive() && canWrite) {
            actions.add(Screens.rowButton("person-activate-" + person.id(), VaadinIcon.CHECK_CIRCLE, "Reactivar " + person.code(),
                    click -> activate(person)));
        }
        return actions;
    }

    private void deactivate(PersonDto person) {
        try {
            clients.people().deactivate(person.id());
            Screens.success("Desactivada " + person.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    private void activate(PersonDto person) {
        try {
            clients.people().update(person.id(), MergePatch.of(PersonUpdateRequest.active(true), person.version()));
            Screens.success("Reactivada " + person.label());
        } catch (BackofficeApiException failure) {
            UiErrors.show(failure);
        }
        refresh();
    }

    void refresh() {
        pages.refresh();
    }

    private PersonFilter filter() {
        return new PersonFilter(search.getValue(), categories.getValue(), null, state.getValue() == null ? null : state.getValue().value());
    }

    /** Una pagina con su total: una peticion ({@link LazyPages}). */
    private LazyPages.Page<PersonDto> load(int offset, int limit, List<QuerySortOrder> sort) {
        int size = Math.max(1, limit);
        List<String> order = MasterFilters.sort(sort);
        return LazyPages.Page.of(clients.people().search(filter(), offset / size, size, order.isEmpty() ? DEFAULT_SORT : order));
    }
}
